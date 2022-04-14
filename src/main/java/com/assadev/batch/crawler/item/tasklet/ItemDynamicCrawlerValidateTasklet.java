package com.assadev.batch.crawler.item.tasklet;

import com.assadev.batch.core.contant.IndexMode;
import com.assadev.batch.core.contant.IndexType;
import com.assadev.batch.core.exception.ValidationException;
import com.assadev.batch.core.utils.Done;
import com.assadev.batch.core.utils.Lock;
import com.assadev.batch.crawler.item.validation.CrawlerValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.io.File;

@Slf4j
@Component
@RequiredArgsConstructor
public class ItemDynamicCrawlerValidateTasklet implements Tasklet {

    /**
     *  - static/crawler.lock 확인
     *      - [전체수집] 프로세스 진행 중이면 현재 프로세스 종료
     *  - dynamic/indexer.lock 확인
     *      - [증분색인] 프로세스가 실행 중이면 현재 프로세스 종료
     *  - dynamic/crawler.done 확인
     *      - dynamic/crawler.done 파일이 있을 경우
     *          - [증분색인]이 실행되지 않는 것으로 판단하여 현재 프로세스 종료
     *      - dynamic/crawler.done 파일이 없을 경우
     *          - 이전에 실행된 프로세스가 동작하지 않으면 이전 프로세스 종료하고 수집한 폴더 삭제
     *  - dynamic/crawler.lock 생성
     *
     * @param contribution
     * @param chunkContext
     * @return
     * @throws Exception
     */
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        String basePath = (String)chunkContext.getStepContext().getJobExecutionContext().get("basePath");
        String targetPath = (String)chunkContext.getStepContext().getJobExecutionContext().get("targetPath");
        String targetIndexMode = (String)chunkContext.getStepContext().getJobExecutionContext().get("targetIndexMode");

        // static/crawler.lock 확인
        Lock staticCrawlerLock = new Lock(basePath + File.separator + IndexType.STATIC.getValue(),
                IndexMode.CRAWLER.getValue());
        if(CrawlerValidation.lockValidation(staticCrawlerLock) && CrawlerValidation.pidValidation(staticCrawlerLock.getFilePath())) {
            throw new ValidationException(ValidationException.ValidationErrCode.STATIC_CRAWLER_CHECK, "[Dynamic] [Crawler] static crawler batch is running.");
        }

        // dynamic/indexer.lock 확인
        // [증분색인] 프로세스가 실행 중이면 현재 프로세스 종료
        Lock indexerLock = new Lock(targetPath, IndexMode.INDEXER.getValue());
        if(CrawlerValidation.lockValidation(indexerLock) && CrawlerValidation.pidValidation(indexerLock.getFilePath())){
            throw new ValidationException(ValidationException.ValidationErrCode.INDEXER_LOCk_CHECK, "[Dynamic] [Crawler] dynamic index batch is running.");
        }

        // dynamic/crawler.done 확인
        // [증분색인]이 실행되지 않는 것으로 판단하여 현재 프로세스 종료
        Done crawlerDone = new Done(targetPath, targetIndexMode);
        if( crawlerDone.isDone() ){
            throw new ValidationException(ValidationException.ValidationErrCode.DONE_CHECK, "[Dynamic] [Crawler] contain crawler.done");
        }

        // dynamic/crawler.lock 확인
        Lock crawlerLock = new Lock(targetPath, targetIndexMode);
        //이전에 실행된 프로세스가 동작중인지 확인하여 동작중이면 현재 프로세스 종료
        if(CrawlerValidation.lockValidation(crawlerLock) && CrawlerValidation.pidValidation(crawlerLock.getFilePath())){
            throw new ValidationException(ValidationException.ValidationErrCode.LOCK_CHECK, "[Dynamic] [Crawler] dynamic crawler batch is running.");
        }

        // 이전에 생성된 dynamic/crawler.lock 있고 프로세스가 실행되지 않는다면 lock 해제
        if(crawlerLock.isLock()){
            crawlerLock.unLock();
        }
        // dynamic/crawler 하위 디렉토리 삭제
        CrawlerValidation.failDirectoryValidation(targetPath);

        // 현재 static/crawler.lock 생성
        crawlerLock.lock();

        log.info(" >>>>>>>>> 1. Item Dynamic Validation <<<<<<<<< ");

        return RepeatStatus.FINISHED;
    }
}

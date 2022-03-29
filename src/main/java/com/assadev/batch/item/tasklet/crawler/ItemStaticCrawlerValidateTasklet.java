package com.assadev.batch.item.tasklet.crawler;

import com.assadev.batch.core.contant.IndexMode;
import com.assadev.batch.core.contant.IndexType;
import com.assadev.batch.core.exception.ValidationException;
import com.assadev.batch.core.utils.Done;
import com.assadev.batch.core.utils.Lock;
import com.assadev.batch.item.validation.CrawlerValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

@Slf4j
@Component
@RequiredArgsConstructor
public class ItemStaticCrawlerValidateTasklet implements Tasklet {

    /**
     *  - dynamic/crawler.lock 확인
     *      - 증분수집 프로세스가 동작중이면 [증분수집] 프로세스 종료
     *  - static/index.lock 확인
     *      - [전체색인] 프로세스가 실행 중이면 현재 프로세스 종료
     *  - static/crawler.done 확인
     *      - static/crawler.done 파일이 있을 경우
     *          - [전체색인]이 실행되지 않는 것으로 판단하여 현재 프로세스 종료
     *      - static/crawler.done 파일이 없을 경우
     *          - 이전에 실행된 프로세스가 동작하지 않으면 이전 프로세스 종료하고 수집한 폴더 삭제
     *  - static/crawler.lock 생성
     *
     * @param contribution
     * @param chunkContext
     * @return RepeatStatus
     * @throws Exception
     */
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        String basePath = (String)chunkContext.getStepContext().getJobExecutionContext().get("basePath");
        String targetPath = (String)chunkContext.getStepContext().getJobExecutionContext().get("targetPath");
        String targetIndexMode = (String)chunkContext.getStepContext().getJobExecutionContext().get("targetIndexMode");

        // dynamic/crawler.lock 확인 ( 증분수집 프로세스 종료 )
        Lock dynamicCrawlerLock = new Lock(basePath + File.separator + IndexType.DYNAMIC.getValue(),
                IndexMode.CRAWLER.getValue());
        if(CrawlerValidation.lockValidation(dynamicCrawlerLock) && CrawlerValidation.pidValidation(dynamicCrawlerLock.getFilePath())) {
            CrawlerValidation.lockPidKill(dynamicCrawlerLock.getFilePath());
        }

        // static/indexer.lock 확인
        // [전체색인] 프로세스가 실행 중이면 현재 프로세스 종료
        Lock indexerLock = new Lock(targetPath, IndexMode.INDEXER.getValue());
        if(CrawlerValidation.lockValidation(indexerLock) && CrawlerValidation.pidValidation(indexerLock.getFilePath())){
            throw new ValidationException(ValidationException.ValidationErrCode.INDEXER_LOCk_CHECK, "[Static] [Crawler] static index batch is running.");
        }

        // static/crawler.done 확인
        // [전체색인]이 실행되지 않는 것으로 판단하여 현재 프로세스 종료
        Done crawlerDone = new Done(targetPath, targetIndexMode);
        if( crawlerDone.isDone() ){
            throw new ValidationException(ValidationException.ValidationErrCode.DONE_CHECK, "[Static] [Crawler] contain crawler.done");
        }

        // static/crawler.lock 확인
        Lock crawlerLock = new Lock(targetPath, targetIndexMode);
        //이전에 실행된 프로세스가 동작중인지 확인하여 동작중이면 현재 프로세스 종료
        if(CrawlerValidation.lockValidation(crawlerLock) && CrawlerValidation.pidValidation(crawlerLock.getFilePath())){
            throw new ValidationException(ValidationException.ValidationErrCode.LOCK_CHECK, "[Static] [Crawler] static crawler batch is running.");
        }

        // 이전에 생성된 static/crawler.lock 있고 프로세스가 실행되지 않는다면 lock 해제
        if(crawlerLock.isLock()){
            crawlerLock.unLock();
        }
        // static/crawler 하위 디렉토리 삭제
        CrawlerValidation.failDirectoryValidation(targetPath);

        // 현재 static/crawler.lock 생성
        crawlerLock.lock();

        log.info(" >>>>>>>>> 1. Item Static Validation <<<<<<<<< ");

        return RepeatStatus.FINISHED;
    }
}

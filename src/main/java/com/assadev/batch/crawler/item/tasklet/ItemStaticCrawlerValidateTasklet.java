package com.assadev.batch.crawler.item.tasklet;

import com.assadev.batch.core.contant.DataType;
import com.assadev.batch.core.contant.DefineConstant;
import com.assadev.batch.core.contant.IndexMode;
import com.assadev.batch.core.exception.ValidationException;
import com.assadev.batch.core.utils.CrawlerUtils;
import com.assadev.batch.core.utils.DirectoryUtils;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class ItemStaticCrawlerValidateTasklet implements Tasklet {

    /**
     *  - [전체수집] 실행여부 확인
     *      - static/crawler.lock 확인
     *      - [전체수집] 프로세스가 실행 중이면 현재 프로세스 종료
     *  - 로컬 수집폴더 이동
     *      - static/crawler.done 파일이 있을 경우
     *          - 이전 배치 실행시 완료처리 단계에서 [수집폴더 -> 백업폴더]로 이동 중 오류 발생된 경우
     *          - 수집폴더 -> 백업폴더 이동
     *      - static/crawler.done 파일이 없을 경우
     *          - 이전 배치 실행시 수집 단계에서 오류가 발생하여 [수집폴더 -> 에러폴더]로 이동 중 오류 발생된 경우
     *  - static/crawler.lock 생성
     *
     * @param contribution
     * @param chunkContext
     * @return RepeatStatus
     * @throws Exception
     */
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        log.info(" >>>>>>>>> 1. Item Static Validation <<<<<<<<< ");
        String basePath = contribution.getStepExecution().getJobExecution().getExecutionContext().getString("basePath");
        String indexMode = contribution.getStepExecution().getJobExecution().getExecutionContext().getString("indexMode");
        String dataType = contribution.getStepExecution().getJobExecution().getExecutionContext().getString("dataType");

        // static/crawler.lock 확인
        Lock crawlerLock = new Lock(basePath, indexMode);
        //이전에 실행된 프로세스가 동작중인지 확인하여 동작중이면 현재 프로세스 종료
        if(CrawlerValidation.lockValidation(crawlerLock) && CrawlerValidation.pidValidation(crawlerLock.getFilePath())){
            throw new ValidationException(ValidationException.ValidationErrCode.LOCK_CHECK, "[Static] [Crawler] static crawler batch is running.");
        }

        // 이전에 생성된 static/crawler.lock 있고 프로세스가 실행되지 않는다면 lock 해제
        if(crawlerLock.isLock()){
            crawlerLock.unLock();
        }

        // 로컬 수집폴더 이동
        DirectoryUtils.moveToBackup(indexMode, dataType, basePath);
        DirectoryUtils.moveToError(indexMode, dataType, basePath);
        DirectoryUtils.notExistDoneDeleteFolder(indexMode, basePath);

        // 현재 static/crawler.lock 생성
        crawlerLock.lock();

        return RepeatStatus.FINISHED;
    }
}

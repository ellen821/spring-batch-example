package com.assadev.batch.crawler.item.tasklet;

import com.assadev.batch.core.contant.DataType;
import com.assadev.batch.core.utils.CrawlerUtils;
import com.assadev.batch.core.utils.DirectoryUtils;
import com.assadev.batch.core.utils.Lock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ItemStaticCrawlerCompleteTasklet implements Tasklet {

    @Value("${crawler.directory-remain-size}")
    private int directoryRemainSize;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        log.info(" >>>>>>>>> 4. Item Static Unlock ");
        String basePath = contribution.getStepExecution().getJobExecution().getExecutionContext().getString("basePath");
        String indexMode = contribution.getStepExecution().getJobExecution().getExecutionContext().getString("indexMode");
        String dataType = contribution.getStepExecution().getJobExecution().getExecutionContext().getString("dataType");

        try {
            // 로컬 수집폴더 -> 백업폴더로 이동
            DirectoryUtils.moveToBackup(indexMode, dataType, basePath);

            // 로컬 백업/에러 삭제
            DirectoryUtils.directoryDelete(CrawlerUtils.replaceLast(basePath, dataType, DataType.BARCKUP.getValue()), directoryRemainSize);
            DirectoryUtils.directoryDelete(CrawlerUtils.replaceLast(basePath, dataType, DataType.ERROR.getValue()), directoryRemainSize);

            // lock 설정 해제
            Lock crawlerLock = new Lock(basePath,indexMode);
            crawlerLock.unLock();
        }catch (Exception e){
            log.warn("");
        }

        return RepeatStatus.FINISHED;
    }
}

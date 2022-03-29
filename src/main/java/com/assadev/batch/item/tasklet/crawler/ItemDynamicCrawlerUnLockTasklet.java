package com.assadev.batch.item.tasklet.crawler;

import com.assadev.batch.core.contant.IndexMode;
import com.assadev.batch.core.contant.IndexType;
import com.assadev.batch.core.utils.Lock;
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
public class ItemDynamicCrawlerUnLockTasklet implements Tasklet {

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        log.info(" >>>>>>>>> 4. Item Dynamic Unlock ");
        Lock crawlerLock = new Lock((String)chunkContext.getStepContext().getJobExecutionContext().get("targetPath"),
                (String)chunkContext.getStepContext().getJobExecutionContext().get("targetIndexMode"));
        crawlerLock.unLock();

        return RepeatStatus.FINISHED;
    }
}

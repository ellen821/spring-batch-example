package com.assadev.batch.item.job.crawler;

import com.assadev.batch.core.contant.IndexMode;
import com.assadev.batch.core.contant.IndexType;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class ItemStaticCrawlerJobListener extends JobExecutionListenerSupport {

    @Value("${crawler.item.base-path}")
    private String basePath;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        jobExecution.getExecutionContext().putString("basePath", basePath);
        jobExecution.getExecutionContext().putString("targetPath", basePath + File.separator + IndexType.STATIC.getValue());
        jobExecution.getExecutionContext().putString("targetIndexMode", IndexMode.CRAWLER.getValue());
    }
}

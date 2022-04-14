package com.assadev.batch.crawler.item.job;

import com.assadev.batch.core.contant.DataType;
import com.assadev.batch.core.contant.IndexMode;
import com.assadev.batch.core.contant.IndexType;
import com.assadev.batch.core.contant.ServiceName;
import com.assadev.batch.core.properties.CrawlerProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ItemStaticCrawlerJobListener extends JobExecutionListenerSupport {

    private final CrawlerProperties crawlerProperties;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        jobExecution.getExecutionContext().putString("serviceName", ServiceName.ITEM.getValue());
        jobExecution.getExecutionContext().putString("IndexType",IndexType.STATIC.getValue());
        jobExecution.getExecutionContext().putString("indexMode", IndexMode.CRAWLER.getValue());
        jobExecution.getExecutionContext().putString("dataType",DataType.CRAWLER.getValue());
        jobExecution.getExecutionContext().putString("basePath", crawlerProperties.getBasePath()
                .replace("{SERVICE_NAME}",ServiceName.ITEM.getValue())
                .replace("{INDEX_TYPE}", IndexType.STATIC.getValue())
                .replace("{DATA_TYPE}", DataType.CRAWLER.getValue()));
    }
}

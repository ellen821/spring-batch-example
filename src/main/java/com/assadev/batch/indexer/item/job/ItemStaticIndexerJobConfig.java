package com.assadev.batch.indexer.item.job;

import com.assadev.batch.core.properties.CrawlerProperties;
import com.assadev.batch.crawler.item.model.CrawlerItem;
import com.assadev.batch.indexer.item.chunk.ItemIndexerPartitioner;
import com.assadev.batch.indexer.mapper.JsonLineMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ItemStaticIndexerJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job itemStaticIndexerJob() {
        return this.jobBuilderFactory.get("itemStaticIndexerJob")
                .incrementer(new RunIdIncrementer())
                .start(itemStaticIndexerStep())
                .build();
    }

    @Bean
    public TaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(2); //기본 쓰레드 사이즈
        taskExecutor.setMaxPoolSize(2); //최대 쓰레드 사이즈
        taskExecutor.setQueueCapacity(15); //Max쓰레드가 동작하는 경우 대기하는 queue 사이즈
        taskExecutor.setThreadNamePrefix("Executor-");
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
//        executor.setWaitForTasksToCompleteOnShutdown(Boolean.TRUE);
        taskExecutor.initialize();
        return taskExecutor;
    }

    @Bean
    public Step itemStaticIndexerStep() {
        return stepBuilderFactory.get("itemStaticIndexerStep")
                .partitioner(itemStaticIndexerSlaveStep().getName(), itemStaticIndexerPartitioner())
                .step(itemStaticIndexerSlaveStep())
                .gridSize(2)
                .taskExecutor(threadPoolTaskExecutor())
                .build();
    }

    @Bean
    public Partitioner itemStaticIndexerPartitioner() {
        String indexerBasePath = "/System/Volumes/Data/data/item/static/indexer/20220420-221300";
        Collection<File> fileList = FileUtils.listFiles(new File(indexerBasePath), new String[]{"json"}, false);

        List<String> filePathList = new ArrayList<>();
        if(fileList.size() > 0){
            filePathList = fileList.stream().map(f->f.getPath()).collect(Collectors.toList());
        }

        ItemIndexerPartitioner itemIndexerPartitioner = new ItemIndexerPartitioner();
        itemIndexerPartitioner.setJsonFilePathList(filePathList);

        return itemIndexerPartitioner;
    }

    @Bean
    public Step itemStaticIndexerSlaveStep() {
        return stepBuilderFactory.get("itemStaticIndexerSlaveStep")
                .<CrawlerItem, CrawlerItem>chunk(1000)
                .reader(itemStaticIndexerItemReader(null))
                .processor(new ItemProcessor<CrawlerItem, CrawlerItem>() {
                    @Override
                    public CrawlerItem process(CrawlerItem item) throws Exception {
                        return item;
                    }
                })
                .writer(new ItemWriter<CrawlerItem>() {
                    @Override
                    public void write(List<? extends CrawlerItem> items) throws Exception {
//                        log.info(items.toString());
                    }
                })
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<CrawlerItem> itemStaticIndexerItemReader(@Value("#{stepExecutionContext[indexerFilePath]}") String indexerFilePath) {

        log.info("Reader = " + indexerFilePath);

        FlatFileItemReader<CrawlerItem> flatFileItemReader = new FlatFileItemReader<>();
        flatFileItemReader.setResource(new FileSystemResource(indexerFilePath));
        flatFileItemReader.setLineMapper(new JsonLineMapper());
        return flatFileItemReader;
    }
}

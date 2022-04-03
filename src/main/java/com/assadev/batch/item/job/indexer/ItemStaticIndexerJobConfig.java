package com.assadev.batch.item.job.indexer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
                .start(itemStaticIndexerValidateStep())
                .next(itemStaticIndexerStep())
                .next(itemStaticIndexerUnLockStep())
                .build();
    }

    @Bean
    public Step itemStaticIndexerValidateStep() {
        return stepBuilderFactory.get("itemStaticIndexerValidateStep")
                .tasklet((contribution, chunkContext) -> RepeatStatus.FINISHED)
                .build();
    }

    @Bean
    public Step itemStaticIndexerStep() {
        return stepBuilderFactory.get("itemStaticIndexerStep")
                .tasklet((contribution, chunkContext) -> RepeatStatus.FINISHED)
                .build();
    }

    @Bean
    public Step itemStaticIndexerUnLockStep() {
        return stepBuilderFactory.get("itemStaticIndexerUnLockStep")
                .tasklet((contribution, chunkContext) -> RepeatStatus.FINISHED)
                .build();
    }
}

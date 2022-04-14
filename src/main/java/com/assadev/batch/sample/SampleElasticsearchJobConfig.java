package com.assadev.batch.sample;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.MgetResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

//@Configuration
@RequiredArgsConstructor
public class SampleElasticsearchJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final ElasticsearchClient client;

    private static class SomeApplicationData {}

    @Bean
    public Job sampleElasticsearchJob() {
        return jobBuilderFactory.get("sampleElasticsearchJob")
                .incrementer(new RunIdIncrementer())
                .start(sampleElasticsearchStep())
                .build();
    }

    @Bean
    public Step sampleElasticsearchStep() {
        return stepBuilderFactory.get("sampleElasticsearchStep")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                        System.out.println(" >>>>> Sample Elasticsearch Step!!!");

                        MgetResponse<TestIndexData> response = client.mget(r->r
                                        .index("test-index")
                                        .ids(Arrays.asList("1"))
                                , TestIndexData.class);

                        TestIndexData testIndexData = response.docs().get(0).result().source();

                        System.out.println("docId="+testIndexData.getDocId()+", memo="+testIndexData.getMemo());

                        return RepeatStatus.FINISHED;
                    }
                })
                .build();
    }

}

 package com.assadev.batch.crawler.item.job;

 import com.assadev.batch.core.properties.CrawlerProperties;
 import com.assadev.batch.crawler.item.chunk.*;
 import com.assadev.batch.crawler.item.constant.ItemStaticCrawlerView;
 import com.assadev.batch.crawler.item.model.CrawlerItem;
 import com.assadev.batch.crawler.item.tasklet.ItemStaticCrawlerCompleteTasklet;
 import com.assadev.batch.crawler.item.tasklet.ItemStaticCrawlerValidateTasklet;
 import lombok.RequiredArgsConstructor;
 import lombok.extern.slf4j.Slf4j;
 import org.apache.ibatis.session.SqlSessionFactory;
 import org.mybatis.spring.batch.MyBatisCursorItemReader;
 import org.springframework.batch.core.Job;
 import org.springframework.batch.core.Step;
 import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
 import org.springframework.batch.core.configuration.annotation.JobScope;
 import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
 import org.springframework.batch.core.configuration.annotation.StepScope;
 import org.springframework.batch.core.launch.support.RunIdIncrementer;
 import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
 import org.springframework.batch.item.json.JsonFileItemWriter;
 import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
 import org.springframework.beans.factory.annotation.Value;
 import org.springframework.context.annotation.Bean;
 import org.springframework.context.annotation.Configuration;
 import org.springframework.core.io.FileSystemResource;
 import org.springframework.core.task.SimpleAsyncTaskExecutor;

 import java.io.File;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;

 @Slf4j
@Configuration
@RequiredArgsConstructor
public class ItemStaticCrawlerJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private final CrawlerProperties crawlerProperties;

    private final ItemStaticCrawlerJobListener itemStaticCrawlerJobListener;
    private final ItemStaticCrawlerValidateTasklet itemStaticCrawlerValidateTasklet;
    private final ItemCrawlerPartitioner itemCrawlerPartitioner;
    private final ItemStaticCrawlerListener itemStaticCrawlerListener;
    private final ItemStaticCrawlerItemProcessor itemStaticCrawlerItemProcessor;
    private final ItemStaticCrawlerItemWriter itemStaticCrawlerItemWriter;
    private final ItemStaticCrawlerCompleteTasklet itemStaticCrawlerCompleteTasklet;

    @Bean
    public Job itemStaticCrawlerJob() {
        return this.jobBuilderFactory.get("itemStaticCrawlerJob")
                .incrementer(new RunIdIncrementer())
                .listener(itemStaticCrawlerJobListener)
                .start(itemStaticCrawlerValidateStep())
                .next(itemStaticCrawlerStep())
                .next(itemStaticCrawlerCompleteStep())
                .build();
    }

    @Bean
    @JobScope
    public Step itemStaticCrawlerValidateStep() {
        return stepBuilderFactory.get("itemStaticCrawlerValidateStep")
                .tasklet(itemStaticCrawlerValidateTasklet)
                .build();
    }

    @Bean
    public Step itemStaticCrawlerStep() {
        itemCrawlerPartitioner.setMapperCount(2);
        return stepBuilderFactory.get("itemStaticCrawlerStep")
                .listener(itemStaticCrawlerListener)
                .partitioner(itemStaticCrawlerSlaveStep().getName(), itemCrawlerPartitioner)
                .step(itemStaticCrawlerSlaveStep())
                .gridSize(2)
                .taskExecutor(new SimpleAsyncTaskExecutor())
                .build();
    }

     @Bean
     public Step itemStaticCrawlerSlaveStep() {
         return stepBuilderFactory.get("itemStaticCrawlerSlaveStep")
                 .<CrawlerItem, CrawlerItem>chunk(4)
                 .reader(itemStaticCrawlerItemReader(null, null))
                 .processor(itemStaticCrawlerItemProcessor)
                 .writer(itemStaticCrawlerItemWriter)
                 .build();
     }

     @Bean
     @StepScope
     public MyBatisCursorItemReader<CrawlerItem> itemStaticCrawlerItemReader(SqlSessionFactory sqlSessionFactory,
                                                                             @Value("#{stepExecutionContext['cateId']}") String cateId){

         Map<String, Object> parameterValues = new HashMap<>();
         parameterValues.put("cateId", cateId);

         MyBatisCursorItemReader<CrawlerItem> myBatisCursorItemReader = new MyBatisCursorItemReader<>();
         myBatisCursorItemReader.setSqlSessionFactory(sqlSessionFactory);
         myBatisCursorItemReader.setParameterValues(parameterValues);
         myBatisCursorItemReader.setQueryId("com.assadev.batch.crawler.item.mapper.ItemMapper.getItemList");

         return myBatisCursorItemReader;

     }

//     @Bean
//     @StepScope
//     public JsonFileItemWriter<CrawlerItem> writer(@Value("#{jobExecutionContext['crawlerPath']}") String crawlerPath,
//                                                   @Value("#{stepExecutionContext['cateId']}") String cateId){
//         String resource = crawlerPath + File.separator + cateId + "_item" + fileCount + ".json";
//         if( crawlerProperties.getFileRowMaxSize() < rowCount + 2) {
//             rowCount = 0;
//             fileCount = fileCount + 1;
//             resource = crawlerPath + File.separator + cateId + "_item" + fileCount + ".json";
//         }
//
//         log.info(" ㄴㄴㄴㄴ Crawler Writer = " + resource);
//
//        return new JsonFileItemWriterBuilder<CrawlerItem>()
//                .name("itemWriterJson")
//                .jsonObjectMarshaller(new JacksonJsonObjectMarshaller<>())
//                .resource(new FileSystemResource(resource))
//                .append(true)
//                .build();
//     }

//     @Bean
//     @StepScope
//     public PaginationReader itemStaticCrawlerItemReader(
//             @Value("#{stepExecutionContext['viewNumber']}") Integer viewNumber) {
//
//         // V_SEARCH_ITEM_001 ~ 016, V_ITEM_DATA_SEARCH_MO
//         String viewName = ItemStaticCrawlerView.findView(viewNumber).getViewName();
//
//         //TODO : 수집쿼리 실행
//         // case1: spring-batch에서 제공하는 Database Reader 사용이 가능하다면 cursor 또는 paging 을 선택하여 개발 진행
//         // case2: 만약 별도로 구현해야 한다면 AbstractPagingItemReader를 상속받아서 별도로 구현해야 할 것으로 보임
//         // https://n1tjrgns.tistory.com/159
//         // https://techblog.woowahan.com/2662/
//
//         int pageSize = 6;
//         int totalCount = 12;
//         int blockSize = (int)Math.ceil(totalCount / pageSize);
//
//         List<CrawlerItem> itemList = new ArrayList<>();
//         for(int i=1; i<=totalCount; i++){
//             CrawlerItem crawlerItem = new CrawlerItem();
//             crawlerItem.setDocId(viewName+"_"+i);
//             itemList.add(crawlerItem);
//         }
//
//         log.info(" ㄴㄴㄴㄴ Crawler Reader = "+viewNumber);
//
//         return new PaginationReader(pageSize, blockSize, itemList);
//     }

    @Bean
    public Step itemStaticCrawlerCompleteStep() {
        return stepBuilderFactory.get("itemStaticCrawlerCompleteStep")
                .tasklet(itemStaticCrawlerCompleteTasklet)
                .build();
    }

}

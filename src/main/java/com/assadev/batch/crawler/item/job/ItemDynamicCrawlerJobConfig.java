 package com.assadev.batch.crawler.item.job;

 import com.assadev.batch.crawler.item.chunk.ItemCrawlerPartitioner;
 import com.assadev.batch.crawler.item.constant.ItemDynamicCrawlerView;
 import com.assadev.batch.crawler.item.tasklet.ItemDynamicCrawlerValidateTasklet;
 import com.assadev.batch.crawler.item.chunk.ItemDynamicCrawlerItemProcessor;
 import com.assadev.batch.crawler.item.chunk.ItemDynamicCrawlerItemWriter;
 import com.assadev.batch.crawler.item.chunk.PaginationReader;
 import com.assadev.batch.crawler.item.model.CrawlerItem;
 import com.assadev.batch.crawler.item.chunk.ItemDynamicCrawlerListener;
 import com.assadev.batch.crawler.item.tasklet.ItemDynamicCrawlerUnLockTasklet;
 import lombok.RequiredArgsConstructor;
 import lombok.extern.slf4j.Slf4j;
 import org.springframework.batch.core.Job;
 import org.springframework.batch.core.Step;
 import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
 import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
 import org.springframework.batch.core.configuration.annotation.StepScope;
 import org.springframework.batch.core.launch.support.RunIdIncrementer;
 import org.springframework.beans.factory.annotation.Value;
 import org.springframework.context.annotation.Bean;
 import org.springframework.context.annotation.Configuration;
 import org.springframework.core.task.SimpleAsyncTaskExecutor;

 import java.util.ArrayList;
 import java.util.List;

 @Slf4j
// @Configuration
 @RequiredArgsConstructor
 public class ItemDynamicCrawlerJobConfig {
 
     @Value("${crawler.item.dynamic-thread-count}")
     private int threadCount;
 
     private final JobBuilderFactory jobBuilderFactory;
     private final StepBuilderFactory stepBuilderFactory;

     private final ItemDynamicCrawlerJobListener itemDynamicCrawlerJobListener;
     private final ItemDynamicCrawlerValidateTasklet itemDynamicCrawlerValidateTasklet;
     private final ItemDynamicCrawlerListener itemDynamicCrawlerListener;
     private final ItemDynamicCrawlerItemProcessor itemDynamicCrawlerItemProcessor;
     private final ItemDynamicCrawlerItemWriter itemDynamicCrawlerItemWriter;
     private final ItemDynamicCrawlerUnLockTasklet itemDynamicCrawlerUnLockTasklet;
 
     @Bean
     public Job itemDynamicCrawlerJob() {
         return this.jobBuilderFactory.get("itemDynamicCrawlerJob")
                 .incrementer(new RunIdIncrementer())
                 .listener(itemDynamicCrawlerJobListener)
                 .start(itemDynamicCrawlerValidateStep())
                 .next(itemDynamicCrawlerStep())
                 .next(itemDynamicCrawlerUnLockStep())
                 .build();
     }
 
     @Bean
     public Step itemDynamicCrawlerValidateStep() {
         return stepBuilderFactory.get("itemDynamicCrawlerValidateStep")
                 .tasklet(itemDynamicCrawlerValidateTasklet)
                 .build();
     }
 
     @Bean
     public Step itemDynamicCrawlerStep() {
         return stepBuilderFactory.get("itemDynamicCrawlerStep")
                 .listener(itemDynamicCrawlerListener)
                 .partitioner(itemDynamicCrawlerSlaveStep().getName(), itemCrawlerPartitioner())
                 .step(itemDynamicCrawlerSlaveStep())
                 .gridSize(threadCount)
                 .taskExecutor(new SimpleAsyncTaskExecutor())
                 .build();
     }

     @Bean
     public ItemCrawlerPartitioner itemCrawlerPartitioner() {
         ItemCrawlerPartitioner itemCrawlerPartitioner = new ItemCrawlerPartitioner();
         itemCrawlerPartitioner.setMapperCount(ItemDynamicCrawlerView.count());

         return itemCrawlerPartitioner;
     }
 
      @Bean
      public Step itemDynamicCrawlerSlaveStep() {
          return stepBuilderFactory.get("itemDynamicCrawlerSlaveStep")
                  .<CrawlerItem, CrawlerItem>chunk(12)
                  .reader(itemDynamicCrawlerItemReader(null))
                  .processor(itemDynamicCrawlerItemProcessor)
                  .writer(itemDynamicCrawlerItemWriter)
                  .build();
      }
 
      @Bean
      @StepScope
      public PaginationReader itemDynamicCrawlerItemReader(
              @Value("#{stepExecutionContext['viewNumber']}") Integer viewNumber) {
 
          // V_SEARCH_ITEM_001 ~ 016, V_ITEM_DATA_SEARCH_MO
          String viewName = ItemDynamicCrawlerView.findView(viewNumber).getViewName();
 
          //TODO : 수집쿼리 실행
          // case1: spring-batch에서 제공하는 Database Reader 사용이 가능하다면 cursor 또는 paging 을 선택하여 개발 진행
          // case2: 만약 별도로 구현해야 한다면 AbstractPagingItemReader를 상속받아서 별도로 구현해야 할 것으로 보임
          // https://n1tjrgns.tistory.com/159
          // https://techblog.woowahan.com/2662/
 
          int pageSize = 12;
          int totalCount = 12;
          int blockSize = (int)Math.ceil(totalCount / pageSize);
 
          List<CrawlerItem> itemList = new ArrayList<>();
          for(int i=1; i<=totalCount; i++){
              CrawlerItem crawlerItem = new CrawlerItem();
              crawlerItem.setDocId(viewNumber+"_"+i);
              itemList.add(crawlerItem);
          }
 
          log.info(" ㄴㄴㄴㄴ Crawler Reader = "+viewName);
 
          return new PaginationReader(pageSize, blockSize, itemList);
      }
 
     @Bean
     public Step itemDynamicCrawlerUnLockStep() {
         return stepBuilderFactory.get("itemDynamicCrawlerUnLockStep")
                 .tasklet(itemDynamicCrawlerUnLockTasklet)
                 .build();
     }
 
 }

package com.assadev.batch.crawler.item.chunk;

import com.assadev.batch.crawler.item.model.CrawlerItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.annotation.BeforeChunk;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ItemStaticCrawlerItemWriter implements ItemWriter<CrawlerItem> {
    private String crawlerPath;
    private int commitNo;

    @BeforeChunk
    public void before(ChunkContext context){
        crawlerPath = (String) context.getStepContext().getJobExecutionContext().get("crawlerPath");
        commitNo = context.getStepContext().getStepExecution().getCommitCount() + 1;
//        log.info("targetPath = "+context.getStepContext().getStepExecution().getExecutionContext().getString("viewNumber"));
    }

    @Override
    public void write(List<? extends CrawlerItem> items) throws Exception {
        // TODO : 수집 파일 생성
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(new File(crawlerPath, "itemCrawler" + commitNo + ".json"), items);

        log.info(" ㄴㄴㄴㄴ Crawler Writer "+commitNo+" = " + items.stream().map(i->i.getDqId()).collect(Collectors.toList()));
    }
}

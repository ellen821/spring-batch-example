package com.assadev.batch.crawler.item.chunk;

import com.assadev.batch.crawler.item.model.CrawlerItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.annotation.AfterChunk;
import org.springframework.batch.core.annotation.BeforeChunk;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@StepScope
@Component
public class ItemStaticCrawlerItemWriter implements ItemWriter<CrawlerItem> {
    @Value("#{jobExecutionContext['crawlerPath']}")
    private String crawlerPath;
    @Value("#{stepExecutionContext['cateId']}")
    private String cateId;
    @Value("${crawler.file-row-max-size}")
    private int fileRowMaxSize;

    private int fileCount = 1;
    private List<CrawlerItem> writeJsonList = new ArrayList<>();

    @Override
    public void write(List<? extends CrawlerItem> items) throws Exception {

        // TODO : 수집 파일 생성
        ObjectMapper objectMapper = new ObjectMapper();
        if( fileRowMaxSize <= writeJsonList.size() + items.size() ){
            long remainCount = writeJsonList.size() + items.size() - fileRowMaxSize;
            writeJsonList.addAll( remainCount == 0 ? items :
                    items.stream().limit(remainCount).collect(Collectors.toList()));

            objectMapper.writeValue(new File(crawlerPath, cateId + "_item" + fileCount + ".json"), writeJsonList);
            log.info(" ㄴㄴㄴㄴ Crawler Writer = " + cateId + "_item" + fileCount + ".json");

            writeJsonList = new ArrayList<>();
            if(remainCount > 0) {
                writeJsonList.addAll(items.stream().skip(remainCount).collect(Collectors.toList()));
            }
            fileCount = fileCount + 1;
        }else{
            writeJsonList.addAll(items);
        }

    }
}

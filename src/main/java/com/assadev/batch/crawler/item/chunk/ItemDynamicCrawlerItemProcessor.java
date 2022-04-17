package com.assadev.batch.crawler.item.chunk;

import com.assadev.batch.crawler.item.model.CrawlerItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ItemDynamicCrawlerItemProcessor implements ItemProcessor<CrawlerItem, CrawlerItem> {
    @Override
    public CrawlerItem process(CrawlerItem item) throws Exception {
        //TODO: row 단위로 처리 가능
        log.info(" ㄴㄴㄴㄴ Crawler Processor = " + item.getDqId());
        return item;
    }
}

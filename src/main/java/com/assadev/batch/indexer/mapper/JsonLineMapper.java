package com.assadev.batch.indexer.mapper;

import com.assadev.batch.crawler.item.model.CrawlerItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.batch.item.file.LineMapper;

public class JsonLineMapper implements LineMapper<CrawlerItem> {

    private ObjectMapper mapper = new ObjectMapper();


    @Override
    public CrawlerItem mapLine(String line, int lineNumber) throws Exception {
        return mapper.readValue(line, CrawlerItem.class);
    }
}

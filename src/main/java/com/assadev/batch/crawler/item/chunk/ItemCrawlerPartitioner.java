package com.assadev.batch.crawler.item.chunk;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ItemCrawlerPartitioner implements Partitioner {

    private int mapperCount;

    public void setMapperCount(int mapperCount) {
        this.mapperCount = mapperCount;
    }

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        int targetSize = 1;
        int start = 1;
        int end = start + targetSize - 1;

        Map<String, ExecutionContext> result = new HashMap<>();
        while (start <= mapperCount) {

            ExecutionContext value = new ExecutionContext();
            result.put("partition" + start, value);

            if (end >= mapperCount) {
                end = mapperCount;
            }

            value.putInt("viewNumber", end);

            start += targetSize;
            end += targetSize;
        }

        return result;
    }
}

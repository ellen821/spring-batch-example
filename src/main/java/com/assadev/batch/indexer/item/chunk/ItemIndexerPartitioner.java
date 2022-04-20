package com.assadev.batch.indexer.item.chunk;

import com.assadev.batch.crawler.item.constant.ItemStaticCrawlerView;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemIndexerPartitioner implements Partitioner {

    private List<String> jsonFilePathList;

    public void setJsonFilePathList(List<String> jsonFilePathList) {
        this.jsonFilePathList = jsonFilePathList;
    }

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        int targetSize = 1;
        int start = 1;
        int end = start + targetSize - 1;

        Map<String, ExecutionContext> result = new HashMap<>();
        while (start <= jsonFilePathList.size()) {

            ExecutionContext value = new ExecutionContext();
            result.put("partition" + start, value);

            if (end >= jsonFilePathList.size()) {
                end = jsonFilePathList.size();
            }

            value.putString("indexerFilePath", jsonFilePathList.get(end-1));

            start += targetSize;
            end += targetSize;
        }

        return result;
    }
}

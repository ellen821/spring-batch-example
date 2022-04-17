package com.assadev.batch.crawler.item.chunk;

import com.assadev.batch.crawler.item.model.CrawlerItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.database.AbstractPagingItemReader;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class PaginationReader extends AbstractPagingItemReader<CrawlerItem> {

    private final int blockSize;
    private final List<CrawlerItem> itemList;

    public PaginationReader(int pageSize, int blockSize, List<CrawlerItem> itemList) {
        this.blockSize = blockSize;
        this.itemList = itemList;
        setPageSize(pageSize);

    }

    @Override
    protected void doReadPage() {

        if (results == null) {
            results = new ArrayList<>();
        } else {
            results.clear();
        }

        //blockCount
        if (getPage() >= blockSize) {
            return;
        }

        try {

            List<CrawlerItem> result = itemList.stream().skip(getPage()*getPageSize()).limit(getPageSize())
                    .collect(Collectors.toList());

            List<String> dd = result.stream().map(i->i.getDqId()).collect(Collectors.toList());

            log.info("page: " + getPage() + ", pageSize: " + getPageSize() + ", result: " + dd);

            results.addAll(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doJumpToPage(int itemIndex) {

    }
}

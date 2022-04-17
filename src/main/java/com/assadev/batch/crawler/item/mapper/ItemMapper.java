package com.assadev.batch.crawler.item.mapper;

import com.assadev.batch.core.db.annotation.PrimaryConnection;
import com.assadev.batch.crawler.item.model.CrawlerItem;

import java.util.List;
import java.util.Map;

@PrimaryConnection
public interface ItemMapper {
    List<CrawlerItem> getItemList(Map<String,Object> param);
}

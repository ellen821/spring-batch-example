package com.assadev.batch.crawler.item.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class CrawlerItem {
    private String dqId;
    private String itemNo;
    private String itemNm;
}

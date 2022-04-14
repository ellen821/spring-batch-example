package com.assadev.batch.crawler.item.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum ItemDynamicCrawlerView {
    NONE(""),
    V_SEARCH_ITEM_1("SEARCHUSR.V_ITEM_SEARCH_INC"),
    V_SEARCH_ITEM_2("SEARCHUSR.V_ITEM_DATA_SEARCH_MO_INC");

    private final String viewName;

    public static ItemDynamicCrawlerView findView(int number) {

        return Arrays.stream(ItemDynamicCrawlerView.values())
                .filter(c -> c.name().equalsIgnoreCase("V_SEARCH_ITEM_"+number))
                .findAny()
                .orElse(ItemDynamicCrawlerView.NONE);
    }

    public static int count(){
        return ItemDynamicCrawlerView.values().length - 1;
    }

}

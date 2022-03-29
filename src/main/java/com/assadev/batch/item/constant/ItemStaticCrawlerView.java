package com.assadev.batch.item.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum ItemStaticCrawlerView {
    NONE(""),
    V_SEARCH_ITEM_1("SEARCHUSR.V_SEARCH_ITEM_001"),
    V_SEARCH_ITEM_2("SEARCHUSR.V_SEARCH_ITEM_002"),
    V_SEARCH_ITEM_3("SEARCHUSR.V_SEARCH_ITEM_003"),
    V_SEARCH_ITEM_4("SEARCHUSR.V_SEARCH_ITEM_004"),
    V_SEARCH_ITEM_5("SEARCHUSR.V_SEARCH_ITEM_005"),
    V_SEARCH_ITEM_6("SEARCHUSR.V_SEARCH_ITEM_006"),
    V_SEARCH_ITEM_7("SEARCHUSR.V_SEARCH_ITEM_007"),
    V_SEARCH_ITEM_8("SEARCHUSR.V_SEARCH_ITEM_008"),
    V_SEARCH_ITEM_9("SEARCHUSR.V_SEARCH_ITEM_009"),
    V_SEARCH_ITEM_10("SEARCHUSR.V_SEARCH_ITEM_010"),
    V_SEARCH_ITEM_11("SEARCHUSR.V_SEARCH_ITEM_011"),
    V_SEARCH_ITEM_12("SEARCHUSR.V_SEARCH_ITEM_012"),
    V_SEARCH_ITEM_13("SEARCHUSR.V_SEARCH_ITEM_013"),
    V_SEARCH_ITEM_14("SEARCHUSR.V_SEARCH_ITEM_014"),
    V_SEARCH_ITEM_15("SEARCHUSR.V_SEARCH_ITEM_015"),
    V_SEARCH_ITEM_16("SEARCHUSR.V_SEARCH_ITEM_016"),
    V_SEARCH_ITEM_17("SEARCHUSR.V_ITEM_DATA_SEARCH_MO");

    private final String viewName;

    public static ItemStaticCrawlerView findView(int number) {

        return Arrays.stream(ItemStaticCrawlerView.values())
                .filter(c -> c.name().equalsIgnoreCase("V_SEARCH_ITEM_"+number))
                .findAny()
                .orElse(ItemStaticCrawlerView.NONE);
    }

    public static int count(){
        return ItemStaticCrawlerView.values().length - 1;
    }
}

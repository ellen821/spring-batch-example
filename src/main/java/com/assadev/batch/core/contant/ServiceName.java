package com.assadev.batch.core.contant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ServiceName {
    ITEM("item"),
    PLAN("pgm"),
    VMS_ITEM("vms-item"),
    SEARCH_BANNER("search-banner"),
    ETV_ITEM("etv-item"),
    CELEBSHOP_RECOMMEND_ITEM("celebshop-recommend-item");

    private final String value;
}

package com.assadev.batch.core.contant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DataType {
    CRAWLER("crawler"),
    INDEXER("indexer"),
    BARCKUP("backup"),
    ERROR("error");

    private final String value;
}

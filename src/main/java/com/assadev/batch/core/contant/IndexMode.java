package com.assadev.batch.core.contant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum IndexMode {
    CRAWLER("crawler"),
    INDEXER("indexer");

    private final String value;
}

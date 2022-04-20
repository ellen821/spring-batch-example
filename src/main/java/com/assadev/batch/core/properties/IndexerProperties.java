package com.assadev.batch.core.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "indexer", ignoreInvalidFields = true)
public class IndexerProperties {
    private String basePath;
}

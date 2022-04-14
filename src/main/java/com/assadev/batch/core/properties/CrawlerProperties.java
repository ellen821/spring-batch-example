package com.assadev.batch.core.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "crawler", ignoreInvalidFields = true)
public class CrawlerProperties {
    private String basePath;
    private String directoryNamePattern;
    private int directoryRemainSize;
    private int fileRowMaxSize;
    private String fileName;
}

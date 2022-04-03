package com.assadev.batch.core.config;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ElasticsearchProperties {
    private String host;
    private int port;
    private String httpProtocol;
    private int timeout;
    private String userName;
    private String password;
}

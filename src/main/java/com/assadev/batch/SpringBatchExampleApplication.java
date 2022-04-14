package com.assadev.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javax.sql.DataSource;

@Slf4j
@EnableBatchProcessing
@SpringBootApplication
//public class SpringBatchExampleApplication extends DefaultBatchConfigurer {
public class SpringBatchExampleApplication {

    public static void main(String[] args) {
        System.exit(SpringApplication.exit(SpringApplication.run(SpringBatchExampleApplication.class, args)));
    }

    //운영에서는 이렇게 하면 안된다 ~ 샘플이니까 로컬에서는 테스트로만 ...
//    @Override
//    public void setDataSource(DataSource dataSource) {
//    }

}

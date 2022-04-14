package com.assadev.batch.core.db.construct;

import com.assadev.batch.core.db.annotation.PrimaryConnection;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

public class MybatisConfig {
    public static final String BASE_PACKAGE = "com.assadev";
}

@Configuration
@EnableTransactionManagement
@MapperScan(basePackages = MybatisConfig.BASE_PACKAGE, annotationClass = PrimaryConnection.class, sqlSessionFactoryRef = "primarySqlSessionFactory")
class PrimaryByBatisConfig {
    @Bean(name = "primarySqlSessionFactory")
    public SqlSessionFactory primarySqlSessionFactory(@Qualifier("primaryDataSource") DataSource primaryDataSource) throws Exception {
        return SqlSessionFactoryBuilder.build(primaryDataSource);
    }

    @Bean(name = "primaryDataSource")
    @ConfigurationProperties("spring.datasource.primary")
    public DataSource dataSource(){
        return DataSourceBuilder.create().build();
    }

    @Bean
    public PlatformTransactionManager transactionManager(@Qualifier("primaryDataSource") DataSource primaryDataSource) {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(primaryDataSource);
        transactionManager.setGlobalRollbackOnParticipationFailure(false);
        return transactionManager;
    }
}

//@Configuration
//@MapperScan(basePackages = MybatisConfig.BASE_PACKAGE, annotationClass = BatchMetaConnection.class, sqlSessionFactoryRef = "batchMetaSqlSessionFactory")
//class BatchMetaMyBatisConfig{
//    @Primary
//    @Bean(name = "batchMetaSqlSessionFactory")
//    public SqlSessionFactory batchMetaSqlSessionFactory(@Qualifier("batchMetaDataSource") DataSource batchMetaDataSource) throws Exception {
//        return SqlSessionFactoryBuilder.build(batchMetaDataSource);
//    }
//
//    @Primary
//    @Bean(name = "batchMetaDataSource")
//    @ConfigurationProperties("spring.datasource.batch-meta")
//    public DataSource dataSource(){
//        return DataSourceBuilder.create().build();
//    }
//
//    @Primary
//    @Bean
//    public PlatformTransactionManager batchMetaTransactionManager(@Qualifier("batchMetaDataSource") DataSource batchMetaDataSource) {
//        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(batchMetaDataSource);
//        transactionManager.setGlobalRollbackOnParticipationFailure(false);
//        return transactionManager;
//    }
//}
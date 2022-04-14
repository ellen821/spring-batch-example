package com.assadev.batch.core.db.construct;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.io.IOException;

public class SqlSessionFactoryBuilder {
    private static final String CONFIG_LOCATION_PATH = "classpath:/mybatis-config.xml";
    private static final String MAPPER_LOCATION_PATH = "classpath:com/assadev/**/*.xml";
    private static final String TYPE_ALIASES_PACKAGE = "com.assadev";
    private static final String TYPE_HANDLERS_PACKAGE = "com.assadev";

    public static SqlSessionFactory build(DataSource dataSource) throws Exception {
        PathMatchingResourcePatternResolver pathResolver = new PathMatchingResourcePatternResolver();

        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setTypeAliasesPackage(TYPE_ALIASES_PACKAGE);
        sqlSessionFactoryBean.setTypeHandlersPackage(TYPE_HANDLERS_PACKAGE);
        sqlSessionFactoryBean.setConfigLocation(pathResolver.getResource(CONFIG_LOCATION_PATH));
        sqlSessionFactoryBean.setMapperLocations(pathResolver.getResources(MAPPER_LOCATION_PATH));

        return sqlSessionFactoryBean.getObject();
    }
}

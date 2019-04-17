/*
 * Copyright (c) 2019. zhangbohan.dell@gmail.com ,All Rights Reserved
 *
 */

package xyz.zhangbohan.hive.config;

/**
 * file encoding: utf-8
 * Function :
 * Create : 4/12/2019 4:09 PM
 *
 * @author : zhangbohan.dell@gmail.com
 * @version : 1.0
 */


import com.alibaba.druid.pool.DruidDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
@Slf4j
public class HiveDruidConfig {

	private final HiveDruid druid;
	@Autowired
	public HiveDruidConfig(HiveDruid druid) {
		this.druid = druid;
	}

	@Bean(name = "hiveDruidDataSource")
	@Qualifier("hiveDruidDataSource")
	public DataSource dataSource() {
		log.info(druid.getUser());
		log.info(druid.getPassword());
		DruidDataSource datasource = new DruidDataSource();
		datasource.setUrl(druid.getUrl());
		datasource.setUsername(druid.getUser());
		datasource.setPassword(druid.getPassword());
		datasource.setDriverClassName(druid.getDriverClassName());
		datasource.setInitialSize(druid.getInitialSize());
		datasource.setMinIdle(druid.getMinIdle());
		datasource.setMaxActive(druid.getMaxActive());
		datasource.setMaxWait(druid.getMaxWait());
		datasource.setTimeBetweenEvictionRunsMillis(druid.getTimeBetweenEvictionRunsMillis());
		datasource.setMinEvictableIdleTimeMillis(druid.getMinEvictableIdleTimeMillis());
		datasource.setValidationQuery(druid.getValidationQuery());
		datasource.setTestWhileIdle(druid.isTestWhileIdle());
		datasource.setTestOnBorrow(druid.isTestOnBorrow());
		datasource.setTestOnReturn(druid.isTestOnReturn());
		datasource.setPoolPreparedStatements(druid.isPoolPreparedStatements());
		datasource.setMaxPoolPreparedStatementPerConnectionSize(druid.getMaxPoolPreparedStatementPerConnectionSize());
		datasource.setConnectionErrorRetryAttempts(druid.getConnectionErrorRetryAttempts());
		datasource.setBreakAfterAcquireFailure(druid.isBreakAfterAcquireFailure());
		return datasource;
	}

	@Bean(name = "hiveDruidTemplate")
	public JdbcTemplate hiveDruidTemplate(@Qualifier("hiveDruidDataSource") DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}
	//省略getter、setter
}
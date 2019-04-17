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


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Data
@Component
@ConfigurationProperties(prefix = "hive")
public class HiveDruid {

	private String url;
	private String user;
	private String password;
	private String driverClassName;
	private int initialSize;
	private int minIdle;
	private int maxActive;
	private int maxWait;
	private int timeBetweenEvictionRunsMillis;
	private int minEvictableIdleTimeMillis;
	private String validationQuery;
	private boolean testWhileIdle;
	private boolean testOnBorrow;
	private boolean testOnReturn;
	private boolean poolPreparedStatements;
	private int maxPoolPreparedStatementPerConnectionSize;
	private int connectionErrorRetryAttempts;
	private boolean breakAfterAcquireFailure;

}
/*
 * Copyright (c) 2019. zhangbohan.dell@gmail.com ,All Rights Reserved
 *
 */

package xyz.zhangbohan.hive.controller;

/**
 * file encoding: utf-8
 * Function :
 * Create : 4/12/2019 4:11 PM
 *
 * @author : zhangbohan.dell@gmail.com
 * @version : 1.0
 */

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.zhangbohan.hive.tools.HiveUtils;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
public class IndexController {


	@Autowired
	private HiveUtils hiveUtils;

	@GetMapping("/table/show")
	public List<String> showtables() {
		return hiveUtils.listTables();
	}

}

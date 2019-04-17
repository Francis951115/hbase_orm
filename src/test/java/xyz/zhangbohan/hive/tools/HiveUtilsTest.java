/*
 * Copyright (c) 2019. zhangbohan.dell@gmail.com ,All Rights Reserved
 *
 */

package xyz.zhangbohan.hive.tools;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import xyz.zhangbohan.hive.HiveApplication;
import xyz.zhangbohan.hive.entity.NativePage;

import java.sql.SQLException;
import java.util.Collections;

import static org.junit.Assert.*;
@RunWith(SpringRunner.class)
@SpringBootTest(classes = HiveApplication.class)
public class HiveUtilsTest {

	@Autowired
	private HiveUtils hiveUtils;


	@Test
	public void listTables() {
		hiveUtils.listTables().forEach(System.out::println);
	}

	@Test
	public void insert() throws Exception {
		hiveUtils.insert(Collections.singletonList(new NativePage()));
	}

	@Test
	public void getMedata() throws SQLException {
		String[][] strings = new String[2][1];
		hiveUtils.getMetadata("nativepage",strings);
	}
}

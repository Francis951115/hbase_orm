/*
 * Copyright (c) 2019. zhangbohan.dell@gmail.com ,All Rights Reserved
 *
 */

package xyz.zhangbohan.hive.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 * file encoding: utf-8
 * Function :
 * Create : 4/16/2019 7:04 PM
 *
 * @author : zhangbohan.dell@gmail.com
 * @version : 1.0
 */
@Slf4j
@Component
public class HiveUtils {


	@Autowired
	@Qualifier("hiveDruidTemplate")
	private JdbcTemplate jdbcTemplate;

	Statement statement;

	@Autowired
	HiveUtils(DataSource druidDataSource) throws Exception {
		 statement = druidDataSource.getConnection().createStatement();
	}

	public List<String> listTables() {
		List<String> list = new ArrayList<String>();
		try {
			String sql = "show tables";
			log.info("Running: " + sql);
			ResultSet res = statement.executeQuery(sql);
			while (res.next()) {
				list.add(res.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public int insert(List objects) throws Exception {
		Class<?> clazz = objects.get(0).getClass();
//	类名 变小写 ==》 表名
		String tableName = clazz.getSimpleName().toLowerCase();
		final String[][] columns = new String[2][1];
		getMetadata(tableName, columns);
		StringBuilder stringBuilder = new StringBuilder("insert into ").append(tableName).append("(");
		Arrays.stream(columns[0]).forEach(c-> stringBuilder.append(c).append(","));
		stringBuilder.deleteCharAt(stringBuilder.length()-1).append(") ").append("values");
		objects.forEach(o->{
			stringBuilder.append("(");
			for (int i = 0; i < Integer.parseInt(columns[0][0]); i++) {
				try {
//					表字段 变驼峰 去除_ ==》 类属性名
					Method method = clazz.getDeclaredMethod("get" + StringUtils.upperAndChangeUnderline(columns[0][i]));
					Object invoke = method.invoke(objects);
					if ("string".equals(columns[1][i])){
						stringBuilder.append("\"").append(invoke).append("\",");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			stringBuilder.deleteCharAt(stringBuilder.length() - 1).append("),");
		});
		String s = stringBuilder.deleteCharAt(stringBuilder.length() - 1).toString();
		log.info("正在执行 "+s);
		return jdbcTemplate.update(s);
	}
	/**
	 * @desc 获取表的
	 * @param  tableName 表名
	 * @param columns 接收字段名和字段类型
	 */
	public void getMetadata(String tableName, String[][] columns) throws SQLException {
		String sql = "describe " + tableName;
		jdbcTemplate.query(sql, new ResultSetExtractor<Object>() {
				@Override
				public Object extractData(ResultSet resultSet) throws SQLException, DataAccessException {
					List<String> names = new ArrayList<>();
					List<String> types = new ArrayList<>();

					while (resultSet.next()) {
						names.add(resultSet.getString(1));
						types.add(resultSet.getString(2));
					}
					String[] array =new String[names.size()];
					columns[0] =  names.toArray(array);
					columns[1] = types.toArray(array);
					return null;
				}
		});
	}


	public int update(Object... objects) {
		return 0;
	}



}

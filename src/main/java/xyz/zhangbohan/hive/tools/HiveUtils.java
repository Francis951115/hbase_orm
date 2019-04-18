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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
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

	private Statement statement;
	private Connection connection;

	@Autowired
	HiveUtils(DataSource druidDataSource) throws Exception {
		connection = druidDataSource.getConnection();
		statement = connection.createStatement();
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

	public int insert(Object object) throws Exception {
		Class<?> clazz = object.getClass();
//	类名 变小写 ==》 表名
		String tableName = clazz.getSimpleName().toLowerCase();
		final String[][] columns = new String[2][1];
		getMetadata(tableName, columns);
		StringBuilder stringBuilder = new StringBuilder("insert into ").append(tableName).append("(");
		Arrays.stream(columns[0]).forEach(c -> stringBuilder.append(c).append(","));
		stringBuilder.deleteCharAt(stringBuilder.length() - 1).append(") ").append("values").append("(");
		createInsertValues(object, clazz, columns, stringBuilder);
		stringBuilder.deleteCharAt(stringBuilder.length() - 1).append("),");
		String s = stringBuilder.deleteCharAt(stringBuilder.length() - 1).toString();
		log.info("正在执行 " + s);
		return jdbcTemplate.update(s);
	}

	public int insert(List objects) throws Exception {
		Class<?> objectClass = objects.get(0).getClass();
//	类名 变小写 ==》 表名
		String tableName = objectClass.getSimpleName().toLowerCase();
		final String[][] columns = new String[2][1];
		getMetadata(tableName, columns);
		StringBuilder stringBuilder = new StringBuilder("insert into ").append(tableName).append("(");
		Arrays.stream(columns[0]).forEach(c -> stringBuilder.append(c).append(","));
		stringBuilder.deleteCharAt(stringBuilder.length() - 1).append(") ").append("values");
		objects.forEach(o -> {
			stringBuilder.append("(");
			createInsertValues(objects, objectClass, columns, stringBuilder);
			stringBuilder.deleteCharAt(stringBuilder.length() - 1).append("),");
		});
		String s = stringBuilder.deleteCharAt(stringBuilder.length() - 1).toString();
		log.info("正在执行 " + s);
		return jdbcTemplate.update(s);
	}


	public int updateByPrimaryKey(Object object) throws Exception {
		Class<?> objectClass = object.getClass();
//	类名 变小写 ==》 表名
		String tableName = objectClass.getSimpleName().toLowerCase();
		final String[][] columns = new String[2][1];
		getMetadata(tableName, columns);
		StringBuilder stringBuilder = new StringBuilder("update ").append(tableName).append(" set ");
		createUpdateSet(stringBuilder, columns, objectClass, object);
		List<String> pks = getPrimaryKey(tableName);
		if (pks.isEmpty()) {
			throw new Exception("no primary key！");
		}
		stringBuilder.append(" where ");
		AtomicBoolean flag = new AtomicBoolean(false);
		pks.forEach(p->{
			createUpdateWhere(object, objectClass, stringBuilder, flag, p);
		});
		if (flag.get()) {
			stringBuilder.deleteCharAt(stringBuilder.length() - 3);
		}
		return jdbcTemplate.update(stringBuilder.toString());
	}


	private void createUpdateWhere(Object object, Class<?> objectClass, StringBuilder stringBuilder, AtomicBoolean flag, String p) {
		try {
			Method method = objectClass.getDeclaredMethod("get" + StringUtils.upperAndChangeUnderline(p));
			Object value = method.invoke(object);
			if (value != null) {
				flag.set(true);
				stringBuilder.append(p).append(" = ").append(value).append( " and");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private List<String> getPrimaryKey(String tableName) throws SQLException {
		String sql = "describe " + tableName;
		List<String> pks = new ArrayList<>();
		ResultSet resultSet = connection.getMetaData().getPrimaryKeys(null, "default", tableName);
		while (resultSet.next()) {
			pks.add(resultSet.getString(1));
		}
		return pks;
	}
	private void createUpdateSet(StringBuilder stringBuilder, String[][] columns, Class<?> objectClass, Object object) {
		AtomicBoolean flag = new AtomicBoolean(false);
		Arrays.stream(columns[0]).forEach(c ->{
			createUpdateWhere(object, objectClass, stringBuilder, flag, c);

		});
		if (flag.get()) {
			stringBuilder.deleteCharAt(stringBuilder.length() - 3);
		}
	}


	/**
	 * @param tableName 表名
	 * @param columns   接收字段名和字段类型
	 * @desc 获取表的
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
				String[] array = new String[names.size()];
				columns[0] = names.toArray(array);
				columns[1] = types.toArray(array);
				return null;
			}
		});
	}
	/**
	 * 解析object
	 */
	public void createInsertValues(Object object, Class<?> objectClass, String[][] columns, StringBuilder stringBuilder) {
		for (int i = 0; i < Integer.parseInt(columns[0][0]); i++) {
			try {
//					表字段 变驼峰 去除_ ==》 类属性名
				Method method = objectClass.getDeclaredMethod("get" + StringUtils.upperAndChangeUnderline(columns[0][i]));
				Object invoke = method.invoke(object);
				if ("string".equals(columns[1][i])) {
					stringBuilder.append("\"").append(invoke).append("\",");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public int update(Object... objects) {

		return 0;
	}


}

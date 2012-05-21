package org.db.jdbcutils.utils;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 常量库
 * 
 * @User: HUBO
 * @Date May 16, 2012
 * @Time 10:16:15 AM
 */
public final class Constant {

	/**
	 * 数据库类型：oracle
	 */
	public static String ORACLE = "oracle";

	/**
	 * 数据库类型：mysql
	 */
	public static String MYSQL = "mysql";

	/**
	 * 数据库类型：sqlserver
	 */
	public static String SQLSERVER = "sqlserver";

	/**
	 * MySQL数据库ID键值是否递增
	 */
	public static String MYSQL_SEQ = "increase by degrees";
	
	/**
	 * 判断字符串类型等于nul或空字符串。
	 * 
	 * @return true(等于nul或空字符串)，false(不等于nul或空字符串)
	 */
	public static boolean isEmpty(String value) {
		if (value == null || value.equals("")) {
			return true;
		}
		return false;
	}

	/**
	 * 判断字符串类型不等于nul或空字符串。
	 * 
	 * @return true(不等于nul或空字符串)，false(等于nul或空字符串)
	 */
	public static boolean isNotEmpty(String value) {
		if (value == null || value.equals("")) {
			return false;
		}
		return true;
	}

	/**
	 * 判断参数是否为Java集合类型
	 */
	public static boolean isCollection(Class type) {
		if (Map.class.isAssignableFrom(type)) {
			return true;
		} else if (List.class.isAssignableFrom(type)) {
			return true;
		} else if (Set.class.isAssignableFrom(type)) {
			return true;
		}
		return false;
	}

	/**
	 * 判断Class模版是否基础类型
	 * 
	 * @param clazz
	 *            Class模版
	 * @return true(是基础类型) || false(不是基础类型)
	 */
	public static boolean isBasicType(Class clazz) {
		if (String.class.isAssignableFrom(clazz)) {
			return true;

		} else if (int.class.isAssignableFrom(clazz)
				|| Integer.class.isAssignableFrom(clazz)) {
			return true;

		} else if (double.class.isAssignableFrom(clazz)
				|| Double.class.isAssignableFrom(clazz)) {
			return true;

		} else if (boolean.class.isAssignableFrom(clazz)
				|| Boolean.class.isAssignableFrom(clazz)) {
			return true;

		} else if (float.class.isAssignableFrom(clazz)
				|| Float.class.isAssignableFrom(clazz)) {
			return true;

		} else if (long.class.isAssignableFrom(clazz)
				|| Long.class.isAssignableFrom(clazz)) {
			return true;

		} else if (Date.class.isAssignableFrom(clazz)) {
			return true;

		} else if (byte.class.isAssignableFrom(clazz)
				|| Byte.class.isAssignableFrom(clazz)) {
			return true;

		} else if (short.class.isAssignableFrom(clazz)
				|| Short.class.isAssignableFrom(clazz)) {
			return true;

		} else if (char.class.isAssignableFrom(clazz)
				|| Character.class.isAssignableFrom(clazz)) {
			return true;

		} else if (Timestamp.class.isAssignableFrom(clazz)) {
			return true;
		}

		return false;
	}

	public static void main(String[] args) {
	}
}

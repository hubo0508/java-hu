package org.db.jdbcutils.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * 常量库
 * 
 * @User: HUBO
 * @Date May 16, 2012
 * @Time 10:16:15 AM
 */
public final class Constant {
	
	/**
	 * 日志输出
	 */
	static Logger log = Logger.getLogger(Constant.class);

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
	 * 清除Object[]数组中null元素，返回新数组
	 */
	public static Object[] cleanEmpty(Object[] value) {
		int emptyCount = countCharacter(deepToString(value), "null");
		if (emptyCount == 0) {
			return value;
		}

		int count = 0;

		Object[] newObject = new Object[value.length - emptyCount];
		for (int i = 0; i < value.length; i++) {
			Object obj = value[i];
			if (obj != null) {
				newObject[count] = value[i];
				count++;
			}
		}

		return newObject;
	}
	
	public static String deepToString(Object[] a) {
		if (a == null || a.length == 0)
			return "null";

		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < a.length; i++) {
			buf.append(a[i] + ",");
		}

		return buf.toString();
	}

	/**
	 * 取得字符在文本中出现的次数
	 */
	public static int countCharacter(String text, String character) {
		int count = 0;
		int m = text.indexOf(character);
		while (m != -1) {
			m = text.indexOf(character, m + 1);
			count++;
		}
		return count;
	}

	

	public static boolean startsWithWhere(String sql) {
		if (sql == null) {
			return false;
		}
		return sql.toUpperCase().startsWith("WHERE");
	}

	public static boolean startsWithSelect(String sql) {
		if (sql == null) {
			return false;
		}
		return sql.toUpperCase().startsWith("SELECT");
	}

	public static boolean startsWithUpate(String sql) {
		if (sql == null) {
			return false;
		}
		return sql.toUpperCase().startsWith("UPDATE");
	}

	public static boolean startsWithDelete(String sql) {
		if (sql == null) {
			return false;
		}
		return sql.toUpperCase().startsWith("DELETE");
	}

	public static boolean startsWithInsert(String sql) {
		if (sql == null) {
			return false;
		}
		return sql.toUpperCase().startsWith("INSERT");
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
	 * 判断Class是否为ArrayList
	 * 
	 * @return true(为List或为List的子集ArrayList)，false(不为List或为List的子集ArrayList)
	 */
	public static boolean isArrayList(Class clazz) {
		if (ArrayList.class.isAssignableFrom(clazz)) {
			return true;
		} 
		return false;
	}
	
	/**
	 * 判断Class是否为List或为List的子集
	 * 
	 * @return true(为List或为List的子集)，false(不为List或为List的子集)
	 */
	public static boolean isList(Class clazz) {
		if (List.class.isAssignableFrom(clazz)) {
			return true;
		} 
		return false;
	}
	

	/**
	 * 判断Class是否为Map
	 * 
	 * @return true(为Map)，false(不为Map)
	 */
	public static boolean isMap(Class clazz) {
		if (Map.class.isAssignableFrom(clazz)) {
			return true;
		} 
		return false;
	}
	
	/**
	 * 判断Class是否为Map或为Map的子集HashMap
	 * 
	 * @return true(为Map或为Map的子集HashMap)，false(不为Map或为Map的子集HashMap)
	 */
	public static boolean isHashMap(Class clazz) {
		if (HashMap.class.isAssignableFrom(clazz)) {
			return true;
		}
		return false;
	}

	/**
	 * 判断Class是否为Map或为Map的子集LinkedHashMap
	 * 
	 * @return true(为Map或为Map的子集LinkedHashMap)，false(不为Map或为Map的子集LinkedHashMap)
	 */
	public static boolean isLinkedHashMap(Class clazz) {
		if (LinkedHashMap.class.isAssignableFrom(clazz)) {
			return true;
		}
		return false;
	}
	
	/**
	 * 判断文本是否全部大写
	 * 
	 * @param text
	 *            文本
	 * @return true(文本全为大写) || false(文本不全为大写)
	 */
	public static boolean isAllCaps(String text) {
		if (text.equals(text.toUpperCase())) {
			return true;
		}
		return false;
	}
	
	/**
	 * 关闭连接
	 */
	public static void close(ResultSet rs, PreparedStatement stmt) {
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException e) {
			log.error(e.getMessage());
		}

		try {
			if (stmt != null) {
				stmt.close();
			}
		} catch (SQLException e) {
			log.error(e.getMessage());
		}
	}

	/**
	 * 关闭连接
	 */
	public static void close(PreparedStatement stmt) {
		try {
			if (stmt != null) {
				stmt.close();
			}
		} catch (SQLException e) {
			log.error(e.getMessage());
		}
	}

	/**
	 * 关闭连接
	 */
	public static void close(Connection conn) {
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			log.error(e.getMessage());
		}
	}

	/**
	 * 关闭连接
	 */
	public static void close(ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException e) {
			log.error(e.getMessage());
		}
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

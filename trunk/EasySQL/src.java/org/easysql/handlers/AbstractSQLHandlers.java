package org.easysql.handlers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.StringTokenizer;

import org.easysql.core.Entity;

public class AbstractSQLHandlers {
	
	private EntityFilter filter;
	
	private String fieldFule;
	
	/**
	 * SQL关键字
	 */
	protected final static String[] keywords = new String[] { "IN", "WHERE",
			"SELECT", "FROM", "UPDATE", "SET" };

	public static void main(String[] args) {
		String sql = "UPDATE user SET id=?, username=?, password=? WHERE   username=   ?, password=         ?";

		AbstractSQLHandlers sqlHandlers = new AbstractSQLHandlers();
		System.out.println(sqlHandlers.standardFormattingSQL(sql));

	}

	public String[] splitSQL(String sql) {
		StringTokenizer stk = new StringTokenizer(sql, " ");
		int len = stk.countTokens();
		String[] sqlArray = new String[len];
		for (int i = 0; i < len; i++) {
			sqlArray[i] = stk.nextToken();
		}
		return sqlArray;
	}

	public String standardFormattingSQL(String sql) {

		StringBuffer sb = new StringBuffer();
		String[] splitSql = splitSQL(sql);

		int len = splitSql.length;
		for (int i = 0; i < len; i++) {
			String element = splitSql[i];
			sb.append(element);
			if (i == len - 1) {
				break;
			}
			String nextElement = splitSql[i + 1];
			if ("=".equals(nextElement)) {
				sb.append(nextElement);
				sb.append(splitSql[i + 2]);
				i = i + 2;
			}
			if ("=?".equals(nextElement)) {
				sb.append(nextElement);
				i = i + 1;
			}
			if ((element.length() - 1) == element.indexOf("=")) {
				sb.append(nextElement);
				i = i + 1;
			}
			if (i < len-1) {
				sb.append(" ");
			}
		}

		return sb.toString();
	}
	
	@SuppressWarnings("unchecked")
	public Object getEntityValue(String methodname, Entity entity) {
		try {
			Class clazz = entity.getClass();
			Method method = clazz.getMethod(methodname, new Class[] {});
			Object value = method.invoke(entity, new Object[] {});
			return value;
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	public EntityFilter getFilter() {
		return filter;
	}

	public void setFilter(EntityFilter filter) {
		this.filter = filter;
	}

	public String getFieldFule() {
		return fieldFule;
	}

	public void setFieldFule(String fieldFule) {
		this.fieldFule = fieldFule;
	}
}

package org.easysql.handlers;

import java.util.StringTokenizer;

public class AbstractSQLHandlers {

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
			String nextElement = splitSql[i + 1];
			if ("=".equals(nextElement)) {
				sb.append(nextElement);
				sb.append(splitSql[i + 2]);
				i = i + 2;
			}
			if ("=?".equals(nextElement)) {
				sb.append(nextElement);
			}
			sb.append(" ");
		}

		return sb.toString();
	}
}

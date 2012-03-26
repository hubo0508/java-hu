package com.easysql.engine.sql;

public class SQLEngine {

	public static String getInsertSQL(String[] fields) {
		StringBuffer sb = new StringBuffer();

		sb.append("INSERT INTO ");
		sb.append(fields[0]);
		sb.append(" (");
		setInsertKey(fields, sb);
		sb.append(") VALUES (");
		setInsertValue(fields, sb);
		sb.append(")");

		return sb.toString();
	}

	private static void setInsertKey(String[] fields, StringBuffer sb) {
		int len = fields.length;
		for (int i = 1; i < len; i++) {
			String field = fields[i];
			sb.append(field);
			if (i < len - 1) {
				sb.append(", ");
			}
		}
	}

	private static void setInsertValue(String[] fields, StringBuffer sb) {
		int len = fields.length;
		for (int i = 0; i < len; i++) {
			sb.append("?");
			if (i < len - 1) {
				sb.append(", ");
			}
		}
	}
}

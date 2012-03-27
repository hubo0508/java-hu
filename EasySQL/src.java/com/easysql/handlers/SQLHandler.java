package com.easysql.handlers;

import org.apache.commons.lang.StringUtils;

import com.easysql.EasySQL;
import com.easysql.core.Mapping;

public class SQLHandler {

	public static String getInsertSQL(EntityHandler ref) {

		String[] fields = ref.getRowField();
		fields = SQLAdaptation.changeFileds(ref.getClazz(), fields);
		
		EntityFilter targetMap = (EntityFilter) Mapping.getInstance().get(
				EasySQL.key(ref.getClazz()));
		String database = (String) Mapping.getInstance().get(EasySQL.DATABASE);

		// 主鍵生成機制
		String generatorName = (String) targetMap.get(EntityFilter.GENERATOR);
		if (StringUtils.isEmpty(generatorName)) {
			generatorName = (String) Mapping.getInstance().get(
					EasySQL.GENERATOR_SEQUENCE);
		}

		StringBuffer sb = new StringBuffer();

		sb.append("INSERT INTO ");
		sb.append(fields[0]);
		sb.append(" (");
		setInsertKey(fields, sb);
		sb.append(") VALUES (");
		setInsertValue(database, generatorName, fields, sb);
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

	private static void setInsertValue(String database, String generatorName,
			String[] fields, StringBuffer sb) {

		int len = fields.length;
		for (int i = 1; i < len; i++) {
			if (i == 1 && getGenerationOfPrimaryKey()) {
				if ("oracle".equals(database)) {
					sb.append(generatorName);
					sb.append(".NEXTVAL");
				}
			} else {
				sb.append("?");
			}
			if (i < len - 1) {
				sb.append(", ");
			}
		}
	}

	private static boolean getGenerationOfPrimaryKey() {

		Mapping mapping = Mapping.getInstance();
		String databasename = (String) mapping.get(EasySQL.DATABASE);
		String generator = (String) mapping.get(EasySQL.GENERATOR);

		if ("oracle".equals(databasename)) {
			if ("native".equals(generator) || "sequence".equals(generator)) {
				return true;
			}
		}

		if ("sqlserver".equals(databasename)) {

		}

		if ("oracle".equals(databasename)) {

		}

		return false;
	}
}

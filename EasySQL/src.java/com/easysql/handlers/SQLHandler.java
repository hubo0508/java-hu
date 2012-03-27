package com.easysql.handlers;

import com.easysql.EasySQL;
import com.easysql.core.Mapping;

public class SQLHandler {

	public static String getInsertSQL(EntityHandler ref) {

		String[] fields = ref.getRowField();
		fields = SQLAdaptation.changeFileds(ref.getClazz(), fields);

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

		String database = (String) Mapping.getInstance().get(EasySQL.DATABASE);
		String generatorName = (String) Mapping.getInstance().get(
				EasySQL.GENERATOR_SEQUENCE);

		int len = fields.length;
		for (int i = 0; i < len; i++) {
			if (i == 0 && getGenerationOfPrimaryKey()) {
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

package com.easysql.handlers;

import org.apache.commons.lang.StringUtils;

import com.easysql.EasySQL;
import com.easysql.core.Mapping;

public class SQLHandler {

	public static String getSelectSQL(EntityHandler ref, String sql) {

		StringBuffer sb = new StringBuffer();
		int index = sql.indexOf("*");
		if (sql.indexOf("*") >= 0) {
			String[] fields = formatFields(ref.getClazz(), ref.getFields());
			int len = fields.length;
			for (int i = 1; i < fields.length; i++) {
				sb.append(fields[i]);
				if (i < len - 1) {
					sb.append(",");
				}
			}
		}

		sql = sql.substring(0, index) + sb.toString()
				+ sql.substring(index + 1);

		return formatFields(ref, sql);
	}

	public static String getDeleteSQL(EntityHandler ref) {

		EntityFilter targetMap = (EntityFilter) Mapping.getInstance().get(
				EasySQL.key(ref.getClazz()));
		String idkey = (String) targetMap.get(EntityFilter.ID);

		String tablename = formatSingeField(ref.getClazz(), ref.getClazz()
				.getSimpleName());

		StringBuffer sb = new StringBuffer();
		sb.append("DELETE FROM ");
		sb.append(tablename);
		sb.append(" WHERE ");
		sb.append(idkey);
		sb.append("=?");

		return sb.toString();
	}

	public static String getDeleteSQL(EntityHandler ref, String where) {

		String tablename = formatSingeField(ref.getClazz(), ref.getClazz()
				.getSimpleName());

		StringBuffer sb = new StringBuffer();
		sb.append("DELETE FROM ");
		sb.append(tablename);
		sb.append(" WHERE ");
		sb.append(formatFields(ref, where));

		return sb.toString();
	}

	public static String getUpdateSQL(EntityHandler ref, String[] filed) {

		String[] fields = formatFields(ref.getClazz(), filed);

		// 取得當前實體鍋爐條件
		EntityFilter targetMap = (EntityFilter) Mapping.getInstance().get(
				EasySQL.key(ref.getClazz()));
		String idkey = (String) targetMap.get(EntityFilter.ID);

		return generateUpdateSQL(fields, idkey, idkey + "=?").toString();
	}

	public static String getUpdateSQL(EntityHandler ref, String[] filed,
			String where) {

		String[] fields = formatFields(ref.getClazz(), filed);

		// 取得當前實體鍋爐條件
		EntityFilter targetMap = (EntityFilter) Mapping.getInstance().get(
				EasySQL.key(ref.getClazz()));
		String idkey = (String) targetMap.get(EntityFilter.ID);

		return generateUpdateSQL(fields, idkey, where).toString();
	}

	public static String getUpdateSQL(EntityHandler ref) {

		String[] fields = formatFields(ref.getClazz(), ref.getFields());

		// 取得當前實體鍋爐條件
		EntityFilter targetMap = (EntityFilter) Mapping.getInstance().get(
				EasySQL.key(ref.getClazz()));
		String idkey = (String) targetMap.get(EntityFilter.ID);

		return generateUpdateSQL(fields, idkey, idkey + "=?").toString();
	}

	// UPDATE 表名称 SET 列名称 = 新值 WHERE 列名称 = 某值
	public static String getUpdateSQL(EntityHandler ref, String sql) {

		String[] fields = formatFields(ref.getClazz(), ref.getFields());

		// 取得當前實體鍋爐條件
		EntityFilter targetMap = (EntityFilter) Mapping.getInstance().get(
				EasySQL.key(ref.getClazz()));
		String idkey = (String) targetMap.get(EntityFilter.ID);

		return generateUpdateSQL(fields, idkey, formatFields(ref, sql))
				.toString();
	}

	public static String getInsertSQL(EntityHandler ref) {

		String[] fields = formatFields(ref.getClazz(), ref.getFields());

		// 取得當前實體鍋爐條件
		EntityFilter targetMap = (EntityFilter) Mapping.getInstance().get(
				EasySQL.key(ref.getClazz()));
		String database = (String) Mapping.getInstance().get(EasySQL.DATABASE);

		// 主鍵生成機制
		String geneValue = (String) targetMap
				.get(EntityFilter.GENERATOR_SEQ_VALUE);
		if (StringUtils.isEmpty(geneValue)) {
			geneValue = (String) Mapping.getInstance().get(
					EasySQL.GENERATOR_SEQ_VALUE);
		}

		StringBuffer sb = new StringBuffer();

		sb.append("INSERT INTO ");
		sb.append(fields[0]);
		sb.append(" (");
		setInsertKey(database, fields, sb);
		sb.append(") VALUES (");
		setInsertValue(database, geneValue, fields, sb);
		sb.append(")");

		return sb.toString();
	}

	private static void setUpdateKey(String idkey, String[] fields,
			StringBuffer sb) {

		int len = fields.length;
		for (int i = 0; i < len; i++) {
			String field = fields[i];
			if (!idkey.equals(field)) {
				sb.append(field);
				if (i < len - 1) {
					sb.append("=?, ");
				} else {
					sb.append("=? ");
				}
			}
		}
	}

	private static void setInsertKey(String database, String[] fields,
			StringBuffer sb) {
		int len = fields.length;
		for (int i = 1; i < len; i++) {
			if (i == 1 && getGenerationOfPrimaryKey(database)) {
				if ("mysql".equals(database)) {
					continue;
				} else if ("sqlserver".equals(database)) {
					continue;
				}
			}
			String field = fields[i];
			sb.append(field);
			if (i < len - 1) {
				sb.append(", ");
			}
		}
	}

	private static void setInsertValue(String database, String geneValue,
			String[] fields, StringBuffer sb) {

		int len = fields.length;
		for (int i = 1; i < len; i++) {
			if (i == 1 && getGenerationOfPrimaryKey(database)) {
				if ("oracle".equals(database)) {
					sb.append(geneValue);
					sb.append(".NEXTVAL");
				} else if ("mysql".equals(database)) {
					continue;
				} else if ("sqlserver".equals(database)) {
					continue;
				}
			} else {
				sb.append("?");
			}
			if (i < len - 1) {
				sb.append(", ");
			}
		}
	}

	private static boolean getGenerationOfPrimaryKey(String databasename) {

		Mapping mapping = Mapping.getInstance();
		String generator = (String) mapping.get(EasySQL.GENERATOR);

		if ("oracle".equals(databasename)) {
			if ("native".equals(generator) || "sequence".equals(generator)) {
				return true;
			}
		}

		if ("sqlserver".equals(databasename)) {

		}

		if ("mysql".equals(databasename)) {
			if ("native".equals(generator) || "identity".equals(generator)) {
				return true;
			}
		}

		return false;
	}

	/** ******************************************************************************************** */

	private static StringBuffer generateUpdateSQL(String[] fields,
			String idkey, String where) {
		StringBuffer sb = new StringBuffer();

		sb.append("UPDATE ");
		sb.append(fields[0]);
		sb.append(" SET ");
		setUpdateKey(idkey, fields, sb);
		sb.append("WHERE ");
		sb.append(where);

		return sb;
	}

	public static String formatSingeField(Class<?> clazz, String elements) {

		String nameRule = (String) Mapping.getInstance()
				.get(EasySQL.FIELD_RULE);
		EntityFilter targetMap = (EntityFilter) Mapping.getInstance().get(
				EasySQL.key(clazz));
		String[] replaceValue = (String[]) targetMap.get(EntityFilter.REPLACE);

		return convertedAfterElement(elements, replaceValue, nameRule);
	}

	public static String formatFields(EntityHandler ref, String sql) {
		String[] fields = ref.getFields();
		for (String s : fields) {
			int matchIndex = sql.indexOf(s);
			if (matchIndex >= 0) {
				String formatAfter = formatSingeField(ref.getClazz(), s);
				sql = sql.substring(0, matchIndex) + formatAfter
						+ sql.substring(matchIndex + s.length());
			}
		}

		return sql;
	}

	public static String[] formatFields(Class<?> clazz, String[] fields) {

		String nameRule = (String) Mapping.getInstance()
				.get(EasySQL.FIELD_RULE);
		EntityFilter targetMap = (EntityFilter) Mapping.getInstance().get(
				EasySQL.key(clazz));
		String[] replaceValue = (String[]) targetMap.get(EntityFilter.REPLACE);

		if (EasySQL.FIELD_RULE_HUMP.equals(nameRule)) {
			return fields;
		}

		for (int i = 0; i < fields.length; i++) {
			fields[i] = convertedAfterElement(fields[i], replaceValue, nameRule);
		}

		return fields;
	}

	public static String convertedAfterElement(String ele,
			String[] replaceValue, String nameRule) {

		if (EasySQL.FIELD_RULE_HUMP.equals(nameRule)) {
			return ele;
		}

		if (replaceValue != null) {
			ele = EasySQL.replaceFiled(replaceValue, ele);
		}

		if (EasySQL.FIELD_RULE_SEGMENTATION.equals(nameRule)) {
			ele = EasySQL.convertedIntoSegmentation(ele);
		}

		return ele;
	}
}

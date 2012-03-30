package org.easysql.handlers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.easysql.EasySQL;
import org.easysql.core.Entity;
import org.easysql.core.Mapping;

public class SQLHandler extends AbstractSQLHandlers {

	private EntityHandler eHandler;

	public SQLHandler(EntityHandler eHandler) {
		this.eHandler = eHandler;
	}

	public Object[] objectArray(Entity entity, String sql) {

		String[] fields = eHandler.getFields();
		for (String s : fields) {
			if (sql.indexOf(s) >= 0) {
				System.out.println(s);
			}
		}

		return null;
	}

	public Object[] objectArray(Entity entity) {

		String[] fields = eHandler.getFields();
		String database = (String) Mapping.getInstance().get(EasySQL.DATABASE);

		int count = 0;
		int len = fields.length;
		Object[] params = new Object[len - 1];
		for (int i = 1; i < len; i++) {
			if (i == 1 && getGenerationOfPrimaryKey(database)) {
				if ("mysql".equals(database)) {
					params = new Object[params.length - 1];
					continue;
				} else if ("sqlserver".equals(database)) {
					params = new Object[params.length - 1];
					continue;
				}
			}
			String methodname = getMethodName("get", fields[i]);
			Object value = getValue(eHandler.getClazz(), methodname, entity);
			params[count] = value;
			count++;
		}

		return params;
	}

	private Object getValue(Class<Entity> clazz, String methodname,
			Entity entity) {
		try {
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

	public String getSelectSQL(String sql) {

		StringBuffer sb = new StringBuffer();
		int index = sql.indexOf("*");
		if (sql.indexOf("*") >= 0) {
			String[] fields = formatFields(eHandler.getClazz(), eHandler
					.getFields());
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

		return formatFields(sql);
	}

	public String getDeleteSQL() {

		EntityFilter targetMap = (EntityFilter) Mapping.getInstance().get(
				EasySQL.key(eHandler.getClazz()));
		String idkey = (String) targetMap.get(EntityFilter.ID);

		String tablename = formatSingeField(eHandler.getClazz(), eHandler
				.getClazz().getSimpleName());

		StringBuffer sb = new StringBuffer();
		sb.append("DELETE FROM ");
		sb.append(tablename);
		sb.append(" WHERE ");
		sb.append(idkey);
		sb.append("=?");

		return sb.toString();
	}

	public String getDeleteSQL(String where) {

		String tablename = formatSingeField(eHandler.getClazz(), eHandler
				.getClazz().getSimpleName());

		StringBuffer sb = new StringBuffer();
		sb.append("DELETE FROM ");
		sb.append(tablename);
		sb.append(" WHERE ");
		sb.append(formatFields(where));

		return sb.toString();
	}

	public String getUpdateSQL(String[] filed) {

		String[] fields = formatFields(eHandler.getClazz(), filed);

		// 取得當前實體鍋爐條件
		EntityFilter targetMap = (EntityFilter) Mapping.getInstance().get(
				EasySQL.key(eHandler.getClazz()));
		String idkey = (String) targetMap.get(EntityFilter.ID);

		return generateUpdateSQL(fields, idkey, idkey + "=?").toString();
	}

	public String getUpdateSQL(String[] filed, String where) {

		String[] fields = formatFields(eHandler.getClazz(), filed);

		// 取得當前實體鍋爐條件
		EntityFilter targetMap = (EntityFilter) Mapping.getInstance().get(
				EasySQL.key(eHandler.getClazz()));
		String idkey = (String) targetMap.get(EntityFilter.ID);

		return generateUpdateSQL(fields, idkey, where).toString();
	}

	public String getUpdateSQL() {

		String[] fields = formatFields(eHandler.getClazz(), eHandler
				.getFields());

		// 取得當前實體鍋爐條件
		EntityFilter targetMap = (EntityFilter) Mapping.getInstance().get(
				EasySQL.key(eHandler.getClazz()));
		String idkey = (String) targetMap.get(EntityFilter.ID);

		return generateUpdateSQL(fields, idkey, idkey + "=?").toString();
	}

	// UPDATE 表名称 SET 列名称 = 新值 WHERE 列名称 = 某值
	public String getUpdateSQL(String sql) {

		String[] fields = formatFields(eHandler.getClazz(), eHandler
				.getFields());

		// 取得當前實體鍋爐條件
		EntityFilter targetMap = (EntityFilter) Mapping.getInstance().get(
				EasySQL.key(eHandler.getClazz()));
		String idkey = (String) targetMap.get(EntityFilter.ID);

		return standardFormattingSQL(generateUpdateSQL(fields, idkey,
				formatFields(sql)).toString());
	}

	public String getInsertSQL() {

		String[] fields = formatFields(eHandler.getClazz(), eHandler
				.getFields());

		// 取得當前實體鍋爐條件
		EntityFilter targetMap = (EntityFilter) Mapping.getInstance().get(
				EasySQL.key(eHandler.getClazz()));
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

	private void setUpdateKey(String idkey, String[] fields, StringBuffer sb) {

		int len = fields.length;
		for (int i = 1; i < len; i++) {
			String field = fields[i];
			// if (!idkey.equals(field)) {
			sb.append(field);
			if (i < len - 1) {
				sb.append("=?, ");
			} else {
				sb.append("=? ");
			}
			// }
		}
	}

	private void setInsertKey(String database, String[] fields, StringBuffer sb) {
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

	private void setInsertValue(String database, String geneValue,
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

	private boolean getGenerationOfPrimaryKey(String databasename) {

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

	private StringBuffer generateUpdateSQL(String[] fields, String idkey,
			String where) {
		StringBuffer sb = new StringBuffer();

		sb.append("UPDATE ");
		sb.append(fields[0]);
		sb.append(" SET ");
		setUpdateKey(idkey, fields, sb);
		sb.append("WHERE ");
		sb.append(where);

		return sb;
	}

	public String formatSingeField(Class<?> clazz, String elements) {

		String nameRule = (String) Mapping.getInstance()
				.get(EasySQL.FIELD_RULE);
		EntityFilter targetMap = (EntityFilter) Mapping.getInstance().get(
				EasySQL.key(clazz));
		String[] replaceValue = (String[]) targetMap.get(EntityFilter.REPLACE);

		return convertedAfterElement(elements, replaceValue, nameRule);
	}

	public String formatFields(String sql) {
		String[] fields = eHandler.getFields();
		for (String s : fields) {
			int matchIndex = sql.indexOf(s);
			if (matchIndex >= 0) {
				String formatAfter = formatSingeField(eHandler.getClazz(), s);
				sql = sql.substring(0, matchIndex) + formatAfter
						+ sql.substring(matchIndex + s.length());
			}
		}

		return sql;
	}

	public String[] formatFields(Class<?> clazz, String[] fields) {

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

	public String convertedAfterElement(String ele, String[] replaceValue,
			String nameRule) {

		if (EasySQL.FIELD_RULE_HUMP.equals(nameRule)) {
			return ele;
		}

		if (replaceValue != null) {
			ele = replaceFiled(replaceValue, ele);
		}

		if (EasySQL.FIELD_RULE_SEGMENTATION.equals(nameRule)) {
			ele = convertedIntoSegmentation(ele);
		}

		return ele;
	}

	private String getMethodName(String methodPrefix, String fieldName) {
		String firstLetter = fieldName.substring(0, 1).toUpperCase();
		return methodPrefix + firstLetter + fieldName.substring(1);
	}

	private String replaceFiled(String[] replaceValue, String text) {

		String returnvalue = text;
		for (String string : replaceValue) {
			if (StringUtils.isNotEmpty(string)) {
				String[] value = string.split(":");
				if (value[0].equals(text)) {
					returnvalue = value[1];
				}
			}
		}

		return returnvalue;
	}

	private String convertedIntoSegmentation(String text) {

		StringBuffer sb = new StringBuffer();
		int cacheIndex = 0;

		Pattern p = Pattern.compile("[A-Z]");
		Matcher m = p.matcher(text);
		while (m.find()) {
			String value = text.substring(cacheIndex, m.start());
			if (StringUtils.isEmpty(value)) {
				break;
			}
			sb.append(value);
			sb.append("_");
			cacheIndex = m.start();
		}
		sb.append(text.substring(cacheIndex));

		return sb.toString().toLowerCase();
	}

}

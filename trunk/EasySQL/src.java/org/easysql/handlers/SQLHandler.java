package org.easysql.handlers;

import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.easysql.EasySQL;
import org.easysql.core.Entity;
import org.easysql.core.Mapping;

/**
 * SQL处理</br></br>
 * 
 * 在实例化该对象时，必须传入org.easysql.handlers.EntityHandler实例化对象
 */
public class SQLHandler extends AbstractSQLHandlers {

	private EntityHandler eHandler;

	/**
	 * 在实例化该对象时，必须传入org.easysql.handlers.EntityHandler实例化对象
	 */
	public SQLHandler(EntityHandler eHandler) {
		this.eHandler = eHandler;
		super.setFilter((EntityFilter) Mapping.getInstance().get(
				EasySQL.key(eHandler.getClazz())));
		super.setNameRule((String) Mapping.getInstance()
				.get(EasySQL.FIELD_RULE));
	}

	/**
	 * 根据SQL中参数位置，按顺序取得Object[]值
	 */
	public Object[] objectArray(Entity entity, String sql) {

		String[] fields = getOrderlyDatabaseColumn(sql);
		int len = fields.length;
		Object[] params = new Object[len];
		for (int i = 0; i < len; i++) {
			String column = (String) fields[i];
			if (EasySQL.FIELD_RULE_SEGMENTATION.equals(getNameRule())) {
				column = convertedIntoHump(column);
			}
			String methodname = getMethodName("get", column);
			params[i] = getFieldValues(methodname, entity);
		}
		return params;
	}

	/**
	 * 根据Entity取得取得Object[]值
	 */
	public Object[] objectArray(Entity entity) {

		String[] fields = eHandler.getEntityFields();
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
			params[count] = getFieldValues(methodname, entity);
			count++;
		}

		return params;
	}

	public String getSelectSQL(String sql) {

		StringBuffer sb = new StringBuffer();
		int index = sql.indexOf("*");
		if (sql.indexOf("*") >= 0) {
			String[] fields = setConditionsOfFilter(eHandler.getEntityFields());
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

		return setConditionsOfFilter(sql);
	}

	public String getDeleteSQL() {

		String idkey = (String) getFilter().get(EntityFilter.ID);

		String tablename = formatSingeField(eHandler.getClazz().getSimpleName());

		StringBuffer sb = new StringBuffer();
		sb.append("DELETE FROM ");
		sb.append(tablename);
		sb.append(" WHERE ");
		sb.append(idkey);
		sb.append("=?");

		return sb.toString();
	}

	public String getDeleteSQL(String where) {

		String tablename = formatSingeField(eHandler.getClazz().getSimpleName());

		StringBuffer sb = new StringBuffer();
		sb.append("DELETE FROM ");
		sb.append(tablename);
		sb.append(" WHERE ");
		sb.append(setConditionsOfFilter(where));

		return sb.toString();
	}

	public String getUpdateSQL(String[] filed) {

		String[] fields = setConditionsOfFilter(filed);

		String idkey = (String) getFilter().get(EntityFilter.ID);

		return generateUpdateSQL(fields, idkey, idkey + "=?").toString();
	}

	public String getUpdateSQL(String[] filed, String where) {

		String[] fields = setConditionsOfFilter(filed);

		String idkey = (String) getFilter().get(EntityFilter.ID);

		return generateUpdateSQL(fields, idkey, where).toString();
	}

	public String getUpdateSQL() {

		String[] fields = setConditionsOfFilter(eHandler.getEntityFields());

		String idkey = (String) getFilter().get(EntityFilter.ID);

		return generateUpdateSQL(fields, idkey, idkey + "=?").toString();
	}

	public String getUpdateSQL(String where) {

		String newWhere = where.toUpperCase();
		if (newWhere.indexOf("UPDATE") >= 0) {
			return standardFormattingOfSQL(setConditionsOfFilter(where));
		}

		String[] fields = setConditionsOfFilter(eHandler.getEntityFields());
		String idkey = (String) getFilter().get(EntityFilter.ID);

		return standardFormattingOfSQL(generateUpdateSQL(fields, idkey,
				setConditionsOfFilter(where)).toString());
	}

	public String getInsertSQL() {

		String[] fields = setConditionsOfFilter(eHandler.getEntityFields());

		String database = (String) Mapping.getInstance().get(EasySQL.DATABASE);

		// 主鍵生成機制
		String geneValue = (String) getFilter().get(
				EntityFilter.GENERATOR_SEQ_VALUE);
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

	public String formatSingeField(String elements) {

		String[] replaceValue = (String[]) getFilter()
				.get(EntityFilter.REPLACE);

		return convertedElement(elements, replaceValue, getNameRule());
	}

	public String setConditionsOfFilter(String sql) {
		String[] fields = eHandler.getEntityFields();
		for (String s : fields) {
			int matchIndex = sql.indexOf(s);
			if (matchIndex >= 0) {
				String formatAfter = formatSingeField(s);
				sql = sql.substring(0, matchIndex) + formatAfter
						+ sql.substring(matchIndex + s.length());
			}
		}

		return sql;
	}

	/**
	 * 设置过滤条件
	 */
	public String[] setConditionsOfFilter(String[] fields) {

		String[] replaceValue = (String[]) getFilter()
				.get(EntityFilter.REPLACE);

		if (EasySQL.FIELD_RULE_HUMP.equals(getNameRule())) {
			return fields;
		}

		if (EasySQL.FIELD_RULE_SEGMENTATION.equals(getNameRule())) {
			for (int i = 0; i < fields.length; i++) {
				fields[i] = convertedElement(fields[i], replaceValue,
						getNameRule());
			}
		}

		return fields;
	}

	public String reverseElement(String ele, String[] replaceValue,
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

	private String convertedElement(String ele, String[] replaceValue,
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

	/**
	 * 取得方法名，如：getUserName
	 */
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

	/**
	 * 将文本转换成驼峰命名规则。</br></br>
	 * 
	 * 如：user_name => userName
	 */
	public String convertedIntoHump(String text) {

		StringBuffer humpname = new StringBuffer();
		String[] textArray = text.split("_");
		int len = textArray.length;
		if (len == 1) {
			return text;
		} else {
			for (int i = 0; i < len; i++) {
				if (i == 0) {
					humpname.append(textArray[i]);
				} else {
					String oldvalue = textArray[i];
					String firstLetter = oldvalue.substring(0, 1).toUpperCase();
					humpname.append(firstLetter + oldvalue.substring(1));
				}
			}
		}

		return humpname.toString();
	}

	/**
	 * 将文本转换成分段命名规则。</br></br>
	 * 
	 * 如：userName => user_name
	 */
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

	/**
	 * 根据SQL中参数(key=?)位置，取得相应的列字段（有顺序的列字段）
	 */
	public String[] getOrderlyDatabaseColumn(String sql) {

		String[] databaseColumnArray = new String[countParameter(sql)];
		int count = 0;
		while (true) {
			int index = sql.indexOf("?");
			if (index >= 0) {
				String tableField = sql.substring(0, index - 1);
				if (isContainsTheKeyword(tableField)) {
					tableField = deleteKeywordsOfSQL(tableField);
				} else {
					tableField = tableField.substring(tableField
							.lastIndexOf(" ") + 1);
				}
				databaseColumnArray[count] = tableField;
				count++;

				sql = sql.substring(index + 1);
			} else {
				break;
			}
		}

		return databaseColumnArray;
	}

	/**
	 * 刪除SQL中的關鍵字</br></br>
	 * 
	 * 该方法未完善，待以后完善
	 */
	private String deleteKeywordsOfSQL(String sql) {

		String temp = sql;

		while (true) {
			sql = sql.toUpperCase();
			int index = sql.lastIndexOf("IN");
			if (index >= 0) {
				sql = sql.substring(0, index - 1);
			}
			String columnField = sql.substring(sql.lastIndexOf(" ") + 1);
			if (!isKeywordsOfSQL(columnField)) {
				int columnFieldIndex = temp.toUpperCase().lastIndexOf(
						columnField);
				return temp.substring(columnFieldIndex, columnFieldIndex
						+ columnField.length());
			}
		}
	}

	/**
	 * 是否为SQL关键字
	 */
	private boolean isKeywordsOfSQL(String text) {
		for (String key : keywords) {
			if (text.equals(key)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * SQL中是否包含有关键字
	 */
	private boolean isContainsTheKeyword(String text) {
		text = text.toUpperCase();
		for (String key : keywords) {
			int index = text.lastIndexOf(key);
			if (index >= 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 取得SQL中参数个数
	 */
	private int countParameter(String sql) {
		StringTokenizer stk = new StringTokenizer(sql, "?");
		return stk.countTokens();
	}
}

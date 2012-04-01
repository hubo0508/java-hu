package org.easysql.handlers;

import java.util.StringTokenizer;

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
		super
				.setTableName(singleTextFilter(eHandler.getClazz()
						.getSimpleName()));
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
			if (i == 1 && primaryKeyMechanism(database)) {
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

	public String getSelectSQLById() {

		StringBuffer sb = new StringBuffer();
		sb.append(getSelectSQL());
		sb.append(" WHERE ");
		sb.append(getFilter().get(EntityFilter.ID));
		sb.append("=?");

		return sb.toString();
	}

	public static String getPagingSQL(String sqlText) {

		StringBuffer sb = new StringBuffer(100);
		String database = (String) Mapping.getInstance().get(EasySQL.DATABASE);
		if (EasySQL.ORACLE.equals(database)) {
			sb.append("select * from ( select row_.*, rownum rownum_ from ( ");
			sb.append(sqlText);
			sb.append(" ) row_ where rownum<=?) where rownum_>?");
			return sb.toString();
		}

		if (EasySQL.SQLSERVICE.equals(database)) {

		}

		if (EasySQL.MYSQL.equals(database)) {
			return sqlText + " LIMIT ?,?";
		}

		return sqlText;
	}

	public String getSelectSQL() {

		StringBuffer sb = new StringBuffer();
		sb.append("SELECT ");
		String[] fields = fieldsFilterAfter(eHandler.getEntityFields());
		sb.append(fieldsIntoString(fields));
		sb.append(" FROM ");
		sb.append(getTableName());

		return super.standardFormattingOfSQL(sqlTextFilter(sb.toString()));
	}

	public String getSelectSQL(String sql) {

		StringBuffer sb = new StringBuffer();
		int index = sql.indexOf("*");
		if (sql.indexOf("*") >= 0) {
			String[] fields = fieldsFilterAfter(eHandler.getEntityFields());
			sb.append(fieldsIntoString(fields));
			sql = sql.substring(0, index) + sb.toString()
					+ sql.substring(index + 1);
		} else {
			index = sql.toUpperCase().indexOf("FROM");
			if (index >= 0) {
				String[] fields = fieldsFilterAfter(eHandler.getEntityFields());
				sb.append("SELECT ");
				sb.append(fieldsIntoString(fields));
				sb.append(" ");
				sb.append(sql);
				sql = sb.toString();
			} else {
				String[] fields = fieldsFilterAfter(eHandler.getEntityFields());
				sb.append("SELECT ");
				sb.append(fieldsIntoString(fields));
				sb.append(" FROM ");
				sb.append(this.getTableName());
				sb.append(" WHERE ");
				sb.append(sql);
				sql = sb.toString();
			}
		}

		return sqlTextFilter(sql);
	}

	public String getDeleteSQL() {

		String idkey = (String) getFilter().get(EntityFilter.ID);

		StringBuffer sb = new StringBuffer();
		sb.append("DELETE FROM ");
		sb.append(getTableName());
		sb.append(" WHERE ");
		sb.append(idkey);
		sb.append("=?");

		return sb.toString();
	}

	public String getDeleteSQL(String where) {

		String tablename = singleTextFilter(eHandler.getClazz().getSimpleName());

		StringBuffer sb = new StringBuffer();
		sb.append("DELETE FROM ");
		sb.append(tablename);
		sb.append(" WHERE ");
		sb.append(sqlTextFilter(where));

		return sb.toString();
	}

	public String getUpdateSQL(String[] filed) {

		String[] fields = fieldsFilterAfter(filed);
		String idkey = (String) getFilter().get(EntityFilter.ID);

		return generateUpdateSQL(fields, idkey, idkey + "=?").toString();
	}

	public String getUpdateSQL(String[] filed, String where) {

		String[] fields = fieldsFilterAfter(filed);
		String idkey = (String) getFilter().get(EntityFilter.ID);

		return generateUpdateSQL(fields, idkey, where).toString();
	}

	public String getUpdateSQL() {

		String[] fields = fieldsFilterAfter(eHandler.getEntityFields());

		String idkey = (String) getFilter().get(EntityFilter.ID);

		return generateUpdateSQL(fields, idkey, idkey + "=?").toString();
	}

	public String getUpdateSQL(String where) {

		String newWhere = where.toUpperCase();
		if (newWhere.indexOf("UPDATE") >= 0) {
			return standardFormattingOfSQL(sqlTextFilter(where));
		}

		String[] fields = fieldsFilterAfter(eHandler.getEntityFields());
		String idkey = (String) getFilter().get(EntityFilter.ID);

		String sql = generateUpdateSQL(fields, idkey, sqlTextFilter(where))
				.toString();

		return standardFormattingOfSQL(sql);
	}

	public String getInsertSQL() {

		String[] fields = fieldsFilterAfter(eHandler.getEntityFields());
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
			if (i == 1 && primaryKeyMechanism(database)) {
				if ("mysql".equals(database)) {
					continue;
				} else if ("sqlserver".equals(database)) {
					continue;
				}
			}
			sb.append(fields[i]);
			if (i < len - 1) {
				sb.append(", ");
			}
		}
	}

	private void setInsertValue(String database, String geneValue,
			String[] fields, StringBuffer sb) {

		int len = fields.length;
		for (int i = 1; i < len; i++) {
			if (i == 1 && primaryKeyMechanism(database)) {
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

	/**
	 * 取得主键生成机制</br></br>
	 * 
	 * 1、根据EasySQL.xml中的配置信息，判断主键的生成规则，是自动生成还是手动维护主键。</br>
	 * 
	 * @param databasename
	 *            数据库类型(mysql、sqlserver、oracle)
	 * 
	 * @return true:主键生成机制为自动 || false:主键生成机制为手动维护
	 * 
	 */
	private boolean primaryKeyMechanism(String databasename) {

		Mapping mapping = Mapping.getInstance();
		String generator = (String) mapping.get(EasySQL.GENERATOR);

		if (EasySQL.ORACLE.equals(databasename)) {
			if ("native".equals(generator) || "sequence".equals(generator)) {
				return true;
			}
		}

		if (EasySQL.SQLSERVICE.equals(databasename)) {

		}

		if (EasySQL.MYSQL.equals(databasename)) {
			if ("native".equals(generator) || "identity".equals(generator)) {
				return true;
			}
		}

		return false;
	}

	/** ******************************************************************************************** */

	/**
	 * 生成Update SQL
	 */
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

	/**
	 * 设置文本过滤</br></br>
	 */
	public String sqlTextFilter(String text) {
		String[] fields = eHandler.getEntityFields();
		for (String s : fields) {
			int matchIndex = text.indexOf(s);
			if (matchIndex >= 0) {
				String formatAfter = singleTextFilter(s);
				text = text.substring(0, matchIndex) + formatAfter
						+ text.substring(matchIndex + s.length());
			}
		}

		return text;
	}

	/**
	 * 设置过滤条件
	 */
	public String[] fieldsFilterAfter(String[] fields) {

		if (EasySQL.FIELD_RULE_HUMP.equals(getNameRule())) {
			return fields;
		}

		if (EasySQL.FIELD_RULE_SEGMENTATION.equals(getNameRule())) {
			for (int i = 0; i < fields.length; i++) {
				fields[i] = singleTextFilter(fields[i]);
			}
		}

		return fields;
	}

	/**
	 * 对单个字段设置过虑条件</br></br>
	 * 
	 * @param text
	 *            单个字段
	 */
	private String singleTextFilter(String text) {

		String[] replaceValue = (String[]) getFilter()
				.get(EntityFilter.REPLACE);

		if (EasySQL.FIELD_RULE_HUMP.equals(getNameRule())) {
			return text;
		}

		if (replaceValue != null) {
			text = replaceTextValues(replaceValue, text);
		}

		if (EasySQL.FIELD_RULE_SEGMENTATION.equals(getNameRule())) {
			return convertedIntoSegmentation(text);
		}

		return text;
	}

	/**
	 * 取得方法名，如：getUserName
	 */
	private String getMethodName(String methodPrefix, String fieldName) {
		String firstLetter = fieldName.substring(0, 1).toUpperCase();
		return methodPrefix + firstLetter + fieldName.substring(1);
	}

	/**
	 * 替换文本值
	 */
	private String replaceTextValues(String[] replaceValue, String text) {

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
	 * 根据SQL中参数(key=?)位置，取得相应的列字段（有顺序的列字段）</br></br>
	 * 
	 * 该方法完整性有待后期深入。</br>
	 * 
	 * 如：UPDATE user SET user_name=?,
	 * userAddress=?;对于该SQL取得Column为：["user_name","userAddress"]
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

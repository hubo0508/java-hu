package org.easysql.handlers;

import java.lang.reflect.Method;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysql.core.Entity;

/**
 * SQL处理抽像类
 */
public class AbstractSQLHandlers {

	protected Log log = LogFactory.getLog(AbstractSQLHandlers.class);

	/**
	 * POJO过滤条件
	 */
	private EntityFilter filter;

	/**
	 * POJO与数据库字段名字规则(hump segmentation)
	 */
	private String nameRule;

	/**
	 * POJO所对应的表名
	 */
	private String tableName;

	/**
	 * SQL关键字
	 */
	protected final static String[] keywords = new String[] { "IN", "WHERE",
			"SELECT", "FROM", "UPDATE", "SET" };

	public static void main(String[] args) {
		String sql = "UPDATE user SET id=?, username=?, password=? WHERE   username=   ?, password=         ?";

		AbstractSQLHandlers sqlHandlers = new AbstractSQLHandlers();
		System.out.println(sqlHandlers.standardFormattingOfSQL(sql));

	}

	/**
	 * 拆分SQL
	 */
	private String[] splitSQL(String sql) {
		StringTokenizer stk = new StringTokenizer(sql, " ");
		int len = stk.countTokens();
		String[] sqlArray = new String[len];
		for (int i = 0; i < len; i++) {
			sqlArray[i] = stk.nextToken();
		}
		return sqlArray;
	}

	/**
	 * 对SQL进行标准格式化。</br></br>
	 */
	public String standardFormattingOfSQL(String sql) {

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
			if (i < len - 1) {
				sb.append(" ");
			}
		}

		return sb.toString();
	}

	/**
	 * 根据方法名取得值
	 * 
	 * @param methodname
	 *            方法名
	 * @param entity
	 *            POJO对象
	 */
	@SuppressWarnings("unchecked")
	public Object getFieldValues(String methodname, Entity entity) {
		try {
			Class clazz = entity.getClass();
			Method method = clazz.getMethod(methodname, new Class[] {});
			Object value = method.invoke(entity, new Object[] {});

			return value;
		} catch (Throwable e) {
			log.info(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * 将文本转换成驼峰命名规则。</br></br>
	 * 
	 * 如：user_name => userName
	 */
	protected String convertedIntoHump(String text) {

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
	protected String convertedIntoSegmentation(String text) {

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
	 * String Array转换成SQL字符串
	 */
	protected String fieldsIntoString(String[] fields) {

		StringBuffer sb = new StringBuffer();
		int len = fields.length;
		for (int i = 1; i < fields.length; i++) {
			sb.append(fields[i]);
			if (i < len - 1) {
				sb.append(",");
			}
		}

		return sb.toString();
	}

	/**
	 * POJO过滤条件
	 */
	public EntityFilter getFilter() {
		return filter;
	}

	public void setFilter(EntityFilter filter) {
		this.filter = filter;
	}

	/**
	 * POJO与数据库字段名字规则(hump segmentation)
	 */
	public String getNameRule() {
		return nameRule;
	}

	public void setNameRule(String nameRule) {
		this.nameRule = nameRule;
	}

	/**
	 * POJO所对应的表名
	 */
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

}

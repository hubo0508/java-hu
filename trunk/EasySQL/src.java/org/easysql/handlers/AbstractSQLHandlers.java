package org.easysql.handlers;

import java.lang.reflect.Method;
import java.util.StringTokenizer;

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

}

package org.db.jdbcutils;

import java.util.regex.Pattern;

/**
 * SQL统计处理
 * 
 * @author: 魔力猫咪<code>(http://wlmouse.iteye.com/category/60230)</code>
 * @author: hubo.0508@gmail.com
 */
public class SqlCompatible {
	
	

	/**
	 * 数量统计正则表达式
	 */
	private String countRegex = "$1 count\\(*\\) $3";

	/**
	 * 不同结果正则表达式
	 */
	private String distinctRegex = "$1 distinct $2 $3";

	/**
	 * 不同结果统计正则表达式
	 */
	private String countDistinctRegex = "$1 count\\(distinct $2\\) $3";

	/**
	 * 求最大值正则表达式
	 */
	private String maxRegex = "$1 max\\($2\\) $3";

	/**
	 * 求最小值正则表达式
	 */
	private String minRegex = "$1 min\\($2\\) $3";

	/**
	 * 求和正则表达式
	 */
	private String sumRegex = "$1 sum\\($2\\) $3";

	/**
	 * 求平均数正则表达式
	 */
	private String avgRegex = "$1 avg\\($2\\) $3";

	/**
	 * 语句结果匹配正则表达式
	 */
	private Pattern regex = Pattern.compile("(SELECT)(.*)(FROM.*)",
			Pattern.CASE_INSENSITIVE);
	
	public String getPagingSQL(String sqlText, String database) {

		StringBuffer sb = new StringBuffer(100);
		if (JdbcUtils.ORACLE.equals(database)) {
			sb.append("SELECT * FROM (SELECT row_.*, ROWNUM rownum_ FROM ( ");
			sb.append(sqlText);
			sb.append(" ) row_ WHERE ROWNUM<=?) WHERE ROWNUM>=?");
			return sb.toString();
		}

		if (JdbcUtils.SQLSERVER.equals(database)) {
			
		}

		if (JdbcUtils.MYSQL.equals(database)) {
			return sqlText + " LIMIT ?,?";
		}

		return sqlText;
	}

	/**
	 * 数量统计
	 * 
	 * @return 语句
	 */
	public String count(String statement) {
		return regex.matcher(statement).replaceAll(countRegex);
	}

	/**
	 * 不同结果
	 * 
	 * @return 语句
	 */
	public String distinct(String statement) {
		return regex.matcher(statement).replaceAll(distinctRegex);
	}

	/**
	 * 不同结果统计
	 * 
	 * @return 语句
	 */
	public String countDistinct(String statement) {
		return regex.matcher(statement).replaceAll(countDistinctRegex);
	}

	/**
	 * 求最大值
	 * 
	 * @return 语句
	 */
	public String max(String statement) {
		return regex.matcher(statement).replaceAll(maxRegex);
	}

	/**
	 * 求最小值
	 * 
	 * @return 语句
	 */
	public String min(String statement) {
		return regex.matcher(statement).replaceAll(minRegex);
	}

	/**
	 * 求和
	 * 
	 * @return 语句
	 */
	public String sum(String statement) {
		return regex.matcher(statement).replaceAll(sumRegex);
	}

	/**
	 * 求平均数
	 * 
	 * @return 语句
	 */
	public String avg(String statement) {
		return regex.matcher(statement).replaceAll(avgRegex);
	}
}

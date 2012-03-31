/*
 * @(#)Storage.java	0.3 01/05/2009
 */
package org.easysql.core;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 结果对象
 * @author Miao
 * @version 0.4
 * @since 0.3
 */
public class SQLResult {

    /**
     * 语句
     */
    private String statement;
    /**
     * 排序列表
     */
    private List<String> orders;
    /**
     * 语句结果匹配正则表达式
     */
    private Pattern regex = Pattern.compile("(SELECT)(.*)(FROM.*)", Pattern.CASE_INSENSITIVE);
    /**
     * 逗号结尾匹配正则表达式
     */
    private Pattern commaRegex = Pattern.compile(",\\s*$");
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
     * 构造方法
     * @param statement 语句
     * @throws NullPointerException 当参数statement为null时，抛出NullPointerException异常
     * @since 0.3
     */
    public SQLResult(String statement) {
        if (statement != null) {
            this.statement = statement;
            orders = new LinkedList<String>();
        } else {
            throw new NullPointerException("Param statement is null");
        }
    }

    /**
     * 构造方法
     * @param statement 语句
     * @param orders 排序列表，如果该参数为null则对象自动生成一个空List。
     * @throws NullPointerException 当参数statement为null时，抛出NullPointerException异常
     * @since 0.4
     */
    public SQLResult(String statement, List<String> orders) {
        if (statement != null) {
            this.statement = statement;
        } else {
            throw new NullPointerException("Param statement is null");
        }
        if (orders != null) {
            this.orders = orders;
        } else {
            this.orders = new LinkedList<String>();
        }
    }

    /**
     * 增加排序
     * @param order 排序
     * @since 0.4
     */
    public void addOrder(String order) {
        orders.add(order);
    }

    /**
     * 数量统计
     * @return 语句
     * @since 0.3
     */
    public String count() {
    	String count=regex.matcher(statement).replaceAll(countRegex);
        return count;
    }

    /**
     * 不同结果
     * @return 语句
     * @since 0.3
     */
    public String distinct() {
        return regex.matcher(statement).replaceAll(distinctRegex);
    }

    /**
     * 不同结果统计
     * @return 语句
     * @since 0.3
     */
    public String countDistinct() {
        return regex.matcher(statement).replaceAll(countDistinctRegex);
    }

    /**
     * 求最大值
     * @return 语句
     * @since 0.3
     */
    public String max() {
        return regex.matcher(statement).replaceAll(maxRegex);
    }

    /**
     * 求最小值
     * @return 语句
     * @since 0.3
     */
    public String min() {
        return regex.matcher(statement).replaceAll(minRegex);
    }

    /**
     * 求和
     * @return 语句
     * @since 0.3
     */
    public String sum() {
        return regex.matcher(statement).replaceAll(sumRegex);
    }

    /**
     * 求平均数
     * @return 语句
     * @since 0.3
     */
    public String avg() {
        return regex.matcher(statement).replaceAll(avgRegex);
    }

    @Override
    public String toString() {
        if (orders.size() == 0) {
            return statement;
        } else {
            StringBuilder sb = new StringBuilder(200);
            sb.append(statement + " ORDER BY ");
            for (String order : orders) {
                sb.append(order + ", ");
            }
            return commaRegex.matcher(sb).replaceAll("");
        }
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (statement != null ? statement.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SQLResult)) {
            return false;
        }
        SQLResult other = (SQLResult) obj;
        if ((this.statement == null && other.statement != null) || (this.statement != null && !this.statement.equals(other.statement))) {
            return false;
        }
        return true;
    }

    /**
     * 获得排序列表
     * @return 排序列表
     * @since 0.4
     */
    public List<String> getOrders() {
        return orders;
    }
}

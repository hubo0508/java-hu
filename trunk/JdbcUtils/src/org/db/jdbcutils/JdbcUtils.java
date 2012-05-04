package org.db.jdbcutils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import test.ConfigDevice;

/**
 * 数据库底层工具类
 * 
 * <li>封装了常用的增、删、查、改；</li>
 * <li>该版本适用于jdk1.4+以上，jdk1.4以下未测试过；</li>
 * <li>部分代码采用或参照apache dbutils项目，项目网址为<code>http://commons.apache.org/dbutils/</code>；</li>
 * <li>操作底层可为纯SQL，适用习惯于写SQL的开发人员；</li>
 * <li>操作底层可为对象，适用习惯用Hibernate操作的开发人员，于Hiberante类似80%；</li>
 * <li>操作底层为对象时，可以指定SQL字段的命名方式，如userName或user_name；</li>
 * <li>可为不同数据之间的简单兼容进行处理；Insert的主键维护机制(sqlserver未实现)、分页构造机制(分页暂时未实现)；</li>
 * <li>自动生成SQL语句时，增加过滤条件；</li>
 * <li>目前版本对sqlserver的API不支持；</li>
 * 
 * @User: hubo.0508@gmail.com
 * @Date Apr 20, 2012
 * @Time 10:43:09 AM
 * 
 * <table>
 * <tr>
 * <td>版本号 </td>
 * <td>修改时间 </td>
 * <td>说明</td>
 * </tr>
 * <tr>
 * <td>0.1</td>
 * <td>2012-04-20</td>
 * <td>创建</td>
 * </tr>
 * <tr>
 * <td>0.2</td>
 * <td>2012-05-02</td>
 * <td>返回结果重构</td>
 * </tr>
 * <tr>
 * <td>0.2.1</td>
 * <td>2012-05-03</td>
 * <td>注释的添加，重构</td>
 * </tr>
 * <tr>
 * <td>0.2.2</td>
 * <td>2012-05-04</td>
 * <td>增加过滤条件；增加SQL统计处理</td>
 * </tr>
 * </table>
 * 
 */
public class JdbcUtils {

	/**
	 * Java Bean字段命名与数据库字段命名的方式为：驼峰命名法。如：userName;
	 */
	public final static String HUMP = "hump";

	/**
	 * Java Bean字段命名与数据库字段命名的方式为：分段名法。如：user_name;
	 */
	public final static String SEGMENTATION = "segmentation";

	/**
	 * 数据库类型：oracle
	 */
	public final static String ORACLE = "oracle";

	/**
	 * 数据库类型：mysql
	 */
	public final static String MYSQL = "mysql";

	/**
	 * 数据库类型：sqlserver
	 */
	public final static String SQLSERVER = "sqlserver";

	/**
	 * MySQL数据库ID键值是否递增
	 */
	public final static String MYSQL_SEQ = "increase by degrees";

	/**
	 * 数据库字段与Java Bean字段的命名规则，默认为常量HUMP
	 * 
	 * @see JdbcUtils#HUMP
	 * @see JdbcUtils#SEGMENTATION
	 */
	private String rule = HUMP;

	/**
	 * 数据映射模版，与数据库结果集的数据表相对应、自动构造SQL时对应。
	 * 
	 * <p>
	 * 该数据映射模版作用于Java
	 * Bean与SQL之间的转换、数据库结果集的数据表与该数据映射模版之间值的设置。SQL的查询字段或更新字段、插入字段该dataMappingClass(Java
	 * Bean)映射模版中取得。 可在dataMappingClass(Java Bean)中增加过滤方法，该过滤方法返回<code>Map</code>类型数据。
	 * 该数据映射模版类型可为：
	 * 
	 * <li>JavaBean.class</li>
	 * <li>Map.class/HashMap.class/LinkedHashMap.class</li>
	 * <li>List.class/ListArray.class</li>
	 * <li>Integer.class 或其它基本数据类型</li>
	 * 
	 * </br></br>当dataMappingClass类型是<code>Map.class/HashMap.class/LinkedHashMap.class/List.class/ListArray.class/</code>基本数据类型时。
	 * 此时与数据库结果集的数据表相对应、自动构造SQL时对应都不成立，必须手动写SQL语句。为了可能自动构造SQL及数据映射，可重新设置<code>JdbcUtils.setSqlMappingClass(Class)</code>。
	 * </p>
	 * 
	 * @see JdbcUtils#setSqlMappingClass(Class)
	 * @see JdbcUtils#getSqlFilter()
	 */
	private Class dataMappingClass;

//	/**
//	 * SQL数据映射Class。</br>
//	 * 
//	 * 在自动构造sql时，SDx默认从<code>JdbcUtils#dataMappingClass</code>中取得相应字段构造sql。
//	 * 当<code>JdbcUtils#dataMappingClass</code>类型为List或Map、Java基本数据类型时，在自动构造sql时，无法取得相应字段，
//	 * 可通过设值<code>JdbcUtils#sqlMappingClass</code>使用构造sql语句生效。
//	 */
	private Class sqlMappingClass;

	/**
	 * 自动构造SQL的过滤条件
	 */
	private Map sqlFilter;

	/**
	 * Domain主键字段，默认为id
	 */
	private String primaryKey = "id";

	private volatile boolean pmdKnownBroken = false;

	private final static String[] TOTYPE = { "bean", "database" };

	/*
	 * Bean转换成SQL语句处理(私有)
	 */
	private final SqlProcessor sqlPro = new JdbcUtils.SqlProcessor();

	/*
	 * POJO处理(私有)
	 */
	private final BeanProcessor beanPro = new JdbcUtils.BeanProcessor();

	/*
	 * 结果集处理(私有)
	 */
	private final ResultProcessor rsPro = new JdbcUtils.ResultProcessor();

	/*
	 * SQL转换成统计语句处理(私有)
	 * 
	 * @User: 魔力猫咪(http://wlmouse.iteye.com/category/60230)
	 */
	private final SqlStatisticsProcessor statPro = new JdbcUtils.SqlStatisticsProcessor();

	/**
	 * 构造函数
	 * 
	 * @param dataMappingClass
	 *            返回结果集映射格式。
	 *            <li>{Domain}.class</li>
	 *            <li>Map.class/HashMap.class/LinkedHashMap.class</li>
	 *            <li>List.class/ListArray.class</li>
	 *            <li>Integer.class 或其它基本数据类型</li>
	 */
	public JdbcUtils(Class dataMappingClass) {
		this.setDataMappingClass(dataMappingClass);
	}

	/**
	 * 构造函数
	 * 
	 * @param dataMappingClass
	 *            返回结果集映射格式。
	 *            <li>{Domain}.class</li>
	 *            <li>Map.class/HashMap.class/LinkedHashMap.class</li>
	 *            <li>List.class/ListArray.class</li>
	 *            <li>Integer.class 或其它基本数据类型</li>
	 * @param rule
	 *            数据库字段命名规则，默认为常量HUMP。
	 * 
	 * @see JdbcUtils#HUMP
	 * @see JdbcUtils#SEGMENTATION
	 */
	public JdbcUtils(Class dataMappingClass, String rule) {
		this.setDataMappingClass(dataMappingClass);
		this.rule = rule;
	}

	/**
	 * 自动构造SQL语言，无条件查询数据。根据构造函数参数clazz构造出查询SQL。
	 * 
	 * @param con
	 *            数据库连接对象
	 * 
	 * @return ArrayList
	 * 
	 * @throws SQLException
	 */
	public ArrayList queryResultToArrayList(Connection con) throws SQLException {
		return (ArrayList) query(con, sqlPro.makeSelectSql(), null,
				new ArrayList());
	}

	/**
	 * 自动构造SQL语言，查询数据。根据构造函数参数clazz构造出查询SQL。
	 * 
	 * @param con
	 *            数据库连接对象
	 * @param sqlOrWhereIf
	 *            SQL查询语句(<cdoe>select * from dual</code>)或查询条件(<code>where
	 *            id=1</code>)
	 * 
	 * @return ArrayList
	 * 
	 * @throws SQLException
	 */
	public ArrayList queryResultToArrayList(Connection con, String sqlOrWhereIf)
			throws SQLException {
		return queryResultToArrayList(con, sqlOrWhereIf, null);
	}

	/**
	 * 自动构造SQL语言，查询数据。根据构造函数参数clazz构造出查询SQL。
	 * 
	 * @param con
	 *            数据库连接对象
	 * @param sqlOrWhereIf
	 *            SQL查询语句(<cdoe>select * from dual</code>)或查询条件(<code>where
	 *            id=1</code>)
	 * @param params
	 *            查询参数
	 * 
	 * @return ArrayList
	 * 
	 * @throws SQLException
	 */
	public ArrayList queryResultToArrayList(Connection con,
			String sqlOrWhereIf, Object[] params) throws SQLException {
		if (!isSelect(sqlOrWhereIf)) {
			sqlOrWhereIf = sqlPro.makeSelectSql(sqlOrWhereIf);
		}
		return (ArrayList) query(con, sqlOrWhereIf, params, new ArrayList());
	}

	/**
	 * 自动构造SQL语言，查询数据。根据构造函数参数clazz构造出查询SQL。
	 * 
	 * @param con
	 *            数据库连接对象
	 * 
	 * @return HashMap
	 * 
	 * @throws SQLException
	 */
	public HashMap queryResultToHashMap(Connection con) throws SQLException {
		return (HashMap) query(con, sqlPro.makeSelectSql(), null, new HashMap());
	}

	/**
	 * 自动构造SQL语言，查询数据。根据构造函数参数clazz构造出查询SQL。
	 * 
	 * @param con
	 *            数据库连接对象
	 * @param sqlOrWhereIf
	 *            SQL查询语句(<cdoe>select * from dual</code>)或查询条件(<code>where
	 *            id=1</code>)
	 * 
	 * @return HashMap
	 * 
	 * @throws SQLException
	 */
	public HashMap queryResultToHashMap(Connection con, String sqlOrWhereIf)
			throws SQLException {
		return queryResultToHashMap(con, sqlOrWhereIf, null);
	}

	/**
	 * 自动构造SQL语言，查询数据。根据构造函数参数clazz构造出查询SQL。
	 * 
	 * @param con
	 *            数据库连接对象
	 * @param sqlOrWhereIf
	 *            SQL查询语句(<cdoe>select * from dual</code>)或查询条件(<code>where
	 *            id=1</code>)
	 * @param params
	 *            查询参数
	 * 
	 * @return HashMap
	 * 
	 * @throws SQLException
	 */
	public HashMap queryResultToHashMap(Connection con, String sqlOrWhereIf,
			Object[] params) throws SQLException {

		if (!isSelect(sqlOrWhereIf)) {
			sqlOrWhereIf = sqlPro.makeSelectSql(sqlOrWhereIf);
		}
		return (HashMap) query(con, sqlOrWhereIf, params, new HashMap());
	}

	/**
	 * 自动构造SQL语言，查询数据。根据构造函数参数clazz构造出查询SQL。
	 * 
	 * @param con
	 *            数据库连接对象
	 * 
	 * @return LinkedHashMap
	 * 
	 * @throws SQLException
	 */
	public LinkedHashMap queryResultToLinkedHashMap(Connection con)
			throws SQLException {
		return (LinkedHashMap) query(con, sqlPro.makeSelectSql(), null,
				new LinkedHashMap());
	}

	/**
	 * 自动构造SQL语言，查询数据。根据构造函数参数clazz构造出查询SQL。
	 * 
	 * @param con
	 *            数据库连接对象
	 * @param sqlOrWhereIf
	 *            SQL查询语句(<cdoe>select * from dual</code>)或查询条件(<code>where
	 *            id=1</code>)
	 * 
	 * @return LinkedHashMap
	 * 
	 * @throws SQLException
	 */
	public LinkedHashMap queryResultToLinkedHashMap(Connection con,
			String sqlOrWhereIf) throws SQLException {
		if (!isSelect(sqlOrWhereIf)) {
			sqlOrWhereIf = sqlPro.makeSelectSql(sqlOrWhereIf);
		}
		return queryResultToLinkedHashMap(con, sqlOrWhereIf, null);
	}

	/**
	 * 自动构造SQL语言，查询数据。根据构造函数参数clazz构造出查询SQL。
	 * 
	 * @param con
	 *            数据库连接对象
	 * @param sqlOrWhereIf
	 *            SQL查询语句(<cdoe>select * from dual</code>)或查询条件(<code>where
	 *            id=1</code>)
	 * @param params
	 *            查询参数
	 * 
	 * @return LinkedHashMap
	 * 
	 * @throws SQLException
	 */
	public LinkedHashMap queryResultToLinkedHashMap(Connection con,
			String sqlOrWhereIf, Object[] params) throws SQLException {
		if (!isSelect(sqlOrWhereIf)) {
			sqlOrWhereIf = sqlPro.makeSelectSql(sqlOrWhereIf);
		}
		return (LinkedHashMap) query(con, sqlOrWhereIf, params,
				new LinkedHashMap());
	}

	/**
	 * 自动构造SQL语言，查询数据。根据构造函数参数clazz构造出查询SQL。
	 * 
	 * @param con
	 *            数据库连接对象
	 * @param sqlOrWhereIf
	 *            SQL查询语句(<cdoe>select * from dual</code>)或查询条件(<code>where
	 *            id=1</code>)
	 * @return 唯一值对象
	 * 
	 * @throws SQLException
	 */
	public Object queryResultToUnique(Connection con, String sqlOrWhereIf)
			throws SQLException {
		return queryResultToUnique(con, sqlOrWhereIf, null);
	}

	/**
	 * 自动构造SQL语言，查询数据。根据构造函数参数clazz构造出查询SQL。
	 * 
	 * @param con
	 *            数据库连接对象
	 * @param sqlOrWhereIf
	 *            SQL查询语句(<cdoe>select * from dual</code>)或查询条件(<code>where
	 *            id=1</code>)
	 * @param 查询参数
	 * 
	 * @return 唯一值对象
	 * 
	 * @throws SQLException
	 */
	public Object queryResultToUnique(Connection con, String sqlOrWhereIf,
			Object[] params) throws SQLException {
		if (!isSelect(sqlOrWhereIf)) {
			sqlOrWhereIf = sqlPro.makeSelectSql(sqlOrWhereIf);
		}
		return this.query(con, sqlOrWhereIf, params, dataMappingClass);
	}

	/**
	 * @param conn
	 *            数据库连接对象
	 * @param sql
	 *            查询SQL
	 * @param params
	 *            查询参数
	 * @param instanceCollectionOrClass
	 * 
	 * @return
	 * 
	 * @throws SQLException
	 * 
	 */
	public Object query(Connection conn, String sql, Object[] params,
			Object instanceCollectionOrClass) throws SQLException {

		if (conn == null) {
			throw new SQLException("Null connection");
		}

		if (sql == null) {
			throw new SQLException("Null SQL statement");
		}

		if (instanceCollectionOrClass == null) {
			throw new SQLException("Null result set");
		}

		if (dataMappingClass == null) {
			throw new SQLException("Null dataMappingClass");
		}

		PreparedStatement stmt = null;
		ResultSet rs = null;
		Object result = null;
		try {
			stmt = this.prepareStatement(conn, sql);
			this.fillStatement(stmt, params);
			rs = this.wrap(stmt.executeQuery());
			result = rsPro.handle(rs, instanceCollectionOrClass);
		} catch (SQLException e) {
			this.rethrow(e, sql, params);
		} finally {
			try {
				close(rs, stmt);
			} catch (Exception e) {
				close(rs, stmt);
			}
		}

		return result;
	}

	// //////////////////////UPDATE-BEGIN///////////////////////////////////////////////////////////////

	public int update(Connection conn, String sql) throws SQLException {
		return this.execute(conn, sql, null);
	}

	/**
	 * 更新数据。sql自动根据<code>JdbcUtils.dataMappingClass</code>构造。
	 * 
	 * @param conn
	 *            数据库连接对象
	 * @param sql
	 *            sql语句
	 * @param params
	 *            sql参数
	 * 
	 * @return 影响的行数
	 * 
	 * @throws SQLException
	 */
	public int update(Connection conn, String sql, Object[] params)
			throws SQLException {
		return this.execute(conn, sql, params);
	}

	/**
	 * 更新数据。sql自动根据<code>JdbcUtils.dataMappingClass</code>构造。
	 * 
	 * @param conn
	 *            数据库连接对象
	 * @param params
	 *            sql参数
	 * 
	 * @return 影响的行数
	 * 
	 * @throws SQLException
	 */
	public int update(Connection conn, Object[] params) throws SQLException {
		return execute(conn, sqlPro.makeUpdateSql(), params);
	}

	/**
	 * 更新数据。sql自动根据<code>JdbcUtils.dataMappingClass</code>构造。
	 * 
	 * @param conn
	 *            数据库连接对象
	 * @param instanceDomain
	 *            设置有值的领域对象
	 * 
	 * @return 影响的行数
	 * 
	 * @throws SQLException
	 */
	public int update(Connection conn, Object instanceDomain)
			throws SQLException {
		return update(conn, null, instanceDomain);
	}

	/**
	 * 更新数据。sql自动根据<code>JdbcUtils.dataMappingClass</code>构造，或手动构造。
	 * 
	 * @param conn
	 *            数据库连接对象
	 * @param instanceDomain
	 *            设置有值的领域对象
	 * @param sqlOrWhereIf
	 *            SQL更新语句(<cdoe>update user set id=?,username=? where id=?</code>)或查询条件(<code>where
	 *            id=?</code>)
	 * 
	 * @return 影响的行数
	 * 
	 * @throws SQLException
	 */
	public int update(Connection conn, String sqlOrWhereIf,
			Object instanceDomain) throws SQLException {
		if (!isUpate(sqlOrWhereIf)) {
			sqlOrWhereIf = sqlPro.makeUpdateSql(sqlOrWhereIf);
		}
		Object[] params = beanPro.objectArray(instanceDomain, sqlOrWhereIf);

		return execute(conn, sqlOrWhereIf, params);
	}

	// //////////////////////UPDATE-END///////////////////////////////////////////////////////////////

	// //////////////////////INSERT-BEGIN///////////////////////////////////////////////////////////////

	/**
	 * 将领域对象保存至数据库，sql根据<code>JdbcUtils.dataMappingClass</code>中的字段自动构造。
	 * 
	 * @param conn
	 *            数据库连接对象
	 * @param instanceDomain
	 *            设置有值的领域对象
	 * @param database
	 *            数据库类型(<code>JdbcUtils#ORACLE、JdbcUtils#MYSQL、JdbcUtils#SQLSERVER</code>)
	 * @param sequence
	 *            序列類型
	 *            <li><code>database=JdbcUtils#ORACLE,sequence=任意值</code>时，自动构造的sql的主键自动维护</li>
	 *            <li><code>database=JdbcUtils#ORACLE,sequence=null</code>时，自动构造的sql的主键手动维护</li>
	 *            <li><code>database=JdbcUtils#MYSQL,sequence=MYSQL_SEQ</code>时，自动构造的sql的主键自动维护</li>
	 *            <li><code>database=JdbcUtils#MYSQL,sequence=null</code>时，自动构造的sql的主键手动维护</li>
	 *            <li><code>database=JdbcUtils#SQLSERVER,sequence=null</code>时，未增加API</li>
	 * 
	 * @return 影响的行数
	 * 
	 * @throws SQLException
	 * 
	 * @see JdbcUtils#ORACLE
	 * @see JdbcUtils#MYSQL
	 * @see JdbcUtils#SQLSERVER
	 * @see JdbcUtils#MYSQL_SEQ
	 */
	public int insert(Connection conn, Object instanceDomain, String database,
			String sequence) throws SQLException {
		String sql = sqlPro.makeInsertSql(database, sequence);
		Object[] params = beanPro.objectArray(instanceDomain, sql, database,
				sequence);
		return execute(conn, sql, params);
	}

	/**
	 * 保存数据，sql手动维护，参数根据<code>instanceDomain</code>中的值自动构造
	 * 
	 * @param conn
	 *            数据库连接对象
	 * @param insertSql
	 *            新增sql语句
	 * @param instanceDomain
	 *            设置有值的领域对象
	 * @param database
	 *            数据库类型(<code>JdbcUtils#ORACLE、JdbcUtils#MYSQL、JdbcUtils#SQLSERVER</code>)
	 * @param sequence
	 *            序列類型
	 *            <li><code>database=JdbcUtils#ORACLE,sequence=任意值</code>时，自动构造的sql的主键自动维护</li>
	 *            <li><code>database=JdbcUtils#ORACLE,sequence=null</code>时，自动构造的sql的主键手动维护</li>
	 *            <li><code>database=JdbcUtils#MYSQL,sequence=MYSQL_SEQ</code>时，自动构造的sql的主键自动维护</li>
	 *            <li><code>database=JdbcUtils#MYSQL,sequence=null</code>时，自动构造的sql的主键手动维护</li>
	 *            <li><code>database=JdbcUtils#SQLSERVER,sequence=null</code>时，未增加API</li>
	 * 
	 * @return 影响的行数
	 * 
	 * @throws SQLException
	 * 
	 * @see JdbcUtils#ORACLE
	 * @see JdbcUtils#MYSQL
	 * @see JdbcUtils#SQLSERVER
	 * @see JdbcUtils#MYSQL_SEQ
	 */
	public int insert(Connection conn, String insertSql, Object instanceDomain,
			String database, String sequence) throws SQLException {

		if (getDataMappingClass() == null) {
			setDataMappingClass(instanceDomain.getClass());
		}

		Object[] params = beanPro.objectArray(instanceDomain, insertSql,
				database, sequence);
		return execute(conn, insertSql, params);
	}

	/**
	 * 保存数据，sql手动维护，参数手动维护。
	 * 
	 * @param conn
	 *            数据库连接对象
	 * @param insertSql
	 *            新增sql语句
	 * @param params
	 *            参数值
	 * @param database
	 *            数据库类型(<code>JdbcUtils#ORACLE、JdbcUtils#MYSQL、JdbcUtils#SQLSERVER</code>)
	 * @param sequence
	 *            序列類型
	 *            <li><code>database=JdbcUtils#ORACLE,sequence=任意值</code>时，自动构造的sql的主键自动维护</li>
	 *            <li><code>database=JdbcUtils#ORACLE,sequence=null</code>时，自动构造的sql的主键手动维护</li>
	 *            <li><code>database=JdbcUtils#MYSQL,sequence=MYSQL_SEQ</code>时，自动构造的sql的主键自动维护</li>
	 *            <li><code>database=JdbcUtils#MYSQL,sequence=null</code>时，自动构造的sql的主键手动维护</li>
	 *            <li><code>database=JdbcUtils#SQLSERVER,sequence=null</code>时，未增加API</li>
	 * 
	 * @return 影响的行数
	 * 
	 * @throws SQLException
	 * 
	 * @see JdbcUtils#ORACLE
	 * @see JdbcUtils#MYSQL
	 * @see JdbcUtils#SQLSERVER
	 * @see JdbcUtils#MYSQL_SEQ
	 */
	public int insert(Connection conn, String insertSql, Object[] params)
			throws SQLException {
		return execute(conn, insertSql, params);
	}

	/**
	 * 保存数据，sql根据<code>JdbcUtils.dataMappingClass</code>中的字段自动构造。
	 * 
	 * @param conn
	 *            数据库连接对象
	 * @param params
	 *            参数值
	 * @param database
	 *            数据库类型(<code>JdbcUtils#ORACLE、JdbcUtils#MYSQL、JdbcUtils#SQLSERVER</code>)
	 * @param sequence
	 *            序列類型
	 *            <li><code>database=JdbcUtils#ORACLE,sequence=任意值</code>时，自动构造的sql的主键自动维护</li>
	 *            <li><code>database=JdbcUtils#ORACLE,sequence=null</code>时，自动构造的sql的主键手动维护</li>
	 *            <li><code>database=JdbcUtils#MYSQL,sequence=MYSQL_SEQ</code>时，自动构造的sql的主键自动维护</li>
	 *            <li><code>database=JdbcUtils#MYSQL,sequence=null</code>时，自动构造的sql的主键手动维护</li>
	 *            <li><code>database=JdbcUtils#SQLSERVER,sequence=null</code>时，未增加API</li>
	 * 
	 * @return 影响的行数
	 * 
	 * @throws SQLException
	 * 
	 * @see JdbcUtils#ORACLE
	 * @see JdbcUtils#MYSQL
	 * @see JdbcUtils#SQLSERVER
	 * @see JdbcUtils#MYSQL_SEQ
	 */
	public int insert(Connection conn, Object[] params, String database,
			String sequence) throws SQLException {
		return execute(conn, sqlPro.makeInsertSql(database, sequence), params);
	}

	// //////////////////////INSERT-END///////////////////////////////////////////////////////////////

	// //////////////////////DELETE-BEGIN///////////////////////////////////////////////////////////////

	public int delete(Connection conn, Object instanceDomain)
			throws SQLException {
		return delete(conn, sqlPro.makeDeleteSql(), instanceDomain);
	}

	public int delete(Connection conn, Object[] params) throws SQLException {
		return execute(conn, sqlPro.makeDeleteSql(), params);
	}

	/**
	 * @param conn
	 *            数据库连库
	 * @param sql
	 *            sql语句
	 * @return 影响行数
	 * 
	 * @throws SQLException
	 */
	public int delete(Connection conn, String sql) throws SQLException {
		if (!isDelete(sql)) {
			throw new SQLException();
		}
		return execute(conn, sql, null);
	}

	/**
	 * @param conn
	 *            数据库连库
	 * @param params
	 *            参数
	 * @param whereIfOrSql
	 *            自定义追加SQL或SQL
	 * @return 影响行数
	 * 
	 * @throws SQLException
	 */
	public int delete(Connection conn, String sqlOrWhereIf, Object[] params)
			throws SQLException {
		if (!isDelete(sqlOrWhereIf)) {
			sqlOrWhereIf = sqlPro.makeDeleteSql(sqlOrWhereIf);
		}
		return execute(conn, sqlPro.makeDeleteSql(sqlOrWhereIf), params);
	}

	/**
	 * @param conn
	 *            数据库连库
	 * @param instanceDomain
	 *            实例化POJO对象
	 * @param sqlOrWhereIf
	 *            自定义追加SQL
	 * @return 影响行数
	 * 
	 * @throws SQLException
	 */
	public int delete(Connection conn, String sqlOrWhereIf,
			Object instanceDomain) throws SQLException {

		if (!isDelete(sqlOrWhereIf)) {
			sqlOrWhereIf = sqlPro.makeDeleteSql(sqlOrWhereIf);
		}

		Object[] params = beanPro.objectArray(instanceDomain, sqlOrWhereIf);
		return execute(conn, sqlOrWhereIf, params);
	}

	// //////////////////////DELETE-END///////////////////////////////////////////////////////////////

	public int execute(Connection conn, String sql, Object[] params)
			throws SQLException {

		if (conn == null) {
			throw new SQLException("Null connection");
		}

		if (sql == null) {
			throw new SQLException("Null SQL statement");
		}

		PreparedStatement stmt = null;
		int rows = 0;

		try {
			stmt = this.prepareStatement(conn, sql);
			this.fillStatement(stmt, params);
			rows = stmt.executeUpdate();
		} catch (SQLException e) {
			this.rethrow(e, sql, params);
		} finally {
			close(stmt);
		}

		return rows;
	}

	/**
	 * 关闭连接
	 */
	public static void close(ResultSet rs, PreparedStatement stmt) {
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		try {
			if (stmt != null) {
				stmt.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 关闭连接
	 */
	public static void close(PreparedStatement stmt) {
		try {
			if (stmt != null) {
				stmt.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 关闭连接
	 */
	public static void close(Connection conn) {
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 关闭连接
	 */
	public static void close(ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 异常统计输出
	 */
	protected void rethrow(SQLException cause, String sql, Object[] params)
			throws SQLException {

		String causeMessage = cause.getMessage();
		if (causeMessage == null) {
			causeMessage = "";
		}
		StringBuffer msg = new StringBuffer(causeMessage);

		msg.append(" Query: ");
		msg.append(sql);
		msg.append(" Parameters: ");

		if (params == null) {
			msg.append("[]");
		} else {
			msg.append(deepToString(params));
		}

		SQLException e = new SQLException(msg.toString(), cause.getSQLState(),
				cause.getErrorCode());
		e.setNextException(cause);

		throw e;
	}

	public Map columnsToBean(Class dataMappingClass, Map map)
			throws SQLException {
		this.setDataMappingClass(dataMappingClass);
		return beanPro.columnsToBean(getDataMappingClass(), map);
	}

	public List columnsToBean(Class dataMappingClass, List list)
			throws SQLException {
		this.setDataMappingClass(dataMappingClass);
		return beanPro.mapColumnsToBean(getDataMappingClass(), list);
	}

	/**
	 * 将Object[]数组输出成字符串
	 */
	private String deepToString(Object[] a) {
		if (a == null || a.length == 0)
			return "null";

		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < a.length; i++) {
			buf.append(a[i] + ",");
		}

		return buf.toString();
	}

	/**
	 * 检查数据的唯一性
	 */
	private void checkDataUnique(ResultSet rs) throws SQLException {
		if (rsPro.resultSize(rs) >= 2) {
			throw new SQLException("Result set is not unique!");
		}
	}

	protected ResultSet wrap(ResultSet rs) {
		return rs;
	}

	protected PreparedStatement prepareStatement(Connection conn, String sql)
			throws SQLException {
		return conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);
	}

	protected void fillStatement(PreparedStatement stmt, Object[] params)
			throws SQLException {

		// check the parameter count, if we can
		ParameterMetaData pmd = null;
		if (!pmdKnownBroken) {
			try {
				pmd = stmt.getParameterMetaData();
			} catch (Throwable e) {
				e.printStackTrace();
			}
			int stmtCount = pmd.getParameterCount();
			int paramsCount = params == null ? 0 : params.length;

			if (stmtCount != paramsCount) {
				throw new SQLException("Wrong number of parameters: expected "
						+ stmtCount + ", was given " + paramsCount);
			}
		}

		// nothing to do here
		if (params == null) {
			return;
		}

		for (int i = 0; i < params.length; i++) {
			if (params[i] != null) {
				stmt.setObject(i + 1, params[i]);
			} else {
				int sqlType = Types.VARCHAR;
				if (!pmdKnownBroken) {
					try {
						sqlType = pmd.getParameterType(i + 1);
					} catch (SQLException e) {
						pmdKnownBroken = true;
					}
				}
				stmt.setNull(i + 1, sqlType);
			}
		}
	}

	/** ******************************************************************************************** */
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	/** ******************************************************************************************** */

	public boolean isSelect(String sql) {
		if (isNotEmpty(sql) && sql.toUpperCase().indexOf("SELECT") == 0) {
			return true;
		}

		return false;
	}

	public boolean isUpate(String sql) {
		if (isNotEmpty(sql) && sql.toUpperCase().indexOf("UPDATE") == 0) {
			return true;
		}

		return false;
	}

	public boolean isDelete(String sql) {
		if (isNotEmpty(sql) && sql.toUpperCase().indexOf("DELETE") == 0) {
			return true;
		}

		return false;
	}

	public boolean isInsert(String sql) {
		if (isNotEmpty(sql) && sql.toUpperCase().indexOf("INSERT") == 0) {
			return true;
		}

		return false;
	}

	/**
	 * 判断字符串类型等于nul或空字符串。
	 * 
	 * @return true(等于nul或空字符串)，false(不等于nul或空字符串)
	 */
	public boolean isEmpty(String value) {
		if (value == null || value.equals("")) {
			return true;
		}
		return false;
	}

	/**
	 * 判断字符串类型不等于nul或空字符串。
	 * 
	 * @return true(不等于nul或空字符串)，false(等于nul或空字符串)
	 */
	public boolean isNotEmpty(String value) {
		if (value == null || value.equals("")) {
			return false;
		}
		return true;
	}

	/**
	 * 判断Class是否为List或为List的子集ArrayList
	 * 
	 * @return true(为List或为List的子集ArrayList)，false(不为List或为List的子集ArrayList)
	 */
	private boolean isArrayList(Class clazz) {
		String type = clazz.toString();
		if ("class java.util.ArrayList".equals(type)
				|| "interface java.util.List".equals(type)) {
			return true;
		}
		return false;
	}

	/**
	 * 判断Class是否为List
	 * 
	 * @return true(为List)，false(不为List)
	 */
	private boolean isList(Class clazz) {
		if ("interface java.util.List".equals(clazz.toString())) {
			return true;
		}
		return false;
	}

	/**
	 * 判断Class是否为Map
	 * 
	 * @return true(为Map)，false(不为Map)
	 */
	private boolean isMap(Class clazz) {
		String type = clazz.toString();
		if ("interface java.util.Map".equals(type)
				|| "class java.util.HashMap".equals(type)
				|| "class java.util.LinkedHashMap".equals(type)) {
			return true;
		}
		return false;
	}

	/**
	 * 判断Class是否为Map或为Map的子集HashMap
	 * 
	 * @return true(为Map或为Map的子集HashMap)，false(不为Map或为Map的子集HashMap)
	 */
	private boolean isHashMap(Class clazz) {
		String rshTypeStr = clazz.toString();
		if ("class java.util.HashMap".equals(rshTypeStr)
				|| "interface java.util.Map".equals(rshTypeStr)) {
			return true;
		}
		return false;
	}

	/**
	 * 判断Class是否为Map或为Map的子集LinkedHashMap
	 * 
	 * @return true(为Map或为Map的子集LinkedHashMap)，false(不为Map或为Map的子集LinkedHashMap)
	 */
	private boolean isLinkedHashMap(Class clazz) {
		String rshTypeStr = clazz.toString();
		if ("class java.util.LinkedHashMap".equals(rshTypeStr)
				|| "interface java.util.Map".equals(rshTypeStr)) {
			return true;
		}

		return false;
	}

	/**
	 * 清除Object[]数组中null元素，返回新数组
	 */
	public Object[] cleanEmpty(Object[] value) {
		int emptyCount = countCharacter(deepToString(value), "null");
		if (emptyCount == 0) {
			return value;
		}

		int count = 0;

		Object[] newObject = new Object[value.length - emptyCount];
		for (int i = 0; i < value.length; i++) {
			Object obj = value[i];
			if (obj != null) {
				newObject[count] = value[i];
				count++;
			}
		}

		return newObject;
	}

	/**
	 * 取得字符在文本中出现的次数
	 */
	public int countCharacter(String text, String character) {
		int count = 0;
		int m = text.indexOf(character);
		while (m != -1) {
			m = text.indexOf(character, m + 1);
			count++;
		}
		return count;
	}

	/**
	 * 取得数据映射模版，与数据库结果集的数据表相对应、自动构造SQL时对应。
	 * 
	 * <p>
	 * 该数据映射模版作用于Java
	 * Bean与SQL之间的转换、数据库结果集的数据表与该数据映射模版之间值的设置。SQL的查询字段或更新字段、插入字段该dataMappingClass(Java
	 * Bean)映射模版中取得。 可在dataMappingClass(Java Bean)中增加过滤方法，该过滤方法返回<code>Map</code>类型数据。
	 * 该数据映射模版类型可为：
	 * 
	 * <li>JavaBean.class</li>
	 * <li>Map.class/HashMap.class/LinkedHashMap.class</li>
	 * <li>List.class/ListArray.class</li>
	 * <li>Integer.class 或其它基本数据类型</li>
	 * 
	 * </br></br>当dataMappingClass类型是<code>Map.class/HashMap.class/LinkedHashMap.class/List.class/ListArray.class/</code>基本数据类型时。
	 * 此时与数据库结果集的数据表相对应、自动构造SQL时对应都不成立，必须手动写SQL语句。为了可能自动构造SQL及数据映射，可重新设置<code>JdbcUtils.setSqlMappingClass(Class)</code>。
	 * </p>
	 * 
	 * @see JdbcUtils#setSqlMappingClass(Class)
	 * @see JdbcUtils#getSqlFilter()
	 */
	public Class getDataMappingClass() {
		return dataMappingClass;
	}

	/**
	 * 设值数据映射模版，与数据库结果集的数据表相对应、自动构造SQL时对应。
	 * 
	 * <p>
	 * 该数据映射模版作用于Java
	 * Bean与SQL之间的转换、数据库结果集的数据表与该数据映射模版之间值的设置。SQL的查询字段或更新字段、插入字段该dataMappingClass(Java
	 * Bean)映射模版中取得。 可在dataMappingClass(Java Bean)中增加过滤方法，该过滤方法返回<code>Map</code>类型数据。
	 * 该数据映射模版类型可为：
	 * 
	 * <li>JavaBean.class</li>
	 * <li>Map.class/HashMap.class/LinkedHashMap.class</li>
	 * <li>List.class/ListArray.class</li>
	 * <li>Integer.class 或其它基本数据类型</li>
	 * 
	 * </br></br>当dataMappingClass类型是<code>Map.class/HashMap.class/LinkedHashMap.class/List.class/ListArray.class/</code>基本数据类型时。
	 * 此时与数据库结果集的数据表相对应、自动构造SQL时对应都不成立，必须手动写SQL语句。为了可能自动构造SQL及数据映射，可重新设置<code>JdbcUtils.setSqlMappingClass(Class)</code>。
	 * </p>
	 * 
	 * @see JdbcUtils#setSqlMappingClass(Class)
	 * @see JdbcUtils#getSqlFilter()
	 */
	public void setDataMappingClass(Class dataMappingClass) {
		this.dataMappingClass = dataMappingClass;
		this.sqlPro.setDataMappingClass(dataMappingClass);
		this.beanPro.setDataMappingClass(dataMappingClass);
		this.setSqlFilter(this.beanPro.getSqlFilter());
	}

	/**
	 * 取得数据库字段与Java Bean字段的命名规则，默认为常量HUMP
	 * 
	 * @see JdbcUtils#HUMP
	 * @see JdbcUtils#SEGMENTATION
	 */
	public String getRule() {
		return rule;
	}

	/**
	 * 设置数据库字段与Java Bean字段的命名规则，默认为常量HUMP
	 * 
	 * @see JdbcUtils#HUMP
	 * @see JdbcUtils#SEGMENTATION
	 */
	public void setRule(String rule) {
		this.rule = rule;
	}

	public String getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}

	/**
	 * 取得SQL数据映射Class。</br>
	 * 
	 * 在自动构造sql时，SDK默认从<code>JdbcUtils#dataMappingClass</code>中取得相应字段构造sql。
	 * 当<code>JdbcUtils#dataMappingClass</code>类型为List或Map、Java基本数据类型时，在自动构造sql时，无法取得相应字段，
	 * 可通过设值<code>JdbcUtils#sqlMappingClass</code>使用构造sql语句生效。
	 * 
	 * @see JdbcUtils#dataMappingClass
	 */
	public Class getSqlMappingClass() {
		return sqlMappingClass;
	}

	/**
	 * 设置SQL数据映射Class。</br>
	 * 
	 * 在自动构造sql时，SDK默认从<code>JdbcUtils#dataMappingClass</code>中取得相应字段构造sql。
	 * 当<code>JdbcUtils#dataMappingClass</code>类型为List或Map、Java基本数据类型时，在自动构造sql时，无法取得相应字段，
	 * 可通过设值<code>JdbcUtils#sqlMappingClass</code>使用构造sql语句生效。
	 * 
	 * @see JdbcUtils#dataMappingClass
	 */
	public void setSqlMappingClass(Class sqlMappingClass) {
		this.sqlMappingClass = sqlMappingClass;
		this.sqlPro.setDataMappingClass(sqlMappingClass);
		this.beanPro.setDataMappingClass(sqlMappingClass);
	}

	public Map getSqlFilter() {
		return sqlFilter;
	}

	public void setSqlFilter(Map sqlFilter) {
		this.sqlFilter = sqlFilter;
	}

	/**
	 * 结果集处理
	 * 
	 * @User: hubo.0508@gmail.com
	 * @Date Apr 27, 2012
	 * @Time 11:01:37 PM
	 */
	class ResultProcessor {

		private Object handle(ResultSet rs, Object instanceCollectionOrClass)
				throws SQLException {

			if (instanceCollectionOrClass == null) {
				throw new SQLException("Null result set");
			}

			// Result is ArrayList
			if (ArrayList.class.isInstance(instanceCollectionOrClass)) {
				return rsPro.toArrayList((ArrayList) instanceCollectionOrClass,
						rs);
			}

			// Result is LinkedHashMap Or HashMap
			if (LinkedHashMap.class.isInstance(instanceCollectionOrClass)
					|| HashMap.class.isInstance(instanceCollectionOrClass)) {
				checkDataUnique(rs);
				return rs.next() ? rsPro.toMap((Map) instanceCollectionOrClass,
						rs) : null;
			}

			// Back to the only result set
			if (instanceCollectionOrClass.toString().indexOf("class") == 0) {
				checkDataUnique(rs);

				if (isHashMap(getDataMappingClass())) {
					return rs.next() ? rsPro.toUniqueObject(new HashMap(), rs)
							: null;
				} else if (isLinkedHashMap(getDataMappingClass())) {
					return rs.next() ? rsPro.toUniqueObject(
							new LinkedHashMap(), rs) : null;
				} else if (isArrayList(getDataMappingClass())) {
					return rs.next() ? rsPro
							.toUniqueObject(new ArrayList(), rs) : null;
				} else if (beanPro.isBasicType(getDataMappingClass())) {
					return rs.next() ? rsPro.toUniqueBiscType(rs,
							getDataMappingClass()) : null;
				} else {
					return rs.next() ? rsPro.toUniqueObject(beanPro
							.newInstance(getDataMappingClass()), rs) : null;
				}
			}

			return null;
		}

		/**
		 * 取得数据库结果集数据表总记录数
		 * 
		 * @param rs
		 *            数据库结果集数据表
		 * 
		 * @return 总记录数
		 * 
		 * @throws SQLException
		 */
		private long resultSize(ResultSet rs) throws SQLException {

			rs.last();
			long rowCount = rs.getRow();
			rs.beforeFirst();

			return rowCount;
		}

		/**
		 * 将数据库结果集的数据表映射成Java基本数据类型。
		 * 
		 * @param clazz
		 *            Java基本数据类型Class模版(Integer.class)
		 * @param rs
		 *            数据库结果集的数据表
		 * 
		 * @return Java基本数据类型数据集
		 * 
		 * @see ResultProcessor#getDataMappingClass()
		 * 
		 * @exception
		 * 在数据库结果集数据表列超过2列时，抛出SQLException；数据库结果集数据表中数据类型与设定的类型不匹配，抛出SQLException；
		 * 
		 * @throws SQLException
		 */
		private Object toUniqueBiscType(ResultSet rs, Class clazz)
				throws SQLException {
			ResultSetMetaData rsmd = rs.getMetaData();
			int cols = rsmd.getColumnCount();

			if (cols >= 2) {
				throw new SQLException("Query column number greater than !");
			}

			Object value = rs.getObject(1);
			if (!clazz.toString().equals(value.getClass().toString())) {
				throw new SQLException(
						"Set the return value type does not match!"
								+ value.getClass() + " can not convert "
								+ clazz);
			}

			return value;
		}

		/**
		 * 将数据库结果集的数据表映射成Map/List/JavaBean。
		 * 
		 * <p>
		 * 返回Map/List/JavaBean中的具体值的映射根据<code>JdbcUtils.getDataMappingClass()</code>类型定义。
		 * <li>类型为Map时，将数据库结果集的数据映射成Map，键为列名。</li>
		 * <li>类型为List时，将数据库结果集的数据映射成List。</li>
		 * <li>类型为JavaBean时，将数据库结果集的数据映射成JavaBean。</li>
		 * </p>
		 * 
		 * @param instanceObject
		 *            返回数据类型(实例化后对象)
		 * @param rs
		 *            数据库结果集的数据表
		 * 
		 * @return Map/List/JavaBean数据集
		 * 
		 * @see ResultProcessor#getDataMappingClass()
		 * 
		 * @throws SQLException
		 */
		private Object toUniqueObject(Object instanceObject, ResultSet rs)
				throws SQLException {

			ResultSetMetaData rsmd = rs.getMetaData();
			int cols = rsmd.getColumnCount();

			for (int i = 0; i < cols; i++) {
				String field = rsmd.getColumnName(i + 1);
				if (isMap(instanceObject.getClass())) {
					((Map) instanceObject).put(field, rs.getObject(field));
				} else if (isList(instanceObject.getClass())) {
					((List) instanceObject).add(rs.getObject(field));
				} else {
					PropertyDescriptor pro = beanPro.getProDescByName(sqlPro
							.convert(field, TOTYPE[0]));
					beanPro
							.callSetter(instanceObject, pro, rs
									.getObject(field));
				}
			}

			return instanceObject;
		}

		/**
		 * 将数据库结果集的数据表映射成Map数据集。
		 * 
		 * <p>
		 * 返回Map数据集中的具体值的映射根据<code>JdbcUtils.getDataMappingClass()</code>类型定义。
		 * <li>类型为Map时，将数据库结果集的数据映射成Map，键为列名。</li>
		 * </p>
		 * 
		 * @param rsh
		 *            返回数据类型(实例化后对象)
		 * @param rs
		 *            数据库结果集的数据表
		 * 
		 * @return Map数据集
		 * 
		 * @see ResultProcessor#getDataMappingClass()
		 * 
		 * @throws SQLException
		 */
		private Map toMap(Map rsh, ResultSet rs) throws SQLException {

			ResultSetMetaData rsmd = rs.getMetaData();
			int cols = rsmd.getColumnCount();

			for (int i = 1; i <= cols; i++) {
				if (isMap(getDataMappingClass())) {
					rsh.put(rsmd.getColumnName(i), rs.getObject(i));
				} else {
					rsh.put(sqlPro.convert(rsmd.getColumnName(i), TOTYPE[0]),
							rs.getObject(i));
				}
			}

			return rsh;
		}

		/**
		 * 将数据库结果集的数据表映射成ArrayList数据集。
		 * 
		 * <p>
		 * 返回ArrayList数据集中的具体值的映射根据<code>JdbcUtils.getDataMappingClass()</code>类型定义。
		 * <li>类型为Map时，将数据库结果集的数据映射成Map，键为列名。</li>
		 * <li>类型为JavaBean时，将数据库结果集的数据映射到JavaBean中。</li>
		 * </p>
		 * 
		 * @param rsh
		 *            返回数据类型(实例化后对象)
		 * @param rs
		 *            数据库结果集的数据表
		 * 
		 * @return ArrayList数据集
		 * 
		 * @see ResultProcessor#getDataMappingClass()
		 * 
		 * @throws SQLException
		 */
		private List toArrayList(ArrayList rsh, ResultSet rs)
				throws SQLException {
			ResultSetMetaData rsmd = rs.getMetaData();
			int cols = rsmd.getColumnCount();
			while (rs.next()) {
				if (isHashMap(getDataMappingClass())) {
					rsh.add(toMap(new HashMap(), rs));
				} else if (isLinkedHashMap(getDataMappingClass())) {
					rsh.add(toMap(new LinkedHashMap(), rs));
				} else {
					Object bean = beanPro.newInstance(getDataMappingClass());
					for (int i = 0; i < cols; i++) {
						String field = rsmd.getColumnName(i + 1);
						PropertyDescriptor pro = beanPro
								.getProDescByName(sqlPro.convert(field,
										TOTYPE[0]));
						beanPro.callSetter(bean, pro, rs.getObject(field));
					}
					rsh.add(bean);
				}
			}

			return rsh;
		}

	}

	/**
	 * 实体处理
	 * 
	 * @author hubo.0508@gmail.com
	 */
	class BeanProcessor {

		/**
		 * 数据映射模版，与数据库结果集的数据表相对应。
		 * 
		 * <p>
		 * 该数据映射模版作用于Java Bean业务描述。可在Java Bean dataMappingClass中增加过滤规则，该过滤方法返回<code>Map</code>过滤规则。
		 * </p>
		 * 
		 * @see JdbcUtils#getSqlFilter()
		 */
		private Class _dataMappingClass;

		/**
		 * 空构造函数
		 */
		public BeanProcessor() {
		}

		/**
		 * @param _dataMappingClass 
		 *            数据映射模版，与数据库结果集的数据表相对应。该数据映射模版作用于Java Bean业务描述。可在Java Bean
		 *            dataMappingClass中增加过滤规则，该过滤方法返回<code>Map</code>过滤规则。
		 * 
		 * @see JdbcUtils#getSqlFilter()
		 */
		public BeanProcessor(Class _dataMappingClass) {
			this._dataMappingClass = _dataMappingClass;
		}

		/**
		 * 取得数据映射模版，与数据库结果集的数据表相对应。
		 * 
		 * 该数据映射模版作用于Java Bean业务描述。可在Java Bean dataMappingClass中增加过滤规则，该过滤方法返回<code>Map</code>过滤规则。
		 * 
		 * @see JdbcUtils#getSqlFilter()
		 */
		public Class getDataMappingClass() {
			return _dataMappingClass;
		}

		/**
		 * 设置数据映射模版，与数据库结果集的数据表相对应。
		 * 
		 * @param _dataMappingClass 
		 *            该数据映射模版作用于Java Bean业务描述。可在Java Bean
		 *            dataMappingClass中增加过滤规则，该过滤方法返回<code>Map</code>过滤规则。
		 * 
		 * @see JdbcUtils#getSqlFilter()
		 */
		public void setDataMappingClass(Class mappingClass) {
			_dataMappingClass = mappingClass;
		}

		/**
		 * 取得Java Bean(<code>BeanProcessor.getDataMappingClass()</code>)的过滤规则
		 * 
		 * @see BeanProcessor#getDataMappingClass()
		 */
		public Map getSqlFilter() {
			try {
				Object obj = newInstance(getDataMappingClass());
				return (Map) callGetter(obj, "sqlFilter");
			} catch (SQLException e) {
			}
			return null;
		}

		/**
		 * 对SQL语句中的参数键在Java Bean中取得对应的值，组装成<code>Object[]</code>数组。
		 * 
		 * @param instanceBean
		 *            存储参数值的Java Bean
		 * @param sql
		 *            SQL语句(Insert/Update/Delete/Select)
		 * 
		 * @return Object[] 参数数组
		 * 
		 * @throws SQLException
		 * 
		 * @see SqlProcessor#getDataMappingClass()
		 */
		public Object[] objectArray(Object instanceDomain, String sql)
				throws SQLException {
			return objectArray(instanceDomain, sql, null, null);
		}

		/**
		 * 对SQL语句中的参数键在Java Bean中取得对应的值，组装成<code>Object[]</code>数组。
		 * 
		 * <p>
		 * 当参数database与sequence不为空或空字符串时，SQL语句为Insert SQL时，从Java
		 * Bean中取得对应值也会有相应的变化，规根于不同数据库对Insert SQL的主键值的维护不一样。
		 * </p>
		 * 
		 * <li><code>database=JdbcUtils.ORACLE,sequence="seq"</code>,SQL为<code>INSERT INTO user (username, id) VALUES (?, seq.nextval)</code>返回<code>["admin"]</code></li>
		 * <li><code>database=JdbcUtils.ORACLE,sequence="" || null</code>,SQL为<code>INSERT INTO user (username, id) VALUES (?, ?)</code>返回<code>["admin",1]</code></li>
		 * <li><code>database=JdbcUtils.MYSQL,sequence=Jdbcutils.MYSQL_SEQ</code>,SQL为<code>INSERT INTO user (username) VALUES (?)</code>返回<code>["admin"]</code></li>
		 * <li><code>database=JdbcUtils.MYSQL,sequence="" || null</code>,SQL为<code>INSERT INTO user (username, id) VALUES (?, ?)</code>返回<code>["admin",1]</code></li>
		 * <li>SQLSERVER：未实现</li>
		 * 
		 * @param instanceBean
		 *            存储参数值的Java Bean
		 * @param sql
		 *            SQL语句(Insert/Update/Delete/Select)
		 * @param database
		 *            数据类型
		 * @param sequence
		 *            序列类型
		 * 
		 * @return Object[] 参数数组
		 * 
		 * @throws SQLException
		 * 
		 * @see JdbcUtils#MYSQL
		 * @see JdbcUtils#MYSQL_SEQ
		 * @see JdbcUtils#ORACLE
		 * @see JdbcUtils#SQLSERVER
		 * @see SqlProcessor#getDataMappingClass()
		 */
		public Object[] objectArray(Object instanceBean, String sql,
				String database, String sequence) throws SQLException {

			String[] columns = sqlPro.getColumnsKey(sql);
			int columnsLen = columns.length;

			PropertyDescriptor[] proDesc = beanPro
					.propertyDescriptors(getDataMappingClass());
			int len = proDesc.length;

			Object[] params = new Object[columnsLen];

			for (int j = 0; j < columnsLen; j++) {
				String domainField = sqlPro.convert(columns[j], TOTYPE[0]);
				for (int i = 0; i < len; i++) {
					PropertyDescriptor prop = proDesc[i];
					if (beanPro.isBasicType(prop.getPropertyType())
							&& prop.getName().equals(domainField)) {
						if (sqlPro.isOracleAutomatic(prop, database, sequence)) {
						} else if (sqlPro.isMySqlAutomatic(prop, database,
								sequence)) {
						} else {
							params[j] = beanPro.callGetter(instanceBean, prop);
						}
						break;
					}
				}
			}

			return cleanEmpty(params);
		}

		/**
		 * 数据库列字段转换成Java Bean字段。
		 * 
		 * <p>
		 * Map map = new HashMap();</br> map.put("USERNAME","admin");</br>
		 * <p>
		 * 将map中的键KEY转换成所对应的Java Bean字段
		 * 
		 * @param beanClazz
		 *            Java Bean
		 * @param map
		 *            转换对象
		 * 
		 * @return 转换后的Map对象
		 * 
		 * @throws SQLException
		 */
		public Map columnsToBean(Class beanClazz, Map map) throws SQLException {

			Map afterConver = new HashMap();
			Iterator iter = map.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				String key = (String) entry.getKey();
				afterConver.put(sqlPro.convert(key, TOTYPE[0]), entry
						.getValue());
			}
			map = null;
			return afterConver;
		}

		/**
		 * 数据库列字段转换成Java Bean字段。
		 * 
		 * <p>
		 * List columns = new ArrayList();</br> Map map = new HashMap();</br>
		 * map.put("USERNAME","admin");</br>columns.add(map);</br>
		 * <p>
		 * 将columns中的Map中的键KEY转换成所对应的Java Bean字段
		 * 
		 * @param beanClazz
		 *            Java Bean
		 * @param list
		 *            转换对象
		 * 
		 * @return 转换后的List对象
		 * 
		 * @throws SQLException
		 */
		public List mapColumnsToBean(Class beanClazz, List list)
				throws SQLException {

			List afterConver = new ArrayList();
			for (int i = 0; i < list.size(); i++) {
				Map map = (Map) list.get(i);
				afterConver.add(i, columnsToBean(beanClazz, map));
			}

			return afterConver;
		}

		// private String makeStringName(String methodPrefix, String fieldName)
		// {
		// String firstLetter = fieldName.substring(0, 1).toUpperCase();
		// return methodPrefix + firstLetter + fieldName.substring(1);
		// }

		/**
		 * 根据Java Bean字段名称取得属性存储器(PropertyDescriptor)
		 * 
		 * @param name
		 *            Java Bean字段
		 * 
		 * @return 属性存储器(PropertyDescriptor)
		 */
		private PropertyDescriptor getProDescByName(String name)
				throws SQLException {
			PropertyDescriptor[] proDescs = this
					.propertyDescriptors(getDataMappingClass());
			for (int i = 0; i < proDescs.length; i++) {
				PropertyDescriptor pro = proDescs[i];
				if (pro.getName().equals(name)) {
					return pro;
				}
			}

			throw new SQLException(getDataMappingClass().toString()
					+ " : Cannot set " + name);
		}

		/**
		 * 通过Java反射调用目标对象(Java Bean)的指定方法。
		 * 
		 * @param target
		 *            目标Java Bean对象
		 * @param methodName
		 *            方法名
		 * 
		 * @throws SQLException
		 */
		private Object callGetter(Object target, String methodName)
				throws SQLException {
			try {
				Method readMethod = target.getClass().getMethod(methodName,
						new Class[] {});
				return readMethod.invoke(target, new Object[] {});
			} catch (IllegalArgumentException e) {
				throw new SQLException("Cannot get " + methodName + ": "
						+ e.getMessage());
			} catch (IllegalAccessException e) {
				throw new SQLException("Cannot get " + methodName + ": "
						+ e.getMessage());
			} catch (InvocationTargetException e) {
				throw new SQLException("Cannot get " + methodName + ": "
						+ e.getMessage());
			} catch (SecurityException e) {
				throw new SQLException("Cannot get " + methodName + ": "
						+ e.getMessage());
			} catch (NoSuchMethodException e) {
				throw new SQLException("Cannot get " + methodName + ": "
						+ e.getMessage());
			}
		}

		/**
		 * 通过Java反射调用目标对象(Java Bean)的指定方法。
		 * 
		 * @param target
		 *            目标Java Bean对象
		 * @param prop
		 *            Java Bean 属性存储器
		 * @param value
		 *            数据库查询值
		 * 
		 * @throws SQLException
		 */
		private Object callGetter(Object target, PropertyDescriptor prop)
				throws SQLException {

			Method getter = prop.getReadMethod();
			if (getter == null) {
				return null;
			}

			try {
				return getter.invoke(target, new Object[] {});
			} catch (IllegalArgumentException e) {
				throw new SQLException("Cannot get " + prop.getName() + ": "
						+ e.getMessage());
			} catch (IllegalAccessException e) {
				throw new SQLException("Cannot get " + prop.getName() + ": "
						+ e.getMessage());
			} catch (InvocationTargetException e) {
				throw new SQLException("Cannot get " + prop.getName() + ": "
						+ e.getMessage());
			}
		}

		/**
		 * 通过Java反射将数据库查询值设值到目标Java Bean中。
		 * 
		 * @param target
		 *            设置值的目标Java Bean对象
		 * @param prop
		 *            Java Bean 属性存储器
		 * @param value
		 *            数据库查询值
		 * 
		 * @throws SQLException
		 */
		private void callSetter(Object target, PropertyDescriptor prop,
				Object value) throws SQLException {

			Method setter = prop.getWriteMethod();
			if (setter == null) {
				return;
			}

			Class[] params = setter.getParameterTypes();
			try {
				// convert types for some popular ones
				if (value != null) {
					if (value instanceof java.util.Date) {
						if (params[0].getName().equals("java.sql.Date")) {
							value = new java.sql.Date(((java.util.Date) value)
									.getTime());
						} else if (params[0].getName().equals("java.sql.Time")) {
							value = new java.sql.Time(((java.util.Date) value)
									.getTime());
						} else if (params[0].getName().equals(
								"java.sql.Timestamp")) {
							value = new java.sql.Timestamp(
									((java.util.Date) value).getTime());
						}
					}
				}

				// Don't call setter if the value object isn't the right type
				if (params[0].isInstance(value)) {
					setter.invoke(target, new Object[] { value });
				} else {
					throw new SQLException("Cannot set " + prop.getName()
							+ ": incompatible types.");
				}

			} catch (IllegalArgumentException e) {
				throw new SQLException("Cannot set " + prop.getName() + ": "
						+ e.getMessage());

			} catch (IllegalAccessException e) {
				throw new SQLException("Cannot set " + prop.getName() + ": "
						+ e.getMessage());

			} catch (InvocationTargetException e) {
				throw new SQLException("Cannot set " + prop.getName() + ": "
						+ e.getMessage());
			}
		}

		/**
		 * 数据库列段命名以驼峰规则时(USERNAME)，与Java Bean
		 * <code>BeanProcessor.getDataMappingClass()</code>中字段userName对应不上，
		 * 在无法通过Java反射将数据库查询值设置到Java Bean。调用该方法进行转换。
		 * 
		 * @param column
		 *            数据库列字段名称
		 * 
		 * @return 转换成Java Bean中的字段
		 * 
		 * @see BeanProcessor#getDataMappingClass()
		 * 
		 * @throws SQLException
		 */
		private String convertedBeanField(String column) throws SQLException {

			PropertyDescriptor[] proDesc = propertyDescriptors(getDataMappingClass());
			for (int i = 0; i < proDesc.length; i++) {
				PropertyDescriptor pro = proDesc[i];
				if (isBasicType(pro.getPropertyType())
						&& pro.getName().toUpperCase().equals(column)) {
					return pro.getName();
				}
			}

			return null;
		}

		/**
		 * 初始化Bean內部信息
		 * 
		 * @param c
		 *            Java Bean class模版
		 * 
		 * @throws SQLException
		 */
		private PropertyDescriptor[] propertyDescriptors(Class c)
				throws SQLException {
			BeanInfo beanInfo = null;
			try {
				beanInfo = Introspector.getBeanInfo(c);
			} catch (IntrospectionException e) {
				throw new SQLException("Bean introspection failed: "
						+ e.getMessage());
			}

			return beanInfo.getPropertyDescriptors();
		}

		/**
		 * 实例化Class模板
		 * 
		 * @param c
		 *            Class模版
		 * 
		 * @return 实例化对象
		 * 
		 * @throws SQLException
		 */
		private Object newInstance(Class c) throws SQLException {
			try {
				return c.newInstance();

			} catch (InstantiationException e) {
				throw new SQLException("Cannot create " + c.getName() + ": "
						+ e.getMessage());

			} catch (IllegalAccessException e) {
				throw new SQLException("Cannot create " + c.getName() + ": "
						+ e.getMessage());
			}
		}

		/**
		 * 判断Class模版是否基础类型
		 * 
		 * @param clazz
		 *            Class模版
		 * 
		 * @return true(是基础类型) || false(不是基础类型)
		 */
		private boolean isBasicType(Class clazz) {
			if (clazz == String.class) {
				return true;

			} else if (clazz == int.class || clazz == Integer.class) {
				return true;

			} else if (clazz == double.class || clazz == Double.class) {
				return true;

			} else if (clazz == boolean.class || clazz == Boolean.class) {
				return true;

			} else if (clazz == float.class || clazz == Float.class) {
				return true;

			} else if (clazz == long.class || clazz == Long.class) {
				return true;

			} else if (clazz == Date.class) {
				return true;

			} else if (clazz == byte.class || clazz == Byte.class) {
				return true;

			} else if (clazz == short.class || clazz == Short.class) {
				return true;

			} else if (clazz == char.class || clazz == Character.class) {
				return true;

			} else if (clazz == Timestamp.class) {
				return true;
			}

			return false;
		}
	}

	/**
	 * Java Bean自动构造SQL处理工具类。
	 * 
	 * @author hubo.0508@gmail.com
	 */
	class SqlProcessor {

		/**
		 * 数据映射模版，与数据库结果集的数据表相对应。
		 * 
		 * <p>
		 * 该数据映射模版作用于Java Bean与SQL之间的转换。 SQL的查询字段或更新字段、插入字段该<code>SqlProcessor.getDataMappingClass()</code>映射模版中取得。
		 * </p>
		 * 
		 * 可在Java Bean dataMappingClass中增加过滤方法，该过滤方法返回<code>Map</code>过滤规则。
		 * 
		 * @see JdbcUtils#getSqlFilter()
		 */
		private Class _dataMappingClass;

		/**
		 * 参数规则<code>{ "=?", "?", "<?", "<=?", ")", "(", ">" }</code>
		 */
		private final String[] equalsparams = new String[] { "=?", "?", "<?",
				"<=?", ")", "(", ">" };

		/**
		 * 参数规则<code>{ "(", ">" }</code>
		 */
		private final String[] notequalsparams = new String[] { "(", ">" };

		/**
		 * 空构造函数
		 */
		public SqlProcessor() {
		}

		/**
		 * @param _dataMappingClass 
		 *            数据映射模版，与数据库结果集的数据表相对应。该数据映射模版作用于Java Bean与SQL之间的转换。
		 *            SQL的查询字段或更新字段、插入字段该<code>SqlProcessor.getDataMappingClass()</code>映射模版中取得。
		 *            可在Java Bean dataMappingClass中增加过滤方法，该过滤方法返回<code>Map</code>过滤规则。
		 * 
		 * @see JdbcUtils#getSqlFilter()
		 */
		public SqlProcessor(Class _dataMappingClass) {
			this._dataMappingClass = _dataMappingClass;
		}

		/**
		 * 取得Java Bean(<code>SqlProcessor.getDataMappingClass()</code>)类名称。
		 * 
		 * @return Java Bean名称
		 */
		public String getSimpleName() {
			String text = getDataMappingClass().getName();
			int index = text.lastIndexOf(".");
			if (index >= 0) {
				return text.substring(text.lastIndexOf(".") + 1);
			}
			return text;
		}

		/**
		 * 根据Java Bean(<code>SqlProcessor.getDataMappingClass()</code>)构造Select
		 * SQL语句。</br></br> 在构造Select SQL语句时，如果Java Bean(<code>SqlProcessor.getDataMappingClass()</code>)中定义了<code>public Map sqlFilter(){}</code>方法，或手动设置<code>JdbcUtils.setSqlFilter(Map)</code>过滤条件，则在构造SQL时，会根据规则进行过滤。
		 * 
		 * <li><code>makeSelectSql()</cdoe>，构造出的SQL为<code>SELECT id,user_name FROM user</code></li>
		 * <li>SQLSERVER：未实现</li>
		 * 
		 * @return SQL语句
		 *            
		 * @throws SQLException
		 *            
		 * @see JdbcUtils#setSqlFilter(Map)
		 * @see SqlProcessor#getDataMappingClass()
		 */
		public String makeSelectSql() throws SQLException {
			return makeSelectSql(null);
		}

		/**
		 * 根据Java Bean(<code>SqlProcessor.getDataMappingClass()</code>)构造Select
		 * SQL语句。</br></br> 在构造Select SQL语句时，如果Java Bean(<code>SqlProcessor.getDataMappingClass()</code>)中定义了<code>public Map sqlFilter(){}</code>方法，或手动设置<code>JdbcUtils.setSqlFilter(Map)</code>过滤条件，则在构造SQL时，会根据规则进行过滤。
		 * 
		 * <li><code>makeSelectSql(null || "")</cdoe>，构造出的SQL为<code>SELECT id,user_name FROM user</code></li>
		 * <li><code>makeSelectSql("where id=? and user_name=?")</cdoe>，构造出的SQL为<code>SELECT id,user_name FROM user WHERE id=? and user_name=?</code></li>
		 * <li>SQLSERVER：未实现</li>
		 * 
		 * @param whereIf
		 *            SQL自定自定义条件
		 * @return SQL语句
		 *            
		 * @throws SQLException
		 *            
		 * @see JdbcUtils#setSqlFilter(Map)
		 * @see SqlProcessor#getDataMappingClass()
		 */
		public String makeSelectSql(String key) throws SQLException {
			StringBuffer sb = new StringBuffer("SELECT ");

			PropertyDescriptor[] proDesc = beanPro.propertyDescriptors(this
					.getDataMappingClass());
			int len = proDesc.length;
			for (int i = 0; i < len; i++) {
				PropertyDescriptor pro = proDesc[i];
				if (beanPro.isBasicType(pro.getPropertyType())) {
					if (sqlFilter == null) {
						appendSelectParams(sb, pro.getName(), i, len);
					} else {
						Object filter = sqlFilter.get(pro.getName());
						if (String.class.isInstance(filter)
								&& filter.toString().equals(pro.getName())) {
							appendSelectParams(sb, filter.toString(), i, len);
						} else if (filter == null || toBoolean(filter)) {
							appendSelectParams(sb, pro.getName(), i, len);
						}
					}
				}
			}
			sb.append(" FROM ");
			sb.append(convert(textFilter(getSimpleName()), TOTYPE[1]));
			if (isNotEmpty(key)) {
				sb.append(" ");
				appendParamsId(sb, key);
			}

			return sb.toString();
		}

		/**
		 * 对文本text进行条件过滤。
		 * 
		 * @param text
		 *            文本
		 * @param sqlFilter
		 *            过滤规则
		 * 
		 * @return 文本
		 */
		public String textFilter(String text) {
			if (getSqlFilter() == null) {
				return text;
			}

			Object filter = getSqlFilter().get(text);
			if (String.class.isInstance(filter)) {
				return filter.toString();
			}

			return text;
		}

		/**
		 * 追加参数到select SQL语句
		 * 
		 * @param sb
		 *            SQL容器
		 * @param name
		 *            Java Bean 字段
		 * @param i
		 * @param len
		 * @throws SQLException
		 */
		private void appendSelectParams(StringBuffer sb, String name, int i,
				int len) throws SQLException {
			sb.append(sqlPro.convert(name, TOTYPE[1]));
			if (i < (len - 1)) {
				sb.append(", ");
			}
		}

		/**
		 * 将值转换成<code>boolean</code>值。
		 * 
		 * @param value
		 *            转换值
		 * 
		 * @return value == null > false；value != boolean > false；value ==
		 *         boolean > true；
		 */
		public boolean toBoolean(Object value) {
			boolean returnvalue = true;
			try {
				returnvalue = Boolean.valueOf(value.toString()).booleanValue();
			} catch (RuntimeException e) {
				if (value != null) {
					returnvalue = false;
				}
			}
			return returnvalue;
		}

		/**
		 * 根据Java Bean(<code>SqlProcessor.getDataMappingClass()</code>)构造Delete
		 * SQL语句。</br></br> 在构造Delete SQL语句时，如果Java Bean(<code>SqlProcessor.getDataMappingClass()</code>)中定义了<code>public Map sqlFilter(){}</code>方法，或手动设置<code>JdbcUtils.setSqlFilter(Map)</code>过滤条件，则在构造SQL时，会根据规则进行过滤。
		 * 
		 * <li><code>makeDeleteSql()</cdoe>，构造出的SQL为<code>DELETE FROM user WHERE id=?</code></li>
		 * <li>SQLSERVER：未实现</li>
		 * 
		 * @param whereIf
		 *            SQL自定自定义条件
		 * @return SQL语句
		 *            
		 * @throws SQLException
		 *            
		 * @see JdbcUtils#setSqlFilter(Map)
		 * @see SqlProcessor#getDataMappingClass()
		 */
		public String makeDeleteSql() throws SQLException {
			return makeDeleteSql(null);
		}

		/**
		 * 根据Java Bean(<code>SqlProcessor.getDataMappingClass()</code>)构造Delete
		 * SQL语句。</br></br> 在构造Delete SQL语句时，如果Java Bean(<code>SqlProcessor.getDataMappingClass()</code>)中定义了<code>public Map sqlFilter(){}</code>方法，或手动设置<code>JdbcUtils.setSqlFilter(Map)</code>过滤条件，则在构造SQL时，会根据规则进行过滤。
		 * 
		 * <li><code>makeDeleteSql(null || "")</cdoe>，构造出的SQL为<code>DELETE FROM user WHERE id=?</code></li>
		 * <li><code>makeDeleteSql("where id=? and name=?")</cdoe>，构造出的SQL为<code>DELETE FROM user WHERE id=? and user_name=?</code></li>
		 * <li>SQLSERVER：未实现</li>
		 * 
		 * @param whereIf
		 *            SQL自定自定义条件
		 * @return SQL语句
		 *            
		 * @throws SQLException
		 *            
		 * @see JdbcUtils#setSqlFilter(Map)
		 * @see SqlProcessor#getDataMappingClass()
		 */
		public String makeDeleteSql(String whereIf) throws SQLException {
			StringBuffer sb = new StringBuffer();
			sb.append("DELETE FROM ");
			sb.append(convert(textFilter(getSimpleName()), TOTYPE[1]));
			if (isNotEmpty(whereIf)) {
				sb.append(" ");
				appendParamsId(sb, whereIf);
			} else {
				sb.append(" ");
				appendParamsId(sb, null);
			}

			return sb.toString();
		}

		/**
		 * 根据Java Bean(<code>SqlProcessor.getDataMappingClass()</code>)构造Update
		 * SQL语句。</br></br> 在构造Update SQL语句时，如果Java Bean(<code>SqlProcessor.getDataMappingClass()</code>)中定义了<code>public Map sqlFilter(){}</code>方法，或手动设置<code>JdbcUtils.setSqlFilter(Map)</code>过滤条件，则在构造SQL时，会根据规则进行过滤。
		 * 
		 * <li><code>makeUpdateSql()</cdoe>，构造出的SQL为<code>UPDATE user SET user_name, has_data=?, id=? WHERE id=?</code></li>
		 * <li>SQLSERVER：未实现</li>
		 * 
		 * @param whereIf
		 *            SQL自定自定义条件
		 * @return SQL语句
		 *            
		 * @throws SQLException
		 *            
		 * @see JdbcUtils#setSqlFilter(Map)
		 * @see SqlProcessor#getDataMappingClass()
		 */
		public String makeUpdateSql() throws SQLException {
			return makeUpdateSql(null);
		}

		/**
		 * 根据Java Bean(<code>SqlProcessor.getDataMappingClass()</code>)构造Update
		 * SQL语句。</br></br> 在构造Update SQL语句时，如果Java Bean(<code>SqlProcessor.getDataMappingClass()</code>)中定义了<code>public Map sqlFilter(){}</code>方法，或手动设置<code>JdbcUtils.setSqlFilter(Map)</code>过滤条件，则在构造SQL时，会根据规则进行过滤。
		 * 
		 * <li><code>makeUpdateSql(null || "")</cdoe>，构造出的SQL为<code>UPDATE user SET user_name, has_data=?, id=? WHERE id=?</code></li>
		 * <li><code>makeUpdateSql("where id=? and name=?")</cdoe>，构造出的SQL为<code>UPDATE user SET user_name, has_data=?, id=? WHERE id=? and name=?</code></li>
		 * <li>SQLSERVER：未实现</li>
		 * 
		 * @param whereIf
		 *            SQL自定自定义条件
		 * @return SQL语句
		 *            
		 * @throws SQLException
		 *            
		 * @see JdbcUtils#setSqlFilter(Map)
		 * @see SqlProcessor#getDataMappingClass()
		 */
		public String makeUpdateSql(String whereIf) throws SQLException {
			StringBuffer sb = new StringBuffer();
			sb.append("UPDATE ");
			sb.append(convert(textFilter(getSimpleName()), TOTYPE[1]));
			sb.append(" SET ");

			PropertyDescriptor[] proDesc = beanPro.propertyDescriptors(this
					.getDataMappingClass());
			int len = proDesc.length;
			for (int i = 0; i < len; i++) {
				PropertyDescriptor pro = proDesc[i];
				if (beanPro.isBasicType(pro.getPropertyType())) {
					if (sqlFilter == null) {
						appendSelectParams(sb, pro.getName(), i, len);
					} else {
						Object filter = sqlFilter.get(pro.getName());
						if (String.class.isInstance(filter)
								&& filter.toString().equals(pro.getName())) {
							appendUpdateParams(sb, filter.toString(), i, len);
						} else if (filter == null || toBoolean(filter)) {
							appendUpdateParams(sb, pro.getName(), i, len);
						}
					}
				}
			}
			if (isNotEmpty(whereIf)) {
				appendParamsId(sb, whereIf);
			} else {
				appendParamsId(sb, null);
			}

			return sb.toString();
		}

		/**
		 * @param sb
		 *            SQL存储容器
		 * @param name
		 *            Java Bean 字段
		 * @param i
		 * @param len
		 * @throws SQLException
		 */
		private void appendUpdateParams(StringBuffer sb, String name, int i,
				int len) throws SQLException {
			sb.append(sqlPro.convert(name, TOTYPE[1]));
			if (i < len - 1) {
				sb.append("=?, ");
			} else {
				sb.append("=? ");
			}
		}

		/**
		 * 根据Java Bean(<code>SqlProcessor.getDataMappingClass()</code>)构造Insert
		 * SQL语句。主键值通过手动维护，对于不同的数据库构造出的Insert SQL语句都是一样的，无差别。</br></br>
		 * 在构造Insert SQL语句时，如果Java Bean(<code>SqlProcessor.getDataMappingClass()</code>)中定义了<code>public Map sqlFilter(){}</code>方法，或手动设置<code>JdbcUtils.setSqlFilter(Map)</code>过滤条件，则在构造SQL时，会根据规则进行过滤。
		 * 
		 * <li>ORACLE：<code>makeInsertSql(DbTools.ORACLE, null || "")</cdoe>，构造出的SQL为<code>INSERT INTO user (username, id) VALUES (?, ?)</code></li>
		 * <li>MYSQL：<code>makeInsertSql(DbTools.MYSQL, null || "")，构造出的SQL为<code>INSERT INTO user (username, id) VALUES (?, ?)</code></li>
		 * <li>SQLSERVER：未实现</li>
		 * 
		 * @return SQL语句
		 *            
		 * @throws SQLException
		 * 
		 * @see JdbcUtils#MYSQL
		 * @see JdbcUtils#MYSQL_SEQ
		 * @see JdbcUtils#ORACLE
		 * @see JdbcUtils#SQLSERVER
		 * @see JdbcUtils#setSqlFilter(Map)
		 * @see SqlProcessor#getDataMappingClass()
		 */
		public String makeInsertSql() throws SQLException {
			return makeInsertSql(null, null);
		}

		/**
		 * 根据Java Bean(<code>SqlProcessor.getDataMappingClass()</code>)构造Insert
		 * SQL语句。对于Insert
		 * SQL语句根据数据库的不同所构造出的SQL也会有差异。在Oracle主键值的增长可通过序列自主维护，而MySql数据库不需要序列。</br></br>
		 * 在构造Insert SQL语句时，如果Java Bean(<code>SqlProcessor.getDataMappingClass()</code>)中定义了<code>public Map sqlFilter(){}</code>方法，或手动设置<code>JdbcUtils.setSqlFilter(Map)</code>过滤条件，则在构造SQL时，会根据规则进行过滤。
		 * 
		 * <li>ORACLE：<code>makeInsertSql(DbTools.ORACLE, "seq")</cdoe>，构造出的SQL为<code>INSERT INTO user (username, id) VALUES (?, seq.NEXTVAL)</code></li>
		 * <li>ORACLE：<code>makeInsertSql(DbTools.ORACLE, null || "")</cdoe>，构造出的SQL为<code>INSERT INTO user (username, id) VALUES (?, ?)</code></li>
		 * <li>MYSQL：<code>makeInsertSql(DbTools.MYSQL, DbTools.MYSQL_SEQ)，构造出的SQL为<code>INSERT INTO user (username) VALUES (?)</code></li>
		 * <li>MYSQL：<code>makeInsertSql(DbTools.MYSQL, null || "")，构造出的SQL为<code>INSERT INTO user (username, id) VALUES (?, ?)</code></li>
		 * <li>SQLSERVER：未实现</li>
		 * 
		 * @param database
		 *            数据库类型
		 * @param sequence
		 *            序列名称
		 *            
		 * @return SQL语句
		 *            
		 * @throws SQLException
		 *            
		 * @see JdbcUtils#MYSQL
		 * @see JdbcUtils#MYSQL_SEQ
		 * @see JdbcUtils#ORACLE
		 * @see JdbcUtils#SQLSERVER
		 * @see JdbcUtils#setSqlFilter(Map)
		 * @see SqlProcessor#getDataMappingClass()
		 */
		public String makeInsertSql(String database, String sequence)
				throws SQLException {
			StringBuffer sb = new StringBuffer();
			sb.append("INSERT INTO ");
			sb.append(convert(textFilter(getSimpleName()), TOTYPE[1]));
			sb.append(" (");

			StringBuffer paramsvalue = new StringBuffer();

			PropertyDescriptor[] proDesc = beanPro.propertyDescriptors(this
					.getDataMappingClass());
			int len = proDesc.length;
			for (int i = 0; i < len; i++) {
				PropertyDescriptor pro = proDesc[i];
				if (beanPro.isBasicType(pro.getPropertyType())) {

					if (sqlFilter != null
							&& toBoolean(sqlFilter.get(pro.getName())) == false) {
						continue;
					}

					if (isMySqlAutomatic(pro, database, sequence)) {
						sb.deleteCharAt(sb.length() - 2);
						paramsvalue.deleteCharAt(paramsvalue.length() - 2);
						continue;
					} else {
						Object filter = sqlFilter.get(pro.getName());
						if (String.class.isInstance(filter)
								&& filter.toString().equals(pro.getName())) {
							sb.append(sqlPro.convert(filter.toString(),
									TOTYPE[1]));
						} else {
							sb.append(sqlPro.convert(pro.getName(), TOTYPE[1]));
						}
					}

					if (i < len - 1) {
						sb.append(", ");
					}

					if (isOracleAutomatic(pro, database, sequence)) {
						paramsvalue.append(sequence);
						paramsvalue.append(".NEXTVAL");
					} else if (isMySqlAutomatic(pro, database, sequence)) {
						continue;
					} else {
						if (i < len - 1) {
							paramsvalue.append("?, ");
						} else {
							paramsvalue.append("? ");
						}
					}
				}
			}
			sb.append(") VALUES (");
			sb.append(paramsvalue.toString());
			sb.append(")");

			return sb.toString();
		}

		/**
		 * 判断Oracle数据库的主键值是否自动递增。
		 * 
		 * @param pro
		 *            Java Bean对象
		 * @param database
		 *            数据库类型
		 * @param sequence
		 *            序列类型
		 * 
		 * @return true(Oracle数据库主键值为自动递增) || false(Oracle数据库主键值为手动维护)
		 * 
		 * @see JdbcUtils#getPrimaryKey()
		 * @see JdbcUtils#MYSQL
		 * @see JdbcUtils#MYSQL_SEQ
		 * @see JdbcUtils#ORACLE
		 * @see JdbcUtils#SQLSERVER
		 */
		protected boolean isOracleAutomatic(PropertyDescriptor pro,
				String database, String sequence) {
			if (pro.getName().equals(getPrimaryKey())
					&& ORACLE.equals(database) && isNotEmpty(sequence)) {
				return true;
			}

			return false;
		}

		/**
		 * 判断MySql数据库的主键值是否自动递增。
		 * 
		 * @param pro
		 *            Java Bean对象
		 * @param database
		 *            数据库类型
		 * @param sequence
		 *            序列类型
		 * 
		 * @return true(MySql数据库主键值为自动递增) || false(MySql数据库主键值为手动维护)
		 * 
		 * @see JdbcUtils#getPrimaryKey()
		 * @see JdbcUtils#MYSQL
		 * @see JdbcUtils#MYSQL_SEQ
		 * @see JdbcUtils#ORACLE
		 * @see JdbcUtils#SQLSERVER
		 */
		protected boolean isMySqlAutomatic(PropertyDescriptor pro,
				String database, String sequence) {
			if (MYSQL.equals(database) && pro.getName().equals(getPrimaryKey())
					&& MYSQL_SEQ.equals(sequence)) {
				return true;
			}

			return false;
		}

		/**
		 * 对SQL语句追加自定义条件。
		 * 
		 * <li><code>appendParamsId(sb, "WHERE id=? and name=?")</code>，则返回<code>... WHERE id=? and name=?</code></li>
		 * <li><code>appendParamsId(sb, null || "")</code>，则返回<code>... where id=?</code></li>
		 * 
		 * @param sb
		 *            SQL字符存储容器
		 * @param whereIf
		 *            SQL查询或更新、删除条件
		 */
		private void appendParamsId(StringBuffer sb, String whereIf) {
			if (isNotEmpty(whereIf)) {
				sb.append(whereIf);
			} else {
				sb.append("WHERE ");
				sb.append(getPrimaryKey());
				sb.append("=?");
			}
		}

		/**
		 * 对更新、插入SQL语句文本中的参数键字段进行拆分，以数组形式返回。
		 * 
		 * <li><code>update user set id=?,username=? where id=?</code>，则返回<code>["id","username","id"]</code></li>
		 * <li><code>insert into user (id,username) values(?,?)</code>，则返回<code>["id","username"]</code></li>
		 * 
		 * @param sql
		 *            SQL语句
		 * 
		 * @return SQL语句参数键数组。未解析到有键字段，则返回null。
		 */
		public String[] getColumnsKey(String sql) {

			sql = standardFormatting(sql);

			if (isInsert(sql)) {
				return columnsKeyOfInsert(sql);
			}
			if (isUpate(sql) || isDelete(sql)) {
				return columnsKeyOfUpdate(sql);
			}

			return null;
		}

		/**
		 * 对更新SQL语句文本中的参数键字段进行拆分，以数组形式返回。
		 * 
		 * <li><code>update user set id=?,username=? where id=?</code>，则返回<code>["id","username","id"]</code></li>
		 * 
		 * @param sql
		 *            SQL语句
		 * 
		 * @return SQL语句参数键数组
		 */
		private String[] columnsKeyOfUpdate(String sql) {
			int len = countCharacter(sql, "?");
			String[] rsColumns = new String[len];

			String[] columns = sql.split("\\?");
			for (int i = 0; i < len; i++) {
				String column = columns[i];
				int index = column.lastIndexOf("=");
				if (index >= 0) {
					rsColumns[i] = getColumnKey(column, 1);
				}

				index = column.toUpperCase().lastIndexOf("IN");
				if (index >= 0) {
					rsColumns[i] = rsColumns[i] = getColumnKey(column, 4);
				}
			}

			return rsColumns;
		}

		/**
		 * 对插入SQL语句文本中的参数键字段进行拆分，以数组形式返回。
		 * 
		 * <li><code>insert into user (id,username) values(?,?)</code>，则返回<code>["id","username"]</code></li>
		 * 
		 * @param sql
		 *            SQL语句
		 * 
		 * @return SQL语句参数键数组
		 */
		private String[] columnsKeyOfInsert(String sql) {
			String columns = sql.substring(sql.indexOf("(") + 1, sql
					.indexOf(")"));
			return columns.replaceAll(" ", "").split(",");
		}

		/**
		 * 对SQL文本进行键值拆分。如user_name=?，则返回user_name。
		 * 
		 * @param text
		 *            SQL文本
		 * 
		 * @return SQL语句参数键
		 */
		private String getColumnKey(String text, int endindex) {
			int index = text.lastIndexOf(" ");
			if (endindex < 4) {
				if (index >= 0) {
					return text.substring(index + 1, text.length() - endindex);
				}

				index = text.lastIndexOf(",");
				if (index >= 0) {
					return text.substring(index + 1, text.length() - endindex);
				}
			} else {
				text = text.substring(0, text.lastIndexOf(" "));
				index = text.lastIndexOf(" ");
				return text.substring(index + 1);
			}

			return text;
		}

		/**
		 * 对文本字段进行转换。从Java
		 * Bean字段转换成数据库列字段格式(userName转换成user_name)，或者从数据库列字段转换成Java
		 * Bean字段格式(user_name/username转换成userName)。转换的规则根据属性值<code>JdbcUtils.getRule()</code>定义。
		 * 
		 * </br></br> 将Java Bean字段转换成数据库列字段(userName转换成user_name)，定义如下：
		 * <li>设置命名规则为分段命名规则，可通过构造参数传递。如<code>JdbcUtils.setRule(JdbcUtils.SEGMENTATION)</code></li>
		 * <li>设置转换的目的地为数据库，如<code>SqlProcessor.filter(text,'database')</code></li>
		 * 
		 * </br></br> 将数据库列字段转换成Java
		 * Bean字段(user_name/username转换成userName)，定义如下：
		 * <li>设置命名规则为分段命名规则，可通过构造参数传递。如<code>JdbcUtils.setRule(JdbcUtils.HUMP)</code></li>
		 * <li>设置转换的目的地为Java Bean，如<code>SqlProcessor.filter(text,'bean')</code></li>
		 * 
		 * @param text
		 *            文本字段
		 * @param toType
		 *            转换类型(bean || database)
		 * 
		 * @return 转换后的文本字段
		 * 
		 * @throws SQLException
		 * 
		 * @see JdbcUtils#HUMP
		 * @see JdbcUtils#SEGMENTATION
		 * @see JdbcUtils#setRule(String)
		 * @see JdbcUtils#getRule()
		 * @see JdbcUtils#TOTYPE
		 */
		public String convert(String text, String toType) throws SQLException {
			if (HUMP.equals(getRule())) {
				if (isAllCaps(text)) {
					if (toType.equals(TOTYPE[0])) {
						return beanPro.convertedBeanField(text);
					} else {

					}
				} else {
					return text;
				}
			}

			if (SEGMENTATION.equals(getRule())) {
				if (isAllCaps(text)) {
					if (toType.equals(TOTYPE[0])) {
						return beanPro
								.convertedBeanField(removeSeparator(text));
					} else {

					}
				} else {
					if (toType.equals(TOTYPE[1])) {
						return sqlPro.convertedIntoSegmentation(text);
					}
					return sqlPro.convertedIntoHump(text);
				}
			}

			return text;
		}

		/**
		 * 判断文本是否全部大写
		 * 
		 * @param text
		 *            文本
		 * 
		 * @return true(文本全为大写) || false(文本不全为大写)
		 */
		public boolean isAllCaps(String text) {
			if (text.equals(text.toUpperCase())) {
				return true;
			}
			return false;
		}

		/**
		 * 将文本转换成驼峰命名规则。如：user_name => userName
		 * 
		 * @param text
		 *            文本字段
		 * 
		 * @return 无分隔线，按照驼峰命名法的文本字段，如userName
		 * 
		 * @see JdbcUtils#HUMP
		 * @see JdbcUtils#SEGMENTATION
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
						String firstLetter = oldvalue.substring(0, 1)
								.toUpperCase();
						humpname.append(firstLetter + oldvalue.substring(1));
					}
				}
			}

			return humpname.toString();
		}

		/**
		 * 将文本转换成分段命名规则。如：userName => user_name
		 * 
		 * @param text
		 *            文本字段
		 * 
		 * @return 有分隔线的文本字段，如user_name
		 * 
		 * @see JdbcUtils#HUMP
		 * @see JdbcUtils#SEGMENTATION
		 */
		public String convertedIntoSegmentation(String text) {

			StringBuffer sb = new StringBuffer();
			int cacheIndex = 0;
			int count = 0;

			Pattern p = Pattern.compile("[A-Z]");
			Matcher m = p.matcher(text);
			while (m.find()) {
				String value = text.substring(cacheIndex, m.start());
				if (value == null || value.equals("")) {
					continue;
				}
				sb.append(value);
				sb.append("_");
				cacheIndex = m.start();
				count++;
			}
			sb.append(text.substring(cacheIndex));

			return sb.toString().toLowerCase();
		}

		/**
		 * 删除文本字段的分隔线。如user_name => username
		 * 
		 * @param text
		 *            文本字段
		 * 
		 * @return 无分隔线的文本字段，如userName
		 */
		private String removeSeparator(String text) {
			StringTokenizer st = new StringTokenizer(text, "_");
			int len = st.countTokens();
			StringBuffer buff = new StringBuffer();
			for (int i = 0; i < len; i++) {
				buff.append(st.nextToken());
			}

			return buff.toString();
		}

		/**
		 * 对SQL进行标准格式化。在JdbcUtils中所能解析的SQL规则称之为标准规则。
		 * 
		 * @param sql
		 *            SQL语句
		 * 
		 * @return 标准格式化后SQL
		 */
		public String standardFormatting(String sql) {

			sql = removeSpaces(sql);
			StringBuffer sb = new StringBuffer();
			String[] columns = splitSql(sql);

			int len = columns.length;
			for (int i = 0; i < len; i++) {
				String column = columns[i];

				sb.append(column);
				if (i == len - 1) {
					break;
				}

				String nextColumn = columns[i + 1];
				if ("=".equals(nextColumn)) {
					sb.append(nextColumn);
					sb.append(columns[i + 2]);
					i = i + 2;
				}

				if (equalsParams(column, nextColumn)) {
					sb.append(nextColumn);
					i = i + 1;
				}

				if (notEqualsParams(nextColumn) && !columns[i + 1].equals(")")) {
					sb.append(" ");
				}
			}

			return sb.toString();
		}

		/**
		 * 判断数据库列字段与下一个数据库列字段不等于指定某规则。规则为<code>SqlProcessor.notequalsparams</code>
		 * 
		 * @param columnName
		 *            数据库列名称
		 * 
		 * @return true(不等于指定某规则),false(等于指定某规则)
		 * 
		 * @see SqlProcessor#notequalsparams
		 */
		public boolean notEqualsParams(String columnName) {
			for (int i = 0; i < notequalsparams.length; i++) {
				if (notequalsparams[i].equalsIgnoreCase(columnName)) {
					return false;
				}
			}
			return true;
		}

		/**
		 * 判断数据库列字段与下一个数据库列字段是否与相等于某规则。规则为<code>SqlProcessor.equalsparams</code>
		 * 
		 * @param columnName
		 *            数据库列名称
		 * @param nextColumnName
		 *            数据库列名称(下一个)
		 * 
		 * @return true(等于指定某规则),false(不等于指定某规则)
		 * 
		 * @see SqlProcessor#equalsparams
		 */
		public boolean equalsParams(String columnName, String nextColumnName) {
			for (int i = 0; i < equalsparams.length; i++) {
				if (equalsparams[i].equalsIgnoreCase(nextColumnName)) {
					return true;
				}
			}
			if ((columnName.length() - 1) == columnName.indexOf("=")) {
				return true;
			}
			return false;
		}

		/**
		 * 对SQL文本按一个空格进行拆分
		 * 
		 * @param sql
		 *            SQL语句
		 * 
		 * @return sql数组
		 */
		private String[] splitSql(String sql) {
			StringTokenizer stk = new StringTokenizer(sql, " ");
			int len = stk.countTokens();
			String[] sqlArray = new String[len];
			for (int i = 0; i < len; i++) {
				sqlArray[i] = stk.nextToken();
			}
			return sqlArray;
		}

		/**
		 * 将SQL文本中出现两次及以上的空格转换成一个空格
		 * 
		 * @param sql
		 *            SQL语句
		 * 
		 * @return SQL语句
		 */
		public String removeSpaces(String sql) {
			StringBuffer sb = new StringBuffer();
			StringTokenizer sk = new StringTokenizer(sql, " ");
			int count = sk.countTokens();
			for (int i = 0; i < count; i++) {
				sb.append(sk.nextToken());
				sb.append(" ");
			}
			return sb.toString();
		}

		/**
		 * 取得数据映射模版，与数据库结果集的数据表相对应。
		 * 
		 * <p>
		 * 该数据映射模版作用于Java Bean与SQL之间的转换。
		 * SQL的查询字段或更新字段、插入字段该dataMappingClass(Java Bean)映射模版中取得。
		 * 可在dataMappingClass(Java Bean)中增加过滤方法，该过滤方法返回<code>Map</code>类型数据。
		 * </p>
		 */
		public Class getDataMappingClass() {
			return _dataMappingClass;
		}

		/**
		 * 设置数据映射模版，与数据库结果集的数据表相对应。
		 * 
		 * @param dataMappingClass
		 *            该数据映射模版作用于Java Bean与SQL之间的转换。
		 *            SQL的查询字段或更新字段、插入字段该dataMappingClass(Java Bean)映射模版中取得。
		 *            可在dataMappingClass(Java Bean)中增加过滤方法，该过滤方法返回<code>Map</code>类型数据。
		 */
		public void setDataMappingClass(Class dataMappingClass) {
			_dataMappingClass = dataMappingClass;
		}
	}

	/**
	 * SQL统计处理
	 * 
	 * @User: 魔力猫咪<code>(http://wlmouse.iteye.com/category/60230)</code>
	 */
	class SqlStatisticsProcessor {

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

	public static void main(String[] args) throws SQLException {

		JdbcUtils db = new JdbcUtils(ConfigDevice.class, JdbcUtils.SEGMENTATION);
		// System.out.println(db.sqlPro.makeSelectSql("where id=111"));
		// System.out.println(db.sqlPro.makeDeleteSql());
		// System.out.println(db.sqlPro.makeUpdateSql());
		// System.out.println(db.sqlPro.makeInsertSql(JdbcUtils.MYSQL, null));

		String sql = "SELECT device_ename, device_factory, device_ip, device_type, id FROM nhwm_config_device where id=111";
		System.out.println(db.statPro.count(sql));
	}

}

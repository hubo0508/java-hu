package org.db.jdbcutils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.db.jdbcutils.sql.SqlStatement;
import org.db.jdbcutils.utils.Constant;

import test.pojo.ConfigDevice;

/**
 * 数据库底层工具类
 * 
 * <li>封装了常用的增、删、查、改；</li>
 * <li>该版本适用于jdk1.4+以上，jdk1.4以下未测试过；</li>
 * <li>部分代码采用或参照apache dbutils项目，项目网址为<code>http://commons.apache.org/dbutils/</code>；</li>
 * <li>操作底层可为纯SQL，适用习惯于写SQL的开发人员；</li>
 * <li>操作底层可为对象，适用习惯用Hibernate操作的开发人员，于Hiberante类似80%；</li>
 * <li>操作底层为对象时，可以指定SQL字段的命名方式，如userName或user_name；</li>
 * <li>可为不同数据之间的简单兼容进行处理；Insert的主键维护机制(sqlserver未实现)、分页构造机制(sqlserver未实现)；</li>
 * <li>自动生成SQL语句时，增加过滤条件；</li>
 * <li>目前版本对sqlserver的API不支持。</li>
 * 
 * </br></br>
 * 
 * 异常代码说明：
 * 
 * @User: hubo.0508@gmail.com
 * @Date Apr 20, 2012
 * @Time 10:43:09 AM
 * @since 0.1：创建；0.3：增加分页兼容
 */
public class JdbcUtils {

	/**
	 * 日志输出
	 */
	Logger log = Logger.getLogger(JdbcUtils.class);

	/**
	 * Java Bean 字段命名与数据库字段命名的方式为：驼峰命名法。如：userName；可通过 API
	 * <code>JdbcUtils.setRule(String)</code>设置工具(JdbcUtils)类的命名规则，工具(JdbcUtils)类默认的规则为<code>JdbcUtils.SEGMENTATION</code>
	 * 
	 * @see JdbcUtils#setRule(String)
	 */
	public final static String HUMP = "hump";

	/**
	 * Java Bean 字段命名与数据库字段命名的方式为：分段名法。如：user_name；可通过 API
	 * <code>JdbcUtils.setRule(String)</code>设置工具(JdbcUtils)类的命名规则，工具(JdbcUtils)类默认的规则为<code>JdbcUtils.SEGMENTATION</code>
	 * 
	 * @see JdbcUtils#setRule(String)
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
	 * 数据库字段与Java Bean字段的命名规则，默认为驼峰命名法(<code>JdbcUtils.SEGMENTATION</code>)
	 * 
	 * @see JdbcUtils#HUMP
	 * @see JdbcUtils#SEGMENTATION
	 */
	private String rule = SEGMENTATION;

	/**
	 * Java Bean 与 SQL 映射模版（自动构造SQL时）、返回数据的映射模版（查询数据库结果集映谢到Java
	 * Bean或其它Java类型），同查询数据库结果集的数据表相对应
	 * 
	 * <p>
	 * 该数据映射模版作用于Java
	 * Bean与SQL之间的转换、数据库结果集的数据表与该数据映射模版之间值的设置。SQL的查询字段或更新字段、插入字段从方法<code>JdbcUtils.getDataMappingClass()</code>中取得映射模版。
	 * 当<code>JdbcUtils.dataMappingClass</code>的类型为Java
	 * Bean时，可在类中增加过滤方法，该过滤方法直接作用于SQL的自动构造。方法定义为<code>public Map sqlFilter(){}</code>，也可以通过<code>JdbcUtils.setSqlFilter(Map)</code>方法设置数据映射模版。
	 * 该数据映射模版<code>JdbcUtils.dataMappingClass</code>类型可为（目前API只实现了对这些类型的支持）：
	 * 
	 * <li>{JavaBean}.class</li>
	 * <li>Map.class/HashMap.class/LinkedHashMap.class</li>
	 * <li>List.class/ListArray.class</li>
	 * <li>Integer.class 或其它基本数据类型</li>
	 * 
	 * </br></br>当<code>JdbcUtils.dataMappingClass</code>类型是<code>Map.class/HashMap.class/LinkedHashMap.class/List.class/ArrayList.class/</code>（目前API只实现了对这些类型的支持）基本数据类型时。
	 * 此时与数据库结果集的数据表相对应、自动构造SQL时对应都不成立，必须手动写SQL语句。为了可能自动构造SQL及数据映射，可调用SDK<code>JdbcUtils.setSqlMappingClass(Class)</code>来实现自动的SQL与返回数据映谢。
	 * </p>
	 * 
	 * @see JdbcUtils#setSqlMappingClass(Class)
	 * @see JdbcUtils#getSqlFilter()
	 * @see JdbcUtils#setSqlFilter(Map)
	 */
	private Class dataMappingClass;

	/**
	 * Java Bean 与 SQL 映射模版
	 * 
	 * <p>
	 * 当<code>JdbcUtils.getDataMappingClass()</code>的映射类型不为Java
	 * Bean模版时，可对该变量设置数据映射模版，新设置的数据映射模版只会作用于与SQL、返回数据之间的关系。
	 * </p>
	 * 
	 * <p>
	 * 在通过<code>JdbcUtils.setSqlMappingClass(Class)</code>设置新数据映射模版时，该值中也可以定义<code>public Map sqlFilter(){}</code>方法来设置与SQL之间的规则关系(Java
	 * Bean自动构造成SQL时的条件规则)。也可以通过<code>JdbcUtils.setSqlFilter(Map)</code>方法设置数据映射模版。
	 * </p>
	 * 
	 * @see JdbcUtils#getDataMappingClass()
	 * @see JdbcUtils#setSqlFilter(Map)
	 */
	private Class sqlMappingClass;

	/**
	 * 自动构造SQL语句的过滤规则
	 * 
	 * <p>
	 * 在对数据执行增、删、改、查时，使用对象Java Bean自动构造SQL API时，过虑条伯可以影响自动构造出的SQL结果。 在Java
	 * Bean中定义了<code>public Map sqlFilter(){}</code>方法，会影响到执行增、删、改、查动作时，根据映射模版<code>JdbcUtils.setDataMappingClass(Class)</code>或<code>JdbcUtils.setSqlMappingClass(Class)</code>自动构造的SQL语句。
	 * 当在执行增、删、改、查动作时，单独的设置<code>JdbcUtils.setSqlFilter(Map)</code>只会作用于当前动作。
	 * </p>
	 * 
	 * <blockquote>
	 * 
	 * <pre>
	 * public class User{
	 *   private Integer id;
	 *   private String userName;
	 *   private String password;
	 *   private Integer hasData;
	 *   
	 *   public Map sqlFilter(){
	 *     Map filter = new HashMap();
	 *     filter.put(&quot;User&quot;, &quot;SimpleUser&quot;);
	 *     filter.put(&quot;hasData&quot;, new Boolean(false));
	 *     return filter;
	 *   }
	 *   .....
	 * }
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * 如上Java Bean User.java所示，在类中定义了方法<code>public Map sqlFilter(){}</code>，该方法返回一个Map集合。
	 * 下面我们通过实际的例子讲解：当正情况下未定义<code>public Map sqlFilter(){}</code>方法时，产生的查询SQL为：
	 * <code>SELECT id, user_name, password, has_data FROM USER</code>。此时我们将<code>public Map sqlFilter(){}</code>方法加上，再次自动构造的SQL为：
	 * <code>SELECT id, user_name, password FROM simple_user</code>。两条SQL语句对比可发现，SQL表名user转换成了simple_user，而has_data字段在第二个SQL语句中无查询字段。
	 * 也就是说定义的<code>public Map sqlFilter(){}</code>方法影响到了最终自动构造成的SQL。
	 * 
	 * <p>
	 * 现有规则如下(现SDK只实现了以下规则)：
	 * <li><code>filter.put("User", "SimpleUser");</code>。当键Key的值为Java
	 * Bean类名或字段名称时，Value设置一个有效字符串，SDK在解析时会认为这是一个字符的替换规则；</li>
	 * <li><code>filter.put("hasData", new Boolean(false或true));</code>。当键Key的值为Java
	 * Bean字段名称时，Value设置一个有效的<code>Boolean</code>布尔值，SDK在解析时会认为这是一个字符显示与不显示规则；</li>
	 * </p>
	 * 
	 * @see JdbcUtils#setDataMappingClass(Class)
	 * @see JdbcUtils#setSqlMappingClass(Class)
	 */
	private Map sqlFilter;

	/**
	 * Java Bean主键字符，默认为“id”。
	 */
	private String primaryKey = "id";

	/**
	 * SQL语句中包含有查询参数"?"号，执行增、删、改、查动作时是否检查参数个数与参数值相等。
	 */
	private volatile boolean pmdKnownBroken = false;

	/*
	 * Java Bean 与 Database 的类型(私有)
	 */
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

	/**
	 * 分页参数Java Bean
	 * 
	 * <p>
	 * 当调用<code>ju.queryResultTo(args,args)</code>前，设置了值<code>ju.setPage(new Page(new Page(1, 2)))</code>，在查询数据时将会进行分页查询
	 * <blockquote>
	 * 
	 * <pre>
	 * JdbcUtils ju = new JdbcUtils(User.class, new Page(1, 2), JdbcUtils.MYSQL);
	 * ju.setPage(new Page(new Page(1, 2)));
	 * Page page = (Page) db.queryResultTo(con, new ArrayList());//分页Java Bean
	 * List result = (List) page.getResult();//结果数据
	 * 
	 * System.out.println(&quot;当前页：&quot; + page.getThisPage());
	 * System.out.println(&quot;下一页：&quot; + page.getPageNext());
	 * System.out.println(&quot;上一页：&quot; + page.getPagePrev());
	 * System.out.println(&quot;尾  页：&quot; + page.getPageLast());
	 * System.out.println(&quot;总页数：&quot; + page.getTotalPage());
	 * System.out.println(&quot;总行数：&quot; + page.getTotalCount());
	 * </pre>
	 * 
	 * </blockquote>
	 * </p>
	 */
	private Page page = null;

	/**
	 * 数据库类型，该类型直接影响到自动构造的Insert语句，可设置类型有：<code>JdbcUtils.MYSQL、JdbcUtils.ORACLE、Jdbcutils.SQLSERVER</code>
	 */
	private String database;

	// ////////////////////////构造函数START/////////////////////////////////////////////////////////////////
	/**
	 * 构造函数
	 * 
	 * @param dataMappingClass
	 *            Java Bean 与 SQL 映射模版（自动构造SQL时）、返回数据的映射模版（查询数据库结果集映谢到Java
	 *            Bean或其它Java类型），同查询数据库结果集的数据表相对应
	 * 
	 * @throws 如果
	 *             <code>Class dataMappingClass == null</code>，抛出异常(DATA_MAPPING_CLASS_ERROR)
	 * 
	 * @see JdbcUtils#getDataMappingClass()
	 */
	public JdbcUtils(Class dataMappingClass) {
		this.setDataMappingClass(dataMappingClass);
	}

	/**
	 * @see JdbcUtils#setDataMappingClass(Class)
	 * @see JdbcUtils#setPage(Page)
	 */
	public JdbcUtils(Class dataMappingClass, Page page) {
		this(dataMappingClass);
		this.setPage(page);
	}

	/**
	 * @see JdbcUtils#setDataMappingClass(Class)
	 * @see JdbcUtils#setPage(Page)
	 * @see JdbcUtils#setDatabase(String)
	 */
	public JdbcUtils(Class dataMappingClass, Page page, String database) {
		this(dataMappingClass);
		this.setPage(page);
		this.setDatabase(database);
	}

	/**
	 * 构造函数
	 * 
	 * @param dataMappingClass
	 *            Java Bean 与 SQL 映射模版（自动构造SQL时）、返回数据的映射模版（查询数据库结果集映谢到Java
	 *            Bean或其它Java类型），同查询数据库结果集的数据表相对应
	 * @param rule
	 *            数据库字段与Java Bean字段的命名规则，默认为驼峰命名法(<code>JdbcUtils.HUMP</code>)
	 * @throws 如果
	 *             <code>Class dataMappingClass == null</code>，抛出异常(DATA_MAPPING_CLASS_ERROR)
	 * 
	 * @see JdbcUtils#getRule()
	 * @see JdbcUtils#HUMP
	 * @see JdbcUtils#SEGMENTATION
	 * @see JdbcUtils#getDataMappingClass()
	 */
	public JdbcUtils(Class dataMappingClass, String rule) {
		this.setDataMappingClass(dataMappingClass);
		this.setRule(rule);
	}

	public JdbcUtils(Class dataMappingClass, String rule, Page page) {
		this(dataMappingClass, rule);
		this.setPage(page);
	}

	public JdbcUtils(Class dataMappingClass, String rule, Page page,
			String database) {
		this(dataMappingClass, rule);
		this.setPage(page);
		this.setDatabase(database);
	}

	// ////////////////////////构造函数END/////////////////////////////////////////////////////////////////

	/**
	 * 无参数查询数据，SQL自动构造。根据映射模版<code>JdbcUtils.getDataMappingClass()</code>或<code>JdbcUtils#getSqlMappingClass()</code>自动产生SQL语句。可对映射模版增加过滤条件(<code>JdbcUtils.setSqlFilter(Map)/public Map sqlFilter(){}</code>)。
	 * 
	 * @param conn
	 *            数据库连接对象
	 * @param instanceCollection
	 *            查询返回数据集类型(List\Map\Set)
	 * @return ArrayList
	 * @exception
	 *         <li>数据库连接对象Connection为NULL时，抛出SQLException异常(CONNECTION_ERROR)</li>
	 *         <li>vk其它SQLException则为非功能性SQLException异常；</li>
	 * 
	 * @see JdbcUtils#getDataMappingClass()
	 * @see JdbcUtils#getSqlMappingClass()
	 * @see JdbcUtils#setSqlMappingClass(Class)
	 * @see JdbcUtils#setSqlFilter(Map)
	 * @see JdbcUtils#getSqlFilter()
	 */
	public Object queryResultTo(Connection conn, Object instanceCollection)
			throws SQLException {
		return query(conn, sqlPro.makeSelectSql(), null, instanceCollection);
	}

	/**
	 * 无参数查询数据，SQL半自动构造。根据映射模版<code>JdbcUtils.getDataMappingClass()</code>或<code>JdbcUtils#getSqlMappingClass()</code>自动产生SQL语句。可对映射模版增加过滤条件(<code>JdbcUtils.setSqlFilter(Map)/public Map sqlFilter(){}</code>)。
	 * 
	 * <li>sqlOrWhereIf=“<code>select * from user</code>”时，SDK
	 * API不作什么解析与附加过滤条件；</li>
	 * <li>sqlOrWhereIf=“<code>where name=“张三”</code>”时，在SDK
	 * API构造的SQL语句后增加条件，<code>select id,user_name,password from user where name=?</code>其中where之前SQL语句根据映射模版自动构造。</li>
	 * 
	 * @param conn
	 *            数据库连接对象
	 * @param sqlOrWhereIf
	 *            SQL查询语句或查询条件
	 * @return ArrayList
	 * @exception
	 *         <li><code>String sqlOrWhereIf</code>参数值开头不包含SELECT或WHERE时，抛出SQLException异常(SQL_STATEMENT_ERROR)；</li>
	 *         <li>数据库连接对象Connection为NULL时，抛出SQLException异常(CONNECTION_ERROR)</li>
	 *         <li>其它SQLException则为非功能性SQLException异常；</li>
	 * 
	 * @see JdbcUtils#getDataMappingClass()
	 * @see JdbcUtils#getSqlMappingClass()
	 * @see JdbcUtils#setSqlMappingClass(Class)
	 * @see JdbcUtils#setSqlFilter(Map)
	 * @see JdbcUtils#getSqlFilter()
	 */
	public Object queryResultTo(Connection conn, String sqlOrWhereIf,
			Object instanceCollection) throws SQLException {
		return queryResultTo(conn, sqlOrWhereIf, null, instanceCollection);
	}

	/**
	 * 有参数查询数据，SQL半自动构造。根据映射模版<code>JdbcUtils.getDataMappingClass()</code>或<code>JdbcUtils#getSqlMappingClass()</code>自动产生SQL语句。可对映射模版增加过滤条件(<code>JdbcUtils.setSqlFilter(Map)/public Map sqlFilter(){}</code>)。
	 * 
	 * <li>sqlOrWhereIf=“<code>select * from user</code>”时，SDK
	 * API不作什么解析与附加过滤条件；</li>
	 * <li>sqlOrWhereIf=“<code>where name=“张三”</code>”时，在SDK
	 * API构造的SQL语句后增加条件，<code>select id,user_name,password from user where name=?</code>其中where之前SQL语句根据映射模版自动构造。</li>
	 * 
	 * @param conn
	 *            数据库连接对象
	 * @param sqlOrWhereIf
	 *            SQL查询语句或查询条件
	 * @param params
	 *            查询参数
	 * @return ArrayList
	 * @exception
	 *         <li><code>String sqlOrWhereIf</code>语句开头不包含SELECT或WHERE时，抛出SQLException异常(SQL_STATEMENT_ERROR)；</li>
	 *         <li>数据库连接对象Connection为NULL时，抛出SQLException异常(CONNECTION_ERROR)</li>
	 *         <li>其它SQLException则为非功能性SQLException异常；</li>
	 * 
	 * @see JdbcUtils#getDataMappingClass()
	 * @see JdbcUtils#getSqlMappingClass()
	 * @see JdbcUtils#setSqlMappingClass(Class)
	 * @see JdbcUtils#setSqlFilter(Map)
	 * @see JdbcUtils#getSqlFilter()
	 */
	public Object queryResultTo(Connection conn, String sqlOrWhereIf,
			Object[] params, Object instanceCollection) throws SQLException {

		if (sqlOrWhereIf == null) {
			throw new SQLException("SQL_STATEMENT_ERROR:Null SQL statement");
		}

		if (!Constant.startsWithSelect(sqlOrWhereIf)) {
			sqlOrWhereIf = sqlPro.makeSelectSql(sqlOrWhereIf);
		}

		return query(conn, sqlOrWhereIf, params, instanceCollection);
	}

	/**
	 * 无参数查询唯一数据，SQL半自动构造。根据映射模版<code>JdbcUtils.getDataMappingClass()</code>或<code>JdbcUtils#getSqlMappingClass()</code>自动产生SQL语句。可对映射模版增加过滤条件(<code>JdbcUtils.setSqlFilter(Map)/public Map sqlFilter(){}</code>)。
	 * 
	 * <li>sqlOrWhereIf=“<code>select * from user where id=1</code>”时，SDK
	 * API不作什么解析与附加过滤条件；</li>
	 * <li>sqlOrWhereIf=“<code>where id=1</code>”时，在SDK API构造的SQL语句后增加条件，<code>select id,user_name,password from user where id=1</code>其中where之前SQL语句根据映射模版自动构造。</li>
	 * 
	 * @param conn
	 *            数据库连接对象
	 * @param sqlOrWhereIf
	 *            SQL查询语句或查询条件
	 * @return Object
	 * @exception
	 *         <li>查询结果集中超过两条数据将会抛出SQLException异常(RESULT_SIZE_ERROR)</li>
	 *         <li><code>String sqlOrWhereIf</code>参数值开头不包含SELECT或WHERE时，抛出SQLException异常(SQL_TYPES_ERROR)；</li>
	 *         <li>数据库连接对象Connection为NULL时，抛出SQLException异常(CONNECTION_NULL_ERROR)</li>
	 *         <li>其它SQLException则为非功能性SQLException异常；</li>
	 * 
	 * @see JdbcUtils#getDataMappingClass()
	 * @see JdbcUtils#getSqlMappingClass()
	 * @see JdbcUtils#setSqlMappingClass(Class)
	 * @see JdbcUtils#setSqlFilter(Map)
	 * @see JdbcUtils#getSqlFilter()
	 */
	public Object queryResultToUnique(Connection con, String sqlOrWhereIf)
			throws SQLException {
		return queryResultToUnique(con, sqlOrWhereIf, null);
	}

	/**
	 * 有参数查询唯一数据，SQL半自动构造。根据映射模版<code>JdbcUtils.getDataMappingClass()</code>或<code>JdbcUtils.getSqlMappingClass()</code>自动产生SQL语句。可对映射模版增加过滤条件(<code>JdbcUtils.setSqlFilter(Map)/public Map sqlFilter(){}</code>)。
	 * 
	 * <li>sqlOrWhereIf=“<code>select * from user where id=?</code>”时，SDK
	 * API不作什么解析与附加过滤条件；</li>
	 * <li>sqlOrWhereIf=“<code>where id=?</code>”时，在SDK API构造的SQL语句后增加条件，<code>select id,user_name,password from user where id=?</code>其中where之前SQL语句根据映射模版自动构造。</li>
	 * 
	 * @param conn
	 *            数据库连接对象
	 * @param sqlOrWhereIf
	 *            SQL查询语句或查询条件
	 * @param params
	 *            查询参数
	 * @return Object
	 * @exception
	 *         <li>查询结果集中超过两条数据将会抛出SQLException异常(RESULT_SIZE_ERROR)</li>
	 *         <li><code>String sqlOrWhereIf</code>参数值开头不包含SELECT或WHERE时，抛出SQLException异常(SQL_TYPES_ERROR)；</li>
	 *         <li>数据库连接对象Connection为NULL时，抛出SQLException异常(CONNECTION_NULL_ERROR)</li>
	 *         <li>其它SQLException则为非功能性SQLException异常；</li>
	 * 
	 * @see JdbcUtils#getDataMappingClass()
	 * @see JdbcUtils#getSqlMappingClass()
	 * @see JdbcUtils#setSqlMappingClass(Class)
	 * @see JdbcUtils#setSqlFilter(Map)
	 * @see JdbcUtils#getSqlFilter()
	 */
	public Object queryResultToUnique(Connection con, String sqlOrWhereIf,
			Object[] params) throws SQLException {
		if (!Constant.startsWithSelect(sqlOrWhereIf)) {
			sqlOrWhereIf = sqlPro.makeSelectSql(sqlOrWhereIf);
		}
		return this.query(con, sqlOrWhereIf, params, getDataMappingClass());
	}

	/**
	 * 查询数据
	 * 
	 * @param conn
	 *            数据库连接对象
	 * @param statement
	 *            查询SQL语句
	 * @param params
	 *            查询参数
	 * @param instanceCollectionOrClass
	 *            查询结果返回数据类型。可传<code>new ArrayList()、new HashMap()、new LinkedHashMap()</code>或Java基本类型如<code>Long.class</code>或Java
	 *            Bean<code>{JavaBean.class}</code>
	 * @return Object
	 * @exception
	 *         <li>参数<code>Object instanceCollectionOrClass</code>值为Java
	 *         Bean或Java 基础类型时，查询结果集中超过两条数据将会抛出SQLException异常(RESULT_SIZE_ERROR)</li>
	 *         <li><code>Connection conn==null</code>，抛出SQLException异常(CONNECTION_NULL_ERROR)；</li>
	 *         <li><code>String sql==null</code>，抛出SQLException异常(SQL_STATEMENT_NULL_ERROR)；</li>
	 *         <li><code>Object instanceCollectionOrClass==null</code>，抛出SQLException异常(RESULT_TYPE_NULL_ERROR)；</li>
	 *         <li>当查询SQL语句不是标准Sql时，抛出SQLException异常(DATA_MAPPING_CLASS_ERROR)；</li>
	 *         <li>其它SQLException则为非功能性SQLException异常；</li>
	 * 
	 * @see JdbcUtils#getDataMappingClass()
	 * @see JdbcUtils#getSqlMappingClass()
	 * @see JdbcUtils#setSqlMappingClass(Class)
	 * @see JdbcUtils#setSqlFilter(Map)
	 * @see JdbcUtils#getSqlFilter()
	 */
	public Object query(Connection conn, String statement, Object[] params,
			Object instanceCollectionOrClass) throws SQLException {

		if (conn == null) {
			throw new SQLException("CONNECTION_NULL_ERROR:Null connection");
		}

		if (statement == null) {
			throw new SQLException(
					"SQL_STATEMENT_NULL_ERROR:Null SQL statement");
		}

		if (instanceCollectionOrClass == null) {
			throw new SQLException("RESULT_TYPE_NULL_ERROR:Null result set");
		}

		if (!Constant.startsWithSelect(statement)) {
			throw new SQLException("SQL_TYPES_ERROR:SQL types do not match！");
		}

		if (this.getPage() != null) {
			SqlStatement stateSql = new SqlStatement(getDatabase());
			Object[] pageParams = params == null || params.length == 0 ? null
					: beanPro.mergerObject(params, getParamsObject(), database);
			params = params == null || params.length == 0 ? getParamsObject()
					: beanPro.mergerObject(params, getParamsObject(), database);

			Class temp = this.getDataMappingClass();
			this.setDataMappingClass(Long.class);
			long totalCount = Long.valueOf(
					queryResult(conn, stateSql.count(statement), pageParams,
							Long.class).toString()).longValue();
			this.setDataMappingClass(temp);

			return new Page(totalCount, getPage().getStartPage() + 1, getPage()
					.getPageSize(), queryResult(conn, stateSql
					.paging(statement), params, instanceCollectionOrClass));
		}

		return queryResult(conn, statement, params, instanceCollectionOrClass);
	}

	private Object queryResult(Connection conn, String statement,
			Object[] params, Object instanceCollectionOrClass)
			throws SQLException {

		log.info("SQL：" + statement);

		PreparedStatement stmt = null;
		ResultSet rs = null;
		Object result = null;
		try {
			stmt = this.prepareStatement(conn, statement);
			this.fillStatement(stmt, params);
			rs = stmt.executeQuery();
			result = rsPro.handle(rs, instanceCollectionOrClass);
		} catch (SQLException e) {
			this.rethrow(e, statement, params);
		} finally {
			try {
				close(rs, stmt);
			} catch (Exception e) {
				close(rs, stmt);
			}
		}

		return result;
	}

	private Object[] getParamsObject() {
		int start = getPage().getStartToDatabase(getDatabase());
		int end = getPage().getEndToDatabase(getDatabase());
		return new Object[] { new Integer(end), new Integer(start) };
	}

	// //////////////////////INSERT-BEGIN///////////////////////////////////////////////////////////////

	/**
	 * 以对象(Java Bean)形式在数据库插入一条数据，参数值由instanceDomain(Java Bean)自动映射，根据映射模版<code>JdbcUtils.getDataMappingClass()</code>自动产生SQL语句。
	 * 可对映射模版增加过滤条件(<code>JdbcUtils.setSqlFilter(Map)/public Map sqlFilter(){}</code>)。
	 * 
	 * <li><code>database=JdbcUtils.ORACLE,sequence="seq"</code>时，产生的SQL为：<code>INSERT INTO user(id,user_name,password) VALUES(seq.NEXTVAL, ?, ?)</code>；</li>
	 * <li><code>database=JdbcUtils.ORACLE,sequence=null</code>时，产生的SQL为：<code>INSERT INTO user(id,user_name,password) VALUES(?, ?, ?)</code>；</li>
	 * <li><code>database=JdbcUtils.MYSQL,sequence=JdbcUtils.MYSQL_SEQ</code>时，产生的SQL为：<code>INSERT INTO user(user_name,password) VALUES(?, ?)</code>；</li>
	 * <li><code>database=JdbcUtils.MYSQL,sequence=null</code>时，产生的SQL为：<code>INSERT INTO user(id,user_name,password) VALUES(?, ?, ?)</code>；</li>
	 * <li><code>database=JdbcUtils.SQLSERVER</code>，未增加API。</li>
	 * 
	 * @param conn
	 *            数据库连接对象
	 * @param instanceDomain
	 *            设置有值的Java Bean对象
	 * @param database
	 *            数据库类型(<code>JdbcUtils.ORACLE、JdbcUtils.MYSQL、JdbcUtils.SQLSERVER</code>)
	 * @param sequence
	 *            序列類型
	 * @return 影响的行数
	 * @throws SQLException
	 * 
	 * @see JdbcUtils#ORACLE
	 * @see JdbcUtils#MYSQL
	 * @see JdbcUtils#SQLSERVER
	 * @see JdbcUtils#MYSQL_SEQ
	 * @see JdbcUtils#setDataMappingClass(Class)
	 * @see JdbcUtils#getDataMappingClass()
	 * @see JdbcUtils#setSqlFilter(Map)
	 * @see JdbcUtils#getSqlFilter()
	 */
	public int insert(Connection conn, Object instanceDomain, String database,
			String sequence) throws SQLException {

		if (getDataMappingClass() == null) {
			setDataMappingClass(instanceDomain.getClass());
		}

		String sql = sqlPro.makeInsertSql(database, sequence);
		Object[] params = beanPro.objectArray(instanceDomain, sql, database,
				sequence);
		return execute(conn, sql, params);
	}

	/**
	 * 以Insert Sql语句形式在数据库插入一条数据，参数值由instanceDomain(Java Bean)自动映射，SQL语句通过外部传入。
	 * 
	 * <li><code>database=JdbcUtils.ORACLE,sequence="seq"</code>，SQL为<code>INSERT INTO user(id,user_name,password) VALUES(seq.NEXTVAL, ?, ?)</code>，Java
	 * Bean转化后的Object[]参数为：["张三","123"]</li>
	 * <li><code>database=JdbcUtils.ORACLE,sequence=null</code>，SQL为<code>INSERT INTO user(id,user_name,password) VALUES(?, ?, ?)</code>，Java
	 * Bean转化后的Object[]参数为：[1,"张三","123"]</li>
	 * <li><code>database=JdbcUtils.MYSQL,sequence=JdbcUtils.MYSQL_SEQ</code>，SQL为<code>INSERT INTO user(user_name,password) VALUES(?, ?)</code>，Java
	 * Bean转化后的Object[]参数为：["张三","123"]</li>
	 * <li><code>database=JdbcUtils.MYSQL,sequence=null</code>，SQL为：<code>INSERT INTO user(id,user_name,password) VALUES(?, ?, ?)</code>，Java
	 * Bean转化后的Object[]参数为：[1,"张三","123"]</li>
	 * <li><code>database=JdbcUtils.SQLSERVER</code>，未增加API。</li>
	 * 
	 * @param conn
	 *            数据库连接对象
	 * @param insertSql
	 *            Insert Sql语句
	 * @param instanceDomain
	 *            设置有值的Java Bean对象
	 * @param database
	 *            数据库类型(<code>JdbcUtils.ORACLE、JdbcUtils.MYSQL、JdbcUtils.SQLSERVER</code>)
	 * @param sequence
	 *            序列類型
	 * @return 影响的行数
	 * @throws SQLException
	 * 
	 * @see JdbcUtils#ORACLE
	 * @see JdbcUtils#MYSQL
	 * @see JdbcUtils#SQLSERVER
	 * @see JdbcUtils#MYSQL_SEQ
	 * @see JdbcUtils#setDataMappingClass(Class)
	 * @see JdbcUtils#getDataMappingClass()
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
	 * 以Insert Sql语句形式在数据库插入一条数据，参数值外部传入，SQL语句通过外部传入。
	 * 
	 * @param conn
	 *            数据库连接对象
	 * @param insertSql
	 *            Insert Sql语句
	 * @return 影响的行数
	 * @throws SQLException
	 */
	public int insert(Connection conn, String insertSql, Object[] params)
			throws SQLException {
		return execute(conn, insertSql, params);
	}

	/**
	 * 以对象(Java Bean)形式在数据库插入一条数据，参数值由外部提供，根据映射模版<code>JdbcUtils.getDataMappingClass()</code>自动产生SQL语句。
	 * 可对映射模版增加过滤条件(<code>JdbcUtils.setSqlFilter(Map)/public Map sqlFilter(){}</code>)。
	 * 
	 * <li><code>database=JdbcUtils.ORACLE,sequence="seq"</code>时，产生的SQL为：<code>INSERT INTO user(id,user_name,password) VALUES(seq.NEXTVAL, ?, ?)</code>；</li>
	 * <li><code>database=JdbcUtils.ORACLE,sequence=null</code>时，产生的SQL为：<code>INSERT INTO user(id,user_name,password) VALUES(?, ?, ?)</code>；</li>
	 * <li><code>database=JdbcUtils.MYSQL,sequence=JdbcUtils.MYSQL_SEQ</code>时，产生的SQL为：<code>INSERT INTO user(user_name,password) VALUES(?, ?)</code>；</li>
	 * <li><code>database=JdbcUtils.MYSQL,sequence=null</code>时，产生的SQL为：<code>INSERT INTO user(id,user_name,password) VALUES(?, ?, ?)</code>；</li>
	 * <li><code>database=JdbcUtils.SQLSERVER</code>，未增加API。</li>
	 * 
	 * @param conn
	 *            数据库连接对象
	 * @param instanceDomain
	 *            设置有值的Java Bean对象
	 * @param database
	 *            数据库类型(<code>JdbcUtils.ORACLE、JdbcUtils.MYSQL、JdbcUtils.SQLSERVER</code>)
	 * @param sequence
	 *            序列類型
	 * @return 影响的行数
	 * @throws SQLException
	 * 
	 * @see JdbcUtils#ORACLE
	 * @see JdbcUtils#MYSQL
	 * @see JdbcUtils#SQLSERVER
	 * @see JdbcUtils#MYSQL_SEQ
	 * @see JdbcUtils#setDataMappingClass(Class)
	 * @see JdbcUtils#getDataMappingClass()
	 * @see JdbcUtils#setSqlFilter(Map)
	 * @see JdbcUtils#getSqlFilter()
	 */
	public int insert(Connection conn, Object[] params, String database,
			String sequence) throws SQLException {
		return execute(conn, sqlPro.makeInsertSql(database, sequence), params);
	}

	// //////////////////////INSERT-END///////////////////////////////////////////////////////////////

	// //////////////////////UPDATE-BEGIN///////////////////////////////////////////////////////////////

	/**
	 * 以对象(Java Bean)形式在数据库更新一条数据，参数值由instanceDomain(Java Bean)自动映射，根据映射模版<code>JdbcUtils.getDataMappingClass()</code>自动产生SQL语句，在产生的Update
	 * Sql中自动增加<code>where id=?</code>。可对映射模版增加过滤条件(<code>JdbcUtils.setSqlFilter(Map)/public Map sqlFilter(){}</code>)。
	 * 
	 * @param conn
	 *            数据库连接对象
	 * @param instanceDomain
	 *            设置有值的Java Bean对象
	 * @return 影响的行数
	 * @throws SQLException
	 * 
	 * @see JdbcUtils#setDataMappingClass(Class)
	 * @see JdbcUtils#getDataMappingClass()
	 * @see JdbcUtils#setSqlFilter(Map)
	 * @see JdbcUtils#getSqlFilter()
	 */
	public int update(Connection conn, Object instanceDomain)
			throws SQLException {
		return update(conn, null, instanceDomain);
	}

	/**
	 * 1、当参数sqlOrWhereIf为where条件时(<code>where id=? and name=?</code>)，会以对象(Java
	 * Bean)形式在数据库更新一条数据，参数值由instanceDomain(Java Bean)自动映射，根据映射模版<code>JdbcUtils.getDataMappingClass()</code>自动产生SQL语句，在产生的Update
	 * Sql后追加自定义更新Sql条件<code>where id=? and name=?</code>。可对映射模版增加过滤条件(<code>JdbcUtils.setSqlFilter(Map)/public Map sqlFilter(){}</code>)。
	 * 
	 * </br></br> 2、当参数sqlOrWhereIf为整个Update Sql语句时(<code>update user set id=?,username=? where id=?</code>)，SDK
	 * API不作任何业务处理(SQL)，参数值由instanceDomain(Java Bean)自动映射。
	 * 
	 * 
	 * @param conn
	 *            数据库连接对象
	 * @param instanceDomain
	 *            设置有值的领域对象
	 * @param sqlOrWhereIf
	 *            SQL更新语句(<cdoe>update user set id=?,username=? where id=?</code>)或查询条件(<code>where
	 *            id=?</code>)
	 * @return 影响的行数
	 * @throws SQLException
	 * 
	 * @see JdbcUtils#setDataMappingClass(Class)
	 * @see JdbcUtils#getDataMappingClass()
	 * @see JdbcUtils#setSqlFilter(Map)
	 * @see JdbcUtils#getSqlFilter()
	 */
	public int update(Connection conn, String sqlOrWhereIf,
			Object instanceDomain) throws SQLException {
		if (!Constant.startsWithUpate(sqlOrWhereIf)) {
			sqlOrWhereIf = sqlPro.makeUpdateSql(sqlOrWhereIf);
		}
		Object[] params = beanPro.objectArray(instanceDomain, sqlOrWhereIf);

		return execute(conn, sqlOrWhereIf, params);
	}

	/**
	 * 以Update Sql语句形式在数据库中更新一条或多条数据，无参数映射。
	 * 
	 * @param conn
	 *            数据库连接对象
	 * @param sql
	 *            SQL更新语句
	 * @return 影响的行数
	 * @throws SQLException
	 */
	public int update(Connection conn, String sql) throws SQLException {
		return this.execute(conn, sql, null);
	}

	/**
	 * 以Update Sql语句形式在数据库中更新一条或多条数据，参数值通过外部传入。
	 * 
	 * @param conn
	 *            数据库连接对象
	 * @param sql
	 *            SQL更新语句
	 * @param params
	 *            更新参数
	 * @return 影响的行数
	 * @throws SQLException
	 */
	public int update(Connection conn, String sql, Object[] params)
			throws SQLException {
		return this.execute(conn, sql, params);
	}

	/**
	 * 以对象(Java Bean)形式在数据库更新一条数据，参数值由外部传入，根据映射模版<code>JdbcUtils.getDataMappingClass()</code>自动产生SQL语句，在产生的Update
	 * Sql中自动增加<code>where id=?</code>。可对映射模版增加过滤条件(<code>JdbcUtils.setSqlFilter(Map)/public Map sqlFilter(){}</code>)。
	 * 
	 * @param conn
	 *            数据库连接对象
	 * @param params
	 *            更新参数
	 * @return 影响的行数
	 * @throws SQLException
	 * 
	 * @see JdbcUtils#setDataMappingClass(Class)
	 * @see JdbcUtils#getDataMappingClass()
	 * @see JdbcUtils#setSqlFilter(Map)
	 * @see JdbcUtils#getSqlFilter()
	 */
	public int update(Connection conn, Object[] params) throws SQLException {
		return execute(conn, sqlPro.makeUpdateSql(), params);
	}

	// //////////////////////UPDATE-END///////////////////////////////////////////////////////////////

	// //////////////////////DELETE-BEGIN///////////////////////////////////////////////////////////////

	/**
	 * 以对象(Java Bean)形式在数据库删除一条数据，参数值由instanceDomain(Java Bean)自动映射，根据映射模版<code>JdbcUtils.getDataMappingClass()</code>自动产生SQL语句，在产生的Update
	 * Sql中自动增加<code>where id=?</code>。
	 * 
	 * @param conn
	 *            数据库连接对象
	 * @param instanceDomain
	 *            设置有值的Java Bean对象
	 * @return 影响的行数
	 * @throws SQLException
	 * 
	 * @see JdbcUtils#setDataMappingClass(Class)
	 * @see JdbcUtils#getDataMappingClass()
	 */
	public int delete(Connection conn, Object instanceDomain)
			throws SQLException {
		return delete(conn, sqlPro.makeDeleteSql(), instanceDomain);
	}

	/**
	 * 以对象(Java Bean)形式在数据库删除一条数据，参数值由外部传入，根据映射模版<code>JdbcUtils.getDataMappingClass()</code>自动产生SQL语句，在产生的Update
	 * Sql中自动增加<code>where id=?</code>。
	 * 
	 * @param conn
	 *            数据库连接对象
	 * @param params
	 *            删除参数
	 * @return 影响的行数
	 * @throws SQLException
	 * 
	 * @see JdbcUtils#setDataMappingClass(Class)
	 * @see JdbcUtils#getDataMappingClass()
	 */
	public int delete(Connection conn, Object[] params) throws SQLException {
		return execute(conn, sqlPro.makeDeleteSql(), params);
	}

	/**
	 * 以Delete Sql语句形式在数据库删除一条或多数据，无参数。
	 * 
	 * @param conn
	 *            数据库连接对象
	 * @param params
	 *            删除参数
	 * @return 影响的行数
	 * @exception 当SQL语句不是Delete语句时，抛出SQLException(SQL_TYPES_ERROR)。
	 */
	public int delete(Connection conn, String sql) throws SQLException {
		if (!Constant.startsWithDelete(sql)) {
			throw new SQLException("SQL_TYPES_ERROR:SQL types do not match！");
		}
		return execute(conn, sql, null);
	}

	/**
	 * 1、当参数sqlOrWhereIf为where条件时(<code>where id=? and name=?</code>)，会以对象(Java
	 * Bean)形式在数据库删除一条数据，参数值由外部传入，根据映射模版<code>JdbcUtils.getDataMappingClass()</code>自动产生SQL语句，在产生的Delete
	 * Sql后追加自定义更新Sql条件<code>where id=? and name=?</code>。
	 * 
	 * </br></br> 2、当参数sqlOrWhereIf为整个Delete Sql语句时(<code>DELETE FROM user WHERE id=?</code>)，SDK
	 * API不作任何业务处理(SQL)，参数值由外部传入。
	 * 
	 * 
	 * @param conn
	 *            数据库连接对象
	 * @param instanceDomain
	 *            设置有值的领域对象
	 * @param sqlOrWhereIf
	 *            SQL更新语句(<cdoe>DELETE FROM user WHERE id=?</code>)或查询条件(<code>WHERE
	 *            id=?</code>)
	 * @return 影响的行数
	 * @throws SQLException
	 * 
	 * @see JdbcUtils#setDataMappingClass(Class)
	 * @see JdbcUtils#getDataMappingClass()
	 */
	public int delete(Connection conn, String sqlOrWhereIf, Object[] params)
			throws SQLException {
		if (!Constant.startsWithDelete(sqlOrWhereIf)) {
			sqlOrWhereIf = sqlPro.makeDeleteSql(sqlOrWhereIf);
		}
		return execute(conn, sqlPro.makeDeleteSql(sqlOrWhereIf), params);
	}

	/**
	 * 1、当参数sqlOrWhereIf为where条件时(<code>where id=? and name=?</code>)，会以对象(Java
	 * Bean)形式在数据库删除一条数据，参数值由instanceDomain(Java Bean)自动映射，根据映射模版<code>JdbcUtils.getDataMappingClass()</code>自动产生SQL语句，在产生的Delete
	 * Sql后追加自定义更新Sql条件<code>where id=? and name=?</code>。
	 * 
	 * </br></br> 2、当参数sqlOrWhereIf为整个Delete Sql语句时(<code>DELETE FROM user WHERE id=?</code>)，SDK
	 * API不作任何业务处理(SQL)，参数值由instanceDomain(Java Bean)自动映射。
	 * 
	 * 
	 * @param conn
	 *            数据库连接对象
	 * @param instanceDomain
	 *            设置有值的领域对象
	 * @param sqlOrWhereIf
	 *            SQL更新语句(<cdoe>DELETE FROM user WHERE id=?</code>)或查询条件(<code>WHERE
	 *            id=?</code>)
	 * @return 影响的行数
	 * @throws SQLException
	 * 
	 * @see JdbcUtils#setDataMappingClass(Class)
	 * @see JdbcUtils#getDataMappingClass()
	 */
	public int delete(Connection conn, String sqlOrWhereIf,
			Object instanceDomain) throws SQLException {

		if (!Constant.startsWithDelete(sqlOrWhereIf)) {
			sqlOrWhereIf = sqlPro.makeDeleteSql(sqlOrWhereIf);
		}

		Object[] params = beanPro.objectArray(instanceDomain, sqlOrWhereIf);
		return execute(conn, sqlOrWhereIf, params);
	}

	// //////////////////////DELETE-END///////////////////////////////////////////////////////////////

	/**
	 * 数据的增、删、改操作。
	 * 
	 * @param conn
	 *            数据连接对象
	 * @param sql
	 *            执行SQL语句
	 * @param params
	 *            执行参数值
	 * @exception
	 *            <li><code>Connection conn==null</code>，抛出SQLException异常(CONNECTION_NULL_ERROR)；</li>
	 *            <li><code>String sql==null</code>，抛出SQLException异常(SQL_STATEMENT_NULL_ERROR)；</li>
	 */
	public int execute(Connection conn, String sql, Object[] params)
			throws SQLException {

		if (conn == null) {
			throw new SQLException("CONNECTION_NULL_ERROR:Null connection");
		}

		if (sql == null) {
			throw new SQLException(
					"SQL_STATEMENT_NULL_ERROR:Null SQL statement");
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
		Constant.close(rs, stmt);
	}

	/**
	 * 关闭连接
	 */
	public static void close(PreparedStatement stmt) {
		Constant.close(stmt);
	}

	/**
	 * 关闭连接
	 */
	public static void close(Connection conn) {
		Constant.close(conn);
	}

	/**
	 * 关闭连接
	 */
	public static void close(ResultSet rs) {
		Constant.close(rs);
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

		msg.append(" SQL: ");
		msg.append(sql);
		msg.append(" Parameters: ");

		if (params == null) {
			msg.append("[]");
		} else {
			msg.append(Constant.deepToString(params));
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
	 * 检查数据的唯一性
	 * 
	 * @param rs
	 *            数据库结果集的数据表
	 * 
	 * @exception
	 * <li>查询结果集中超过两条数据将会抛出SQLException异常(RESULT_SIZE_ERROR)</li>
	 */
	private void checkDataUnique(ResultSet rs) throws SQLException {
		if (rsPro.resultSize(rs) >= 2) {
			throw new SQLException(
					"RESULT_SIZE_ERROR：Result set is not unique!");
		}
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

	/**
	 * 取得Java Bean 与 SQL 映射模版（自动构造SQL时）、返回数据的映射模版（查询数据库结果集映谢到Java
	 * Bean或其它Java类型），同查询数据库结果集的数据表相对应
	 * 
	 * <p>
	 * 该数据映射模版作用于Java
	 * Bean与SQL之间的转换、数据库结果集的数据表与该数据映射模版之间值的设置。SQL的查询字段或更新字段、插入字段从方法<code>JdbcUtils.getDataMappingClass()</code>中取得映射模版。
	 * 当<code>JdbcUtils.dataMappingClass</code>的类型为Java
	 * Bean时，可在类中增加过滤方法，该过滤方法直接作用于SQL的自动构造。方法定义为<code>public Map sqlFilter(){}</code>，也可以通过<code>JdbcUtils.setSqlFilter(Map)</code>方法设置数据映射模版。
	 * 该数据映射模版<code>JdbcUtils.dataMappingClass</code>类型可为（目前API只实现了对这些类型的支持）：
	 * 
	 * <li>{JavaBean}.class</li>
	 * <li>Map.class/HashMap.class/LinkedHashMap.class</li>
	 * <li>List.class/ListArray.class</li>
	 * <li>Integer.class 或其它基本数据类型</li>
	 * 
	 * </br></br>当<code>JdbcUtils.dataMappingClass</code>类型是<code>Map.class/HashMap.class/LinkedHashMap.class/List.class/ListArray.class/</code>（目前API只实现了对这些类型的支持）基本数据类型时。
	 * 此时与数据库结果集的数据表相对应、自动构造SQL时对应都不成立，必须手动写SQL语句。为了可能自动构造SQL及数据映射，可调用SDK<code>JdbcUtils.setSqlMappingClass(Class)</code>来实现自动的SQL与返回数据映谢。
	 * </p>
	 * 
	 * @see JdbcUtils#setSqlMappingClass(Class)
	 * @see JdbcUtils#getSqlFilter()
	 */
	public Class getDataMappingClass() {
		return dataMappingClass;
	}

	/**
	 * 设置Java Bean 与 SQL 映射模版（自动构造SQL时）、返回数据的映射模版（查询数据库结果集映谢到Java
	 * Bean或其它Java类型），同查询数据库结果集的数据表相对应
	 * 
	 * <p>
	 * 该数据映射模版作用于Java
	 * Bean与SQL之间的转换、数据库结果集的数据表与该数据映射模版之间值的设置。SQL的查询字段或更新字段、插入字段从方法<code>JdbcUtils.getDataMappingClass()</code>中取得映射模版。
	 * 当<code>JdbcUtils.dataMappingClass</code>的类型为Java
	 * Bean时，可在类中增加过滤方法，该过滤方法直接作用于SQL的自动构造。方法定义为<code>public Map sqlFilter(){}</code>，也可以通过<code>JdbcUtils.setSqlFilter(Map)</code>方法设置数据映射模版。
	 * 该数据映射模版<code>JdbcUtils.dataMappingClass</code>类型可为（目前API只实现了对这些类型的支持）：
	 * 
	 * <li>{JavaBean}.class</li>
	 * <li>Map.class/HashMap.class/LinkedHashMap.class</li>
	 * <li>List.class/ListArray.class</li>
	 * <li>Integer.class 或其它基本数据类型</li>
	 * 
	 * </br></br>当<code>JdbcUtils.dataMappingClass</code>类型是<code>Map.class/HashMap.class/LinkedHashMap.class/List.class/ListArray.class/</code>（目前API只实现了对这些类型的支持）基本数据类型时。
	 * 此时与数据库结果集的数据表相对应、自动构造SQL时对应都不成立，必须手动写SQL语句。为了可能自动构造SQL及数据映射，可调用SDK<code>JdbcUtils.setSqlMappingClass(Class)</code>来实现自动的SQL与返回数据映谢。
	 * </p>
	 * 
	 * @exception
	 * <li><code>Class dataMappingClass == null</code>，抛出SQLException异常(DATA_MAPPING_CLASS_ERROR)；</li>
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
	 * 取得数据库字段与Java Bean字段的命名规则，默认为驼峰命名法(<code>JdbcUtils.HUMP</code>)
	 * 
	 * @see JdbcUtils#HUMP
	 * @see JdbcUtils#SEGMENTATION
	 */
	public String getRule() {
		return rule;
	}

	/**
	 * 设置数据库字段与Java Bean字段的命名规则，默认为驼峰命名法(<code>JdbcUtils.HUMP</code>)
	 * 
	 * @see JdbcUtils#HUMP
	 * @see JdbcUtils#SEGMENTATION
	 */
	public void setRule(String rule) {
		this.rule = rule;
	}

	/**
	 * 取得Java Bean主键字符，默认为“id”。
	 */
	public String getPrimaryKey() {
		return primaryKey;
	}

	/**
	 * 设置Java Bean主键字符，默认为“id”。
	 */
	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}

	/**
	 * 取得Java Bean 与 SQL 映射模版
	 * 
	 * <p>
	 * 当<code>JdbcUtils.getDataMappingClass()</code>的映射类型不为Java
	 * Bean模版时，可对该变量设置数据映射模版，新设置的数据映射模版只会作用于与SQL、返回数据之间的关系。
	 * </p>
	 * 
	 * <p>
	 * 在通过<code>JdbcUtils.setSqlMappingClass(Class)</code>设置新数据映射模版时，该值中也可以定义<code>public Map sqlFilter(){}</code>方法来设置与SQL之间的规则关系(Java
	 * Bean自动构造成SQL时的条件规则)。也可以通过<code>JdbcUtils.setSqlFilter(Map)</code>方法设置数据映射模版。
	 * </p>
	 * 
	 * @see JdbcUtils#getDataMappingClass()
	 * @see JdbcUtils#setSqlFilter(Map)
	 */
	public Class getSqlMappingClass() {
		return sqlMappingClass;
	}

	/**
	 * 设置Java Bean 与 SQL 映射模版
	 * 
	 * <p>
	 * 当<code>JdbcUtils.getDataMappingClass()</code>的映射类型不为Java
	 * Bean模版时，可对该变量设置数据映射模版，新设置的数据映射模版只会作用于与SQL、返回数据之间的关系。
	 * </p>
	 * 
	 * <p>
	 * 在通过<code>JdbcUtils.setSqlMappingClass(Class)</code>设置新数据映射模版时，该值中也可以定义<code>public Map sqlFilter(){}</code>方法来设置与SQL之间的规则关系(Java
	 * Bean自动构造成SQL时的条件规则)。也可以通过<code>JdbcUtils.setSqlFilter(Map)</code>方法设置数据映射模版。
	 * </p>
	 * 
	 * @see JdbcUtils#getDataMappingClass()
	 * @see JdbcUtils#setSqlFilter(Map)
	 */
	public void setSqlMappingClass(Class sqlMappingClass) {
		this.sqlMappingClass = sqlMappingClass;
		this.sqlPro.setDataMappingClass(sqlMappingClass);
		this.beanPro.setDataMappingClass(sqlMappingClass);
		this.setSqlFilter(this.beanPro.getSqlFilter(sqlMappingClass));
	}

	/**
	 * 取得自动构造SQL语句的过滤规则
	 * 
	 * <p>
	 * 在对数据执行增、删、改、查时，使用对象Java Bean自动构造SQL API时，改变量可以影响自动构造出的SQL结果。 在Java
	 * Bean中定义了<code>JdbcUtils.setSqlFilter(Map)</code>方法，会影响到所有执行增、删、改、查动作时，根据映射模版<code>JdbcUtils.setDataMappingClass(Class)</code>或<code>JdbcUtils.setSqlMappingClass(Class)</code>自动构造的SQL语句。
	 * 当在执行增、删、改、查动作时，每个SDK API临时调用<code>JdbcUtils.setSqlFilter(Map)</code>只会作用于当前动作。
	 * </p>
	 * 
	 * <p>
	 * <blockquote>
	 * 
	 * <pre>
	 * public class User{
	 *   private Integer id;
	 *   private String userName;
	 *   private String password;
	 *   private Integer hasData;
	 *   
	 *   public Map sqlFilter(){
	 *     Map filter = new HashMap();
	 *     filter.put(&quot;User&quot;, &quot;SimpleUser&quot;);
	 *     filter.put(&quot;hasData&quot;, new Boolean(false));
	 *     return filter;
	 *   }
	 *   .....
	 * }
	 * </pre>
	 * 
	 * </blockquote>
	 * </p>
	 * 
	 * 如上Java Bean User.java所示，在类中定义了全局方法<code>public Map sqlFilter(){}</code>，该方法返回一个Map集合。
	 * 下面我们通过实际的例子讲解：当正情况下未定义全局<code>public Map sqlFilter(){}</code>方法时，产生的查询SQL为：
	 * <code>SELECT id, user_name, password, has_data FROM USER</code>。此时我们将全局<code>public Map sqlFilter(){}</code>方法加上，再次自动构造的SQL为：
	 * <code>SELECT id, user_name, password FROM simple_user</code>。两条SQL语句对比可发现，SQL表名user转换成了simple_user，而has_data字段在第二个SQL语句中查询。
	 * 也就是说定义的全局<code>public Map sqlFilter(){}</code>方法影响到了最终自动构造成的SQL。
	 * 
	 * <p>
	 * 现有规则如下(现SDK只实现了以下规则)：
	 * <li><code>filter.put("User", "SimpleUser");</code>。当键Key的值为Java
	 * Bean类名或字段名称时，Value设置一个有效字符串，SDK在解析时会认为这是一个字符的替换规则；</li>
	 * <li><code>filter.put("hasData", new Boolean(false或true));</code>。当键Key的值为Java
	 * Bean字段名称时，Value设置一个有效的<code>Boolean</code>布尔值，SDK在解析时会认为这是一个字符显示与不显示规则；</li>
	 * </p>
	 * 
	 * @see JdbcUtils#setDataMappingClass(Class)
	 * @see JdbcUtils#setSqlMappingClass(Class)
	 */
	public Map getSqlFilter() {
		return sqlFilter;
	}

	/**
	 * 设置自动构造SQL语句的过滤规则
	 * 
	 * <p>
	 * 在对数据执行增、删、改、查时，使用对象Java Bean自动构造SQL API时，改变量可以影响自动构造出的SQL结果。 在Java
	 * Bean中定义了<code>JdbcUtils.setSqlFilter(Map)</code>方法，会影响到所有执行增、删、改、查动作时，根据映射模版<code>JdbcUtils.setDataMappingClass(Class)</code>或<code>JdbcUtils.setSqlMappingClass(Class)</code>自动构造的SQL语句。
	 * 当在执行增、删、改、查动作时，每个SDK API临时调用<code>JdbcUtils.setSqlFilter(Map)</code>只会作用于当前动作。
	 * </p>
	 * 
	 * <p>
	 * <blockquote>
	 * 
	 * <pre>
	 * public class User{
	 *   private Integer id;
	 *   private String userName;
	 *   private String password;
	 *   private Integer hasData;
	 *   
	 *   public Map sqlFilter(){
	 *     Map filter = new HashMap();
	 *     filter.put(&quot;User&quot;, &quot;SimpleUser&quot;);
	 *     filter.put(&quot;hasData&quot;, new Boolean(false));
	 *     return filter;
	 *   }
	 *   .....
	 * }
	 * </pre>
	 * 
	 * </blockquote>
	 * </p>
	 * 
	 * 如上Java Bean User.java所示，在类中定义了全局方法<code>public Map sqlFilter(){}</code>，该方法返回一个Map集合。
	 * 下面我们通过实际的例子讲解：当正情况下未定义全局<code>public Map sqlFilter(){}</code>方法时，产生的查询SQL为：
	 * <code>SELECT id, user_name, password, has_data FROM USER</code>。此时我们将全局<code>public Map sqlFilter(){}</code>方法加上，再次自动构造的SQL为：
	 * <code>SELECT id, user_name, password FROM simple_user</code>。两条SQL语句对比可发现，SQL表名user转换成了simple_user，而has_data字段在第二个SQL语句中查询。
	 * 也就是说定义的全局<code>public Map sqlFilter(){}</code>方法影响到了最终自动构造成的SQL。
	 * 
	 * <p>
	 * 现有规则如下(现SDK只实现了以下规则)：
	 * <li><code>filter.put("User", "SimpleUser");</code>。当键Key的值为Java
	 * Bean类名或字段名称时，Value设置一个有效字符串，SDK在解析时会认为这是一个字符的替换规则；</li>
	 * <li><code>filter.put("hasData", new Boolean(false或true));</code>。当键Key的值为Java
	 * Bean字段名称时，Value设置一个有效的<code>Boolean</code>布尔值，SDK在解析时会认为这是一个字符显示与不显示规则；</li>
	 * </p>
	 * 
	 * @see JdbcUtils#setDataMappingClass(Class)
	 * @see JdbcUtils#setSqlMappingClass(Class)
	 */
	public void setSqlFilter(Map sqlFilter) {
		this.sqlFilter = sqlFilter;
	}

	/**
	 * 取得数据库类型，该类型直接影响到自动构造的Insert语句，可设置类型有：<code>JdbcUtils.MYSQL、JdbcUtils.ORACLE、Jdbcutils.SQLSERVER</code>
	 */
	public String getDatabase() {
		return database;
	}

	/**
	 * 设置数据库类型，该类型直接影响到自动构造的Insert语句，可设置类型有：<code>JdbcUtils.MYSQL、JdbcUtils.ORACLE、Jdbcutils.SQLSERVER</code>
	 */
	public void setDatabase(String database) {
		this.database = database;
	}

	/**
	 * 取得分页参数Java Bean
	 * 
	 * <p>
	 * 当调用<code>ju.queryResultTo(args,args)</code>前，设置了值<code>ju.setPage(new Page(new Page(1, 2)))</code>，在查询数据时将会进行分页查询
	 * <blockquote>
	 * 
	 * <pre>
	 * JdbcUtils ju = new JdbcUtils(User.class, new Page(1, 2), JdbcUtils.MYSQL);
	 * ju.setPage(new Page(new Page(1, 2)));
	 * Page page = (Page) db.queryResultTo(con, new ArrayList());//分页Java Bean
	 * List result = (List) page.getResult();//结果数据
	 * 
	 * System.out.println(&quot;当前页：&quot; + page.getThisPage());
	 * System.out.println(&quot;下一页：&quot; + page.getPageNext());
	 * System.out.println(&quot;上一页：&quot; + page.getPagePrev());
	 * System.out.println(&quot;尾  页：&quot; + page.getPageLast());
	 * System.out.println(&quot;总页数：&quot; + page.getTotalPage());
	 * System.out.println(&quot;总行数：&quot; + page.getTotalCount());
	 * </pre>
	 * 
	 * </blockquote>
	 * </p>
	 */
	public Page getPage() {
		return page;
	}

	/**
	 * 设置分页参数Java Bean
	 * 
	 * <p>
	 * 当调用<code>ju.queryResultTo(args,args)</code>前，设置了值<code>ju.setPage(new Page(new Page(1, 2)))</code>，在查询数据时将会进行分页查询
	 * <blockquote>
	 * 
	 * <pre>
	 * JdbcUtils ju = new JdbcUtils(User.class, new Page(1, 2), JdbcUtils.MYSQL);
	 * ju.setPage(new Page(new Page(1, 2)));
	 * Page page = (Page) db.queryResultTo(con, new ArrayList());//分页Java Bean
	 * List result = (List) page.getResult();//结果数据
	 * 
	 * System.out.println(&quot;当前页：&quot; + page.getThisPage());
	 * System.out.println(&quot;下一页：&quot; + page.getPageNext());
	 * System.out.println(&quot;上一页：&quot; + page.getPagePrev());
	 * System.out.println(&quot;尾  页：&quot; + page.getPageLast());
	 * System.out.println(&quot;总页数：&quot; + page.getTotalPage());
	 * System.out.println(&quot;总行数：&quot; + page.getTotalCount());
	 * </pre>
	 * 
	 * </blockquote>
	 * </p>
	 */
	public void setPage(Page page) {
		this.page = page;
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
				throw new SQLException("RESULT_SET_NULL_ERROR:Null result set");
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

				if (Constant.isHashMap(getDataMappingClass())) {
					return rs.next() ? rsPro.toUniqueObject(new HashMap(), rs)
							: null;
				} else if (Constant.isLinkedHashMap(getDataMappingClass())) {
					return rs.next() ? rsPro.toUniqueObject(
							new LinkedHashMap(), rs) : null;
				} else if (Constant.isArrayList(getDataMappingClass())) {
					return rs.next() ? rsPro
							.toUniqueObject(new ArrayList(), rs) : null;
				} else if (Constant.isBasicType(getDataMappingClass())) {
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
		 * @return 总记录数
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
		 * @throws SQLException
		 * 
		 * @see ResultProcessor#getDataMappingClass()
		 */
		private Object toUniqueBiscType(ResultSet rs, Class clazz)
				throws SQLException {
			// ResultSetMetaData rsmd = rs.getMetaData();
			// int cols = rsmd.getColumnCount();
			// if (cols >= 2) {
			// throw new SQLException("Query column number greater than !");
			// }

			Object value = rs.getObject(1);
			if (BigDecimal.class.isAssignableFrom(value.getClass())) {
				return value;
			}
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
		 * @return Map/List/JavaBean数据集
		 * @throws SQLException
		 * 
		 * @see ResultProcessor#getDataMappingClass()
		 */
		private Object toUniqueObject(Object instanceObject, ResultSet rs)
				throws SQLException {

			ResultSetMetaData rsmd = rs.getMetaData();
			int cols = rsmd.getColumnCount();

			for (int i = 0; i < cols; i++) {
				String field = rsmd.getColumnName(i + 1);
				if (Constant.isMap(instanceObject.getClass())) {
					((Map) instanceObject).put(field, rs.getObject(field));
				} else if (Constant.isList(instanceObject.getClass())) {
					((List) instanceObject).add(rs.getObject(field));
				} else if (Constant.isBasicType(instanceObject.getClass())) {
					return rs.getObject(1);
				} else {
					PropertyDescriptor pro = beanPro.getProDescByName(sqlPro
							.convert(field, JdbcUtils.TOTYPE[0]));
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
		 * @return Map数据集
		 * @throws SQLException
		 * 
		 * @see ResultProcessor#getDataMappingClass()
		 */
		private Map toMap(Map rsh, ResultSet rs) throws SQLException {

			ResultSetMetaData rsmd = rs.getMetaData();
			int cols = rsmd.getColumnCount();

			for (int i = 1; i <= cols; i++) {
				if (Constant.isMap(getDataMappingClass())) {
					rsh.put(rsmd.getColumnName(i), rs.getObject(i));
				} else {
					rsh.put(sqlPro.convert(rsmd.getColumnName(i),
							JdbcUtils.TOTYPE[0]), rs.getObject(i));
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
		 * @return ArrayList数据集
		 * @throws SQLException
		 * 
		 * @see ResultProcessor#getDataMappingClass()
		 */
		private List toArrayList(ArrayList rsh, ResultSet rs)
				throws SQLException {

			Map sqlFilter = beanPro.getSqlFilter();

			ResultSetMetaData rsmd = rs.getMetaData();
			int cols = rsmd.getColumnCount();
			while (rs.next()) {
				if (Constant.isHashMap(getDataMappingClass())) {
					rsh.add(toMap(new HashMap(), rs));
				} else if (Constant.isLinkedHashMap(getDataMappingClass())) {
					rsh.add(toMap(new LinkedHashMap(), rs));
				} else {
					Object bean = beanPro.newInstance(getDataMappingClass());
					for (int i = 0; i < cols; i++) {
						String field = rsmd.getColumnName(i + 1);
						//System.out.println("field = " + field);
						String fValue = field;
						if (sqlFilter != null) {
							Object rValue = sqlFilter.get(field);

							if (rValue != null
									&& Boolean.class.isAssignableFrom(rValue
											.getClass())
									&& Boolean.valueOf(rValue.toString())
											.booleanValue() == false) {
								continue;
							}

							if (rValue != null
									&& String.class.isAssignableFrom(rValue
											.getClass())) {
								fValue = rValue.toString();
							}
						}
						String beanname = sqlPro.convert(fValue,
								JdbcUtils.TOTYPE[0]);
						checkBeanname(beanname, field);
						PropertyDescriptor pro = beanPro
								.getProDescByName(beanname);
						beanPro.callSetter(bean, pro, rs.getObject(field));
					}
					rsh.add(bean);
				}
			}

			return rsh;
		}

		private void checkBeanname(String beanname, String field)
				throws SQLException {
			if (beanname == null) {
				throw new SQLException("在'"
						+ Constant.getURI(getDataMappingClass())
						+ "'中无法找到与SQL查询语句的列名称'" + field + "'相匹配，无法动态设置值。");
			}
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
			return getSqlFilter(getDataMappingClass());
		}

		public Map getSqlFilter(Class dataMappingClazz) {
			try {
				Object obj = newInstance(dataMappingClazz);
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
		 * @return Object[] 参数数组
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
		 * @return Object[] 参数数组
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
				String domainField = sqlPro.convert(columns[j],
						JdbcUtils.TOTYPE[0]);
				for (int i = 0; i < len; i++) {
					PropertyDescriptor prop = proDesc[i];
					if (Constant.isBasicType(prop.getPropertyType())
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

			return Constant.cleanEmpty(params);
		}

		public Object[] mergerObject(Object[] params, Object[] paramsPage,
				String database) {
			Object[] newparams = new Object[params.length + paramsPage.length];

			for (int i = 0; i < params.length; i++) {
				newparams[i] = params[i];
			}

			if (JdbcUtils.MYSQL.equals(database)) {
				for (int i = 0; i < paramsPage.length; i++) {
					newparams[i + params.length] = paramsPage[i];
				}
			} else if (JdbcUtils.SQLSERVER.equals(database)) {

			} else if (JdbcUtils.ORACLE.equals(database)) {
				int len = paramsPage.length - 1;
				int count = params.length;
				for (int i = len; i >= 0; i--) {
					newparams[count] = paramsPage[i];
					count++;

				}
			}

			return newparams;
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
		 * @return 转换后的Map对象
		 * @throws SQLException
		 */
		public Map columnsToBean(Class beanClazz, Map map) throws SQLException {

			Map afterConver = new HashMap();
			Iterator iter = map.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				String key = (String) entry.getKey();
				afterConver.put(sqlPro.convert(key, JdbcUtils.TOTYPE[0]), entry
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
		 * @return 转换后的List对象
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

		/**
		 * 根据Java Bean字段名称取得属性存储器(PropertyDescriptor)
		 * 
		 * @param name
		 *            Java Bean字段
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

				//System.out.println(value + "|" + setter.getName());

				// Don't call setter if the value object isn't the right type
				if (BigDecimal.class.isAssignableFrom(value.getClass())) {
					setter.invoke(target, new Object[] { Constant.valueOf(
							long.class, value) });
				} else {
					if (params[0].isInstance(value)) {
						setter.invoke(target, new Object[] { value });
					} else {
						throw new SQLException("Cannot set " + prop.getName()
								+ ": incompatible types.");
					}
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
		 * @return 转换成Java Bean中的字段
		 * @throws SQLException
		 * 
		 * @see BeanProcessor#getDataMappingClass()
		 */
		private String convertedBeanField(String column) throws SQLException {

			PropertyDescriptor[] proDesc = propertyDescriptors(getDataMappingClass());
			for (int i = 0; i < proDesc.length; i++) {
				PropertyDescriptor pro = proDesc[i];
				if (Constant.isBasicType(pro.getPropertyType())
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
		 * @return 实例化对象
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
		 * @throws SQLException
		 *            
		 * @see JdbcUtils#setSqlFilter(Map)
		 * @see SqlProcessor#getDataMappingClass()
		 */
		public String makeSelectSql(String key) throws SQLException {
			StringBuffer sb = new StringBuffer("SELECT ");

			Integer temp = null;

			PropertyDescriptor[] proDesc = beanPro.propertyDescriptors(this
					.getDataMappingClass());
			int len = proDesc.length;
			for (int i = 0; i < len; i++) {
				PropertyDescriptor pro = proDesc[i];
				Class proType = pro.getPropertyType();
				if ((Constant.isBasicType(proType) || !Constant
						.isCollection(proType))
						&& !Class.class.isAssignableFrom(proType)) {

					if (sqlFilter == null) {
						temp = appendSelectParams(sb, pro.getName(), temp, i);
					} else {
						Object filter = sqlFilter.get(pro.getName());
						if (String.class.isInstance(filter)
								&& filter.toString().equals(pro.getName())) {
							temp = appendSelectParams(sb, filter.toString(),
									temp, i);
						} else if (filter == null || toBoolean(filter)) {
							temp = appendSelectParams(sb, pro.getName(), temp,
									i);
						}
					}
				}
			}
			sb.append(" FROM ");
			sb
					.append(convert(textFilter(getSimpleName()),
							JdbcUtils.TOTYPE[1]));
			if (Constant.isNotEmpty(key)) {
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
		private Integer appendSelectParams(StringBuffer sb, String name,
				Integer temp, int i) throws SQLException {

			if (temp == null) {
				temp = new Integer(i);
			}

			if (temp.intValue() != i) {
				sb.append(", ");
			}
			sb.append(sqlPro.convert(name, JdbcUtils.TOTYPE[1]));

			return temp;
		}

		/**
		 * 将值转换成<code>boolean</code>值。
		 * 
		 * @param value
		 *            转换值
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
		 * @throws SQLException
		 *            
		 * @see JdbcUtils#setSqlFilter(Map)
		 * @see SqlProcessor#getDataMappingClass()
		 */
		public String makeDeleteSql(String whereIf) throws SQLException {
			StringBuffer sb = new StringBuffer();
			sb.append("DELETE FROM ");
			sb
					.append(convert(textFilter(getSimpleName()),
							JdbcUtils.TOTYPE[1]));
			if (Constant.isNotEmpty(whereIf)) {
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
		 * @throws SQLException
		 *            
		 * @see JdbcUtils#setSqlFilter(Map)
		 * @see SqlProcessor#getDataMappingClass()
		 */
		public String makeUpdateSql(String whereIf) throws SQLException {
			StringBuffer sb = new StringBuffer();
			sb.append("UPDATE ");
			sb
					.append(convert(textFilter(getSimpleName()),
							JdbcUtils.TOTYPE[1]));
			sb.append(" SET ");

			Integer temp = null;

			PropertyDescriptor[] proDesc = beanPro.propertyDescriptors(this
					.getDataMappingClass());
			int len = proDesc.length;
			for (int i = 0; i < len; i++) {
				PropertyDescriptor pro = proDesc[i];
				if (Constant.isBasicType(pro.getPropertyType())) {
					if (sqlFilter == null) {
						temp = appendUpdateParams(sb, pro.getName(), temp, i);
					} else {
						Object filter = sqlFilter.get(pro.getName());
						if (String.class.isInstance(filter)
								&& filter.toString().equals(pro.getName())) {
							temp = appendUpdateParams(sb, filter.toString(),
									temp, i);
						} else if (filter == null || toBoolean(filter)) {
							temp = appendUpdateParams(sb, pro.getName(), temp,
									i);
						}
					}
				}
			}

			sb.append("=?");

			if (Constant.isNotEmpty(whereIf)) {
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
		private Integer appendUpdateParams(StringBuffer sb, String name,
				Integer temp, int i) throws SQLException {

			if (temp == null) {
				temp = new Integer(i);
			}

			if (temp.intValue() != i) {
				sb.append("=?, ");
			}
			sb.append(sqlPro.convert(name, JdbcUtils.TOTYPE[1]));

			return temp;
		}

		/**
		 * 根据Java Bean(<code>SqlProcessor.getDataMappingClass()</code>)构造Insert
		 * SQL语句。主键值通过手动维护，对于不同的数据库构造出的Insert SQL语句都是一样的，无差别。</br></br>
		 * 在构造Insert SQL语句时，如果Java Bean(<code>SqlProcessor.getDataMappingClass()</code>)中定义了<code>public Map sqlFilter(){}</code>方法，或手动设置<code>JdbcUtils.setSqlFilter(Map)</code>过滤条件，则在构造SQL时，会根据规则进行过滤。
		 * 
		 * <li>ORACLE：<code>makeInsertSql(DbTools.ORACLE, null || "")</cdoe>，构造出的SQL为<code>INSERT INTO user (username, id) VALUES (?, ?)</code></li>
		 * <li>MYSQL：<code>makeInsertSql(DbTools.MYSQL, null || "")，构造出的SQL为<code>INSERT INTO user (username, id) VALUES (?, ?)</code></li>
		 * <li>SQLSERVER：未实现</li>
		 * @return SQL语句
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
		 * @return SQL语句
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
			sb
					.append(convert(textFilter(getSimpleName()),
							JdbcUtils.TOTYPE[1]));
			sb.append(" (");

			StringBuffer paramsvalue = new StringBuffer();

			PropertyDescriptor[] proDesc = beanPro.propertyDescriptors(this
					.getDataMappingClass());
			int len = proDesc.length;
			for (int i = 0; i < len; i++) {
				PropertyDescriptor pro = proDesc[i];
				if (Constant.isBasicType(pro.getPropertyType())) {

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
									JdbcUtils.TOTYPE[1]));
						} else {
							sb.append(sqlPro.convert(pro.getName(),
									JdbcUtils.TOTYPE[1]));
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
					&& JdbcUtils.ORACLE.equals(database)
					&& Constant.isNotEmpty(sequence)) {
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
			if (JdbcUtils.MYSQL.equals(database)
					&& pro.getName().equals(getPrimaryKey())
					&& JdbcUtils.MYSQL_SEQ.equals(sequence)) {
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
			if (Constant.isNotEmpty(whereIf)) {
				sb.append(whereIf);
			} else {
				sb.append(" WHERE ");
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
		 * @return SQL语句参数键数组。未解析到有键字段，则返回null。
		 */
		public String[] getColumnsKey(String sql) {

			sql = standardFormatting(sql);

			if (Constant.startsWithInsert(sql)) {
				return columnsKeyOfInsert(sql);
			}
			if (Constant.startsWithUpate(sql) || Constant.startsWithDelete(sql)) {
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
		 * @return SQL语句参数键数组
		 */
		private String[] columnsKeyOfUpdate(String sql) {
			int len = Constant.countCharacter(sql, "?");
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
		 * @return 转换后的文本字段
		 * @throws SQLException
		 * 
		 * @see JdbcUtils#HUMP
		 * @see JdbcUtils#SEGMENTATION
		 * @see JdbcUtils#setRule(String)
		 * @see JdbcUtils#getRule()
		 * @see JdbcUtils#TOTYPE
		 */
		public String convert(String text, String toType) throws SQLException {
			if (JdbcUtils.HUMP.equals(getRule())) {
				if (Constant.isAllCaps(text)) {
					if (toType.equals(JdbcUtils.TOTYPE[0])) {
						return beanPro.convertedBeanField(text);
					} else {

					}
				} else {
					return text;
				}
			}

			if (JdbcUtils.SEGMENTATION.equals(getRule())) {
				if (Constant.isAllCaps(text)) {
					if (toType.equals(JdbcUtils.TOTYPE[0])) {
						return beanPro
								.convertedBeanField(removeSeparator(text));
					} else {

					}
				} else {
					if (toType.equals(JdbcUtils.TOTYPE[1])) {
						return sqlPro.convertedIntoSegmentation(text);
					}
					return sqlPro.convertedIntoHump(text);
				}
			}

			return text;
		}

		/**
		 * 将文本转换成驼峰命名规则。如：user_name => userName
		 * 
		 * @param text
		 *            文本字段
		 * @return 无分隔线，按照驼峰命名法的文本字段，如userName
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
		 * @return 有分隔线的文本字段，如user_name
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
		 * @return true(不等于指定某规则),false(等于指定某规则)
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
		 * @return true(等于指定某规则),false(不等于指定某规则)
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

	public static void main(String[] args) throws SQLException {

		JdbcUtils db = new JdbcUtils(ConfigDevice.class);
		// System.out.println(db.sqlPro.makeSelectSql());
		// System.out.println(db.sqlPro.makeDeleteSql());
		// System.out.println(db.sqlPro.makeUpdateSql());
		// System.out.println(db.sqlPro.makeInsertSql(JdbcUtils.MYSQL, null));

		// SqlCompatible sqlCom = new SqlCompatible();
		// String sql = db.sqlPro.makeSelectSql();
		// sql = sqlCom.getPagingSql(sql, "mysql");
		// System.out.println(sql);
		// System.out.println(new SqlCompatible().count(sql));

		// JdbcUtils db = new JdbcUtils(Users.class);
		System.out.println(db.sqlPro.makeSelectSql());
		System.out.println(db.sqlPro.makeUpdateSql());

	}

}

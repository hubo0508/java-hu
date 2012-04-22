package org.dbtools.run;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
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

import org.dbtools.AbstractDBRun;
import org.dbtools.DBTools;

/**
 * <p>
 * 数据库JDBC底层操作封装，适用JDK版本1.4+
 * </p>
 * <p>
 * 在与数据的基础交互中
 * </p>
 * 
 * @User: HUBO
 * @Date Apr 20, 2012
 * @Time 10:43:09 AM
 */
public class DBRun extends AbstractDBRun{

	/**
	 * 数据库字段命名规则，默认为常量HUMP
	 */
	private String ruleName = DBTools.HUMP;

	/**
	 * POJO Class
	 */
	private Class clazz;

	/*
	 * POJO 字段信息(私有)
	 */
	private String[] domainFields = null;

	/*
	 * 
	 */
	private volatile boolean pmdKnownBroken = false;

	/*
	 * 私有
	 */
	private String resultTypes;

	public DBRun(Class clazz) {
		this.clazz = clazz;
	}

	public DBRun(Class clazz, String ruleName) {
		this.clazz = clazz;
		this.ruleName = ruleName;
	}

	public static void main(String[] args) {
		// DBHandler db = new DBHandler(Device.class, SEGMENTATION,
		// INVERSION_SQL);
		// String sql = "SELECT
		// ID,DEVICE_IP,DEVICE_ENAME,DEVICE_TYPE,DEVICE_FACTORY,HAS_DATA,DEVICE_TYPE,DEVICE_CNAME
		// FROM nhwm_config_device";
		// String[] tableFields = db.fieldsFilter(db.getTableFields(sql));
		// for (int i = 0; i < tableFields.length; i++) {
		// String string = tableFields[i];
		// System.out.println(string);
		// }

	}

	/** ******************************************************************************************* */
	// ///////////////////////////////////////////////////////////////////////////////////////////////
	// 供外部查询使用 start//
	// ///////////////////////////////////////////////////////////////////////////////////////////////
	/** ******************************************************************************************* */
	public ArrayList queryResultToArrayList(String sql, Connection con)
			throws SQLException {
		this.resultTypes = DBTools.ARRAY_LIST;
		return (ArrayList) query(sql, con, null, ArrayList.class);
	}

	public ArrayList queryResultToArrayList(String sql, Connection con,
			Object[] params) throws SQLException {
		this.resultTypes = DBTools.ARRAY_LIST;
		return (ArrayList) query(sql, con, params, ArrayList.class);
	}

	public LinkedHashMap queryResultToLinkedHashMap(String sql, Connection con,
			Object[] params) throws SQLException {
		this.resultTypes = DBTools.LINKED_HASH_MAP;
		return (LinkedHashMap) query(sql, con, params, HashMap.class);
	}

	public LinkedHashMap queryResultToLinkedHashMap(String sql, Connection con)
			throws SQLException {
		this.resultTypes = DBTools.LINKED_HASH_MAP;
		return (LinkedHashMap) query(sql, con, null, LinkedHashMap.class);
	}

	public HashMap queryResultToHashMap(String sql, Connection con,
			Object[] params) throws SQLException {
		this.resultTypes = DBTools.HASH_MAP;
		return (HashMap) query(sql, con, params, HashMap.class);
	}

	public HashMap queryResultToHashMap(String sql, Connection con)
			throws SQLException {
		this.resultTypes = DBTools.HASH_MAP;
		return (HashMap) query(sql, con, null, HashMap.class);
	}

	public Object queryResultToUnique(String sql, Connection con,
			Object[] params) throws SQLException {
		this.resultTypes = DBTools.UNIQUE;
		return query(sql, con, params, clazz);
	}

	public Object queryResultToUnique(String sql, Connection con)
			throws SQLException {
		this.resultTypes = DBTools.UNIQUE;
		return query(sql, con, null, clazz);
	}

	public Object query(String sql, Connection conn, Object[] params,
			Class paramsTypes, String resultTypes) throws SQLException {
		return query(sql, conn, params, paramsTypes);
	}

	public Object query(String sql, Connection conn, Class paramsTypes,
			String resultTypes) throws SQLException {

		if (isEmpty(resultTypes)) {
			throw new SQLException("Null connection");
		}

		this.resultTypes = resultTypes;
		return query(sql, conn, null, paramsTypes);
	}

	private Object query(String sql, Connection conn, Object[] params,
			Class rshType) throws SQLException {

		if (conn == null) {
			throw new SQLException("Null connection");
		}

		if (sql == null) {
			throw new SQLException("Null SQL statement");
		}

		if (rshType == null) {
			throw new SQLException("Null ResultSetHandler");
		}

		if (clazz == null) {
			throw new SQLException("Null Clazz");
		}

		PreparedStatement stmt = null;
		ResultSet rs = null;
		Object result = null;
		try {
			stmt = this.prepareStatement(conn, sql);
			this.fillStatement(stmt, params);
			rs = this.wrap(stmt.executeQuery());
			result = handle(rs, rshType);
		} catch (SQLException e) {
			this.rethrow(e, sql, params);
		} catch (DBUtilException e) {
			throw e;
		} finally {
			try {
				close(rs, stmt);
			} catch (Exception e) {
				close(rs, stmt);
			}
		}

		return result;
	}

	public int execute(String sql, Connection conn, Object[] params)
			throws SQLException {

		if (conn == null) {
			throw new SQLException("Null connection");
		}

		if (sql == null) {
			throw new SQLException("Null SQL statement");
		}

		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = this.prepareStatement(conn, sql);
			this.fillStatement(stmt, params);
			rs = this.wrap(stmt.executeQuery());
		} catch (SQLException e) {
			e.printStackTrace();
			this.rethrow(e, sql, params);
		} finally {
			try {
				close(rs, stmt);
			} catch (Exception e) {
				close(rs, stmt);
			}
		}

		return 0;
	}

	/** ******************************************************************************************* */
	// ///////////////////////////////////////////////////////////////////////////////////////////////
	// 供外部查询使用 end//
	// ///////////////////////////////////////////////////////////////////////////////////////////////
	/** ******************************************************************************************* */

	/** ******************************************************************************************* */
	// ///////////////////////////////////////////////////////////////////////////////////////////////
	// 供外部对象使用 start//
	// ///////////////////////////////////////////////////////////////////////////////////////////////
	/** ******************************************************************************************* */

	public Map convertedMapFieldToBean(Class beanClazz, Map map) {

		this.clazz = beanClazz;

		Map afterConver = new HashMap();
		Iterator iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			String key = (String) entry.getKey();
			afterConver.put(filterFieldToBean(key), entry.getValue());
		}
		map = null;
		return afterConver;
	}

	public List convertedMapFieldToBean(Class beanClazz, List list) {

		this.clazz = beanClazz;

		List afterConver = new ArrayList();
		for (int i = 0; i < list.size(); i++) {
			Map map = (Map) list.get(i);
			afterConver.add(i, convertedMapFieldToBean(beanClazz, map));
		}

		return afterConver;
	}

	/**
	 * 设置过滤条件
	 */
	public String[] fieldsFilterToBean(String[] fields) {

		for (int i = 0; i < fields.length; i++) {
			fields[i] = filterFieldToBean(fields[i]);
		}

		return fields;
	}

	/**
	 * 对单个字段设置过虑条件</br></br>
	 * 
	 * @param text
	 *            单个字段
	 */
	public String filterFieldToBean(String text) {

		if (DBTools.HUMP.equals(getRuleName())) {
			if (isAllCaps(text)) {
				return convertedDomainField(text);
			} else {
				return text;
			}
		}

		if (DBTools.SEGMENTATION.equals(getRuleName())) {
			if (isAllCaps(text)) {
				return convertedDomainField(removeSeparator(text));
			} else {
				return convertedIntoHump(text);
			}
		}

		return text;
	}

	/** ******************************************************************************************* */
	// ///////////////////////////////////////////////////////////////////////////////////////////////
	// 供外部对象使用 end//
	// ///////////////////////////////////////////////////////////////////////////////////////////////
	/** ******************************************************************************************* */

	protected Object handle(ResultSet rs, Class rshType) throws SQLException,
			DBUtilException {

		if (DBTools.ARRAY_LIST.equals(resultTypes)) {
			return queryResultToArrayList(new ArrayList(), rs);
		}

		if (DBTools.LINKED_HASH_MAP.equals(resultTypes)) {
			checkDataUnique(rs);
			return rs.next() ? toMap(new LinkedHashMap(), rs) : null;
		}

		if (DBTools.HASH_MAP.equals(resultTypes)) {
			checkDataUnique(rs);
			return rs.next() ? toMap(new HashMap(), rs) : null;
		}

		if (DBTools.UNIQUE.equals(resultTypes) && isHashMap()) {
			checkDataUnique(rs);
			return rs.next() ? toUniqueObject(new HashMap(), rs) : null;
		} else {
			if (DBTools.UNIQUE.equals(resultTypes)
					&& !isPrimitiveTypes(rshType)) {
				checkDataUnique(rs);
				return rs.next() ? toUniqueObject(instance(clazz), rs) : null;
			} else {
				checkDataUnique(rs);
				if (isPrimitiveTypes(rshType)) {
					return rs.next() ? toUniqueBaseType(rs, rshType) : null;
				}
			}
		}

		return null;
	}

	private Object toUniqueBaseType(ResultSet rs, Class clazz)
			throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int cols = rsmd.getColumnCount();

		if (cols >= 2) {
			throw new DBUtilException("Query column number greater than !",
					DBUtilException.QUERY_UNIQUE_EXCEPTION);
		}

		Object value = rs.getObject(1);
		if (!clazz.toString().equals(value.getClass().toString())) {
			throw new DBUtilException(
					"Set the return value type does not match!"
							+ value.getClass() + " can not convert " + clazz,
					DBUtilException.QUERY_UNIQUE_EXCEPTION);
		}

		return value;
	}

	private Object toUniqueObject(Object instanceObject, ResultSet rs)
			throws SQLException, DBUtilException {

		ResultSetMetaData rsmd = rs.getMetaData();
		int cols = rsmd.getColumnCount();

		Map map = null;
		for (int i = 0; i < cols; i++) {
			String field = rsmd.getColumnName(i + 1);
			if (isHashMap()) {
				if (map == null) {
					map = (HashMap) instanceObject;
				}
				map.put(field, rs.getObject(field));
			} else {
				String mField = makeStringName("set", filterFieldToBean(field));
				copyValueToDomain(instanceObject, rs.getObject(field), mField);
			}
		}
		return instanceObject;
	}

	private Map toMap(Map rsh, ResultSet rs) throws SQLException {

		ResultSetMetaData rsmd = rs.getMetaData();
		int cols = rsmd.getColumnCount();

		for (int i = 1; i <= cols; i++) {
			if (isHashMap()) {
				rsh.put(rsmd.getColumnName(i), rs.getObject(i));
			} else {
				rsh.put(filterFieldToBean(rsmd.getColumnName(i)), rs
						.getObject(i));
			}
		}

		return rsh;
	}

	private List queryResultToArrayList(List rsh, ResultSet rs) {
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			int cols = rsmd.getColumnCount();
			while (rs.next()) {
				if (isHashMap()) {
					rsh.add(toMap(new HashMap(), rs));
				} else {
					Object instanceDomain = instance(clazz);
					for (int i = 0; i < cols; i++) {
						String field = rsmd.getColumnName(i + 1);
						String mField = makeStringName("set",
								filterFieldToBean(field));
						copyValueToDomain(instanceDomain, rs.getObject(field),
								mField);
					}
					rsh.add(instanceDomain);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return rsh;
	}

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

	private String deepToString(Object[] a) {
		if (a == null || a.length == 0)
			return "null";

		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < a.length; i++) {
			buf.append(a[i]);
		}

		return buf.toString();
	}

	/**
	 * 检查数据的唯一性
	 */
	private void checkDataUnique(ResultSet rs) throws SQLException {
		if (resultSize(rs) >= 2) {
			throw new DBUtilException("Result set is not unique!",
					DBUtilException.QUERY_UNIQUE_EXCEPTION);
		}
	}

	/**
	 * 取得结果集ResultSet总记录数
	 */
	private long resultSize(ResultSet rs) throws SQLException {

		rs.last(); // 移到最后一行
		long rowCount = rs.getRow(); // 得到当前行号，也就是记录数
		rs.beforeFirst(); // 还要用到记录集，就把指针再移到初始化的位置

		return rowCount;
	}

	/**
	 * 实例化Object对象
	 */
	protected Object instance(Class clazz) {
		try {
			return clazz.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return null;
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

	/**
	 * 设置值
	 */
	private void copyValueToDomain(Object instanceDomain, Object value,
			String methodname) {
		try {
			Method method = clazz.getMethod(methodname, new Class[] { value
					.getClass() });
			method.invoke(instanceDomain, new Object[] { value });
		} catch (Exception e) {
			throw new DBUtilException(e.getMessage(),
					DBUtilException.QUERY_UNIQUE_EXCEPTION);
		}
	}

	/** ******************************************************************************************** */

	/**
	 * 删除分隔线
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
	 * 判断文本是否全部大写
	 */
	private boolean isAllCaps(String text) {
		if (text.equals(text.toUpperCase())) {
			return true;
		}
		return false;
	}

	/**
	 * 将没有任何文本转换成领域类字段
	 */
	private String convertedDomainField(String text) {

		if (domainFields == null) {
			initDomainFields();
		}

		int len = domainFields.length;
		for (int i = 0; i < len; i++) {
			String domainFiled = domainFields[i];
			if (domainFields[i].toUpperCase().equals(text)) {
				return domainFiled;
			}
		}
		return null;
	}

	/**
	 * 初始化POJO所有文本字段，保存到公共变量
	 */
	private void initDomainFields() {
		if (domainFields == null || domainFields.length == 0) {
			Field[] dFields = clazz.getDeclaredFields();
			int len = dFields.length;
			domainFields = new String[len];
			for (int i = 0; i < len; i++) {
				domainFields[i] = dFields[i].getName();
			}
		}
	}

	/**
	 * 取得方法名，如：getUserName
	 */
	private String makeStringName(String methodPrefix, String fieldName) {
		String firstLetter = fieldName.substring(0, 1).toUpperCase();
		return methodPrefix + firstLetter + fieldName.substring(1);
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
	public String convertedIntoSegmentation(String text) {

		StringBuffer sb = new StringBuffer();
		int cacheIndex = 0;

		Pattern p = Pattern.compile("[A-Z]");
		Matcher m = p.matcher(text);
		while (m.find()) {
			String value = text.substring(cacheIndex, m.start());
			if (value == null || value.equals("")) {
				break;
			}
			sb.append(value);
			sb.append("_");
			cacheIndex = m.start();
		}
		sb.append(text.substring(cacheIndex));

		return sb.toString().toLowerCase();
	}

	public boolean isEmpty(String value) {
		if (value == null || value.equals("")) {
			return true;
		}
		return false;
	}

	public boolean isNotEmpty(String value) {
		if (value == null || value.equals("")) {
			return false;
		}
		return true;
	}

	private boolean isHashMap() {
		String rshTypeStr = clazz.toString();
		if ("class java.util.HashMap".equals(rshTypeStr)
				|| "interface java.util.Map".equals(rshTypeStr)) {
			return true;
		}
		return false;
	}

	/**
	 * 判断Class类型是否Java原始(基础)类型。
	 * 
	 * @return true || false
	 */
	public boolean isPrimitiveTypes(Class clazz) {
		if (clazz == String.class) {
			return true;
		}

		else if (clazz == int.class || clazz == Integer.class) {
			return true;
		}

		else if (clazz == double.class || clazz == Double.class) {
			return true;
		}

		else if (clazz == boolean.class || clazz == Boolean.class) {
			return true;
		}

		else if (clazz == float.class || clazz == Float.class) {
			return true;
		}

		else if (clazz == long.class || clazz == Long.class) {
			return true;
		}

		else if (clazz == Date.class) {
			return true;
		}

		else if (clazz == byte.class || clazz == Byte.class) {
			return true;
		}

		else if (clazz == short.class || clazz == Short.class) {
			return true;
		}

		else if (clazz == char.class || clazz == Character.class) {
			return true;
		}

		return false;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public Class getClazz() {
		return clazz;
	}

	public void setClazz(Class clazz) {
		this.clazz = clazz;
	}

}

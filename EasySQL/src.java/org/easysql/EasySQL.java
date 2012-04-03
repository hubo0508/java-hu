package org.easysql;


import org.easysql.core.Entity;

/**
 * 常量库
 */
public class EasySQL {

	public final static int SUCCESSFUL = 1;

	public final static int FAILED = 0;

	/** ************************************************************************************************************************************** */
	
	public final static String ORACLE = "oracle";
	
	public final static String MYSQL = "mysql";
	
	public final static String SQLSERVICE = "sqlserver";
	
	public final static String NATIVE  = "native";
	
	public final static String SEQUENCE = "sequence";
	
	public final static String IDENTITY = "identity";
	
	/** ************************************************************************************************************************************** */

	/**
	 * POJO字段命名与数据命名的方式为：驼峰命名法。如：userName;
	 */
	public final static String FIELD_RULE_HUMP = "hump";

	/**
	 * POJO字段命名与数据命名的方式为：分段名法。如：user_name;
	 */
	public final static String FIELD_RULE_SEGMENTATION = "segmentation";

	/** ************************************************************************************************************************************** */

	public static final String GENERATOR = "easysql-mapping/generator";

	public static final String GENERATOR_SEQ_VALUE = "easysql-mapping/generator/param";

	public static final String ENTITY = "easysql-mapping/entitys/entity";

	public static final String FIELD_RULE = "easysql-mapping/field-rule";

	public static final String DATABASE = "easysql-mapping/database";

	/** ************************************************************************************************************************************** */

	/**
	 * 取得过滤条件全路径
	 */
	public static String key(Class<?> clazz) {
		return clazz.getCanonicalName() + "." + Entity.FILTER;
	}
}

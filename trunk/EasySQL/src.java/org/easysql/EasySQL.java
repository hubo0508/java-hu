package org.easysql;

import org.easysql.core.Entity;

public class EasySQL {

	public final static int SUCCESSFUL = 1;

	public final static int FAILED = 0;

	/** ************************************************************************************************************************************** */

	public final static String FIELD_RULE_HUMP = "hump";

	public final static String FIELD_RULE_SEGMENTATION = "segmentation";

	/** ************************************************************************************************************************************** */

	public static final String GENERATOR = "easysql-mapping/generator";

	public static final String GENERATOR_SEQ_VALUE = "easysql-mapping/generator/param";

	public static final String ENTITY = "easysql-mapping/entitys/entity";

	public static final String FIELD_RULE = "easysql-mapping/field-rule";

	public static final String DATABASE = "easysql-mapping/database";

	/** ************************************************************************************************************************************** */

	public static String key(Class<?> clazz) {
		return clazz.getCanonicalName() + "." + Entity.NOT_TAKE;
	}

}

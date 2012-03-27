package com.easysql;

import com.easysql.core.Entity;

public class Const {

	public final static String FIELD_RULE_HUMP = "hump";

	public final static String FIELD_RULE_SEGMENTATION = "segmentation";

	public static String key(Class<?> clazz) {
		return clazz.getCanonicalName() + "." + Entity.NOT_TAKE;
	}
}

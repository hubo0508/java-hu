package com.easysql.orm;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.easysql.core.Entity;
import com.easysql.core.ObjectManage;
import com.easysql.core.SqlMap;

public class EntityEngine extends ObjectManage {

	public EntityEngine() {
		super();
	}

	public EntityEngine(Class<?> clazz) {
		super.setCanonicalName(clazz.getCanonicalName());
		super.setClazz(clazz);
	}

	public String[] getRowField() {

		SqlMap filterConditions = (SqlMap) SqlMap.getInstance().get(
				super.getCanonicalName() + "." + Entity.NOT_TAKE);

		Field[] fields = getClazz().getDeclaredFields();
		String[] filedsStr = new String[fields.length];

		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			if (filterConditions == null) {
				filedsStr[i] = field.getName();
			} else {
				Object obj = filterConditions.get(field.getName());
				if (obj == null || (Boolean) obj) {
					filedsStr[i] = field.getName();
				}
			}
		}

		return filedsStr;
	}

}

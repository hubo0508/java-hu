package com.easysql.orm;

import java.lang.reflect.Field;

import com.easysql.core.Entity;
import com.easysql.core.ObjectManage;
import com.easysql.core.Mapping;

public class EntityEngine extends ObjectManage {

	public EntityEngine() {
		super();
	}

	public EntityEngine(Class<?> clazz) {
		super.setCanonicalName(clazz.getCanonicalName());
		super.setClazz(clazz);
	}

	public String[] getRowField() {

		Mapping filterConditions = (Mapping) Mapping.getInstance().get(
				super.getCanonicalName() + "." + Entity.NOT_TAKE);

		Field[] fields = getClazz().getDeclaredFields();
		String[] filedsStr = new String[fields.length];

		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			String value = null;
			if (filterConditions == null) {
				value = field.getName();
			} else {
				Object obj = filterConditions.get(field.getName());
				if (obj == null || (Boolean) obj) {
					value = field.getName();
				}
			}

			filedsStr[i] = value;
		}

		return filedsStr;
	}

}

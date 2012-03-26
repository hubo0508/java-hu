package com.easysql.orm;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.easysql.core.ObjectManage;
import com.easysql.core.object.SqlMap;
import com.easysql.engine.xml.NodeEngine;

public class EntityEngine extends ObjectManage {

	public EntityEngine() {
		super();
	}

	public EntityEngine(Class<?> clazz) {
		super.setCanonicalName(clazz.getCanonicalName());
		super.setClazz(clazz);
	}

	public String[] getRowField() {

		SqlMap filterConditions = getFilterConditions();

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

	@SuppressWarnings("unchecked")
	private SqlMap getFilterConditions() {

		try {

			Object instance = NodeEngine.getInstance().node
					.get(getCanonicalName());
			Method getOldMethod = getClazz().getMethod("notTake",
					new Class[] {});
			SqlMap sqlMap = (SqlMap) getOldMethod.invoke(instance,
					new Object[] {});

			return sqlMap;

		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		return null;
	}
}

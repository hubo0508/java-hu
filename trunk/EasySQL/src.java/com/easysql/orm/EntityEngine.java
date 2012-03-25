package com.easysql.orm;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.easysql.core.ObjectManage;
import com.easysql.core.object.SqlMap;

public class EntityEngine extends ObjectManage {

	public EntityEngine() {
		super();
	}

	public EntityEngine(Class<?> clazz) {
		super.setCanonicalName(clazz.getCanonicalName());
		super.setClazz(clazz);
	}

	public String[] getRowField() {

		getFilterConditions();

		Field[] fields = getClazz().getDeclaredFields();

		String[] filedsStr = new String[fields.length];

		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			filedsStr[i] = field.getName();
		}

		return filedsStr;
	}

	@SuppressWarnings("unchecked")
	private SqlMap getFilterConditions() {
		// CopyOfUser x = null;
		// if (x instanceof CopyOfUser) {
		//
		// }
		// if (!getClazz().isInstance(Entity.class)) {
		// throw new RuntimeException("类型错误");
		// }

		try {

			Object instance = getClazz().newInstance();

			Method getOldMethod = getClazz().getMethod("notTake",
					new Class[] {});
			@SuppressWarnings("unused")
			SqlMap sqlMap = (SqlMap) getOldMethod.invoke(instance,
					new Object[] {});
			
			System.out.println(sqlMap.get("serialVersionUID"));

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
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
}

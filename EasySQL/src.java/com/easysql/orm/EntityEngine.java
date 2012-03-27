package com.easysql.orm;

import java.lang.reflect.Field;

import com.easysql.core.Entity;
import com.easysql.core.ObjectManage;
import com.easysql.core.Mapping;
import com.easysql.core.object.IfMap;

public class EntityEngine extends ObjectManage {

	public EntityEngine() {
		super();
	}

	public EntityEngine(Class<?> clazz) {
		super.setCanonicalName(clazz.getCanonicalName());
		super.setClazz(clazz);
	}

	public String[] getRowField() {

		// 判断实例类是否继承com.easysql.core.Entity
		if (!isExtendsEntity(getClazz())) {
			throw new RuntimeException(getClazz().getCanonicalName()
					+ "未继承基类com.easysql.core.Entity");
		}

		// 取得过滤条件
		IfMap ifmap = (IfMap) Mapping.getInstance().get(
				super.getCanonicalName() + "." + Entity.NOT_TAKE);

		Field[] fields = getClazz().getDeclaredFields();
		String[] elements = new String[fields.length];

		int count = 0;

		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];

			Object isFalg = collection.get(field.getType().toString());
			if (isFalg != null && (Boolean) isFalg) {
				count++;
				continue;
			}
			String value = null;
			if (ifmap == null) {
				value = field.getName();
			} else {
				Object obj = ifmap.get(field.getName());
				if (obj == null || (Boolean) obj) {
					value = field.getName();
				}
			}

			if (value == null) {
				count++;
			}
			elements[i] = value;
		}

		return removeNullElements(count, elements);
	}

	private String[] removeNullElements(int count, String[] elements) {

		String[] newElements = new String[(elements.length - count) + 1];
		newElements[0] = getClazz().getSimpleName();

		count = 1;
		for (String e : elements) {
			if (e != null) {
				newElements[count] = e;
				count++;
			}
		}

		return newElements;
	}

	public boolean isExtendsEntity(Class<?> clazz) {

		if (clazz == null) {
			throw new RuntimeException("clazz对象为NULL");
		}

		if (clazz.getSuperclass().getCanonicalName().toString().equals(
				"com.easysql.core.Entity")) {
			return true;
		}

		return false;
	}
}

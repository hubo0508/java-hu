package org.easysql.handlers;

import java.lang.reflect.Field;

import org.easysql.core.Entity;
import org.easysql.core.Mapping;
import org.easysql.core.ObjectManage;

public class EntityHandler extends ObjectManage {

	public EntityHandler() {
		super();
	}

	@SuppressWarnings("unchecked")
	public EntityHandler(Class<?> clazz) {
		super.setCanonicalName(clazz.getCanonicalName());
		super.setClazz((Class<Entity>) clazz);
	}

	public String[] getFields() {

		// 判断实例类是否继承com.easysql.core.Entity
		if (!isExtendsEntity()) {
			throw new RuntimeException(getClazz().getCanonicalName()
					+ "未继承基类com.easysql.core.Entity");
		}

		// 取得过滤条件
		EntityFilter ifmap = (EntityFilter) Mapping.getInstance().get(
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

}

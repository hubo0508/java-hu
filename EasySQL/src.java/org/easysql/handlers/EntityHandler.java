package org.easysql.handlers;

import java.lang.reflect.Field;

import org.easysql.EasySQL;
import org.easysql.core.Mapping;
import org.easysql.core.ObjectManage;


/**
 * 实体简化操作类
 * 
 * @User: 龙飞跃
 */
@SuppressWarnings("unchecked")
public class EntityHandler extends ObjectManage {

	public EntityHandler() {
		super();
	}

	public EntityHandler(Class<?> clazz) {
		super.setCanonicalName(clazz.getCanonicalName());
		super.setClazz(clazz);
	}

	/**
	 * 取得指定POJO的字段。</br></br>
	 * 
	 * 1、该POJO属实继承org.easysql.core.Entity；</br>
	 * 2、所取POJO字段，将根据POJO中自定的过滤条件filter()来进行简单业务处理；</br>
	 * 3、如POJO中有字段类型为List、Set、Map，过滤条件自动去除改字段；</br>
	 * 
	 * @see org.easysql.core.Entity;
	 */
	public String[] getEntityFields() {

		// 判断实例类是否继承com.easysql.core.Entity
		if (!isExtendsEntity()) {
			throw new RuntimeException(getClazz().getCanonicalName()
					+ "未继承基类org.easysql.core.Entity");
		}

		// 取得过滤条件
		EntityFilter filter = (EntityFilter) Mapping.getInstance().get(
				EasySQL.key(getClazz()));

		// 取得POJO中定义的字段
		Field[] fields = getClazz().getDeclaredFields();
		String[] elements = new String[fields.length];
		int count = 0;

		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];

			// 判断类型是否为List、Set、Map
			Object isFalg = collection.get(field.getType().toString());
			if (isFalg != null && (Boolean) isFalg) {
				count++;
				continue;
			}
			String value = null;
			if (filter == null) {
				value = field.getName();
			} else {
				// 对字段进行过滤
				Object obj = filter.get(field.getName());
				if (obj == null || (Boolean) obj) {
					value = field.getName();
				}
			}

			if (value == null) {
				count++;
			}
			elements[i] = value;
		}

		return removeEmptyElement(count, elements);
	}

	/**
	 * 将现有String[]中的Null元素删除，组成新Array。</br>
	 * 
	 * 
	 * 
	 * @param count
	 *            新String[]长度
	 * @param elements
	 *            需要重新构造的String[]
	 */
	private String[] removeEmptyElement(int count, String[] elements) {

		String[] reconstruction = new String[(elements.length - count) + 1];
		reconstruction[0] = getClazz().getSimpleName();

		count = 1;
		for (String e : elements) {
			if (e != null) {
				reconstruction[count] = e;
				count++;
			}
		}

		return reconstruction;
	}

}

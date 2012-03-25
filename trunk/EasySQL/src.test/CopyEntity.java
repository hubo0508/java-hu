
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CopyEntity {

	/**
	 * 2010-12-27
	 * 
	 * @param list
	 * @param orm
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List copyDomainInList(List list, Map<String, Object> orm) {
		List<Object> newList = new ArrayList<Object>();
		for (int i = 0; i < list.size(); i++) {
			Object newDomain = copyDomian(list.get(i), getInstance(list.get(i)));
			setORMID(list.get(i), newDomain, orm);
			newList.add(newDomain);
		}
		return newList;
	}

	/**
	 * 传�?�一个集合，将属性拷贝一�? zhubin
	 * 
	 * @param list
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List copyDomainInList(List list) {
		List<Object> newList = new ArrayList<Object>();
		for (int i = 0; i < list.size(); i++) {
			try {
				newList.add(copyDomian(list.get(i), list.get(i).getClass()
						.newInstance()));
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return newList;
	}

	/**
	 * 成批替换源集合中的oldDomian实体�? 新的Domian实体
	 * 
	 * @param oldDomianList
	 * @param newDomian
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List batchCopyDomian(List oldDomianList, Class newDomian) {
		List newDomianList = new ArrayList();
		try {
			for (int i = 0; i < oldDomianList.size(); i++) {
				Object o = newDomian.newInstance();
				CopyEntity.copyDomian(oldDomianList.get(i), o);
				newDomianList.add(o);
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return newDomianList;
	}

	/**
	 * copy value
	 * 
	 * @param oldEntity
	 * @param newEntity
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Object copyDomian(Object oldDomian, Object newDomian) {

		if (oldDomian == null) {
			return newDomian;
		}

		Class old = oldDomian.getClass();
		Class news = newDomian.getClass();

		Field[] fields = news.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			if (isBoolean(field.getType())) {

				// 方法�?
				String fieldName = field.getName();
				String firstLetter = fieldName.substring(0, 1).toUpperCase();
				String getMethodName = "get" + firstLetter
						+ fieldName.substring(1);
				String setNewMethodName = "set" + firstLetter
						+ fieldName.substring(1);

				try {
					// 返回指定类的成员方法
					Method getOldMethod = old.getMethod(getMethodName,
							new Class[] {});
					Method setNewMethod = news.getMethod(setNewMethodName,
							new Class[] { field.getType() });
					// 取�??
					Object oldValue = getOldMethod.invoke(oldDomian,
							new Object[] {});
					// 设�??
					setNewMethod.invoke(newDomian, new Object[] { oldValue });

				} catch (Exception e) {
					continue;
				}

			}
		}

		return newDomian;
	}

	/**
	 * 取得方法�? 2010-12-27
	 * 
	 * @param methodPrefix
	 * @param name
	 * @return
	 */
	private static String getMethodName(String methodPrefix, String fieldName) {
		String firstLetter = fieldName.substring(0, 1).toUpperCase();
		return methodPrefix + firstLetter + fieldName.substring(1);
	}

	/**
	 * 设置子项ID�? 2010-12-27
	 * 
	 * @param oldDomian
	 * @param newDomian
	 * @param mappingProperties
	 */
	@SuppressWarnings("unchecked")
	private static void setORMID(Object oldDomian, Object newDomian,
			Map<String, Object> mappingProperties) {

		Class oldCls = oldDomian.getClass();
		Class newsCls = newDomian.getClass();

		Set keys = mappingProperties.keySet();
		Iterator it = keys.iterator();
		while (it.hasNext()) {
			String primaryKey = (String) it.next();
			String mappingClassStr = (String) mappingProperties.get(primaryKey);

			try {
				// 取�??
				String methodName = getMethodName("get", mappingClassStr);
				Method getOldMethod = oldCls.getMethod(methodName,
						new Class[] {});
				Object ORMObj = getOldMethod.invoke(oldDomian, new Object[] {});
				Class ORMCls = ORMObj.getClass();

				String ORMMethodName = getMethodName("get", "id");
				Method ORMMethod = ORMCls.getMethod(ORMMethodName,
						new Class[] {});
				Object ORMID = ORMMethod.invoke(ORMObj, new Object[] {});

				// 设�??
				// System.out.println(ORMID);
				String newMethodName = getMethodName("set", primaryKey);
				Field field = newsCls.getDeclaredField(primaryKey);
				Method newMethod = newsCls.getMethod(newMethodName,
						new Class[] { field.getType() });
				newMethod.invoke(newDomian, new Object[] { ORMID });

			} catch (Exception e) {
			}
		}
	}

	/**
	 * 取得实例 2010-12-27
	 * 
	 * @param obj
	 * @return
	 */
	private static Object getInstance(Object obj) {
		try {
			return obj.getClass().newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 判别属�?�类�?
	 * 
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static boolean isBoolean(Class clazz) {
		// 字符�?
		if (clazz == String.class) {
			return true;
		}

		// int,Integer
		else if (clazz == int.class || clazz == Integer.class) {
			return true;
		}

		// double,Double
		else if (clazz == double.class || clazz == Double.class) {
			return true;
		}

		// boolean,Boolean
		else if (clazz == boolean.class || clazz == Boolean.class) {
			return true;
		}

		// float,Float
		else if (clazz == float.class || clazz == Float.class) {
			return true;
		}

		// long,Long
		else if (clazz == long.class || clazz == Long.class) {
			return true;
		}

		// java.util.Date
		else if (clazz == Date.class) {
			return true;
		}

		// byte,Byte
		else if (clazz == byte.class || clazz == Byte.class) {
			return true;
		}

		// short,Short
		else if (clazz == short.class || clazz == Short.class) {
			return true;
		}

		// char,Character
		else if (clazz == char.class || clazz == Character.class) {
			return true;
		} else if (clazz == Timestamp.class) {

			return true;
		}
		return false;

	}
}

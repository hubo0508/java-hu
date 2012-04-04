/**
 * @Project: XEntityNode
 * @Title: XEntityNode.java
 * @Package org.hu.xnode.xml
 * @Description: TODO
 * @author HUBO hubo.0508@gmail.com  
 * @date 2011-3-12 PM 02:45:58
 * @version V1.0  
 */
package org.hu.xnode.xml;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.thoughtworks.xstream.XStream;

/**
 * @ClassName: XEntityNode
 * @Description: 根据实体类产生XML数据
 * @author HUBO hubo.0508@gmail.com
 * @date 2011-3-12 PM 10:43:40
 * 
 * v0.2
 */
public class XNode<T> {

	public static Map<String, Object> node = new HashMap<String, Object>();

	/**
	 * <p>
	 * 将List集合对象转换成XML节点字符串
	 * </p>
	 * 
	 * @param listPojo
	 *            List对像
	 * 
	 * @return XML字符串
	 */
	public static String xmlInList(List<?> listPojo) {

		StringBuffer buff = new StringBuffer();
		try {
			for (Object pojo : listPojo) {
				XStream xs = setAttribute(new XStream(), pojo);
				buff.append(xs.toXML(pojo) + "\n");
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			node = null;
		}
		return buff.toString();
	}

	/**
	 * <p>
	 * 将POJO对象转换成XML节点字符串
	 * </p>
	 * 
	 * @param list
	 *            List对像
	 * 
	 * @return XML字符串
	 */
	public static String xmlInPojo(Object pojo) {
		try {
			XStream xs = setAttribute(new XStream(), pojo);
			return xs.toXML(pojo);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			node = null;
		}
	}

	// ///////////////////////////////////////////////////////////////////////////////////

	private static XStream setAttribute(XStream xStream, Object pojo)
			throws Exception {

		Class<?> pojoClazz = pojo.getClass();
		String aliasName = getAliasName(pojoClazz.getName());
		if (aliasName != null) {
			xStream.alias(aliasName, pojoClazz);
		}

		Field[] pojoField = pojoClazz.getDeclaredFields();
		for (int i = 0; i < pojoField.length; i++) {
			Field field = pojoField[i];
			String name = field.getName();
			if (isBasicType(field.getType())) {
				xStream.aliasAttribute(pojoClazz, name, name);
			} else {
				if (isCollection(field.getType())) {
					collectionHandler(xStream, pojo, name);
				} else {
					Object instanceSubPojo = getSubPojo(pojo, name);
					if (instanceSubPojo != null) {
						setAttribute(xStream, instanceSubPojo);
					}
				}
			}
		}

		return xStream;
	}

	/**
	 * <p>
	 * Java集合(List\Set\Map)处理
	 * </p>
	 */
	private static void collectionHandler(XStream xStream, Object entity,
			String fieldName) throws Exception {

		Class<?> entityCls = entity.getClass();
		Object nodeMark = node.get(fieldName);
		if ((nodeMark != null && (Boolean) nodeMark)) {

		} else {
			xStream.addImplicitCollection(entityCls, fieldName);
		}

		Object subCollectionItem = getSubPojo(entity, fieldName);

		// 判断具体类型
		if (subCollectionItem instanceof List) {
			List<?> subListItem = (List<?>) subCollectionItem;
			for (int j = 0; j < subListItem.size(); j++) {
				setAttribute(xStream, subListItem.get(j));
			}
		} else if (subCollectionItem instanceof Set) {
			Set<?> subSetItem = (Set<?>) subCollectionItem;
			for (Iterator<?> iterator = subSetItem.iterator(); iterator
					.hasNext();) {
				setAttribute(xStream, iterator.next());
			}
		} else if (subCollectionItem instanceof Map) {

		}
	}

	/**
	 * <p>
	 * 取得子POJO对象实例
	 * </p>
	 */
	private static Object getSubPojo(Object parentPojo, String subPojoName)
			throws SecurityException, NoSuchMethodException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {

		String methodName = getMethoName("get", subPojoName);
		Method instanceMethod = parentPojo.getClass().getMethod(methodName,
				new Class[] {});

		return instanceMethod.invoke(parentPojo, new Object[] {});
	}

	@SuppressWarnings( { "unchecked", "unused" })
	private static XStream setAttribute(XStream xStream, Object entity,
			String rootName, Map<String, String> filtrate) throws Exception {

		Class entityCls = null;
		try {
			entityCls = entity.getClass();
		} catch (NullPointerException e) {
			System.err.println("property is empty");
			return xStream;
		}

		xStream.alias(rootName, entityCls);

		Field[] entityField = entityCls.getDeclaredFields();
		for (int i = 0; i < entityField.length; i++) {
			Field field = entityField[i];

			if (filtrateAttribute(field, filtrate)) {

				if (isBasicType(field.getType())) {
					xStream.aliasAttribute(entityCls, field.getName(), field
							.getName());
				} else {

					String methodName = getMethoName("get", field.getName());
					Method getMethod = entityCls.getMethod(methodName,
							new Class[] {});
					Object childObj = getMethod.invoke(entity, new Object[] {});

					setAttribute(xStream, childObj, rootName, filtrate);
				}
			}

		}

		return xStream;
	}

	@SuppressWarnings("unchecked")
	private static boolean filtrateAttribute(Field field,
			Map<String, String> filtrate) {

		String fieldName = field.getName();
		// String fieldType = field.getType().toString();

		Set keys = filtrate.keySet();
		Iterator it = keys.iterator();
		while (it.hasNext()) {
			String filtrateName = (String) it.next();
			// String filtrateType = (String) filtrate.get(filtrateName);
			if (filtrateName.equals(fieldName)) {
				return false;
			}
		}

		return true;
	}

	private static String getAliasName(String path) {

		Object aliasName = node.get(path);

		if (aliasName instanceof Boolean && (Boolean) aliasName) {
			return null;
		}

		if (aliasName == null) {
			int lastIndex = path.lastIndexOf(".");
			if (lastIndex < 0) {
				return path.toLowerCase();
			} else {
				return path.substring(lastIndex + 1, path.length())
						.toLowerCase();
			}
		} else {
			return aliasName.toString();
		}
	}

	/**
	 * <p>
	 * 根据成员变量名组装成标准get或set方法名。
	 * </p>
	 * 
	 * @param methodPrefix
	 *            名字前缀(get||set)
	 * @param fieldName
	 *            成员名字
	 * 
	 * @return getName || setName
	 */
	public static String getMethoName(String methodPrefix, String fieldName) {
		String firstLetter = fieldName.substring(0, 1).toUpperCase();
		return methodPrefix + firstLetter + fieldName.substring(1);
	}

	/**
	 * <p>
	 * 判断当前Class类型是否为Java集合类型
	 * </p>
	 * 
	 * @return true:clazz的类型为List或Map、List，false则反之。
	 */
	private static boolean isCollection(Class<?> clazz) {
		String str = clazz.toString();
		if ("interface java.util.List".equals(str)) {
			return true;
		} else if ("interface java.util.Set".equals(str)) {
			return true;
		} else if ("interface java.util.Map".equals(str)) {
			return true;
		}
		return false;
	}

	/**
	 * <p>
	 * 判断Class的类型是否为Java基础类型
	 * </p>
	 */
	public static boolean isBasicType(Class<?> clazz) {
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
		}

		return false;

	}

	public static void main(String[] args) {
		System.out.println(isBasicType(int.class));
	}
}

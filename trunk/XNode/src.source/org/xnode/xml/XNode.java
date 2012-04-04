/*
 * @author HUBO hubo.0508@gmail.com  
 * 
 * @version V0.1 (创建2011-3-12 PM 02:45:58)
 * @version V0.2 (重构2012-04-04)
 */
package org.xnode.xml;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.thoughtworks.xstream.XStream;

/**
 * @Description: 根据POJO产生XML数据
 */
public class XNode {

	/**
	 * 条件过滤
	 */
	private Map<String, Object> filter;

	/**
	 * <p>
	 * 对POJO产生的XML节点具有等级形式的标识。
	 * </p>
	 */
	public final static String LEVEL = "level";

	public XNode() {

	}

	public XNode(Map<String, Object> filter) {
		this.filter = filter;
	}

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
	public String xmlInList(List<?> listPojo) {

		StringBuffer buff = new StringBuffer();
		try {
			for (Object pojo : listPojo) {
				XStream xs = setAttribute(new XStream(), pojo);
				buff.append(xs.toXML(pojo) + "\n");
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
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
	public String xmlInPojo(Object pojo) {
		try {
			XStream xs = setAttribute(new XStream(), pojo);
			return xs.toXML(pojo);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// ///////////////////////////////////////////////////////////////////////////////////

	private XStream setAttribute(XStream xStream, Object pojo) throws Exception {

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
					Object instanceSubPojo = getSubPojoOrCollection(pojo, name);
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
	private void collectionHandler(XStream xStream, Object parentPojo,
			String parentFieldName) throws Exception {

		Class<?> entityCls = parentPojo.getClass();
		Object nodeMark = filter.get(parentFieldName);
		if (LEVEL.equals(nodeMark)) {

		} else {
			xStream.addImplicitCollection(entityCls, parentFieldName);
		}

		Object subCollectionItem = getSubPojoOrCollection(parentPojo,
				parentFieldName);

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
	 * 取得子POJO对象或子集合实例
	 * </p>
	 */
	private Object getSubPojoOrCollection(Object parentPojo, String subPojoName)
			throws SecurityException, NoSuchMethodException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {

		String methodName = getMethoName("get", subPojoName);
		Method instanceMethod = parentPojo.getClass().getMethod(methodName,
				new Class[] {});

		return instanceMethod.invoke(parentPojo, new Object[] {});
	}

	/**
	 * <p>
	 * 取得XML节点名称
	 * </p>
	 * 
	 * @param classpath
	 *            当前节点POJO对象全路径
	 */
	private String getAliasName(String classpath) {

		Object aliasName = filter.get(classpath);
		if (LEVEL.equals(aliasName)) {
			return null;
		}

		if (aliasName == null) {
			int lastIndex = classpath.lastIndexOf(".");
			if (lastIndex < 0) {
				return classpath.toLowerCase();
			} else {
				return classpath.substring(lastIndex + 1, classpath.length())
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
	public String getMethoName(String methodPrefix, String fieldName) {
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
	private boolean isCollection(Class<?> clazz) {
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
	public boolean isBasicType(Class<?> clazz) {
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

	/**
	 * 取得条件过滤
	 */
	public Map<String, Object> getFilter() {
		return filter;
	}

	/**
	 * 设置条件过滤
	 */
	public void setFilter(Map<String, Object> filter) {
		this.filter = filter;
	}

}

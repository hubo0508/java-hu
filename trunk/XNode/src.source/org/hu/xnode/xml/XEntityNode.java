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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hu.xnode.util.BasicType;

import com.thoughtworks.xstream.XStream;

/**
 * @ClassName: XEntityNode
 * @Description: 根据实体类产生XML数据
 * @author HUBO hubo.0508@gmail.com
 * @date 2011-3-12 PM 10:43:40
 * 
 */
public class XEntityNode<T> {

	private static Map<String, Boolean> collection = new HashMap<String, Boolean>();

	public static Map<String, Object> replaceNode = new HashMap<String, Object>();

	static {
		collection.put("interface java.util.List", true);
		collection.put("interface java.util.Map", true);
		collection.put("interface java.util.Set", true);
	}

	@SuppressWarnings("unchecked")
	public static String entitiesIntoNodeList(List list) {

		StringBuffer sb = new StringBuffer();
		try {
			for (Object entity : list) {
				XStream xs = setAttribute(new XStream(), entity);
				sb.append(xs.toXML(entity) + "\n");
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			XEntityNode.replaceNode = null;
		}
		return sb.toString();
	}

	public static String entitiesIntoNode(Object entity) {
		try {
			XStream xs = setAttribute(new XStream(), entity);
			return xs.toXML(entity);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// ///////////////////////////////////////////////////////////////////////////////////

	@SuppressWarnings("unchecked")
	private static XStream setAttribute(XStream xStream, Object entity)
			throws Exception {

		Class entityCls = entity.getClass();
		xStream.alias(getAliasName(entityCls.getName()), entityCls);

		Field[] entityField = entityCls.getDeclaredFields();
		int len = entityField.length;
		for (int i = 0; i < len; i++) {
			Field field = entityField[i];
			String classStr = field.getType().toString();
			if (BasicType.isBasicType(field.getType())) {
				xStream.aliasAttribute(entityCls, field.getName(), field
						.getName());
			} else {
				Boolean collFlag = collection.get(classStr);
				if (collFlag != null) {
					collectionHandler(xStream, entity, field.getName());
				} else {
					Object subEntityItem = getSubCollectionItemOrSubEntity(
							entity, field.getName());
					if (subEntityItem != null) {
						setAttribute(xStream, subEntityItem);
					}
				}
			}
		}

		return xStream;
	}

	@SuppressWarnings( { "unchecked" })
	private static void collectionHandler(XStream xStream, Object entity,
			String fieldName) throws Exception {

		Class entityCls = entity.getClass();
		// xStream.addImplicitCollection(entityCls, fieldName);
		Boolean falg = (Boolean) replaceNode.get(fieldName);
		if (falg == null) {
			xStream.addImplicitCollection(entityCls, fieldName);
		} else {
			if (!falg) {
				xStream.addImplicitCollection(entityCls, fieldName);
			}
		}
		Object subCollectionItem = getSubCollectionItemOrSubEntity(entity,
				fieldName);

		// 判断具体类型
		if (subCollectionItem instanceof List) {
			List subListItem = (List) subCollectionItem;
			for (int j = 0; j < subListItem.size(); j++) {
				setAttribute(xStream, subListItem.get(j));
			}
		} else if (subCollectionItem instanceof Set) {
			Set subSetItem = (Set) subCollectionItem;
			for (Iterator iterator = subSetItem.iterator(); iterator.hasNext();) {
				setAttribute(xStream, iterator.next());
			}
		} else if (subCollectionItem instanceof Map) {

		}
	}

	@SuppressWarnings("unchecked")
	private static Object getSubCollectionItemOrSubEntity(Object entity,
			String fieldName) throws SecurityException, NoSuchMethodException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {

		Class entityCls = entity.getClass();

		String getMethodName = combinationMethod("get", fieldName);
		Method instanceMethod = entityCls.getMethod(getMethodName,
				new Class[] {});
		Object subCollectionItem = instanceMethod.invoke(entity,
				new Object[] {});

		return subCollectionItem;
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

				if (BasicType.isBasicType(field.getType())) {
					xStream.aliasAttribute(entityCls, field.getName(), field
							.getName());
				} else {

					String methodName = combinationMethod("get", field
							.getName());
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

	private static String combinationMethod(String methodPrefix,
			String fieldName) {
		String firstLetter = fieldName.substring(0, 1).toUpperCase();
		return methodPrefix + firstLetter + fieldName.substring(1);
	}

	private static String getAliasName(String path) {
		String aliasName = (String) replaceNode.get(path);

		if (aliasName == null) {
			int lastIndex = path.lastIndexOf(".");
			if (lastIndex < 0) {
				return path.toLowerCase();
			} else {
				return path.substring(lastIndex + 1, path.length())
						.toLowerCase();
			}
		} else {
			return aliasName;
		}
	}
}

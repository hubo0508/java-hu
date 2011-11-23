/**
 * @Project: MyProject
 * @Title: XMLUtils.java
 * @Package com.xstream
 * @Description: TODO
 * @author HUBO hubo.0508@gmail.com  
 * @date 2010-12-28
 * @version V1.0  
 */
package org.hu.xnode.util;

import java.util.Date;

/**
 * 
 * @ClassName: BasicTypeUtils 
 * @Description: TODO
 * @author HUBO hubo.0508@gmail.com 
 * @date 2010-12-28
 *
 */
public class BasicType {	

	@SuppressWarnings("unchecked")
	public static boolean isBasicType(Class clazz) {
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
}

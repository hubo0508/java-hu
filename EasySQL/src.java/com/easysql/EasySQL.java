package com.easysql;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.easysql.core.Entity;

public class EasySQL {

	public final static String FIELD_RULE_HUMP = "hump";

	public final static String FIELD_RULE_SEGMENTATION = "segmentation";

	public static String key(Class<?> clazz) {
		return clazz.getCanonicalName() + "." + Entity.NOT_TAKE;
	}
	
	public static String replaceFiled(String[] replaceValue, String text) {

		String returnvalue = text;
		for (String string : replaceValue) {
			if (StringUtils.isNotEmpty(string)) {
				String[] value = string.split(":");
				if (value[0].equals(text)) {
					returnvalue = value[1];
				}
			}
		}

		return returnvalue;
	}

	public static String convertedIntoSegmentation(String text) {

		StringBuffer sb = new StringBuffer();
		int cacheIndex = 0;

		Pattern p = Pattern.compile("[A-Z]");
		Matcher m = p.matcher(text);
		while (m.find()) {
			String value = text.substring(cacheIndex, m.start());
			if (StringUtils.isEmpty(value)) {
				break;
			}
			sb.append(value);
			sb.append("_");
			cacheIndex = m.start();
		}
		sb.append(text.substring(cacheIndex));

		return sb.toString().toLowerCase();
	}
}

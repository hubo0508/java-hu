package com.easysql.engine.sql;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.easysql.core.Mapping;
import com.easysql.engine.xml.XmlNamespace;
import com.easysql.util.Const;
import com.easysql.util.StringUtil;

public class NameEngine {

	public static String[] changeFileds(String[] elements) {

		String fieldRule = (String) Mapping.getInstance().get(
				XmlNamespace.FIELD_RULE);

		if (Const.FIELD_RULE_HUMP.equals(fieldRule)) {
			return elements;
		}

		if (Const.FIELD_RULE_SEGMENTATION.equals(fieldRule)) {
			for (int i = 0; i < elements.length; i++) {
				elements[i] = StringUtil.convertedIntoSegmentation(elements[i]);
			}
			return elements;
		}

		return elements;
	}

	public static void main(String[] args) {
		String text = "accountName";
		//
		// String[] strarr = str.split(":");
		//
		// System.out.println("");

		StringBuffer sb = new StringBuffer();
		String expression = "[A-Z]";
		int cacheIndex = 0;

		Pattern p = Pattern.compile(expression);
		Matcher m = p.matcher(text);
		while (m.find()) {
			sb.append(text.substring(cacheIndex, m.start()));
			sb.append("_");
			cacheIndex = m.start();
		}
		sb.append(text.substring(cacheIndex));

		System.out.println(sb.toString().toLowerCase());
	}
}

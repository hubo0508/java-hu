package com.easysql.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class StringUtil {

	public static void main(String[] args) {
		String text = "Account";

		System.out.println(StringUtil.convertedIntoSegmentation(text));
	}

	public static String convertedIntoSegmentation(String text) {
		StringBuffer sb = new StringBuffer();
		String expression = "[A-Z]";
		int cacheIndex = 0;

		Pattern p = Pattern.compile(expression);
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

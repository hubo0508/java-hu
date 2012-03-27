package com.easysql.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

	public static String convertedIntoSegmentation(String text) {
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

		return sb.toString().toLowerCase();
	}
}

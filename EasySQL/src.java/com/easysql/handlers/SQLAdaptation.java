package com.easysql.handlers;

import com.easysql.EasySQL;
import com.easysql.core.Mapping;

public class SQLAdaptation {

	public static String convertedSingleField(Class<?> clazz, String elements) {

		String nameRule = (String) Mapping.getInstance()
				.get(EasySQL.FIELD_RULE);
		EntityFilter targetMap = (EntityFilter) Mapping.getInstance().get(
				EasySQL.key(clazz));
		String[] replaceValue = (String[]) targetMap.get(EntityFilter.REPLACE);

		return convertedAfterElement(elements, replaceValue, nameRule);
	}

	public static String[] convertedFileds(Class<?> clazz, String[] elements) {

		String nameRule = (String) Mapping.getInstance()
				.get(EasySQL.FIELD_RULE);
		EntityFilter targetMap = (EntityFilter) Mapping.getInstance().get(
				EasySQL.key(clazz));
		String[] replaceValue = (String[]) targetMap.get(EntityFilter.REPLACE);

		if (EasySQL.FIELD_RULE_HUMP.equals(nameRule)) {
			return elements;
		}

		for (int i = 0; i < elements.length; i++) {
			elements[i] = convertedAfterElement(elements[i], replaceValue,
					nameRule);
		}

		return elements;
	}

	public static String convertedAfterElement(String ele,
			String[] replaceValue, String nameRule) {

		if (EasySQL.FIELD_RULE_HUMP.equals(nameRule)) {
			return ele;
		}

		if (replaceValue != null) {
			ele = EasySQL.replaceFiled(replaceValue, ele);
		}

		if (EasySQL.FIELD_RULE_SEGMENTATION.equals(nameRule)) {
			ele = EasySQL.convertedIntoSegmentation(ele);
		}

		return ele;
	}
}

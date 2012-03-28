package com.easysql.handlers;

import com.easysql.EasySQL;
import com.easysql.core.Mapping;

public class SQLAdaptation {

	public static String[] convertedFileds(Class<?> clazz, String[] elements) {

		String fieldRule = (String) Mapping.getInstance().get(
				EasySQL.FIELD_RULE);
		EntityFilter targetMap = (EntityFilter) Mapping.getInstance().get(EasySQL.key(clazz));
		String[] replaceValue = (String[]) targetMap.get(EntityFilter.REPLACE);

		if (EasySQL.FIELD_RULE_HUMP.equals(fieldRule)) {
			return elements;
		}

		for (int i = 0; i < elements.length; i++) {
			String rowFiled = elements[i];
			if (replaceValue != null) {
				rowFiled = EasySQL.replaceFiled(replaceValue, rowFiled);
			}
			if (EasySQL.FIELD_RULE_SEGMENTATION.equals(fieldRule)) {
				rowFiled = EasySQL.convertedIntoSegmentation(rowFiled);
			}
			elements[i] = rowFiled;
		}

		return elements;
	}

}

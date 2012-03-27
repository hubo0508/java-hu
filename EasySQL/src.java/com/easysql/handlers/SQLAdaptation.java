package com.easysql.handlers;

import com.easysql.EasySQL;
import com.easysql.IfMap;
import com.easysql.core.Mapping;

public class SQLAdaptation {

	public static String[] changeFileds(Class<?> clazz, String[] elements) {

		String fieldRule = (String) Mapping.getInstance().get(
				EasySQL.FIELD_RULE);
		IfMap targetMap = (IfMap) Mapping.getInstance().get(EasySQL.key(clazz));
		String[] replaceValue = (String[]) targetMap.get(IfMap.REPLACE);

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

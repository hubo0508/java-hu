package com.easysql.handlers;

import com.easysql.Const;
import com.easysql.StringUtil;
import com.easysql.core.Mapping;
import com.easysql.core.object.IfMap;
import com.easysql.engine.xml.XmlNamespace;

public class SQLAdaptation {

	public static String[] changeFileds(Class<?> clazz, String[] elements) {

		String fieldRule = (String) Mapping.getInstance().get(
				XmlNamespace.FIELD_RULE);
		IfMap targetMap = (IfMap) Mapping.getInstance().get(Const.key(clazz));
		String[] replaceValue = (String[]) targetMap.get(IfMap.REPLACE);

		if (Const.FIELD_RULE_HUMP.equals(fieldRule)) {
			return elements;
		}

		for (int i = 0; i < elements.length; i++) {
			String rowFiled = elements[i];
			if (replaceValue != null) {
				rowFiled = StringUtil.replaceFiled(replaceValue, rowFiled);
			}
			if (Const.FIELD_RULE_SEGMENTATION.equals(fieldRule)) {
				rowFiled = StringUtil.convertedIntoSegmentation(rowFiled);
			}
			elements[i] = rowFiled;
		}

		return elements;
	}

}

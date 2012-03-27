package com.easysql.engine.sql;

import com.easysql.core.Mapping;
import com.easysql.engine.xml.XmlNamespace;
import com.easysql.util.Const;
import com.easysql.util.StringUtil;

public class SQLAdaptation {

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

}

package com.easysql;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.easysql.core.Entity;

public class EasySQL {

	public final static String FIELD_RULE_HUMP = "hump";

	public final static String FIELD_RULE_SEGMENTATION = "segmentation";

	/**
	 * 
	 * 1) assigned 主键由外部程序负责生成，无需Hibernate参与。 2) hilo 通过hi/lo
	 * 算法实现的主键生成机制，需要额外的数据库表保存主 键生成历史状态。 3) seqhilo 与hilo 类似，通过hi/lo
	 * 算法实现的主键生成机制，只是主键历史 状态保存在Sequence中，适用于支持Sequence的数据库，如Oracle。 4) increment
	 * 主键按数值顺序递增。此方式的实现机制为在当前应用实例中维持 一个变量，以保存着当前的最大值，之后每次需要生成主键的时候 将此值加1作为主键。
	 * 这种方式可能产生的问题是：如果当前有多个实例访问同一个数据 库，那么由于各个实例各自维护主键状态，不同实例可能生成同样
	 * 的主键，从而造成主键重复异常。因此，如果同一数据库有多个实 例访问，此方式必须避免使用。 5) identity
	 * 采用数据库提供的主键生成机制。如DB2、SQL Server、MySQL 中的主键生成机制。 6) sequence
	 * 采用数据库提供的sequence 机制生成主键。如Oralce 中的 Sequence。 7) native
	 * 由Hibernate根据底层数据库自行判断采用identity、hilo、sequence 其中一种作为主键生成方式。 8) uuid.hex
	 * 由Hibernate基于128 位唯一值产生算法生成16 进制数值（编码后 以长度32 的字符串表示）作为主键。 9) uuid.string
	 * 与uuid.hex 类似，只是生成的主键未进行编码（长度16）。在某些 数据库中可能出现问题（如PostgreSQL）。 10) foreign
	 * 使用外部表的字段作为主键。 一般而言，利用uuid.hex方式生成主键将提供最好的性能和数据库平台适 应性。
	 */

	/** ************************************************************************************************************************************** */

	public static final String GENERATOR = "easysql-mapping/generator";
	
	public static final String GENERATOR_SEQUENCE = "easysql-mapping/generator/sequence";

	public static final String ENTITY = "easysql-mapping/entitys/entity";

	public static final String FIELD_RULE = "easysql-mapping/field-rule";

	public static final String DATABASE = "easysql-mapping/database";

	/** ************************************************************************************************************************************** */

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

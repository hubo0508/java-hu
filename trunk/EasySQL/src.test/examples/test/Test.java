package examples.test;

import com.easysql.engine.xml.XmlAnalyze;
import com.easysql.handlers.EntityEngine;
import com.easysql.handlers.SQLEngine;

import examples.domain.Device;

public class Test {
	public static void main(String[] args) throws InstantiationException,
			IllegalAccessException {

		new XmlAnalyze().init();		

		String sql = SQLEngine.getInsertSQL(new EntityEngine(Device.class));

		System.out.println(sql);

	}
}

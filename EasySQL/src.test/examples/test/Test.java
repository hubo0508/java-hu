package examples.test;

import com.easysql.handlers.EntityHandler;
import com.easysql.handlers.SQLHandler;
import com.easysql.xml.XmlAnalyze;

import examples.domain.Device;

public class Test {
	public static void main(String[] args) throws InstantiationException,
			IllegalAccessException {

		new XmlAnalyze().init();		

		String sql = SQLHandler.getInsertSQL(new EntityHandler(Device.class));

		System.out.println(sql);

	}
}

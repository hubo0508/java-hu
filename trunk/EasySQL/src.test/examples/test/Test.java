package examples.test;

import com.easysql.engine.sql.SQLEngine;
import com.easysql.engine.xml.XmlAnalyze;
import com.easysql.orm.EntityEngine;

import examples.domain.Device;

public class Test {
	public static void main(String[] args) throws InstantiationException,
			IllegalAccessException {

		new XmlAnalyze().init();		

		EntityEngine ref = new EntityEngine(Device.class);

		String sql = SQLEngine.getInsertSQL(ref.getRowField());

		System.out.println(sql);
		
		Device ob =null;
		
		if(Device.class.getSuperclass().isInstance(ob)){
			System.out.println("'");
		}
				
		System.out.println(Device.class.getSuperclass().getCanonicalName());
	}
}

package examples.test;

import com.easysql.handlers.EntityHandler;
import com.easysql.handlers.SQLHandler;

import examples.BaseTest;
import examples.domain.Device;

public class Test extends BaseTest {
	public static void main(String[] args) throws InstantiationException,
			IllegalAccessException {

		 String sql = SQLHandler.getInsertSQL(new EntityHandler(Device.class));

		System.out.println(sql);

	}
}

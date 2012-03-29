package examples.test;

import com.easysql.handlers.EntityHandler;
import com.easysql.handlers.SQLHandler;

import examples.BaseTest;
import examples.dhome.domain.Device;
import examples.dhome.domain.User;

public class Test extends BaseTest {

	public static void main(String[] args) throws InstantiationException,
			IllegalAccessException {

		Test test = new Test();
		// System.out.println("***************updateSQL***************");
		// test.updateSQL();
		// System.out.println("***************insertSQL***************");
		// test.insertSQL();
		// System.out.println("***************deleteSQL***************");
		// test.deleteSQL();
		
		test.selectSQL();
	}
	
	public void selectSQL() {
		
		String sql = "select * from Device";
		
		sql = SQLHandler.selectSQL(new EntityHandler(Device.class),sql);

		System.out.println(sql);
	}

	public void deleteSQL() {
		String sql = SQLHandler.deleteSQL(new EntityHandler(Device.class));
		System.out.println(sql);

		sql = SQLHandler.deleteSQL(new EntityHandler(Device.class),
				"factoryCode=? AND deviceCode=?");
		System.out.println(sql);
	}

	public void updateSQL() {
		String sql = SQLHandler.updateSQL(new EntityHandler(Device.class),
				"id=?");

		System.out.println(sql);

		String[] ifmap = new String[] { "accountName", "accountPass" };

		sql = SQLHandler.updateSQL(new EntityHandler(User.class), ifmap);

		System.out.println(sql);
	}

	public void insertSQL() {
		String sql = SQLHandler.insertSQL(new EntityHandler(Device.class));

		System.out.println(sql);

	}
}

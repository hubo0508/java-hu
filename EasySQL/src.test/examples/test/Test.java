package examples.test;

import org.easysql.handlers.EntityHandler;
import org.easysql.handlers.SQLHandler;

import examples.BaseTest;
import examples.dhome.domain.Device;

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

		SQLHandler sqlHandler = new SQLHandler(new EntityHandler(Device.class));
		sql = sqlHandler.getSelectSQL(sql);

		System.out.println(sql);
	}

	public void deleteSQL() {
		SQLHandler sqlHandler = new SQLHandler(new EntityHandler(Device.class));
		String sql = sqlHandler.getDeleteSQL();
		System.out.println(sql);

		sql = sqlHandler.getDeleteSQL("factoryCode=? AND deviceCode=?");
		System.out.println(sql);
	}

	public void updateSQL() {
		SQLHandler sqlHandler = new SQLHandler(new EntityHandler(Device.class));
		String sql = sqlHandler.getUpdateSQL("id=?");

		System.out.println(sql);

		String[] ifmap = new String[] { "accountName", "accountPass" };

		sql = sqlHandler.getUpdateSQL(ifmap);

		System.out.println(sql);
	}

	public void insertSQL() {
		SQLHandler sqlHandler = new SQLHandler(new EntityHandler(Device.class));
		String sql = sqlHandler.getInsertSQL();

		System.out.println(sql);

	}
}

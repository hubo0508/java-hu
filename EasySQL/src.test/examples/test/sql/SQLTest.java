package examples.test.sql;

import org.easysql.handlers.EntityHandler;
import org.easysql.handlers.SQLHandler;

import examples.BaseTest;
import examples.dhome.domain.Device;
import examples.dhome.domain.User;

public class SQLTest extends BaseTest {

	public static void main(String[] args) {

		SQLTest test = new SQLTest();
		// System.out.println("***************updateSQL***************");
		// test.updateSQL();
		// System.out.println("***************insertSQL***************");
		// test.insertSQL();
		// System.out.println("***************deleteSQL***************");
		// test.deleteSQL();

		test.selectSQL();
	}

	public void selectSQL() {

		SQLHandler sqlHandler = new SQLHandler(new EntityHandler(User.class));
		
		String sql = sqlHandler.getSelectSQL("select * from User");
		System.out.println(sql);
		
		sql = sqlHandler.getSelectSQL("from User");
		System.out.println(sql);
		
		sql = sqlHandler.getSelectSQL();
		System.out.println(sql);
		
		sql = sqlHandler.getSelectSQLById();
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

package examples.test;

import com.easysql.handlers.EntityHandler;
import com.easysql.handlers.SQLHandler;

import examples.BaseTest;
import examples.domain.Account;
import examples.domain.Device;
import examples.domain.User;

public class Test extends BaseTest {

	// UPDATE 表名称 SET 列名称 = 新值 WHERE 列名称 = 某值
	public static void main(String[] args) throws InstantiationException,
			IllegalAccessException {

		Test test = new Test();
		System.out.println("***************updateSQL***************");
		test.updateSQL();
		System.out.println("***************insertSQL***************");
		test.insertSQL();

	}

	public void updateSQL() {
		String sql = SQLHandler.getUpdateSQL(new EntityHandler(Device.class),
				"id=?");

		System.out.println(sql);

		sql = SQLHandler.getUpdateSQL(new EntityHandler(User.class), "id=?");

		System.out.println(sql);

		sql = SQLHandler.getUpdateSQL(new EntityHandler(Account.class), "id=?");

		System.out.println(sql);

	}

	public void insertSQL() {
		String sql = SQLHandler.getInsertSQL(new EntityHandler(Device.class));

		System.out.println(sql);

		sql = SQLHandler.getInsertSQL(new EntityHandler(User.class));

		System.out.println(sql);

		sql = SQLHandler.getInsertSQL(new EntityHandler(Account.class));

		System.out.println(sql);

	}
}

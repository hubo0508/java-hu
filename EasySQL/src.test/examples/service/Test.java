package examples.service;

import com.easysql.core.Entity;
import com.easysql.engine.sql.SQLEngine;
import com.easysql.orm.EntityEngine;

import examples.domain.Account;
import examples.domain.User;

public class Test {
	public static void main(String[] args) throws InstantiationException,
			IllegalAccessException {

		// String sql = "select * from account a left join user u";

		EntityEngine ref = new EntityEngine(Account.class);

		// String SQLS = "inster into (";
		// String[] files = ref.getRowField();
		// for (String s : files) {
		// if (s != null) {
		// SQLS += s+", ";
		// }
		// }
		//
		// SQLS += ") value";

		String sql = SQLEngine.getInsertSQL("account", ref.getRowField());

		System.out.println(sql);
	}
}

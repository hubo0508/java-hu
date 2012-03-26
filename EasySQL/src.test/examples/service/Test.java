package examples.service;

import com.easysql.engine.sql.SQLEngine;
import com.easysql.engine.xml.NodeEngine;
import com.easysql.orm.EntityEngine;

import examples.domain.Account;
import examples.domain.User;

public class Test {
	public static void main(String[] args) throws InstantiationException,
			IllegalAccessException {

		new NodeEngine().init();

		EntityEngine ref = new EntityEngine(Account.class);

		String sql = SQLEngine.getInsertSQL(ref.getRowField());

		System.out.println(sql);
	}
}

package examples.service;

import com.easysql.engine.sql.SQLEngine;
import com.easysql.engine.xml.NodeEngine;
import com.easysql.orm.EntityEngine;

import examples.domain.User;

public class Test {
	public static void main(String[] args) throws InstantiationException,
			IllegalAccessException {

		new NodeEngine().init();

		EntityEngine ref = new EntityEngine(User.class);

		String sql = SQLEngine.getInsertSQL("User", ref.getRowField());

		System.out.println(sql);
	}
}

package examples.service;

import java.util.Map;

import com.easysql.core.SqlMap;
import com.easysql.engine.sql.SQLEngine;
import com.easysql.engine.xml.NodeEngine;
import com.easysql.engine.xml.NodeNamespace;
import com.easysql.orm.EntityEngine;

import examples.domain.Account;
import examples.domain.User;

public class Test {
	public static void main(String[] args) throws InstantiationException,
			IllegalAccessException {

		new NodeEngine().init();

		EntityEngine ref = new EntityEngine(Account.class);

		Map m = SqlMap.getInstance().getSqlMap();

		String sql = SQLEngine.getInsertSQL("account", ref.getRowField());

		System.out.println(sql);
	}
}

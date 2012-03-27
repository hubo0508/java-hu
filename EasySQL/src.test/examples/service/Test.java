package examples.service;

import com.easysql.core.Mapping;
import com.easysql.engine.sql.SQLEngine;
import com.easysql.engine.xml.XmlAnalyze;
import com.easysql.engine.xml.XmlNamespace;
import com.easysql.orm.EntityEngine;

import examples.domain.Account;
import examples.domain.User;

public class Test {
	public static void main(String[] args) throws InstantiationException,
			IllegalAccessException {

		new XmlAnalyze().init();		

		EntityEngine ref = new EntityEngine(Account.class);

		String sql = SQLEngine.getInsertSQL(ref.getRowField());

		System.out.println(sql);
	}
}

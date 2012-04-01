package examples;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysql.EasySQL;
import org.easysql.core.Mapping;
import org.easysql.xml.XmlAnalyze;

import examples.dhome.pool.DBPool;
import examples.dhome.pool.MySqlPool;
import examples.dhome.pool.OraclePool;
import examples.dhome.pool.SQLServerPool;

public class BaseTest {

	protected static Log log = LogFactory.getLog(BaseTest.class);

	protected static DBPool pool = null;

	static {
		new XmlAnalyze().init();

		String database = (String) Mapping.getInstance().get(EasySQL.DATABASE);
		if (EasySQL.ORACLE.equals(database)) {
			pool = OraclePool.getInstance();
		} else if (EasySQL.MYSQL.equals(database)) {
			pool = MySqlPool.getInstance();
		} else if (EasySQL.SQLSERVICE.equals(database)) {
			pool = SQLServerPool.getInstance();
		}
	}

	public static void main(String[] args) {
		// System.out.println((double)15/(double)4);

	}

}

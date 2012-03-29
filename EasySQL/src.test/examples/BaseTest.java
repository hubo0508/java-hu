package examples;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.easysql.xml.XmlAnalyze;

import examples.dhome.pool.MySqlPool;

public class BaseTest {
	
	protected static Log log = LogFactory.getLog(BaseTest.class);
	
	static {
		new XmlAnalyze().init();
		MySqlPool.getInstance();
	}
}

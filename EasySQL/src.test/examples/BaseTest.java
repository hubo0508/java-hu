package examples;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysql.xml.XmlAnalyze;

import examples.dhome.pool.DBPool;
import examples.dhome.pool.MySqlPool;

public class BaseTest {

	protected static Log log = LogFactory.getLog(BaseTest.class);
	
	

	static {
		new XmlAnalyze().init();
		// MySqlPool.getInstance();
	}

	public static void main(String[] args) {
		//System.out.println((double)15/(double)4);
		
	}


}

package examples;

import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysql.xml.XmlAnalyze;

public class BaseTest {

	protected static Log log = LogFactory.getLog(BaseTest.class);

	static {
		// new XmlAnalyze().init();
		// MySqlPool.getInstance();
	}

	public static void main(String[] args) {
		String sql = "UPDATE user SET id=?, username=?, password=? WHERE id in(?)";

	}

	
}

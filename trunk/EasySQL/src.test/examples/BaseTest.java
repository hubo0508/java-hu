package examples;

import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysql.xml.XmlAnalyze;

public class BaseTest {

	protected static Log log = LogFactory.getLog(BaseTest.class);

	static {
		//new XmlAnalyze().init();
		// MySqlPool.getInstance();
	}
	
	public static void main(String[] args) {
		String sql = "UPDATE user SET id=?, username=?, password=? WHERE username=? and password=?";
		
		StringTokenizer stk = new StringTokenizer(sql, " ");
		int len = stk.countTokens();
		String[] sqlArray = new String[len];
		for (int i = 0; i < len; i++) {
			System.out.println();
		}
		
		System.out.println();
	}
}

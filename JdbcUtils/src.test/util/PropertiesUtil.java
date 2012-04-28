package util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * <p>
 * 属性文件读取
 * </p>
 * 
 * @User: HUBO
 * @Date Feb 29, 2012
 * @Time 3:45:02 PM
 * 
 */
public class PropertiesUtil {

	Logger log = Logger.getLogger(PropertiesUtil.class);

	private static Object initLock = new Object();

	private static PropertiesUtil proUtil = null;

	private Properties props = null;

	private static String cachePath;

	public static PropertiesUtil getInstance() {
		if (proUtil == null) {
			synchronized (initLock) {
				if (proUtil == null) {
					proUtil = new PropertiesUtil();
				}
			}
		}
		return proUtil;
	}

	private synchronized void loadProperties(String typeOrPath)
			throws IOException {

		this.log.info("typeOrPath : " + typeOrPath);

		cachePath = typeOrPath;

		props = new Properties();
		props.load(new FileInputStream(typeOrPath));

	}

	public String getProperty(String typeOrPath, String key) {

		if (proUtil == null) {
			proUtil = getInstance();
		}

		try {
			if (props == null || !typeOrPath.equals(cachePath)) {
				loadProperties(typeOrPath);
			}
			return props.getProperty(key);
		} catch (IOException e) {
			log.info(e.getMessage(), e);
			throw new RuntimeException(e.getMessage());
		}
	}

	public void setProperty(String path, String key, String value)
			throws IOException {
		try {
			if (props == null) {
				loadProperties(path);
			}
			props.setProperty(key, value);

			saveConfig(path);
		} catch (Exception e) {
			log.info(e.getMessage(), e);
			throw new IOException(e.getMessage());
		}
	}

	public void saveConfig(String filePath) throws IOException {
		OutputStream outputStream = new FileOutputStream(filePath);
		props.store(outputStream, "set");
		outputStream.close();
	}

	public static void main(String[] args) throws IOException {
		String path = "D:\\temp\\UUID.properties";

		PropertiesUtil.getInstance().setProperty(path, "age", "asdfasdfasdf");

		System.out.println(PropertiesUtil.getInstance()
				.getProperty(path, "age"));

	}

}

package com.easysql;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * <p>
 * �����ļ���ȡ
 * </p>
 * 
 * @User: HUBO
 * @Date Feb 29, 2012
 * @Time 3:45:02 PM
 * 
 */
public class ReadProperties {

	Logger log = Logger.getLogger(ReadProperties.class);

	private static Object initLock = new Object();

	private static ReadProperties proUtil = null;

	private Properties props = null;

	private static String cachePath;

	public static ReadProperties getInstance() {
		if (proUtil == null) {
			synchronized (initLock) {
				if (proUtil == null) {
					proUtil = new ReadProperties();
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

		// if (Const.JDBC.equals(typeOrPath)) {
		// InputStream is = getClass().getResourceAsStream(
		// "/properties/jdbc.properties");
		// props.load(is);
		// } else {
		props.load(new FileInputStream(typeOrPath));
		// }

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

		ReadProperties.getInstance().setProperty(path, "age", "asdfasdfasdf");

		System.out.println(ReadProperties.getInstance()
				.getProperty(path, "age"));

	}

}

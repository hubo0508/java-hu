package org.db.jdbcutils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class PropertiesFile {
	Logger log = Logger.getLogger(PropertiesFile.class);

	private static String cachePath;

	private static class Instance {
		public final static PropertiesFile pro = new PropertiesFile();
		public static Properties props = new Properties();
	}

	public static PropertiesFile getInstance() {
		return Instance.pro;
	}

	private static Properties getProps() {
		return Instance.props;
	}

	private synchronized void loadProperties(String typeOrPath)
			throws IOException {

		cachePath = typeOrPath;
		getProps().load(new FileInputStream(typeOrPath));

	}

	public String getProperty(String typeOrPath, String key) {
		try {
			if (getProps() == null || !typeOrPath.equals(cachePath)) {
				loadProperties(typeOrPath);
			}
			return getProps().getProperty(key);
		} catch (IOException e) {
			log.info(e.getMessage(), e);
			throw new RuntimeException(e.getMessage());
		}
	}

	public void setProperty(String path, String key, String value)
			throws IOException {
		try {
			if (getProps() == null) {
				loadProperties(path);
			}
			getProps().setProperty(key, value);

			saveConfig(path);
		} catch (Exception e) {
			log.info(e.getMessage(), e);
			throw new IOException(e.getMessage());
		}
	}

	public void saveConfig(String filePath) throws IOException {
		OutputStream outputStream = new FileOutputStream(filePath);
		getProps().store(outputStream, "set");
		outputStream.close();
	}

	public static void main(String[] args) throws IOException {
		String path = "D:\\temp\\UUID.properties";

		PropertiesFile.getInstance().setProperty(path, "age", "asdfasdfasdf");

		System.out.println(PropertiesFile.getInstance()
				.getProperty(path, "age"));

	}
}

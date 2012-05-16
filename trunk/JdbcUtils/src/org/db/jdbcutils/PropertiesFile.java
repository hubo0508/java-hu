package org.db.jdbcutils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * 属性文件的读取
 */
public class PropertiesFile {

	/**
	 * 日志输出
	 */
	protected Logger log = Logger.getLogger(PropertiesFile.class);

	/**
	 * 文件路径
	 */
	public static String url;

	public PropertiesFile() {
	}

	/**
	 * 构造函数设置文件路径
	 */
	public PropertiesFile(String url) {
		if (PropertiesFile.url != null && !PropertiesFile.url.equals(url)) {
			PropertiesFile.url = url;
		}
	}

	/**
	 * 取得单例缓存
	 */
	private static class Instance {
		public final static PropertiesFile pro = new PropertiesFile();
		public final static Properties props = new Properties();
	}

	/**
	 * 取得单例
	 */
	public static PropertiesFile getInstance() {
		return Instance.pro;
	}

	/**
	 * 取得单例
	 */
	public static PropertiesFile getInstance(String url) {
		if (url != null && !url.equals(PropertiesFile.url)) {
			PropertiesFile.url = url;
		}
		return Instance.pro;
	}

	/**
	 * 加载属性文件
	 */
	private synchronized void loadProperties(String url) {
		FileInputStream fileStr = null;
		try {
			fileStr = new FileInputStream(url);
			Instance.props.load(fileStr);
		} catch (FileNotFoundException e) {
			log.error(e.getMessage(), e);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		} finally {
			log.info("URI:"+url);
			closeIS(fileStr);
		}
	}

	/**
	 * 取值
	 */
	public String getValue(String key) {
		return getValue(PropertiesFile.url, key);
	}

	/**
	 * 取值
	 */
	public String getValue(String url, String key) {

		if (url == null) {
			throw new RuntimeException("加载配置文件路径为空。");
		}

		loadProperties(url);
		if (!url.equals(PropertiesFile.url)) {
			PropertiesFile.url = url;
		}

		return Instance.props.getProperty(key);
	}

	/**
	 * 设置值
	 */
	public void setValue(String key, String value) {
		this.setValue(PropertiesFile.url, key, value);
	}

	/**
	 * 设置值
	 */
	public void setValue(String url, String key, String value) {
		if (url == null) {
			throw new RuntimeException("加载配置文件路径为空。");
		}
		Instance.props.setProperty(key, value);
		saveValue(url);
	}

	/**
	 * 将值写入到硬盘
	 */
	private void saveValue(String url) {
		OutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(url);
			Instance.props.store(outputStream, "set");
		} catch (FileNotFoundException e) {
			log.error(e.getMessage(), e);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		} finally {
			closeOS(outputStream);
		}
	}

	/**
	 * 关闭输出流连接通道
	 */
	private void closeOS(OutputStream outputStream) {
		if (outputStream == null) {
			return;
		}
		try {
			outputStream.close();
		} catch (IOException e) {
		}
	}

	/**
	 * 关闭输入流连接通道
	 */
	private void closeIS(FileInputStream fileStr) {
		if (fileStr == null) {
			return;
		}
		try {
			fileStr.close();
		} catch (IOException e) {
		}
	}

	public static void main(String[] args) throws IOException {
		String url = "D:\\work\\myeclipse6.6\\JdbcUtils\\src.test\\jdbc.properties";
		// PropertiesFile.getInstance().setValue(path, "age", "asdfasdfasdf");
		PropertiesFile pro = PropertiesFile.getInstance(url);
		System.out.println(pro.getValue("sqlserver.jdbc.password"));
	}
}

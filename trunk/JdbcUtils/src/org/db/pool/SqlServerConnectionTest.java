package org.db.pool;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;

public class SqlServerConnectionTest {

	private static String FORMAT = "yyyy/MM/dd HH:mm:ss ms";

	DBPool pool = SqlServerPool.getInstance();

	Map map = new LinkedHashMap();

	// dataSource = (BasicDataSource)BasicDataSourceFactory.createDataSource(p);
	public static void main(String[] args) {
		
		SqlServerConnectionTest test = new SqlServerConnectionTest();
		test.createConnection();
		test.map.put("主线程 Create a successful!", new Date());
	}

	private static void printTime(String msg) {
		SimpleDateFormat df = new SimpleDateFormat(FORMAT);
		df.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
		System.out.println(msg + df.format(new Date()));
	}

	public void createConnection() {
		Connection conn = null;
		try {
			conn = pool.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(conn);
		}
	}

	// //////////////////同时开启多个线程(A~H)，测试连接/////////////////////////////

	// 线程测试A
	public class ThreadA extends Thread {
		public void run() {
			createConnection();
			map.put("ThreadA Create a successful!", new Date());
		}
	}

	// 线程测试B
	public class ThreadB extends Thread {
		public void run() {
			createConnection();
			map.put("ThreadB Create a successful!", new Date());
		}
	}

	// 线程测试C
	public class ThreadC extends Thread {
		public void run() {
			createConnection();
			map.put("ThreadC Create a successful!", new Date());
		}
	}

	// 线程测试D
	public class ThreadD extends Thread {
		public void run() {
			createConnection();
			map.put("ThreadD Create a successful!", new Date());
		}
	}

	// 线程测试E
	public class ThreadF extends Thread {
		public void run() {
			createConnection();
			map.put("ThreadF Create a successful!", new Date());
		}
	}

	// 线程测试G
	public class ThreadG extends Thread {
		public void run() {
			createConnection();
			map.put("ThreadG Create a successful!", new Date());
		}
	}

	// 线程测试H
	public class ThreadH extends Thread {
		public void run() {
			createConnection();
			map.put("ThreadH Create a successful!", new Date());
		}
	}
}

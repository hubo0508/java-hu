package org.db.pool;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.Map.Entry;

public class SqlServerConnectionTest {

	private static String FORMAT = "yyyy/MM/dd HH:mm:ss ms";

	//DBPool pool = SqlServerPool.getInstance();

	Map map = new LinkedHashMap();

	// dataSource = (BasicDataSource)BasicDataSourceFactory.createDataSource(p);
	public static void main(String[] args) throws InterruptedException,
			SQLException {

		SqlServerConnectionTest test = new SqlServerConnectionTest();
		test.createConnection();
		test.map.put("主线程 Create a successful!", new Date());
		
		System.out.println("主线程 Create a successful!");
		SqlServerPool.getInstance().printDataSourceStats();
		
		test.setupThread();

		Thread.sleep(10 * 1000);
		
		System.out.println("colse.");
		SqlServerPool.getInstance().close();

		Iterator it = test.map.entrySet().iterator();
		while (it.hasNext()) {
			Entry entry = (Entry) it.next();
			String msg = timeToString((Date) entry.getValue());
			System.out.println(msg + " | " + entry.getKey());
		}
	}
	
	private void setupThread(){
		new ThreadA().start();
		new ThreadB().start();
		new ThreadC().start();
		new ThreadD().start();
		new ThreadE().start();
		new ThreadF().start();
		new ThreadG().start();
		new ThreadH().start();
		new ThreadI().start();
	}

	private static String timeToString(Date d) {
		SimpleDateFormat df = new SimpleDateFormat(FORMAT);
		df.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
		return df.format(d);
	}

	public void createConnection() {
		Connection conn = null;
		try {
			conn = SqlServerPool.getInstance().getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			SqlServerPool.getInstance().freeConnection(conn);
		}
	}

	// //////////////////同时开启多个线程(A~H)，测试连接/////////////////////////////

	// 线程测试A
	class ThreadA extends Thread {
		public void run() {
			createConnection();
			map.put("ThreadA Create a successful!", new Date());

			System.out.println("ThreadA Create a successful!");
			SqlServerPool.getInstance().printDataSourceStats();
		}
	}

	// 线程测试B
	public class ThreadB extends Thread{
		public void run() {
			createConnection();
			map.put("ThreadB Create a successful!", new Date());
			
			System.out.println("ThreadB Create a successful!");
			SqlServerPool.getInstance().printDataSourceStats();
		}
	}

	// 线程测试C
	public class ThreadC extends Thread{
		public void run() {
			createConnection();
			map.put("ThreadC Create a successful!", new Date());
			
			System.out.println("ThreadC Create a successful!");
			SqlServerPool.getInstance().printDataSourceStats();
		}
	}

	// 线程测试D
	public class ThreadD extends Thread{
		public void run() {
			createConnection();
			map.put("ThreadD Create a successful!", new Date());
			
			System.out.println("ThreadD Create a successful!");
			SqlServerPool.getInstance().printDataSourceStats();
		}
	}
	
	// 线程测试E
	public class ThreadE extends Thread{
		public void run() {
			createConnection();
			map.put("ThreadE Create a successful!", new Date());
			
			System.out.println("ThreadE Create a successful!");
			SqlServerPool.getInstance().printDataSourceStats();
		}
	}

	// 线程测试F
	public class ThreadF extends Thread{
		public void run() {
			createConnection();
			map.put("ThreadF Create a successful!", new Date());
			
			System.out.println("ThreadF Create a successful!");
			SqlServerPool.getInstance().printDataSourceStats();
		}
	}

	// 线程测试G
	public class ThreadG extends Thread{
		public void run() {
			createConnection();
			map.put("ThreadG Create a successful!", new Date());
			
			System.out.println("ThreadG Create a successful!");
			SqlServerPool.getInstance().printDataSourceStats();
		}
	}

	// 线程测试H
	public class ThreadH extends Thread{
		public void run() {
			createConnection();
			map.put("ThreadH Create a successful!", new Date());
			
			System.out.println("ThreadH Create a successful!");
			SqlServerPool.getInstance().printDataSourceStats();
		}
	}
	
	// 线程测试I
	public class ThreadI extends Thread{
		public void run() {
			createConnection();
			map.put("ThreadI Create a successful!", new Date());
			
			System.out.println("ThreadI Create a successful!");
			SqlServerPool.getInstance().printDataSourceStats();
		}
	}
}

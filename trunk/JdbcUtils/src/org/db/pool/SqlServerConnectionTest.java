package org.db.pool;

import java.sql.*;

public class SqlServerConnectionTest {

	// dataSource = (BasicDataSource)BasicDataSourceFactory.createDataSource(p);
	public static void main(String[] args) throws InterruptedException,
			SQLException {

		SqlServerConnectionTest test = new SqlServerConnectionTest();
		test.createConnection();

		SqlServerPool.getInstance().printDataSourceStats("主线程");

		test.setupThread();

		for (int i = 1; i < 20; i++) {
			Thread.sleep(10 * 1000);
			if (i == 1) {
				System.out.println("");
			}
			SqlServerPool.getInstance().printDataSourceStats(
					"maxWait=" + (i * 10));
		}

		System.out.println("colse.");
		SqlServerPool.getInstance().close();

	}

	private void setupThread() {
		new ThreadA().start();
		new ThreadB().start();
		new ThreadC().start();
		new ThreadD().start();
		new ThreadE().start();
		new ThreadF().start();
		new ThreadG().start();
		new ThreadH().start();
		new ThreadI().start();
		new ThreadJ().start();
		new ThreadK().start();
		new ThreadL().start();
		new ThreadM().start();
		new ThreadN().start();
	}

	public void createConnection() {
		Connection conn = null;
		try {
			conn = SqlServerPool.getInstance().getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			SqlServerPool.getInstance().freeConnection(conn);
		}
	}

	// //////////////////同时开启多个线程(A~H)，测试连接/////////////////////////////

	// 线程测试A
	class ThreadA extends Thread {
		public void run() {
			createConnection();
			SqlServerPool.getInstance().printDataSourceStats("ThreadA");
		}
	}

	// 线程测试B
	public class ThreadB extends Thread {
		public void run() {
			createConnection();
			SqlServerPool.getInstance().printDataSourceStats("ThreadB");
		}
	}

	// 线程测试C
	public class ThreadC extends Thread {
		public void run() {
			createConnection();
			SqlServerPool.getInstance().printDataSourceStats("ThreadC");
		}
	}

	// 线程测试D
	public class ThreadD extends Thread {
		public void run() {
			createConnection();
			SqlServerPool.getInstance().printDataSourceStats("ThreadD");
		}
	}

	// 线程测试E
	public class ThreadE extends Thread {
		public void run() {
			createConnection();
			SqlServerPool.getInstance().printDataSourceStats("ThreadE");
		}
	}

	// 线程测试F
	public class ThreadF extends Thread {
		public void run() {
			createConnection();
			SqlServerPool.getInstance().printDataSourceStats("ThreadF");
		}
	}

	// 线程测试G
	public class ThreadG extends Thread {
		public void run() {
			createConnection();
			SqlServerPool.getInstance().printDataSourceStats("ThreadG");
		}
	}

	// 线程测试H
	public class ThreadH extends Thread {
		public void run() {
			createConnection();
			SqlServerPool.getInstance().printDataSourceStats("ThreadH");
		}
	}

	// 线程测试I
	public class ThreadI extends Thread {
		public void run() {
			createConnection();
			SqlServerPool.getInstance().printDataSourceStats("ThreadI");
		}
	}
	
	// 线程测试J
	public class ThreadJ extends Thread {
		public void run() {
			createConnection();
			SqlServerPool.getInstance().printDataSourceStats("ThreadJ");
		}
	}
	
	// 线程测试K
	public class ThreadK extends Thread {
		public void run() {
			createConnection();
			SqlServerPool.getInstance().printDataSourceStats("ThreadK");
		}
	}
	
	// 线程测试L
	public class ThreadL extends Thread {
		public void run() {
			createConnection();
			SqlServerPool.getInstance().printDataSourceStats("ThreadL");
		}
	}
	
	// 线程测试M
	public class ThreadM extends Thread {
		public void run() {
			createConnection();
			SqlServerPool.getInstance().printDataSourceStats("ThreadM");
		}
	}
	
	// 线程测试N
	public class ThreadN extends Thread {
		public void run() {
			createConnection();
			SqlServerPool.getInstance().printDataSourceStats("ThreadN");
		}
	}
}

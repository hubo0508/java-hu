package examples.dhome.service.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.easysql.core.Entity;
import org.easysql.core.Page;
import org.easysql.core.SQLResult;
import org.easysql.handlers.EntityHandler;
import org.easysql.handlers.SQLHandler;

import examples.BaseTest;
import examples.dhome.domain.Device;
import examples.dhome.domain.User;
import examples.dhome.pool.DBPool;
import examples.dhome.pool.MySqlPool;
import examples.dhome.pool.OraclePool;

@SuppressWarnings("unused")
public class UserServiceTest extends BaseTest {

	public static void main(String[] args) {

		Connection con = pool.getConnection();

		User u = new User();
		u.setId(7);
		u.setUsername("hubo-te-----");
		u.setPassword("hubo-password");

		UserServiceTest test = new UserServiceTest();
		// test.save(u);
		// test.update(u);
		// test.queryDeviceListA();
		test.queryDeviceListB(con);

		// test.queryDeviceList(con);
	}

	@SuppressWarnings("unchecked")
	public void queryDeviceListB(Connection con) {

		try {

			SQLHandler sqlHandler = new SQLHandler(new EntityHandler(
					Device.class));
			String sql = sqlHandler.getSelectSQL("id>? ORDER BY id DESC");

			Object[] params = new Object[] { 0 };

			Page page = (Page) new QueryRunner(new Page(1, 2)).query(con, sql,
					new BeanListHandler(Device.class), params);

			List<Device> listdevice = (List<Device>) page.getResult();

			for (Device d : listdevice) {
				System.out.println(d.getId());
			}
			System.out.println("当前页：" + page.getThisPage());
			System.out.println("下一页：" + page.getPageNext());
			System.out.println("上一页：" + page.getPagePrev());
			System.out.println("尾  页：" + page.getPageLast());
			System.out.println("总页数：" + page.getTotalPage());
			System.out.println("总行数：" + page.getTotalCount());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.release();
		}
	}

	@SuppressWarnings("unchecked")
	public void queryDeviceListA(Connection con) {
		try {

			SQLHandler sqlHandler = new SQLHandler(
					new EntityHandler(User.class));
			String sql = sqlHandler.getSelectSQL();

			Page page = (Page) new QueryRunner(new Page(1, 2)).query(con, sql,
					new BeanListHandler(User.class));

			List<User> listuser = (List<User>) page.getResult();

			for (User user : listuser) {
				System.out.println(user.getId());
			}
			System.out.println("当前页：" + page.getThisPage());
			System.out.println("下一页：" + page.getPageNext());
			System.out.println("上一页：" + page.getPagePrev());
			System.out.println("共页：" + page.getTotalPage());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.release();
		}
	}

	public void queryDeviceList(Connection con) {
		try {

			SQLHandler sqlHandler = new SQLHandler(new EntityHandler(
					Device.class));
			String sql = sqlHandler.getSelectSQL();

			List<Device> listDevice = new QueryRunner().query(con, sql,
					new BeanListHandler<Device>(Device.class));

			for (Device o : listDevice) {
				System.out.println(o.getId());
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.release();
		}
	}

	public void update(User u, Connection con) {

		try {

			SQLHandler sqlHandler = new SQLHandler(
					new EntityHandler(User.class));
			String sql = sqlHandler.getUpdateSQL();
			Object[] params = sqlHandler.objectArray(u, sql);

			System.out.println(sql);

			int i = new QueryRunner().update(con, sql, params);

			System.out.println(i);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.release();
		}
	}

	@SuppressWarnings("unchecked")
	public void save(User u, Connection con) {

		DBPool pool = MySqlPool.getInstance();

		try {
			SQLHandler sqlHandler = new SQLHandler(
					new EntityHandler(User.class));
			String sql = sqlHandler.getInsertSQL();
			Object[] params = sqlHandler.objectArray(u);

			System.out.println(sql);

			int i = new QueryRunner().update(con, sql, params);

			System.out.println(i);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.release();
		}
	}

}

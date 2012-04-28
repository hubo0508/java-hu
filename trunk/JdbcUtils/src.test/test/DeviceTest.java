package test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.db.jdbcutils.JdbcUtils;

import pool.*;

public class DeviceTest {

	DBPool pool = MySqlPool.getInstance();
	// DBPool pool = OraclePool.getInstance();
	Connection con = pool.getConnection();

	public static void main(String[] args) {

		DeviceTest test = new DeviceTest();

		new DeviceTest().queryResultToArrayList();
		// new DeviceTest().queryResultToUnique();
		// new DeviceTest().queryResultToHashMap();
		// System.out.println(Map.class);

		// new DeviceTest().updateSQL();

		// test.updateObject();

		// test.insertObject();
	}

	public void insertObject() {

		NhwmConfigDevice d = new NhwmConfigDevice();
		// d.setId(new Integer(1));
		d.setDeviceCname("中文名++++++++++++++");
		d.setDeviceFactory("厂家");
		d.setDeviceIp("133.40.60.24");
		d.setDeviceType("g-dkb-type");
		d.setHasData(new Integer(0));
		d.setDeviceEname("ename");

		JdbcUtils db = new JdbcUtils(NhwmConfigDevice.class,
				JdbcUtils.SEGMENTATION);
		try {
			int rows = db.insert(con, d, JdbcUtils.MYSQL, JdbcUtils.MYSQL_SEQ);
			System.out.println(rows);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			pool.release();
		}
	}

	public void deleteObject() {

		Object[] params = new Object[] { new Integer(6000) };

		JdbcUtils db = new JdbcUtils(NhwmConfigDevice.class,
				JdbcUtils.SEGMENTATION);
		try {
			int rows = db.delete(con, "id>?", params);
			System.out.println(rows);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			pool.release();
		}
	}

	public void updateObject() {

		NhwmConfigDevice d = new NhwmConfigDevice();
		d.setId(new Integer(1));
		d.setDeviceCname("中文名");
		d.setDeviceFactory("厂家");
		d.setDeviceIp("133.40.60.24");
		d.setDeviceType("g-dkb-type");
		d.setHasData(new Integer(0));
		d.setDeviceEname("ename");

		JdbcUtils db = new JdbcUtils(NhwmConfigDevice.class,
				JdbcUtils.SEGMENTATION);
		try {
			int rows = db.update(con, d);
			System.out.println(rows);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			pool.release();
		}
	}

	public void updateSQL() {
		String sql = "UPDATE nhwm_config_device set DEVICE_ENAME = ?";

		Object[] params = new Object[] { new String("1111") };
		JdbcUtils db = new JdbcUtils(null);
		try {
			int rows = db.update(sql, con, params);

			System.out.println(rows);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			pool.release();
		}
	}

	public void queryResultToHashMap() {

		String sql = "SELECT ID,DEVICE_IP,DEVICE_ENAME,DEVICE_TYPE,DEVICE_FACTORY,HAS_DATA,DEVICE_TYPE,DEVICE_CNAME "
				+ "FROM nhwm_config_device where id = ?";

		Object[] params = new Object[] { new Integer(1) };

		Map map = null;
		try {
			JdbcUtils db = new JdbcUtils(NhwmConfigDevice.class,
					JdbcUtils.SEGMENTATION);
			// 方式1
			// map = db.queryResultToHashMap(sql, con, params});

			// 方式2
			map = db.queryResultToLinkedHashMap(sql, con, params);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			pool.release();
		}
		System.out.println(map);
	}

	public void queryResultToUnique() {

		String sql = "SELECT count(*) FROM nhwm_config_device where id > ?";

		Object[] params = new Object[] { new Integer(10) };

		try {
			// 方式1
			// DBUtil db = new DBUtil(Long.class);
			// Long totalCount = (Long) db.queryResultToUnique(sql, con,
			// params);
			// System.out.println(totalCount);

			// 方式2
			// sql = "SELECT
			// ID,DEVICE_IP,DEVICE_ENAME,DEVICE_TYPE,DEVICE_FACTORY,HAS_DATA,DEVICE_TYPE,DEVICE_CNAME
			// FROM nhwm_config_device where id = ?";
			// DBUtil db = new DBUtil(Device.class, DBUtil.SEGMENTATION);
			// Device device = (Device) db.queryResultToUnique(sql, con,
			// params);
			// System.out.println(device.getDeviceIp() + "|"
			// + device.getDeviceCname());

			// 方式3
			sql = "SELECT ID,DEVICE_IP,DEVICE_ENAME,DEVICE_TYPE,DEVICE_FACTORY,HAS_DATA,DEVICE_TYPE,DEVICE_CNAME FROM nhwm_config_device where id = ?";
			JdbcUtils db = new JdbcUtils(Map.class, JdbcUtils.SEGMENTATION);
			Map map = (HashMap) db.queryResultToUnique(sql, con, params);
			System.out.println(db.columnsToBean(NhwmConfigDevice.class, map));
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			pool.release();
		}
	}

	public void queryResultToArrayList() {

		String sql = "SELECT ID,DEVICE_IP,DEVICE_ENAME,DEVICE_TYPE,DEVICE_FACTORY,HAS_DATA,DEVICE_TYPE,DEVICE_CNAME FROM nhwm_config_device";

		try {

			// 方式1
			// DBUtil db = new DBUtil(Device.class, DBUtil.SEGMENTATION,
			// DBUtil.INVERSION_SQL);
			// List list = db.queryResultToArrayList(sql, con);
			//			
			// for (int i = 0; i < list.size(); i++) {
			// Device d = (Device) list.get(i);
			// System.out.println(d.getDeviceIp() + "|" + d.getDeviceCname());
			// }

			// 方式2
			JdbcUtils db = new JdbcUtils(Map.class, JdbcUtils.SEGMENTATION);
			List list = db.queryResultToArrayList(sql, con);

			for (int i = 0; i < list.size(); i++) {
				System.out.println(list.get(i));
			}

			System.out.println("+++++++++++++++++++++++++++++++++++++++");

			List afterConver = db.columnsToBean(NhwmConfigDevice.class, list);
			for (int i = 0; i < afterConver.size(); i++) {
				System.out.println(afterConver.get(i));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			pool.release();
		}

	}
}

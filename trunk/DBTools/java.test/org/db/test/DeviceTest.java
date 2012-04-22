package org.db.test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.db.domain.Device;
import org.db.pool.DBPool;
import org.db.pool.MySqlPool;
import org.db.pool.OraclePool;
import org.dbtools.DBTools;
import org.dbtools.run.DBUtil;


public class DeviceTest {

	DBPool pool = MySqlPool.getInstance();
	// DBPool pool = OraclePool.getInstance();
	Connection con = pool.getConnection();

	public static void main(String[] args) {
		// new DeviceTest().queryResultToArrayList();
		new DeviceTest().queryResultToUnique();
		// new DeviceTest().queryResultToHashMap();
		// System.out.println(Map.class);
	}

	public void queryResultToHashMap() {

		String sql = "SELECT ID,DEVICE_IP,DEVICE_ENAME,DEVICE_TYPE,DEVICE_FACTORY,HAS_DATA,DEVICE_TYPE,DEVICE_CNAME "
				+ "FROM nhwm_config_device where id = ?";

		Object[] params = new Object[] { new Integer(1) };

		Map map = null;
		try {
			DBUtil db = new DBUtil(Device.class, DBTools.SEGMENTATION);
			// ��ʽ1
			// map = db.queryResultToHashMap(sql, con, params});

			// ��ʽ2
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
			// ��ʽ1
			// DBUtil db = new DBUtil(Long.class);
			// Long totalCount = (Long) db.queryResultToUnique(sql, con,
			// params);
			// System.out.println(totalCount);

			// ��ʽ2
			// sql = "SELECT
			// ID,DEVICE_IP,DEVICE_ENAME,DEVICE_TYPE,DEVICE_FACTORY,HAS_DATA,DEVICE_TYPE,DEVICE_CNAME
			// FROM nhwm_config_device where id = ?";
			// DBUtil db = new DBUtil(Device.class, DBUtil.SEGMENTATION);
			// Device device = (Device) db.queryResultToUnique(sql, con,
			// params);
			// System.out.println(device.getDeviceIp() + "|"
			// + device.getDeviceCname());

			// ��ʽ3
			sql = "SELECT ID,DEVICE_IP,DEVICE_ENAME,DEVICE_TYPE,DEVICE_FACTORY,HAS_DATA,DEVICE_TYPE,DEVICE_CNAME FROM nhwm_config_device where id = ?";
			DBUtil db = new DBUtil(Map.class, DBTools.SEGMENTATION);
			Map map = (HashMap) db.queryResultToUnique(sql, con, params);
			System.out.println(db.convertedMapFieldToBean(Device.class, map));
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			pool.release();
		}
	}

	public void queryResultToArrayList() {

		String sql = "SELECT ID,DEVICE_IP,DEVICE_ENAME,DEVICE_TYPE,DEVICE_FACTORY,HAS_DATA,DEVICE_TYPE,DEVICE_CNAME FROM nhwm_config_device";

		try {

			// ��ʽ1
			// DBUtil db = new DBUtil(Device.class, DBUtil.SEGMENTATION,
			// DBUtil.INVERSION_SQL);
			// List list = db.queryResultToArrayList(sql, con);
			//			
			// for (int i = 0; i < list.size(); i++) {
			// Device d = (Device) list.get(i);
			// System.out.println(d.getDeviceIp() + "|" + d.getDeviceCname());
			// }

			// ��ʽ2
			DBUtil db = new DBUtil(Map.class, DBTools.SEGMENTATION);
			List list = db.queryResultToArrayList(sql, con);

			for (int i = 0; i < list.size(); i++) {
				System.out.println(list.get(i));
			}

			System.out.println("+++++++++++++++++++++++++++++++++++++++");

			List afterConver = db.convertedMapFieldToBean(Device.class, list);
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

package test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
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

		// test.queryResultToUniqueA();
		// test.queryResultToUniqueB();
		// test.queryResultToUniqueC();

		// test.queryResultToArrayListA();
		// test.queryResultToArrayListB();
		// test.queryResultToArrayListC();

		// test.queryResultToHashMapA();
		// test.queryResultToHashMapB();

		// test.queryResultToLinkedHashMapA();
		// test.queryResultToLinkedHashMapB();

		// test.insertObjectToMySqlA();
		// test.insertObjectToMySqlB();
		// test.insertObjectToMySqlC();

		// test.insertObjectToOracleA();
		// test.insertObjectToOracleB();

		// test.updateObjectA();
		// test.updateObjectB();
		// test.updateObjectC();
	}

	public void updateObjectC() {

		NhwmConfigDevice d = new NhwmConfigDevice();
		d.setId(new Integer(5886));
		d.setDeviceCname("中文名++++++++++++++++++++++++");
		d.setDeviceFactory("厂家");
		d.setDeviceIp("133.40.60.24");
		d.setDeviceType("g-dkb-type");
		d.setHasData(new Integer(11111111));
		d.setDeviceEname("ename");

		String sql = "update Nhwm_Config_Device set has_data=? where id=?";

		JdbcUtils db = new JdbcUtils(NhwmConfigDevice.class,
				JdbcUtils.SEGMENTATION);
		try {
			int rows = db.update(con, sql, d);
			System.out.println(rows);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBPool.close(con);
		}
	}

	public void updateObjectB() {

		Object[] params = new Object[] { new Integer(1), new Integer(5886) };
		String sql = "update Nhwm_Config_Device set has_data=? where id=?";

		JdbcUtils db = new JdbcUtils(NhwmConfigDevice.class,
				JdbcUtils.SEGMENTATION);
		try {
			int rows = db.update(con, sql, params);
			System.out.println(rows);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBPool.close(con);
		}
	}

	public void updateObjectA() {

		NhwmConfigDevice d = new NhwmConfigDevice();
		d.setId(new Integer(5885));
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
			DBPool.close(con);
		}
	}

	// 主键手动维护
	public void insertObjectToOracleB() {

		NhwmConfigDevice d = new NhwmConfigDevice();
		d.setId(new Integer(24));
		d.setDeviceCname("D-NAME");
		d.setDeviceFactory("D-FACTORY");
		d.setDeviceIp("D-IP");
		d.setDeviceType("D-TYPE");
		d.setHasData(new Integer(0));
		d.setDeviceEname("D-ENAME");

		JdbcUtils db = new JdbcUtils(NhwmConfigDevice.class,
				JdbcUtils.SEGMENTATION);

		try {
			int rows = db.insert(con, d, JdbcUtils.ORACLE, null);
			System.out.println(rows);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBPool.close(con);
		}
	}

	// 主键自动递增
	public void insertObjectToOracleA() {

		NhwmConfigDevice d = new NhwmConfigDevice();
		d.setDeviceCname("主键自动递增insertObjectToMySql");
		d.setDeviceFactory("厂家");
		d.setDeviceIp("133.40.60.24");
		d.setDeviceType("g-dkb-type");
		d.setHasData(new Integer(0));
		d.setDeviceEname("ename");

		JdbcUtils db = new JdbcUtils(NhwmConfigDevice.class,
				JdbcUtils.SEGMENTATION);

		try {
			int rows = db.insert(con, d, JdbcUtils.ORACLE, "seq");
			System.out.println(rows);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBPool.close(con);
		}
	}

	// 主键手动维护
	public void insertObjectToMySqlB() {

		NhwmConfigDevice d = new NhwmConfigDevice();
		d.setId(new Integer(24));
		d.setDeviceCname("D-NAME");
		d.setDeviceFactory("D-FACTORY");
		d.setDeviceIp("D-IP");
		d.setDeviceType("D-TYPE");
		d.setHasData(new Integer(0));
		d.setDeviceEname("D-ENAME");

		JdbcUtils db = new JdbcUtils(NhwmConfigDevice.class,
				JdbcUtils.SEGMENTATION);

		try {
			int rows = db.insert(con, d, JdbcUtils.MYSQL, null);
			System.out.println(rows);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBPool.close(con);
		}
	}

	// 主键自动维护,SQL手动维护
	public void insertObjectToMySqlC() {

		NhwmConfigDevice d = new NhwmConfigDevice();
		d.setDeviceCname("D-NAME");
		d.setDeviceFactory("D-FACTORY");
		d.setDeviceIp("D-IP");
		d.setDeviceType("D-TYPE");
		d.setHasData(new Integer(0));
		d.setDeviceEname("D-ENAME");

		String sql = "INSERT INTO nhwm_config_device (device_cname, device_ename, device_factory, device_ip, device_type, has_data ) "
				+ "VALUES (?, ?, ?, ?, ?, ? )";

		JdbcUtils db = new JdbcUtils(null, JdbcUtils.SEGMENTATION);

		try {
			int rows = db.insert(con, sql, d, JdbcUtils.MYSQL,
					JdbcUtils.MYSQL_SEQ);
			System.out.println(rows);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBPool.close(con);
		}
	}

	// 主键自动递增
	public void insertObjectToMySqlA() {

		NhwmConfigDevice d = new NhwmConfigDevice();
		d.setDeviceCname("主键自动递增insertObjectToMySql");
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
			DBPool.close(con);
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
			DBPool.close(con);
		}
	}

	public void queryResultToLinkedHashMapA() {

		String sql = "SELECT ID,DEVICE_IP,DEVICE_ENAME,DEVICE_TYPE,DEVICE_FACTORY,HAS_DATA,DEVICE_TYPE,DEVICE_CNAME "
				+ "FROM nhwm_config_device where id = ?";

		Object[] params = new Object[] { new Integer(1) };
		Map map = null;
		try {
			JdbcUtils db = new JdbcUtils(NhwmConfigDevice.class,
					JdbcUtils.SEGMENTATION);
			map = db.queryResultToLinkedHashMap(con, sql, params);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBPool.close(con);
		}
		System.out.println(map);
	}

	public void queryResultToLinkedHashMapB() {

		String sql = "where id = ?";
		Object[] params = new Object[] { new Integer(1) };

		Map map = null;
		try {
			JdbcUtils db = new JdbcUtils(NhwmConfigDevice.class,
					JdbcUtils.SEGMENTATION);
			map = db.queryResultToLinkedHashMap(con, sql, params);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBPool.close(con);
		}
		System.out.println(map);
	}

	public void queryResultToHashMapA() {

		String sql = "SELECT ID,DEVICE_IP,DEVICE_ENAME,DEVICE_TYPE,DEVICE_FACTORY,HAS_DATA,DEVICE_TYPE,DEVICE_CNAME "
				+ "FROM nhwm_config_device where id = ?";

		Object[] params = new Object[] { new Integer(1) };
		Map map = null;
		try {
			JdbcUtils db = new JdbcUtils(NhwmConfigDevice.class,
					JdbcUtils.SEGMENTATION);
			map = db.queryResultToHashMap(con, sql, params);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBPool.close(con);
		}
		System.out.println(map);
	}

	public void queryResultToHashMapB() {

		String sql = "where id = ?";
		Object[] params = new Object[] { new Integer(1) };

		Map map = null;
		try {
			JdbcUtils db = new JdbcUtils(NhwmConfigDevice.class,
					JdbcUtils.SEGMENTATION);
			map = db.queryResultToHashMap(con, sql, params);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBPool.close(con);
		}
		System.out.println(map);
	}

	public void queryResultToArrayListC() {

		try {

			JdbcUtils db = new JdbcUtils(LinkedHashMap.class,
					JdbcUtils.SEGMENTATION);
			db.setSqlMappingClass(NhwmConfigDevice.class);
			List list = db.queryResultToArrayList(con);

			for (int i = 0; i < list.size(); i++) {
				System.out.println(list.get(i));
			}

			// System.out.println("+++++++++++++++++++++++++++++++++++++++");
			//
			// List afterConver = db.columnsToBean(NhwmConfigDevice.class,
			// list);
			// for (int i = 0; i < afterConver.size(); i++) {
			// System.out.println(afterConver.get(i));
			// }

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBPool.close(con);
		}

	}

	public void queryResultToArrayListB() {

		String sql = "SELECT ID,DEVICE_IP,DEVICE_ENAME,DEVICE_TYPE,DEVICE_FACTORY,HAS_DATA,DEVICE_TYPE,DEVICE_CNAME "
				+ " FROM nhwm_config_device where id > ?";
		Object[] params = new Object[] { new Integer(15) };
		try {

			JdbcUtils db = new JdbcUtils(LinkedHashMap.class,
					JdbcUtils.SEGMENTATION);
			List list = db.queryResultToArrayList(con, sql, params);

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
			DBPool.close(con);
		}

	}

	public void queryResultToArrayListA() {

		try {
			JdbcUtils db = new JdbcUtils(NhwmConfigDevice.class,
					JdbcUtils.SEGMENTATION);
			List list = db.queryResultToArrayList(con);

			for (int i = 0; i < list.size(); i++) {
				NhwmConfigDevice d = (NhwmConfigDevice) list.get(i);
				System.out.println(d.getDeviceIp() + "|" + d.getDeviceCname());
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBPool.close(con);
		}

	}

	public void queryResultToUniqueB() {

		String sql = "SELECT count(*) FROM nhwm_config_device where id > ?";
		Object[] params = new Object[] { new Integer(10) };
		try {
			sql = "SELECT ID,DEVICE_IP,DEVICE_ENAME,DEVICE_TYPE,DEVICE_FACTORY,HAS_DATA,DEVICE_TYPE,DEVICE_CNAME "
					+ " FROM nhwm_config_device where id = ?";
			sql = "where id=?";

			JdbcUtils db = new JdbcUtils(NhwmConfigDevice.class,
					JdbcUtils.SEGMENTATION);
			NhwmConfigDevice device = (NhwmConfigDevice) db
					.queryResultToUnique(con, sql, params);

			System.out.println(device.getDeviceIp() + "|"
					+ device.getDeviceCname());
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(con);
		}
	}

	public void queryResultToUniqueA() {

		String sql = "SELECT count(*) FROM nhwm_config_device where id > ?";

		Object[] params = new Object[] { new Integer(10) };
		try {
			JdbcUtils db = new JdbcUtils(Long.class);
			Long totalCount = (Long) db.queryResultToUnique(con, sql, params);
			System.out.println(totalCount);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(con);
		}
	}

	public void queryResultToUniqueC() {

		String sql = "SELECT ID,DEVICE_IP,DEVICE_ENAME,DEVICE_TYPE,DEVICE_FACTORY,HAS_DATA,DEVICE_TYPE,DEVICE_CNAME"
				+ " FROM nhwm_config_device where id = ?";
		Object[] params = new Object[] { new Integer(10) };
		try {
			JdbcUtils db = new JdbcUtils(HashMap.class, JdbcUtils.SEGMENTATION);
			Map map = (HashMap) db.queryResultToUnique(con, sql, params);
			System.out.println(db.columnsToBean(NhwmConfigDevice.class, map));
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(con);
		}
	}
}

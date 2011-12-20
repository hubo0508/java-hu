package com.poi.excel;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.poi.excel.vo.Device;
import com.util.Util;

public class ExcleRenderTest {

	protected Log logger = LogFactory.getLog(ExcleRenderTest.class);

	public static void main(String[] args) throws Exception {
		InputStream in = null;
		try {
			in = readFileByStream(Util.getRealURL() + "device.xls");
			List<Device> listDevice = getDeviceInfo(in);
			for (Device device : listDevice) {
				System.err.println(device.getDeviceCode());
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		} finally {
			in.close();
		}
	}

	/**
	 * @date Sep 23, 2011 10:39:14 AM
	 * @author HUBO
	 * @Description: 取得设备信息
	 * @return List<Device>
	 */
	public static List<Device> getDeviceInfo(InputStream in) throws Exception {

		ExcelParser2003 excel = new ExcelParser2003();
		Map<Integer, String[]> content = excel.renderExcel(in);

		List<Device> listDevice = new ArrayList<Device>();
		Date storageDate = new Date();

		Set<Integer> keys = content.keySet();
		Iterator<Integer> it = keys.iterator();
		while (it.hasNext()) {
			String[] rowValue = content.get(it.next());
			// 丆家編號[0]、設備編號[1]、信息類型ID[2]、信息內容[3]、入庫人[4]、入庫時間[5]、活動狀态[6]
			String factoryCode = rowValue[0];
			if (StringUtils.isNotEmpty(factoryCode)) {
				listDevice.add(new Device(factoryCode, rowValue[1],
						rowValue[2], rowValue[3], rowValue[4], storageDate, 1));
			}
		}

		return listDevice;
	}

	/**
	 * 以字节为单位读取文件，常用于读二进制文件，如图片、声音、影像等文件。
	 */
	public static InputStream readFileByStream(String fileName) {
		InputStream in = null;
		try {
			in = new FileInputStream(fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return in;
	}
}

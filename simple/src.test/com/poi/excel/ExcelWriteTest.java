package com.poi.excel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.poi.excel.vo.StatisticsVo;

public class ExcelWriteTest {
	public static void main(String[] args) {
		List<StatisticsVo> listvo = new ArrayList<StatisticsVo>();

		StatisticsVo v1 = new StatisticsVo();
		v1.setRankings(1);
		v1.setProvince("四川");
		v1.setHall("华北厅");
		v1.setCategory("精品游戏");
		v1.setApplicationName("软件A");
		v1.setDownloadsNumbers(1);

		StatisticsVo v2 = new StatisticsVo();
		v2.setRankings(2);
		v2.setProvince("四川");
		v2.setHall("华北厅");
		v2.setCategory("精品游戏");
		v2.setApplicationName("软件B");
		v2.setDownloadsNumbers(2);

		listvo.add(v1);
		listvo.add(v2);

		ExcelWrite2003 excel = new ExcelWrite2003();

		Map<String, Object> map = new HashMap<String, Object>();
		map.put(ExcelWrite2003.CATEGORY, 11);
		map.put(ExcelWrite2003.APP_NAME, 11);
		map.put(ExcelWrite2003.DOW_NUM, 11);

		excel.highestLevelAppExcel(listvo, map, "集团应用统计", "集团应用统计");
	}
}

package com.poi;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.util.Region;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelUtil {

	private static final String CATEGORY = "category";
	private static final String APP_NAME = "applicationName";
	private static final String DOW_NUM = "downloadsNumbers";

	/** Excel 文件要存放的位置，假定在D盘JTest目录下 */

	public static String outputFile = "C:/Users/HU/Desktop/gongye.xls";

	public static void main(String argv[]) {

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

		ExcelUtil excel = new ExcelUtil();

		Map<String, Object> map = new HashMap<String, Object>();
		map.put(CATEGORY, 11);
		map.put(APP_NAME, 11);
		map.put(DOW_NUM, 11);

		excel.highestLevelAppExcel(listvo, map, "集团应用统计", "集团应用统计");
	}

	/**
	 * 
	 * @author HUBO hubo.0508@gmail.com
	 * @Description: 集團、基地統計--應用統計
	 * @param listvo
	 *            统计数据行
	 * @param sheetname
	 *            sheet名称
	 * @param summary
	 *            汇总数据
	 * @param title
	 *            標題
	 */
	private void highestLevelAppExcel(List<StatisticsVo> listvo,
			Map<String, Object> summary, String sheetname, String title) {

		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet(sheetname);

		// 起始行
		int rowIndex = 0;

		// 设置列宽
		sheet.setColumnWidth((short) 0, (short) 4000);
		sheet.setColumnWidth((short) 1, (short) 5000);
		sheet.setColumnWidth((short) 2, (short) 5000);
		sheet.setColumnWidth((short) 3, (short) 8000);
		sheet.setColumnWidth((short) 4, (short) 8000);
		sheet.setColumnWidth((short) 5, (short) 4000);

		// 文字加粗，10号字，样式
		HSSFCellStyle style = getShareStyle(workbook, 10);

		try {

			// 合并单远格
			sheet.addMergedRegion(new Region(rowIndex, (short) 0, rowIndex,
					(short) 5));

			// 标题行 --- 样式
			HSSFFont titleFont = createFont(workbook, 16);
			HSSFCellStyle titleStyle = createStyle(workbook);
			titleStyle.setFont(titleFont);

			// 标题行
			HSSFRow rowTitle = sheet.createRow((short) rowIndex);
			createColumn(rowTitle, 0, title, titleStyle);

			rowIndex++;

			// 内容标题行
			HSSFRow rowContentTitle = sheet.createRow((short) rowIndex);
			createColumn(rowContentTitle, 0, "排名", style);
			createColumn(rowContentTitle, 1, "省", style);
			createColumn(rowContentTitle, 2, "厅", style);
			createColumn(rowContentTitle, 3, "类别", style);
			createColumn(rowContentTitle, 4, "应用名称", style);
			createColumn(rowContentTitle, 5, "下载量", style);

			rowIndex++;

			// 内容行
			for (int i = 0; i < listvo.size(); i++) {

				StatisticsVo vo = listvo.get(i);

				HSSFRow row = sheet.createRow((short) rowIndex);
				createColumn(row, 0, vo.getRankings());// 排名
				createColumn(row, 1, vo.getProvince());// 省
				createColumn(row, 2, vo.getHall());// 厅
				createColumn(row, 3, vo.getCategory());// 类别
				createColumn(row, 4, vo.getApplicationName());// 就用名称
				createColumn(row, 5, vo.getDownloadsNumbers());// 下载量

				rowIndex++;
			}

			rowIndex++;

			// 汇总标题
			HSSFRow rowSummaryTitle = sheet.createRow((short) rowIndex);
			createColumn(rowSummaryTitle, 0, "汇总", style);// 排名

			// 合并单元格
			sheet.addMergedRegion(new Region(rowIndex, (short) 0, rowIndex,
					(short) 5));

			rowIndex++;

			// 汇总行
			HSSFRow rowSummary = sheet.createRow((short) rowIndex);
			createColumn(rowSummary, 0, "---", style);// 排名
			createColumn(rowSummary, 1, "---", style);// 省
			createColumn(rowSummary, 2, "---", style);// 厅
			createColumn(rowSummary, 3, summary.get(CATEGORY), style);// 类别
			createColumn(rowSummary, 4, summary.get(APP_NAME), style);// 就用名称
			createColumn(rowSummary, 5, summary.get(DOW_NUM), style);// 下载量

			rowIndex++;

			// 新建一输出文件流
			FileOutputStream fOut = new FileOutputStream(outputFile);
			// 把相应的Excel 工作簿存盘
			workbook.write(fOut);
			fOut.flush();
			// 操作结束，关闭文件
			fOut.close();
			System.out.println("文件生成...");

		} catch (Exception e) {
			System.out.println("已运行 xlCreate() : " + e);
		}
	}

	private HSSFCellStyle getShareStyle(HSSFWorkbook workbook, int fontSize) {
		HSSFFont font = createFont(workbook, fontSize);
		HSSFCellStyle style = workbook.createCellStyle();
		style.setFont(font);

		return style;
	}

	@SuppressWarnings("deprecation")
	private HSSFCell createColumn(HSSFRow row, int column, Object value) {

		HSSFCell cell = row.createCell((short) column);
		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue(value.toString());

		return cell;
	}

	@SuppressWarnings("deprecation")
	private HSSFCell createColumn(HSSFRow row, int column, Object value,
			HSSFCellStyle style) {

		HSSFCell cell = row.createCell((short) column);
		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue(value.toString());
		cell.setCellStyle(style);

		return cell;
	}

	private HSSFCellStyle createStyle(HSSFWorkbook workbook) {
		HSSFCellStyle style = workbook.createCellStyle();
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		style.setBorderRight(HSSFCellStyle.BORDER_THIN);
		style.setBorderTop(HSSFCellStyle.BORDER_THIN);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		return style;
	}

	private HSSFFont createFont(HSSFWorkbook workbook, int fontSize) {
		HSSFFont font = workbook.createFont();
		font.setFontHeightInPoints((short) fontSize);
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		return font;
	}
}
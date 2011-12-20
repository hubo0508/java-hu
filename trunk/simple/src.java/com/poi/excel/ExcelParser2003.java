package com.poi.excel;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * @Description: 解析Excel
 * @author HUBO
 */
public class ExcelParser2003 {

	protected Log logger = LogFactory.getLog(ExcelParser2003.class);

	// private static String[] zero = new String[] { ".0", ".00", ".000",
	// ".0000",
	// ".00000", ".000000", ".0000000", ".00000000", ".000000000" };

	private static DecimalFormat df = new DecimalFormat("#");

	public ExcelParser2003() {

	}

	/**
	 * @date Sep 23, 2011 10:29:33 AM
	 * @author HUBO
	 * @Description: 读取Excel
	 * @return Map<Integer,String>
	 */
	@SuppressWarnings("unused")
	public Map<Integer, String[]> renderExcel(InputStream in) throws Exception {
		HSSFWorkbook wb = new HSSFWorkbook(in);
		HSSFSheet sheet = wb.getSheetAt(0);
		return getContent(sheet, sheet.getLastRowNum());
	}

	/**
	 * @date Sep 23, 2011 10:24:29 AM
	 * @author HUBO
	 * @Description: 取得所有行、单元格内容，以key-value存储
	 * @return Map<Integer,String>
	 */
	private Map<Integer, String[]> getContent(HSSFSheet sheet, int rowEnd) {
		Map<Integer, String[]> content = new HashMap<Integer, String[]>();
		int colNum = sheet.getRow(0).getPhysicalNumberOfCells();
		for (int j = 1; j <= rowEnd; j++) {
			HSSFRow rowC = sheet.getRow(j);
			String[] rowValue = new String[colNum];
			for (int i = 0; i < colNum; i++) {
				try {
					rowValue[i] = getCellValue(rowC.getCell(i));
				} catch (RuntimeException e) {
					rowValue[i] = null;
				}
			}
			content.put(j, rowValue);
		}
		return content;
	}

	/**
	 * @date Sep 23, 2011 10:04:31 AM
	 * @author HUBO
	 * @Description: 取得单元格值
	 * @return String
	 */
	@SuppressWarnings("unused")
	private String getCellValue(HSSFCell cell) {
		String value = null;
		switch (cell.getCellType()) {
		case HSSFCell.CELL_TYPE_STRING:
			value = cell.getRichStringCellValue().getString();
			break;
		case HSSFCell.CELL_TYPE_NUMERIC:
			value = df.format(cell.getNumericCellValue());
			break;
		case HSSFCell.CELL_TYPE_BLANK:
			value = "";
			break;
		case HSSFCell.CELL_TYPE_FORMULA:
			value = String.valueOf(cell.getCellFormula());
			break;
		case HSSFCell.CELL_TYPE_BOOLEAN:
			value = String.valueOf(cell.getBooleanCellValue());
			break;
		case HSSFCell.CELL_TYPE_ERROR:
			value = String.valueOf(cell.getErrorCellValue());
			break;
		default:
			break;
		}
		return value;
	}
}

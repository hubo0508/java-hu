package com.poi.excel;
//import org.apache.poi.hssf.usermodel.*;
//import java.io.FileOutputStream;
//import java.io.IOException;
//
//public class CreateCells {
//	public static void main(String[] args) {
//		HSSFWorkbook wb = new HSSFWorkbook(); // 建立新HSSFWorkbook对象
//		HSSFSheet sheet = wb.createSheet("new sheet"); // 建立新的sheet对象
//		HSSFRow row = sheet.createRow((short) 0);
//		// 在sheet里创建一行，参数为行号（第一行，此处可想象成数组）
//		HSSFCell cell = row.createCell((short) 0);
//		// 在row里建立新cell（单元格），参数为列号（第一列）
//		//cell.setCellvalue(1); // 设置cell的整数类型的值
//		row.createCell((short) 1).setCellvalue(1.2); // 设置cell浮点类型的值
//		row.createCell((short) 2).setCellvalue("test"); // 设置cell字符类型的值
//		row.createCell((short) 3).setCellvalue(true); // 设置cell布尔类型的值
//		HSSFCellStyle cellStyle = wb.createCellStyle(); // 建立新的cell样式
//		cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy h:mm"));
//		// 设置cell样式为定制的日期格式
//		HSSFCell dCell = row.createCell((short) 4);
//		dCell.setCellvalue(new Date()); // 设置cell为日期类型的值
//		dCell.setCellStyle(cellStyle); // 设置该cell日期的显示格式
//		HSSFCell csCell = row.createCell((short) 5);
//		csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
//		// 设置cell编码解决中文高位字节截断
//		csCell.setCellvalue("中文测试_Chinese Words Test"); // 设置中西文结合字符串
//		row.createCell((short) 6).setCellType(HSSFCell.CELL_TYPE_ERROR);
//		// 建立错误cell
//		FileOutputStream fileOut = new FileOutputStream("workbook.xls");
//		wb.write(fileOut);
//		fileOut.close();
//	}
//}

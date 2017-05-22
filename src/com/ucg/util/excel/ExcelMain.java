package com.ucg.util.excel;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;

public class ExcelMain {
	public static void main(String[] args) throws IOException {
	  InputStream is = ExcelMain.class.getClassLoader().getResourceAsStream("com/ucg/util/excel/model/model.xls");
	  HSSFWorkbook workbook;
		try {
			workbook = new HSSFWorkbook(is);
			ExportExcel.init(workbook);//初始化
		    HSSFSheet sheet = workbook.getSheetAt(0);
		    HSSFRow row = sheet.createRow(1); //从第1行开始
		    
		    Cell one_cell = row.createCell(0);//   第一列
		    one_cell.setCellValue("xxxx");
		    
		    Cell two_cell = row.createCell(1);//   第二列
		    two_cell.setCellValue("xxxx");
		    ExportExcel.formatMoneyNumber(two_cell, new BigDecimal(123.55555555));
		    
		    Cell three_cell = row.createCell(2);//   第三列
		    ExportExcel.formatBuiltin(three_cell, new BigDecimal(0.15));
		    
		    Cell four_cell = row.createCell(3);//   第四列
		    ExportExcel.formatDate(four_cell,new Date());
		    
		    String path="D://text.xls";
		    OutputStream outp=new FileOutputStream(path);
			workbook.write(outp);
			Runtime.getRuntime().exec("cmd /c start "+path);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			is.close();
		}
	     
		
	}
}

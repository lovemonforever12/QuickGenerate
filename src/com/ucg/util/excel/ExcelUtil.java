package com.ucg.util.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;

import com.ucg.base.framework.core.util.StringUtil;

public class ExcelUtil {

	public static void setCellStyle(HSSFCellStyle style,short color, short border,boolean wrap,short align,short valign){
		style.setFillForegroundColor(color);//格子背景色
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		style.setBorderBottom(border);
		style.setBorderLeft(border);
		style.setBorderRight(border);
		style.setBorderTop(border);
		style.setAlignment(align);
		style.setVerticalAlignment(valign);
		style.setWrapText(wrap); 
	}
	
	public static void setFont(HSSFFont font,short color,short size,String name,short bold){
		font.setColor(color);//字体颜色
		font.setFontHeightInPoints(size);
		font.setFontName(name);
		font.setBoldweight(bold);
	}  
	

	public static String[] bulidExcelTitle(String templatePath,HSSFWorkbook workbook) throws IOException{
		File file =new File(templatePath);
		FileInputStream fis =new FileInputStream(file);
		HSSFWorkbook book =new HSSFWorkbook(fis);
		copySheet(book, workbook, 0, 0);
		HSSFSheet sheet= book.getSheetAt(0);
		int lastRowNumber=sheet.getLastRowNum();
		HSSFRow row=sheet.getRow(lastRowNumber);
		int countCell=row.getPhysicalNumberOfCells();
		String fields[] = new String[countCell];
		if(row!=null){
			for(int i=0;i<countCell;i++){
				HSSFCell cell=row.getCell(i);
				fields[i]= cell.getStringCellValue();
			}
		}
		return fields;
	}
	
	public static String[] bulidExcelTitle(String templatePath,HSSFWorkbook workbook,Integer fromIndex,Integer toIndex) throws IOException{
		File file =new File(templatePath);
		FileInputStream fis =new FileInputStream(file);
		HSSFWorkbook book =new HSSFWorkbook(fis);
		copySheet(book, workbook, fromIndex, toIndex);
		HSSFSheet sheet= book.getSheetAt(fromIndex);
		int lastRowNumber=sheet.getLastRowNum();
		HSSFRow row=sheet.getRow(lastRowNumber);
		int countCell=row.getPhysicalNumberOfCells();
		String fields[] = new String[countCell];
		if(row!=null){
			for(int i=0;i<countCell;i++){
				HSSFCell cell=row.getCell(i);
				fields[i]= cell.getStringCellValue();
			}
		}
		return fields;
	}
	
	public static void copySheet(HSSFWorkbook fromWorkbook, HSSFWorkbook toWorkbook, int fromSheetIndex, int toSheetIndex) {
	    HSSFSheet fromSheet = fromWorkbook.getSheetAt(fromSheetIndex);
	    for (int i = fromSheet.getFirstRowNum(); i <= fromSheet.getLastRowNum(); i++) {
	      copyRows(fromWorkbook, toWorkbook, fromSheetIndex, toSheetIndex, i, i, i);
	    }
	  }

	  /**
	   * 复制行
	   * 此方法主要用于复制2个不同HSSFWorkbook间的行
	   */
	  public static void copyRows(HSSFWorkbook fromWorkbook, HSSFWorkbook toWorkbook, int fromSheetIndex, int toSheetIndex, int startRow, int endRow, int position) {
	    HSSFSheet fromSheet = fromWorkbook.getSheetAt(fromSheetIndex);
	    HSSFSheet toSheet = toWorkbook.getSheetAt(toSheetIndex);
	    int i;
	    int j;
	    if ((startRow == -1) || (endRow == -1)) {
	      return;
	    }

	    List<CellRangeAddress> oldRanges = new ArrayList<CellRangeAddress>();
	    for (i = 0; i < fromSheet.getNumMergedRegions(); i++) {
	      oldRanges.add(fromSheet.getMergedRegion(i));
	    }
	    // 拷贝合并的单元格。原理：复制当前合并单元格后，原位置的格式会移动到新位置，需在原位置生成旧格式
	    for (CellRangeAddress oldRange : oldRanges) {
	      CellRangeAddress newRange = new CellRangeAddress(oldRange.getFirstRow(), oldRange.getLastRow(),
	          oldRange.getFirstColumn(), oldRange.getLastColumn());

	      if (oldRange.getFirstRow() >= startRow && oldRange.getLastRow() <= endRow) {
	        int targetRowFrom = oldRange.getFirstRow() - startRow + position;
	        int targetRowTo = oldRange.getLastRow() - startRow + position;
	        oldRange.setFirstRow(targetRowFrom);
	        oldRange.setLastRow(targetRowTo);
	        toSheet.addMergedRegion(oldRange);
	        fromSheet.addMergedRegion(newRange);
	      }
	    }

	    // 设置列宽
	    for (i = startRow; i <= endRow; i++) {
	      HSSFRow fromRow = fromSheet.getRow(i);
	      if (fromRow != null) {
	        for (j = fromRow.getLastCellNum(); j >= fromRow.getFirstCellNum(); j--) {
	          toSheet.setColumnWidth(j, fromSheet.getColumnWidth(j));
	          toSheet.setColumnHidden(j, false);
	        }
	        break;
	      }
	    }

	    // 拷贝行并填充数据
	    for (; i <= endRow; i++) {
	      HSSFRow fromRow = fromSheet.getRow(i);
	      if (fromRow == null) {
	        continue;
	      }
	      HSSFRow toRow = toSheet.createRow(i - startRow + position);
	      toRow.setHeight(fromRow.getHeight());
	      for (j = fromRow.getFirstCellNum(); j <= fromRow.getPhysicalNumberOfCells(); j++) {
	        HSSFCell fromCell = fromRow.getCell(j);
	        if (fromCell == null) {
	          continue;
	        }
	        HSSFCell toCell = toRow.createCell(j);
	        HSSFCellStyle toStyle = toWorkbook.createCellStyle();
	        copyCellStyle(fromWorkbook, toWorkbook, fromCell.getCellStyle(), toStyle);
	        toCell.setCellStyle(toStyle);
	        int cType = fromCell.getCellType();
	        toCell.setCellType(cType);
	        switch (cType) {
	          case HSSFCell.CELL_TYPE_BOOLEAN:
	            toCell.setCellValue(fromCell.getBooleanCellValue());
	            // System.out.println("--------TYPE_BOOLEAN:" +
	            // targetCell.getBooleanCellValue());
	            break;
	          case HSSFCell.CELL_TYPE_ERROR:
	            toCell.setCellErrorValue(fromCell.getErrorCellValue());
	            // System.out.println("--------TYPE_ERROR:" +
	            // targetCell.getErrorCellValue());
	            break;
	          case HSSFCell.CELL_TYPE_FORMULA:
	            toCell.setCellFormula(parseFormula(fromCell.getCellFormula()));
	            // System.out.println("--------TYPE_FORMULA:" +
	            // targetCell.getCellFormula());
	            break;
	          case HSSFCell.CELL_TYPE_NUMERIC:
	            toCell.setCellValue(fromCell.getNumericCellValue());
	            // System.out.println("--------TYPE_NUMERIC:" +
	            // targetCell.getNumericCellValue());
	            break;
	          case HSSFCell.CELL_TYPE_STRING:
	            toCell.setCellValue(fromCell.getRichStringCellValue());
	            // System.out.println("--------TYPE_STRING:" + i +
	            // targetCell.getRichStringCellValue());
	            break;
	        }
	      }
	    }
	  }

	  /**
	   * 复制行
	   * 如果是同一个HSSFWorkbook中的行请用此方法
	   */
	  public static void copyRows(HSSFWorkbook workbook, int fromSheetIndex, int toSheetIndex, int startRow, int endRow, int position) {
	    HSSFSheet fromSheet = workbook.getSheetAt(fromSheetIndex);
	    HSSFSheet toSheet = workbook.getSheetAt(toSheetIndex);
	    int i;
	    int j;

	    if ((startRow == -1) || (endRow == -1)) {
	      return;
	    }

	    List<CellRangeAddress> oldRanges = new ArrayList<CellRangeAddress>();
	    for (i = 0; i < fromSheet.getNumMergedRegions(); i++) {
	      oldRanges.add(fromSheet.getMergedRegion(i));
	    }

	    // 拷贝合并的单元格。原理：复制当前合并单元格后，原位置的格式会移动到新位置，需在原位置生成旧格式
	    for (CellRangeAddress oldRange : oldRanges) {
	      CellRangeAddress newRange = new CellRangeAddress(oldRange.getFirstRow(), oldRange.getLastRow(),
	          oldRange.getFirstColumn(), oldRange.getLastColumn());

	      if (oldRange.getFirstRow() >= startRow && oldRange.getLastRow() <= endRow) {
	        int targetRowFrom = oldRange.getFirstRow() - startRow + position;
	        int targetRowTo = oldRange.getLastRow() - startRow + position;
	        oldRange.setFirstRow(targetRowFrom);
	        oldRange.setLastRow(targetRowTo);
	        toSheet.addMergedRegion(oldRange);
	        fromSheet.addMergedRegion(newRange);
	      }
	    }

	    // 设置列宽
	    for (i = startRow; i <= endRow; i++) {
	      HSSFRow fromRow = fromSheet.getRow(i);
	      if (fromRow != null) {
	        for (j = fromRow.getLastCellNum(); j >= fromRow.getFirstCellNum(); j--) {
	          toSheet.setColumnWidth(j, fromSheet.getColumnWidth(j));
	          toSheet.setColumnHidden(j, false);
	        }
	        break;
	      }
	    }

	    // 拷贝行并填充数据
	    for (; i <= endRow; i++) {
	      HSSFRow fromRow = fromSheet.getRow(i);
	      if (fromRow == null) {
	        continue;
	      }
	      HSSFRow toRow = toSheet.createRow(i - startRow + position);
	      toRow.setHeight(fromRow.getHeight());
	      for (j = fromRow.getFirstCellNum(); j <= fromRow.getPhysicalNumberOfCells(); j++) {
	        HSSFCell fromCell = fromRow.getCell(j);
	        if (fromCell == null) {
	          continue;
	        }
	        HSSFCell toCell = toRow.createCell(j);
	        toCell.setCellStyle(fromCell.getCellStyle());
	        int cType = fromCell.getCellType();
	        toCell.setCellType(cType);
	        switch (cType) {
	          case HSSFCell.CELL_TYPE_BOOLEAN:
	            toCell.setCellValue(fromCell.getBooleanCellValue());
	            // System.out.println("--------TYPE_BOOLEAN:" +
	            // targetCell.getBooleanCellValue());
	            break;
	          case HSSFCell.CELL_TYPE_ERROR:
	            toCell.setCellErrorValue(fromCell.getErrorCellValue());
	            // System.out.println("--------TYPE_ERROR:" +
	            // targetCell.getErrorCellValue());
	            break;
	          case HSSFCell.CELL_TYPE_FORMULA:
	            toCell.setCellFormula(parseFormula(fromCell.getCellFormula()));
	            // System.out.println("--------TYPE_FORMULA:" +
	            // targetCell.getCellFormula());
	            break;
	          case HSSFCell.CELL_TYPE_NUMERIC:
	            toCell.setCellValue(fromCell.getNumericCellValue());
	            // System.out.println("--------TYPE_NUMERIC:" +
	            // targetCell.getNumericCellValue());
	            break;
	          case HSSFCell.CELL_TYPE_STRING:
	            toCell.setCellValue(fromCell.getRichStringCellValue());
	            // System.out.println("--------TYPE_STRING:" + i +
	            // targetCell.getRichStringCellValue());
	            break;
	        }
	      }
	    }
	  }

	  /**
	   * 复制单元格样式
	   * 此方法主要用于复制2个不同HSSFWorkbook间的单元格样式
	   */
	  public static void copyCellStyle(HSSFWorkbook fromWorkbook, HSSFWorkbook toWorkbook, HSSFCellStyle fromStyle, HSSFCellStyle toStyle) {
	    toStyle.setAlignment(fromStyle.getAlignment());

	    // 边框和边框颜色
	    toStyle.setBorderBottom(fromStyle.getBorderBottom());
	    toStyle.setBorderLeft(fromStyle.getBorderLeft());
	    toStyle.setBorderRight(fromStyle.getBorderRight());
	    toStyle.setBorderTop(fromStyle.getBorderTop());
	    toStyle.setTopBorderColor(fromStyle.getTopBorderColor());
	    toStyle.setBottomBorderColor(fromStyle.getBottomBorderColor());
	    toStyle.setRightBorderColor(fromStyle.getRightBorderColor());
	    toStyle.setLeftBorderColor(fromStyle.getLeftBorderColor());

	    // 字体
	    HSSFFont tofont = toWorkbook.createFont();
	    copyFont(fromStyle.getFont(fromWorkbook), tofont);
	    toStyle.setFont(tofont);

	    // 背景和前景
	    toStyle.setFillBackgroundColor(fromStyle.getFillBackgroundColor());
	    toStyle.setFillForegroundColor(fromStyle.getFillForegroundColor());

	    toStyle.setDataFormat(fromStyle.getDataFormat());
	    toStyle.setFillPattern(fromStyle.getFillPattern());
	    toStyle.setHidden(fromStyle.getHidden());
	    toStyle.setIndention(fromStyle.getIndention());
	    toStyle.setLocked(fromStyle.getLocked());
	    toStyle.setRotation(fromStyle.getRotation());
	    toStyle.setVerticalAlignment(fromStyle.getVerticalAlignment());
	    toStyle.setWrapText(fromStyle.getWrapText());
	  }

	  /**
	   * 复制字体
	   * 此方法主要用于复制2个不同HSSFWorkbook间的字体
	   */
	  public static void copyFont(HSSFFont fromFont, HSSFFont toFont) {
	    toFont.setBoldweight(fromFont.getBoldweight());
	    toFont.setCharSet(fromFont.getCharSet());
	    toFont.setColor(fromFont.getColor());
	    toFont.setFontHeight(fromFont.getFontHeight());
	    toFont.setFontHeightInPoints(fromFont.getFontHeightInPoints());
	    toFont.setFontName(fromFont.getFontName());
	    toFont.setItalic(fromFont.getItalic());
	    toFont.setStrikeout(fromFont.getStrikeout());
	    toFont.setTypeOffset(fromFont.getTypeOffset());
	    toFont.setUnderline(fromFont.getUnderline());
	  }

	  private static String parseFormula(String pPOIFormula) {
	    final String cstReplaceString = "ATTR(semiVolatile)"; //$NON-NLS-1$
	    StringBuffer result;
	    int index;

	    result = new StringBuffer();
	    index = pPOIFormula.indexOf(cstReplaceString);
	    if (index >= 0) {
	      result.append(pPOIFormula.substring(0, index));
	      result.append(pPOIFormula.substring(index + cstReplaceString.length()));
	    } else {
	      result.append(pPOIFormula);
	    }

	    return result.toString();
	  }
	  
	  public static String subZeroAndDot(String s){
		  	if(StringUtil.isEmpty(s)){
		  		return "";
		  	}
	        if(s.indexOf(".") > 0){  
	            s = s.replaceAll("0+?$", "");//去掉多余的0  
	            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉  
	        }  
	        return s;  
	    } 
	  
	  public static String doubleToStr(Double money) {
			BigDecimal big1= new BigDecimal(money);
			big1= big1.setScale(6, BigDecimal.ROUND_HALF_UP);
			if(money==0) return "";
			else return ExcelUtil.subZeroAndDot(big1.toString());
		}
	  public static String doubleToStr2(String money) {
		  if(StringUtil.isEmpty(money)) return null;
			BigDecimal big1= new BigDecimal(money);
			big1= big1.setScale(6, BigDecimal.ROUND_HALF_UP);
			 return ExcelUtil.subZeroAndDot(big1.toString());
		}
	 /* public static void main(String[] args) {
		 System.out.println(subZeroAndDot("10.00000"));
	}*/
}

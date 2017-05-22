package com.ucg.util.excel;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;


public class ExportExcelModel<T> {
	
	/**
	 * 新通用excel导出
	 * 这是一个通用的方法，利用了JAVA的反射机制，可以将放置在JAVA集合中并且符号一定条件的数据以EXCEL 的形式输出到指定IO设备上，用于解析javabean的数据
	 * 
	 * @param title
	 *            表格标题名
	 * @param headers
	 *            表格属性列名数组
	 * @param headColumns
	 *            表格属性列名实体属性数组           
	 * @param dataset
	 *            需要显示的数据集合,集合中一定要放置符合javabean风格的类的对象。此方法支持的
	 *            javabean属性的数据类型有基本数据类型及String,Date,byte[](图片数据)
	 * @param pattern
	 *            如果有时间数据，设定输出格式。默认为"yyy-MM-dd"
	 * @throws IOException 
	 */
	@SuppressWarnings("unchecked")
	public HSSFWorkbook newExportExcelByBean(String title, String[] headers,String[] headColumns,
			Collection<T> dataset, String pattern) throws IOException {
		// 声明一个工作薄
		HSSFWorkbook workbook = new HSSFWorkbook();
		// 生成一个表格
		HSSFSheet sheet = workbook.createSheet(title);
		// 设置表格默认列宽度为15个字节
		sheet.setDefaultColumnWidth(20);
		// 生成一个样式
		HSSFCellStyle style = workbook.createCellStyle();
		// 设置这些样式(标题行样式)
		ExcelUtil.setCellStyle(style, HSSFColor.TURQUOISE.index, HSSFCellStyle.BORDER_THIN, true, HSSFCellStyle.ALIGN_CENTER, HSSFCellStyle.VERTICAL_CENTER);
		// 生成一个字体
		HSSFFont font = workbook.createFont();
		ExcelUtil.setFont(font, HSSFColor.BLACK.index, (short) 10, "宋体", HSSFFont.BOLDWEIGHT_BOLD);
		// 把字体应用到当前的样式
		style.setFont(font);
		// 生成并设置另一个样式
		HSSFCellStyle style2 = workbook.createCellStyle();
		ExcelUtil.setCellStyle(style2, HSSFColor.WHITE.index, HSSFCellStyle.BORDER_THIN, true, HSSFCellStyle.ALIGN_CENTER, HSSFCellStyle.VERTICAL_CENTER);
		// 生成另一个字体
		HSSFFont font2 = workbook.createFont();
		ExcelUtil.setFont(font2, HSSFColor.BLACK.index, (short) 10, "宋体", HSSFFont.BOLDWEIGHT_NORMAL);
		// 把字体应用到当前的样式
		style2.setFont(font2);
		// 声明一个画图的顶级管理器
		HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
		
		// 产生表格标题行
		HSSFRow row = sheet.createRow(0);
		row.setHeight((short)450);
		for (int i = 0; i < headers.length; i++) {
			HSSFCell cell = row.createCell(i);
			cell.setCellStyle(style);
			HSSFRichTextString text = new HSSFRichTextString(headers[i]);
			cell.setCellValue(text);
		}
		// 遍历集合数据，产生数据行
		Iterator<T> it = dataset.iterator();
		int index = 0;
		while (it.hasNext()) {
			index++;
			row = sheet.createRow(index);
			row.setHeight((short)450);
			
			HSSFCell cell = row.createCell(0);
			cell.setCellStyle(style2);
			sheet.setColumnWidth(0, 1500);
			cell.setCellValue(index);
			
			T t = (T) it.next();
			// 利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值
			//Field[] fields = t.getClass().getDeclaredFields();
			for(int i=0;i<headColumns.length;i++){
				
				    String fieldName = headColumns[i];
						cell = row.createCell(i+1);
						cell.setCellStyle(style2);
						String getMethodName = "get"
								+ fieldName.substring(0, 1).toUpperCase()
								+ fieldName.substring(1);
						try {
							Class tCls = t.getClass();
							Method getMethod = tCls.getMethod(getMethodName,
									new Class[] {});
							Object value = getMethod.invoke(t, new Object[] {});
							if(value == null){
								value = "";
							}
							// 判断值的类型后进行强制类型转换
							String textValue = null;
							
							if (value instanceof Boolean) {
								boolean bValue = (Boolean) value;
								textValue = "是";
								if (!bValue) {
									textValue = "否";
								}
							} else if (value instanceof Date) {
								Date date = (Date) value;
								SimpleDateFormat sdf = new SimpleDateFormat(pattern);
								textValue = sdf.format(date);
							} else if (value instanceof byte[]) {
								// 有图片时，设置行高为60px;
								row.setHeightInPoints(60);
								// 设置图片所在列宽度为80px,注意这里单位的一个换算
								sheet.setColumnWidth(i, (short) (35.7 * 80));
								// sheet.autoSizeColumn(i);
								byte[] bsValue = (byte[]) value;
								HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0,
										1023, 255, (short) 6, index, (short) 6, index);
								anchor.setAnchorType(2);
								patriarch.createPicture(anchor, workbook.addPicture(
										bsValue, HSSFWorkbook.PICTURE_TYPE_JPEG));
							} else {
								// 其它数据类型都当作字符串简单处理
								textValue = value.toString();
							}
							// 如果不是图片数据，就利用正则表达式判断textValue是否全部由数字组成
							if (textValue != null) {
								if("null".equals(textValue)){
									textValue = "";
								}
								Pattern p = Pattern.compile("([1-9]+[0-9]*|0)(\\.[\\d]+)?");
								Matcher matcher = p.matcher(textValue);
								if (matcher.matches()) {
									// 是数字当作double处理
									cell.setCellValue(Double.parseDouble(textValue));
								} else {
									HSSFRichTextString richString = new HSSFRichTextString(
											textValue);
									richString.applyFont(font2);
									cell.setCellValue(richString);
								}
							}
						} catch (SecurityException e) {
							e.printStackTrace();
						} catch (NoSuchMethodException e) {
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						} finally {
							// 清理资源
						}
					}
				}
		return workbook;
	}
	
	}

package com.ucg.util.excel;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellRangeAddress;

import com.ucg.base.framework.core.util.StringUtil;


public class ExportExcel<T> {
	

	public static HSSFWorkbook workbook;
	public static HSSFCellStyle dateStyle;//日期样式
	public static HSSFCellStyle currencyStyle;//货币
	public static HSSFCellStyle moneyNumberStyle;//金钱样式
	public static HSSFCellStyle builtinStyle;//百分比样式
	
	//之所以要初始化，因为 报了 The maximum number of cell styles was exceeded. You can define up to 4000 styles in a .xls workbook 这个错误
	public static void init(HSSFWorkbook workbook){
		ExportExcel.workbook=workbook;
		dateStyle = workbook.createCellStyle();
		currencyStyle = workbook.createCellStyle();
		moneyNumberStyle = workbook.createCellStyle();
		builtinStyle = workbook.createCellStyle();
	}
	

	/**     
	* 创建人：陈永培   
	* 创建时间：2016-12-27 下午3:55:04
	* 功能说明：格式化小数点(默认6位)    
	*/
	public static void formatMoneyNumber(Cell cell,BigDecimal bigDecimal){
		if(bigDecimal!=null){
			DecimalFormat df=new DecimalFormat("0.000000");
			cell.setCellValue(df.format(bigDecimal));
			moneyNumberStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
			cell.setCellStyle(moneyNumberStyle);
		}
	}
	
	/**     
	* 创建人：陈永培   
	* 创建时间：2016-12-27 下午3:55:04
	* 功能说明：格式化日期(默认yyyy/MM/dd)    
	*/
	public static void formatDate(Cell cell,Date date){
			if(date!=null){
				cell.setCellValue(date);
			    HSSFDataFormat format= workbook.createDataFormat();
			    dateStyle.setDataFormat(format.getFormat("yyyy/MM/dd"));
			    cell.setCellStyle(dateStyle);
			}
	}
	
	/**     
	* 创建人：陈永培   
	* 创建时间：2016-12-27 下午4:03:12
	* 功能说明：货币格式化    
	*/
	public static void formatCurrency (Cell cell,BigDecimal bigDecimal){
		if(bigDecimal!=null){
			cell.setCellValue(bigDecimal.doubleValue());
		    HSSFDataFormat format= workbook.createDataFormat();
		    currencyStyle.setDataFormat(format.getFormat("¥#,##0"));
		    cell.setCellStyle(currencyStyle);
		}
	}
	
	
	/**     
	* 创建人：陈永培   
	* 创建时间：2016-12-27 下午4:00:46
	* 功能说明： 格式化百分比   
	*/
	public static void formatBuiltin(Cell cell,BigDecimal bigDecimal){
		if(bigDecimal!=null){
			bigDecimal = bigDecimal.divide(new BigDecimal(100));
			cell.setCellValue(bigDecimal.doubleValue());
			builtinStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00%"));
		    cell.setCellStyle(builtinStyle);
		}
	}
	
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

	
	@SuppressWarnings("unchecked")
	public HSSFWorkbook newNeedRepayExcelByBean(String title, String[] headers,String[] headColumns,
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
		
		// 生成并设置另一个样式
		HSSFCellStyle style3 = workbook.createCellStyle();
		ExcelUtil.setCellStyle(style3, HSSFColor.WHITE.index, HSSFCellStyle.BORDER_THIN, true, HSSFCellStyle.ALIGN_RIGHT, HSSFCellStyle.VERTICAL_CENTER);
		style3.setFont(font2);
		// 生成并设置另一个样式
		HSSFCellStyle style4 = workbook.createCellStyle();
		ExcelUtil.setCellStyle(style4, HSSFColor.WHITE.index, HSSFCellStyle.BORDER_THIN, true, HSSFCellStyle.ALIGN_LEFT, HSSFCellStyle.VERTICAL_CENTER);
		style4.setFont(font2);
		
		HSSFCellStyle style5 = workbook.createCellStyle();
		HSSFDataFormat df = workbook.createDataFormat(); 
		style5.setDataFormat(df.getFormat("#,##0.00"));
		ExcelUtil.setCellStyle(style5, HSSFColor.WHITE.index, HSSFCellStyle.BORDER_THIN, true, HSSFCellStyle.ALIGN_RIGHT, HSSFCellStyle.VERTICAL_CENTER);
		style3.setFont(font2);
		
		
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
						if ("tradeCurrency".equals(headColumns[i])) {
							cell.setCellStyle(style3);
						}else if ("tradeMoney".equals(headColumns[i])) {
							cell.setCellStyle(style5);
						} else{
							cell.setCellStyle(style4);
						}
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
	
	public HSSFWorkbook exportExcelWithExByBean(String title, String[] headers,String[] headColumns,
			Collection<T> dataset, String pattern, String extraParams) {
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
				
				// 生成并设置另一个样式
				HSSFCellStyle style3 = workbook.createCellStyle();
				ExcelUtil.setCellStyle(style3, HSSFColor.WHITE.index, HSSFCellStyle.BORDER_THIN, true, HSSFCellStyle.ALIGN_CENTER, HSSFCellStyle.VERTICAL_CENTER);
				// 生成另一个字体
				HSSFFont font3 = workbook.createFont();
				ExcelUtil.setFont(font3, HSSFColor.BLACK.index, (short) 12, "宋体", HSSFFont.BOLDWEIGHT_NORMAL);
				
				// 生成并设置另一个样式
				HSSFCellStyle style4 = workbook.createCellStyle();
				HSSFDataFormat df = workbook.createDataFormat(); 
				style4.setDataFormat(df.getFormat("#,##0.00"));
				ExcelUtil.setCellStyle(style4, HSSFColor.WHITE.index, HSSFCellStyle.BORDER_THIN, true, HSSFCellStyle.ALIGN_RIGHT, HSSFCellStyle.VERTICAL_CENTER);
				// 生成另一个字体
				HSSFFont font4 = workbook.createFont();
				ExcelUtil.setFont(font4, HSSFColor.BLACK.index, (short) 10, "宋体", HSSFFont.BOLDWEIGHT_NORMAL);
				
				// 声明一个画图的顶级管理器
				HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
				

				
				// 额外内容行
				HSSFRow extraRow = sheet.createRow(0);
				extraRow.setHeight((short)900);
				HSSFCell extraCell = extraRow.createCell(0);
				extraCell.setCellStyle(style3);
				extraCell.setCellValue(extraParams);
				sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headers.length-1));
				sheet.setColumnWidth(0, 500);
				style3.setAlignment(HSSFCellStyle.ALIGN_LEFT);// 水平     
				
				// 产生表格标题行
				HSSFRow row = sheet.createRow(1);
				row.setHeight((short)450);
				for (int i = 0; i < headers.length; i++) {
					HSSFCell cell = row.createCell(i);
					cell.setCellStyle(style);
					HSSFRichTextString text = new HSSFRichTextString(headers[i]);
					cell.setCellValue(text);
				}
				// 遍历集合数据，产生数据行
				Iterator<T> it = dataset.iterator();
				int index = 1;
				while (it.hasNext()) {
					index++;
					row = sheet.createRow(index);
					row.setHeight((short)450);
					
					HSSFCell cell = row.createCell(0);
					cell.setCellStyle(style2);
					sheet.setColumnWidth(0, 1500);
					cell.setCellValue(index-1);
					
					T t = (T) it.next();
					// 利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值
					//Field[] fields = t.getClass().getDeclaredFields();
					for(int i=0;i<headColumns.length;i++){
						
						    String fieldName = headColumns[i];
								cell = row.createCell(i+1);
								if (i > 1) {
									cell.setCellStyle(style4);
								}else {
									cell.setCellStyle(style2);
								}
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
	
	public HSSFWorkbook exportCommonExcel(String title, String[] headers,String[] headColumns,String totalFields,
			List< Map< String , Object > > dataset, String pattern,String excelTitle) throws IOException {
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet(title);
		// 设置表格默认列宽度为15个字节
		sheet.setDefaultColumnWidth(15);
		sheet.setColumnWidth(0, 3000);
		sheet.setColumnWidth(8, 6000);
		sheet.setColumnWidth(31, 6000);
		sheet.setColumnWidth(32, 6000);
		sheet.setColumnWidth(54, 6000);
		sheet.setColumnWidth(55, 6000);
		sheet.setColumnWidth(57, 5000);
		// 生成一个样式
		HSSFCellStyle style = workbook.createCellStyle();
		// 设置这些样式(标题行样式)
		ExcelUtil.setCellStyle(style, HSSFColor.TURQUOISE.index, HSSFCellStyle.BORDER_THIN, true, HSSFCellStyle.ALIGN_CENTER, HSSFCellStyle.VERTICAL_CENTER);
		// 生成一个字体
		HSSFFont font = workbook.createFont();
		ExcelUtil.setFont(font, HSSFColor.BLACK.index, (short) 10, "宋体", HSSFFont.BOLDWEIGHT_NORMAL);
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
		
		HSSFCellStyle style3 = workbook.createCellStyle();
		ExcelUtil.setCellStyle(style3, HSSFColor.WHITE.index, HSSFCellStyle.BORDER_NONE, true, HSSFCellStyle.ALIGN_CENTER, HSSFCellStyle.VERTICAL_CENTER);
		// 生成另一个字体
		HSSFFont font3 = workbook.createFont();
		ExcelUtil.setFont(font3, HSSFColor.BLACK.index, (short) 18, "宋体", HSSFFont.BOLDWEIGHT_BOLD);
		
		// 把字体应用到当前的样式
		style3.setFont(font3);
		
		HSSFCellStyle style4 = workbook.createCellStyle();
		ExcelUtil.setCellStyle(style4, HSSFColor.WHITE.index, HSSFCellStyle.BORDER_NONE, true, HSSFCellStyle.ALIGN_RIGHT, HSSFCellStyle.VERTICAL_BOTTOM);
		// 把字体应用到当前的样式
		style4.setFont(font2);
		
		// 声明一个画图的顶级管理器
		//HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
		HSSFRow row = sheet.createRow(0);
		row.setHeight((short)800);
		for(int i=0;i<=8;i++){
			HSSFCell cell = row.createCell(i);
			cell.setCellStyle(style3);
			if(i==2){
				HSSFRichTextString text = new HSSFRichTextString(excelTitle);
				cell.setCellValue(text);
			}else{
				cell.setCellValue("");
			}
			if(i==0){
				cell.setCellStyle(style4);
				cell.setCellValue("单位：万元");
			}
		}
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 2, 9));
		row = sheet.createRow(1);
		row.setHeight((short)450);
		for (int i = 0; i < headers.length; i++) {
			HSSFCell cell = row.createCell(i);
			cell.setCellStyle(style);
			HSSFRichTextString text = new HSSFRichTextString(headers[i]);
			cell.setCellValue(text);
		}
		
		// 遍历集合数据，产生数据行
		int index = 1;
		//int beginSumIndex = 1;
		for( Map< String , Object > t : dataset ){
			index++;
			row = sheet.createRow(index);
			row.setHeight((short)450);
			HSSFCell cell = null;
			cell = row.createCell(0);
			cell.setCellStyle(style2);
			cell.setCellValue(index-1);
			for(int i=0;i<headColumns.length;i++){
				cell = row.createCell(i+1);
				cell.setCellStyle(style2);
			    String fieldName = headColumns[i];
			    Object value = t.get( fieldName ) == null ? "" : t.get( fieldName );
			    String textValue = null;
			    if( value instanceof Date ){
			    	Date date = (Date) value;
					SimpleDateFormat sdf = new SimpleDateFormat(pattern);
					textValue = sdf.format(date);
			    }else{
			    	textValue = value.toString();
			    }
			    if (textValue != null) {
					if("null".equals(textValue)){
						textValue = "";
					}
					Pattern p = Pattern.compile("-?([1-9]+[0-9]*|0)(\\.[\\d]+)?");
					Matcher matcher = p.matcher(textValue);
					if (matcher.matches()) {
						cell.setCellValue( Double.parseDouble( StringUtil.isEmpty( textValue ) ? "0" : textValue ) );
					} else {
						HSSFRichTextString richString = new HSSFRichTextString(textValue);
						richString.applyFont(font2);
						cell.setCellValue(richString);
					}
				}
			}
		}
		
		//合计列
		if( StringUtil.isNotEmpty( totalFields ) ){
			String col_Str = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
			row = sheet.createRow( index + 1 );
			row.setHeight( ( short )450 );
			HSSFCell cell = row.createCell( 0 );
			cell.setCellStyle( style2 );
			cell.setCellValue( new HSSFRichTextString( "合计" ) );
			for( int i = 0 ; i < headColumns.length ; i++ ){
				cell = row.createCell( i + 1 );
				cell.setCellStyle( style2 );
				if( totalFields.indexOf( headColumns[i] ) != -1 ){
					cell.setCellType( HSSFCell.CELL_TYPE_FORMULA );
					String formula = "SUM(" + col_Str.charAt( i + 1 ) + "" + 3 + ":" + col_Str.charAt( i + 1 ) + "" + ( index + 1 ) + ")";
					cell.setCellFormula( formula );
				}else{
					HSSFRichTextString text = new HSSFRichTextString( "" );
					cell.setCellValue(text);
				}
			}
		}
		return workbook;
	}
	
	
	// 台账导出
	public HSSFWorkbook exportBusinessLedgerExcel(String title, String[] headers,String[] headColumns,String totalFields,
			List< Map< String , Object > > dataset, String pattern,String excelTitle) throws IOException {
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet(title);
		// 设置表格默认列宽度为15个字节
		sheet.setDefaultColumnWidth(15);
		
		/*指定列宽*/
		sheet.setColumnWidth(0, 3000);
		sheet.setColumnWidth(8, 6000);
		sheet.setColumnWidth(28, 6000);
		sheet.setColumnWidth(9, 8000);
		sheet.setColumnWidth(16, 8000);
		sheet.setColumnWidth(30, 6000);
				
		/* 生成一个样式 样式一*/
		HSSFCellStyle style = workbook.createCellStyle();
		/* 设置这些样式(标题行样式)*/
		ExcelUtil.setCellStyle(style, HSSFColor.TURQUOISE.index, HSSFCellStyle.BORDER_THIN, true, HSSFCellStyle.ALIGN_CENTER, HSSFCellStyle.VERTICAL_CENTER);
		/*生成一个字体,并把字体应用到当前的样式*/
		HSSFFont font = workbook.createFont();
		ExcelUtil.setFont(font, HSSFColor.BLACK.index, (short) 10, "宋体", HSSFFont.BOLDWEIGHT_NORMAL);
		style.setFont(font);
		
		/*样式二*/
		HSSFCellStyle style2 = workbook.createCellStyle();
		ExcelUtil.setCellStyle(style2, HSSFColor.WHITE.index, HSSFCellStyle.BORDER_THIN, true, HSSFCellStyle.ALIGN_CENTER, HSSFCellStyle.VERTICAL_CENTER);
		HSSFFont font2 = workbook.createFont();
		ExcelUtil.setFont(font2, HSSFColor.BLACK.index, (short) 10, "宋体", HSSFFont.BOLDWEIGHT_NORMAL);
		style2.setFont(font2);
		
		/*样式三*/
		HSSFCellStyle style3 = workbook.createCellStyle();
		ExcelUtil.setCellStyle(style3, HSSFColor.WHITE.index, HSSFCellStyle.BORDER_NONE, true, HSSFCellStyle.ALIGN_CENTER, HSSFCellStyle.VERTICAL_CENTER);
		HSSFFont font3 = workbook.createFont();
		ExcelUtil.setFont(font3, HSSFColor.BLACK.index, (short) 18, "宋体", HSSFFont.BOLDWEIGHT_BOLD);
		style3.setFont(font3);
		
		/*样式四*/
		HSSFCellStyle style4 = workbook.createCellStyle();
		HSSFDataFormat df = workbook.createDataFormat(); 
		style4.setDataFormat(df.getFormat("#,##0.00"));
		ExcelUtil.setCellStyle(style4, HSSFColor.WHITE.index, HSSFCellStyle.BORDER_THIN, true, HSSFCellStyle.ALIGN_RIGHT, HSSFCellStyle.VERTICAL_BOTTOM);
		style4.setFont(font2);
		
		/*样式5*/
		HSSFCellStyle style5 = workbook.createCellStyle();
		ExcelUtil.setCellStyle(style5, HSSFColor.WHITE.index, HSSFCellStyle.BORDER_THIN, true, HSSFCellStyle.ALIGN_LEFT, HSSFCellStyle.VERTICAL_BOTTOM);
		style4.setFont(font2);
		
		
		// 声明一个画图的顶级管理器
		//HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
		HSSFRow row = sheet.createRow(0); /*创建第一行*/
		row.setHeight((short)900);		/*设置第一行的高度*/
		for(int i = 0; i <= 30; i++) {   /*一共有30列表*/
			HSSFCell cell = row.createCell(i); /*在第一行创建27个单元格*/
			cell.setCellStyle(style3);  /*第一行的样式用样式三*/
			if(i == 2) {
				HSSFRichTextString text = new HSSFRichTextString(excelTitle); /*在第二个单元格处设置单元格的内容*/
				cell.setCellValue(text);
			}else{
				cell.setCellValue("");  /*其他单元格设置为空*/
			}
			/*这里在第一个单元格位置处设置单位描述,这里不需要.可以设置为动态配置。传入参数即可*/
//			if(i==0){
//				cell.setCellStyle(style4);
//				cell.setCellValue("单位：万元");
//			}
		}
		/*合并单元格 这里可以动态的配置四个参数分辨是：第一行,最后一行,第一列,最后一列*/
		//sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));  	/*这里是合并单元：万元的*/
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 2, 8)); /*这里是合并的整个标题行所有的列*/
		row = sheet.createRow(1);   /*又新建了一行*/
		row.setHeight((short)450);  /*设置该行的高度为450*/
		for (int i = 0; i < headers.length; i++) {  /*遍历所有要展示的列名*/
			HSSFCell cell = row.createCell(i);  /*创建每一列*/
			cell.setCellStyle(style);   /*该列每一个单元格设置的样式*/
			if(i == 1) { /*第一行设置的是项目基本信息*/
				HSSFRichTextString text = new HSSFRichTextString("项目基本信息");
				cell.setCellValue(text);
			}else if(i == 8) {
				HSSFRichTextString text = new HSSFRichTextString("项目放款信息");
				cell.setCellValue(text);
			}else if(i == 15) {
				HSSFRichTextString text = new HSSFRichTextString("项目还款及逾期信息");
				cell.setCellValue(text);
			}else if(i == 19) {
				HSSFRichTextString text = new HSSFRichTextString("借款人信息");
				cell.setCellValue(text);
			}else if(i == 24) {
				HSSFRichTextString text = new HSSFRichTextString("行业信息");
				cell.setCellValue(text);
			}else if(i == 28) {
				HSSFRichTextString text = new HSSFRichTextString("所在地信息");
				cell.setCellValue(text);
			}else if((i > 1 && i<=7) || (i > 8 && i <= 14) || (i > 15 && i<= 18) ||
					(i > 19 && i<=23) || (i > 24 && i<= 26) || (i > 27 && i<= 30)) { /*这里的意思就是在合并单元格的时候就是开始列有数据,其他列都不需要有数据*/
				HSSFRichTextString text = new HSSFRichTextString("");
				cell.setCellValue(text);
			}else { /*如果没有数据,默认设置传入的标题,列的2,3行需要合并起来*/
				HSSFRichTextString text = new HSSFRichTextString(headers[i]); 
				cell.setCellValue(text);
			}
		}
		
		row = sheet.createRow(2); /*又创建一行*/
		row.setHeight((short)450); 
		for (int i = 0; i < headers.length; i++) { /*该行也是用于设置标题*/
			HSSFCell cell = row.createCell(i);
			cell.setCellStyle(style);
			if(!((i >= 1 && i <= 7) || (i >= 8 && i <= 14) || (i >= 15 && i <= 18) || (i >= 19 && i <= 23) || (i >= 24 && i<= 26) || (i >= 27 && i<= 30))) {
				HSSFRichTextString text = new HSSFRichTextString("");
				cell.setCellValue(text);
				sheet.addMergedRegion(new CellRangeAddress(1, 2, i, i));
			}else{
				HSSFRichTextString text = new HSSFRichTextString(headers[i]);
				cell.setCellValue(text);
			}
		}
		
		/*合并单元格*/
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 1, 7));
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 8, 14));
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 15, 18));
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 19, 23));
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 24, 26));
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 27, 30));

		// 遍历集合数据，产生数据行
		int index = 2;
		//int beginSumIndex = 1;
		for( Map< String , Object > t : dataset ){
			index++;
			row = sheet.createRow(index);
			row.setHeight((short)450);
			HSSFCell cell = null;
			cell = row.createCell(0);
			cell.setCellStyle(style2);
			cell.setCellValue(index-2);
			for(int i=0;i<headColumns.length;i++) {
				cell = row.createCell(i+1);
				cell.setCellStyle(style5);
			    String fieldName = headColumns[i];
			    Object value = t.get( fieldName ) == null ? "" : t.get( fieldName );
				if ("projectnumber".equals(fieldName) || "papernumber".equals(fieldName) || "mobilephone".equals(fieldName)) { // 对很长的数字进行处理
					value = value + " ";
				}
				if ("financingmoney".equals(fieldName) || "actualloanmoney".equals(fieldName) || //
						"baninterest".equals(fieldName) ||"actualrepaymentmoney".equals(fieldName) ||//
						"shouldrepaymoney".equals(fieldName) || "overduemoney".equals(fieldName) || "punishmoney".equals(fieldName)) { // 金额数据居右
					cell.setCellStyle(style4);
				}else if ("financingperiod".equals(fieldName) || "financingbegindate".equals(fieldName) || //
						"acceptdate".equals(fieldName) || "financingenddate".equals(fieldName)) { // 这些居中
					cell.setCellStyle(style2);
				}
				
			    String textValue = null;
			    if( value instanceof Date ){
			    	Date date = (Date) value;
					SimpleDateFormat sdf = new SimpleDateFormat(pattern);
					textValue = sdf.format(date);
			    }else{
			    	textValue = value.toString();
			    }
			    if (textValue != null) {
					if("null".equals(textValue)){
						textValue = "";
					}
					Pattern p = Pattern.compile("-?([1-9]+[0-9]*|0)(\\.[\\d]+)?");
					Matcher matcher = p.matcher(textValue);
					if (matcher.matches()) {
						cell.setCellValue( Double.parseDouble( StringUtil.isEmpty( textValue ) ? "0" : textValue ) );
					} else {
						HSSFRichTextString richString = new HSSFRichTextString(textValue);
						richString.applyFont(font2);
						cell.setCellValue(richString);
					}
				}
			}
		}
		return workbook;
		}
	}



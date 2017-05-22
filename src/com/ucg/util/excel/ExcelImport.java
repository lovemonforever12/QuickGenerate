package com.ucg.util.excel; 
/** 
* @author 作者 施鹏程
* @version 创建时间：2016年10月24日 下午2:39:36 
* 类说明 
*/
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;

import com.ucg.base.framework.core.util.StringUtil;

public class ExcelImport {
 
	private static final String EXTENSION_XLS = "xls";
    private static final String EXTENSION_XLSX = "xlsx";
 
    
    private String dateConverter( String dateStr) {
    	if (StringUtil.isNotEmpty(dateStr)) {
    		String [] str = dateStr.split("-");
        	String monthStr = conveterMonth( str[1]);
        	return str[2] + "-" + monthStr + "-" + str[0]; 
    	}{
    		return "";
    	}
    	
    }
    
    private String conveterMonth(String str) {
    	String reusltMonthStr = "";
		if ("一月".equals(str)) {
			reusltMonthStr = "01";
		}else if ("二月".equals(str)) {
			reusltMonthStr = "02";
		}else if ("三月".equals(str)) {
			reusltMonthStr = "03";
		}else if ("四月".equals(str)) {
			reusltMonthStr = "04";
		}else if ("五月".equals(str)) {
			reusltMonthStr = "05";
		}else if ("六月".equals(str)) {
			reusltMonthStr = "06";
		}else if ("七月".equals(str)) {
			reusltMonthStr = "07";
		}else if ("八月".equals(str)) {
			reusltMonthStr = "08";
		}else if ("九月".equals(str)) {
			reusltMonthStr = "09";
		}else if ("十月".equals(str)) {
			reusltMonthStr = "10";
		}else if ("十一月".equals(str)) {
			reusltMonthStr = "11";
		}else if ("十二月".equals(str)) {
			reusltMonthStr = "12";
		}
		return reusltMonthStr;
	}

	

    
    /**
     * @author 作者 施鹏程
     * @version 创建时间：2016年11月16日 下午5:29:41 
     * 方法说明 解决excel类型问题，获得数值
     * @param map 
     */
  	public static String getValue(Cell cell, Map<String, Object> map) {
  		String value = "";
  		if(null==cell){
  			return value;
  		}
  		switch (cell.getCellType()) {
  	 	//数值型
    		case Cell.CELL_TYPE_NUMERIC:
    			if (HSSFDateUtil.isCellDateFormatted(cell)) {
  			    //如果是date类型则 ，获取该cell的date值
    				Date date = HSSFDateUtil.getJavaDate(cell.getNumericCellValue());
    				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
  			    value = format.format(date);;
    			}else {// 纯数字
    				double numericCellValue = cell.getNumericCellValue();
    				BigDecimal big=new BigDecimal(String.valueOf(numericCellValue));
    				value = big.toString();
    				//解决1234.0  去掉后面的.0
    				if(null!=value&&!"".equals(value.trim())){
    				     String[] item = value.split("[.]");
    				     if(1<item.length&&"0".equals(item[1])){
    				    	 value=item[0];
    				     }
    				}
    			}
    			break;
    			//字符串类型 
    		case Cell.CELL_TYPE_STRING:
    			value = cell.getStringCellValue().toString();
    			break;
    		// 公式类型
    		case Cell.CELL_TYPE_FORMULA:
    			//读公式计算值
    			value = String.valueOf(cell.getNumericCellValue());
    			if (value.equals("NaN")) {// 如果获取的数据值为非法值,则转换为获取字符串
    				value = cell.getStringCellValue().toString();
    			}
    			break;
    			
    		// 布尔类型
    		case Cell.CELL_TYPE_BOOLEAN:
    			value = " "+ cell.getBooleanCellValue();
    			break;
    		// 空值
    		case Cell.CELL_TYPE_BLANK: 
    			value = "";
    			map.put("msg", "excel出现空值");
    			break;
    		// 故障
    		case Cell.CELL_TYPE_ERROR: 
    			value = "";
    			map.put("msg", "excel出现故障");
    			break;
    		default:
    			value = cell.getStringCellValue().toString();
  	}
  	if("null".endsWith(value.trim())){
  		value="";
  	}
    return value;
  	}
 
}
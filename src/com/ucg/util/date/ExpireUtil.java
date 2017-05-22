package com.ucg.util.date;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.ucg.util.string.StringUtil;

public class ExpireUtil {
	
	/** 
	* 获得指定日期的前一天 
	* @param specifiedDay 
	* @return 
	 * @throws java.text.ParseException 
	* @throws Exception 
	*/ 
	public static String getSpecifiedDayBefore(String specifiedDay,int beforday) throws Exception{ 
		SimpleDateFormat simpleDateFormat=null;
		if(StringUtil.isEmpty(specifiedDay)){
			 simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd"); 
			 specifiedDay=simpleDateFormat.format(new Date());
		}
		Calendar c = Calendar.getInstance(); 
		Date date=null; 
		date = new SimpleDateFormat("yyyy-MM-dd").parse(specifiedDay); 
		c.setTime(date); 
		int day=c.get(Calendar.DATE); 
		c.set(Calendar.DATE,day-beforday); 
	
		String dayBefore=new SimpleDateFormat("yyyy-MM-dd").format(c.getTime()); 
		return dayBefore; 
	} 
	/** 
	* 获得指定日期的后一天 
	* @param specifiedDay 
	* @return 
	 * @throws java.text.ParseException 
	*/ 
	public static String getSpecifiedDayAfter(String specifiedDay,int afterday) throws Exception{ 
		SimpleDateFormat simpleDateFormat=null;
		if(StringUtil.isEmpty(specifiedDay)){
			 simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd"); 
			 specifiedDay=simpleDateFormat.toString();
		}
		Calendar c = Calendar.getInstance(); 
		Date date=null; 
		date = new SimpleDateFormat("yyyy-MM-dd").parse(specifiedDay); 
		c.setTime(date); 
		int day=c.get(Calendar.DATE); 
		c.set(Calendar.DATE,day+afterday); 
		String dayAfter=new SimpleDateFormat("yyyy-MM-dd").format(c.getTime()); 
		return dayAfter; 
	} 
	
	public static String getCurrentBeforeDate(int dataLastDay){
		Calendar now = Calendar.getInstance();
		now.set(Calendar.DATE, now.get(Calendar.DATE) - dataLastDay);
        Date createTime = now.getTime();
        String formatDate = com.ucg.base.framework.core.util.DateUtils.formatDate(createTime, "yyyy-MM-dd");
		return formatDate;
		
	}
	
	public static String getCurrentAfterDate(int dataLastDay){
		Calendar now = Calendar.getInstance();
		now.set(Calendar.DATE, now.get(Calendar.DATE) + dataLastDay);
        Date createTime = now.getTime();
        String formatDate = com.ucg.base.framework.core.util.DateUtils.formatDate(createTime, "yyyy-MM-dd");
		return formatDate;
		
	}
	
	public static void main(String[] args) {
		
		try {
			System.out.println(getCurrentBeforeDate(14)+" "+getCurrentAfterDate(17));
			System.out.println(getSpecifiedDayBefore(getCurrentBeforeDate(0),14)+" "+getSpecifiedDayAfter(getCurrentAfterDate(0),17));
			System.out.println(getSpecifiedDayBefore(getCurrentBeforeDate(1),14)+" "+getSpecifiedDayAfter(getCurrentAfterDate(1),17));
			System.out.println(getSpecifiedDayBefore("",14)+" "+getSpecifiedDayAfter(getCurrentAfterDate(1),17));
		} catch (Exception e) {
			e.printStackTrace();
		} 

		
		
	}
	

}

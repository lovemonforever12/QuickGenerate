package com.ucg.util.date;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FormatDataToStr {
	
	public static String  getTimeStr(Date date1){
		 SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		 SimpleDateFormat sdf2  = new SimpleDateFormat("HH:mm:ss");
		 try {
			 long nd = 1000*24*60*60;//一天的毫秒数
			 long nh = 1000*60*60;//一小时的毫秒数
			 long nm = 1000*60;//一分钟的毫秒数
			 long ns = 1000;
			Long long1=date1.getTime();
			Date date=new Date();
			Long long2= date.getTime();
			Long long3= long2-long1;
			System.out.println();
			if((long3/nd)==1){
				if(date.getDate()-date1.getDate()>1)
					return sdf.format(date1);
				else
					return "昨天"+sdf2.format(date1);
			}else if((long3/nd)>1){
				return sdf.format(date1);
			}else if((long3/nd)<1){
				if(long3/nh>=1){
					if(date1.getDate()==date.getDate())
						return sdf2.format(date1);
					else
						return "昨天"+sdf2.format(date1);
				}else if(long3/nm>=1){
					return (long3/nm)+"分钟前";
				}else{
					return (long3/ns)+"秒前";
				}
			}
		} catch (Exception e) {
			 e.printStackTrace();
		}
		return "";
	}
	
	
	public static String totalDataStr(Date smallDate,Date bigDate){
		String timeStr="";
		if(smallDate==null || bigDate==null) 
			return timeStr;
		try {
			long l2=bigDate.getTime();
			long l1=smallDate.getTime();
			 long nd = 1000*24*60*60;//一天的毫秒数
			 long nh = 1000*60*60;//一小时的毫秒数
			 long nm = 1000*60;//一分钟的毫秒数
			 long ns = 1000;
			long l3=l2-l1;
			if(l3<ns) timeStr="不足1秒";
			else if(l3>=ns && l3<nm) {
				timeStr=(l3/ns)+"秒";
			}else if(l3>=nm && l3<nh){
				timeStr=(l3/nm)+"分钟"+(((l3%nm)/ns)==0 ? "":((l3%nm)/ns)+"秒");
			}else if(l3>=nh && l3< nd){
				timeStr=(l3/nh)+"小时";
				timeStr+=((l3%nh)/nm)==0?"":((l3%nh)/nm)+"分钟";
			}else if(l3>nd){
				timeStr=(l3/nd)+"天";
				timeStr+=((l3%nd)/nh)==0?"":((l3%nd)/nh)+"小时";
			}
				
			 
			
		} catch (Exception e) {
			 
		}
		return timeStr;
	}
	
	public static void main(String[] args) {
		
	}

}

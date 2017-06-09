package com.ucg.util.common;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.poi2.hssf.model.Model;

import com.ucg.base.framework.core.common.model.json.AjaxJson;
import com.ucg.base.framework.core.constant.AppConstant;
import com.ucg.base.framework.core.util.JSONHelper;
import com.ucg.base.framework.tag.core.easyui.TagUtil;
import com.ucg.util.freemark.FreeMarkerUtil;
import com.ucg.util.mail.Mailer;
import com.ucg.util.string.StringUtil;


@SuppressWarnings("unused")
public class CommonUtil {
	
	public static final CommonUtil cu = new CommonUtil();
	
	
	public static CommonUtil get(){
		return cu;
	}
	
	

	
	/**     
	* 创建人：陈永培   
	* 创建时间：2017-5-12 上午1:36:29
	* 功能说明：添加替换参数    
	*/
	public CommonUtil addPara(Map<String,String> paras,String key,String value){
		Map<String,String> newPara = paras;
		newPara.put(key, value);
		return this;
	}
	
	/***
	 * 短信验证码消息类型
	 */
	public static final Map<String, String> phoneTypeCode = new HashMap<String, String>(){
		private static final long serialVersionUID = 1L;
		{
			put("0","验证码已失效");			
			put("1","此手机没有发送短信验证码或已失效，请重新发送");			
			put("2","短信验证码不正确");	
		}
	};
	
	
	
	/**
	* 创建人： 王郎郎
	* 创建时间：2016-2-28 下午06:04:58    
	* 说明：   从n个数获取不重复的几个数
	* @param total
	* @param len
	* @return
	* int[]
	*/
	public static int[] getRandom(int total,int len){
		//根据需要自定义有序数组
		int[] ra=new int[total];
		for(int i=0;i<total;i++){
			ra[i]=i;
		}
		//无序排列，只重复len次
		if(len>total) len = total;
		for(int i=0;i<len;i++){
			Random rd=new Random();
			int temp1=rd.nextInt(total-i);
			int temp2=ra[total-1-i];//保存相对末尾的数据
			ra[total-1-i]=ra[temp1];//交换
			ra[temp1]=temp2;
		}
		//倒序截取
		int[] returnInt=new int[len];
		for(int i=0;i<len;i++){
			returnInt[i]=ra[total-1-i];
		}
		//返回
		return returnInt;
	}
	

	public static String getMultiplyStr(String num,String  multiply){
		String ret = num;
		if(StringUtil.isNotEmpty(multiply)){
			BigDecimal bMultiply = new BigDecimal(multiply);
			if(bMultiply.compareTo(new BigDecimal("1"))!=0){
				BigDecimal bNum = new BigDecimal(num);
				bNum = bMultiply.multiply(bNum);
				bNum = removeBigDecimalExZero(bNum);
				ret = String.valueOf(bNum);
			}
		}
		return ret;
	}
	
	public static BigDecimal removeBigDecimalExZero(BigDecimal bd){
		BigDecimal rbd = null;
		if(bd.compareTo(BigDecimal.ZERO)==0){
			rbd = new BigDecimal("0");
		}else{
			rbd = new BigDecimal(bd.stripTrailingZeros().toPlainString());
		}
		return rbd;
	}
	
	/**
	* 创建人： 王郎郎
	* 创建时间：2016-1-18 下午05:47:43    
	* 说明：   获取分
	* @param amount
	* @return
	* String
	*/
	public static String getAmountStr(BigDecimal amount){
		String amountStr = "";
		BigDecimal bigRate = new BigDecimal("100");
		amount = amount.multiply(bigRate).setScale(0,BigDecimal.ROUND_HALF_UP);;
		amountStr = String.valueOf(amount);
		return amountStr;
	}
	
	/**
	* 创建人： 王郎郎
	* 创建时间：2016-1-12 下午05:27:31    
	* 说明：   保留小位
	* @param b
	* void
	*/
	public static BigDecimal getScaleDecimal(BigDecimal b){
		return b.setScale(2,BigDecimal.ROUND_HALF_UP);
	}
	
	public static BigDecimal getScaleDecimal(BigDecimal b,int i){
		return b.setScale(i,BigDecimal.ROUND_HALF_UP);
	}
	
	/**
	* 创建人： 王郎郎
	* 创建时间：2016-1-9 下午04:40:50    
	* 说明：   是否关注
	* @param jo
	* @return
	* boolean
	*/
	public static boolean isSubscribe(JSONObject jo){
		boolean flag = false;
		int subscribe = 0;
		if(jo.containsKey("subscribe")) subscribe = jo.getInt("subscribe");
		if(subscribe==1) flag = true;
		return flag;
	}
	
	/**
	* 创建人： 王郎郎
	* 创建时间：2016-1-8 下午06:26:38    
	* 说明：  请求的数据是否正确
	* @param jo
	* @return
	* boolean
	*/
	public static boolean isJsonOk(JSONObject jo){
		boolean flag = false;
		if(!jo.containsKey("errcode")) flag = true;
		return flag;
	}
	
	public static boolean isMapOk(Map<String,Object> map){
		boolean flag = false;
		if(!map.containsKey("errcode")) flag = true;
		return flag;
	}
	
	/**
	* 创建人： 王郎郎
	* 创建时间：2016-1-8 下午02:43:17    
	* 说明：   将所有单引号换成双引号
	* @param str
	* void
	*/
	public static String toHalfQuotation(String str){
		return str.replaceAll("'", "\"");
	}
 
	
	/**
	 * 凌晨
	 * @param date
	 * @flag 0 返回yyyy-MM-dd 00:00:00日期<br>
	 *       1 返回yyyy-MM-dd 23:59:59日期
	 * @return
	 */
	public static Date weeHours(Object... aobj) {
		int flag = 0;
		Date date = new Date();
		int paraLen = aobj.length;
		if(paraLen==1){
			flag = Integer.parseInt(String.valueOf(aobj[0]));
		}else if(paraLen==2){
			flag = Integer.parseInt(String.valueOf(aobj[0]));
			date = (Date) aobj[1];
		}
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(date);
	    int hour = cal.get(Calendar.HOUR_OF_DAY);
	    int minute = cal.get(Calendar.MINUTE);
	    int second = cal.get(Calendar.SECOND);
	    //时分秒（毫秒数）
	    long millisecond = hour*60*60*1000 + minute*60*1000 + second*1000;
	    //凌晨00:00:00
	    cal.setTimeInMillis(cal.getTimeInMillis()-millisecond);
	      
	    if (flag == 0) {
	        return cal.getTime();
	    } else if (flag == 1) {
	        //凌晨23:59:59
	        cal.setTimeInMillis(cal.getTimeInMillis()+23*60*60*1000 + 59*60*1000 + 59*1000);
	    }
	    return cal.getTime();
	}
	
	
	/**
	* 创建人： 王郎郎
	* 创建时间：2015-11-8 下午11:11:45    
	* 说明：   将request里的参数封装到map中
	* @param request
	* @return
	* Map<String,String>
	*/
	public static Map<String,String> getMapFromReq(HttpServletRequest request){
		Map<String,String> map = new HashMap<String,String>();
		for(Enumeration paramEnums = request.getParameterNames(); paramEnums.hasMoreElements();){
            String paramName = (String)paramEnums.nextElement();
    		String paramval = request.getParameter(paramName);
    		if(StringUtil.isNotEmpty(paramval)) paramval = paramval.trim();
    		map.put(paramName, paramval);
        }
		return map;
	}
	

	/**
	* 创建人： 王郎郎
	* 创建时间：2015-11-8 下午11:11:45    
	* 说明：   将request里的参数封装到map中
	* @param request
	* @return
	* Map<String,String>
	*/
	public static Map<String,String> getMapFromReq(HttpServletRequest request,String exceptParam){
		Map<String,String> map = new HashMap<String,String>();
		for(Enumeration paramEnums = request.getParameterNames(); paramEnums.hasMoreElements();){
            String paramName = (String)paramEnums.nextElement();
            if(StringUtils.isEmpty(exceptParam)||(StringUtils.isNotEmpty(exceptParam)&&!exceptParam.contains(paramName))){
        		String paramval = request.getParameter(paramName);
        		if(StringUtil.isNotEmpty(paramval)) paramval = paramval.trim();
        		map.put(paramName, paramval);
            }
        }
		return map;
	}
	
	

	
	
	/**
	* 创建人： 王郎郎
	* 创建时间：2015-11-9 下午08:15:26    
	* 说明：   远程数据返回
	* @param response
	* @param json
	* void
	*/
	
	
	//调用invoke方法
	public static Object invokeMethod(Object obj,String methodName,Object... aobj){
		Object o = null;
		try {
			int paraLen = aobj.length;
			if(paraLen>0){
				Class<?>[] paraClass = new Class<?>[paraLen];
				for(int i=0;i<paraLen;i++){
					paraClass[i] = aobj[i].getClass();
				}
				Method method = obj.getClass().getMethod(methodName, paraClass);
				o = method.invoke(obj, aobj);
			}else{
				Method method = obj.getClass().getMethod(methodName);
				o = method.invoke(obj);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return o;
	}
	
	
	/**     
	* 创建人：陈永培   
	* 创建时间：2016-9-27 下午12:33:37
	* 功能说明：判断列表是否不空    
	*/
	public static boolean isNotNull(List list){
		boolean flag=false; 
		if(list!=null&&list.size()>0)
			flag=true;
		return flag;
	}
	
	public static boolean isNull(List list){
		boolean flag=false; 
		if(list==null||list.size()==0)
			flag=true;
		return flag;
	}
	
	public static boolean isNotNull(Map map){
		boolean flag=false; 
		if(map!=null&&map.size()>0)
			flag=true;
		return flag;
	}
	
	public static boolean isNull(Map map){
		boolean flag=false; 
		if(map==null||map.size()==0)
			flag=true;
		return flag;
	}
}
	

package com.ucg.util.jsp;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.ucg.base.framework.core.common.entity.IdEntity;
import com.ucg.base.framework.core.util.StringUtil;
import com.ucg.base.framework.mybatis.entity.Page;

/**
 * @author llking
 *
 */
public class JspBean {

	
	/**   
	*    
	* 项目名称：UCG_OSS     
	* 创建人：王郎郎
	* 创建时间：2015-5-29 上午11:13:29   
	* 功能说明：获取JSP中的list对象
	* 修改人：王郎郎 
	* 修改时间：2015-5-29 上午11:13:29   
	* 修改备注：   
	* @version    
	*    
	*/ 
	public static List getList(String listName,Class<?> c,HttpServletRequest request){
		List<Object> list = new ArrayList<Object>();
		Field[] classFields=c.getDeclaredFields();//获取实体中的所有属性
		String fieldName = null;
		String fieldValue = null;
		boolean flag = false;
		try {
			for(int i=0;;i++){
				Object obj = c.newInstance();
				flag = false;
				for(Field f:classFields){
					fieldName = f.getName();
					fieldValue = request.getParameter(listName+"["+i+"]/"+fieldName);
					if(fieldValue!=null){
						flag = true;
						invokeSetMethod(obj,fieldName,fieldValue);
					}
				}
				if(flag) list.add(obj);
				else break;
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return list;
		
	}
		
	//将字符串的首字母大写
	public static String getMethodName(String fieldName){
		return fieldName.replaceFirst(fieldName.substring(0, 1),fieldName.substring(0, 1).toUpperCase());

	}  
	
	//调用set方法
	public static void invokeSetMethod(Object obj,String fieldName,String fieldValue){
		try {
			Method method=null;
			Field field = null;
			if("id".equals(fieldName))
			field=IdEntity.class.getDeclaredField(fieldName);
			else field=obj.getClass().getDeclaredField(fieldName);
			Class<?> fieldClass = field.getType();
			method=obj.getClass().getMethod("set"+getMethodName(fieldName),fieldClass);
			Object fvObj = getFieldValue(fieldClass,fieldValue);
			method.invoke(obj, fvObj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//判断某个实例是否包含fieldName属性
	public static boolean keyContains(Class<?> c,String fName){
		boolean flag = false;
		Field[] classFields=c.getDeclaredFields();//获取实体中的所有属性
		String fieldName = null;
		for(Field f:classFields){
			fieldName = f.getName();
			if(fName.equals(fieldName)){
				flag = true;
				break;
			}
		}
		return flag;
	}
	
	//调用set方法
	public static Object getFieldValue(Class<?> c,String fieldValue){
		Object obj = null;
		try {
			if(fieldValue.equals("")){
				if(c.getName().equals(String.class.getName())) obj = fieldValue;
			}else{
				if(c.getName().equals(String.class.getName())){
					obj = fieldValue;
				}else if(c.getName().equals(Integer.class.getName())){
					obj = Integer.parseInt(fieldValue);
				}else if(c.getName().equals(Double.class.getName())){
					obj = Double.parseDouble(fieldValue);
				}else if(c.getName().equals(BigDecimal.class.getName())){
					obj = new BigDecimal(fieldValue);
				}else if(c.getName().equals(Date.class.getName())){
					DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
					Date date = format.parse(fieldValue);
					obj = date;
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	/**   
	*    
	* 项目名称：UCG_OSS     
	* 创建人：王郎郎
	* 创建时间：2015-6-28 上午06:22:54   
	* 功能说明：获取hql动态数据 entityName:datagrid实体名称 gridField：gridField字段 addOrder:是否添加排序信息
	* 修改人：王郎郎 
	* 修改时间：2015-6-28 上午06:22:54   
	* 修改备注：   
	* @version    
	 * @throws NoSuchFieldException 
	 * @throws SecurityException 
	*    
	*/ 
	public static Map<String,Object> getHqlAndObj(Class<?> c,Page<?> page,Boolean addOrder){
		Map<String, Object> map = new HashMap<String, Object>();
		
		int count = 0;
		int length = 0;
		StringBuffer hql = new StringBuffer();
		hql.append("from "+c.getName()+" where 1=1 ");
		
		Map<String,Object> param = (Map<String, Object>) page.getParameter();
		Field[] classFields=c.getDeclaredFields();//获取实体中的所有属性
		String fieldName = null;
		String fieldValue = null;
		for(Field f:classFields){
			fieldName = f.getName();
			if("id".equals(fieldName)) continue;
			if(param.containsKey(fieldName)){
				if(StringUtil.isNotEmpty(param.get(fieldName))) ++length;
			}else{
				if(param.containsKey(fieldName+"_begin")){
					if(StringUtil.isNotEmpty(param.get(fieldName+"_begin"))) ++length;
				}
				if(param.containsKey(fieldName+"_end")){
					if(StringUtil.isNotEmpty(param.get(fieldName+"_end"))) ++length;
				}
			}
		}
		
		count = length;
		Object[] obj = new Object[length];
		Field field = null;
		try {
			for(Field f:classFields){
				fieldName = f.getName();
				if("id".equals(fieldName)) continue;
				field=c.getDeclaredField(fieldName);
				Class<?> fieldClass = field.getType();
				if(param.containsKey(fieldName)){
					if(StringUtil.isNotEmpty(param.get(fieldName))){
						fieldValue = (String) param.get(fieldName);
						if(fieldClass.getName().equals(String.class.getName())){
							hql.append("and "+fieldName+" like ? ");
							obj[count-length] = "%"+param.get(fieldName)+"%";
						}else{
							hql.append("and "+fieldName+" = ? ");
							Object o = getFieldValue(fieldClass,fieldValue);
							obj[count-length] = o;
						}
						--length;
					}
				}else{
					if(param.containsKey(fieldName+"_begin")){
						if(StringUtil.isNotEmpty(param.get(fieldName+"_begin"))){
							hql.append("and "+fieldName+" >= ? ");
							fieldValue = (String) param.get(fieldName+"_begin");
							Object o = getFieldValue(fieldClass,fieldValue);
							obj[count-length] = o;
							--length;
						}
					}
					if(param.containsKey(fieldName+"_end")){
						if(StringUtil.isNotEmpty(param.get(fieldName+"_end"))){
							hql.append("and "+fieldName+" <= ? ");
							fieldValue = (String) param.get(fieldName+"_end");
							Object o = getFieldValue(fieldClass,fieldValue);
							obj[count-length] = o;
							--length;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(addOrder){
			String orderBy = (String)param.get("sort");
			String order = (String)param.get("order");
			hql.append("order by "+orderBy+" "+order);
		}
		
		map.put("hql", hql.toString());
		map.put("obj", obj);
		
		return map;
	}  
	
}

package com.ucg.util.bean;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ucg.base.framework.core.util.BeanUtils;
import com.ucg.base.framework.core.util.StringUtil;
import com.ucg.base.framework.mybatis.entity.Page;

/**
 * @author llking
 *
 */
 /**    
* 项目名称：UCG_OSS     
* 创建人：王郎郎
* 创建时间：2016-3-26 上午12:20:27   
* 功能说明：
* 修改人： 王郎郎
* 修改时间：2016-3-26 上午12:20:27    
* 修改备注：   
* @version       
*/
public class BeanUtil {

	
/*	public static void main(String[] args) {
		Class c = TestVo.class;
		List<Map<String,Object>> srcs = new ArrayList<Map<String,Object>>();
		for(int i=0;i<5;i++){
			Map<String,Object> map = new HashMap<String, Object>();
			String id = "id" + i;
			String name = "name" + i;
			int count = i;
			BigDecimal bigdec = new BigDecimal(i);
			Date now = new Date();
			map.put("id", id);
			map.put("name", name);
			map.put("count", count);
			map.put("bigdec", bigdec);
			map.put("now", now);
			srcs.add(map);
		}
		List<TestVo> tos = BeanUtil.jdbcToBean(c, srcs);
		
	}*/
	
	/**     
	* 创建人：陈永培   
	* 创建时间：2016-7-7 下午4:21:27
	* 功能说明：    jdbc list转bean
	*/
	public static <T>  List<T> jdbcToBean(Class<T> c,List<Map<String,Object>> srcs){
		List<T> tos = new ArrayList<T>();
		if(BeanUtils.isNotEmpty(srcs)){
			for(Map<String,Object> map:srcs){
				T t = null;
				try {
					t = c.newInstance();
				} catch (Exception e) {
					e.printStackTrace();
				}
				for (Map.Entry<String, Object> entry : map.entrySet()) {
					String key = entry.getKey();
					Object value = entry.getValue();
					String fieldValue = String.valueOf(value);
					invokeSetMethod(t,key,fieldValue);
				}
				tos.add(t);
			}
		}
		return tos;
	}
	
	//调用set方法
	public static void invokeSetMethod(Object obj,String fieldName,String fieldValue){
		try {
			Method method=null;
			Field field = null;
			field=obj.getClass().getDeclaredField(fieldName);
			Class<?> fieldClass = field.getType();
			method=obj.getClass().getMethod("set"+getMethodName(fieldName),fieldClass);
			Object fvObj = getFieldValue(fieldClass,fieldValue);
			method.invoke(obj, fvObj);
		} catch (Exception e) {
			//e.printStackTrace();
			System.out.println("调用set方法报错了============>"+e.getMessage());
		}
	}
	
		/**     
		* 创建人：陈永培   
		* 创建时间：2016-11-8 上午8:50:03
		* 功能说明： 调用get方法   
		*/
	public static Object invokeGetMethod(Object obj,String fieldName){
		Object value=null;
		try {
			value = (Object) BeanUtil.invokeMethod(obj, "get"+getMethodName(fieldName));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}
	
	
	/**
	* 创建人： 王郎郎
	* 创建时间：2016-3-24 上午08:39:16    
	* 说明：   执行方法
	* @param obj
	* @param methodName
	* @param aobj
	* @return
	* Object
	*/
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
	
	//将字符串的首字母大写
	public static String getMethodName(String fieldName){
		return fieldName.replaceFirst(fieldName.substring(0, 1),fieldName.substring(0, 1).toUpperCase());
	}  
	
	/**
	* 创建人： 王郎郎
	* 创建时间：2016-3-24 上午09:10:41    
	* 说明：   执行set方法
	* @param obj
	* @param fieldName
	* @param fieldValue
	* void
	*/
	public static void invokeSetMethod(Object obj,String fieldName,String fieldValue,BeanField bf){
		try {
			Method method=null;
			Field field = null;
			String fields = bf.getFields();
			if(fields.contains(fieldName+",")){
				field=obj.getClass().getDeclaredField(fieldName);
			}else{
				String pfields = bf.getPfields();
				if(StringUtil.isNotEmpty(pfields)){
					String tmpName = fieldName+",";
					if(pfields.contains(tmpName)){
						Class<?> pc = bf.getPc();
						field=pc.getDeclaredField(fieldName);
					}else{
						String ppfields = bf.getPpfields();
						if(StringUtil.isNotEmpty(ppfields)){
							if(ppfields.contains(tmpName)){
								Class<?> ppc = bf.getPpc();
								field=ppc.getDeclaredField(fieldName);
							}else{
								String pppfields = bf.getPpfields();
								if(StringUtil.isNotEmpty(pppfields)){
									if(pppfields.contains(tmpName)){
										Class<?> pppc = bf.getPpc();
										field=pppc.getDeclaredField(fieldName);
									}
								}
							}
						}
					}
				}
			}
			if(field!=null){
				Class<?> fieldClass = field.getType();
				method=obj.getClass().getMethod("set"+getMethodName(fieldName),fieldClass);
				Object fvObj = getFieldValue(fieldClass,fieldValue);
				method.invoke(obj, fvObj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	* 创建人： 王郎郎
	* 创建时间：2016-3-24 上午08:45:17    
	* 说明：   获取sql查询的map对象
	* @param objlist
	* void
	*/
	public static <T> List<T> getSqlList(List<Object[]> objlist,Class<T> c,HqlParam hqlParam){
		List<T> list = null;
		String field = hqlParam.getField();
		BeanField bf = hqlParam.getBeanField();
		boolean isOne = false;
		if(!field.contains(",")) isOne = true;
		try {
			if(objlist!=null&&objlist.size()>0) {
				list = new ArrayList<T>();
				String[] fields = field.split(",");
				for(int i=0;i<objlist.size();i++){
					if(isOne){
						Object o = objlist.get(i);
						T obj = c.newInstance();
						if(o!=null) {
							String fieldName = field;
							String fieldValue = null;
							fieldValue = String.valueOf(o);
							invokeSetMethod(obj, fieldName, fieldValue,bf);
						}
						list.add(obj);
					}else{
						Object[] o = objlist.get(i);
						T obj = c.newInstance();
						for(int j=0;j<fields.length;j++){
							if(o[j]!=null){
								String fieldName = fields[j];
								String fieldValue = null;
								fieldValue = String.valueOf(o[j]);
								invokeSetMethod(obj, fieldName, fieldValue,bf);
							}
						}
						list.add(obj);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	private static final String except = "serialVersionUID";
	
	/**
	* 创建人： 王郎郎
	* 创建时间：2016-3-24 上午08:38:17    
	* 说明：   获取类中所有字段
	* @param <T>
	* @param c
	* @return
	* String
	*/
	public static <T> BeanField getAllField(Class<T> c) {
		BeanField bf = new BeanField();
		String fieldstr = "";
		
		Field[] fields = c.getDeclaredFields();
		String fd = "";
		for(int i=0;i<fields.length;i++){
			String fieldName = fields[i].getName();
			if(except.contains(fieldName)) continue;
			fieldstr += fieldName + ",";
			fd += fieldName + ",";
		}
		bf.setFields(fd);
		Class<?> p = c.getSuperclass();
		if(p!=null){
			Field[] pfields = p.getDeclaredFields();
			String pf = "";
			for(Field f:pfields){
				String fieldName = f.getName();
				if(except.contains(fieldName)) continue;
				fieldstr += fieldName + ",";
				pf += fieldName + ",";
			}
			bf.setPfields(pf);
			bf.setPc(p);
			Class<?> pp = p.getSuperclass();
			if(pp!=null){
				Field[] ppfields = pp.getDeclaredFields();
				String ppf = "";
				for(Field f:ppfields){
					String fieldName = f.getName();
					if(except.contains(fieldName)) continue;
					fieldstr += fieldName + ",";
					ppf += fieldName + ",";
				}
				bf.setPpfields(ppf);
				bf.setPpc(pp);
				Class<?> ppp = pp.getSuperclass();
				if(ppp!=null){
					Field[] pppfields = ppp.getDeclaredFields();
					String pppf = "";
					for(Field f:pppfields){
						String fieldName = f.getName();
						if(except.contains(fieldName)) continue;
						fieldstr += fieldName + ",";
						pppf += fieldName + ",";
					}
					bf.setPppfields(ppf);
					bf.setPppc(pp);
				}
			}
		}
		
		if(fieldstr.contains(",")){
			int last = fieldstr.lastIndexOf(',');
			fieldstr = fieldstr.substring(0, last);
		}
		bf.setAllfield(fieldstr);
		return bf;
	}
	
	/**
	* 创建人： 王郎郎
	* 创建时间：2016-3-24 上午08:38:31    
	* 说明：   类中是否包含某字段
	* @param allField
	* @param f
	* @return
	* boolean
	*/
	public static boolean hasColumnLabel(String allField,String f){
		boolean flag = false;
		allField = allField+",";
		if(allField.contains(f+",")){
			flag = true;
		}
		return flag;
	}
	
	 
	/**
	* 创建人： 王郎郎
	* 创建时间：2016-3-24 上午08:38:48    
	* 说明：   获取要查询字段
	* @param field
	* @param but
	* @param allField
	* @return
	* String
	*/
	public static String getField(String field,String but,String allField){
		String fieldRt = "";
		
		if(StringUtils.isEmpty(field)&&StringUtils.isEmpty(but)){
			fieldRt = allField;
        }else{
        	boolean hasbut = false;
        	if(StringUtils.isNotEmpty(but)) hasbut = true;
        	if(StringUtils.isEmpty(field)){
        		if(hasbut){
        			field = allField;
        		} 
        	}
        	
        	if(hasbut) but = but + ",";
        	
        	String[] fields = field.split(",");
        	int i=0;
        	for(String f:fields){
        		if(StringUtil.isNotEmpty(f)){
        			if(!(hasbut && but.contains(f+","))){
        				if (hasColumnLabel(allField,f)) {
        					if(i==0) fieldRt += f;
        					else fieldRt += ","+f;
        					i++;
        				}
        			}
        		}
        	}
        }
    	
		if(StringUtil.isEmpty(fieldRt)){
			fieldRt = "id";
		}
		return fieldRt;
	}
	
	/**
	* 创建人： 王郎郎
	* 创建时间：2016-3-24 上午08:38:57    
	* 说明：   是否有某字段
	* @param para
	* @param fieldName
	* @param ext
	* @return
	* boolean
	*/
	public static boolean isContais(Map<String,String> para,String fieldName,String ext){
		boolean flag = false;
		if(para.containsKey(fieldName+ext)){
			if(StringUtil.isNotEmpty(para.get(fieldName+ext))) flag = true;
		}
		return flag;
	}
	
	/**
	* 创建人： 王郎郎
	* 创建时间：2016-3-26 上午03:47:19    
	* 说明：   获取所有字段
	* @param p
	* @param ms1
	* @return
	* Field[]
	*/
	public static Field[] contactgetFields(Class<?> p,Field[] ms1){
		if(p==null){
			return ms1;
		}else{
			if(Object.class.getName().equals(p.getName())){
				return ms1;
			}else{
				Field[] ms2 = p.getDeclaredFields();
				int msLen1=ms1.length;
				int msLen2=ms2.length;
				Field[] ms = Arrays.copyOf(ms1,msLen1+ msLen2);//扩容
				System.arraycopy(ms2,0, ms, msLen1,msLen2 );
				Class<?> c = p.getSuperclass();
				if(c==null){
					return ms;
				}else{
					return contactgetFields(c,ms);
				}
			}
		}
	}
	
	/**
	* 创建人： 王郎郎
	* 创建时间：2016-3-26 上午03:47:08    
	* 说明：   获取所有字段
	* @param c
	* @return
	* Field[]
	*/
	public static Field[] getFields(Class<?> c){
		Class<?> p = c.getSuperclass();
		Field[] ms = c.getDeclaredFields();
		if(p==null){
			return ms;
		}else{
			return contactgetFields(p,ms);
		}
	}
	
	/**
	* 创建人： 王郎郎
	* 创建时间：2016-3-23 下午08:53:15    
	* 说明：   获取hql查询的语句及值
	* @param <T>
	* @param pageObj
	* @param addOrder
	* @return
	* @throws Exception
	* HqlParam
	*/
	@SuppressWarnings("unchecked")
	public static <T> HqlParam getHqlObj(Page<T> pageObj,Class<T> c){
		HqlParam hqlParam = new HqlParam();
		BeanField bf = getAllField(c);
		String fields = bf.getAllfield();
		
		Map<String,String> para = (Map<String, String>) pageObj.getParameter();
		
		String sort = "";
		String order = "";
		
		String msort = para.get("msort");
		if(StringUtils.isNotEmpty(msort)){
			sort = msort;
			order = para.get("morder");
		}else{
			sort = para.get("sort");
			order = para.get("order");
		}
		
		
		if(StringUtils.isEmpty(sort))
		    sort = "createTime";
		if(StringUtils.isEmpty(order))
		    order = "desc";
		String but = para.get("but");
		String field = para.get("field");
		field = getField(field,but,fields);
		
		if(StringUtil.isEmpty(para.get("field"))) para.put("field", field);
		
		String tail = " order by ";
		if(sort.contains(",")){
			String[] sorts = sort.split(",");
			String[] orders = order.split(",");
			for(int i=0;i<sorts.length;i++){
				String sName = sorts[i];
				if(sName.contains("-")){
					String[] sNames = sName.split("-");
					sName = "instr('"+sNames[1]+"',"+sNames[0]+")";
				}
				if(i==0) tail += sName+" "+orders[i];
				else tail += ","+sName+" "+orders[i];
			}
		}else{
			String sName = sort;
			if(sName.contains("-")){
				String[] sNames = sName.split("-");
				sName = "instr('"+sNames[1]+"',"+sNames[0]+")";
			}
			tail += sName+" "+order;
		}
		
		
		StringBuffer sqlw = new StringBuffer();
		List<Object> objs = new ArrayList<Object>();
		String fieldValue = null;
		
		Field[] classFields = getFields(c);
		for(Field f:classFields){
			if(f==null) continue;
			String fieldName = f.getName();
			Class<?> fieldClass = f.getType();
			if(isContais(para, fieldName, "")){
				fieldValue = para.get(fieldName);
				String[] fieldValues = fieldValue.split(",");
				if(fieldValues.length==1){
					if(fieldClass.getName().equals(String.class.getName())){
						sqlw.append("and ");
						sqlw.append(fieldName);
						sqlw.append(" = ? ");
						objs.add(fieldValue);
					}else{
						sqlw.append("and ");
						sqlw.append(fieldName);
						sqlw.append(" = ? ");
						Object o1 = getFieldValue(fieldClass,fieldValue);
						objs.add(o1);
					}
				}else{
					for(int i=0;i<fieldValues.length;i++){
						if(i==0){
							fieldValue = fieldValues[i];
							if(fieldClass.getName().equals(String.class.getName())){
								sqlw.append("and (");
								sqlw.append(fieldName);
								sqlw.append(" = ? ");
								objs.add(fieldValue);
							}else{
								sqlw.append("and (");
								sqlw.append(fieldName);
								sqlw.append(" = ? ");
								Object o1 = getFieldValue(fieldClass,fieldValue);
								objs.add(o1);
							}
						}else if(i==fieldValues.length-1){
							fieldValue = fieldValues[i];
							if(fieldClass.getName().equals(String.class.getName())){
								sqlw.append("or ");
								sqlw.append(fieldName);
								sqlw.append(" = ?) ");
								objs.add(fieldValue);
							}else{
								sqlw.append("or ");
								sqlw.append(fieldName);
								sqlw.append(" = ?) ");
								Object o1 = getFieldValue(fieldClass,fieldValue);
								objs.add(o1);
							}
						}else{
							fieldValue = fieldValues[i];
							if(fieldClass.getName().equals(String.class.getName())){
								sqlw.append("or ");
								sqlw.append(fieldName);
								sqlw.append(" = ? ");
								objs.add(fieldValue);
							}else{
								sqlw.append("or ");
								sqlw.append(fieldName);
								sqlw.append(" = ? ");
								Object o1 = getFieldValue(fieldClass,fieldValue);
								objs.add(o1);
							}
						}
					}
				}
			}
			
			if(isContais(para, fieldName, "_ne")){
				fieldValue = para.get(fieldName+"_ne");
				String[] fieldValues = fieldValue.split(",");
				for(int i=0;i<fieldValues.length;i++){
					sqlw.append("and ");
					sqlw.append(fieldName);
					sqlw.append(" != ? ");
					Object o1 = getFieldValue(fieldClass,fieldValue);
					objs.add(o1);
				}
			}
			
			if(isContais(para, fieldName, "_lk")){
				fieldValue = para.get(fieldName+"_lk");
				String[] fieldValues = fieldValue.split(",");
				if(fieldValues.length==1){
					sqlw.append("and ");
					sqlw.append(fieldName);
					sqlw.append(" like ? ");
					Object o1 = "%"+getFieldValue(fieldClass,fieldValue)+"%";
					objs.add(o1);
				}else{
					for(int i=0;i<fieldValues.length;i++){
						if(i==0){
							fieldValue = fieldValues[i];
							sqlw.append("and ");
							sqlw.append(fieldName);
							sqlw.append(" like ? ");
							Object o1 = "%"+getFieldValue(fieldClass,fieldValue)+"%";
							objs.add(o1);
						}else if(i==fieldValues.length-1){
							fieldValue = fieldValues[i];
							sqlw.append("or ");
							sqlw.append(fieldName);
							sqlw.append(" like ? ");
							Object o1 = "%"+getFieldValue(fieldClass,fieldValue)+"%";
							objs.add(o1);
						}else{
							fieldValue = fieldValues[i];
							sqlw.append("or ");
							sqlw.append(fieldName);
							sqlw.append(" like ? ");
							Object o1 = "%"+getFieldValue(fieldClass,fieldValue)+"%";
							objs.add(o1);
						}
					}
				}
			}
			
			boolean isGtBefore = false,isGeBefore = false,isLtAfter = false,isLeAfter = false,isBefore=false,isAfter=false;
			String g = "",l = "",beforeValue="",afterValue="";
			if(isContais(para, fieldName, "_gt")){
				g = " > ? ";
				beforeValue = para.get(fieldName+"_gt");
				isGtBefore = true;
			}else if(isContais(para, fieldName, "_begin")){
				beforeValue = para.get(fieldName+"_begin");
				g = " >= ? ";
				isGeBefore = true;
			} 
			if(isContais(para, fieldName, "_lt")){
				afterValue = para.get(fieldName+"_lt");
				l = " < ? ";
				isLtAfter = true;
			}else if(isContais(para, fieldName, "_end")){
				afterValue = para.get(fieldName+"_end");
				l = " <= ? ";
				isLeAfter = true;
			}
			
			if(isGtBefore||isGeBefore) isBefore = true;
			if(isLtAfter||isLeAfter) isAfter = true;
			
			
			if(isBefore){
				sqlw.append("and ");
				sqlw.append(fieldName);
				sqlw.append(g);
				Object o1 = getFieldValue(fieldClass,beforeValue);
				objs.add(o1);
			}
			
			if(isAfter){
				sqlw.append("and ");
				sqlw.append(fieldName);
				sqlw.append(l);
				if(fieldClass.getName().equals(Date.class.getName())){
					if (afterValue.length() == 10) {
						// 对于"yyyy-MM-dd"格式日期，因时间默认为0，故此添加" 23:23:59"并使用time解析，以方便查询日期时间数据
						afterValue += " 23:23:59";
					}
					 
				}
				Object o1 = getFieldValue(fieldClass,afterValue);
				objs.add(o1);
			}
		}
		
		String tbName = c.getName();
		tbName = tbName + " tbName";
		
		String wStr = "";
		Object[] params = null;
		int objlen = objs.size();
		if(objlen>0){
			wStr = (" where " +sqlw.toString()).replace("where and", "where");
			params = new Object[objlen];
			for(int i=0;i<objlen;i++){
				params[i] = objs.get(i);
			}
		}else{
			params = new Object[0];
		}
		
		String sql = "select " + field + " from " + tbName + wStr + tail;
		hqlParam.setHql(sql);
		hqlParam.setParam(params);
		hqlParam.setField(field);
		hqlParam.setBeanField(bf);
		
		String cql = "";
		
		if(para.containsKey("nt")){
			pageObj.autoCount(false);
		}else{
			pageObj.autoCount(true);
			cql = "select count(tbName) from " + tbName + wStr;
			hqlParam.setCql(cql);
		}
		return hqlParam;
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
			if(StringUtils.isEmpty(orderBy)) orderBy = "updateTime";
			if(StringUtils.isEmpty(order)) order = "desc";
			hql.append("order by "+orderBy+" "+order);
		}
		
		map.put("hql", hql.toString());
		map.put("obj", obj);
		
		return map;
	}  
	
	/**
	* 创建人： 王郎郎
	* 创建时间：2016-3-26 上午12:21:14    
	* 说明：   将字段转成数组
	* @param fieldValues
	* @param c
	* @param fieldValue
	* @return
	* Object[]
	*/
	public static Object[] getFieldObj(String[] fieldValues, Class<?> c){
		int len = fieldValues.length;
		Object[] ret = new Object[len];
		for(int i=0;i<len;i++){
			ret[i] = getFieldValue(c,fieldValues[i]);
		}
		return ret;
	}
	
	/**   
	*    
	* 项目名称：UCG_OSS     
	* 创建人：王郎郎
	* 创建时间：2015-6-28 上午07:08:11   
	* 功能说明：获取字段值
	* 修改人：王郎郎 
	* 修改时间：2015-6-28 上午07:08:11   
	* 修改备注：   
	* @version    
	*    
	*/ 
	public static Object getFieldValue(Class<?> c,String fieldValue){
		Object obj = null;
		try {
			if("".equals(fieldValue)){
				if(c.getName().equals(String.class.getName())) obj = fieldValue;
			}else{
				if(fieldValue!=null){
					if(c.getName().equals(String.class.getName())){
						obj = fieldValue;
					}else if(c.getName().equals(Integer.class.getName())){
						obj = Integer.parseInt(fieldValue);
					}else if(c.getName().equals(Double.class.getName())){
						obj = Double.parseDouble(fieldValue);
					}else if(c.getName().equals(BigDecimal.class.getName())){
						obj = new BigDecimal(fieldValue);
					}else if(c.getName().equals(Date.class.getName())){
						Date date;
						if(fieldValue.contains("CST")){
							SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy",Locale.US);
							date=sdf.parse(fieldValue);
						}else{
							date = org.apache.commons.lang3.time.DateUtils.parseDate(fieldValue, "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss" ,"yyyy-MM-dd HH:mm:ss.S");
						}
						obj = date;
					}else if(c.getName().equals(Short.class.getName())){
						obj = Short.valueOf(fieldValue);
					}else if(c.getName().equals(Float.class.getName())){
						obj = new Float(fieldValue);
					}
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	
	public static  void invokeSet(Object obj ,String filed,String value){
		BeanField bf = BeanUtil.getAllField(obj.getClass());
		String fields = bf.getAllfield();
		if(BeanUtil.hasColumnLabel(fields, filed)){
			BeanUtil.invokeSetMethod(obj, filed, value);
		}
	}
	
}

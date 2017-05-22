package com.ucg.util.dbcompare;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


 /**    
* 表信息对比
* @version       
*/
public class DataCompare {

	private static Connection con1;
	private static Connection con2;
	private static String pk;	//表主键
	private static Map<String,FieldInfo> columns;	//字段信息
	
	//获取源数据库连接
	public static Connection getConnection1() throws Exception{
		if(con1 == null || con1.isClosed()){
			con1 =  DriverManager.getConnection(serverSrc.get("url") , serverSrc.get("username") ,  serverSrc.get("password"));
		}
		return con1;
	}
	
	//获取目标数据库连接
	public static Connection getConnection2() throws Exception{
		if(con2 == null || con2.isClosed()){
			con2 =  DriverManager.getConnection(serverTo.get("url") , serverTo.get("username") ,  serverTo.get("password"));
		}
		return con2;
	}
	
	//表字段信息
	public static final class FieldInfo{
		private String fieldName;
		private String fieldType;
		
		public String getFieldName() {
			return fieldName;
		}
		public void setFieldName(String fieldName) {
			this.fieldName = fieldName;
		}
		public String getFieldType() {
			return fieldType;
		}
		public void setFieldType(String fieldType) {
			this.fieldType = fieldType;
		}
	}
	
	//获取主键
	public static void initPks(String tableName){
		try {
			pk = "";
			DatabaseMetaData dbMeta = con1.getMetaData();
			ResultSet pkRSet = dbMeta.getPrimaryKeys(null, null, tableName); 
			while(pkRSet.next()) {
				String name = (String) pkRSet.getObject(4);
				pk += name + ",";
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}
	
	//初始化表字段
	public static void initColumnMap(ResultSet rs){
		columns = new LinkedHashMap<String, FieldInfo>();
		try {
			ResultSetMetaData rsMeta = rs.getMetaData();
			for(int i = 1; i < rsMeta.getColumnCount()+1; i++){
				String name = rsMeta.getColumnName(i);
				String type = rsMeta.getColumnTypeName(i);
				FieldInfo f = new FieldInfo();
				f.setFieldName(name);
				f.setFieldType(type);
				columns.put(name, f);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	//字节数转16进制
	public static String toHexString(byte[] b){
		StringBuffer buffer = new StringBuffer();
	    for (int i = 0; i < b.length; i++) { 
	      String hex = Integer.toHexString(b[i] & 0xFF); 
	      if (hex.length() == 1) { 
	        hex = '0' + hex; 
	      }
	      buffer.append(hex);
	    }
	    return buffer.toString().toUpperCase();
    } 
	
	//获取map形式的结果数据
	public static Map<String, Map<String,Object>> getResultMap(ResultSet rs){
		Map<String, Map<String,Object>> ret = new HashMap<String, Map<String,Object>>();
		try {
			while(rs.next()){
				String id = "";
				if(pk.indexOf(',') != -1){
					String[] pks = pk.split(",");
					for(int i=0;i<pks.length;i++){
						if(i==0){
							id += rs.getString(pks[i]);
						}else{
							id += ","+rs.getString(pks[i]);
						}
					}
				}else{
					id = rs.getString(pk);
				}
				Map<String,Object> map1 = new LinkedHashMap<String, Object>();
				int i = 1;
				for(Map.Entry<String,FieldInfo> entry : columns.entrySet()){
					FieldInfo f = entry.getValue();
					String type = f.getFieldType();
					Object v = rs.getObject(i);
					if(isBlob(type)){
						v = rs.getObject(i);
						if(v != null){
							v = toHexString((byte[]) v);
						}
					}
					map1.put(entry.getKey(), v);
					i++;
				}
				ret.put(id, map1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	
	
	//获取条件sql
	public static String conSql(String id){
		String sql = "";
		if(pk.indexOf(',') != -1){
			String[] pks = pk.split(",");
			String[] ids = id.split(",");
			for(int i=0;i<pks.length;i++){
				if(i==0){
					sql += pks[i]+"="+"'"+ids[i]+"'";
				}else{
					sql += " and "+pks[i]+"="+"'"+ids[i]+"'";
				}
			}
			sql += ";";
		}else{
			sql += pk+"="+"'"+id+"';";
		}
		return sql;
	}
	
	//获取删除sql
	public static String delSql(String tableName,String id){
		String sql = "delete from "+tableName+" where "+conSql(id);
		return sql;
	}
	
	//将特殊字符转化
	public static String transSpecial(String valueStr){
		Pattern p1 = Pattern.compile("\n");
		Matcher m1 = p1.matcher(valueStr);
		Pattern p2 = Pattern.compile("\r\n");
		Matcher m2 = p2.matcher(valueStr);
		if(m1.find()){
			valueStr = m1.replaceAll("\\\\n");
		}else if(m2.find()){
			valueStr = m2.replaceAll("\\\\r\\\\n");
		}
		return valueStr;
	}
	
	//判断是否为大字段类型
	public static boolean isBlob(String type){
		boolean flag = false;
		if(type.equals("BLOB")||type.equals("LONGBLOB")){
			flag = true;
		}
		return flag;
	}
	
	
	//获取修改sql
	public static String upSql(String tableName,String id,Map<String,Object> src,Map<String,Object> to){
		StringBuffer sql = new StringBuffer();
		sql.append("update ");
		sql.append(tableName);
		sql.append(" set ");
		
		StringBuffer inn = new StringBuffer();
		for(Map.Entry<String, Object> srce:src.entrySet()){
			String key = srce.getKey();
			Object srcv = srce.getValue();
			Object tov = to.get(key);
			if(!String.valueOf(srcv).equals(String.valueOf(tov))){
				if(tov != null){
					FieldInfo f = columns.get(key);
					String type = f.getFieldType();
					inn.append(key);
					if(isBlob(type)){
						inn.append("=0x");
						inn.append(tov.toString());
						inn.append(",");
					}else{
						String toValue = transSpecial(tov.toString());
						inn.append("='");
						inn.append(toValue);
						inn.append("',");
					}
					
				}else{
					inn.append(key);
					inn.append("=null,");
				}
			}
		}
		String inner = inn.toString();
		inner = inner.substring(0,inner.length()-1);
		sql.append(inner);
		sql.append(" where ");
		sql.append(conSql(id));
		return sql.toString();
	}
	
	//获取新增sql
	public static String addSql(String tableName,Map<String,Object> to){
		StringBuffer sql = new StringBuffer();
		sql.append("insert into ");
		sql.append(tableName);
		sql.append(" values(");
		StringBuffer inn = new StringBuffer();
		for(Map.Entry<String, Object> toe:to.entrySet()){
			String key = toe.getKey();
			Object tov = toe.getValue();
			if(tov != null){
				FieldInfo f = columns.get(key);
				String type = f.getFieldType();
				String toValue = tov.toString();
				if(isBlob(type)){
					inn.append("0x");
					inn.append(toValue);
					inn.append(",");
				}else{
					toValue = transSpecial(toValue);
					inn.append("'");
					inn.append(toValue);
					inn.append("',");
				}
			}else{
				inn.append("null,");
			}
		}
		String inner = inn.toString();
		inner = inner.substring(0,inner.length()-1);
		sql.append(inner);
		sql.append(");");
		return sql.toString();
	}
	
	//将list写入文本
	public static void bufferWrite(String path,List<String> list){
		File file = new File(path);
		BufferedWriter bw = null;
		OutputStreamWriter writerStream = null;
		try {
			writerStream = new OutputStreamWriter(new FileOutputStream(file),"utf-8");
			bw = new BufferedWriter(writerStream);
			int l = list.size();
			for(int i=0;i<l;i++){
				bw.write(list.get(i));
				if(i < l-1){
					bw.newLine();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if(bw != null){
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	* @param tableNames	比较的表名
	* @param path	结果保存路径
	* @param isDel	是否保存删除结果
	* @param isUp	是否保存修改结果
	* @param isAdd	是否保存新增结果
	* @throws Exception
	* void
	*/
	public static void compare(List<String> tableNames,String path,boolean isDel,boolean isUp,boolean isAdd) throws Exception{
		try {
			
			long t1 = System.currentTimeMillis();
			System.out.println("开始对比数据……");
			
			getConnection1();
			getConnection2();
			
			List<String> compareScript = new ArrayList<String>();
			compareScript.add("SET FOREIGN_KEY_CHECKS=0;");
			for(String tableName:tableNames){
				System.out.print("正在对比表:"+tableName);
				List<String> dels = new ArrayList<String>();
				List<String> ups = new ArrayList<String>();
				List<String> adds = new ArrayList<String>();
				
				PreparedStatement stmtSrc = con1.prepareStatement("select * from "+tableName);
				PreparedStatement stmtTo = con2.prepareStatement("select * from "+tableName);
				ResultSet rsSrc = stmtSrc.executeQuery();
				ResultSet rsTo = stmtTo.executeQuery();
				
				initPks(tableName);
				initColumnMap(rsSrc);
				
				Map<String, Map<String,Object>> mapSrc =  getResultMap(rsSrc);
				Map<String, Map<String,Object>> mapTo =  getResultMap(rsTo);
				
				for(Map.Entry<String, Map<String,Object>> entry:mapSrc.entrySet()){
					String id = entry.getKey();
					Map<String,Object> r = mapTo.get(id);
					Map<String,Object> value = entry.getValue();
					if(r == null){
						dels.add(delSql(tableName, id));
					}else{
						if(!r.equals(value)){
							ups.add(upSql(tableName, id,value,r));
						}
						mapTo.remove(id);
					}
				}
				for(Map.Entry<String, Map<String,Object>> entry:mapTo.entrySet()){
					adds.add(addSql(tableName,entry.getValue()));
				}
				
				compareScript.add("/*表"+tableName+"对比结果*/");
				if(isDel){
					if(dels.size()>0){
						compareScript.add("#delete");
						compareScript.addAll(dels);
					}
				}
				if(isUp){
					if(ups.size()>0){
						compareScript.add("#update");
						compareScript.addAll(ups);
					}
				}
				if(isAdd){
					if(adds.size()>0){
						compareScript.add("#insert");
						compareScript.addAll(adds);
					}
				}
				String msg = "(del:"+dels.size()+"------------up:"+ups.size()+"------------add:"+adds.size()+")";
				System.out.println(msg);
				compareScript.add("");
			}
			
			compareScript.add("SET FOREIGN_KEY_CHECKS=1;");
			
			bufferWrite(path, compareScript);
			
			long t2 = System.currentTimeMillis();
			
			System.out.println("整个对比过程完毕!大约耗时" + (t2 - t1) + "ms!");
			System.out.println("对比结果保存位置：" + path);
			
			Runtime runtime = Runtime.getRuntime();
			runtime.exec("cmd /c start "+path);
			
		}catch(Exception e){
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}finally{
			if(con1 != null){
				con1.close();
			}
			if(con2 != null){
				con2.close();
			}
		}
	}
	
	//功能菜单对比需要的表名
	public static final List<String> menuTables = new ArrayList<String>(){
		private static final long serialVersionUID = 1L;
		{
			add("t_org_job");
			add("t_org_module");
			add("t_org_resource");
			add("t_org_role");
			add("t_sys_dict_type");
			add("t_sys_dict_value");
			add("t_sys_parameter");
		}
	};
	
	//定时器对比需要的表名
	public static final List<String> qrtzTables = new ArrayList<String>(){
		private static final long serialVersionUID = 1L;
		{
			add("qrtz_blob_triggers");
			add("qrtz_calendars");
			add("qrtz_cron_triggers");
			add("qrtz_fired_triggers");
			add("qrtz_job_details");
			add("qrtz_locks");
			add("qrtz_paused_trigger_grps");
			add("qrtz_scheduler_state");
			add("qrtz_simple_triggers");
			add("qrtz_simprop_triggers");
			add("qrtz_triggers");
		}
	};
	
	//流程对比需要的表名
	public static final List<String> flowTables = new ArrayList<String>(){
		private static final long serialVersionUID = 1L;
		{
			add("act_ge_bytearray");
			add("act_re_deployment");
			add("act_re_model");
			add("act_re_procdef");
			add("t_conclusion_mapping");
			add("t_flow_approve_item");
			add("t_flow_definition");
			add("t_flow_form");
			add("t_flow_node_message");
			add("t_flow_node_rule");
			add("t_flow_node_script");
			add("t_flow_node_set");
			add("t_flow_node_sign");
			add("t_flow_node_user");
			add("t_flow_task_due");
			add("t_sys_type");
		}
	};
	
	//源数据库，需要执行结果sql的数据库
	private static final Map<String,String> serverSrc = new HashMap<String, String>(){
		private static final long serialVersionUID = 1L;
		{
			put("url","jdbc:mysql://127.0.0.1:3306/ucg_oss_gdfrgc?useUnicode=true&characterEncoding=UTF-8");
			put("username","root");
			put("password","123456");
		}
	};
	
	//目标数据库，有更改的数据库
	private static final Map<String,String> serverTo = new HashMap<String, String>(){
		private static final long serialVersionUID = 1L;
		{
			put("url","jdbc:mysql://125.95.17.154:3388/ucg_oss_gdfrgc?useUnicode=true&characterEncoding=UTF-8");
			put("username","root");
			put("password","ucg");
		}
	};

	public static void main(String[] args) {
		try {
			/*compare(menuTables,"F:/menu.sql",true,true,true);
			compare(qrtzTables,"F:/qrtz.sql",true,true,true);*/
			compare(flowTables,"F:/flow.sql",true,true,true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

package com.ucg.util.quick;  
  
  
import java.io.File;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.ucg.util.config.PropertiesUtil;
import com.ucg.util.string.StringUtil;

import freemarker.template.Configuration;
  
 /**    
* 项目名称：UCG_OSS     
* 创建人：陈永培   
* 创建时间：2017-5-10下午2:35:00
* 功能说明：生成实体
* 修改人：陈永培
* 修改时间：    2017-5-10
* 修改备注：   
* @version       
*/
public class AddFieldUtils {

	//jdbc
	private static Configuration cfg;
	private static Connection con = null;  
	
	private static String jdbcUrl="";
	private static String user="";
	private static String password="";
	
	//数据库
	private static String tablename="";
	private static String addFields="";
	
	public static void initConfig(){
		PropertiesUtil.loadPropertyFile("addField.properties");
		jdbcUrl = PropertiesUtil.getProperty("jdbcUrl");
		user = PropertiesUtil.getProperty("user");
		password = PropertiesUtil.getProperty("password");
		tablename = PropertiesUtil.getProperty("tablename");
		addFields = PropertiesUtil.getProperty("addFields");
	}

	
	public static List<String> getTemplateList(String str){
		ArrayList<String> list = new ArrayList<String>();
		String[] indexs = str.split(",");
		for (String tIndex : indexs) {
			String tempalteName = QuickConstant.templateType.get(tIndex);
			list.add(tempalteName);
		}
		return list;
	}
	

	public static void generateEntity(String tableName, String packageName) throws Exception{
		try{
		    connectSQL("com.mysql.jdbc.Driver",jdbcUrl, user, password);//连接数据
		    String tableSql=" show full fields from "+tableName;
		    if(StringUtil.isNotEmpty(addFields)){
		    	String field = StringUtil.addQuotes(addFields);
		    	tableSql=tableSql+" where field in("+field+")";
		    }
			PreparedStatement stmt = con.prepareStatement(tableSql);
			ResultSet rs = stmt.executeQuery();
			List<FieldInfo> list = new ArrayList<FieldInfo>();
			while(rs.next()){
				String column = rs.getString("field");
				if(isSurperField(column)){
					continue;
				}
				FieldInfo fieldInfo=new FieldInfo();
				
				String staticField="   	public static final String "+column.toUpperCase()+"=\""+column+"\";  //"+rs.getString("comment")+"";
				fieldInfo.setFiled(staticField);
				StringBuffer getMothod=new StringBuffer();
				getMothod.append("   	public "+GenEntityUtils.getFieldType(rs.getString("type"))+" get"+StringUtil.firstUpperCase(getFiledName(column))+"(){ //"+rs.getString("comment")+"\n");
				getMothod.append("		return "+GenEntityUtils.getMothodType(rs.getString("type"))+"(\""+column+"\");\n");
				getMothod.append("	}");
				fieldInfo.setGetMethod(getMothod.toString());
				
				StringBuffer setMothod=new StringBuffer();
				setMothod.append("	public void set"+StringUtil.firstUpperCase(getFiledName(column))+"("+GenEntityUtils.getFieldType(rs.getString("type"))+" "+getFiledName(column)+"){\n");
				if(GenEntityUtils.getFieldType(rs.getString("type")).equals("Date")){
					setMothod.append("		set(\""+column+"\", new Timestamp("+getFiledName(column)+".getTime()));\n");
				}else{
					setMothod.append("		set(\""+column+"\", "+getFiledName(column)+");\n");
				}
				setMothod.append("	}");
				fieldInfo.setSetMethod(setMothod.toString());
				list.add(fieldInfo);
			}
			
			//输出
			for (FieldInfo fieldInfo : list) {
				System.out.println(fieldInfo.getFiled());
			}
				System.out.println("\n");
			for (FieldInfo fieldInfo : list) {
				System.out.println(fieldInfo.getGetMethod());
				System.out.println(fieldInfo.getSetMethod());
				System.out.println("\n");
			}
		}catch(Exception e){
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}finally{
			if(con != null){
				con.close();
			}
		}
	}

	public static  boolean isSurperField(String field){
		if("create_user_id".equals(field) || "create_user_name".equals(field) || "create_time".equals(field)|| "update_user_id".equals(field)|| "update_user_name".equals(field)|| "update_time".equals(field) || "ID".equals(field)||"id".equals(field)){
			return true;
		}
		return false;
	}
	public static String getFiledName(String dbName){
		String entityField = "";
		String[] str = dbName.split("_");
		for(int i=0;i<str.length;i++){
			if(i==0){
				entityField+=str[i];
			}else{
				String s = str[i].substring(0,1).toUpperCase()+str[i].substring(1);
				entityField+=s;
			}
		}
		return entityField;
	}

	
	public static final class FieldInfo{
		private String filed;
		private String setMethod;
		private String getMethod;
		public String getFiled() {
			return filed;
		}
		public void setFiled(String filed) {
			this.filed = filed;
		}
		public String getSetMethod() {
			return setMethod;
		}
		public void setSetMethod(String setMethod) {
			this.setMethod = setMethod;
		}
		public String getGetMethod() {
			return getMethod;
		}
		public void setGetMethod(String getMethod) {
			this.getMethod = getMethod;
		}
		
	}
	
	
    public static void connectSQL(String driver, String url, String UserName, String Password) {  
        try {  
            Class.forName(driver).newInstance();  
            con = DriverManager.getConnection(url, UserName, Password);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
	
	
	public static Configuration getConfiguration() throws Exception{  
		try{
			if (null == cfg) {  
				cfg = new Configuration();
				String path =  AddFieldUtils.class.getClassLoader().getResource("quickgenerate").getPath();
				File templateDirFile = new File(URLDecoder.decode(path, "UTF-8"));
				cfg.setDirectoryForTemplateLoading(templateDirFile);
				cfg.setLocale(Locale.CHINA);
				cfg.setDefaultEncoding("UTF-8");
			}  
		}catch(Exception e){
			throw new Exception(e.getMessage());
		}
        return cfg;  
	} 
	
	public static void main(String[] args) throws Exception {
		try {
			initConfig();
			generateEntity(tablename, addFields);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
    
	}
	
}  

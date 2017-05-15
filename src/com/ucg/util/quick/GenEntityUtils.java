package com.ucg.util.quick;  
  
  
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.ucg.util.config.PropertiesUtil;
import com.ucg.util.file.FileUtil;
import com.ucg.util.string.StringUtil;
import com.ucg.util.translate.tools.TranslateTools;

import freemarker.template.Configuration;
import freemarker.template.Template;
  
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
public class GenEntityUtils {

	//jdbc
	private static Configuration cfg;
	private static Connection con = null;  
	private static String javafilePath = "";
	private static String jdbcUrl="";
	private static String user="";
	private static String password="";
	
	//数据库
	private static String tablename="";
	private static String packagename="";
	private static String entityname="";
	private static String entityChineseName="";
	
	
	//系统路径
	private static String path="";
	private static String creator="";
	private static String acessUrl="";
	
	
	
	//model里面的方法
	private static String template="";
	
	private static  LinkedList<String> filePathList = new LinkedList<String>();
	
	
	public static void initConfig(){
		PropertiesUtil.loadPropertyFile("quickl.properties");
		jdbcUrl = PropertiesUtil.getProperty("jdbcUrl");
		user = PropertiesUtil.getProperty("user");
		password = PropertiesUtil.getProperty("password");
		tablename = PropertiesUtil.getProperty("tablename");
		packagename = PropertiesUtil.getProperty("packagename");
		entityname = PropertiesUtil.getProperty("entityname");
		javafilePath = PropertiesUtil.getProperty("javafilePath")+entityname+".java";
		path = PropertiesUtil.getProperty("path");
		creator = PropertiesUtil.getProperty("creator");
		acessUrl = PropertiesUtil.getProperty("acessUrl");
		template = PropertiesUtil.getProperty("template");
		entityChineseName = PropertiesUtil.getProperty("entityChineseName");
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
	

	public static void generateEntity(String tableName, String packageName, String entityName) throws Exception{
		try{
		    connectSQL("com.mysql.jdbc.Driver",jdbcUrl, user, password);//连接数据
			PreparedStatement stmt = con.prepareStatement(" show full fields from "+tableName);
			ResultSet rs = stmt.executeQuery();
			List<FieldInfo> list = new ArrayList<FieldInfo>();
			List<String> fieldList=new ArrayList<String>();
			while(rs.next()){
				String column = rs.getString("field");
				if(isSurperField(column)){
					continue;
				}
				FieldInfo fieldInfo = new FieldInfo();
				fieldInfo.setColumn(column);
				fieldInfo.setFieldType(getFieldType(rs.getString("type")));
				fieldInfo.setGetMethod(getMothodType(rs.getString("type")));
				fieldInfo.setFieldName(getFiledName(column));
				fieldList.add(getFiledName(column));
				fieldInfo.setComment(rs.getString("comment"));
				list.add(fieldInfo);
			}
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("columns", list);
			map.put("tableName", tableName);
			map.put("packageName", packageName);
			map.put("entityName", entityName);
			map.put("entityLowerName", entityName.toLowerCase());
			if(StringUtil.isNotEmpty(entityChineseName)){
				map.put("chinese",entityChineseName);
			}else{
				map.put("chinese",TranslateTools.translateText(entityName.toLowerCase()).getTranslation()[0]);
			}
			map.put("field",StringUtil.asString(fieldList, ","));
			map.put("createUser",creator);
			map.put("acessUrl",acessUrl);
			map.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
			generateFile(getTemplateList(template), javafilePath, map);
		}catch(Exception e){
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}finally{
			if(con != null){
				con.close();
			}
		}
	}


	
	@SuppressWarnings("unchecked")
	public static void  generateFile(List<String> templateList, String javafile, Object data) throws Exception{
		Writer out = null;
		try{
			FileUtil.deleteDirectory(new File(path));
			//判断文件夹是否存在
			if(!FileUtil.checkExist(path)){
				FileUtil.CreateDirectory(path);
			}
			for (int i = 0; i < templateList.size(); i++) {
				String ftlName=templateList.get(i);
				Map<String,Object> map = (Map<String, Object>) data;
				String entityName = map.get("entityName").toString();
				String chinese = map.get("chinese").toString();
				String filePath="";
				if(i==0){
					filePath = path+entityName+".java";
				}else{
					filePath = path+entityName+ftlName.substring(0, ftlName.indexOf("."))+".java";
					if(ftlName.equals("interface.ftl")){
						filePath = path+chinese+"接口"+".txt";
					}else if(ftlName.equals("Appconfig.ftl")){
						filePath = path+"Appconfig配置"+".txt";
					}
				}
				filePathList.add(filePath);
				Configuration cfg =getConfiguration();
				Template template = cfg.getTemplate(ftlName);
				template.setEncoding("UTF-8");
				out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), "UTF-8")); 
				template.process(data, out);  
				//for循环 下面close方法不能写在finally方法里面，不然就没法结束流
				out.flush();  
				out.close();
			}
		}catch(Exception e){
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}


	@SuppressWarnings("unused")
	private static void MergeFile(String javafile) throws IOException,Exception {
		try {
			//对生成的模板进行合并
			if(filePathList.size()==1){
				FileUtil.copyFile(filePathList.get(0), javafile);
			}else if(filePathList.size()>1){
				String mainJava = filePathList.get(0);
				StringBuffer mainJavaText= new StringBuffer(FileUtil.readTextFile(mainJava));
				int indexOf = mainJavaText.indexOf(QuickConstant.flag);
				if(indexOf>-1){//如果有自动生成标记号的话，就在它前面加方法
					indexOf = mainJavaText.indexOf(QuickConstant.flag);
				}else{//没有直接加载最后面
					indexOf=mainJavaText.lastIndexOf("}");
				}
				StringBuffer begin=new StringBuffer(mainJavaText.substring(0,indexOf));
				StringBuffer end=new StringBuffer(mainJavaText.substring(indexOf,mainJavaText.length()));
				//这里添加其他的模板
				for (int i = 1; i < filePathList.size(); i++) {
					StringBuffer otherJavaText= new StringBuffer(FileUtil.readTextFile(filePathList.get(i)));
					begin.append("\n\r");
					begin.append(otherJavaText);
				}
				begin.append("\n\r");
				begin.append(end);
				FileUtil.saveAsFileOutputStream(javafile, begin.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			//删除其他文件
			for (int i = 0; i < filePathList.size(); i++) {
				boolean deleteFlag = FileUtil.deleteFromName(filePathList.get(i));
				//System.out.println("文件["+filePathList.get(i)+"]删除"+(deleteFlag ==true?"成功":"失败"));
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
	public static String getFieldType(String dbType){
		if(dbType.startsWith("varchar") || dbType.startsWith("char") || dbType.startsWith("text")){
			return "String";
		}else if(dbType.startsWith("decimal")){
			return "BigDecimal";
		}else if(dbType.startsWith("int") ||dbType.startsWith("integer")){
			return "Integer";
		}else if(("datetime").equals(dbType) || "date".equals(dbType)){
			return "Date";
		}else if(dbType.startsWith("float")){
			return "Float";
		}else if(dbType.startsWith("double")){
			return "Double";
		}else if(dbType.startsWith("tinyint")){
			return "Boolean";
		}
		return "";
	}
	
	public static String getMothodType(String dbType){
		if(dbType.startsWith("varchar") || dbType.startsWith("char") || dbType.startsWith("text")){
			return "get";
		}else if(dbType.startsWith("decimal")){
			return "getBigDecimal";
		}else if(dbType.startsWith("int") ||dbType.startsWith("integer")){
			return "getInt";
		}else if("datetime".equals(dbType)){
			return "getTime";
		}else if("date".equals(dbType)){
			return "getDate";
		}else if(dbType.startsWith("float")){
			return "getFloat";
		}else if(dbType.startsWith("double")){
			return "getDouble";
		}else if(dbType.startsWith("tinyint")){
			return "getBoolean";
		}
		return "";
	}
	
	public static final class FieldInfo{
		private String column;
		private String fieldName;
		private String fieldType;
		private String comment;
		private String getMethod;
		
		public String getColumn() {
			return column;
		}
		public void setColumn(String column) {
			this.column = column;
		}
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
		public String getComment() {
			return comment;
		}
		public void setComment(String comment) {
			this.comment = comment;
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
				String path =  GenEntityUtils.class.getClassLoader().getResource("quickgenerate").getPath();
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
			generateEntity(tablename, packagename, entityname);
			//JOptionPane.showMessageDialog(null, "完美生成实体","提示" , JOptionPane.INFORMATION_MESSAGE)
			String doc = javafilePath.substring(0,javafilePath.lastIndexOf("\\"));
			Runtime.getRuntime().exec("cmd /c start "+doc);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
    
	}
	
}  

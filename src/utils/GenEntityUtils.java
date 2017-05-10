package utils;  
  
  
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;
  
public class GenEntityUtils {

	private static Configuration cfg;
	private static Connection con = null;  
	private static String file = "";

	public static void generateEntity(String tableName, String packageName, String entityName) throws Exception{
		try{
		    connectSQL("com.mysql.jdbc.Driver", "jdbc:mysql://202.104.31.178:5559/zyxd?useUnicode=true&characterEncoding=UTF-8", "root", "ucg");//������ݿ�  
			PreparedStatement stmt = con.prepareStatement(" show full fields from "+tableName);
			ResultSet rs = stmt.executeQuery();
			List<FieldInfo> list = new ArrayList<FieldInfo>();
			while(rs.next()){
				String column = rs.getString("field");
				if(isSurperField(column)){
					continue;
				}
				FieldInfo fieldInfo = new FieldInfo();
				fieldInfo.setColumn(column);
				fieldInfo.setFieldType(getFieldType(rs.getString("type")));
				fieldInfo.setFieldName(getFiledName(column));
				fieldInfo.setComment(rs.getString("comment"));
				list.add(fieldInfo);
			}
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("columns", list);
			map.put("tableName", tableName);
			map.put("packageName", packageName);
			map.put("entityName", entityName);
			map.put("createUser", System.getProperty("user.name"));
			map.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
			
			//String file = this.getClass().getClassLoader().getResource("com/ucg/base").getPath()+"/"+entityName+".java";
			file = "D:/"+entityName+".java";
			generateFile("entityTemplate.ftl", file, map);
		}catch(Exception e){
			throw new Exception(e.getMessage());
		}finally{
			if(con != null){
				con.close();
			}
		}
	}


	
	public static void  generateFile(String ftlName, String file, Object data) throws Exception{
		Writer out = null;
		try{
			Configuration cfg =getConfiguration();
			Template template = cfg.getTemplate(ftlName);
			template.setEncoding("UTF-8");
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8")); 
			template.process(data, out);  
		}catch(Exception e){
			throw new Exception(e.getMessage());
		}finally{
			out.flush();  
			out.close();
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
		}else if(dbType.startsWith("int") ||dbType.startsWith("integer")||dbType.startsWith("tinyint")){
			return "Integer";
		}else if(("datetime").equals(dbType) || "date".equals(dbType)){
			return "Date";
		}else if(dbType.startsWith("float")){
			return "Float";
		}else if(dbType.startsWith("double")){
			return "Double";
		}
		return "";
	}
	
	public static final class FieldInfo{
		private String column;
		private String fieldName;
		private String fieldType;
		private String comment;
		
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
		
	}
	
	
    public static void connectSQL(String driver, String url, String UserName, String Password) {  
        try {  
            Class.forName(driver).newInstance();  
            con = DriverManager.getConnection(url, UserName, Password);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
	
	
	private static Configuration getConfiguration() throws Exception{  
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
		generateEntity("t_org_user", "com.ucg.base.model", "User");
		Runtime.getRuntime().exec("cmd /c start "+file);
	}
	
}  

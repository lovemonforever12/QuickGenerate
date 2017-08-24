package com.ucg.util.db;  
  
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.ucg.util.config.PropertiesUtil;

import freemarker.template.Configuration;
  

 /**    
* 项目名称：UCG_OSS     
* 创建人：陈永培   
* 创建时间：2017-1-10上午11:27:41
* 功能说明：查询结果导出insert语句
* 修改人：陈永培
* 修改时间：    2017-1-10
* 修改备注：   
* @version       
*/
public class synDictSQL {  
  
	private static String code="";
    private static Connection conn = null;  
    private static Statement sm = null;  
    private static String insert = "INSERT INTO";//插入sql  
    private static String values = "VALUES";//values关键字  
    private static List<String> tableList = new ArrayList<String>();//全局存放表名列表  
    private static List<String> insertList = new ArrayList<String>();//全局存放insertsql文件的数据  
    private static String filePath = "E://insertSQL.sql";//绝对路径 导出数据的文件  
  
  //jdbc
  	private static Configuration cfg;
  	private static Connection con = null;  
  	
  	private static String jdbcUrl="";
  	private static String user="";
  	private static String password="";
  	
    public static void initConfig(){
		PropertiesUtil.loadPropertyFile("quickl.properties");
		jdbcUrl = PropertiesUtil.getProperty("jdbcUrl");
		user = PropertiesUtil.getProperty("user");
		password = PropertiesUtil.getProperty("password");
	}
    
    public static String generateTableDataSQL(String sql, String[] params) {  
        return null;  
    }  
  
    public static String executeSelectSQLFile(String file, String[] params) throws Exception {  
        List<String> listSQL = new ArrayList<String>();  
        connectSQL("com.mysql.jdbc.Driver", jdbcUrl, user, password);//连接数据库  
        listSQL = createSQL(file);//创建查询语句  
        executeSQL(conn, sm, listSQL, tableList);//执行sql并拼装  
        createFile();//创建文件  
        return null;  
    }  
  
    /** 
     * 拼装查询语句 
     * 
     * @return 返回select集合 
     */  
    private static List<String> createSQL(String file) throws Exception {  
        List<String> listSQL = new ArrayList<String>();  
        BufferedReader br = null;  
        InputStreamReader fr = null;  
        InputStream is = null;  
  
        int i;//表名的第一个字符位置  
        int k;//表名单最后一个字符的位置  
        String tableName;  
  
        try {  
            is = synDictSQL.class.getResourceAsStream(file);  
            fr = new InputStreamReader(is);  
            br = new BufferedReader(fr);  
            String rec = null;//一行  
            while ((rec = br.readLine()) != null) {  
                rec = rec.toLowerCase();  
                if(rec.equals("")) continue;
                if(rec.startsWith("#")) continue;
                i = rec.indexOf("from ", 1) + 5;  
                k = rec.indexOf(" ", i);  
                if (k == -1) {  
                    k = rec.length();  
                }  
                ;  
                tableName = rec.substring(i, k);  
                tableList.add(tableName);  
                //获取所有查询语句  
                listSQL.add(rec.toString());  
            }  
  
        } finally {  
            if (br != null) {  
                br.close();  
            }  
            if (fr != null) {  
                fr.close();  
            }  
            if (is != null) {  
                is.close();  
            }  
        }  
        return listSQL;  
    }  
  
    /** 
     * 创建insertsql.txt并导出数据 
     */  
    private static void createFile() {  
        File file = new File(filePath);  
        if (!file.exists()) {  
            try {  
                file.createNewFile();  
            } catch (IOException e) {  
                System.out.println("创建文件名失败！！");  
                e.printStackTrace();  
            }  
        }  
        FileWriter fw = null;  
        BufferedWriter bw = null;  
        try {  
            fw = new FileWriter(file);  
            bw = new BufferedWriter(fw);  
            if (insertList.size() > 0) {  
                for (int i = 0; i < insertList.size(); i++) {  
                    bw.append(insertList.get(i));  
                    bw.append("\n");  
                }  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            try {  
                bw.close();  
                fw.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
    }  
  
    /** 
     * 连接数据库 创建statement对象 
     * 
     * @param driver 
     * @param url 
     * @param UserName 
     * @param Password 
     */  
    public static void connectSQL(String driver, String url, String UserName, String Password) {  
        try {  
            Class.forName(driver).newInstance();  
            conn = DriverManager.getConnection(url, UserName, Password);  
            sm = conn.createStatement();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
  
    /** 
     * 执行sql并返回插入sql 
     * 
     * @param conn 
     * @param sm 
     * @param listSQL 
     * @throws java.sql.SQLException 
     */  
    public static void executeSQL(Connection conn, Statement sm, List listSQL, List listTable) throws SQLException {  
        List<String> insertSQL = new ArrayList<String>();  
        ResultSet rs = null;  
        try {  
            rs = getColumnNameAndColumeValue(sm, listSQL, listTable, rs);  
        } catch (SQLException e) {  
            e.printStackTrace();  
        } finally {  
            rs.close();  
            sm.close();  
            conn.close();  
        }  
    }  
  
    /** 
     * 获取列名和列值 
     * 
     * @param sm 
     * @param listSQL 
     * @param rs 
     * @return 
     * @throws java.sql.SQLException 
     */  
    private static ResultSet getColumnNameAndColumeValue(Statement sm,  List listSQL, List ListTable, ResultSet rs) throws SQLException {
    	if(null!=listSQL&&listSQL.size()>0){
    		String  typeSql = listSQL.get(0).toString();//参数类型
    		String typeTable = ListTable.get(0).toString();//参数类型表
    		String code_r="${code}";
    		typeSql = typeSql.replace(code_r, "\'"+code+"\'");
    		String  valueSql = listSQL.get(1).toString();//参数值
    		String valueTable = ListTable.get(1).toString();//参数类型表
    		rs = commonGenernate(sm, typeSql,typeTable);  
    		 rs = sm.executeQuery(typeSql);  
    		 String type_Id="";
    		 String is_list="";
    		 String dict_type_value="";
    		   while (rs.next()) {  
    			   type_Id = rs.getString("id");
    			   String type_code = rs.getString("type_code");//列表还是下拉树
    			   dict_type_value = rs.getString("name");
    			   is_list="0";
    			   if(type_code.equals("list")){//列表
    				   is_list="1";//是
    			   }else{
    				   is_list="1";//否
    			   }
    			   break;
			}
    		String type_Id_r="${type_id}";
    		String is_list_r="${is_list}";
    		String dict_type_value_r="${dict_type_value}";
    		System.out.println(valueSql);
    		valueSql=valueSql.replace(is_list_r, ""+is_list+"");
    		valueSql = valueSql.replace(type_Id_r,"\'"+type_Id+"\'");
    		valueSql = valueSql.replace(dict_type_value_r,"\'"+dict_type_value+"\'");
    	
       		rs = commonGenernate(sm, valueSql,valueTable);  
    		   
    	}

        return rs;  
    }

	private static ResultSet commonGenernate(Statement sm, String listSQL,String ListTable) throws SQLException {
		ResultSet rs;
		String sql = listSQL;  
		rs = sm.executeQuery(sql);  
		ResultSetMetaData rsmd = rs.getMetaData();  
		int columnCount = rsmd.getColumnCount();  
		while (rs.next()) {  
		    StringBuffer ColumnName = new StringBuffer();  
		    StringBuffer ColumnValue = new StringBuffer();  
		    for (int i = 1; i <= columnCount; i++) {  
		        String value = rs.getString(i);  
		        if (i == columnCount) {  
		            ColumnName.append(rsmd.getColumnLabel(i));  
		            String columnLabel = rsmd.getColumnLabel(i);
		            if (Types.CHAR == rsmd.getColumnType(i) || Types.VARCHAR == rsmd.getColumnType(i)  
		                    || Types.LONGVARCHAR == rsmd.getColumnType(i)) {  
		                if (value == null) {  
		                    ColumnValue.append("null");  
		                } else {  
		                    ColumnValue.append("'").append(value).append("'");  
		                }  
		            } else if (Types.SMALLINT == rsmd.getColumnType(i) || Types.INTEGER == rsmd.getColumnType(i)  
		                    || Types.BIGINT == rsmd.getColumnType(i) || Types.FLOAT == rsmd.getColumnType(i)  
		                    || Types.DOUBLE == rsmd.getColumnType(i) || Types.NUMERIC == rsmd.getColumnType(i)  
		                    || Types.DECIMAL == rsmd.getColumnType(i)) {  
		                if (value == null) {  
		                    ColumnValue.append("null");  
		                } else {  
		                    ColumnValue.append(value);  
		                }  
		            } else if (Types.DATE == rsmd.getColumnType(i) || Types.TIME == rsmd.getColumnType(i)  
		                    || Types.TIMESTAMP == rsmd.getColumnType(i)) {  
		                if (value == null) {  
		                    ColumnValue.append("null");  
		                } else {  
		                    ColumnValue.append("timestamp'").append(value).append("'");  
		                }  
		            } else {  
		                if (value == null) {  
		                    ColumnValue.append("null");  
		                } else {  
		                    ColumnValue.append(value);  
		                }  
		            }  
		        } else {  
		            ColumnName.append(rsmd.getColumnLabel(i) + ",");  
		            if (Types.CHAR == rsmd.getColumnType(i) || Types.VARCHAR == rsmd.getColumnType(i)  
		                    || Types.LONGVARCHAR == rsmd.getColumnType(i)) {  
		                if (value == null) {  
		                    ColumnValue.append("null,");  
		                } else {  
		                    ColumnValue.append("'").append(value).append("',");  
		                }  
		            } else if (Types.SMALLINT == rsmd.getColumnType(i) || Types.INTEGER == rsmd.getColumnType(i)  
		                    || Types.BIGINT == rsmd.getColumnType(i) || Types.FLOAT == rsmd.getColumnType(i)  
		                    || Types.DOUBLE == rsmd.getColumnType(i) || Types.NUMERIC == rsmd.getColumnType(i)  
		                    || Types.DECIMAL == rsmd.getColumnType(i)) {  
		                if (value == null) {  
		                    ColumnValue.append("null,");  
		                } else {  
		                    ColumnValue.append(value).append(",");  
		                }  
		            } else if (Types.DATE == rsmd.getColumnType(i) || Types.TIME == rsmd.getColumnType(i)  
		                    || Types.TIMESTAMP == rsmd.getColumnType(i)) {  
		                if (value == null) {  
		                    ColumnValue.append("null,");  
		                } else {  
		                    ColumnValue.append("timestamp'").append(value).append("',");  
		                }  
		            } else {  
		                if (value == null) {  
		                    ColumnValue.append("null,");  
		                } else {  
		                    ColumnValue.append(value).append(",");  
		                }  
		            }  
		        }  
		    }  
		    //System.out.println(ColumnName.toString());  
		    //System.out.println(ColumnValue.toString());  
		    insertSQL(ListTable, ColumnName, ColumnValue);  
		    
		}
		return rs;
	}  
  
    /** 
     * 拼装insertsql 放到全局list里面 
     * 
     * @param ColumnName 
     * @param ColumnValue 
     */  
    private static void insertSQL(String TableName, StringBuffer ColumnName,  
                                  StringBuffer ColumnValue) {  
        StringBuffer insertSQL = new StringBuffer();  
        insertSQL.append(insert).append(" ").append(TableName).append("(").append(ColumnName.toString())  
                .append(")").append(values).append("(").append(ColumnValue.toString()).append(");\r\n");  
        insertList.add(insertSQL.toString());  
        System.out.println(insertSQL.toString());  
     
    }  
  
    public static void main(String[] args) throws Exception {  
    	initConfig();
    	code="SLLoanApplication";
        String file2 = "config/syn_dict_sqlite_data.cfg";  
        executeSelectSQLFile(file2, null);  
        Runtime.getRuntime().exec("cmd /c start "+filePath);
  
    }  
}
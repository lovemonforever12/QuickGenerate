package com.ucg.util.db;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.ResourceBundle;

import com.ucg.base.framework.core.util.DateUtils;
import com.ucg.base.framework.core.util.FileUtils;
import com.ucg.util.file.FileUtil;
 /**    
* 项目名称：UCG_OSS     
* 创建人：陈永培   
* 创建时间：2017-1-21上午10:02:32
* 功能说明：备份数据库
* 修改人：陈永培
* 修改时间：    2017-1-21
* 修改备注：   
* @version       
*/
public class MysqlUtils {
 
	private static  String serverIp = "";
	private static  String port = "";
	private static ResourceBundle bundle=ResourceBundle.getBundle("mysql");
	private static String mysqlpath;
	private static String savepath;
	private static String user="";
	private static String password="";
	//数据库	
	private static String dbName="";
		
	 public static void initConfig(){
		user = bundle.getString("user");
		password = bundle.getString("password");
		dbName = bundle.getString("dbName");
		serverIp = bundle.getString("serverIp");
		port = bundle.getString("port");
		mysqlpath = bundle.getString("mysqlpath");
	}
	 
	
	/**
	 * 备份
	 * @return
	 */
	 public static String backup() {
		 
	  String path ="";
	  InputStream in =null;
	  InputStreamReader isr=null;
	  BufferedReader br=null;
	  FileOutputStream fout = null;
	  OutputStreamWriter writer=null;
	  try {
	   Runtime rt = Runtime.getRuntime();
	   // 调用 调用mysql的安装目录的命令 -x 必定加（锁表） 不然倒不出来、
	   mysqlpath = "cmd /c start "+mysqlpath.replaceAll(" ","\" \"");
	   String cmd=mysqlpath+"\\mysqldump -hlocalhost -u"+user+" -p"+password+" --opt "+dbName+">d:\\bk.sql";
	 
       Process pro = Runtime.getRuntime().exec(cmd);
	   // 设置导出编码为utf-8。这里必须是utf-8
	   // 把进程执行中的控制台输出信息写入.sql文件，即生成了备份文件。注：如果不对控制台信息进行读出，则会导致进程堵塞无法运行
	    in = pro.getInputStream();// 控制台的输出信息作为输入流
	    isr= new InputStreamReader(in, "utf-8");
	   // 设置输出流编码为utf-8。这里必须是utf-8，否则从流中读入的是乱码
	   String inStr;
	   // 组合控制台输出信息字符串
	   path=savepath+DateUtils.getCurrentTimeMillis()+"-zyxd.sql";
	   fout = new FileOutputStream(path,true);
	   writer = new OutputStreamWriter(fout, "utf-8");
	  
	   br = new BufferedReader(isr);
	   while ((inStr = br.readLine()) != null) {
	    writer.write(inStr + "\r\n");
	   }
	  
	   // 要用来做导入用的sql目标文件：
	   
	   boolean checkExist = FileUtil.checkDirExist(savepath);
	   
	   if(!checkExist){
		 FileUtils.createFolderFile(savepath);
	   }
	 
	  } catch (Exception e) {
	   e.printStackTrace();
	  }finally{
		  try {
			writer.flush();
			 in.close();
			   isr.close();
			   br.close();
			   writer.close();
			   fout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	  }
	  return path;
	 }
	
	 public  void load() {      
	    try {      
	        String fPath = "d:/task.sql";    
	        Runtime rt = Runtime.getRuntime();      
	 
	        // 调用 mysql 的 cmd:      
	        Process child = rt.exec(mysqlpath+"//mysql.exe -u"+user+" -p"+password+" task");      
	      
	        OutputStream out = child.getOutputStream();//控制台的输入信息作为输出流      
	        String inStr;      
	        StringBuffer sb = new StringBuffer("");      
	        String outStr;      
	        BufferedReader br = new BufferedReader(new InputStreamReader(      
	                new FileInputStream(fPath), "utf8"));      
	        while ((inStr = br.readLine()) != null) {     
	            sb.append(inStr + "\r\n");      
	        }      
	        outStr = sb.toString();      
	 
	        OutputStreamWriter writer = new OutputStreamWriter(out, "utf8");      
	        writer.write(outStr);      
	        // 注：这里如果用缓冲方式写入文件的话，会导致中文乱码，用flush()方法则可以避免      
	        writer.flush();      
	        // 别忘记关闭输入输出流      
	        out.close();      
	        br.close();      
	        writer.close();      
	 
	        System.out.println("");      
	 
	    } catch (Exception e) {      
	        e.printStackTrace();      
	    }      
	 
	 }
	 
	 
	
	
	public static void main(String[] args) throws Exception {
			/*MysqlUtils.savepath="D:\\";
			initConfig();
			MysqlUtils.backup();*/
			
			String command = "cmd /c C:/Program Files (x86)/MySQL/MySQL Server 5.5/bin>mysqldump -hlocalhost -uroot -p123456 zyxd> E:/aijia.sql";   
			  try {   
			   Process process = Runtime.getRuntime().exec(command);   
			   InputStreamReader ir = new InputStreamReader(process.getInputStream());   
			   LineNumberReader input = new LineNumberReader(ir);   
			   String line;   
			   while ((line = input.readLine()) != null)   
			    System.out.println(line);   
			   input.close();   
			  } catch (IOException e) {   
			   e.printStackTrace();   
			  }   
	}

	
}
package com.ucg.util.pdf;  
  
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;
/** 
 * office转化为pdf 
 * pdf转化为swf文件 
 * @author Administrator 
 * 
 */  
public class Converter {    
      private static String openOfficePath = "C:/Program Files (x86)/OpenOffice 4/";  //openoffice软件的安装路径  
      
      
      static{
    	  try {
			startServerAfterCheck();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      }
      
    /**  
     * 将Office文档转换为PDF. 运行该函数需要用到OpenOffice和jodconverter-2.2.2 
     * <pre>  
     * 方法示例:  
     * String sourcePath = "F:\\office\\source.doc";  
     * String destFile = "F:\\pdf\\dest.pdf";  
     * Converter.office2PDF(sourcePath, destFile);  
     * </pre>  
     *   
     * @param sourceFile  
     *            源文件, 绝对路径. 可以是Office2003-2007全部格式的文档, Office2010的没测试. 包括.doc,  
     *            .docx, .xls, .xlsx, .ppt, .pptx等. 示例: F:\\office\\source.doc  
     * @param destFile  
     *            目标文件. 绝对路径. 示例: F:\\pdf\\dest.pdf  
     * @return 操作成功与否的提示信息. 如果返回 -1, 表示找不到源文件, 或url.properties配置错误; 如果返回 0,  
     *         则表示操作成功; 返回1, 则表示转换失败  
     */    
    public static int office2PDF(String sourceFile, String destFile) {    
        try {  
        	startServerAfterCheck();  
            File inputFile = new File(sourceFile);    
            if (!inputFile.exists()) {    
                return -1;// 找不到源文件, 则返回-1    
            }    
    
            // 如果目标路径不存在, 则新建该路径    
            File outputFile = new File(destFile);    
            if (!outputFile.getParentFile().exists()) {    
                outputFile.getParentFile().mkdirs();    
            }    
            
            // connect to an OpenOffice.org instance running on port 8100    
            OpenOfficeConnection connection = new SocketOpenOfficeConnection(    
                    "127.0.0.1", 8100);    
            connection.connect();    
    
            // convert    
            DocumentConverter converter = new OpenOfficeDocumentConverter(    
                    connection);    
            converter.convert(inputFile, outputFile);    
    
            // close the connection    
            connection.disconnect();    
            // 关闭OpenOffice服务的进程    
    
            return 0;    
        } catch (Exception e) {    
            e.printStackTrace();    
        }
    
        return 1;    
    }    
    
    private static boolean isWin(){
    	boolean flag = false;
    	String os = System.getProperty("os.name");  
    	if(os.toLowerCase().startsWith("win")){  
    		flag = true;  
    	} 
    	return flag;
    }
    
    private static String[] getCmd(String cmd){
    	boolean flag = isWin();
    	String[] command = new String[3];
    	if(flag){
    		command[0] = "cmd";
    		command[1] = "/C";
    	}else{
    		command[0] = "/bin/sh";
    		command[1] = "-c";
    	}
    	command[2] = cmd;
    	return command;
    }
    
    private static boolean checkServer() throws Exception{
    	boolean flag = false; 
    	String[] cmd = getCmd("netstat -lnp|grep 8100");
    	Process pro = Runtime.getRuntime().exec(cmd);
    	int result = pro.waitFor();
    	if(result == 0 || result == 1){
    		InputStream is = pro.getInputStream();
    		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    		String info = "";
    		String line;
    		while ((line = reader.readLine()) != null) {
    			info += line;
    		}
    		if(!"".equals(info.trim())){
    			flag = true;
    		}
    		is.close();
    		reader.close();
    	}
    	pro.destroy();  
    	return flag;
    }
    
    private static boolean startServer() throws Exception{
    	boolean flag = false;
    	String OpenOffice_HOME = openOfficePath;//这里是OpenOffice的安装目录    
        // 如果从文件中读取的URL地址最后一个字符不是 '\'，则添加'\'    
        if (OpenOffice_HOME.charAt(OpenOffice_HOME.length() - 1) != '/') {    
            OpenOffice_HOME += "/";    
        }    
        String command="";
        if(isWin()){
        	// 启动OpenOffice的服务   Window
             command = OpenOffice_HOME  
                    + "program/soffice -headless -accept=\"socket,host=127.0.0.1,port=8100;urp;\" -nofirststartwizard ";  
        }else{
        	// 启动OpenOffice的服务    Linux
             command = "nohup "+OpenOffice_HOME    
                    + "program/soffice -headless -accept=\"socket,host=127.0.0.1,port=8100;urp;\" -nofirststartwizard &";  
        }
        
    	String[] cmd = getCmd(command);
    	Process pro = Runtime.getRuntime().exec(cmd);
    	int result = pro.waitFor();
    	if(result == 0){
    		flag = true;
    	}
    	pro.destroy();
    	return flag;
    }
    
    private static boolean startServerAfterCheck() throws Exception{
    	boolean flag = true;
    	if(!checkServer()){
    		flag = startServer();
    	}
    	return flag;
    }
}    
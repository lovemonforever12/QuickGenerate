package com.ucg.util.pdf;  
  
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;
 
 /**    
* 项目名称：UCG_OSS     
* 创建人：陈永培   
* 创建时间：2017-6-15下午8:35:35
* 功能说明：转pdf
* 修改人：陈永培
* 修改时间：    2017-6-15
* 修改备注：   
* @version       
*/
public class PDFConverter {    
	
	/**
	 * 查看8200端口是否开启 ：netstat -ano  | findstr  "8100"
	 * 终止8200端口：taskkill /pid 11736 /f
	 * 
	 */
	  public static String openOfficePath = "C:/Program Files (x86)/OpenOffice 4/";  //openoffice软件的安装路径  
	  private  static final int PORT=12310;
      private  static Process pro = null;
      
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
            OpenOfficeConnection connection = new SocketOpenOfficeConnection("127.0.0.1", PORT);    
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
    
  
    
    /**     
    * 创建人：陈永培   
    * 创建时间：2017-6-15 下午8:36:17
    * 功能说明：开启服务
    * @return
    * @throws Exception   
    */
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
                    + "program/soffice -headless -accept=\"socket,host=127.0.0.1,port="+PORT+";urp;\"";  
             pro = Runtime.getRuntime().exec(command);
 			flag = true;
        }else{
        	// 启动OpenOffice的服务    Linux
             command = "nohup "+OpenOffice_HOME    
                    + "program/soffice -headless -accept=\"socket,host=127.0.0.1,port="+PORT+";urp;\" -nofirststartwizard &";  
             String[] cmd = getCmd(command);
         	pro = Runtime.getRuntime().exec(cmd);
         	int result = pro.waitFor();
        	if(result == 0){
        		flag = true;
        	}
        	
        }
        /*java.lang.Process.waitFor()方法将导致当前的线程等待，如果必要的话，
    	//直到由该Process对象表示的进程已经终止。此方法将立即返回，
    	如果子进程已经终止。如果子进程尚未终止，则调用线程将被阻塞，直到子进程退出。*/
    	return flag;
    }
    
    public static boolean startServerAfterCheck() throws Exception{
    	boolean flag = true;
    	if(!checkServer()){
    		flag = startServer();
    	}
    	return flag;
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
    	String command="";
    	Process pro = null;
        if(isWin()){
        	return false;//window 始终返回false
        }else{
        	command = "netstat -lnp|grep :"+PORT;
        	String[] cmd = getCmd(command);
        	pro = Runtime.getRuntime().exec(cmd);
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
        }
    	
    	return flag;
    }
    public static void main(String[] args) throws Exception {
    	String info ="";
		String sourcePath = "D://java//testFdd.docx";
        String destFile = "D://java//testFdd.pdf";   
        PDFConverter.startServerAfterCheck();//pdf转化插件，优先启动服务，by小培
        int flag = PDFConverter.office2PDF(sourcePath, destFile);   
        if (flag == 1) {  
        	info = "转化失败";  
        }else if(flag == 0){  
        	info = "转化成功";  
        }else {  
        	info = "找不到源文件, 或url.properties配置错误";          
        }  
        System.out.println(info);
	}
}    
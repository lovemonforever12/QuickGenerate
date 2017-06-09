package com.ucg.util.freemark;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

import sun.misc.BASE64Encoder;

import com.ucg.util.common.CommonUtil;
import com.ucg.util.string.StringUtil;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

public class FreeMarkerUtil {
	private static Configuration cfg;
	/**   
	*    
	* 项目名称：UCG_OSS     
	* 创建人：王郎郎
	* 创建时间：2015-2-28 上午10:52:09   
	* 功能说明：freeMarker生成文档
	* 修改人：王郎郎 
	* 修改时间：2015-2-28 上午10:52:09   
	* 修改备注：
	* @param templatePath模板所在文件夹,调用时可通过this.getClass().getResource("/ucg/template/report").getFile()获取 ;
	* @param templateName模板名称,例如surveryReport.ftl;map,数据集合;docName,要输出的文档名称;response,请求输出流;
	* @version    
	*    
	*/ 
	public static void freeMarkerDoc(String templatePath,String templateName,Map map,String docName,HttpServletResponse response){
		
		if(map==null||map.isEmpty())
			return ;
		Configuration configuration = null;
		Template t = null;
		Writer out = null;
     	configuration = new Configuration();
     	docName = docName+".doc";
		try {
			
			// 设置模本装置方法和路径,FreeMarker支持多种模板装载方法。可以从servlet，classpath，文件系统、数据库装载，
			configuration.setDirectoryForTemplateLoading(new File(templatePath));
			// 设置对象包装器
			configuration.setObjectWrapper(new DefaultObjectWrapper());
			// 设置异常处理器
			configuration.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
			// 设置默认编码
			configuration.setDefaultEncoding("utf-8");
			// test.ftl为要装载的模板
			t = configuration.getTemplate(templateName,"utf-8");
			
			//数据的替换
			response.setContentType("APPLICATION/OCTET-STREAM");
			String filename = toISO8859_1(docName);
			response.setHeader("Content-Disposition", "attachment; filename=\""
					+ filename + "\"");
			//直接输出文件流
			OutputStream outps = response.getOutputStream();
			out = new BufferedWriter(new OutputStreamWriter(outps, "utf-8"));
			t.process(map, out);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(out!=null){
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**     
	* 创建人：陈永培   
	* 创建时间：2017-3-27 下午3:23:07
	* 功能说明：多个文件以zip包下载    
	*/
	public static void freeMarkerMultiplyDoc(String templatePath,String templateName,List<Map> listMap,List<String> fileName,String docName,HttpServletResponse response,HttpServletRequest request){
		if(CommonUtil.isNull(listMap)) return;
		Configuration configuration = null;
		Template t = null;
		Writer out = null;
     	configuration = new Configuration();
     	docName = docName+".zip";
		try {
			// 设置模本装置方法和路径,FreeMarker支持多种模板装载方法。可以从servlet，classpath，文件系统、数据库装载，
			configuration.setDirectoryForTemplateLoading(new File(templatePath));
			// 设置对象包装器
			configuration.setObjectWrapper(new DefaultObjectWrapper());
			// 设置异常处理器
			configuration.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
			// 设置默认编码
			configuration.setDefaultEncoding("utf-8");
			// test.ftl为要装载的模板
			t = configuration.getTemplate(templateName,"utf-8");

			//生成临时的模板文件
			List<File> fileList=new ArrayList<File>();
			if(CommonUtil.isNotNull(listMap)){
				for (int i = 0; i < listMap.size(); i++) {
					String name=fileName.get(i);
					File tmpFile = new File(templatePath+"/"+name+".doc");
					fileList.add(tmpFile);
					out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tmpFile), "utf-8"));
					t.process(listMap.get(i), out);
				}
			}
			
			//直接输出文件流
			if(CommonUtil.isNotNull(fileList)){
				 try {   
					 byte[] buffer = new byte[8192];
					 String FilePath = request.getSession().getServletContext().getRealPath("/");
					 String strZipPath = FilePath + docName;   
					 ZipOutputStream zipout = new ZipOutputStream(new FileOutputStream(strZipPath));   
			         for (File file : fileList) {
		        	   FileInputStream fis = new FileInputStream(file);   
		        	   zipout.putNextEntry(new ZipEntry(file.getName()));   
		        	   //设置压缩文件内的字符编码，不然会变成乱码   
		        	   zipout.setEncoding("GBK");   
		        	   int len;   
		        	   // 读入需要下载的文件的内容，打包到zip文件   
		        	   while ((len = fis.read(buffer)) > 0) {   
		        		   zipout.write(buffer, 0, len);   
		        	   }   
		        	   zipout.closeEntry();   
		        	   fis.close();   
			          }
		            zipout.close();
		            downFile(response, docName,request);   
		        } catch (Exception e) {  
		            e.printStackTrace();
		            System.out.println("压缩时出错了！");
		        }finally{
		        	//完成后删除临时的文件
		        	 for (File file : fileList) {
		        		 file.delete();
		        	 }
		        }
			} 
			
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(out!=null){
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
		
	private static void downFile(HttpServletResponse response, String docName,HttpServletRequest request) {
        try {
            String FilePath = request.getSession().getServletContext().getRealPath("/");
            String path = FilePath + docName;   
            File file = new File(path);   
            if (file.exists()) {   
                InputStream ins = new FileInputStream(file);   
                BufferedInputStream bins = new BufferedInputStream(ins);// 放到缓冲流里面   
                OutputStream outs = response.getOutputStream();// 获取文件输出IO流   
                BufferedOutputStream bouts = new BufferedOutputStream(outs);
                response.setCharacterEncoding("UTF-8");
   
            	//数据的替换
    			response.setContentType("APPLICATION/OCTET-STREAM");
    			String filename = toISO8859_1(docName);
    			response.setHeader("Content-Disposition", "attachment; filename=\""
    					+ filename + "\"");
                int bytesRead = 0;   
                byte[] buffer = new byte[8192];   
                // 开始向网络传输文件流   
                while ((bytesRead = bins.read(buffer, 0, 8192)) != -1) {   
                    bouts.write(buffer, 0, bytesRead);   
                }   
                bouts.flush();// 这里一定要调用flush()方法   
                ins.close();   
                bins.close();   
                outs.close();   
                bouts.close();   
            } else {   
            }   
        } catch (Exception e) {   
            e.printStackTrace();
            System.out.println("下载出错了！");
        } finally{
            String FilePath = request.getSession().getServletContext().getRealPath("/");
            String path = FilePath + docName;   
            File file = new File(path);
            file.delete();
        }  
    }
	
	
	/**     
	* 创建人：陈永培   
	* 创建时间：2017-5-29 下午2:03:03
	* 功能说明：获取freemark模版的内容    
	*/
	public static String getTemplateContent(String ftlName,Map map) throws Exception{
		Configuration cfg =getConfiguration();
		Template template = cfg.getTemplate(ftlName);
		template.setEncoding("UTF-8");
		StringWriter sw=new StringWriter();
		template.process(map, sw);  
		//for循环 下面close方法不能写在finally方法里面，不然就没法结束流
		sw.flush();  
		sw.close();
		return sw.toString();
	}
	
	/**     
	* 创建人：陈永培   
	* 创建时间：2017-5-29 下午1:59:24
	* 功能说明：    
	*/
	public static Configuration getConfiguration() throws Exception{  
		try{
				cfg = new Configuration();
				String path =  FreeMarkerUtil.class.getClassLoader().getResource("").getPath();
				File templateDirFile = new File(URLDecoder.decode(path, "UTF-8"));
				cfg.setDirectoryForTemplateLoading(templateDirFile);
				cfg.setLocale(Locale.CHINA);
				cfg.setDefaultEncoding("UTF-8");
		}catch(Exception e){
			throw new Exception(e.getMessage());
		}
        return cfg;  
	} 
	
	public static String toISO8859_1(String in) {
		return toISO8859_1(in, "GBK");
	}

	/**   
	*    
	* 项目名称：UCG_OSS     
	* 创建人：王郎郎
	* 创建时间：2015-2-28 上午10:52:36   
	* 功能说明：将字符转化为适应不同环境编码
	* 修改人：王郎郎 
	* 修改时间：2015-2-28 上午10:52:36   
	* 修改备注：   
	* @version    
	*    
	*/ 
	public static String toISO8859_1(String in, String fromEncoding) {
		String strOut = null;
		if (in == null || in.trim().equals("")) {
			return in;
		}
		try {
			byte b[] = in.getBytes(fromEncoding);
			strOut = new String(b, "ISO8859_1");
		} catch (UnsupportedEncodingException unsupportedencodingexception) {
		}
		return strOut;
	}
	
	/**     
	* 创建人：陈永培   
	* 创建时间：2016-4-22 上午10:35:11
	* 功能说明：将图片转成BASE64Encoder，然后显示在word文档中
	*/
	public static String getImageStr(String imgPath) {
		if(StringUtil.isEmpty(imgPath))
			return "";
	    InputStream in = null;
	    byte[] data = null;
	    try {
	        in = new FileInputStream(imgPath);
	        data = new byte[in.available()];
	        in.read(data);
	        in.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    BASE64Encoder encoder = new BASE64Encoder();
	    return encoder.encode(data);
	}
	public static void main(String[] args) {
		String path =  FreeMarkerUtil.class.getClassLoader().getResource("").getPath();
		System.err.println(path);
	}
}

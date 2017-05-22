package com.ucg.util.freemark;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import sun.misc.BASE64Encoder;

import com.ucg.base.framework.core.util.StringUtil;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

public class FreeMarkerUtil {
	
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
}

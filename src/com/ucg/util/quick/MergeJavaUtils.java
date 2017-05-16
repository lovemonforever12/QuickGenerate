package com.ucg.util.quick;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.ucg.util.config.PropertiesUtil;
import com.ucg.util.file.FileUtil;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class MergeJavaUtils {

	private static String path="";
	private static String mergerJavafile = "";
	private static String mergerModelMethod="";
	
	private static  LinkedList<String> filePathList = new LinkedList<String>();
	
	public static void initConfig(){
		PropertiesUtil.loadPropertyFile("quickl.properties");
		mergerJavafile = PropertiesUtil.getProperty("mergerJavafile");
		path = PropertiesUtil.getProperty("path");
		mergerModelMethod = PropertiesUtil.getProperty("mergerModelMethod");
	}

	public static void mergerFile() throws Exception{
		try{
			StringBuffer mainJavaText= new StringBuffer(FileUtil.readTextFile(mergerJavafile));
			int indexBegin = mainJavaText.indexOf("class")+5;
			int indexEnd = mainJavaText.indexOf("extends");
			String entityName = mainJavaText.substring(indexBegin, indexEnd).trim();
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("entityName", entityName);
			map.put("entityLowerName", entityName.toLowerCase());
			map.put("createUser", System.getProperty("user.name"));
			map.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
			
			generateFile(GenEntityUtils.getTemplateList(mergerModelMethod), mergerJavafile, map,mainJavaText);
		}catch(Exception e){
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}finally{
			
		}
	}


	
	public static void  generateFile(List<String> templateList, String javafile,Object data, StringBuffer mainJavaText) throws Exception{
		if(templateList.size()==0){
			return;
		}
		Writer out = null;
		try{
			//判断文件夹是否存在
			if(!FileUtil.checkExist(path)){
				FileUtil.CreateDirectory(path);
			}
			
			for (int i = 0; i < templateList.size(); i++) {
				String ftlName=templateList.get(i);
				String filePath = path+ftlName.substring(0, ftlName.indexOf("."))+".java";
				filePathList.add(filePath);
				Configuration cfg =GenEntityUtils.getConfiguration();
				Template template = cfg.getTemplate(ftlName);
				template.setEncoding("UTF-8");
				out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), "UTF-8")); 
				template.process(data, out);  
				//for循环 下面close方法不能写在finally方法里面，不然就没法结束流
				out.flush();  
				out.close();
			}

			
			int indexOf = mainJavaText.indexOf(QuickConstant.flag);
			if(indexOf>-1){//如果有自动生成标记号的话，就在它前面加方法
				indexOf = mainJavaText.indexOf(QuickConstant.flag);
			}else{//没有直接加载最后面
				indexOf=mainJavaText.lastIndexOf("}");
			}
			StringBuffer begin=new StringBuffer(mainJavaText.substring(0,indexOf));
			StringBuffer end=new StringBuffer(mainJavaText.substring(indexOf,mainJavaText.length()));
			//这里添加其他的模板
			for (int i = 0; i < filePathList.size(); i++) {
				StringBuffer otherJavaText= new StringBuffer(FileUtil.readTextFile(filePathList.get(i)));
				begin.append("\n");
				begin.append(otherJavaText);
			}
			begin.append("\n");
			begin.append(end);
			FileUtil.saveFile(javafile, begin.toString());
			
		}catch(Exception e){
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}finally{
			//删除其他文件
			for (int i = 0; i < filePathList.size(); i++) {
				boolean deleteFlag = FileUtil.deleteFromName(filePathList.get(i));
				//System.out.println("文件["+filePathList.get(i)+"]删除"+(deleteFlag ==true?"成功":"失败"));
			}
		}
	}

	public static void main(String[] args) throws Exception {
		try {
			initConfig();
			mergerFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

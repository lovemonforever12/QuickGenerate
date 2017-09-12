package com.ucg.util.pdf;

public class Test {
	public static void main(String[] args) {
		String info ="";
		String sourcePath = "D://java//testFdd.docx";
        String destFile = "D://java//testFdd.pdf";   
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

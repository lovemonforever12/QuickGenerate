package com.ucg.util.app;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;

public class EditionCreateUtil {
	public static String appDownLoad="https://app.utrustfrg.com:10443/appVersionController.do?downLatestVersion&amp;appType=1";
	public static String appTestDownLoad="https://192.168.13.3:8443/GDFRGC_OSS/appVersionController.do?downLatestVersion&amp;appType=1";
	public static void createPlist(Map<String,String> mapApk) throws IOException{
        //这个地址应该是创建的服务器地址，在这里用生成到本地磁盘地址
        final String path = mapApk.get("path");
         
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String plist = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                 + "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n"
                 + "<plist version=\"1.0\">\n" + "<dict>\n"
                 + "<key>items</key>\n" 
                 + "<array>\n" 
                 + "<dict>\n"
                 + "<key>assets</key>\n" 
                 + "<array>\n" 
                 + "<dict>\n"
                 + "<key>kind</key>\n"
                 + "<string>software-package</string>\n"
                 + "<key>url</key>\n"
                 //你之前所上传的ipa文件路径         
                 + "<string>"+mapApk.get("downloadUrl")+"</string>\n"
                 + "</dict>\n" 
                 + "</array>\n"
                 + "<key>metadata</key>\n"
                 + "<dict>\n"
                 + "<key>bundle-identifier</key>\n";
                 //这个是开发者账号用户名，也可以为空，为空安装时看不到图标，完成之后可以看到
 				if(mapApk.get("CFBundleIdentifier")!=null){
 					plist+= "<string>"+mapApk.get("CFBundleIdentifier")+"</string>\n";
				}else{
					plist+= "<string></string>\n";
				}
                 
 				plist+= "<key>bundle-version</key>\n";
 				
 				if(mapApk.get("versionName")!=null){
 					plist+= "<string>"+mapApk.get("versionName")+"</string>\n";
				}else{
					plist+= "<string>1.0</string>\n";
				}
 				plist+= "<key>kind</key>\n"
                 + "<string>software</string>\n"
                 + "<key>subtitle</key>\n"
                 + "<string>下载</string>\n"
                 + "<key>title</key>\n";
  				if(mapApk.get("CFBundleDisplayName")!=null){
 					plist+= "<string>"+mapApk.get("CFBundleDisplayName")+"</string>\n";
				}else{
					plist+= "<string></string>\n";
				}
 				plist+= "</dict>\n" 
                 + "</dict>\n"
                 + "</array>\n"
                 + "</dict>\n"
                 + "</plist>";
        try {
            FileOutputStream output = new FileOutputStream(file);
            OutputStreamWriter writer;
            writer = new OutputStreamWriter(output, "UTF-8");
            writer.write(plist);
            writer.close();
            output.close();
        } catch (Exception e) {
            System.err.println("==========创建plist文件异常：" + e.getMessage());
        }
       
    }

}

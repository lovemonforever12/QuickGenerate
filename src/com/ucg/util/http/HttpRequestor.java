package com.ucg.util.http;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.jfinal.upload.UploadFile;
import com.ucg.base.framework.core.util.FileUtils;
import com.ucg.util.string.StringUtil;


public class HttpRequestor {
    
    private String charset = "utf-8";
    private Integer connectTimeout = null;
    private Integer socketTimeout = null;
    private String proxyHost = null;
    private Integer proxyPort = null;
    private String charseto = "utf-8";
    public  static HttpRequestor hr=null;
    
    /**
    * 创建人： 王郎郎
    * 创建时间：2015-12-2 下午10:33:15    
    * 说明：   下载远程文件
    * @param url
    * @param toSrc
    * @return
    * boolean
     * @throws Exception 
    */
    @SuppressWarnings("static-access")
	public String downloadFile(String url,String outPath) throws Exception{
    	String fileName = "fileName";
    	URL localURL = new URL(url);
        URLConnection connection = openConnection(localURL);
        HttpURLConnection httpURLConnection = (HttpURLConnection)connection;
        
        httpURLConnection.setRequestProperty("Accept-Charset", charset);
        httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        
        InputStream inputStream = null;
        
        if (httpURLConnection.getResponseCode() >= 300) {
            throw new Exception("HTTP Request is not success, Response code is " + httpURLConnection.getResponseCode());
        }
        inputStream = httpURLConnection.getInputStream();
        File outFile = new File(outPath);
        if(!outFile.exists()){
        	FileUtils.createFolder(outPath,false);
        }
		String contentDisposition = new String(connection.getHeaderField("Content-Disposition").getBytes("ISO-8859-1"), "utf-8");
		URLDecoder urlDecoder = new URLDecoder();
		contentDisposition=urlDecoder.decode(contentDisposition, charset);
		fileName =contentDisposition.replace("attachment;filename=", "");
        String toSrc = outPath + File.separator + fileName;
        if(!FileUtils.isFileExist(toSrc)){
        	new File(toSrc).createNewFile();
        }
        FileOutputStream fos = new FileOutputStream(new File(toSrc));
		byte[] bs = new byte[1024];
		int n = 0;
		while ((n = inputStream.read(bs)) != -1) {
			fos.write(bs, 0, n);
		}
        if (inputStream != null) {
            inputStream.close();
        }
        if (fos != null) {
        	fos.close();
        }
		return fileName;
    }
    
  
     /**     
    * 创建人：陈永培   
    * 创建时间：2017-6-9 下午7:53:01
    * 功能说明： 使用线程下载文件
    */
    public String downloadFileWithThread(String url,String outPath,int threadNum) throws Exception{
    	String errorUrl="";//错误的下载地址
         //开启多线程下载  
         MultiThreadManager mtDown=new MultiThreadManager(url,outPath,threadNum);  
         try {  
             mtDown.downLoad();  
         } catch (Exception ex) {
        	 errorUrl=url;
            throw new Exception("[下载路径]: "+url+" 下载错误");
         }
		return errorUrl;  
     }
    
    /**     
     * 创建人：陈永培   
     * 创建时间：2017-6-9 下午7:53:01
     * 功能说明： 使用线程下载多个文件文件
     */
     public List<String> downloadFileWithThread(List<String> urls,String outPath,int threadNum) throws Exception{
     	List<String> errorUrlList=new ArrayList<String>();
     	for (String url : urls) {
     		 String errorUrl = downloadFileWithThread(url, outPath, threadNum);
     		 if(StringUtil.isNotEmpty(errorUrlList)){
     			errorUrlList.add(errorUrl);
     		 }
		}
 		return errorUrlList;  
      }
     
    
    
    /**
     * Do GET request
     * @param url
     * @return
     * @throws Exception
     * @throws IOException
     */
    public String doGet(String url) throws Exception {
        
        URL localURL = new URL(url);
        URLConnection connection = openConnection(localURL);
        HttpURLConnection httpURLConnection = (HttpURLConnection)connection;
        
        httpURLConnection.setRequestProperty("Accept-Charset", charset);
        httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        httpURLConnection.setRequestProperty("contentType", "utf-8");
        
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        StringBuffer resultBuffer = new StringBuffer();
        String tempLine = null;
        
        if (httpURLConnection.getResponseCode() >= 300) {
            throw new Exception("HTTP Request is not success, Response code is " + httpURLConnection.getResponseCode());
        }
        
        try {
            inputStream = httpURLConnection.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream,getCharseto());
            reader = new BufferedReader(inputStreamReader);
            
            while ((tempLine = reader.readLine()) != null) {
                resultBuffer.append(tempLine);
            }
            
        } finally {
            
            if (reader != null) {
                reader.close();
            }
            
            if (inputStreamReader != null) {
                inputStreamReader.close();
            }
            
            if (inputStream != null) {
                inputStream.close();
            }
            
        }

        return resultBuffer.toString();
    }
    
    /**
     * Do POST request
     * @param url
     * @param parameterMap
     * @return
     * @throws Exception 
     */
    public String doPost(String url, Map parameterMap) throws Exception {
        
        /* Translate parameter map to parameter date string */
        StringBuffer parameterBuffer = new StringBuffer();
        if (parameterMap != null) {
            Iterator iterator = parameterMap.keySet().iterator();
            String key = null;
            String value = null;
            while (iterator.hasNext()) {
                key = (String)iterator.next();
                if (parameterMap.get(key) != null) {
                    value = (String)parameterMap.get(key);
                } else {
                    value = "";
                }
                
                parameterBuffer.append(key).append("=").append(value);
                if (iterator.hasNext()) {
                    parameterBuffer.append("&");
                }
            }
        }
        
        System.out.println("\r");
        System.out.println("----------------------------------post请求------------------------------------");
        System.out.println("POST URL : " + url);
        System.out.println("POST parameter : " + parameterBuffer.toString());
      
        
        URL localURL = new URL(url);
        
        URLConnection connection = openConnection(localURL);
        HttpURLConnection httpURLConnection = (HttpURLConnection)connection;
        
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("Accept-Charset", charset);
        httpURLConnection.setRequestProperty("contentType", "utf-8");
        httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        httpURLConnection.setRequestProperty("Content-Length", String.valueOf(parameterBuffer.length()));
        
        OutputStream outputStream = null;
        OutputStreamWriter outputStreamWriter = null;
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        StringBuffer resultBuffer = new StringBuffer();
        String tempLine = null;
        
        try {
            outputStream = httpURLConnection.getOutputStream();
            outputStreamWriter = new OutputStreamWriter(outputStream,getCharseto());
            
            outputStreamWriter.write(parameterBuffer.toString());
            outputStreamWriter.flush();
            
            if (httpURLConnection.getResponseCode() >= 300) {
                throw new Exception("HTTP Request is not success, Response code is " + httpURLConnection.getResponseCode());
            }
            
            inputStream = httpURLConnection.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream,getCharseto());
            reader = new BufferedReader(inputStreamReader);
            
            while ((tempLine = reader.readLine()) != null) {
                resultBuffer.append(tempLine);
            }
            
        } finally {
            
            if (outputStreamWriter != null) {
                outputStreamWriter.close();
            }
            
            if (outputStream != null) {
                outputStream.close();
            }
            
            if (reader != null) {
                reader.close();
            }
            
            if (inputStreamReader != null) {
                inputStreamReader.close();
            }
            
            if (inputStream != null) {
                inputStream.close();
            }
            
        }
        System.out.println("Return："+resultBuffer);
        System.out.println("----------------------------------------------------------------------\r");
        return resultBuffer.toString();
    }
    
    /**    
     * 项目名称：UCG_OSS     
     * 创建人：王郎郎
     * 创建时间：2015-9-27 下午09:21:59   
     * 功能说明：post string
     * 修改人： 王郎郎
     * 修改时间：2015-9-27 下午09:21:59    
     * 修改备注：   
     * @version       
     */
     public String doPost(String url, String str) throws Exception {
         
         URL localURL = new URL(url);
         
         URLConnection connection = openConnection(localURL);
         HttpURLConnection httpURLConnection = (HttpURLConnection)connection;
         
         httpURLConnection.setDoOutput(true);
         httpURLConnection.setRequestMethod("POST");
         httpURLConnection.setRequestProperty("Accept-Charset", getCharset());
         httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
         httpURLConnection.setRequestProperty("contentType", "utf-8");
         httpURLConnection.setRequestProperty("Content-Length", String.valueOf(str.length()));
         
         OutputStream outputStream = null;
         OutputStreamWriter  outputStreamWriter = null;
         InputStream inputStream = null;
         InputStreamReader inputStreamReader = null;
         BufferedReader reader = null;
         StringBuffer resultBuffer = new StringBuffer();
         String tempLine = null;
         
         try {
             outputStream = httpURLConnection.getOutputStream();
             outputStreamWriter = new OutputStreamWriter(outputStream,getCharseto());
             outputStreamWriter.write(str);
             outputStreamWriter.flush();
             
             if (httpURLConnection.getResponseCode() >= 300) {
                 throw new Exception("HTTP Request is not success, Response code is " + httpURLConnection.getResponseCode());
             }
             
             inputStream = httpURLConnection.getInputStream();
             inputStreamReader = new InputStreamReader(inputStream,getCharseto());
             reader = new BufferedReader(inputStreamReader);
             
             while ((tempLine = reader.readLine()) != null) {
                 resultBuffer.append(tempLine);
             }
             
         } finally {
             
             if (outputStreamWriter != null) {
                 outputStreamWriter.close();
             }
             
             if (outputStream != null) {
                 outputStream.close();
             }
             
             if (reader != null) {
                 reader.close();
             }
             
             if (inputStreamReader != null) {
                 inputStreamReader.close();
             }
             
             if (inputStream != null) {
                 inputStream.close();
             }
             
         }

         return resultBuffer.toString();
     }
    
    /** 
     * 上传图片 
     * @param urlStr 
     * @param textMap 
     * @param fileMap 
     * @return 
     */  
    public  String formUpload(String urlStr, Map<String, String> textMap, Map<String, UploadFile> fileMap) {  
        String res = "";  
        HttpURLConnection conn = null;  
        String BOUNDARY = "---------------------------123821742118716"; //boundary就是request头和上传文件内容的分隔符    
        try {  
            URL url = new URL(urlStr);  
            conn = (HttpURLConnection) url.openConnection();  
            conn.setConnectTimeout(5000);  
            conn.setReadTimeout(30000);  
            conn.setDoOutput(true);  
            conn.setDoInput(true);  
            conn.setUseCaches(false);  
            conn.setRequestMethod("POST");  
            conn.setRequestProperty("Connection", "Keep-Alive");  
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");  
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);  
  
            OutputStream out = new DataOutputStream(conn.getOutputStream());  
            // text    
            if (textMap != null) {  
                StringBuffer strBuf = new StringBuffer();  
                Iterator<Map.Entry<String, String>> iter = textMap.entrySet().iterator();  
                while (iter.hasNext()) {  
                    Map.Entry<String, String> entry = iter.next();  
                    String inputName = (String) entry.getKey();  
                    String inputValue = (String) entry.getValue();  
                    if (inputValue == null) {  
                        continue;  
                    }  
                    strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");  
                    strBuf.append("Content-Disposition: form-data; name=\"" + inputName + "\"\r\n\r\n");  
                    strBuf.append(inputValue);  
                }  
                out.write(strBuf.toString().getBytes(getCharseto()));  
            }  
  
            // file    
            if (fileMap != null) {  
                Iterator<Map.Entry<String, UploadFile>> iter = fileMap.entrySet().iterator();  
                while (iter.hasNext()) {  
                    Map.Entry<String, UploadFile> entry = iter.next();  
                    String inputName = (String) entry.getKey();  
                    UploadFile inputValue = entry.getValue();  
                    if (inputValue == null) {  
                        continue;  
                    }  
                    File file = inputValue.getFile();  
                    String filename = file.getName();  
                    String contentType = inputValue.getContentType();  
  
                    StringBuffer strBuf = new StringBuffer();  
                    strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");  
                    strBuf.append("Content-Disposition: form-data; name=\"" + inputName + "\"; filename=\"" + filename + "\"\r\n");  
                    strBuf.append("Content-Type:" + contentType + "\r\n\r\n");  
  
                    out.write(strBuf.toString().getBytes(getCharseto()));  
  
                    DataInputStream in = new DataInputStream(new FileInputStream(file));  
                    int bytes = 0;  
                    byte[] bufferOut = new byte[1024];  
                    while ((bytes = in.read(bufferOut)) != -1) {  
                        out.write(bufferOut, 0, bytes);  
                    }  
                    in.close();  
                }  
            }  
  
            byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();  
            out.write(endData);  
            out.flush();  
            out.close();  
  
            // 读取返回数据    
            StringBuffer strBuf = new StringBuffer();  
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(),getCharseto()));  
            String line = null;  
            while ((line = reader.readLine()) != null) {  
                strBuf.append(line).append("\n");  
            }  
            res = strBuf.toString();  
            reader.close();  
            reader = null;  
        } catch (Exception e) {  
            System.out.println("发送POST请求出错。" + urlStr);  
            e.printStackTrace();  
        } finally {  
            if (conn != null) {  
                conn.disconnect();  
                conn = null;  
            }  
        }  
        return res;  
    }

    private URLConnection openConnection(URL localURL) throws IOException {
        URLConnection connection;
        if (proxyHost != null && proxyPort != null) {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
            connection = localURL.openConnection(proxy);
        } else {
            connection = localURL.openConnection();
        }
        return connection;
    }
    
    /**
     * Render request according setting
     * @param request
     */
    private void renderRequest(URLConnection connection) {
        
        if (connectTimeout != null) {
            connection.setConnectTimeout(connectTimeout);
        }
        
        if (socketTimeout != null) {
            connection.setReadTimeout(socketTimeout);
        }
        
    }

    /*
     * Getter & Setter
     */
    public Integer getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public Integer getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(Integer socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public Integer getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(Integer proxyPort) {
        this.proxyPort = proxyPort;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }
    
    public String getCharseto() {
		return charseto;
	}
	public void setCharseto(String charseto) {
		this.charseto = charseto;
	}
    
}
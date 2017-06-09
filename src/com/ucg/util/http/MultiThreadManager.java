package com.ucg.util.http;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;

import com.ucg.base.framework.core.util.FileUtils;

public class MultiThreadManager {
	
	private String urlPath;
	private String filePath;
	
	private int threadNum;
	private int fileSize;
	
	private DownThreads[] threads;
	
	public MultiThreadManager(String urlPath,String filePath,int threadNum)
	{
		this.urlPath=urlPath;
		this.filePath=filePath;
		this.threadNum=threadNum;
		threads=new DownThreads[threadNum];
	}
	@SuppressWarnings("static-access")
	public void downLoad() throws Exception
	{
		URL url=new URL(urlPath);
		HttpURLConnection connection=(HttpURLConnection)url.openConnection();
		 connection.setRequestMethod("GET");  
         connection.setRequestProperty("Accept-Encoding", "identity"); 
         fileSize = Integer.parseInt(connection.getHeaderField("Content_Length"));
		connection.disconnect();
		//分配每个线程需要在下载的文件块大小
		int currentPartSize=fileSize/threadNum+1;
		//要用RandomAccessFile来写入文件，因为它支持随机访问模式，即程序可以直接跳转至文件的任意位置来读写
		String contentDisposition = new String(connection.getHeaderField("Content-Disposition").getBytes("ISO-8859-1"), "utf-8");
		URLDecoder urlDecoder = new URLDecoder();
		contentDisposition=urlDecoder.decode(contentDisposition, "utf-8");
		String fileName =contentDisposition.replace("attachment;filename=", "");
        String toSrc = filePath + File.separator + fileName;
        File outFile = new File(filePath);
        if(!outFile.exists()){
        	FileUtils.createFolder(filePath,false);
        }
        
        if(!FileUtils.isFileExist(toSrc)){
        	new File(toSrc).createNewFile();
        }
		RandomAccessFile raFile=new RandomAccessFile(toSrc,"rw");
		//记录要写入文件的大小
		raFile.setLength(fileSize);
		raFile.close();
		for(int i=0;i<threadNum;i++)
		{
			int startPosition=i*currentPartSize;
			RandomAccessFile currentPartFile=new RandomAccessFile(toSrc,"rw");
			//将文件记录指针跳转至我们需要分割的位置
			currentPartFile.seek(startPosition);
			threads[i]=new DownThreads(startPosition,currentPartSize,currentPartFile,urlPath);
			threads[i].start();
		}	
	}
	//得到当前下载完成百分比，用于绘制进度条
	public int getCompleteRate()
	{
		int sumSize=0;
		for(int i=0;i<threadNum;i++)
		{
			sumSize+=threads[i].hasDownSize;
		}
		return (int) ((sumSize*1.0/fileSize)*100);
	}

}
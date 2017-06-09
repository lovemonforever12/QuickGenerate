package com.ucg.util.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownThreads extends Thread{
	//下载线程
	public int hasDownSize;
	private int startPosition;
	private int currentPartSize;
	
	private RandomAccessFile currentPartFile;
	
	private String urlPath;

	public DownThreads(int startPosition,int currentPartSize,RandomAccessFile currentPartFile,String urlPath)
	{
		this.startPosition=startPosition;
		this.currentPartSize=currentPartSize;
		this.currentPartFile=currentPartFile;
		this.urlPath=urlPath;
	}
	public void run()
	{
		try {
			URL url=new URL(urlPath);
			HttpURLConnection connection=(HttpURLConnection)url.openConnection();
			connection.setRequestMethod("GET");
			InputStream in=connection.getInputStream();
			in.skip(this.startPosition);
			//设置缓冲区大小为4是为了提高运行速度
			byte[] buffer=new byte[4];
			int hasRead;
			while(hasDownSize<currentPartSize&&((hasRead=in.read(buffer))!=-1))
			{
				//写入文件操作
				currentPartFile.write(buffer, 0, hasRead);
				hasDownSize+=hasRead;
			}
			currentPartFile.close();
			in.close();
		   } catch (IOException e) {
			e.printStackTrace();
	       }
			
	}
}
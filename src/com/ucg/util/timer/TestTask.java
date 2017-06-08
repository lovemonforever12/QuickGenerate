package com.ucg.util.timer;

import org.apache.log4j.Logger;

//import com.aspboy.jxc.tcp.SocketClient;

public class TestTask implements Runnable
{
	
	private String taskName;
	Object o=new Object();
	
	public TestTask() {
		super();
	}
	
	public TestTask(String taskName) {
		super();
		this.taskName = taskName;
	}

	private static int count=0;
	static Logger Log = Logger.getLogger(TestTask.class.getName());

	@Override
	public void run() {
		System.out.println("");
	
		synchronized (o) {
			int countFlag = count++;
			System.out.println(""+taskName+"--------["+countFlag+"]");
				
		}
	}
	

}

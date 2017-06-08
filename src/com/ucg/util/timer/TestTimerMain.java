package com.ucg.util.timer;

public abstract class TestTimerMain {

	 public static final long SECOND = 1000L;  
	/**     
	 * 创建人：陈永培   
	 * 创建时间：2017-5-29 下午2:55:37
	 * 功能说明：    
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
  
        TestTask testtask = new TestTask("任务1");  
        TestTask testtask2 = new TestTask("任务2");  
        TaskEngine.scheduleTask(1,testtask, 0, 1);  
        TaskEngine.scheduleTask(2,testtask2, 0, 1);  
        TaskEngine.start();  
        Thread.sleep(100000);
	}

}

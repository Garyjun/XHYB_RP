package com.brainsoon.resource.support;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * 批量导入任务
 * @author zuo
 *
 */
public class FtpTaskQueue {
	
	private BlockingQueue<FtpFileIds> queueFtp;	
	private int maxQueueSize = 10000;
	
	
	private static class ContextQueueHolder {
		private static FtpTaskQueue instance = buildSingleton();
		
		private static FtpTaskQueue buildSingleton() {			
			return new FtpTaskQueue();
		}
	}

	public static FtpTaskQueue getInst() {
		return ContextQueueHolder.instance;
	}
	
	private FtpTaskQueue() {
		this.queueFtp = new LinkedBlockingQueue<FtpFileIds>(maxQueueSize);
	}
	
	/**
	 * 添加队列
	 * @param message
	 */
	public void addMessage(FtpFileIds message) {
		queueFtp.offer(message);
	}
	
	/**
	 * 获取队列
	 * @return
	 * @throws InterruptedException
	 */
	public FtpFileIds getMessage()
		throws InterruptedException {
		FtpFileIds info = queueFtp.take(); //take为线程阻塞方法
		return info;
	}
	
	public int size() {
		return queueFtp.size();
	}
	
	public Object[] getQueueObj(){
		return queueFtp.toArray();
	}
	
	public void setMaxQueueSize(int maxQueueSize) {
		this.maxQueueSize = maxQueueSize;
	}
	
	public int getMaxQueueSize() {
		return maxQueueSize;
	}
}

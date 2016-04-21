package com.brainsoon.resource.support;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * 拷贝资源文件队列任务
 * @author zuo
 *
 */
public class DoCheckCopyFileQueue {
	
	private BlockingQueue<String> queue;	
	private int maxQueueSize = 10000;
	
	
	private static class ContextQueueHolder {
		private static DoCheckCopyFileQueue instance = buildSingleton();
		
		private static DoCheckCopyFileQueue buildSingleton() {			
			return new DoCheckCopyFileQueue();
		}
	}

	public static DoCheckCopyFileQueue getInst() {
		return ContextQueueHolder.instance;
	}
	
	private DoCheckCopyFileQueue() {
		this.queue = new LinkedBlockingQueue<String>(maxQueueSize);
	}
	
	/**
	 * 添加队列
	 * @param message
	 */
	public void addMessage(String message) {
		queue.offer(message);
	}
	
	/**
	 * 获取队列
	 * @return
	 * @throws InterruptedException
	 */
	public String getMessage()
		throws InterruptedException {
		String info = queue.take(); //take为线程阻塞方法
		return info;
	}
	
	public int size() {
		return queue.size();
	}
	
	public Object[] getQueueObj(){
		return queue.toArray();
	}
	
	public void setMaxQueueSize(int maxQueueSize) {
		this.maxQueueSize = maxQueueSize;
	}
	
	public int getMaxQueueSize() {
		return maxQueueSize;
	}
}

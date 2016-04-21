package com.brainsoon.resource.support;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * 批量导入任务
 * @author zuo
 *
 */
public class TaskQueue {
	
	private BlockingQueue<ImportResExcelFile> queue;	
	private int maxQueueSize = 10000;
	
	
	private static class ContextQueueHolder {
		private static TaskQueue instance = buildSingleton();
		
		private static TaskQueue buildSingleton() {			
			return new TaskQueue();
		}
	}

	public static TaskQueue getInst() {
		return ContextQueueHolder.instance;
	}
	
	private TaskQueue() {
		this.queue = new LinkedBlockingQueue<ImportResExcelFile>(maxQueueSize);
	}
	
	/**
	 * 添加队列
	 * @param message
	 */
	public void addMessage(ImportResExcelFile message) {
		queue.offer(message);
	}
	
	/**
	 * 获取队列
	 * @return
	 * @throws InterruptedException
	 */
	public ImportResExcelFile getMessage()
		throws InterruptedException {
		ImportResExcelFile info = queue.take(); //take为线程阻塞方法
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

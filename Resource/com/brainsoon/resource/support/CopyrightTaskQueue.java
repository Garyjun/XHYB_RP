package com.brainsoon.resource.support;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * 批量导入任务
 * @author zuo
 *
 */
public class CopyrightTaskQueue {
	
	private BlockingQueue<ImportData> queue;	
	private int maxQueueSize = 10000;
	
	
	private static class ContextQueueHolder {
		private static CopyrightTaskQueue instance = buildSingleton();
		
		private static CopyrightTaskQueue buildSingleton() {			
			return new CopyrightTaskQueue();
		}
	}

	public static CopyrightTaskQueue getInst() {
		return ContextQueueHolder.instance;
	}
	
	private CopyrightTaskQueue() {
		this.queue = new LinkedBlockingQueue<ImportData>(maxQueueSize);
	}
	
	/**
	 * 添加队列
	 * @param message
	 */
	public void addMessage(ImportData importData) {
		queue.offer(importData);
	}
	
	/**
	 * 获取队列
	 * @return
	 * @throws InterruptedException
	 */
	public ImportData getMessage()
		throws InterruptedException {
		ImportData info = queue.take();
		return info;
	}
	
	public int size() {
		return queue.size();
	}
	
	public void setMaxQueueSize(int maxQueueSize) {
		this.maxQueueSize = maxQueueSize;
	}
	
	public int getMaxQueueSize() {
		return maxQueueSize;
	}
}

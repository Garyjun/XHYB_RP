package com.brainsoon.resrelease.processThread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.brainsoon.resrelease.po.ResOrder;

/**
 * <dl>
 * <dt>TaskInfo</dt>
 * <dd>Description:加工文件任务队列</dd>
 * <dd>CreateDate: 2015年1月15日 下午5:29:32</dd>
 * </dl>
 * 
 * @author xiehewei
 */

public class FileTaskQueue {

	private BlockingQueue<FileTaskInfo> queue;	
	private int maxQueueSize = 5;
	
	
	private static class ContextQueueHolder {
		private static FileTaskQueue instance = buildSingleton();
		
		private static FileTaskQueue buildSingleton() {			
			return new FileTaskQueue();
		}
	}

	public static FileTaskQueue getInst() {
		return ContextQueueHolder.instance;
	}
	
	private FileTaskQueue() {
		this.queue = new LinkedBlockingQueue<FileTaskInfo>(maxQueueSize);
	}
	
	/**
	 * 添加队列
	 * @param message
	 */
	public void addMessage(FileTaskInfo message) {
		queue.offer(message);
	}
	
	/**
	 * 获取队列
	 * @return
	 * @throws InterruptedException
	 */
	public FileTaskInfo getMessage()
		throws InterruptedException {
		FileTaskInfo info = queue.take(); //take为线程阻塞方法
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

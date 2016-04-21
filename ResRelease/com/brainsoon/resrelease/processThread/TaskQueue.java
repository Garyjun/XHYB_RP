package com.brainsoon.resrelease.processThread;


import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * <dl>
 * <dt>TaskQueue</dt>
 * <dd>Description:加工任务队列</dd>
 * <dd>CreateDate: 2015年1月15日 下午5:29:32</dd>
 * </dl>
 * 
 * @author xiehewei
 */

public class TaskQueue {
	private Queue<TaskInfo> queue;	
	private int maxQueueSize = 10000;
	
	private static class TaskQueueHolder {
		private static TaskQueue instance = buildSingleton();
		
		private static TaskQueue buildSingleton() {			
			return new TaskQueue();
		}
	}

	public static TaskQueue getInst() {
		return TaskQueueHolder.instance;
	}
	
	public TaskQueue() {
		this.queue = new LinkedBlockingQueue<TaskInfo>(maxQueueSize);

	}
	
	public void addMessage(TaskInfo message) {
		queue.offer(message);
	}
	
	public TaskInfo getMessage(int timeoutInMillionSeconds)
		throws InterruptedException {
		if (queue.isEmpty()) {
			Thread.sleep(timeoutInMillionSeconds);			
		}
		/*if (queue.isEmpty()) {
			throw new InterruptedException("wait timeout");
		}*/
		return queue.poll();
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

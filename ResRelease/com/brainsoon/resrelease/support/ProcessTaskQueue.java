package com.brainsoon.resrelease.support;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.brainsoon.resrelease.po.ResReleaseDetail;

public class ProcessTaskQueue {

	private BlockingQueue<ResReleaseDetail> queue;	
	private int maxQueueSize = 5;
	
	
	private static class ContextQueueHolder {
		private static ProcessTaskQueue instance = buildSingleton();
		
		private static ProcessTaskQueue buildSingleton() {			
			return new ProcessTaskQueue();
		}
	}

	public static ProcessTaskQueue getInst() {
		return ContextQueueHolder.instance;
	}
	
	private ProcessTaskQueue() {
		this.queue = new LinkedBlockingQueue<ResReleaseDetail>(maxQueueSize);
	}
	
	/**
	 * 添加队列
	 * @param message
	 */
	public void addMessage(ResReleaseDetail message) {
		queue.offer(message);
	}
	
	/**
	 * 获取队列
	 * @return
	 * @throws InterruptedException
	 */
	public ResReleaseDetail getMessage()
		throws InterruptedException {
		ResReleaseDetail info = queue.take(); //take为线程阻塞方法
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

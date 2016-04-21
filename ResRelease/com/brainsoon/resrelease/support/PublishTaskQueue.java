package com.brainsoon.resrelease.support;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
/**
 * @ClassName: PublishTaskQueue
 * @Description: 发布队列
 * @author xiehewei
 * @date 2014年12月16日 下午2:54:11
 *
 */
public class PublishTaskQueue {
	private BlockingQueue<PublishResOrder> queue;	
	private int maxQueueSize = 10000;
	
	
	private static class ContextQueueHolder {
		private static PublishTaskQueue instance = buildSingleton();
		
		private static PublishTaskQueue buildSingleton() {			
			return new PublishTaskQueue();
		}
	}

	public static PublishTaskQueue getInst() {
		return ContextQueueHolder.instance;
	}
	
	private PublishTaskQueue() {
		this.queue = new LinkedBlockingQueue<PublishResOrder>(maxQueueSize);
	}
	
	/**
	 * 添加队列
	 * @param message
	 */
	public void addMessage(PublishResOrder message) {
		queue.offer(message);
	}
	
	/**
	 * 获取队列
	 * @return
	 * @throws InterruptedException
	 */
	public PublishResOrder getMessage()
		throws InterruptedException {
		PublishResOrder info = queue.take(); //take为线程阻塞方法
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

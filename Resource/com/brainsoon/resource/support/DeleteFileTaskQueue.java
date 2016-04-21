package com.brainsoon.resource.support;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * 批量删除任务
 * @author zuo
 *
 */
public class DeleteFileTaskQueue {
	
	private BlockingQueue<DeleteResFileForCaIds> queue;	
	private int maxQueueSize = 10000;
	
	
	private static class ContextQueueHolder {
		private static DeleteFileTaskQueue instance = buildSingleton();
		
		private static DeleteFileTaskQueue buildSingleton() {			
			return new DeleteFileTaskQueue();
		}
	}

	public static DeleteFileTaskQueue getInst() {
		return ContextQueueHolder.instance;
	}
	
	private DeleteFileTaskQueue() {
		this.queue = new LinkedBlockingQueue<DeleteResFileForCaIds>(maxQueueSize);
	}
	
	/**
	 * 添加队列
	 * @param message
	 */
	public void addMessage(DeleteResFileForCaIds message) {
		queue.offer(message);
	}
	
	/**
	 * 获取队列
	 * @return
	 * @throws InterruptedException
	 */
	public DeleteResFileForCaIds getMessage()
		throws InterruptedException {
		DeleteResFileForCaIds info = queue.take(); //take为线程阻塞方法
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

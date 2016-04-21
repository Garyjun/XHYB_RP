package com.brainsoon.resrelease.processThread;


import java.util.List;

import org.apache.log4j.Logger;
/**
 * <dl>
 * <dt>DataBaseMultiThread</dt>
 * <dd>Description:数据处理多线程基类</dd>
 * <dd>CreateDate: 2015年1月15日 下午5:29:32</dd>
 * </dl>
 * 
 * @author xiehewei
 */

public abstract class DataBaseMultiThread {
	protected final Logger logger = Logger.getLogger(getClass());
	protected TaskQueue queue;
	protected int poolSize = 5;
	protected IThreadPool<DataBaseThread> threadPool;
	protected List<ICollectionTaskListener> listenerList;	
	protected Checker checker;
	
	public abstract void start();
	
	public abstract void restartDeadThread();

	public abstract String getThreadGroupName();
	
	public TaskQueue getTaskQueue() {
		return queue;
	}
	
	public int getPoolSize() {
		return poolSize;
	}
	
	public void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
	}
	
	public IThreadPool<DataBaseThread> getThreadPool() {
		return threadPool;
	}
	
	public int getActiveThreadCount() {
		return threadPool.getActiveThreadCount();
	}
	
	public void addTask(TaskInfo taskInfo) {
		queue.addMessage(taskInfo);
	}
	
	class Checker extends Thread {
		private long sleepTime = 5 * 60 * 1000;
		//private long sleepTime = 60 * 1000;

		public Checker() {
			this.setDaemon(true);
		}

		@Override
		public void run() {			
			while (true) {
				try {					
					int queueSize = getTaskQueue().size();
					String threadGroupName = getThreadGroupName();
					int activeThreadCount = getActiveThreadCount();
					
					logger.info("当前队列长度: " + queueSize + "; 当前活动线程数: " 
							+ activeThreadCount + "; 线程组名: " + threadGroupName);				
					
					if (activeThreadCount < getPoolSize()) {
						logger.error("线程池部分线程宕掉！重新启动宕掉的线程，线程组名: " 
								+ threadGroupName);
						restartDeadThread();
					}
					
					if (queueSize >= getTaskQueue().getMaxQueueSize())	
						logger.warn("队列消息堆积过多！当前队列长度: " + queueSize 
								+ "; 线程组名: " + threadGroupName);			
					
					Thread.sleep(sleepTime);
				} catch (Exception e) {
					
				}
			}
		}

		public long getSleepTime() {
			return sleepTime;
		}

		public void setSleepTime(long sleepTime) {
			this.sleepTime = sleepTime;
		}
	}
}

package com.brainsoon.resrelease.processThread;

import org.apache.log4j.Logger;
import com.brainsoon.resrelease.processThread.threadException.ThreadException;


/**
 * <dl>
 * <dt>DataBaseThread</dt>
 * <dd>Description:数据处理单线程基类</dd>
 * <dd>CreateDate: 2015年1月15日 下午5:29:32</dd>
 * </dl>
 * 
 * @author xiehewei
 */

public abstract class DataBaseThread extends Thread {
	protected final Logger logger = Logger.getLogger(getClass());
				
	private boolean isRunnable = true;			
	
	protected TaskQueue queue;
	
	@Override
	public void run() {
		TaskInfo taskInfo = null;
		while(isRunnable()){				
			try {
				taskInfo = queue.getMessage(1000);
				
				if(taskInfo == null)
				   continue;
				
				try
				{
					processTask(taskInfo);					
				} catch(ThreadException e) {					
					logger.error("线程[" + getThreadName() +"]任务处理异常：" + e.getMessage());
				} catch (Throwable e) {
					logger.error("线程[" + getThreadName() + "]执行异常!", e);
				}				
				
			} catch (InterruptedException e) {
				logger.error("线程[" + getThreadName() + "]异常中断!", e);
			} 			
		}
	}
	
	public abstract String getThreadName();
	
	public void setTaskQueue(TaskQueue queue) {
		this.queue = queue;
	}
	
	public void startThread() {
		isRunnable = true;
	}
	
	public void stopThread() {
		isRunnable = false;
	}
	
	public boolean isRunnable() {
		return isRunnable;
	}
	
	protected abstract void processTask(TaskInfo taskInfo) throws ThreadException;
	
}

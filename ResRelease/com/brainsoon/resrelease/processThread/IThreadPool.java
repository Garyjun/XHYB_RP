package com.brainsoon.resrelease.processThread;


import java.util.List;

import com.brainsoon.resrelease.processThread.threadException.ThreadNotFoundException;
import com.brainsoon.resrelease.processThread.threadException.ThreadPoolFullException;


/**
 * <dl>
 * <dt>IThreadPool</dt>
 * <dd>Description:线程池接口</dd>
 * <dd>CreateDate: 2015年1月15日 下午5:29:32</dd>
 * </dl>
 * 
 * @author xiehewei
 */

public interface IThreadPool<T extends DataBaseThread> {

	public abstract void addThread(T thread) throws ThreadPoolFullException;
	
	public abstract boolean isFull();	
	
	public abstract int getThreadCount();
	
	public abstract int getActiveThreadCount();
	
	public abstract void removeThread(String threadName);
	
	public abstract T getThread(String threadName) throws ThreadNotFoundException;
	
	public abstract List<T> getDeadThread();
	
	public abstract List<T> getThread();
	
}

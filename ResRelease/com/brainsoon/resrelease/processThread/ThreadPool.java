﻿package com.brainsoon.resrelease.processThread;


import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.brainsoon.resrelease.processThread.threadException.ThreadNotFoundException;
import com.brainsoon.resrelease.processThread.threadException.ThreadPoolFullException;

/**
 * <dl>
 * <dt>ThreadPool</dt>
 * <dd>Description:线程池</dd>
 * <dd>CreateDate: 2015年1月15日 下午5:29:32</dd>
 * </dl>
 * 
 * @author xiehewei
 */

public class ThreadPool<T extends DataBaseThread> implements IThreadPool<T> {
	private Map<String, T> threadPool = new Hashtable<String, T>();
	
	private String poolName;
	
	private int poolSize;
	
	private TaskQueue queue;
	
	public ThreadPool(String poolName, int poolSize, TaskQueue queue) {
		this.poolName = poolName;
		this.poolSize = poolSize;	
		this.queue = queue;
	}
	
	public String getPoolName() {
		return this.poolName;
	}
	
	public TaskQueue getTaskQueue() {
		return this.queue;
	}
	
	public synchronized void addThread(T thread) throws ThreadPoolFullException	{
		if(thread == null) {
			throw new NullPointerException("加入线程池的线程对象为空");
		}
		
		if(isFull()) {
			throw new ThreadPoolFullException();
		}
		thread.setTaskQueue(getTaskQueue());
		threadPool.put(thread.getThreadName(), thread);
	}
	
	public synchronized void removeThread(String threadName) {
		if(threadPool.containsKey(threadName)) {
			threadPool.remove(threadName);
		}
	}
	
	public synchronized boolean isFull() {
		return (threadPool.size() >= poolSize);
	}
	
	public synchronized int getThreadCount() {
		return threadPool.size();
	}
	
	public synchronized int getActiveThreadCount() {
		int activeThreadCount = getThreadCount();
		for(T thread : threadPool.values())	{
			if (!thread.isAlive())
				activeThreadCount--;
		}
		return activeThreadCount;
	}
	
	public synchronized T getThread(String threadName) throws ThreadNotFoundException {
		if(threadPool.containsKey(threadName)) {
			return threadPool.get(threadName);
		}
		
		throw new ThreadNotFoundException("Thread name is: " + threadName);
	}
	
	public synchronized List<T> getDeadThread()	{
		List<T> list = new ArrayList<T>();
		for(T thread : threadPool.values())	{
			if(!thread.isAlive())
			   list.add(thread);
		}
		return list;
	}
	
	public synchronized List<T> getThread()	{
		List<T> list = new ArrayList<T>();
		for(T thread : threadPool.values())	{
			list.add(thread);
		}
		return list;
	}
}

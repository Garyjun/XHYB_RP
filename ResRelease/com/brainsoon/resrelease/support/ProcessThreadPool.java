package com.brainsoon.resrelease.support;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ProcessThreadPool {

	private static int corePoolSize = 1;   //线程池维护线程的最少数量 
    private static int maximumPoolSize = 10;  //线程池维护线程的最大数量
    private static int keepAliveTime = 30; //线程池维护线程所允许的空闲时间
	static private ProcessThreadPool threadFixedPool = new ProcessThreadPool();
	private ExecutorService executor;
	public ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(20);
	static public ProcessThreadPool getFixedInstance() {
	      return threadFixedPool;
	}
    public ProcessThreadPool() {
    	 executor = new ThreadPoolExecutor(corePoolSize,maximumPoolSize,keepAliveTime,TimeUnit.MINUTES, queue,Executors.defaultThreadFactory(),new ThreadPoolExecutor.AbortPolicy());
	}
    public void execute(Runnable r) {
    	executor.execute(r);
    }
}

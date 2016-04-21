package com.brainsoon.common.util.dofile.service.test;

import java.io.Serializable;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.brainsoon.common.util.dofile.conver.ConverThreadPool;

public class TestThreadPool {

	private static int produceTaskSleepTime = 2;
	private static int consumeTaskSleepTime = 2000;
	private static int produceTaskMaxNumber = 10;

	
	
	private static int corePoolSize = 1;   //线程池维护线程的最少数量 
    private static int maximumPoolSize = 10;  //线程池维护线程的最大数量
    private static int keepAliveTime = 30; //线程池维护线程所允许的空闲时间
	static private ConverThreadPool converThreadPool = new ConverThreadPool();
	private ExecutorService executor;
	public ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(20);
	
	 public ConverThreadPool getThreadPoolInstance() {
		  executor = new ThreadPoolExecutor(corePoolSize,maximumPoolSize,keepAliveTime,TimeUnit.MINUTES, queue,Executors.defaultThreadFactory(),new ThreadPoolExecutor.AbortPolicy());
	      return converThreadPool;
	}
	
//    public ConverThreadPool() {
//    	 executor = new ThreadPoolExecutor(corePoolSize,maximumPoolSize,keepAliveTime,TimeUnit.MINUTES, queue,Executors.defaultThreadFactory(),new ThreadPoolExecutor.AbortPolicy());
//	}
    public void execute(Runnable r) {
    	executor.execute(r);
    }
	public static void main(String[] args) {

		// 构造一个线程池
		ThreadPoolExecutor threadPool = new ThreadPoolExecutor(2, 4, 3,
				TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(3),
				new ThreadPoolExecutor.DiscardOldestPolicy());

		for (int i = 1; i <= produceTaskMaxNumber; i++) {
			try {
				// 产生一个任务，并将其加入到线程池
				String task = "task@ " + i;
				System.out.println("put " + task);
				threadPool.execute(new ThreadPoolTask(task));

				// 便于观察，等待一段时间
				Thread.sleep(produceTaskSleepTime);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 线程池执行的任务
	 * 
	 * @author hdpan
	 */
	public static class ThreadPoolTask implements Runnable, Serializable {
		private static final long serialVersionUID = 0;
		// 保存任务所需要的数据
		private Object threadPoolTaskData;

		ThreadPoolTask(Object tasks) {
			this.threadPoolTaskData = tasks;
		}

		public void run() {
			// 处理一个任务，这里的处理方式太简单了，仅仅是一个打印语句
			System.out.println("start .." + threadPoolTaskData);
			try {
				// //便于观察，等待一段时间
				Thread.sleep(consumeTaskSleepTime);
			} catch (Exception e) {
				e.printStackTrace();
			}
			threadPoolTaskData = null;
		}

		public Object getTask() {
			return this.threadPoolTaskData;
		}
	}
}
package com.brainsoon.resrelease.processThread;

import java.util.LinkedList;

/**
 * @ClassName: WorkQueue
 * @Description: TODO
 * @author xiehewei
 * @date 2015年5月27日 下午12:44:34
 *
 */
public class WorkQueue {
	private final int nThreads;
	private final PoolWorker[] threads;
	private final LinkedList queue;

	public WorkQueue(int nThreads) {
		this.nThreads = nThreads;
		queue = new LinkedList();
		threads = new PoolWorker[nThreads];
		for (int i = 0; i < nThreads; i++) {
			threads[i] = new PoolWorker();
			threads[i].start();
		}
	}

	public void execute(Runnable r) {
		synchronized (queue) {
			queue.addLast(r);
			queue.notify();
		}
	}

	private class PoolWorker extends Thread {
		public void run() {
			Runnable r;
			while (true) {
				synchronized (queue) {
					while (queue.isEmpty()) {
						try {
							queue.wait();
						} catch (InterruptedException ignored) {
						}
					}
					r = (Runnable) queue.removeFirst();
					System.out.println(this.getName()+" " + this.getId());
				}
				// If we don't catch RuntimeException,
				// the pool could leak threads
				try {
					r.run();
				} catch (RuntimeException e) {
					// You might want to log something here
				}
			}
		}
	}
	
	public static void main(String[] args) {
		WorkQueue queue = new WorkQueue(5);
		queue.execute(new Thread("thread1"));
		queue.execute(new Thread("thread2"));
		queue.execute(new Thread("thread3"));
		queue.execute(new Thread("thread4"));
		queue.execute(new Thread("thread5"));
		//queue.new PoolWorker().start();
		//queue.execute();
	}
}

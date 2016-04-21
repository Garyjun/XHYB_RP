package com.brainsoon.common.util.dofile.test;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * Java 线程：新特征-障碍器
 * 
 * @author th
 */
public class ZAQThread {
	
	/**测试函数*/
	public static void main(String[] args) {
		// 创建障碍器，并设置MainTask 为所有定数量的线程都达到障碍点时候所要执行的任务(Runnable)
		CyclicBarrier cb = new CyclicBarrier(7, new MainTask());
		new SubTask("A", cb).start();
		System.out.println("a" +  1111);
		new SubTask("B", cb).start();
		System.out.println("b" +  1111);
		new SubTask("C", cb).start();
		System.out.println("c" +  1111);
		new SubTask("D", cb).start();
		System.out.println("d" +  1111);
		new SubTask("E", cb).start();
		System.out.println("e" +  1111);
		new SubTask("F", cb).start();
		System.out.println("f" +  1111);
		new SubTask("G", cb).start();
		System.out.println("g" +  1111);
	}
}

/**
 * 主任务 MainTask
 */
class MainTask implements Runnable {
	public void run() {
		System.out.println(">>>>主任务执行了！<<<<");
	}
}

/**
 * 子任务 SubTask
 */
class SubTask extends Thread {
	private String name;
	private CyclicBarrier cb;

	SubTask(String name, CyclicBarrier cb) {
		this.name = name;
		this.cb = cb;
	}

	public void run() {
		try {
			System.out.println("[子任务" + name + "]开始执行了！");
			for (int i = 0; i < 29; i++){// 模拟耗时的任务
				System.out.println("[子任务" + name + i + "]开始执行完成了，并通知障碍器已经完成！");
			}
			//通知障碍器已经完成
			cb.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (BrokenBarrierException e) {
			e.printStackTrace();
		}
	}
}

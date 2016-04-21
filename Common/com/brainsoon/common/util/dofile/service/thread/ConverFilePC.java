package com.brainsoon.common.util.dofile.service.thread;

import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.brainsoon.docviewer.model.ResConverfileTask;

/**
 * 
 * @ClassName: ResConverfileTaskPC
 * @Description: 文件转换生产消费类（控制总类）
 * @author tanghui
 * @date 2014-5-23 上午11:31:47
 * 
 */
public class ConverFilePC {

	private static final Logger logger = Logger.getLogger(ConverFilePC.class);
	/** 待转换的文件队列 */
	private static TreeSet<ResConverfileTask> cf = new TreeSet<ResConverfileTask>();
	/** 队列中cf对象的最小值,默认为10 */
	int minCFNum = 10;

	/**
	 * 
	 * @Title: push
	 * @Description: 去数据库中查询待转换的项
	 * @show 生产方法.
	 * @show 该方法为同步方法，持有方法锁；
	 * @show 首先循环判断满否，满的话使该线程等待，释放同步方法锁，允许消费；
	 * @show 当不满时首先唤醒正在等待的消费方法，但是也只能让其进入就绪状态，
	 * @show 等生产结束释放同步方法锁后消费才能持有该锁进行消费
	 * @param
	 * @return void
	 * @throws
	 */
	public synchronized void pushFile(List<ResConverfileTask> cfItem) {
		try {
			while (cf.size() >= minCFNum) {
				logger.info("!!!!!!!!!生产满了!!!!!!!!!");
				this.wait(); // 等待
			}
			//去重
			cf.removeAll(cfItem);
			cf.addAll(cfItem);
//			for (ResConverfileTask resConverfileTask : cfItem) {
//				cf.add(resConverfileTask);
//			}
			logger.info("生产了：" + cfItem.size() + "个， 当前还有：" + cf.size() + "个未处理!");
			notifyAll();// 唤醒等待线程
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IllegalMonitorStateException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * 
	 * @Title: pop
	 * @Description: 返回待转换的文件对象
	 * @show 消费方法
	 * @show 该方法为同步方法，持有方法锁
	 * @show 首先循环判断空否，空的话使该线程等待，释放同步方法锁，允许生产；
	 * @show 当不空时首先唤醒正在等待的生产方法，但是也只能让其进入就绪状态
	 * @show 等消费结束释放同步方法锁后生产才能持有该锁进行生产
	 * @param
	 * @return ResConverfileTask
	 * @throws
	 */
	public synchronized ResConverfileTask popFile() {
		ResConverfileTask cfItem = null;
		try {
			while (cf.size() == 0) {
				logger.info("!!!!!!!!!消费光了!!!!!!!!!");
				this.wait(); // 等待
			}
			for (Iterator<ResConverfileTask> it = cf.iterator(); it.hasNext();){
				 cfItem = (ResConverfileTask) it.next();
				 cf.remove(cfItem);
				 break;
			 }
			logger.info("消费了：" + cfItem.getSrcPath() + " 当前还剩：" + cf.size() + "个未处理!");
			notifyAll();// 唤醒等待线程
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IllegalMonitorStateException e) {
			e.printStackTrace();
		}
		return cfItem;
	}
}

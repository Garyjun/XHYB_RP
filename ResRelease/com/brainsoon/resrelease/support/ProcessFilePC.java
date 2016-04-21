package com.brainsoon.resrelease.support;

import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.brainsoon.resrelease.po.ResReleaseDetail;

/**
 * @ClassName: ProcessFilePC
 * @Description: 文件处理总类
 * @author xiehewei
 * @date 2014年9月1日 下午3:18:01
 *
 */
public class ProcessFilePC {

	private static final Logger logger = Logger.getLogger(ProcessFilePC.class);
	/** 待转换的文件队列 */
	private static TreeSet<ResReleaseDetail> set = new TreeSet<ResReleaseDetail>();
	/** 队列中cf对象的最小值,默认为10 */
	int minCFNum = 10;

	/**
	 * 
	 * @Title: push
	 * @Description: 去数据库中查询待处理的资源
	 * @show 生产方法.
	 * @show 该方法为同步方法，持有方法锁；
	 * @show 首先循环判断满否，满的话使该线程等待，释放同步方法锁，允许消费；
	 * @show 当不满时首先唤醒正在等待的消费方法，但是也只能让其进入就绪状态，
	 * @show 等生产结束释放同步方法锁后消费才能持有该锁进行消费
	 * @param
	 * @return void
	 * @throws
	 */
	public synchronized void pushFile(List<ResReleaseDetail> cfItem) {
		while (set.size() >= minCFNum) {
			logger.info("!!!!!!!!!生产满了!!!!!!!!!");
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} // 等待
		}
		for (ResReleaseDetail resConverfileTask : cfItem) {
			set.add(resConverfileTask);
		}
		logger.info("生产了：" + cfItem.size() + "个， 当前还有：" + set.size() + "个未处理!");
		notifyAll();// 唤醒等待线程
	}

	/**
	 * 
	 * @Title: pop
	 * @Description: 返回待处理的资源对象
	 * @show 消费方法
	 * @show 该方法为同步方法，持有方法锁
	 * @show 首先循环判断空否，空的话使该线程等待，释放同步方法锁，允许生产；
	 * @show 当不空时首先唤醒正在等待的生产方法，但是也只能让其进入就绪状态
	 * @show 等消费结束释放同步方法锁后生产才能持有该锁进行生产
	 * @param
	 * @return ResReleaseDetail
	 * @throws
	 */
	public synchronized ResReleaseDetail popFile() {
		ResReleaseDetail details = null;
		while (set.size() == 0) {
			logger.info("!!!!!!!!!消费光了!!!!!!!!!");
			try {
				this.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // 等待
		}
		for (Iterator<ResReleaseDetail> it = set.iterator(); it.hasNext();){
			details = (ResReleaseDetail) it.next();
			set.remove(details);
			logger.info("消费了：" + details.getResId() + " 当前还剩：" + set.size() + "个未处理!");
			break;
		 }
		notifyAll();// 唤醒等待线程
		return details;
	}
}

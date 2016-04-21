package com.brainsoon.resrelease.processThread;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.resrelease.po.ResFileRelation;
import com.brainsoon.resrelease.po.ResOrder;
import com.brainsoon.resrelease.service.IResOrderService;

/**
 * @ClassName: Producer
 * @Description: TODO
 * @author xiehewei
 * @date 2015年5月27日 下午1:47:27
 *
 */
public class Producer implements Runnable {
	
	private static final Logger logger = Logger.getLogger(Producer.class);

	private volatile boolean isRunning = true;
	private BlockingQueue queue;
	private ResOrder resOrder;

	public Producer(BlockingQueue queue, ResOrder resOrder) {
		this.queue = queue;
		this.resOrder = resOrder;
	}

	@Override
	public void run() {
		logger.info("启动生产者线程！");
		try {
			//while (isRunning) {
				IResOrderService resOrderService = null;
				try {
					resOrderService = (IResOrderService)BeanFactoryUtil.getBean("resOrderService");
				} catch (Exception e) {
					e.printStackTrace();
				}
				List<ResFileRelation> list = resOrderService.queryFileByOrdeId(resOrder.getOrderId());
				logger.info("正在生产数据...");
				//Thread.sleep(r.nextInt(DEFAULT_RANGE_FOR_SLEEP));
				for(ResFileRelation rf : list){
					logger.info("将数据：" + rf.getFileId() + "放入队列...");
					if (!queue.offer(rf, 2, TimeUnit.SECONDS)) {
						logger.error("放入数据失败：" +  rf.getFileId());
					}
				}
				
			//}
		} catch (InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		} finally {
			System.out.println("退出生产者线程！");
		}
	}
	
	public void stop() {
		isRunning = false;
	}
}

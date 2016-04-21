package com.brainsoon.resrelease.support;

import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import com.brainsoon.common.service.IBaseService;
import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.resrelease.po.ResOrder;
import com.brainsoon.resrelease.po.ResReleaseDetail;
import com.brainsoon.resrelease.service.IResOrderService;

/**
 * @ClassName: ZAQThread
 * @Description: Java 线程：新特征-障碍器
 * @author xiehewei
 * @date 2014年10月9日 上午10:28:12
 *
 */
public class ZAQThread {
	
	/**测试函数*/
	public static void main(String[] args) {
		// 创建障碍器，并设置MainTask 为所有定数量的线程都达到障碍点时候所要执行的任务(Runnable)
		CyclicBarrier cb = new CyclicBarrier(7, new MainTask());
		/*new SubTask("A", cb, releaseId).start();
		System.out.println("a" +  1111);
		new SubTask("B", cb, releaseId).start();
		System.out.println("b" +  1111);
		new SubTask("C", cb, releaseId).start();
		System.out.println("c" +  1111);
		new SubTask("D", cb, releaseId).start();
		System.out.println("d" +  1111);
		new SubTask("E", cb, releaseId).start();
		System.out.println("e" +  1111);
		new SubTask("F", cb, releaseId).start();
		System.out.println("f" +  1111);
		new SubTask("G", cb, releaseId).start();
		System.out.println("g" +  1111);*/
		
		for(int i=0;i<7;i++){
			new SubTask(cb, Long.valueOf(1), Long.valueOf(2));
		}
	}
}

/**
 * 主任务 MainTask
 */
class MainTask implements Runnable {
	private Long orderId;
	private static IResOrderService resOrderService = null;
	public void run() {
		//执行状态更新
		try {
			resOrderService = (IResOrderService) BeanFactoryUtil.getBean("resOrderService");
			ResOrder resOrder = (ResOrder) resOrderService.getByPk(ResOrder.class, orderId);
			resOrder.setStatus(ResReleaseConstant.OrderStatus.PUBLISHED);
			System.out.println(">>>>需求单状态更新！<<<<");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public Long getOrderId() {
		return orderId;
	}
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
}

/**
 * 子任务 SubTask
 */
class SubTask extends Thread {
	
	private static IBaseService baseQueryService = null;
	private CyclicBarrier cb;
	private Long releaseId;
	private Long orderId;

	SubTask(CyclicBarrier cb, Long releaseId, Long orderId) {
		this.cb = cb;
		this.releaseId = releaseId;
		this.orderId = orderId;
	}

	@SuppressWarnings("unchecked")
	public void run() {
		//执行转化任务
		try {
			//获取需要转化的资源
			String hql = " from ResReleaseDetail rrd where rrd.status=0 and rrd.releaseId="+releaseId+" order by rrd.detailId asc";
			baseQueryService = (IBaseService) BeanFactoryUtil.getBean("baseService");
			List<ResReleaseDetail> cf = null;
			cf = baseQueryService.query(hql);
			for (int i = 0; i < cf.size(); i++){// 模拟耗时的任务
				ProcessFile.processResOrder(cf.get(i), orderId);
			}
			//通知障碍器已经完成
			cb.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (BrokenBarrierException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Long getReleaseId() {
		return releaseId;
	}

	public void setReleaseId(Long releaseId) {
		this.releaseId = releaseId;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
}

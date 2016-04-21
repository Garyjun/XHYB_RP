package com.brainsoon.resrelease.service.impl;

import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import com.brainsoon.common.service.IBaseService;
import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.resrelease.po.ResReleaseDetail;
import com.brainsoon.resrelease.support.ProcessFile;

/**
 * @ClassName: SubTask
 * @Description: TODO
 * @author xiehewei
 * @date 2014年10月9日 下午2:34:55
 *
 */
public class SubTask extends Thread {
	
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
			IBaseService baseQueryService = (IBaseService) BeanFactoryUtil.getBean("baseService");
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

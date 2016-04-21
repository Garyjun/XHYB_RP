package com.brainsoon.resrelease.service.impl;

import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.resrelease.po.ResOrder;
import com.brainsoon.resrelease.service.IResOrderService;
import com.brainsoon.resrelease.support.ResReleaseConstant;

/**
 * @ClassName: MainTask
 * @Description: 主任务 MainTask
 * @author xiehewei
 * @date 2014年10月9日 下午2:34:06
 *
 */
public class MainTask implements Runnable {
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

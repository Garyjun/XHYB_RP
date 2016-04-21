package com.brainsoon.system.support;

import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.system.service.ISysOperateService;

public class SysOperateLogUtils {

	private static ISysOperateService sysOperateService;

	static {
		try {
			sysOperateService = (ISysOperateService) BeanFactoryUtil.getBean("sysOperateService");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 操作日志记录
	 * 
	 * @param operateKey 操作类型Key
	 * @param entityDesc 操作业务实体描述：比如，用户名称，资源对象的名称...
	 * @param user 操作用户
	 */
	public static void addLog(String operateKey, String entityDesc, UserInfo user) {
		sysOperateService.addLog(operateKey, entityDesc, user);
	}

}

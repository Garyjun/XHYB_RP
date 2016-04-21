package com.brainsoon.statistics.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brainsoon.common.support.Constants;
import com.brainsoon.common.support.GlobalAppCacheMap;
import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.statistics.service.IModuleNumService;

public class ResModuleStatistics {
	
	protected transient final Log logger = LogFactory.getLog(getClass());
	
	public void doStatistics(){
		logger.debug("同步资源模块数据");
		try {
			int platformId = Integer.parseInt(Constants.EDU_RES_1);
			IModuleNumService moduleNumService = (IModuleNumService) BeanFactoryUtil
					.getBean("moduleNumService");
			moduleNumService.doStatistics(platformId);
		} catch (Exception e) {
			logger.error("同步模块异常"+e);
			e.printStackTrace();
		}
	}
}

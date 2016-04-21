package com.brainsoon.resource.job;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.resource.service.ICopyrightService;

public class CopyrightWarningJob {
	
	protected transient final Log logger = LogFactory.getLog(getClass());
	public static ICopyrightService copyrightService = null;
//	static{
//		try {
//			copyrightService = (ICopyrightService)BeanFactoryUtil.getBean("copyrightService");
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
	public void updateCopyrightWarning(){
		logger.debug("更新预警列表");
		try{
			ICopyrightService copyrightService = (ICopyrightService)BeanFactoryUtil.getBean("copyrightService");
			copyrightService.updateCopyrightWarning();
		}catch (Exception e) {
			logger.error(e);
		}
	}
	public void updateMysqlWidthJourAndEntry(){
		logger.debug("更新预警列表");
		try{
			ICopyrightService copyrightService = (ICopyrightService)BeanFactoryUtil.getBean("copyrightService");
			copyrightService.updateMysqlEntry();
		}catch (Exception e) {
			logger.error(e);
		}
	}
//	public void updatePublishCopyrightWarning(){
//		logger.debug("更新预警列表");
//		try{
//			ICopyrightService copyrightService = (ICopyrightService)BeanFactoryUtil.getBean("copyrightService");
//			copyrightService.updatePubliCopyrightWarning();
//		}catch (Exception e) {
//			logger.error(e);
//		}
//	}
//	public void copyrightWarningSearch(){
//		logger.debug("更新预警列表");
//		try{
//			ICopyrightService copyrightService = (ICopyrightService)BeanFactoryUtil.getBean("copyrightService");
//			copyrightService.updateCopyrightWarning();
//		}catch (Exception e) {
//			logger.error(e);
//		}
//	}
	
}

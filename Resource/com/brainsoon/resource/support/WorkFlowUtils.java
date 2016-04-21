package com.brainsoon.resource.support;

import org.apache.commons.lang.StringUtils;

import com.brainsoon.jbpm.constants.ProcessConstants;
import com.brainsoon.system.support.SystemConstants;

public class WorkFlowUtils {

	/**
	 * 获取资源流程唯一执行ID
	 * 
	 * @param objectId 资源ID
	 * @param libType 资源类型（对应不同流程）
	 */
	public static String getExecuId(String objectId, String libType) {
		String execuId = null;
		if (StringUtils.endsWith(libType, SystemConstants.LibType.ORES_TYPE)) {
			execuId = ProcessConstants.WFType.ORES_CHECK + "." + objectId;
		} else if (StringUtils.endsWith(libType, SystemConstants.LibType.BRES_TYPE)) {
			execuId = ProcessConstants.WFType.BRES_CHECK + "." + objectId;
		} else if (StringUtils.endsWith(libType, SystemConstants.LibType.PRES_TYPE)) {
			execuId = ProcessConstants.WFType.PRES_CHECK + "." + objectId;
		} else if(libType.equals("edu")){
			execuId = ProcessConstants.WFType.ORDER_CHECK + "." + objectId;
		}else if(libType.equals("sub")){
			execuId = ProcessConstants.WFType.SUBJECT_CHECK + "." + objectId;
		}else if(libType.equals("press")){
			execuId = ProcessConstants.WFType.PUB_ORDER_CHECK + "." + objectId;
		}else if(libType.equals("subject")){
			execuId = ProcessConstants.WFType.PUB_SUBJECT_CHECK + "." + objectId;
		}else if(libType.equals("pubresCheck")){
			execuId = ProcessConstants.WFType.PUB_ORES_CHECK + "." + objectId;
		}
		return execuId;
	}
}

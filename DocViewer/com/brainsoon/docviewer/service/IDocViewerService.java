package com.brainsoon.docviewer.service;

import com.brainsoon.docviewer.model.ResConverfileTask;

public interface IDocViewerService {

	/**检查文件是否转换成功*/
	public ResConverfileTask checkingConverSucessFull(String srcFilePath);
	/**
	 * 
	* @Title: getConverPathByObjectId
	* @Description: 根据objectId获取转换后路径
	* @param objectId
	* @return    参数
	* @return String    返回类型
	* @throws
	 */
	public String getConverPathByObjectId(String objectId);
	
}

package com.brainsoon.statistics.service;

import com.brainsoon.common.service.IBaseService;

public interface IResourceOfWordService extends IBaseService{
	
	public void doResourceOfWord();
	
	/**
	 * List页面显示查询资源下文件是否包含敏感词
	 * @return
	 */
	public String findWord(String resourceId);
}

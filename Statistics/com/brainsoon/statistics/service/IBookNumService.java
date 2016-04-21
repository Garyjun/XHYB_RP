package com.brainsoon.statistics.service;

import javax.servlet.http.HttpServletRequest;

import com.brainsoon.common.service.IBaseService;

public interface IBookNumService extends IBaseService{

	//素材资源统计
	public String querySuCai(String publishType,String queryColum);
	
	//图书资源统计
	public String queryBook(String publishType,String queryColum);
}

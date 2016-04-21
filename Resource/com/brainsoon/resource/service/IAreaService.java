package com.brainsoon.resource.service;

import java.util.List;

import com.brainsoon.common.service.IBaseService;

@SuppressWarnings("rawtypes")
public interface IAreaService extends IBaseService{
	/**
	 * 获取省列表
	 * @return
	 */
	public List getProvince();
	
	/**
	 * 获取市列表
	 * @param code
	 * @return
	 */
	public List getCity(String code);
	
	/**
	 * 获取县列表
	 * @param code
	 * @return
	 */
	public List getArea(String code);
	
	/**
	 * 取得所属地区信息
	 * @param provCode
	 * @param cityCode
	 * @param areaCode
	 * @return
	 */
	public String getRegionInfo(String region );
}

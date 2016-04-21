package com.brainsoon.search.service;

import java.io.File;
import java.util.List;

import com.brainsoon.common.service.IBaseService;
import com.brainsoon.semantic.ontology.model.MetaDataDC;

public interface ISearchService extends IBaseService {


	/**
	 * 高级查询，学段、学科、知识点查询条件字典
	 * @param eduPhase
	 * @param subject
	 * @param type
	 * @return
	 */
	public String getAdvaceSearchQueryConditionDic(String eduPhase, String subject, String type,String xPath);
	
	/**
	 * 导出高级查询的结果
	 * @param dcs 结果集
	 * @param type 导出类型 元数据、元数据+文件、文件
	 * @param type TODO
	 * @return
	 */
	public File exportRes(List<MetaDataDC> dcs, String type);
	
	public String getAllDefineMetaData(String resType, String flag);
	
	public String getDefineMetaDataByResType(String resType);
	
	public String getMetaDataValueByEnName(String enName);
}

package com.brainsoon.search.service;

import java.io.File;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.brainsoon.appframe.query.QueryConditionList;

public interface ISearchPubresService {


	/**
	 * 高级查询，学段、学科、知识点查询条件字典
	 * @param eduPhase
	 * @param subject
	 * @param type
	 * @return
	 */
	public String getAdvaceSearchQueryConditionDic(String eduPhase, String subject, String type);
	
	/**
	 * 导出高级查询的结果
	 * @param queryCondition 
	 * @return
	 */
	public File exportRes(String queryCondition);
}

package com.brainsoon.journal.service;

import javax.servlet.http.HttpServletRequest;

import com.brainsoon.appframe.query.QueryConditionList;
import com.brainsoon.common.service.IBaseService;

public interface IJournalservice extends IBaseService{
	/**
	 * 
	* @Title: getTimeTree
	* @Description: 期刊管理左侧页面时间树
	* @param type
	* @return    参数
	* @return String    返回类型
	* @throws
	 */
	public String getTimeTree(Long type, String nodeId, String level, String name, String xpath);
	
	/**
	 * 
	* @Title: queryJournalList
	* @Description:查询期刊列表
	* @param request
	* @param queryUrl
	* @return    参数
	* @return String    返回类型
	* @throws
	 */
	public String queryJournalList(HttpServletRequest request, String queryUrl) ;
	/**
	 * 
	* @Title: getJournalList
	* @Description: 文章列表
	* @param keyWord
	* @param articleTitle
	* @param magazineNum
	* @return    参数
	* @return String    返回类型
	* @throws
	 */
	public String getArticleList(String keyWord, String articleTitle, String magazineNum);
	/**
	 * 
	* @Title: getJournalList
	* @Description: 期刊详细
	* @param level
	* @param code
	* @return    参数
	* @return String    返回类型
	* @throws
	 */
	public String getJournalDetail(String level, String code);
}

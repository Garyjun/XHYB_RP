package com.brainsoon.bsrcm.search.service;

import java.util.List;

import com.brainsoon.bsrcm.search.po.SolrQueue;

/**
 * Description: <p>为Solr提供封装门面模式</p>
 * Content Desc:<p><p>
 */
public interface ISolrQueueFacede {
	
	
	/**
	 * 通过状态返回创建索引信息的集合
	 * @param status
	 * @return
	 */
	public List<SolrQueue> getSolrQueueByStatus(String status);
	
	/**
	 * 添加需要建立索引的资源到SolrQueue中
	 * @param resId 资源ID
	 * @param url 回调方法
	 * @return
	 */
	String addSolrQueue(String resId,String url);

	
	public void updateSolrQueueStatus(Long id, String status);
}

package com.brainsoon.docviewer.service;

import java.util.List;

import com.brainsoon.docviewer.model.ResConverfileTask;
import com.brainsoon.semantic.ontology.model.DoFileQueue;
import com.brainsoon.semantic.ontology.model.DoFileQueueList;

public interface IResConverfileTaskService {

	/**
	 * 
	* @Title: insertQueue
	* @Description: 添加转换数据
	* @param doFileQueueList
	* @return    参数
	* @return String    返回类型
	* @throws
	 */
	public String insertQueue(DoFileQueueList doFileQueueList);
	
	/**
	 * 
	* @Title: addQueue
	* @Description: 添加转换数据
	* @param doFileQueue
	* @return    参数
	* @return String    返回类型
	* @throws
	 */
	public String addQueue(DoFileQueue doFileQueue);
	
	/**
	 * 
	 * @Title: saveResConverfileTask 
	 * @Description: 保存文件到带转换列表中
	 * @param   
	 * @return void 
	 * @throws
	 */
	public void saveResConverfileTask(ResConverfileTask rcft);
	
	
	/**
	 * 
	 * @Title: updateConverfileTask
	 * @Description: 更新待转换队列和转换历史记录
	 * @param
	 * @return void
	 * @throws
	 */
	public String updateConverfileTask(ResConverfileTask rcft);
	
	/**
	 * 
	 * @Title: deleteConverfileTask 
	 * @Description: 删除转换后的文件路径及转换队列表中的记录
	 * @param   
	 * @return void 
	 * @throws
	 */
	public String deleteConverfileTask(ResConverfileTask rcft);
	/**
	 * 
	 * @Title: queryTaskHistory 
	 * @Description: 通过资源id进行重试
	 * @param   
	 * @return void 
	 * @throws
	 */
	public String doTaskHistory(String ids);
	
	
	/**
	 * 
	 * @Title: doTaskHistoryByPath 
	 * @Description: 通过原路径进行重试
	 * @param   
	 * @return void 
	 * @throws
	 */
	public String doTaskHistoryByPath(String srcPaths,String resId);
	
	
	/**
	 * 
	 * @Title: doTaskCheckExitJL 
	 * @Description:  通过txt文件校验文件是否存在
	 * @param   
	 * @return String 
	 * @throws
	 */
	public String doTaskCheckExitJL();
	/**
	 * 
	 * @Title: selectPageAll
	 * @Description: 保存文件到带转换列表中
	 * @param   
	 * @return void 
	 * @throws
	 */
	public String createPageAll();
	
	/**
	 * 
	* @Title: deleteDoFileQueue
	* @Description: 删除转换记录（&历史表） 和转换后的文件
	* @param resId    参数 资源Id
	* @param resRootPath    参数 资源目录
	* @return void    返回类型
	* @throws
	 */
	public String deleteDoFileQueue(String resId,String resRootPath);
	public List<DoFileQueue> queryByfileId(String objectId);
}

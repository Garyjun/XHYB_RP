package com.brainsoon.docviewer.service;


public interface IResConverfileTaskIdService {

	/**
	 * 
	 * @Title: addFpIpAddrWithId 
	 * @Description: 分配ip段id
	 * @param   
	 * @return String 
	 * @throws
	 */
	public String addFpIpAddrWithId(String ipAddrs,String singleGroupIdParmNumber,String groupIdParmNumber);
	
	
	/**
	 * 
	 * @Title: updateFpIpAddrWithId 
	 * @Description: 更新分配ip段id
	 * @param   
	 * @return String 
	 * @throws
	 */
	public String updateFpIpAddr(String oldIpAddr,String newIpAddr);
	
	
	/**
	 * 
	 * @Title: deleteAllFpIpAddr 
	 * @Description: 删除所有存在的并且已分配的ip段id
	 * @param   
	 * @return String 
	 * @throws
	 */
	public void deleteAllFpIpAddr();
	
	
}

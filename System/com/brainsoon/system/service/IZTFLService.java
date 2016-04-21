package com.brainsoon.system.service;

import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.common.service.IBaseService;

public interface IZTFLService extends IBaseService {
	public String getZTFLJson(String path);
	public String addZTFLNode(String node);
	public String editZTFLNode();
	public void delZTFLNode(String id);
	/**
	 * 根据分类path 查询分类的名字
	 * @param path
	 * @return
	 */
	public String queryCatagoryCnName(String path);
	public String queryDictValue(String id);
	public String getMetadataTree(UserInfo user,String publishType);
}

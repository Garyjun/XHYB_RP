package com.brainsoon.system.service;

import java.io.File;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.brainsoon.common.service.IBaseService;
import com.brainsoon.system.model.ResCategoryType;
import com.brainsoon.system.model.TreeRelationType;

public interface IFLTXService extends IBaseService {
	public String getFLTXMenu();
	public String getFLTXContent(Long type);
	public String getFLTXContent(Long type,String path);
	public ResCategoryType addFLTXMenu(String name, String indexTag);
	public void addFLTXContent(JSONObject node);
	public void addRelationType(TreeRelationType treeRalationType);
	public void updateFLTXMenu(JSONObject node);
	public void updateFLTXContent(JSONObject node);
	public void delRelation(Long id);
	public List queryNameAndPath(String id);
	public String queryCheckType(String centerType);
	public String checkRelation(String centerId,String relationId);
	public void deleteFLTXMenu(Long id);
	public void deleteFLTXContent(Long id);
	public File exportExcel(String typeId,String typeName);
	
	public String getFLTXNodeByCode(Long type,String codes);
	public String getFLTXNodeByName(Long type,String names);
	/**
	 * 根据分类path 查询分类的名字
	 * @param path
	 * @return
	 */
	public String queryCatagoryCnName(String path);
	public String queryCatagoryCode(String path);
	public String monthJson(long parseLong, String path,String fieldName,String classTime);
	public String dayJson(long parseLong, String year,String fieldName,String month);
	public String getEntryMainTime(Long type,String path);
}

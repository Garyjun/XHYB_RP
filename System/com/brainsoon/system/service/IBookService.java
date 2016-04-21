package com.brainsoon.system.service;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.brainsoon.common.po.tree.TreeNode;
import com.brainsoon.common.service.IBaseService;

public interface IBookService extends IBaseService {
	//根据nodeType查询数据字典中的键值对
	public LinkedHashMap<String,String> getSelectValue(String nodeType);
	public LinkedHashMap<String, String> getTreeValue(String nodeType,String code);
	//根据code路径查询教材目录
	public String getCatalogByCode(String code,String domainType);
	public LinkedHashMap<String,String> getDictMapByName(String name);
	public LinkedHashMap<String,String> getDictMapByIndex(String index);
	public LinkedHashMap<String,String> getDictMapIdByIndex(String index);
	public JSONArray convertBookJSON(List<TreeNode> list,String xpath);
	public JSONArray convertCatalogJson(List<TreeNode> list,String pid);
	public JSONArray convertXKFLJSON(List<TreeNode> list);
	public JSONArray convertClassicJSON(List<TreeNode> list);
	
	//根据code路径查询知识点列表（code为知识点树前两级）
	public String getKnowledgeByParam(String code);
	//根据字典名称查询数据字典中的code
	public String getDictValueByName(String name,String nodeType);
	public JSONArray convertKnowledgeJSON(List<TreeNode> tree, String xpath,
			String pid);
	
	public File exportVersion(String objectId);
	public LinkedHashMap<String,String> getDictMapByValue(String name);
}

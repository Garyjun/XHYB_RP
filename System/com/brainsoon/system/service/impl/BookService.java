package com.brainsoon.system.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.po.tree.TreeNode;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.system.model.DictName;
import com.brainsoon.system.model.DictValue;
import com.brainsoon.system.service.IBookService;
import com.brainsoon.system.support.ExportExcelUtil;
import com.brainsoon.system.support.RandomString;
import com.brainsoon.system.support.SystemConstants;
@Service
public class BookService extends BaseService implements IBookService {

	private String postUrl = WebappConfigUtil.getParameter("RES_ROOT_URL");
	@Override
	public LinkedHashMap<String, String> getSelectValue(String nodeType) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		String nodeName = SystemConstants.NodeType.getValueByKey(nodeType);
		map = getDictMapByName(nodeName);
		return map;
	}
	
	public LinkedHashMap<String, String> getTreeValue(String nodeType,String code) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		HttpClientUtil http = new HttpClientUtil();
		String url = "";
		if(nodeType.equals("1")){
			url = postUrl + "ontologyListQuery/domainNodesByCode?codes=XKFL&domainType=2";
			
		}else if(nodeType.equals("3")){
			url = postUrl + "ontologyListQuery/domainNodesByCode?codes=XKFL," + code +"&domainType=2";
		}
		if(nodeType.equals("1")||nodeType.equals("3")||code==null){
			String json = http.executeGet(url);
			JSONObject result = JSONObject.fromObject(json);
			JSONArray domains = result.getJSONArray("domains");
			if(domains.size()>0){
				for(int i=0; i<domains.size();i++){
					JSONObject domain = domains.getJSONObject(i);
					map.put(domain.getString("code"), domain.getString("label"));
				}
			}
		}else{
			String nodeName = SystemConstants.NodeType.getValueByKey(nodeType);
			map = getDictMapByName(nodeName);
		}
		return map;
	}

	@Override
	public String getCatalogByCode(String code,String domainType) {
		String url = "";
		if(domainType.equals("0"))
			url = postUrl + "ontologyListQuery/bookNodesByCode?codes="+code;
		else
			url = postUrl + "ontologyListQuery/domainNodesByCode?codes="+code+"&domainType=1";
		JSONArray catalog = new JSONArray();
		HttpClientUtil http = new HttpClientUtil();
		String json = http.executeGet(url);
		JSONObject result = JSONObject.fromObject(json);
		JSONArray domains = result.getJSONArray("domains");
		if(domains.size()>0){
			for(int i=0; i<domains.size();i++){
				JSONObject domain = domains.getJSONObject(i);
				JSONObject jo = new JSONObject();
				jo.put("nodeId", domain.getString("nodeId"));
				if(domain.has("pid"))
					jo.put("pid", domain.getString("pid"));
				jo.put("code", domain.getString("code"));
				jo.put("name", domain.getString("label"));
				jo.put("objectId", domain.getString("objectId"));
				catalog.add(jo);
			}
		}
		return catalog.toString();
	}
	
	public String getKnowledgeByParam(String code){
		JSONArray knowledge = new JSONArray();
		String result = "";
		try {
			HttpClientUtil http = new HttpClientUtil();
			String json = "";
			String url = "";
			if(!StringUtils.isBlank(code) && !StringUtils.equals(code, ",")){
				String[] array = code.split(",");
				url = postUrl + "ontologyListQuery/kwDomainAdvanced?educational_phase="
						+ array[0]+"&subject="+array[1]+"&type=2";
			}else{
				url = postUrl + "ontologyListQuery/domainsByMoudle?code=ZS";
			}
			json = http.executeGet(url);
			JSONObject jsonObject = JSONObject.fromObject(json);
			JSONArray domains = jsonObject.getJSONArray("domains");
			if(domains.size()>0){
				for(int i=0; i<domains.size();i++){
					JSONObject domain = domains.getJSONObject(i);
					JSONObject jo = new JSONObject();
					jo.put("nodeId", domain.getString("nodeId"));
					jo.put("pid", domain.getString("pid"));
					jo.put("name", domain.getString("label"));
					jo.put("objectId", domain.getString("objectId"));
					jo.put("code", domain.getString("code"));
					knowledge.add(jo);
				}
			}
			result = knowledge.toString();
		} catch (Exception e) {
			logger.debug("查询知识点参数不正确，code="+code);
		}
		return result;
	}
	
	public LinkedHashMap<String,String> getDictMapByName(String name){
		LinkedHashMap<String,String> map = new LinkedHashMap<String, String>();
		List<DictName> list = query("from DictName where name='"+name+"'");
		if(list.size()>0){
			List<DictValue> valueList = list.get(0).getValueList();
			for(DictValue dv : valueList){
				map.put(dv.getIndexTag(), dv.getName());
			}
		}
		return map;
	}
	public LinkedHashMap<String,String> getDictMapByValue(String name){
		LinkedHashMap<String,String> map = new LinkedHashMap<String, String>();
		List<DictName> list = query("from DictName where indexTag='"+name+"'");
		if(list.size()>0){
			List<DictValue> valueList = list.get(0).getValueList();
			for(DictValue dv : valueList){
				map.put(dv.getName(),dv.getIndexTag());
			}
		}
		return map;
	}
	public LinkedHashMap<String,String> getDictMapByIndex(String index){
		LinkedHashMap<String,String> map = new LinkedHashMap<String, String>();
		List<DictName> list = query("from DictName where indexTag='"+index+"'");
		if(list.size()>0){
			List<DictValue> valueList = list.get(0).getValueList();
			for(DictValue dv : valueList){
				//获得编码
				map.put(dv.getIndexTag(),dv.getName());
				//获得id
//				map.put(dv.getId()+"",dv.getName());
			}
		}
		return map;
	}
	//返回idmap
	public LinkedHashMap<String,String> getDictMapIdByIndex(String index){
		LinkedHashMap<String,String> map = new LinkedHashMap<String, String>();
		List<DictName> list = query("from DictName where indexTag='"+index+"'");
		if(list.size()>0){
			List<DictValue> valueList = list.get(0).getValueList();
			for(DictValue dv : valueList){
				//获得编码
//				map.put(dv.getIndexTag(),dv.getName());
				//获得id
				map.put(dv.getId()+"",dv.getName());
			}
		}
		return map;
	}
	public String getDictValueByName(String name,String nodeType){
		String nodeName = SystemConstants.NodeType.getValueByKey(nodeType);
		LinkedHashMap<String, String> map = getDictMapByName(nodeName);
		for(Map.Entry<String, String> entry : map.entrySet()){
			if(name.equals(entry.getValue()))
				return entry.getKey();
		}
		return "";
	}
	
	@Override
	public JSONArray convertBookJSON(List<TreeNode> list,String xpath) {
		HttpClientUtil http = new HttpClientUtil();
		//设置nodetype和code
		for(int i=0;i<list.size();i++){
			TreeNode node = list.get(i);
			int level = countTBZYLevel(node,list,xpath);
			node.setLevel(level);
			if(level==3){
				node.setNodeType("1");
				node.setCode(SystemConstants.EducationPeriod.getValueByDesc(node.getName()));
			}else if(level==4){
				node.setNodeType("3");
				node.setCode(getDictValueByName(node.getName(),node.getNodeType()));
			}else if(level==5){
				node.setNodeType("2");
				node.setCode(getDictValueByName(node.getName(),node.getNodeType()));
			}else if(level==6){
				node.setNodeType("4");
				node.setCode(getDictValueByName(node.getName(),node.getNodeType()));
			}else if(level>=7){
				if(isParentNode(node, list))
					node.setNodeType("5");
				else
					node.setNodeType("6");
				node.setCode(RandomString.getRandomString(8));
			}
		}		
		list = addXpathToList(list,xpath);
		String json = http.executeGet(postUrl + "ontologyListQuery/domainVersionTrees");
		JSONObject books = JSONObject.fromObject(json);
		Map<String,String> map = new HashMap<String, String>();
		for(int i=0;i<list.size();i++){
			map.put(i+1+"", RandomString.getRandomString(8));
		}
		//设置nodeid和pid
		for(int i=list.size()-1;i>=0;i--){
			TreeNode node = list.get(i);
			JSONArray array = books.getJSONArray("domains");
			JSONObject sameNode = hasSameNode(node,array);
			JSONObject xpathNode = hasXpath(node,array);
			if(sameNode!=null){
				List<TreeNode> children = getChildrenNode(map.get(node.getId()), list);
				for(TreeNode child : children){
					child.setpId(sameNode.getString("nodeId"));
				}
				node.setId(sameNode.getString("nodeId"));
				node.setpId(sameNode.getString("pid"));
				node.setObjectId(sameNode.getString("objectId"));
			}else if(hasXpath(node,array)!=null){
				node.setId(map.get(node.getId()));
				node.setpId(xpathNode.getString("pid"));
			}else{
				node.setId(map.get(node.getId()));
				if(map.get(node.getpId())!=null)
					node.setpId(map.get(node.getpId()));
			}
		}
		return convertList2JSON(list);
	}
	
	public JSONArray convertCatalogJson(List<TreeNode> list,String pid){
		Map<String,String> map = new HashMap<String, String>();
		for(int i=0;i<list.size();i++){
			map.put(i+1+"", RandomString.getRandomString(8));
		}
		for(int i=0;i<list.size();i++){
			TreeNode node = list.get(i);
			node.setId(map.get(node.getId()));
			if(!node.getpId().equals("0"))
				node.setpId(map.get(node.getpId()));
			else
				node.setpId(pid);
			node.setCode(RandomString.getRandomString(8));
		}
		for(int i=0;i<list.size();i++){
			TreeNode node = list.get(i);
			if(isParentNode(node,list))
				node.setNodeType("5");
			else
				node.setNodeType("6");
//			node.setLevel(countLevel(node,list,null));
		}
		return convertList2JSON(list);
	}
	
	//导入学科分类
	public JSONArray convertXKFLJSON(List<TreeNode> list){
		Map<String,String> map = new HashMap<String, String>();
		for(int i=0;i<list.size();i++){
			map.put(i+1+"", RandomString.getRandomString(8));
		}
		for(int i=0;i<list.size();i++){
			TreeNode node = list.get(i);
			node.setId(map.get(node.getId()));
			if(!node.getpId().equals("0"))
				node.setpId(map.get(node.getpId()));
			else
				node.setpId("-1");
			node.setType("0");
			int level = countClassicLevel(node,list);
			if(level==0){
				node.setCode("XKFL");
				node.setNodeType("-1");
			}
			else if(level==1){
				node.setNodeType("1");
				node.setCode(getDictValueByName(node.getName(),node.getNodeType()));
				node.setXpath("XKFL");
			}else if(level==2){
				node.setNodeType("3");
				node.setCode(getDictValueByName(node.getName(),node.getNodeType()));
				TreeNode pNode = getParentNode(node,list);
				node.setXpath("XKFL"+","+pNode.getCode());
			}
			node.setLevel(level);
		}
		JSONArray jsonArray = new JSONArray();
		for(TreeNode node : list){
			JSONObject jo = new JSONObject();
			jo.put("nodeId", node.getId());
			jo.put("pid", node.getpId());
			jo.put("label", node.getName());
			jo.put("type", node.getType());
			jo.put("nodeType", node.getNodeType());
			jo.put("code", node.getCode());
			jo.put("level", node.getLevel());
			if(node.getXpath()!=null&&!"".equals(node.getXpath()))
				jo.put("xpath", node.getXpath());
			jsonArray.add(jo);
		}
		return jsonArray;
	}
	//分类体系
	public JSONArray convertClassicJSON(List<TreeNode> list){
		Map<String,String> map = new HashMap<String, String>();
		for(int i=0;i<list.size();i++){
			map.put(i+1+"", RandomString.getRandomString(8));
		}
		if(list.get(0).getName().equals("同步资源"))
			list = importTBZY(list, map);
		else if(list.get(0).getName().equals("知识点资源"))
			list = importZSDZY(list, map);
		else
			list = importQTZY(list, map);
		return convertList2JSON(list);
	}
	//知识点
	public JSONArray convertKnowledgeJSON(List<TreeNode> list,String xpath,String pid){	
		for(int i=0;i<list.size();i++){
			TreeNode node = list.get(i);
			int level = countClassicLevel(node,list);
			node.setLevel(level+2);
			if(level==0){
				node.setNodeType("3");
				node.setCode(getDictValueByName(node.getName(),node.getNodeType()));
			}else{
				if(isParentNode(node,list))
					node.setNodeType("5");
				else
					node.setNodeType("6");
				node.setCode(RandomString.getRandomString(8));
			}
		}
		Map<String,String> map = new HashMap<String, String>();
		for(int i=0;i<list.size();i++){
			map.put(i+"", RandomString.getRandomString(8));
		}
		for(int i=list.size()-1;i>=0;i--){
			TreeNode node = list.get(i);
			node.setId(map.get(node.getId()));
			if(i==0){
				node.setpId(pid);
			}else{
				node.setpId(map.get(node.getpId()));
			}
		}
		return convertList2JSON(list);
	}
	
	
	private JSONArray convertList2JSON(List<TreeNode> list){
		JSONArray jsonArray = new JSONArray();
		for(TreeNode node : list){
			JSONObject jo = new JSONObject();
			jo.put("nodeId", node.getId());
			jo.put("pid", node.getpId());
			jo.put("label", node.getName());
			jo.put("code", node.getCode());
			jo.put("level", node.getLevel());
			jo.put("nodeType", node.getNodeType());
			if(node.getObjectId()!=null)
				jo.put("objectId", node.getObjectId());
			if(node.getXpath()!=null)
				jo.put("xpath", node.getXpath());
			jsonArray.add(jo);
		}
		return jsonArray;
	}
	
	private TreeNode getParentNode(TreeNode node,List<TreeNode> list){
		for(TreeNode tn : list){
			if(tn.getId().equals(node.getpId()))
				return tn;
		}
		return null;
	}
	
	private List<TreeNode> getChildrenNode(String nodeId,List<TreeNode> list){
		List<TreeNode> children = new ArrayList<TreeNode>();
		for(TreeNode n : list){
			if(n.getpId().equals(nodeId))
				children.add(n);
		}
		return children;
	}
	
	private boolean isParentNode(TreeNode node,List<TreeNode> list){
		boolean result = false;
		for(TreeNode tn : list){
			if(tn.getpId().equals(node.getId())){
				result = true;
				break;
			}
		}
		return result;
	}
	
	private int countTBZYLevel(TreeNode node,List<TreeNode> list,String xpath){
		//从科目一级开始计算层级
		int level = 2;
		if(xpath!=null&&xpath.indexOf(",")==-1)
			level = 3;
		if(xpath!=null&&xpath.indexOf(",")!=-1)
			level = 4;
		while(getParentNode(node,list)!=null){
			level++;
			node = getParentNode(node,list);
		}
		return level;
	}
	
	private int countClassicLevel(TreeNode node,List<TreeNode> list){
		int level = 0;
		while(getParentNode(node,list)!=null){
			level++;
			node = getParentNode(node,list);
		}
		return level;
	}
	
	//同步资源
	private List<TreeNode> importTBZY(List<TreeNode> list, Map<String,String> map){
		for(int i=0;i<list.size();i++){
			TreeNode node = list.get(i);
			node.setId(map.get(node.getId()));
			if(!node.getpId().equals("0"))
				node.setpId(map.get(node.getpId()));
			else
				node.setpId("-1");
			int level = countClassicLevel(node,list);
			node.setLevel(level);
			if(level==0){
				node.setNodeType("7");
				node.setCode(SystemConstants.ResourceMoudle.getValueByDesc(node.getName()));
			}else if(level==1){
				node.setNodeType("8");
				node.setCode(SystemConstants.ResourceType.getValueByDesc(node.getName()));
			}else if(level==2){
				node.setNodeType("1");
				node.setCode(getDictValueByName(node.getName(),node.getNodeType()));
			}else if(level==3){
				node.setNodeType("3");
				node.setCode(getDictValueByName(node.getName(),node.getNodeType()));
			}else if(level==4){
				node.setNodeType("2");
				node.setCode(getDictValueByName(node.getName(),node.getNodeType()));
			}else if(level==5){
				node.setNodeType("6");
				node.setCode(node.getId());
			}
		}
		return list;
	}
	//知识点资源
	private List<TreeNode> importZSDZY(List<TreeNode> list, Map<String,String> map){
		for(int i=0;i<list.size();i++){
			TreeNode node = list.get(i);
			int level = countClassicLevel(node,list);
			node.setLevel(level);
			if(level==0){
				node.setNodeType("7");
				node.setCode(SystemConstants.ResourceMoudle.getValueByDesc(node.getName()));
			}else if(level==1){
				node.setNodeType("1");
				node.setCode(getDictValueByName(node.getName(),node.getNodeType()));
			}else if(level==2){
				node.setNodeType("3");
				node.setCode(getDictValueByName(node.getName(),node.getNodeType()));
			}else{
				if(isParentNode(node, list))
					node.setNodeType("5");
				else
					node.setNodeType("6");
				node.setCode(RandomString.getRandomString(8));
			}
		}
		
		for(int i=0;i<list.size();i++){
			TreeNode node = list.get(i);
			node.setId(map.get(node.getId()));
			if(!node.getpId().equals("0"))
				node.setpId(map.get(node.getpId()));
			else
				node.setpId("-1");
		}
		return list;
	}
	//其他资源
	private List<TreeNode> importQTZY(List<TreeNode> list, Map<String,String> map){
		for(int i=0;i<list.size();i++){
			TreeNode node = list.get(i);
			node.setId(map.get(node.getId()));
			if(!node.getpId().equals("0"))
				node.setpId(map.get(node.getpId()));
			else
				node.setpId("-1");
			int level = countClassicLevel(node,list);
			node.setLevel(level);
			if(level==0){
				node.setNodeType("7");
				node.setCode(SystemConstants.ResourceMoudle.getValueByDesc(node.getName()));
			}else{
				if(isParentNode(node, list))
					node.setNodeType("5");
				else
					node.setNodeType("6");
				node.setCode(RandomString.getRandomString(8));
			}
		}
		return list;
	}
	
	//treeNode list中，给每个节点添加xpath属性
	private List<TreeNode> addXpathToList(List<TreeNode> list,String xpath){
		for(int i=0; i<list.size(); i++){
			TreeNode node = list.get(i);
			TreeNode parentNode = getParentNode(node,list);
			if(parentNode!=null)
				node.setXpath(parentNode.getXpath() + "," +parentNode.getCode());
			else
				node.setXpath(xpath);
		}
		return list;
	}
	
	//分类体系库中是否存在xpath节点
	private JSONObject hasXpath(TreeNode node,JSONArray array){
		for(int i=array.size()-1;i>=0;i--){
			JSONObject jo = array.getJSONObject(i);
			if(!jo.containsKey("xpath"))
				continue;			
			if(jo.get("xpath").equals(node.getXpath())){
				return jo;
			}
		}
		return null;
	}
	
	//分类体系库中是否存在相同节点
	private JSONObject hasSameNode(TreeNode node,JSONArray array){
		for(int i=array.size()-1;i>=0;i--){
			JSONObject jo = array.getJSONObject(i);
			if(!jo.containsKey("xpath"))
				continue;
			if(!StringUtils.isBlank(jo.getString("xpath"))&&
					jo.getString("xpath").equals(node.getXpath())&&
					jo.getString("label").equals(node.getName())){
				return jo;
			}
		}
		return null;
	}
	
    public static void main(String args[]) { 
    	String test = "dfasdf\\sdfas/fsfa\\sdfa/sdf/fdsa";
    	test = test.replaceAll("\\\\", "/");
    	System.out.println(test);
    }

	@Override
	public File exportVersion(String objectId) {
		List<TreeNode> versionTree = getVersionTreeFromOjbectId(objectId);
		ExportExcelUtil eeu = new ExportExcelUtil(versionTree);
		return eeu.exportVersionExcel();
	} 
	
	private List<TreeNode> getVersionTreeFromOjbectId(String objectId){
		HttpClientUtil http = new HttpClientUtil();
		String url = postUrl + "ontologyListQuery/domainVersionTree?version="+objectId;
		String json = http.executeGet(url);
		return json2TreeNodeList(json);
	}
	
	private List<TreeNode> json2TreeNodeList(String json){
		List<TreeNode> versionTree = new ArrayList<TreeNode>();
		JSONObject jo = JSONObject.fromObject(json);
		JSONArray array = jo.getJSONArray("domains");
		for(int i=0; i<array.size(); i++){
			JSONObject jsonObject = array.getJSONObject(i);
			TreeNode node = new TreeNode();
			node.setId(jsonObject.getString("nodeId"));
			node.setpId(jsonObject.getString("pid"));
			node.setName(jsonObject.getString("label"));
			node.setNodeType(jsonObject.getString("nodeType"));
			node.setLevel(Integer.parseInt(jsonObject.getString("level")));
			versionTree.add(node);
		}
		return versionTree;
	}
}

package com.brainsoon.system.service.impl;

import java.util.Random;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;

import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.system.service.IClassicService;
@Service
public class ClassicService extends BaseService implements IClassicService {
	private String postUrl = WebappConfigUtil.getParameter("RES_ROOT_URL");
	public static final String allChar = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	@Override
	public String getMoudleTree(String code) {
		String json = "";
		HttpClientUtil http = new HttpClientUtil();
		if(code.equals("xkfl")){
			json = http.executeGet(postUrl + "ontologyListQuery/domainAllTree?domainType=2");
			return json;
		}
		json = http.executeGet(postUrl + "ontologyListQuery/domainsByMoudle?code="+code);
		if(code.equals("TB")){
			String book = http.executeGet(postUrl + "ontologyListQuery/domainVersionTrees");
			book = filterVerison(book);
			json = joinBook(json,book);
		}
		return json;
	}
	
	public String getPrivilegeTree(String code){
		String json = "";
		HttpClientUtil http = new HttpClientUtil();
		json = http.executeGet(postUrl + "ontologyListQuery/domainsByMoudle?code="+code);
		if(code.equals("TB")){
			String book = http.executeGet(postUrl + "ontologyListQuery/domainVersionTrees");
			book = filterVerison(book);
			json = joinPrivilege(json,book);
		}
		return json;
	}
	
	//教材改版信息
	public String getChangeVersionInfo(String objectId){
		HttpClientUtil http = new HttpClientUtil();
		String json = http.executeGet(postUrl + "ontologyListQuery/domainVersionTree?version="+objectId);
		return json;
	}
	
	//教材改版
	public JSONObject changeVersion(String objectId, JSONArray domains){
		HttpClientUtil http = new HttpClientUtil();
		JSONArray result = convertChangeVersionNodes(objectId,domains);
		JSONObject books = new JSONObject();
		books.put("@type", "domainList");
		books.put("domainType", "0");
		books.put("domains", result);
		String json = http.postJson(postUrl + "createOntology/domain", books.toString());
		return JSONObject.fromObject(json);
	}
	
	private JSONArray convertChangeVersionNodes(String objectId,JSONArray domains){
		JSONArray subjectNodes = new JSONArray();
		for(int i=0; i<domains.size(); i++){
			if(domains.getJSONObject(i).getString("nodeType").equals("3"))
				subjectNodes.add(domains.getJSONObject(i));
		}
		HttpClientUtil http = new HttpClientUtil();
		String json = http.executeGet(postUrl + "ontologyListQuery/domainVersionTree?version="+objectId);
		JSONArray nodes = addNodes(JSONObject.fromObject(json),subjectNodes);
		for(int i=0; i<nodes.size(); i++){
			domains.add(nodes.getJSONObject(i));
		}
		
		JSONArray resultNodes = setNodeProperty(domains);
		return resultNodes;
	}
	
	private JSONArray setNodeProperty(JSONArray domains){
		JSONArray result = new JSONArray();
		Random random = new Random();
		int offset = random.nextInt(62);
		for(int i=0; i<domains.size(); i++){
			JSONObject jo = domains.getJSONObject(i);
			JSONObject newNode = new JSONObject();
			newNode.put("nodeId", getNewNodeId(jo.getString("nodeId"),offset));
			newNode.put("pid", getNewNodeId(jo.getString("pid"),offset));
			newNode.put("code", getNewNodeId(jo.getString("code"),offset));
			newNode.put("nodeType", jo.getString("nodeType"));
			newNode.put("label", jo.getString("label"));
			newNode.put("level", jo.getString("level"));
			result.add(newNode);
		}
		return result;
	}
	
	private String getNewNodeId(String id, int offset){
		if(id.length() >= 7){
			char[] newIds = new char[id.toCharArray().length];
			char[] allCharArray = allChar.toCharArray();
			char[] ids = id.toCharArray();
			for(int i=0; i<ids.length; i++){
				int index = (allChar.indexOf(ids[i]) + offset)%62;
				char c = allCharArray[index];
				newIds[i] = c;
			}
			return String.valueOf(newIds);
		}else if(id.equals("0")){
			return "-1";
		}else
			return id;
	}
	
	private JSONArray addNodes(JSONObject jo, JSONArray array){
		JSONArray result = new JSONArray();
		JSONArray tree = jo.getJSONArray("domains");
		String test = "";
		for(int i=0; i<array.size(); i++){
			test += array.getJSONObject(i).getString("nodeId") + ",";
		}
		for(int i=0; i<tree.size(); i++){
			JSONObject object = tree.getJSONObject(i);
			if(isChildNode(test,object,tree))
				result.add(object);
		}
		return result;
	}
	
	private boolean isChildNode(String test, JSONObject object, JSONArray tree){
		boolean result = false;
		while(object!=null){
			if(test.indexOf(object.getString("pid"))!=-1){
				result = true;
				break;
			}
			object = getParentNode(object,tree);
		}
		return result;
	}
	
	private JSONObject getParentNode(JSONObject object, JSONArray tree){
		JSONObject result = null;
		for(int i=0; i<tree.size(); i++){
			JSONObject jo = tree.getJSONObject(i);
			if(object.getString("pid").equals(jo.getString("nodeId")))
				result = jo;
		}
		return result;
	}
	
	private String joinBook(String json,String bookJson){
		JSONObject classic = JSONObject.fromObject(json);
		String pid = "";
		for(int i=0; i<classic.getJSONArray("domains").size();i++){
			JSONObject jo = classic.getJSONArray("domains").getJSONObject(i);
			if(jo.getString("code").equals("T00")){
				pid = jo.getString("nodeId");
				break;
			}
		}
		JSONObject book = JSONObject.fromObject(bookJson);
		JSONArray domains = book.getJSONArray("domains");
		for(int i=0; i< domains.size();i++){
			JSONObject jo = domains.getJSONObject(i);
			if(jo.getString("pid").equals("-1"))
				jo.put("pid", pid);
			classic.getJSONArray("domains").add(jo);
		}
		return classic.toString();
	}
	
	private String joinPrivilege(String json,String bookJson){
		JSONObject privilegeTree = new JSONObject();
		JSONArray domains = new JSONArray();
		
		JSONObject classic = JSONObject.fromObject(json);
		JSONObject book = JSONObject.fromObject(bookJson);
		String pid = "";
		
		for(int i=0; i<classic.getJSONArray("domains").size();i++){
			JSONObject jo = classic.getJSONArray("domains").getJSONObject(i);
			if(jo.getString("code").equals("T00")){
				pid = jo.getString("nodeId");
			}
			if("780132".contains(jo.getString("nodeType")))
				domains.add(jo);
		}
		
		for(int i=0; i<book.getJSONArray("domains").size();i++){
			JSONObject jo = book.getJSONArray("domains").getJSONObject(i);
			if(jo.getString("pid").equals("-1"))
				jo.put("pid", pid);
			if("780132".contains(jo.getString("nodeType")))
				domains.add(jo);
		}
		privilegeTree.put("domains", domains);
		return privilegeTree.toString();
	}
	
	private boolean hasProperty(JSONArray array,String nodeId){
		for(int i=0; i<array.size();i++){
			JSONObject jo = array.getJSONObject(i);
			if(jo.getString("nodeId").equals(nodeId)){
				return true;
			}
		}
		return false;
	}
	
	private String filterVerison(String book){
		JSONObject object = JSONObject.fromObject(book);
		JSONArray array = object.getJSONArray("domains");
		JSONArray domains = new JSONArray();
		for(int i=0;i<array.size();i++){
			JSONObject jo = array.getJSONObject(i);
			if(!hasProperty(domains,jo.getString("nodeId"))){
				domains.add(jo);
			}
		}
		object.remove("domains");
		object.put("domains", domains);
		return object.toString();
	}
	
	public static void main(String[] args){
		String test = "fa9sdf82";
		Random random = new Random();
		int offset = random.nextInt(62);
		ClassicService cs = new ClassicService();
		String result = cs.getNewNodeId(test,1);
		System.out.println(result);
	}
}

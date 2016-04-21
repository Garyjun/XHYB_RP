package com.brainsoon.system.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.system.service.IClassicService;
import com.brainsoon.system.service.IRoleResService;
import com.brainsoon.system.support.SystemConstants.NodeType;
import com.brainsoon.system.support.SystemConstants.ResourceMoudle;
@Service
public class RoleResService extends BaseService implements IRoleResService{
	private static final String RES_TREE_URL=WebappConfigUtil.getParameter("RES_TREE_URL");
	@Autowired
	private IClassicService classicService;
	public String getResTree(){
		   
	      JSONArray catalog = new JSONArray();
	      StringBuffer textBook=new StringBuffer("");
	      JSONArray tbCatalog = getBranch(ResourceMoudle.TYPE0,textBook);
	      JSONArray ztCatalog = getBranch(ResourceMoudle.TYPE1,textBook);
	      JSONArray tzCatalog = getBranch(ResourceMoudle.TYPE3,textBook);
	      JSONArray jsCatalog = getBranch(ResourceMoudle.TYPE4,textBook);
	      JSONArray zsCatalog = getBranch(ResourceMoudle.TYPE2,textBook);
	      String textBookStr=textBook.toString();
	     /* if(textBookStr.length()>1){
	    	  textBookStr=textBookStr.substring(0, textBookStr.length()-1);
	      }
	      logger.debug("textBook "+textBookStr);*/
	      textBookStr="V[0-9]*";
	      catalog.addAll(tbCatalog);
	      catalog.addAll(ztCatalog);
	      catalog.addAll(tzCatalog);
	      catalog.addAll(jsCatalog);
	      catalog.addAll(zsCatalog);
	 	  return catalog.toString()+"##"+textBookStr;
		  
	    
	}
	
	public JSONArray getBranch(String code,StringBuffer textBook ){
		    String json =classicService.getPrivilegeTree(code);
		    logger.debug(json);
	        JSONArray catalog = new JSONArray();
			JSONObject result = JSONObject.fromObject(json);
			if(result.has("domains")){
				JSONArray domains = result.getJSONArray("domains");
				if(domains.size()>0){
					for(int i=0; i<domains.size();i++){
						JSONObject domain = domains.getJSONObject(i);
						JSONObject jo = new JSONObject();
						jo.put("id", domain.getString("nodeId"));
						jo.put("pid", domain.getString("pid"));
						jo.put("code", domain.getString("code"));
						jo.put("name", domain.getString("label"));
						jo.put("objectId", domain.getString("objectId"));
						if(domain.containsKey("xpath")){
							jo.put("xpath", domain.getString("xpath"));
						}else{
							jo.put("xpath","");
						}
						/*if(domain.containsKey("nodeType")&&domain.getString("nodeType").equals(NodeType.TYPE0)){
						textBook.append("TB,T00,").append(domain.getString("code")).append(";");
					}*/
						catalog.add(jo);
					}
				}
			}
			return catalog;
	}
	
}

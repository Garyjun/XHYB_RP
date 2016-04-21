package com.brainsoon.system.service.impl;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.semantic.schema.ontology.CustomMetaData;
import com.brainsoon.semantic.schema.ontology.MetadataDefinition;
import com.brainsoon.system.model.DictValue;
import com.brainsoon.system.model.ResCategory;
import com.brainsoon.system.model.ZTFLNode;
import com.brainsoon.system.service.IZTFLService;
import com.brainsoon.system.util.MetadataSupport;
@Service
public class ZTFLService extends BaseService implements IZTFLService {

	@Override
	public String getZTFLJson(String path) {
		String sql = "";
		if(path.equals("root"))
			sql = "from ZTFLNode where pid = '0'";
		else
			sql = "from ZTFLNode where pid =" + Long.parseLong(path);
		List<ZTFLNode> list = query(sql);
		List<JSONObject> ztflTree = new ArrayList<JSONObject>();
		if(list.size()>0){
			for(ZTFLNode node : list){
				JSONObject ztflNode = new JSONObject();
				List<ZTFLNode> parentList = query("from ZTFLNode where pid ="+node.getId());
				if(parentList.size()>0)
					ztflNode.put("isParent", true);
				else
					ztflNode.put("isParent", false);
				ztflNode.put("nodeId", node.getId());
				ztflNode.put("pid", node.getPid());
				ztflNode.put("name", node.getName());
				ztflNode.put("code", node.getCode());
				ztflNode.put("path", node.getPath());
				ztflNode.put("displayOrder", node.getDisplayOrder());
				ztflTree.add(ztflNode);
			}
		}
		return ztflTree.toString();
	}
	/**
	 * 根据分类path 查询分类的名字
	 * @param path
	 * @return
	 */
	public String queryCatagoryCnName(String path) {
		String sql = "";
		String name = "";
		if(StringUtils.isNotBlank(path)){
			if(path.indexOf("-")>=0){
				String[] paths = path.split("-"); //后台存储的数据格式为,22,334,32,-,55,23,为了中图分类号能匹配
				sql = "from ResCategory where ";
				for(String pathTemp:paths){
					pathTemp = pathTemp.substring(1,pathTemp.length()-1);
					sql+="path='"+ pathTemp +"' or ";
				}
				sql = sql.substring(0,sql.length()-3);
			}else{
				if(path.length()>1){
					path = path.substring(1,path.length()-1);
				}
				sql = "from ResCategory where path ='"+ path +"'";
			}
			List<ResCategory> list = query(sql);
			if(list.size()>0){
				for(ResCategory node : list){
					name += node.getCode()+",";
//					name += node.getPath()+",";
				}
				if(name.indexOf(",")!=-1){
					name = name.substring(0,name.length()-1);
				}
			}
		}
		return name;
	}
	/**
	 * 根据分类path 查询分类的名字
	 * @param path
	 * @return
	 */
	public String queryDictValue(String id) {
		String sql = "";
		String name = "";
		if(StringUtils.isNotBlank(id)){
			String ids[]=id.split(",");
			for(int i=0;i<ids.length;i++){
				sql = "from DictValue where id ="+ Long.parseLong(ids[i]);
				try {
					List<DictValue> list = query(sql);
					if(list.size()>0){
						for(DictValue node : list){
							name += node.getName()+",";
						}
					}
				} catch (Exception e) {
					// TODO: handle exception
					name = "";
				}
			}
		}
		if(name.length()>1){
			name = name.substring(0,name.length()-1);
		}
	
		return name;
	}
	@Override
	public String addZTFLNode(String node) {
		JSONObject jsonNode = JSONObject.fromObject(node);
		ZTFLNode ztflNode = null;
		if(jsonNode.getString("nodeId").length()!=8){
			ztflNode = (ZTFLNode) getByPk(ZTFLNode.class, jsonNode.getLong("nodeId"));
			ztflNode.setCode(jsonNode.getString("code"));
			ztflNode.setName(jsonNode.getString("name"));
			saveOrUpdate(ztflNode);
		}else{
			ztflNode = new ZTFLNode();
			ztflNode.setCode(jsonNode.getString("code"));
			ztflNode.setName(jsonNode.getString("name"));
			ztflNode.setPid(jsonNode.getLong("pid"));
			ztflNode = (ZTFLNode) create(ztflNode);
		}
		return ztflNode.getId().toString();
	}

	@Override
	public String editZTFLNode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delZTFLNode(String id) {
		ZTFLNode ztflNode = (ZTFLNode) getByPk(ZTFLNode.class, Long.parseLong(id));
		delete(ztflNode);
	}
	@Override
	public String getMetadataTree(UserInfo user,String publishType) {
//		List<MetadataDefinition>  metadataDefines =user.getMetadataList();
		List<MetadataDefinition> metadataDefines = MetadataSupport.getMetadateDefines(publishType);
//		List<CustomMetaData>  customMetaDatas = MetadataSupport.getAllMetadateList(user,"1");
		JSONArray array = new JSONArray();
//		jo.put("id", "1");
//		jo.put("name", "父节点");
//		jo.put("pid", "-1");
//		array.add(jo);
//		int num = 0;
//		for(CustomMetaData customMetaData:customMetaDatas){
//			JSONObject jo = new JSONObject();
//			num++;
//			List<MetadataDefinition>  metadataDefines =customMetaData.getCustomPropertys();
//			jo.put("nodeId", num+"");
//			jo.put("pid", "-1");
//			jo.put("name", customMetaData.getNameCN());
//			array.add(jo);
			for(MetadataDefinition metadataDefine:metadataDefines){
				JSONObject object = new JSONObject();
				if(!metadataDefine.getFieldName().equals("identifier")){
					object.put("key", metadataDefine.getFieldName());
					object.put("value", metadataDefine.getFieldZhName());
					array.add(object);
				}
			}
//		}
		return array.toString();
	}
}

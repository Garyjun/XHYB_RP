package com.brainsoon.system.service.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.system.model.DictName;
import com.brainsoon.system.model.DictValue;
import com.brainsoon.system.service.IDictNameService;
import com.brainsoon.system.support.SysOperateLogUtils;
import com.brainsoon.system.support.SystemConstants;
@Service
public class DictNameService extends BaseService implements IDictNameService {

	@Override
	public void addDictName(DictName dictName) {
		create(dictName);
	}

	@Override
	public void updDictName(DictName dictName) {
		saveOrUpdate(dictName);
	}

	@Override
	public void deleteByIds(String ids) {
		UserInfo userInfo =  LoginUserUtil.getLoginUser();
		if(StringUtils.isNotBlank(ids)){
			String[] idArray = ids.split(",");
			for(String id : idArray){
				DictName dictName = (DictName) getByPk(DictName.class,
						Long.parseLong(id));
				List<DictValue> valueList = dictName.getValueList();
				if(valueList.size()>0){
					for(DictValue dv : valueList){
						delete(dv);
					}
				}
				delete(dictName);
				int platformId = userInfo.getPlatformId();
				if(platformId==1) {
					SysOperateLogUtils.addLog("dictName_delete", dictName.getName(), userInfo);
				} else{
					SysOperateLogUtils.addLog("pub_dictName_delete", dictName.getName(), userInfo);
				}
				
			}
		}
	}

	@Override
	public DictName getDictNameByValueId(Long valueId) {
		DictValue dictValue = (DictValue) getByPk(DictValue.class, valueId);
		DictName dictName = (DictName) getByPk(DictName.class, dictValue.getPid());
		return dictName;
	}

	@Override
	public String getValueNameByIndex(String dictName, String index) {
		List<DictName> list = query("from DictName where indexTag='"+ index + "'");
		if(list.size()>0){
			DictName dict = list.get(0);
			for(DictValue dv : dict.getValueList()){
				if(dv.getIndexTag().equals(dictName))
					return dv.getName();
			}
		}
		return null;
	}
	
	@Override
	public String getValueKeyByIndex(String dictName, String index) {
		List<DictName> list = query("from DictName where indexTag='"+ index + "'");
		if(list.size()>0){
			DictName dict = list.get(0);
			for(DictValue dv : dict.getValueList()){
				if(dv.getName().equals(dictName))
					return dv.getIndexTag();
			}
		}
		return null;
	}
	public LinkedHashMap<String, String> getSelectValue(String nodeType) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		if(nodeType.equals("1")){
			Set<String> set = SystemConstants.EducationPeriod.map.getValueSet();
			for(String name : set){
				map.put(name, SystemConstants.EducationPeriod.getValueByKey(name));
			}
		}else if(nodeType.equals("7")){
			Set<String> set = SystemConstants.ResourceMoudle.map.getValueSet();
			for(String name : set){
				map.put(name, SystemConstants.ResourceMoudle.getValueByKey(name));
			}
		}else if(nodeType.equals("8")){
			Set<String> set = SystemConstants.ResourceType.map.getValueSet();
			for(String name : set){
				map.put(name, SystemConstants.ResourceType.getValueByKey(name));
			}
		}else{
			String nodeName = SystemConstants.NodeType.getValueByKey(nodeType);
			map = getDictMapByName(nodeName);
		}
		return map;
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
	
	public String getDictIndexByName(String name,String vname){
		String index = "";
		List<DictName> list = query("from DictName where name='"+name+"'");
		if(list.size()>0){
			List<DictValue> valueList = list.get(0).getValueList();
			for(DictValue dv : valueList){
				if (vname.equals(dv.getName())) {
					index = dv.getIndexTag();
				}
			}
		}
		return index;
		
	}
	
	public Long getDictNamePKByIndex(String index){
		List<DictName> list = query("from DictName where status='1' and indexTag='"+ index + "'");
		if(list.size()>0){
			DictName dict = list.get(0);
			return dict.getId();
		}
		return null;
	}

	@Override
	public String getValueNameById(String id) {
		List<DictValue> list = query("from DictValue where id="+ Long.parseLong(id)+ "");
		if(list.size()>0){
			DictValue dict = list.get(0);
			if(StringUtils.isNotBlank(dict.getName())){
				return dict.getName();
			}
		}
		return null;
	}

	@Override
	public String getDictValuePKByIndex(String index) {
		List<DictValue> list = query("from DictValue where indexTag='"+ index+ "'");
		if(list.size()>0){
			DictValue dict = list.get(0);
			if(StringUtils.isNotBlank(dict.getName())){
				return dict.getId().toString();
			}
		}
		return null;
	}
}

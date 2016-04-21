package com.brainsoon.system.service.impl;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;

import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.system.model.Organization;
import com.brainsoon.system.service.IOrganizationService;
@Service
public class OrganizationService extends BaseService implements
		IOrganizationService {

	@Override
	public String getOrganizationJson() {
		List<Organization> orgList = query("from Organization");
		JSONArray array = new JSONArray();
		for(Organization o : orgList){
			JSONObject json = new JSONObject();
			json.put("id", o.getId());
			json.put("pid", o.getPid());
			json.put("name", o.getName());
			json.put("xpath", o.getXpath());
			array.add(json);
		}
		return array.toString();
	}

	@Override
	public boolean hasUsers(Organization id) {
		// TODO Auto-generated method stub
		return false;
	}

}

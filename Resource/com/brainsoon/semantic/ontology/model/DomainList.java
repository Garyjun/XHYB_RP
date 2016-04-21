package com.brainsoon.semantic.ontology.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DomainList extends BaseObjectList {
	private int domainType; //0:教材版本树，1:数据字典树
	
	private String fromId;
	
	private List<Domain> domains;
	
	public int getDomainType() {
		return domainType;	
	}
	
	public void setDomainType(int domainType) {
		this.domainType = domainType;
	}
	
	public String getFromId() {
		return fromId;	
	}
	
	public void setFromId(String fromId) {
		this.fromId = fromId;
	}
	
	public void addDomain(Domain domain) {
		if(domains == null) {
			domains = new ArrayList<Domain>();
		}
		domains.add(domain);
	}

	public void setDomains(List<Domain> domains) {
		this.domains = domains;
	}
	
	public List<Domain> getDomains() {
		return domains;
	}
	
	public String getJsonRes(int status) {
		String json = getJsonResSuccess();		
		if(status != 0)
			json = getJsonResFail(status);
		return json;
	}
	
	private String getJsonResSuccess() {
		String json = "{\"state\":0, \"desc\":\"创建本体描述成功\",\"type\":\"Domain\"";		
		if(getDomains() != null && getDomains().size() > 0) {
			json += ",\"domains\":[";
			for (Domain domain : getDomains()) {
				json += "{\"objectId\":\"" + domain.getObjectId() + "\", \"nodeId\":\"" + domain.getNodeId() + "\"},";
			}
			json += "]";
		}
		json += "}";

		return json;
	}
	
	private String getJsonResFail(int status) {
		String json = "{\"state\":-1, \"desc\":\"创建本体描述失败，\",\"type\":\"Domain\"}";
		return json;
	}
}

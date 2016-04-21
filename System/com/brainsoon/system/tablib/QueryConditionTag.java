package com.brainsoon.system.tablib;

import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspWriter;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.tags.RequestContextAwareTag;

import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.semantic.schema.ontology.CustomMetaData;
import com.brainsoon.semantic.schema.ontology.MetadataDefinition;
import com.brainsoon.semantic.schema.store.jena.service.MetaService;
import com.brainsoon.system.util.MetadataSupport;
import com.brainsoon.system.util.MetadataUtil;

public class QueryConditionTag extends RequestContextAwareTag {
    private static final long serialVersionUID = 1L;
    private String publishType;
    
   	public String getPublishType() {
   		return publishType;
   	}
   	public void setPublishType(String publishType) {
   		this.publishType = publishType;
   	}
	@Override
	protected int doStartTagInternal() throws Exception {
		JspWriter writer = pageContext.getOut();
		ServletRequest request = pageContext.getRequest();
		StringBuffer sb = new StringBuffer(); 
		HttpSession session = pageContext.getSession();
		UserInfo user = (UserInfo) session.getAttribute(LoginUserUtil.USER_SESSION_KEY);
		List<CustomMetaData>  customMetaDatas = MetadataSupport.getAllMetadateList(user,publishType);
		for(CustomMetaData customMetaData:customMetaDatas){
			List<MetadataDefinition> metadataDefinitions = customMetaData.getCustomPropertys();
			for(MetadataDefinition metadataDefinition:metadataDefinitions){
				String name = metadataDefinition.getFieldName();
				int fieldType = metadataDefinition.getFieldType();
				if(fieldType == 6){//分类树需要参与查询
					sb.append("'").append(metadataDefinition.getFieldName()).append("_metaField',");
				}else{
					//如果是精确匹配或者模糊匹配或者二次查询
					if(metadataDefinition.getQueryModel()==2||metadataDefinition.getQueryModel()==3 || (StringUtils.isNotBlank(metadataDefinition.getSecondSearch()) && metadataDefinition.getSecondSearch().equals("0") && metadataDefinition.getFieldType() != 7)){
						sb.append("'").append(metadataDefinition.getFieldName()).append("_metaField',");
					}else if(metadataDefinition.getQueryModel()==4 || (StringUtils.isNotBlank(metadataDefinition.getSecondSearch()) && metadataDefinition.getSecondSearch().equals("0") && metadataDefinition.getFieldType()==7)){//区间查询
						sb.append("'").append(metadataDefinition.getFieldName()).append("_metaField_StartDate',");
						sb.append("'").append(metadataDefinition.getFieldName()).append("_metaField_EndDate',");
					}
				}
			}
		}
		if(sb.length()>1){
			sb.deleteCharAt(sb.length()-1);
		}
		writer.append(sb.toString());
		writer.flush();
		return 0;
	}
}

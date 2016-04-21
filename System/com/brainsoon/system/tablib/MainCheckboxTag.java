package com.brainsoon.system.tablib;

import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspWriter;

import org.springframework.web.servlet.tags.RequestContextAwareTag;

import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.semantic.schema.ontology.CustomMetaData;
import com.brainsoon.semantic.schema.ontology.MetadataDefinition;
import com.brainsoon.semantic.schema.store.jena.service.MetaService;
import com.brainsoon.system.util.MetadataSupport;
import com.brainsoon.system.util.MetadataUtil;

public class MainCheckboxTag extends RequestContextAwareTag {
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
				if(metadataDefinition.getQueryModel()==2||metadataDefinition.getQueryModel()==3){
					int fieldType = metadataDefinition.getFieldType();
					if(fieldType ==3){
						sb.append("var str=\"\";");
						sb.append(" $(\"[name='").append(metadataDefinition.getFieldName()+"_metaField").append("']:checked\").each(function(){");
						sb.append("str+=$(this).val()+\",\";");
						sb.append("});");
						sb.append("if(str.length>0){$('#").append(metadataDefinition.getFieldName()).append("').val(str.substring(0,str.length-1));}");
					}
				}
			}
		}
		writer.append(sb.toString());
		writer.flush();
		return 0;
	}
}

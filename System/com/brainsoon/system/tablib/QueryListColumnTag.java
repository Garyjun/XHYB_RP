package com.brainsoon.system.tablib;

import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspWriter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.tags.RequestContextAwareTag;

import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.semantic.schema.ontology.CustomMetaData;
import com.brainsoon.semantic.schema.ontology.MetadataDefinition;
import com.brainsoon.semantic.schema.store.jena.service.MetaService;
import com.brainsoon.system.util.MetadataSupport;
import com.brainsoon.system.util.MetadataUtil;

public class QueryListColumnTag extends RequestContextAwareTag {
    private static final long serialVersionUID = 1L;
    private Object object;
    private String publishType;
    
   	public String getPublishType() {
   		return publishType;
   	}
   	public void setPublishType(String publishType) {
   		this.publishType = publishType;
   	}
	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}



	@Override
	protected int doStartTagInternal() throws Exception {
		JspWriter writer = pageContext.getOut();
		StringBuffer sb = new StringBuffer(); 
		HttpSession session = pageContext.getSession();
		UserInfo user = (UserInfo) session.getAttribute(LoginUserUtil.USER_SESSION_KEY);
		List<CustomMetaData>  customMetaDatas = MetadataSupport.getAllMetadateList(user,publishType);
		for(CustomMetaData customMetaData:customMetaDatas){
			List<MetadataDefinition> metadataDefinitions = customMetaData.getCustomPropertys();
			for(MetadataDefinition metadataDefinition:metadataDefinitions){
				String viewPriority = metadataDefinition.getViewPriority();
				if(StringUtils.isNotBlank(viewPriority) && viewPriority.contains("2")){
					sb.append("{field:'metadataMap.").append(metadataDefinition.getFieldName()).append("',title:'").append(metadataDefinition.getFieldZhName()).append("',width:fillsize(0.17),align:'center'},");
				}
			}
		}
		writer.append(sb.toString());
		writer.flush();
		return 0;
	}
}

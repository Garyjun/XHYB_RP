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

public class RsetHiddenBatchFlagTag extends RequestContextAwareTag {
    private static final long serialVersionUID = 1L;
	@Override
	protected int doStartTagInternal() throws Exception {
		JspWriter writer = pageContext.getOut();
		ServletRequest request = pageContext.getRequest();
		StringBuffer sb = new StringBuffer(); 
		HttpSession session = pageContext.getSession();
		UserInfo user = (UserInfo) session.getAttribute(LoginUserUtil.USER_SESSION_KEY);
		List<MetadataDefinition>  metadataDefinitions = MetadataSupport. getAllBatchQuery();
		for(MetadataDefinition metadataDefinition:metadataDefinitions){
			String fieldName= metadataDefinition.getFieldName()+"_metaField";
			sb.append("$(\"#"+fieldName+"\").val('');");
		}
		writer.append(sb.toString());
		writer.flush();
		return 0;
	}
}

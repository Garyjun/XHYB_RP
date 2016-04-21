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

public class MainPageConditionTag extends RequestContextAwareTag {
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
		HttpSession session = pageContext.getSession();
		ServletRequest request = pageContext.getRequest();
		String path = getRequestContext().getContextPath();
		String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
		String sb = ""; 
		
		writer.append(sb);
		writer.flush();
		return 0;
	}
}





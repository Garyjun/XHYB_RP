package com.brainsoon.system.service;

import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspWriter;

import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.common.service.IBaseService;
import com.brainsoon.semantic.schema.ontology.CustomMetaData;
import com.brainsoon.semantic.schema.ontology.MetadataDefinition;
import com.brainsoon.system.util.MetadataSupport;
import com.brainsoon.system.util.MetadataUtil;

public interface  ITagUtilService extends IBaseService {

	public String getMainPageConditionTag(HttpSession session,String publishType,String basePath) throws Exception;
	
	
	public String getMainPageTargetTag(HttpSession session,String publishType) throws Exception;
	
}

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

public class HiddenConditionTag extends RequestContextAwareTag {
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
				int filedType = metadataDefinition.getFieldType();
				if(filedType == 6){   //树形
					String fieldName= metadataDefinition.getFieldName()+"_metaField";
					String paramValue = request.getParameter(fieldName);
					sb.append("<input type=\"hidden\" id=\"").append(fieldName).append("\" name=\"").append(fieldName).append("\" value=\"").append(paramValue).append("\" qMatch=\"=\" />");
				}else{
					
					int queryModel = metadataDefinition.getQueryModel();
					/*	//获取是否用于二次查询secondQuery 0表示用于二次查询，1不用于二次查询
					 * String secondQuery = "";
					if(metadataDefinition.getSecondSearch()!=null){
						secondQuery = metadataDefinition.getSecondSearch();
					}
					
					
					//该元数据项用于二次查询
					if(StringUtils.isNotBlank(secondQuery) && secondQuery.equals("0")){
						//时间类的元数据项不用于一般查询但用于二次查询
						if(queryModel == 1 && filedType == 7){
							String startField = metadataDefinition.getFieldName()+"_metaField_StartDate";
							String endField = metadataDefinition.getFieldName()+"_metaField_EndDate";
							String startValue = request.getParameter(startField);
							String endValue = request.getParameter(endField);
							if(startValue!=null && !"".equals(startValue)){
								sb.append("<input type=\"hidden\" id=\"").append(startField).append("\" name=\"").append(startField).append("\" value=\"").append(startValue).append("\" qMatch=\"=\" />");
							}
							if(endValue!=null && !"".equals(endValue)){
								sb.append("<input type=\"hidden\" id=\"").append(endField).append("\" name=\"").append(endField).append("\" value=\"").append(endValue).append("\" qMatch=\"=\" />");
							}
						}else if(queryModel == 1 && filedType!=7){
							String fieldName= metadataDefinition.getFieldName()+"_metaField";
							String paramValue = request.getParameter(fieldName);
							if(paramValue!=null && !"".equals(paramValue) && !"undefined".equals(paramValue)){
								sb.append("<input type=\"hidden\" id=\"").append(fieldName).append("\" name=\"").append(fieldName).append("\" value=\"").append(paramValue).append("\" qMatch=\"like\" />");
							}
						}
					}*/
					
					
					
					if(queryModel == 4){    //区间因为只有时间可以选择区间所以直接拼时间 
						String startField = metadataDefinition.getFieldName()+"_metaField_StartDate";
						String endField = metadataDefinition.getFieldName()+"_metaField_EndDate";
						String startValue = request.getParameter(startField);
						String endValue = request.getParameter(endField);
						if(startValue!=null && !"".equals(startValue)){
							sb.append("<input type=\"hidden\" id=\"").append(startField).append("\" name=\"").append(startField).append("\" value=\"").append(startValue).append("\" qMatch=\"=\" />");
						}
						if(endValue!=null && !"".equals(endValue)){
							sb.append("<input type=\"hidden\" id=\"").append(endField).append("\" name=\"").append(endField).append("\" value=\"").append(endValue).append("\" qMatch=\"=\" />");
						}
					}else{
						String fieldName= metadataDefinition.getFieldName()+"_metaField";
						String paramValue = request.getParameter(fieldName);
						if(paramValue!=null && !"".equals(paramValue) && !"undefined".equals(paramValue)){
							if(queryModel == 2){     //完全匹配查询
								sb.append("<input type=\"hidden\" id=\"").append(fieldName).append("\" name=\"").append(fieldName).append("\" value=\"").append(paramValue).append("\" qMatch=\"=\" />");
							}else if(queryModel == 3){    //模糊查询
								sb.append("<input type=\"hidden\" id=\"").append(fieldName).append("\" name=\"").append(fieldName).append("\" value=\"").append(paramValue).append("\" qMatch=\"like\" />");
							}
						}
					}
				}
			}
		}
		writer.append(sb.toString());
		writer.flush();
		return 0;
	}
}

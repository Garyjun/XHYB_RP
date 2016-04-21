package com.brainsoon.system.tablib;

import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspWriter;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.servlet.tags.RequestContextAwareTag;

import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.common.support.OperDbUtils;
import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.common.util.date.DateUtil;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.semantic.ontology.model.Sco;
import com.brainsoon.semantic.schema.ontology.CustomMetaData;
import com.brainsoon.semantic.schema.ontology.MetadataDefinition;
import com.brainsoon.system.model.MetadataDefinitionGroup;
import com.brainsoon.system.service.IMetaDataModelService;
import com.brainsoon.system.util.ArticleMetadataUtil;
import com.brainsoon.system.util.MetadataSupport;
import com.brainsoon.system.util.MetadataUtil;

public class ArticleMetadataCreateTag extends RequestContextAwareTag{
	private static final Logger logger = Logger.getLogger(RequestContextAwareTag.class);
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

	
	public StringBuffer getGroupHTML(StringBuffer sb,CustomMetaData customMetaData,String groupFieldZhName,String groupId,String basePath){
		List<MetadataDefinition> metadataDefinitions = customMetaData.getCustomPropertys();
		if(metadataDefinitions!=null && metadataDefinitions.size()>0){
			sb.append("<div class=\"portlet\">");
            sb = MetadataUtil.createTitle(sb, groupFieldZhName);
            sb.append("   <div class=\"portlet-body\">");
			sb.append("       <div class=\"container-fluid\">");
			sb.append("           <div class=\"row\">");
			for(MetadataDefinition metadataDefinition:metadataDefinitions){
				String myGroupId = metadataDefinition.getGroupId();
				
				if(StringUtils.isNotBlank(groupId) && groupId.equals(myGroupId)){
					sb = getHtml(sb, metadataDefinition, basePath);
				}else if(StringUtils.isNotBlank(groupFieldZhName) && groupFieldZhName.equals("通用元数据")){
					sb = getHtml(sb, metadataDefinition, basePath);
				}
			}
			sb.append("           </div>");
			sb.append("       </div>");
			sb.append("   </div>");
			sb.append("</div>");
			
			
			
	    }
		return sb;
	}

	
	public StringBuffer getHtml(StringBuffer sb,MetadataDefinition metadataDefinition,String basePath){
		String viewPriority = metadataDefinition.getViewPriority();
		logger.info("viewPriority------------------------"+viewPriority);
		if(StringUtils.isNotBlank(viewPriority) && viewPriority.contains("3")){
			int fieldType = metadataDefinition.getFieldType();
			logger.info("fieldType------------------------"+fieldType);
			
			if(metadataDefinition.getFieldName().equals("authorStartTime")){
				System.out.println("");
			}
			
			if(!metadataDefinition.getFieldName().equals("sourceType")){
				switch(fieldType){
			    case 1:
			    	sb = ArticleMetadataUtil.createInput(sb,"", metadataDefinition,object,true);
			    	logger.info("input------------------------");
			    	break;
			    case 2:	
			    	sb = ArticleMetadataUtil.createSelect(sb,"", metadataDefinition,object,true,false);
			    	logger.info("select------------------------");
			    	break;
			    case 3:
			    	sb = ArticleMetadataUtil.createMoreSelect(sb,"", metadataDefinition,object,true,"");
//			    	sb = MetadataUtil.createCheckbox(sb,"",metadataDefinition,object,true);
			    	logger.info("checkbox------------------------");
			    	break;
			    case 4:	
			    	sb = ArticleMetadataUtil.createMoreSelect(sb,"", metadataDefinition,object,true,"onlySelect");
//			    	sb = MetadataUtil.createRadio(sb,"", metadataDefinition,object,true);
			    	logger.info("createRadio------------------------");
			    	break;
			    case 5:	
			    	sb = ArticleMetadataUtil.createTextarea(sb,"", metadataDefinition,object,true);
			    	logger.info("createTextarea------------------------");
			    	break;
			    case 6:	
			    	sb = ArticleMetadataUtil.createLookup(sb,"", metadataDefinition,object,true,basePath);
			    	logger.info("createLookup------------------------");
			    	break;
			    case 7:	
			    	sb = ArticleMetadataUtil.createDateTime(sb,"", metadataDefinition,object,true,"yyyy-MM-dd");
			    	logger.info("createDateTime----------------------");
			    	break;
			    case 8:	
			    	sb = ArticleMetadataUtil.createURL(sb,"", metadataDefinition,object,true);
			    	logger.info("createURL------------------------");
			    	break;
			    case 9:
			    //	sb = MetadataUtil.createSelect(sb, metadataDefinition,object,true,false);
			    	sb = ArticleMetadataUtil.createMultiSelect(sb, metadataDefinition, object, true, true);
			    	break;
			    case 10:
			    	sb = ArticleMetadataUtil.createLookupPeopleCompany(sb,"", metadataDefinition,object,true,basePath,"");
//			    	sb = MetadataUtil.createMoreSelect(sb,"", metadataDefinition,object,true,"");
			    	logger.info("createURL------------------------");
			    	break;
			    case 11:
			    	sb = ArticleMetadataUtil.createLookupPeopleCompany(sb,"", metadataDefinition,object,true,basePath,"company");
//			    	sb = MetadataUtil.createLookupCompanyUnit(sb,"", metadataDefinition,object,true,basePath);
			    	logger.info("createURL------------------------");
			    	break;
			   }
			}
		}
		return sb;
	}
	
	
	@Override
	protected int doStartTagInternal() throws Exception {
		logger.info("publishType------------------------"+publishType);
		JspWriter writer = pageContext.getOut();
		ServletRequest request = pageContext.getRequest();
		HttpSession session = pageContext.getSession();
		UserInfo user = (UserInfo) session.getAttribute(LoginUserUtil.USER_SESSION_KEY);
		logger.info("userpublishType------------------------"+publishType);
		List<CustomMetaData>  customMetaDatas = MetadataSupport.getAllMetadateList(user,publishType);
		String path = getRequestContext().getContextPath();
		logger.info("path------------------------"+path);
		String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
		StringBuffer sb = new StringBuffer(); 
		for(CustomMetaData customMetaData:customMetaDatas){
			String groupFieldZhName = customMetaData.getNameCN();
			if(StringUtils.isNotBlank(groupFieldZhName) && groupFieldZhName.equals("通用元数据")){
				sb = getGroupHTML(sb, customMetaData, groupFieldZhName,"", basePath);
			}else{
				//根据元数据ID查询分组信息
				IMetaDataModelService dictNameService = (IMetaDataModelService)BeanFactoryUtil.getBean("metaDataModelService");
				List<MetadataDefinitionGroup> listMdg = dictNameService.doTypeChildList(Integer.parseInt(publishType));
				for (MetadataDefinitionGroup metadataDefinitionGroup : listMdg) {
					Long groupId = metadataDefinitionGroup.getId();
				    groupFieldZhName = metadataDefinitionGroup.getFieldZhName();
				    sb = getGroupHTML(sb, customMetaData, groupFieldZhName, groupId+"", basePath);
				}
			}
		}
		
		if(object!=null){
			Sco sco = (Sco)object;
			if(sco!=null && sco.getObjectId()!=null){
				sb.append("<div class=\"portlet\">");
				sb = MetadataUtil.createTitle(sb, "创建信息");
				sb.append("   <div class=\"portlet-body\">");
				sb.append("       <div class=\"container-fluid\">");
				sb.append("           <div class=\"row\">");
				sb.append("               <div class=\"").append(MetadataUtil.COL_MD_6).append("\">");
				sb.append("                   <div class=\"").append(MetadataUtil.groupCSS).append("\">");
				sb.append("                           <label class=\"").append(MetadataUtil.labelCSS).append(" ").append(MetadataUtil.COL_MD_4).append("\">");
				sb.append("创建人").append(" :</label>");
				sb.append("                           <div class=\"").append(MetadataUtil.COL_MD_8).append("\">");
				sb.append("                           <div class=\"form-control-static\">");
				String creator = "";
				if (StringUtils.isNotBlank(creator)) {
					sb.append(OperDbUtils.getUserNameById(creator));
				}
				sb.append("                     	  </div>");
				sb.append("                     	  </div>");
				sb.append("                     	  </div>");
				sb.append("                     	  </div>");
				sb.append("               <div class=\"").append(MetadataUtil.COL_MD_6).append("\">");
				sb.append("                   <div class=\"").append(MetadataUtil.groupCSS).append("\">");
				sb.append("                           <label class=\"").append(MetadataUtil.labelCSS).append(" ").append(MetadataUtil.COL_MD_4).append("\">");
				sb.append("创建时间").append(" :</label>");
				sb.append("                           <div class=\"").append(MetadataUtil.COL_MD_8).append("\">");
				sb.append("                           <div class=\"form-control-static\">");
				String createTime = "";
				if (StringUtils.isNotBlank(createTime)) {
					sb.append(DateUtil.convertLongToStrings(createTime));
				}
				sb.append("                     	  </div>");
				sb.append("           </div>");
				sb.append("       </div>");
				sb.append("   </div>");
				sb.append("</div>");
				sb.append("</div>");
				sb.append("</div>");
				sb.append("</div>");
			}
		}
		writer.append(sb.toString());
		writer.flush();
		return 0;
	}

}

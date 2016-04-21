package com.brainsoon.system.tablib;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.brainsoon.semantic.ontology.model.Entry;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspWriter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.tags.RequestContextAwareTag;

import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.common.support.OperDbUtils;
import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.common.util.date.DateUtil;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.semantic.schema.ontology.CustomMetaData;
import com.brainsoon.semantic.schema.ontology.MetadataDefinition;
import com.brainsoon.semantic.schema.store.jena.service.MetaService;
import com.brainsoon.system.model.MetadataDefinitionGroup;
import com.brainsoon.system.service.IDictNameService;
import com.brainsoon.system.service.IMetaDataModelService;
import com.brainsoon.system.util.AssetMetadataUtil;
import com.brainsoon.system.util.EntryMetadataUtil;
import com.brainsoon.system.util.MetadataSupport;
import com.brainsoon.system.util.MetadataUtil;

public class AssetMetadataDetailTag extends RequestContextAwareTag {
    private static final long serialVersionUID = 1L;
    private Object object;
    private String publishType;
    private String flag;
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
	
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
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
			    	sb = AssetMetadataUtil.createInput(sb,"", metadataDefinition,object,false);
			    	logger.info("input------------------------");
			    	break;
			    case 2:	
			    	sb = AssetMetadataUtil.createSelect(sb,"", metadataDefinition,object,false,false);
			    	logger.info("select------------------------");
			    	break;
			    case 3:
			    	sb = AssetMetadataUtil.createMoreSelect(sb,"", metadataDefinition,object,false,"");
//			    	sb = MetadataUtil.createCheckbox(sb,"",metadataDefinition,object,true);
			    	logger.info("checkbox------------------------");
			    	break;
			    case 4:	
			    	sb = AssetMetadataUtil.createMoreSelect(sb,"", metadataDefinition,object,false,"onlySelect");
//			    	sb = MetadataUtil.createRadio(sb,"", metadataDefinition,object,true);
			    	logger.info("createRadio------------------------");
			    	break;
			    case 5:	
			    	sb = AssetMetadataUtil.createTextarea(sb,"", metadataDefinition,object,false,basePath,flag);
			    	logger.info("createTextarea------------------------");
			    	break;
			    case 6:	
			    	sb = AssetMetadataUtil.createLookup(sb,"", metadataDefinition,object,false,basePath);
			    	logger.info("createLookup------------------------");
			    	break;
			    case 7:	
			    	sb = AssetMetadataUtil.createDateTime(sb,"", metadataDefinition,object,false,"yyyy-MM-dd");
			    	logger.info("createDateTime----------------------");
			    	break;
			    case 8:	
			    	sb = AssetMetadataUtil.createURL(sb,"", metadataDefinition,object,false);
			    	logger.info("createURL------------------------");
			    	break;
			    case 9:
			    //	sb = MetadataUtil.createSelect(sb, metadataDefinition,object,true,false);
			    	sb = AssetMetadataUtil.createMultiSelect(sb, metadataDefinition, object, false, false);
			    	break;
			    case 10:
			    	sb = AssetMetadataUtil.createLookupPeopleCompany(sb,"", metadataDefinition,object,false,basePath,"");
//			    	sb = MetadataUtil.createMoreSelect(sb,"", metadataDefinition,object,true,"");
			    	logger.info("createURL------------------------");
			    	break;
			    case 11:
			    	sb = AssetMetadataUtil.createLookupPeopleCompany(sb,"", metadataDefinition,object,false,basePath,"company");
//			    	sb = MetadataUtil.createLookupCompanyUnit(sb,"", metadataDefinition,object,false,basePath);
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
//					 if(metadataDefinitionGroup.getFieldName()!=null && metadataDefinitionGroup.getFieldName().equals("importCoverType")){
//					    	System.out.println("");
//					    }
					Long groupId = metadataDefinitionGroup.getId();
				    groupFieldZhName = metadataDefinitionGroup.getFieldZhName();
				    sb = getGroupHTML(sb, customMetaData, groupFieldZhName, groupId+"", basePath);
				}
			}
			
//		IDictNameService dictNameService = (IDictNameService)BeanFactoryUtil.getBean("dictNameService");
		}
//		if(object!=null){
//			String resType="";
//			Entry en = (Entry)object;
//			if(en!=null && en.getObjectId()!=null){
//				sb.append("<div class=\"portlet\">");
//				sb = MetadataUtil.createTitle(sb, "创建信息");
//				sb.append("   <div class=\"portlet-body\">");
//				sb.append("       <div class=\"container-fluid\">");
//				sb.append("           <div class=\"row\">");
//				sb.append("               <div class=\"").append(MetadataUtil.COL_MD_6).append("\">");
//				sb.append("                   <div class=\"").append(MetadataUtil.groupCSS).append("\">");
//				sb.append("                           <label class=\"").append(MetadataUtil.labelCSS).append(" ").append(MetadataUtil.COL_MD_4).append("\">");
//				sb.append("创建人").append(" :</label>");
//				sb.append("                           <div class=\"").append(MetadataUtil.COL_MD_8).append("\">");
//				sb.append("                           <div class=\"form-control-static\">");
//				String creator = en.getCreator();
//				if (StringUtils.isNotBlank(creator)) {
//					sb.append(OperDbUtils.getUserNameById(creator));
//				}
//				sb.append("                     	  </div>");
//				sb.append("                     	  </div>");
//				sb.append("                     	  </div>");
//				sb.append("                     	  </div>");
//				sb.append("               <div class=\"").append(MetadataUtil.COL_MD_6).append("\">");
//				sb.append("                   <div class=\"").append(MetadataUtil.groupCSS).append("\">");
//				sb.append("                           <label class=\"").append(MetadataUtil.labelCSS).append(" ").append(MetadataUtil.COL_MD_4).append("\">");
//				sb.append("创建时间").append(" :</label>");
//				sb.append("                           <div class=\"").append(MetadataUtil.COL_MD_8).append("\">");
//				sb.append("                           <div class=\"form-control-static\">");
//				String createTime = ca.getCreateTime();
//				if (StringUtils.isNotBlank(createTime)) {
//					sb.append(DateUtil.convertLongToStrings(createTime));
//				}
//				sb.append("                     	  </div>");
//				sb.append("           </div>");
//				sb.append("       </div>");
//				sb.append("   </div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//			}
//			}
		writer.append(sb.toString());
		writer.flush();
		return 0;
	}
}
package com.brainsoon.system.tablib;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspWriter;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.aspectj.weaver.Iterators;
import org.springframework.web.servlet.tags.RequestContextAwareTag;

import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.common.support.MetadataDefinitionGroupCacheMap;
import com.brainsoon.common.support.OperDbUtils;
import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.common.util.date.DateUtil;
import com.brainsoon.resource.service.IBatchImportResService;
import com.brainsoon.resource.util.ResUtils;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.semantic.schema.ontology.CustomMetaData;
import com.brainsoon.semantic.schema.ontology.MetadataDefinition;
import com.brainsoon.semantic.schema.store.jena.service.MetaService;
import com.brainsoon.system.model.MetadataDefinitionGroup;
import com.brainsoon.system.service.IDictNameService;
import com.brainsoon.system.service.IMetaDataModelService;
import com.brainsoon.system.util.MetadataSupport;
import com.brainsoon.system.util.MetadataUtil;

public class ObjectMetadataCreateTag extends RequestContextAwareTag {
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
			    	sb = MetadataUtil.createInput(sb,"", metadataDefinition,object,true);
			    	logger.info("input------------------------");
			    	break;
			    case 2:	
			    	sb = MetadataUtil.createSelect(sb,"", metadataDefinition,object,true,false);
			    	logger.info("select------------------------");
			    	break;
			    case 3:
			    	sb = MetadataUtil.createMoreSelect(sb,"", metadataDefinition,object,true,"");
//			    	sb = MetadataUtil.createCheckbox(sb,"",metadataDefinition,object,true);
			    	logger.info("checkbox------------------------");
			    	break;
			    case 4:	
			    	sb = MetadataUtil.createMoreSelect(sb,"", metadataDefinition,object,true,"onlySelect");
//			    	sb = MetadataUtil.createRadio(sb,"", metadataDefinition,object,true);
			    	logger.info("createRadio------------------------");
			    	break;
			    case 5:	
			    	sb = MetadataUtil.createTextarea(sb,"", metadataDefinition,object,true);
			    	logger.info("createTextarea------------------------");
			    	break;
			    case 6:	
			    	sb = MetadataUtil.createLookup(sb,"", metadataDefinition,object,true,basePath);
			    	logger.info("createLookup------------------------");
			    	break;
			    case 7:	
			    	sb = MetadataUtil.createDateTime(sb,"", metadataDefinition,object,true,"yyyy-MM-dd");
			    	logger.info("createDateTime----------------------");
			    	break;
			    case 8:	
			    	sb = MetadataUtil.createURL(sb,"", metadataDefinition,object,true);
			    	logger.info("createURL------------------------");
			    	break;
			    case 9:
			    //	sb = MetadataUtil.createSelect(sb, metadataDefinition,object,true,false);
			    	sb = MetadataUtil.createMultiSelect(sb, metadataDefinition, object, true, true);
			    	break;
			    case 10:
			    	sb = MetadataUtil.createLookupPeopleCompany(sb,"", metadataDefinition,object,true,basePath,"");
//			    	sb = MetadataUtil.createMoreSelect(sb,"", metadataDefinition,object,true,"");
			    	logger.info("createURL------------------------");
			    	break;
			    case 11:
			    	sb = MetadataUtil.createLookupPeopleCompany(sb,"", metadataDefinition,object,true,basePath,"company");
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
//					 if(metadataDefinitionGroup.getFieldName()!=null && metadataDefinitionGroup.getFieldName().equals("importCoverType")){
//					    	System.out.println("");
//					    }
					Long groupId = metadataDefinitionGroup.getId();
				    groupFieldZhName = metadataDefinitionGroup.getFieldZhName();
				    sb = getGroupHTML(sb, customMetaData, groupFieldZhName, groupId+"", basePath);
				}
			}
				//根据元数据ID查询分组信息
//				IMetaDataModelService dictNameService = (IMetaDataModelService)BeanFactoryUtil.getBean("metaDataModelService");
//				List<MetadataDefinitionGroup> listMdg = dictNameService.doTypeChildList(Integer.parseInt(publishType));
//				for (MetadataDefinitionGroup metadataDefinitionGroup : listMdg) {
//					Long groupId = metadataDefinitionGroup.getId();
//					List<MetadataDefinition> metadataDefinitions = customMetaData.getCustomPropertys();
//					if(metadataDefinitions!=null && metadataDefinitions.size()>0){
//						sb.append("<div class=\"portlet\">");
//			            sb = MetadataUtil.createTitle(sb, groupFieldZhName);
//			            sb.append("   <div class=\"portlet-body\">");
//						sb.append("       <div class=\"container-fluid\">");
//						sb.append("           <div class=\"row\">");
//						for(MetadataDefinition metadataDefinition:metadataDefinitions){
//							String myGroupId = metadataDefinition.getGroupId();
//							if(groupId.toString().equals(myGroupId)){
//								String viewPriority = metadataDefinition.getViewPriority();
//								logger.info("viewPriority------------------------"+viewPriority);
//								if(StringUtils.isNotBlank(viewPriority) && viewPriority.contains("3")){
//									int fieldType = metadataDefinition.getFieldType();
//									logger.info("fieldType------------------------"+fieldType);
//									if(metadataDefinition.getFieldName().equals("sourceType")){
//										continue;
//									}
//									if(metadataDefinition.getFieldName().equals("authorStartTime")){
//										System.out.println("");
//									}
//									switch(fieldType){
//									    case 1:
//									    	sb = MetadataUtil.createInput(sb,"", metadataDefinition,object,true);
//									    	logger.info("input------------------------");
//									    	break;
//									    case 2:	
//									    	sb = MetadataUtil.createSelect(sb,"", metadataDefinition,object,true,false);
//									    	logger.info("select------------------------");
//									    	break;
//									    case 3:
//									    	sb = MetadataUtil.createCheckbox(sb,"",metadataDefinition,object,true);
//									    	logger.info("checkbox------------------------");
//									    	break;
//									    case 4:	
//									    	sb = MetadataUtil.createRadio(sb,"", metadataDefinition,object,true);
//									    	logger.info("createRadio------------------------");
//									    	break;
//									    case 5:	
//									    	sb = MetadataUtil.createTextarea(sb,"", metadataDefinition,object,true);
//									    	logger.info("createTextarea------------------------");
//									    	break;
//									    case 6:	
//									    	sb = MetadataUtil.createLookup(sb,"", metadataDefinition,object,true,basePath);
//									    	logger.info("createLookup------------------------");
//									    	break;
//									    case 7:	
//									    	sb = MetadataUtil.createDateTime(sb,"", metadataDefinition,object,true,"yyyy-MM-dd");
//									    	logger.info("createDateTime----------------------");
//									    	break;
//									    case 8:	
//									    	sb = MetadataUtil.createURL(sb,"", metadataDefinition,object,true);
//									    	logger.info("createURL------------------------");
//									    	break;
//									    case 9:	
//									    //	sb = MetadataUtil.createSelect(sb, metadataDefinition,object,true,false);
//									    	sb = MetadataUtil.createMultiSelect(sb, metadataDefinition, object, true, true);
//									    	break;
//									}
//								}
//							}
//						}
//						sb.append("           </div>");
//						sb.append("       </div>");
//						sb.append("   </div>");
//						sb.append("</div>");
//				    }
			//	}
		}
		
		
		
//		IDictNameService dictNameService = (IDictNameService)BeanFactoryUtil.getBean("dictNameService");
		if(object!=null){
//			String resType="";
			Ca ca = (Ca)object;
//			Map<String,String> map = dictNameService.getDictMapByName("版权元数据类型");
//			Iterator<Entry<String, String>> it = map.entrySet().iterator();
//			while (it.hasNext()) {
//				Map.Entry pairs = (Map.Entry) it.next();
//				if(StringUtils.isNotBlank(pairs.getKey().toString())&&StringUtils.isNotBlank(pairs.getValue().toString())){
//					if(ca.getPublishType()!=null && ca.getPublishType().equals(pairs.getKey().toString())){
//						resType = pairs.getValue().toString();
//					}
//				}
//			}
//			if(StringUtils.isNotBlank(ca.getHasCopyright())){
//				sb.append("<input type=\"hidden\" name=\"hasCopyright\" id=\"hasCopyright\" value=\"").append(ca.getHasCopyright()).append("\" />");
//			}
//			if(ca.getCopyRightMetaData()!=null && ca.getObjectId()!=null && resType.equals("bookRes")){//图书资源
//				sb.append("<div class=\"portlet\">");
//				sb = MetadataUtil.createTitle(sb, "版权元数据");
//				sb.append("   <div class=\"portlet-body\">");
//				sb.append("       <div class=\"container-fluid\">");
//				sb.append("           <div class=\"row\">");
//				sb.append("               <div class=\"").append(MetadataUtil.COL_MD_6).append("\">");
//				sb.append("                   <div class=\"").append(MetadataUtil.groupCSS).append("\">");
//				sb.append("                           <label class=\"").append(MetadataUtil.labelCSS).append(" ").append(MetadataUtil.COL_MD_4).append("\">");
//				sb.append("授权生效时间").append(" :</label>");
//				sb.append("                           <div class=\"").append(MetadataUtil.COL_MD_8).append("\">");
//				sb.append("                           <div class=\"form-control-static\">");
//				String licenStartTime = ca.getCopyRightMetaData().getLicenStartTime();
//				if (StringUtils.isNotBlank(licenStartTime)) {
//					sb.append(licenStartTime);
//				}
//				sb.append("                     	  </div>");
//				sb.append("                     	  </div>");
//				sb.append("                     	  </div>");
//				sb.append("                     	  </div>");
//				sb.append("               <div class=\"").append(MetadataUtil.COL_MD_6).append("\">");
//				sb.append("                   <div class=\"").append(MetadataUtil.groupCSS).append("\">");
//				sb.append("                           <label class=\"").append(MetadataUtil.labelCSS).append(" ").append(MetadataUtil.COL_MD_4).append("\">");
//				sb.append("授权终止时间").append(" :</label>");
//				sb.append("                           <div class=\"").append(MetadataUtil.COL_MD_8).append("\">");
//				sb.append("                           <div class=\"form-control-static\">");
//				String licenEndTime = ca.getCopyRightMetaData().getLicenEndTime();
//				if (StringUtils.isNotBlank(licenEndTime)) {
//					sb.append(licenEndTime);
//				}
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				
//				sb.append("               <div class=\"").append(MetadataUtil.COL_MD_6).append("\">");
//				sb.append("                   <div class=\"").append(MetadataUtil.groupCSS).append("\">");
//				sb.append("                           <label class=\"").append(MetadataUtil.labelCSS).append(" ").append(MetadataUtil.COL_MD_4).append("\">");
//				sb.append("合同时限").append(" :</label>");
//				sb.append("                           <div class=\"").append(MetadataUtil.COL_MD_8).append("\">");
//				sb.append("                           <div class=\"form-control-static\">");
//				String contractTimeline = ca.getCopyRightMetaData().getContractTimeline();
//				if (StringUtils.isNotBlank(contractTimeline)) {
//					sb.append(contractTimeline);
//				}
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				
//				sb.append("               <div class=\"").append(MetadataUtil.COL_MD_6).append("\">");
//				sb.append("                   <div class=\"").append(MetadataUtil.groupCSS).append("\">");
//				sb.append("                           <label class=\"").append(MetadataUtil.labelCSS).append(" ").append(MetadataUtil.COL_MD_4).append("\">");
//				sb.append("授权语种").append(" :</label>");
//				sb.append("                           <div class=\"").append(MetadataUtil.COL_MD_8).append("\">");
//				sb.append("                           <div class=\"form-control-static\">");
//				String language = "";
//				String licensLanguage = ca.getCopyRightMetaData().getLicensLanguage();
////				if(licensLanguage!=null){
////					for(String value:licensLanguage){
////						language =language+ value+",";
////					}
////				}
//				if (StringUtils.isNotBlank(licensLanguage)) {
//					sb.append(licensLanguage);
//				}
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				
//				sb.append("               <div class=\"").append(MetadataUtil.COL_MD_6).append("\">");
//				sb.append("                   <div class=\"").append(MetadataUtil.groupCSS).append("\">");
//				sb.append("                           <label class=\"").append(MetadataUtil.labelCSS).append(" ").append(MetadataUtil.COL_MD_4).append("\">");
//				sb.append("授权范围").append(" :</label>");
//				sb.append("                           <div class=\"").append(MetadataUtil.COL_MD_8).append("\">");
//				sb.append("                           <div class=\"form-control-static\">");
//				String range = "";
//				String licensRange = ca.getCopyRightMetaData().getLicensRange();
////				if(licensRange!=null){
////					for(String value:licensLanguage){
////						range =range+ value+",";
////					}
////				}
//				if (StringUtils.isNotBlank(licensRange)) {
//					sb.append(licensRange);
//				}
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				
//				sb.append("               <div class=\"").append(MetadataUtil.COL_MD_6).append("\">");
//				sb.append("                   <div class=\"").append(MetadataUtil.groupCSS).append("\">");
//				sb.append("                           <label class=\"").append(MetadataUtil.labelCSS).append(" ").append(MetadataUtil.COL_MD_4).append("\">");
//				sb.append("是否独家").append(" :</label>");
//				sb.append("                           <div class=\"").append(MetadataUtil.COL_MD_8).append("\">");
//				sb.append("                           <div class=\"form-control-static\">");
//				String exclusive = "";
//				String isExclusive = ca.getCopyRightMetaData().getIsExclusive();
////				if(isExclusive!=null){
////					for(String value:isExclusive){
////						exclusive =exclusive+ value+",";
////					}
////				}
//				if (StringUtils.isNotBlank(isExclusive)) {
//					sb.append(isExclusive);
//				}
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				
//				sb.append("               <div class=\"").append(MetadataUtil.COL_MD_6).append("\">");
//				sb.append("                   <div class=\"").append(MetadataUtil.groupCSS).append("\">");
//				sb.append("                           <label class=\"").append(MetadataUtil.labelCSS).append(" ").append(MetadataUtil.COL_MD_4).append("\">");
//				sb.append("有无转授").append(" :</label>");
//				sb.append("                           <div class=\"").append(MetadataUtil.COL_MD_8).append("\">");
//				sb.append("                           <div class=\"form-control-static\">");
//				String delegate = "";
//				String isDelegate = ca.getCopyRightMetaData().getIsDelegate();
////				if(isDelegate!=null){
////					for(String value:isDelegate){
////						delegate =delegate+ value+",";
////					}
////				}
//				if (StringUtils.isNotBlank(isDelegate)) {
//					sb.append(isDelegate);
//				}
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				
//				sb.append("               <div class=\"").append(MetadataUtil.COL_MD_6).append("\">");
//				sb.append("                   <div class=\"").append(MetadataUtil.groupCSS).append("\">");
//				sb.append("                           <label class=\"").append(MetadataUtil.labelCSS).append(" ").append(MetadataUtil.COL_MD_4).append("\">");
//				sb.append("其他限定").append(" :</label>");
//				sb.append("                           <div class=\"").append(MetadataUtil.COL_MD_8).append("\">");
//				sb.append("                           <div class=\"form-control-static\">");
//				String otherLimited = ca.getCopyRightMetaData().getOtherLimited();
//				if (StringUtils.isNotBlank(otherLimited)) {
//					sb.append(otherLimited);
//				}
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				
//				sb.append("               <div class=\"").append(MetadataUtil.COL_MD_6).append("\">");
//				sb.append("                   <div class=\"").append(MetadataUtil.groupCSS).append("\">");
//				sb.append("                           <label class=\"").append(MetadataUtil.labelCSS).append(" ").append(MetadataUtil.COL_MD_4).append("\">");
//				sb.append("是否供集").append(" :</label>");
//				sb.append("                           <div class=\"").append(MetadataUtil.COL_MD_8).append("\">");
//				sb.append("                           <div class=\"form-control-static\">");
//				String forSet = "";
//				String isForSet = ca.getCopyRightMetaData().getIsForSet();
////				if(isForSet!=null){
////					for(String value:isForSet){
////						forSet =forSet+ value+",";
////					}
////				}
//				if (StringUtils.isNotBlank(isForSet)) {
//					sb.append(isForSet);
//				}
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				
//				sb.append("               <div class=\"").append(MetadataUtil.COL_MD_6).append("\">");
//				sb.append("                   <div class=\"").append(MetadataUtil.groupCSS).append("\">");
//				sb.append("                           <label class=\"").append(MetadataUtil.labelCSS).append(" ").append(MetadataUtil.COL_MD_4).append("\">");
//				sb.append("有无信息网络传播权").append(" :</label>");
//				sb.append("                           <div class=\"").append(MetadataUtil.COL_MD_8).append("\">");
//				sb.append("                           <div class=\"form-control-static\">");
//				String inforTranRight = "";
//				String isInforTranRight = ca.getCopyRightMetaData().getIsInforTranRight();
////				if(isInforTranRight!=null){
////					for(String value:isInforTranRight){
////						inforTranRight =inforTranRight+ value+",";
////					}
////				}
//				if (StringUtils.isNotBlank(isInforTranRight)) {
//					sb.append(isInforTranRight);
//				}
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				
//				sb.append("               <div class=\"").append(MetadataUtil.COL_MD_6).append("\">");
//				sb.append("                   <div class=\"").append(MetadataUtil.groupCSS).append("\">");
//				sb.append("                           <label class=\"").append(MetadataUtil.labelCSS).append(" ").append(MetadataUtil.COL_MD_4).append("\">");
//				sb.append("第三方渠道及分成").append(" :</label>");
//				sb.append("                           <div class=\"").append(MetadataUtil.COL_MD_8).append("\">");
//				sb.append("                           <div class=\"form-control-static\">");
//				String channelAndDivide = ca.getCopyRightMetaData().getChannelAndDivide();
//				if (StringUtils.isNotBlank(channelAndDivide)) {
//					sb.append(channelAndDivide);
//				}
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				
//				sb.append("               <div class=\"").append(MetadataUtil.COL_MD_6).append("\">");
//				sb.append("                   <div class=\"").append(MetadataUtil.groupCSS).append("\">");
//				sb.append("                           <label class=\"").append(MetadataUtil.labelCSS).append(" ").append(MetadataUtil.COL_MD_4).append("\">");
//				sb.append("分成比例").append(" :</label>");
//				sb.append("                           <div class=\"").append(MetadataUtil.COL_MD_8).append("\">");
//				sb.append("                           <div class=\"form-control-static\">");
//				String intoProportion = ca.getCopyRightMetaData().getIntoProportion();
//				if (StringUtils.isNotBlank(intoProportion)) {
//					sb.append(intoProportion);
//				}
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				
//				sb.append("               <div class=\"").append(MetadataUtil.COL_MD_6).append("\">");
//				sb.append("                   <div class=\"").append(MetadataUtil.groupCSS).append("\">");
//				sb.append("                           <label class=\"").append(MetadataUtil.labelCSS).append(" ").append(MetadataUtil.COL_MD_4).append("\">");
//				sb.append("试读比例").append(" :</label>");
//				sb.append("                           <div class=\"").append(MetadataUtil.COL_MD_8).append("\">");
//				sb.append("                           <div class=\"form-control-static\">");
//				String tryReadProportion = ca.getCopyRightMetaData().getTryReadProportion();
//				if (StringUtils.isNotBlank(tryReadProportion)) {
//					sb.append(tryReadProportion);
//				}
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				
//				sb.append("               <div class=\"").append(MetadataUtil.COL_MD_6).append("\">");
//				sb.append("                   <div class=\"").append(MetadataUtil.groupCSS).append("\">");
//				sb.append("                           <label class=\"").append(MetadataUtil.labelCSS).append(" ").append(MetadataUtil.COL_MD_4).append("\">");
//				sb.append("影视").append(" :</label>");
//				sb.append("                           <div class=\"").append(MetadataUtil.COL_MD_8).append("\">");
//				sb.append("                           <div class=\"form-control-static\">");
//				String video = ca.getCopyRightMetaData().getVideo();
//				if (StringUtils.isNotBlank(video)) {
//					sb.append(video);
//				}
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				
//			}
//			
//			
//			if(ca.getCopyRightMetaData()!=null && ca.getObjectId()!=null && resType.equals("voiceRes")){//有声资源
//				sb.append("<div class=\"portlet\">");
//				sb = MetadataUtil.createTitle(sb, "版权元数据");
//				sb.append("   <div class=\"portlet-body\">");
//				sb.append("       <div class=\"container-fluid\">");
//				sb.append("           <div class=\"row\">");
//				sb.append("               <div class=\"").append(MetadataUtil.COL_MD_6).append("\">");
//				sb.append("                   <div class=\"").append(MetadataUtil.groupCSS).append("\">");
//				sb.append("                           <label class=\"").append(MetadataUtil.labelCSS).append(" ").append(MetadataUtil.COL_MD_4).append("\">");
//				sb.append("文字权属所有人").append(" :</label>");
//				sb.append("                           <div class=\"").append(MetadataUtil.COL_MD_8).append("\">");
//				sb.append("                           <div class=\"form-control-static\">");
//				String ownership = ca.getCopyRightMetaData().getOwnership();
//				if (StringUtils.isNotBlank(ownership)) {
//					sb.append(ownership);
//				}
//				sb.append("                     	  </div>");
//				sb.append("                     	  </div>");
//				sb.append("                     	  </div>");
//				sb.append("                     	  </div>");
//				sb.append("               <div class=\"").append(MetadataUtil.COL_MD_6).append("\">");
//				sb.append("                   <div class=\"").append(MetadataUtil.groupCSS).append("\">");
//				sb.append("                           <label class=\"").append(MetadataUtil.labelCSS).append(" ").append(MetadataUtil.COL_MD_4).append("\">");
//				sb.append("文字授权许可权力").append(" :</label>");
//				sb.append("                           <div class=\"").append(MetadataUtil.COL_MD_8).append("\">");
//				sb.append("                           <div class=\"form-control-static\">");
//				String permitSort = ca.getCopyRightMetaData().getPermitSort();
//				if (StringUtils.isNotBlank(permitSort)) {
//					sb.append(permitSort);
//				}
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				
//				sb.append("               <div class=\"").append(MetadataUtil.COL_MD_6).append("\">");
//				sb.append("                   <div class=\"").append(MetadataUtil.groupCSS).append("\">");
//				sb.append("                           <label class=\"").append(MetadataUtil.labelCSS).append(" ").append(MetadataUtil.COL_MD_4).append("\">");
//				sb.append("文字授权合同编码").append(" :</label>");
//				sb.append("                           <div class=\"").append(MetadataUtil.COL_MD_8).append("\">");
//				sb.append("                           <div class=\"form-control-static\">");
//				String contractCode = ca.getCopyRightMetaData().getContractCode();
//				if (StringUtils.isNotBlank(contractCode)) {
//					sb.append(contractCode);
//				}
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				
//				sb.append("               <div class=\"").append(MetadataUtil.COL_MD_6).append("\">");
//				sb.append("                   <div class=\"").append(MetadataUtil.groupCSS).append("\">");
//				sb.append("                           <label class=\"").append(MetadataUtil.labelCSS).append(" ").append(MetadataUtil.COL_MD_4).append("\">");
//				sb.append("文字授权生效时间").append(" :</label>");
//				sb.append("                           <div class=\"").append(MetadataUtil.COL_MD_8).append("\">");
//				sb.append("                           <div class=\"form-control-static\">");
//				String authEffeDate = ca.getCopyRightMetaData().getAuthEffeDate();
//				if (StringUtils.isNotBlank(authEffeDate)) {
//					sb.append(authEffeDate);
//				}
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				
//				sb.append("               <div class=\"").append(MetadataUtil.COL_MD_6).append("\">");
//				sb.append("                   <div class=\"").append(MetadataUtil.groupCSS).append("\">");
//				sb.append("                           <label class=\"").append(MetadataUtil.labelCSS).append(" ").append(MetadataUtil.COL_MD_4).append("\">");
//				sb.append("文字授权人").append(" :</label>");
//				sb.append("                           <div class=\"").append(MetadataUtil.COL_MD_8).append("\">");
//				sb.append("                           <div class=\"form-control-static\">");
//				String autherName = ca.getCopyRightMetaData().getAutherName();
//				if (StringUtils.isNotBlank(autherName)) {
//					sb.append(autherName);
//				}
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				
//				sb.append("               <div class=\"").append(MetadataUtil.COL_MD_6).append("\">");
//				sb.append("                   <div class=\"").append(MetadataUtil.groupCSS).append("\">");
//				sb.append("                           <label class=\"").append(MetadataUtil.labelCSS).append(" ").append(MetadataUtil.COL_MD_4).append("\">");
//				sb.append("音频版权归属").append(" :</label>");
//				sb.append("                           <div class=\"").append(MetadataUtil.COL_MD_8).append("\">");
//				sb.append("                           <div class=\"form-control-static\">");
//				String copyright = ca.getCopyRightMetaData().getCopyright();
//				if (StringUtils.isNotBlank(copyright)) {
//					sb.append(copyright);
//				}
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				
//				sb.append("               <div class=\"").append(MetadataUtil.COL_MD_6).append("\">");
//				sb.append("                   <div class=\"").append(MetadataUtil.groupCSS).append("\">");
//				sb.append("                           <label class=\"").append(MetadataUtil.labelCSS).append(" ").append(MetadataUtil.COL_MD_4).append("\">");
//				sb.append("音频版权生效时间").append(" :</label>");
//				sb.append("                           <div class=\"").append(MetadataUtil.COL_MD_8).append("\">");
//				sb.append("                           <div class=\"form-control-static\">");
//				String licenStartTime = ca.getCopyRightMetaData().getLicenStartTime();
//				if (StringUtils.isNotBlank(licenStartTime)) {
//					sb.append(licenStartTime);
//				}
//				sb.append("                     	  </div>");
//				sb.append("           </div>");
//				sb.append("       </div>");
//				sb.append("   </div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				
//			}
//			
//			if(ca.getCopyRightMetaData()!=null && ca.getObjectId()!=null && resType.equals("caricatureRes")){//漫画资源
//				sb.append("<div class=\"portlet\">");
//				sb = MetadataUtil.createTitle(sb, "版权元数据");
//				sb.append("   <div class=\"portlet-body\">");
//				sb.append("       <div class=\"container-fluid\">");
//				sb.append("           <div class=\"row\">");
//				sb.append("               <div class=\"").append(MetadataUtil.COL_MD_6).append("\">");
//				sb.append("                   <div class=\"").append(MetadataUtil.groupCSS).append("\">");
//				sb.append("                           <label class=\"").append(MetadataUtil.labelCSS).append(" ").append(MetadataUtil.COL_MD_4).append("\">");
//				sb.append("授权方").append(" :</label>");
//				sb.append("                           <div class=\"").append(MetadataUtil.COL_MD_8).append("\">");
//				sb.append("                           <div class=\"form-control-static\">");
//				String ownership = ca.getCopyRightMetaData().getOwnership();
//				if (StringUtils.isNotBlank(ownership)) {
//					sb.append(ownership);
//				}
//				sb.append("                     	  </div>");
//				sb.append("                     	  </div>");
//				sb.append("                     	  </div>");
//				sb.append("                     	  </div>");
//				sb.append("               <div class=\"").append(MetadataUtil.COL_MD_6).append("\">");
//				sb.append("                   <div class=\"").append(MetadataUtil.groupCSS).append("\">");
//				sb.append("                           <label class=\"").append(MetadataUtil.labelCSS).append(" ").append(MetadataUtil.COL_MD_4).append("\">");
//				sb.append("授权语种").append(" :</label>");
//				sb.append("                           <div class=\"").append(MetadataUtil.COL_MD_8).append("\">");
//				sb.append("                           <div class=\"form-control-static\">");
//				String langu ="";
//				String language = ca.getCopyRightMetaData().getLanguage();
////				if(language!=null){
////					for(String value:language){
////						langu =langu+ value+",";
////					}
////				}
//				if (StringUtils.isNotBlank(language)) {
//					sb.append(language);
//				}
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				
//				sb.append("               <div class=\"").append(MetadataUtil.COL_MD_6).append("\">");
//				sb.append("                   <div class=\"").append(MetadataUtil.groupCSS).append("\">");
//				sb.append("                           <label class=\"").append(MetadataUtil.labelCSS).append(" ").append(MetadataUtil.COL_MD_4).append("\">");
//				sb.append("授权区域").append(" :</label>");
//				sb.append("                           <div class=\"").append(MetadataUtil.COL_MD_8).append("\">");
//				sb.append("                           <div class=\"form-control-static\">");
//				String arear ="";
//				String licensArear = ca.getCopyRightMetaData().getLicensArear();
////				if(licensArear!=null){
////					for(String value:licensArear){
////						arear =arear+ value+",";
////					}
////				}
//				if (StringUtils.isNotBlank(licensArear)) {
//					sb.append(licensArear);
//				}
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				
//				sb.append("               <div class=\"").append(MetadataUtil.COL_MD_6).append("\">");
//				sb.append("                   <div class=\"").append(MetadataUtil.groupCSS).append("\">");
//				sb.append("                           <label class=\"").append(MetadataUtil.labelCSS).append(" ").append(MetadataUtil.COL_MD_4).append("\">");
//				sb.append("责任方式").append(" :</label>");
//				sb.append("                           <div class=\"").append(MetadataUtil.COL_MD_8).append("\">");
//				sb.append("                           <div class=\"form-control-static\">");
//				String liabilityMode = ca.getCopyRightMetaData().getLiabilityMode();
//				if (StringUtils.isNotBlank(liabilityMode)) {
//					sb.append(liabilityMode);
//				}
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				
//				sb.append("               <div class=\"").append(MetadataUtil.COL_MD_6).append("\">");
//				sb.append("                   <div class=\"").append(MetadataUtil.groupCSS).append("\">");
//				sb.append("                           <label class=\"").append(MetadataUtil.labelCSS).append(" ").append(MetadataUtil.COL_MD_4).append("\">");
//				sb.append("授权终止时间").append(" :</label>");
//				sb.append("                           <div class=\"").append(MetadataUtil.COL_MD_8).append("\">");
//				sb.append("                           <div class=\"form-control-static\">");
//				String authEndDate = ca.getCopyRightMetaData().getAuthEndDate();
//				if (StringUtils.isNotBlank(authEndDate)) {
//					sb.append(authEndDate);
//				}
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				
//				sb.append("               <div class=\"").append(MetadataUtil.COL_MD_6).append("\">");
//				sb.append("                   <div class=\"").append(MetadataUtil.groupCSS).append("\">");
//				sb.append("                           <label class=\"").append(MetadataUtil.labelCSS).append(" ").append(MetadataUtil.COL_MD_4).append("\">");
//				sb.append("授权起始时间").append(" :</label>");
//				sb.append("                           <div class=\"").append(MetadataUtil.COL_MD_8).append("\">");
//				sb.append("                           <div class=\"form-control-static\">");
//				String authStartDate = ca.getCopyRightMetaData().getAuthStartDate();
//				if (StringUtils.isNotBlank(authStartDate)) {
//					sb.append(authStartDate);
//				}
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				
//				sb.append("               <div class=\"").append(MetadataUtil.COL_MD_6).append("\">");
//				sb.append("                   <div class=\"").append(MetadataUtil.groupCSS).append("\">");
//				sb.append("                           <label class=\"").append(MetadataUtil.labelCSS).append(" ").append(MetadataUtil.COL_MD_4).append("\">");
//				sb.append("授权范围").append(" :</label>");
//				sb.append("                           <div class=\"").append(MetadataUtil.COL_MD_8).append("\">");
//				sb.append("                           <div class=\"form-control-static\">");
//				String range = "";
//				String licensRange = ca.getCopyRightMetaData().getLicensRange();
////				if(licensRange!=null){
////					for(String value:licensRange){
////						range =range+ value+",";
////					}
////				}
//				if (StringUtils.isNotBlank(licensRange)) {
//					sb.append(licensRange);
//				}
//				sb.append("                     	  </div>");
//				sb.append("           </div>");
//				sb.append("       </div>");
//				sb.append("   </div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				
//			}
//			
//			
//			if(ca.getCopyRightMetaData()!=null && ca.getObjectId()!=null && resType.equals("cartoonRes")){//动画资源
//				sb.append("<div class=\"portlet\">");
//				sb = MetadataUtil.createTitle(sb, "版权元数据");
//				sb.append("   <div class=\"portlet-body\">");
//				sb.append("       <div class=\"container-fluid\">");
//				sb.append("           <div class=\"row\">");
//				sb.append("               <div class=\"").append(MetadataUtil.COL_MD_6).append("\">");
//				sb.append("                   <div class=\"").append(MetadataUtil.groupCSS).append("\">");
//				sb.append("                           <label class=\"").append(MetadataUtil.labelCSS).append(" ").append(MetadataUtil.COL_MD_4).append("\">");
//				sb.append("授权方").append(" :</label>");
//				sb.append("                           <div class=\"").append(MetadataUtil.COL_MD_8).append("\">");
//				sb.append("                           <div class=\"form-control-static\">");
//				String ownership = ca.getCopyRightMetaData().getOwnership();
//				if (StringUtils.isNotBlank(ownership)) {
//					sb.append(ownership);
//				}
//				sb.append("                     	  </div>");
//				sb.append("                     	  </div>");
//				sb.append("                     	  </div>");
//				sb.append("                     	  </div>");
//				sb.append("               <div class=\"").append(MetadataUtil.COL_MD_6).append("\">");
//				sb.append("                   <div class=\"").append(MetadataUtil.groupCSS).append("\">");
//				sb.append("                           <label class=\"").append(MetadataUtil.labelCSS).append(" ").append(MetadataUtil.COL_MD_4).append("\">");
//				sb.append("授权语种").append(" :</label>");
//				sb.append("                           <div class=\"").append(MetadataUtil.COL_MD_8).append("\">");
//				sb.append("                           <div class=\"form-control-static\">");
//				String langu = "";
//				String language = ca.getCopyRightMetaData().getLanguage();
////				if(language!=null){
////					for(String value:language){
////						langu =langu+ value+",";
////					}
////				}
//				if (StringUtils.isNotBlank(language)) {
//					sb.append(language);
//				}
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				
//				sb.append("               <div class=\"").append(MetadataUtil.COL_MD_6).append("\">");
//				sb.append("                   <div class=\"").append(MetadataUtil.groupCSS).append("\">");
//				sb.append("                           <label class=\"").append(MetadataUtil.labelCSS).append(" ").append(MetadataUtil.COL_MD_4).append("\">");
//				sb.append("授权区域").append(" :</label>");
//				sb.append("                           <div class=\"").append(MetadataUtil.COL_MD_8).append("\">");
//				sb.append("                           <div class=\"form-control-static\">");
//				String arear = "";
//				String licensArear = ca.getCopyRightMetaData().getLicensArear();
////				if(licensArear!=null){
////					for(String value:licensArear){
////						arear =arear+ value+",";
////					}
////				}
//				if (StringUtils.isNotBlank(licensArear)) {
//					sb.append(licensArear);
//				}
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				
//				sb.append("               <div class=\"").append(MetadataUtil.COL_MD_6).append("\">");
//				sb.append("                   <div class=\"").append(MetadataUtil.groupCSS).append("\">");
//				sb.append("                           <label class=\"").append(MetadataUtil.labelCSS).append(" ").append(MetadataUtil.COL_MD_4).append("\">");
//				sb.append("责任方式").append(" :</label>");
//				sb.append("                           <div class=\"").append(MetadataUtil.COL_MD_8).append("\">");
//				sb.append("                           <div class=\"form-control-static\">");
//				String liabilityMode = ca.getCopyRightMetaData().getLiabilityMode();
//				if (StringUtils.isNotBlank(liabilityMode)) {
//					sb.append(liabilityMode);
//				}
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				
//				sb.append("               <div class=\"").append(MetadataUtil.COL_MD_6).append("\">");
//				sb.append("                   <div class=\"").append(MetadataUtil.groupCSS).append("\">");
//				sb.append("                           <label class=\"").append(MetadataUtil.labelCSS).append(" ").append(MetadataUtil.COL_MD_4).append("\">");
//				sb.append("授权终止时间").append(" :</label>");
//				sb.append("                           <div class=\"").append(MetadataUtil.COL_MD_8).append("\">");
//				sb.append("                           <div class=\"form-control-static\">");
//				String authEndDate = ca.getCopyRightMetaData().getAuthEndDate();
//				if (StringUtils.isNotBlank(authEndDate)) {
//					sb.append(authEndDate);
//				}
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				
//				sb.append("               <div class=\"").append(MetadataUtil.COL_MD_6).append("\">");
//				sb.append("                   <div class=\"").append(MetadataUtil.groupCSS).append("\">");
//				sb.append("                           <label class=\"").append(MetadataUtil.labelCSS).append(" ").append(MetadataUtil.COL_MD_4).append("\">");
//				sb.append("授权起始时间").append(" :</label>");
//				sb.append("                           <div class=\"").append(MetadataUtil.COL_MD_8).append("\">");
//				sb.append("                           <div class=\"form-control-static\">");
//				String authStartDate = ca.getCopyRightMetaData().getAuthStartDate();
//				if (StringUtils.isNotBlank(authStartDate)) {
//					sb.append(authStartDate);
//				}
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				
//				sb.append("               <div class=\"").append(MetadataUtil.COL_MD_6).append("\">");
//				sb.append("                   <div class=\"").append(MetadataUtil.groupCSS).append("\">");
//				sb.append("                           <label class=\"").append(MetadataUtil.labelCSS).append(" ").append(MetadataUtil.COL_MD_4).append("\">");
//				sb.append("授权范围").append(" :</label>");
//				sb.append("                           <div class=\"").append(MetadataUtil.COL_MD_8).append("\">");
//				sb.append("                           <div class=\"form-control-static\">");
//				String range = "";
//				String licensRange = ca.getCopyRightMetaData().getLicensRange();
////				if(licensRange!=null){
////					for(String value:licensRange){
////						range =range+ value+",";
////					}
////				}
//				if (StringUtils.isNotBlank(licensRange)) {
//					sb.append(licensRange);
//				}
//				sb.append("                     	  </div>");
//				sb.append("           </div>");
//				sb.append("       </div>");
//				sb.append("   </div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				sb.append("</div>");
//				
//			}
//			
			if(ca!=null && ca.getObjectId()!=null){
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
				String creator = ca.getCreator();
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
				String createTime = ca.getCreateTime();
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

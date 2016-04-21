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

public class RelationMainPageConditionTag extends RequestContextAwareTag {
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
		StringBuffer sb = new StringBuffer(); 
		HttpSession session = pageContext.getSession();
		UserInfo user = (UserInfo) session.getAttribute(LoginUserUtil.USER_SESSION_KEY);
		List<CustomMetaData>  customMetaDatas = MetadataSupport.getAllMetadateList(user,publishType);
		for(CustomMetaData customMetaData:customMetaDatas){
			List<MetadataDefinition> metadataDefinitions = customMetaData.getCustomPropertys();
			for(MetadataDefinition metadataDefinition:metadataDefinitions){
				if(metadataDefinition.getQueryModel()==2||metadataDefinition.getQueryModel()==3||metadataDefinition.getQueryModel()==4){
					int fieldType = metadataDefinition.getFieldType();
					int i=0;
					switch(fieldType){
					    case 1:
					    	sb.append("<div class=\"col-xs-6\">");
					    	sb.append("       <div class=\"by-form-row clearfix\">");
							sb.append("           <div class=\"by-form-title\"><label for=\"title\">").append(metadataDefinition.getFieldZhName()).append(":</label></div>");
							sb.append("           <div class=\"by-form-val\">");
							sb.append("               <input class=\"form-control\" id=\"").append(metadataDefinition.getFieldName()+"_metaField").append("\" name=\"").append(metadataDefinition.getFieldName()+"_metaField").append("\"/>");
							sb.append("           </div>");
							sb.append("       </div>");
							sb.append("</div>");
					    	break;
					    case 2:	
					    	sb.append("<div class=\"col-xs-6\">");
					    	sb.append("       <div class=\"by-form-row clearfix\">");
							sb.append("           <div class=\"by-form-title\"><label for=\"title\">").append(metadataDefinition.getFieldZhName()).append(":</label></div>");
							sb.append("           <div class=\"by-form-val\">");
							sb.append("                               <select name='").append(metadataDefinition.getFieldName()+"_metaField").append("' id='").append(metadataDefinition.getFieldName()+"_metaField").append("' class=\"form-control\"");
							sb.append("                               '>");
							String valueRange = metadataDefinition.getValueRange();
							String[] options = valueRange.split(",");
							sb.append("              <option value=''>").append("请选择").append("</option>");
							for(String option:options){
							    sb.append("              <option value='").append(option).append("'>").append(option).append("</option>");
							}
							sb.append("                                </select>");
							sb.append("           </div>");
							sb.append("       </div>");
							sb.append("</div>");
					    	break;
					    case 3:
					    	sb.append("<div class=\"col-xs-6\">");
					    	sb.append("       <div class=\"by-form-row clearfix\">");
							sb.append("           <div class=\"by-form-title\"><label for=\"title\">").append(metadataDefinition.getFieldZhName()).append(":</label></div>");
							sb.append("           <div style=\"width:50px;line-height:35px;float:left;\">");
							String valueRangeCheckbox = metadataDefinition.getValueRange();
							String[] optionsCheckbox = valueRangeCheckbox.split(",");
							if(optionsCheckbox!=null && optionsCheckbox.length>0){
								for(String option:optionsCheckbox){
									if(i>0 && i%8 == 0){
										sb.append("                           <br>");
									}
									sb.append("<input type='checkbox' name=\"").append(metadataDefinition.getFieldName()+"MetaField").append("\"");
									sb.append("value='").append(option).append("'/>").append(option);
									i++;
								}
							}
							//.append("\" name=\"").append(metadataDefinition.getFieldName()+"_metaField")
							sb.append("           <input type='hidden' id='").append(metadataDefinition.getFieldName()+"_metaField").append("' name='").append(metadataDefinition.getFieldName()+"_metaField").append("'/>");
							sb.append("           </div>");
							sb.append("       </div>");
							sb.append("   <script type=\"text/javascript\">");
							sb.append("         function showURL() {");
							sb.append("           var url = $(\"#").append(metadataDefinition.getFieldName()).append("\").val();");
							sb.append("           window.open(url,'window');");
							sb.append("         }");
							sb.append("    </script>");
							sb.append("</div>");
					    	break;
					    case 4:	
					    	sb.append("<div class=\"col-xs-6\">");
					    	sb.append("       <div class=\"by-form-row clearfix\">");
							sb.append("           <div class=\"by-form-title\"><label for=\"title\">").append(metadataDefinition.getFieldZhName()).append(":</label></div>");
							sb.append("           <div style=\"width:50px;line-height:35px;float:left;\">");
							String valueRangeRadio = metadataDefinition.getValueRange();
							String[] optionsRadio = valueRangeRadio.split(",");
							if(optionsRadio!=null && optionsRadio.length>0){
								for(String option:optionsRadio){
									if(i>0 && i%2 == 0){
										sb.append("                           <br>");
									}
									sb.append("<input type='radio' name='").append(metadataDefinition.getFieldName()+"_metaField").append("\" id=\"").append(metadataDefinition.getFieldName()+"_metaField").append("\"");
									sb.append("' value='").append(option).append("' />").append(option).append("&nbsp;&nbsp;");
									i++;
								}
							}
							sb.append("           </div>");
							sb.append("       </div>");
							sb.append("</div>");
					    	break;
//					    case 5:	
//					    	sb = MetadataUtil.createTextarea(sb, metadataDefinition,object,true);
//					    	break;
					    case 6:	
					    	ServletRequest request = pageContext.getRequest();
							String path = getRequestContext().getContextPath();
							String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
							sb.append("<div class=\"col-xs-6\">");
					    	sb.append("   <script type=\"text/javascript\">");
							sb.append("         function clearConditionCategory() {");
							sb.append("             $(\"#").append(metadataDefinition.getFieldName()+"Name").append("\").val('');");
							sb.append("             $(\"#").append(metadataDefinition.getFieldName()).append("\").val('');");
							sb.append("         }");
							sb.append("         function showConditionCategory() {");
							sb.append("             $.openWindow(\"").append(basePath).append("/system/dataManagement/classification/ztflSelect.jsp?isMain=1&fieldName=").append(metadataDefinition.getFieldName()).append("\",'选择分类',500,450);");
							sb.append("         }");
							sb.append("    </script>");
							sb.append("       <div class=\"by-form-row clearfix\">");
							sb.append("           <div class=\"by-form-title\"><label for=\"title\">").append(metadataDefinition.getFieldZhName()).append(":</label></div>");
							sb.append("           <div class=\"by-form-val\">");
							sb.append("                              <input type=\"text\" name=\"").append(metadataDefinition.getFieldName()+"Name").append("\" id=\"").append(metadataDefinition.getFieldName()+"Name").append("\" disabled=\"disabled\" style=\"width:45%;display: inline;\" class=\"form-control");
							sb.append("\" />");
							sb.append("                              <input type=\"hidden\" name=\"").append(metadataDefinition.getFieldName()).append("\" id=\"").append(metadataDefinition.getFieldName()).append("\"/>");
						    sb.append("<a onclick=\"showConditionCategory();\" href=\"###\" class=\"btn btn-primary\" role=\"button\" style=\"margin-left: 0.5%;width:25%;padding:7px 0.1%;\">选择</a>");
							sb.append("<a onclick=\"clearConditionCategory();\" href=\"###\" class=\"btn btn-primary\" role=\"button\" style=\"margin-left: 1%;width:25%;padding:7px 0.1%;\">清除</a>");
							sb.append("            </div>");
							sb.append("       </div>");
						//	sb = MetadataUtil.createLookup(sb, metadataDefinition,object,true,basePath);
							sb.append("</div>");
					    	break;
					    case 7:	
					    	if(metadataDefinition.getQueryModel()==4){
					    		sb.append("<div class=\"col-xs-6\">");
					    		sb.append("       <div class=\"by-form-row clearfix\">");
								sb.append("           <div class=\"by-form-title\"><label for=\"title\">").append("开始"+metadataDefinition.getFieldZhName()).append(":</label></div>");
								sb.append("           <div class=\"by-form-val\">");
								sb.append("                               <input type=\"text\" id=\"").append(metadataDefinition.getFieldName()+"_metaField_StartDate").append("\" name=\"").append(metadataDefinition.getFieldName()+"_metaField_StartDate");
								sb.append("\" class=\"").append(MetadataUtil.controlCSS).append(" Wdate");
								sb.append("\" onClick=\"WdatePicker({dateFmt:'").append("yyyy-MM-dd").append("',readOnly:true})\"/>");
								sb.append("           </div>");
								sb.append("       </div>");
								sb.append("</div>");
								//sb.append("           <input type='hidden' id='").append(metadataDefinition.getFieldName()+"_metaField_StartDate").append("' name='").append(metadataDefinition.getFieldName()+"_metaField_StartDate").append("'/>");
								sb.append("<div class=\"col-xs-6\">");
								sb.append("       <div class=\"by-form-row clearfix\">");
								sb.append("           <div class=\"by-form-title\"><label for=\"title\">").append("结束"+metadataDefinition.getFieldZhName()).append(":</label></div>");
								sb.append("           <div class=\"by-form-val\">");
								sb.append("                               <input type=\"text\"  id=\"").append(metadataDefinition.getFieldName()+"_metaField_EndDate").append("\" name=\"").append(metadataDefinition.getFieldName()+"_metaField_EndDate");
								sb.append("\" class=\"").append(MetadataUtil.controlCSS).append(" Wdate");
								sb.append("\" onClick=\"WdatePicker({dateFmt:'").append("yyyy-MM-dd").append("',readOnly:true})\"/>");
								sb.append("           </div>");
								sb.append("       </div>");
								sb.append("</div>");
					    	}else{
					    		sb.append("<div class=\"col-xs-6\">");
					    		sb.append("       <div class=\"by-form-row clearfix\">");
								sb.append("           <div class=\"by-form-title\"><label for=\"title\">").append(metadataDefinition.getFieldZhName()).append(":</label></div>");
								sb.append("           <div class=\"by-form-val\">");
								sb.append("                               <input type=\"text\" id=\"").append(metadataDefinition.getFieldName()+"_metaField");
								sb.append("\" class=\"").append(MetadataUtil.controlCSS).append(" Wdate");
								sb.append("\" onClick=\"WdatePicker({dateFmt:'").append("yyyy-MM-dd").append("',readOnly:true})\"/>");
								sb.append("           </div>");
								sb.append("       </div>");
								sb.append("</div>");
					    	}
					    	break;
					}
				}
			}
		}
		writer.append(sb.toString());
		writer.flush();
		return 0;
	}
}

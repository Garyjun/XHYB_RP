package com.brainsoon.system.service.impl;

import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspWriter;

import org.springframework.stereotype.Service;

import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.semantic.schema.ontology.CustomMetaData;
import com.brainsoon.semantic.schema.ontology.MetadataDefinition;
import com.brainsoon.system.service.ITagUtilService;
import com.brainsoon.system.util.MetadataSupport;
import com.brainsoon.system.util.MetadataUtil;
@Service
public class TagUtilService extends BaseService implements ITagUtilService{

	public String getMainPageConditionTag(HttpSession session,String publishType,String basePath) throws Exception {
		StringBuffer sb = new StringBuffer(); 
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
					    	sb.append("       <div class=\"by-form-row clearfix\">");
							sb.append("           <div class=\"by-form-title\"><label for=\"title\">").append(metadataDefinition.getFieldZhName()).append(":</label></div>");
							sb.append("           <div class=\"by-form-val\">");
							sb.append("               <input class=\"form-control\" id=\"").append(metadataDefinition.getFieldName()).append("\" name=\"").append(metadataDefinition.getFieldName()).append("\"/>");
							sb.append("           </div>");
							sb.append("       </div>");
					    	break;
					    case 2:	
					    	sb.append("       <div class=\"by-form-row clearfix\">");
							sb.append("           <div class=\"by-form-title\"><label for=\"title\">").append(metadataDefinition.getFieldZhName()).append(":</label></div>");
							sb.append("           <div class=\"by-form-val\">");
							sb.append("                               <select name='").append(metadataDefinition.getFieldName()).append("' id='").append(metadataDefinition.getFieldName()).append("' class=\"form-control\"");
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
					    	break;
					    case 3:
					    	sb.append("       <div class=\"by-form-row clearfix\">");
							sb.append("           <div class=\"by-form-title\"><label for=\"title\">").append(metadataDefinition.getFieldZhName()).append(":</label></div>");
							sb.append("           <div style=\"width:120px;line-height:35px;float:left;\">");
							String valueRangeCheckbox = metadataDefinition.getValueRange();
							String[] optionsCheckbox = valueRangeCheckbox.split(",");
							if(optionsCheckbox!=null && optionsCheckbox.length>0){
								for(String option:optionsCheckbox){
									if(i>0 && i%2 == 0){
										sb.append("                           <br>");
									}
									sb.append("                               <input type='checkbox' name=\"").append(metadataDefinition.getFieldName()+"_metaField").append("\"");
									sb.append("                               ' value='").append(option).append("' />").append(option);
									i++;
								}
							}
							sb.append("           <input type='hidden' id='").append(metadataDefinition.getFieldName()).append("'/>");
							sb.append("           </div>");
							sb.append("       </div>");
							sb.append("   <script type=\"text/javascript\">");
							sb.append("         function showURL() {");
							sb.append("           var url = $(\"#").append(metadataDefinition.getFieldName()).append("\").val();");
							sb.append("           window.open(url,'window');");
							sb.append("         }");
							sb.append("    </script>");
					    	break;
					    case 4:	
					    	sb.append("       <div class=\"by-form-row clearfix\">");
							sb.append("           <div class=\"by-form-title\"><label for=\"title\">").append(metadataDefinition.getFieldZhName()).append(":</label></div>");
							sb.append("           <div style=\"width:120px;line-height:35px;float:left;\">");
							String valueRangeRadio = metadataDefinition.getValueRange();
							String[] optionsRadio = valueRangeRadio.split(",");
							if(optionsRadio!=null && optionsRadio.length>0){
								for(String option:optionsRadio){
									if(i>0 && i%2 == 0){
										sb.append("                           <br>");
									}
									sb.append("                               <input type='radio' name='").append(metadataDefinition.getFieldName()).append("' id='").append(metadataDefinition.getFieldName()).append("' ");
									sb.append("                               ' value='").append(option).append("' />").append(option).append("&nbsp;&nbsp;");
									i++;
								}
							}
							sb.append("           </div>");
							sb.append("       </div>");
					    	break;
//					    case 5:	
//					    	sb = MetadataUtil.createTextarea(sb, metadataDefinition,object,true);
//					    	break;
					    case 6:	
//					    	ServletRequest request = pageContext.getRequest();
//							String path = getRequestContext().getContextPath();
							//String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
							sb.append("   <script type=\"text/javascript\">");
							sb.append("         function clear").append(metadataDefinition.getFieldName()).append("Category() {");
							sb.append("             $(\"#").append(metadataDefinition.getFieldName()+"Name").append("\").val('');");
							sb.append("             $(\"#").append(metadataDefinition.getFieldName()).append("\").val('');");
							sb.append("         }");
							sb.append("         function show").append(metadataDefinition.getFieldName()).append("Category() {");
							sb.append("             $.openWindow(\"").append(basePath).append("/system/dataManagement/classification/ztflSelect.jsp?isMain=1&fieldName=").append(metadataDefinition.getFieldName()).append("&typeId=").append(metadataDefinition.getValueRange()).append("\",'选择分类',500,450);");
							sb.append("         }");
							sb.append("    </script>");
							sb.append("       <div class=\"by-form-row clearfix\">");
							sb.append("           <div class=\"by-form-title\"><label for=\"title\">").append(metadataDefinition.getFieldZhName()).append(":</label></div>");
							sb.append("           <div class=\"by-form-val\">");
							sb.append("                              <input type=\"text\" name=\"").append(metadataDefinition.getFieldName()+"Name").append("\" id=\"").append(metadataDefinition.getFieldName()+"Name").append("\" disabled=\"disabled\" style=\"width:45%;display: inline;\" class=\"form-control");
							sb.append("\" />");
							sb.append("                              <input type=\"hidden\" name=\"").append(metadataDefinition.getFieldName()).append("\" id=\"").append(metadataDefinition.getFieldName()).append("\"/>");
						    sb.append("<a onclick=\"show").append(metadataDefinition.getFieldName()).append("Category();\" href=\"###\" class=\"btn btn-primary\" role=\"button\" style=\"margin-left: 0.5%;width:25%;padding:7px 0.1%;\">选择</a>");
							sb.append("<a onclick=\"clear").append(metadataDefinition.getFieldName()).append("Category();\" href=\"###\" class=\"btn btn-primary\" role=\"button\" style=\"margin-left: 1%;width:25%;padding:7px 0.1%;\">清除</a>");
							sb.append("            </div>");
							sb.append("       </div>");
						//	sb = MetadataUtil.createLookup(sb, metadataDefinition,object,true,basePath);
					    	break;
					    case 7:	
					    	if(metadataDefinition.getQueryModel()==4){
					    		sb.append("       <div class=\"by-form-row clearfix\">");
					    		String fieldZhName = metadataDefinition.getFieldZhName();
					    		if(fieldZhName.length()>2){
					    			String newName = "开始"+fieldZhName;
					    			String subName = newName.substring(0,4)+"...";
					    			sb.append("           <div class=\"by-form-title\"><label for=\"title\"><span data-toggle=\"tooltip\" data-placement=\"top\" title=\"").append(newName).append("\"> ").append(subName).append(":</span></label></div>");
					    		}else{
					    			sb.append("           <div class=\"by-form-title\"><label for=\"title\">").append(metadataDefinition.getFieldZhName()).append(":</label></div>");
					    		}
								sb.append("           <div class=\"by-form-val\">");
								sb.append("                               <input type=\"text\" id=\"").append(metadataDefinition.getFieldName()+"_metaField_StartDate");
								sb.append("\" class=\"").append(MetadataUtil.controlCSS).append(" Wdate");
								if(metadataDefinition.getValueRange().equals("yyyy-MM-dd HH:mm:ss")){
									sb.append("\" onClick=\"WdatePicker({dateFmt:'").append("yyyy-MM-dd HH:mm:ss").append("',readOnly:true})\"/>");
								}else if(metadataDefinition.getValueRange().equals("yyyy-MM-dd HH:mm")){
									sb.append("\" onClick=\"WdatePicker({dateFmt:'").append("yyyy-MM-dd HH:mm").append("',readOnly:true})\"/>");
								}else if(metadataDefinition.getValueRange().equals("yyyy-MM-dd HH")){
									sb.append("\" onClick=\"WdatePicker({dateFmt:'").append("yyyy-MM-dd HH").append("',readOnly:true})\"/>");
								}else if(metadataDefinition.getValueRange().equals("yyyy-MM-dd")){
									sb.append("\" onClick=\"WdatePicker({dateFmt:'").append("yyyy-MM-dd").append("',readOnly:true})\"/>");
								}else if(metadataDefinition.getValueRange().equals("yyyy-MM")){
									sb.append("\" onClick=\"WdatePicker({dateFmt:'").append("yyyy-MM").append("',readOnly:true})\"/>");
								}else if(metadataDefinition.getValueRange().equals("hh:mm:ss")){
									sb.append("\" onClick=\"WdatePicker({dateFmt:'").append("hh:mm:ss").append("',readOnly:true})\"/>");
								}
								sb.append("           </div>");
								sb.append("       </div>");
								sb.append("       <div class=\"by-form-row clearfix\">");
								if(fieldZhName.length()>2){
									String endName = "结束"+fieldZhName;
					    			String endSubName = endName.substring(0,4)+"...";
									sb.append("           <div class=\"by-form-title\"><label for=\"title\"><span data-toggle=\"tooltip\" data-placement=\"top\" title=\"").append(endName).append("\"> ").append(endSubName).append(":</label></div>");
								}else{
									sb.append("           <div class=\"by-form-title\"><label for=\"title\">").append("结束"+metadataDefinition.getFieldZhName()).append(":</label></div>");
								}
								sb.append("           <div class=\"by-form-val\">");
								sb.append("                               <input type=\"text\"  id=\"").append(metadataDefinition.getFieldName()+"_metaField_EndDate");
								sb.append("\" class=\"").append(MetadataUtil.controlCSS).append(" Wdate");
								if(metadataDefinition.getValueRange().equals("yyyy-MM-dd HH:mm:ss")){
									sb.append("\" onClick=\"WdatePicker({dateFmt:'").append("yyyy-MM-dd HH:mm:ss").append("',readOnly:true})\"/>");
								}else if(metadataDefinition.getValueRange().equals("yyyy-MM-dd HH:mm")){
									sb.append("\" onClick=\"WdatePicker({dateFmt:'").append("yyyy-MM-dd HH:mm").append("',readOnly:true})\"/>");
								}else if(metadataDefinition.getValueRange().equals("yyyy-MM-dd HH")){
									sb.append("\" onClick=\"WdatePicker({dateFmt:'").append("yyyy-MM-dd HH").append("',readOnly:true})\"/>");
								}else if(metadataDefinition.getValueRange().equals("yyyy-MM-dd")){
									sb.append("\" onClick=\"WdatePicker({dateFmt:'").append("yyyy-MM-dd").append("',readOnly:true})\"/>");
								}else if(metadataDefinition.getValueRange().equals("yyyy-MM")){
									sb.append("\" onClick=\"WdatePicker({dateFmt:'").append("yyyy-MM").append("',readOnly:true})\"/>");
								}else if(metadataDefinition.getValueRange().equals("hh:mm:ss")){
									sb.append("\" onClick=\"WdatePicker({dateFmt:'").append("hh:mm:ss").append("',readOnly:true})\"/>");
								}
								
								sb.append("           </div>");
								sb.append("       </div>");
					    	}else{
					    		sb.append("       <div class=\"by-form-row clearfix\">");
								sb.append("           <div class=\"by-form-title\"><label for=\"title\">").append(metadataDefinition.getFieldZhName()).append(":</label></div>");
								sb.append("           <div class=\"by-form-val\">");
								sb.append("                               <input type=\"text\" id=\"").append(metadataDefinition.getFieldName());
								sb.append("\" class=\"").append(MetadataUtil.controlCSS).append(" Wdate");
								sb.append("\" onClick=\"WdatePicker({dateFmt:'").append("yyyy-MM-dd").append("',readOnly:true})\"/>");
								sb.append("           </div>");
								sb.append("       </div>");
					    	}
					    	break;
					    case 9:	
					    	sb.append("       <div class=\"by-form-row clearfix\">");
							sb.append("           <div class=\"by-form-title\"><label for=\"title\">").append(metadataDefinition.getFieldZhName()).append(":</label></div>");
							sb.append("           <div class=\"by-form-val\">");
							sb.append("                               <select type=\"multiSelEle\" multiple=\"multiple\" name=\"").append(metadataDefinition.getFieldName()).append("\" id='").append(metadataDefinition.getFieldName()).append("'");
							sb.append("                               >");
							sb.append("</select>");
							sb.append("           </div>");
							sb.append("       </div>");
							sb.append("   <script type=\"text/javascript\">");
							sb.append("   dyAddOpts(\""+metadataDefinition.getValueRange()+"\",\"edit\",\"\", \"" +  metadataDefinition.getFieldName() + "\""
									+ ");");
							sb.append("   </script>");
					    	break;
					}
				}
			}
		}
		return sb.toString();
	}
	
	
	public String getMainPageTargetTag(HttpSession session,String publishType) throws Exception {
		StringBuffer sb = new StringBuffer(); 
		UserInfo user = (UserInfo) session.getAttribute(LoginUserUtil.USER_SESSION_KEY);
		List<CustomMetaData>  customMetaDatas = MetadataSupport.getAllMetadateList(user,publishType);
		for(CustomMetaData customMetaData:customMetaDatas){
			List<MetadataDefinition> metadataDefinitions = customMetaData.getCustomPropertys();
			for(MetadataDefinition metadataDefinition:metadataDefinitions){
				int targetType = metadataDefinition.getIdentifier();
				if(targetType == 10){
					sb.append(metadataDefinition.getFieldName()+"'");
				}
			}
		}
		if(sb.length()>0){
			sb.deleteCharAt(sb.length()-1);
		}
		return sb.toString();
	}
	
	
	
}

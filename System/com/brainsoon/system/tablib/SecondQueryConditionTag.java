package com.brainsoon.system.tablib;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.tags.RequestContextAwareTag;

import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.common.support.OperDbUtils;
import com.brainsoon.semantic.schema.ontology.CustomMetaData;
import com.brainsoon.semantic.schema.ontology.MetadataDefinition;
import com.brainsoon.system.util.MetadataSupport;
import com.brainsoon.system.util.MetadataUtil;

public class SecondQueryConditionTag extends RequestContextAwareTag{

		private static final long serialVersionUID = 1L;
	    private String publishType;
	    
	   	public String getPublishType() {
	   		return publishType;
	   	}
	   	public void setPublishType(String publishType) {
	   		this.publishType = publishType;
	   	}
	   	
	   	public HttpServletRequest getRequest(){
			HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
			return request;
		}
	@Override
	protected int doStartTagInternal() throws Exception {
		JspWriter writer = pageContext.getOut();
		String basePath = "http://"  + getRequest().getServerName() + ":" + getRequest().getServerPort()+ getRequest().getContextPath();
		StringBuffer sb = new StringBuffer(); 
		UserInfo user = LoginUserUtil.getLoginUser();
		List<CustomMetaData>  customMetaDatas = MetadataSupport.getAllMetadateList(user,publishType);
		for(CustomMetaData customMetaData:customMetaDatas){
			List<MetadataDefinition> metadataDefinitions = customMetaData.getCustomPropertys();
			for(MetadataDefinition metadataDefinition:metadataDefinitions){
				if(StringUtils.isNotBlank(metadataDefinition.getSecondSearch()) && metadataDefinition.getSecondSearch().equals("0")){
						int fieldType = metadataDefinition.getFieldType();
						int i=0;
						switch(fieldType){
						    case 1:
						    	sb.append("       <div class=\"by-form-row clearfix\" style=\"margin-top: 10px;margin-left:50px;\">");
								sb.append("				<div class=\"form-group\">");
						    	//sb.append("           <div class=\"by-form-title\" style=\" margin-left:115px; margin-top:5px;\"><label for=\"title\">").append(metadataDefinition.getFieldZhName()).append(":</label></div>");
								sb.append("					<label class=\"control-label col-md-3\">").append(metadataDefinition.getFieldZhName()).append(":</label>");
								sb.append("           		<div class=\"by-form-val\">");
								sb.append("               		<input class=\"form-control\" style=\"width:170px;\" id=\"").append(metadataDefinition.getFieldName()).append("\" name=\"").append(metadataDefinition.getFieldName()).append("\"/>");
								sb.append("          		</div>");
								sb.append("       		</div>");
								sb.append("       </div>");
								//queryContion +=metadataDefinition.getFieldName()+":"+fieldType+",";
						    	break;
						    case 2:	
						    	/*sb.append("       <div class=\"by-form-row clearfix\">");
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
								queryContion +=metadataDefinition.getFieldName()+":"+fieldType+",";
						    	break;*/
						    case 3:
						    	
						    	
						    	/*sb.append("       <div class=\"by-form-row clearfix\">");
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
								sb.append("    </script>");*/
								/*queryContion +=metadataDefinition.getFieldName()+":"+fieldType+",";
						    	break;*/
						    case 4:	
						    	boolean flag = false;
								LinkedHashMap<String, String> childMap = OperDbUtils.queryValueIdByKey(metadataDefinition.getValueRange()); 
								String nowField = metadataDefinition.getFieldName();
								if(nowField.contains("metadataMap")){
									nowField = nowField.replace("metadataMap['", "");
									nowField = nowField.replace("']", "");
									flag = true;
								}
								sb.append("       <div class=\"by-form-row clearfix\" style=\"margin-top: 10px;margin-left:50px;\">");
								sb.append("			<div class=\"form-group\" style=\"\">");
								sb.append("				<label  class=\"control-label col-md-3\">").append(metadataDefinition.getFieldZhName()).append(":</label>");
								//sb.append("  			<div id=\"lead\" class=\"card\">	  ");
								sb.append(" 				 <div class=\"topnav\" style=\"width:220px; margin-left:190px\"> ");
								sb.append("  						<a id=\""+metadataDefinition.getFieldName()+"_Div\" href=\"javascript:void(0);\" class=\"as\"> ");
								sb.append(" 							<span id=\"spanValue\" title=\"\">请选择</span>  ");
								sb.append("  						</a> ");
								sb.append("  				  </div> ");
								//给隐藏域赋值
								if(flag){
									sb.append(" <input type=\"hidden\" value=\"\" id=\""+metadataDefinition.getFieldName()+"\" name=\""+nowField+"\"/>  ");
								}else{
									sb.append(" <input type=\"hidden\" value=\"\" id=\""+metadataDefinition.getFieldName()+"\" name=\""+metadataDefinition.getFieldZhName()+"\" />  ");
								}
								//单选
								if(StringUtils.isNotBlank(metadataDefinition.getValueLength())){
									sb.append(" <input type=\"hidden\" value=\"1\" id=\""+metadataDefinition.getFieldName()+"MaxNumber\"/>  ");
								//多选
								}else{
									sb.append(" <input type=\"hidden\" value=\"" + metadataDefinition.getValueLength() + "\" id=\""+metadataDefinition.getFieldName()+"MaxNumber\"/>  ");
								}
								sb.append(" 		</div> ");
								sb.append(" 	</div> ");
								sb.append(" <div id=\""+metadataDefinition.getFieldName()+"m2\" class=\"xmenu\" style=\"display: none;\"> ");
								sb.append(" <div class=\"select-info\"> ");
								sb.append(" <label class=\"top-label\">已选"+metadataDefinition.getFieldZhName()+"：</label> ");
								sb.append(" <ul> ");
								sb.append(" </ul> ");
								sb.append(" <a  name=\"menu-confirm\" href=\"javascript:void(0);\" class=\"a-btn\"> ");
								sb.append(" <span class=\"a-btn-text\">确定</span>");
								sb.append(" </a> ");
								sb.append("  </div> ");
								sb.append(" <dl>");
								sb.append(" <dt class=\"open\">选择"+metadataDefinition.getFieldZhName()+"</dt>");
								sb.append(" <dd><ul id=\"selectValue\">");
						        if(childMap != null){
						        	for(Iterator it = childMap.entrySet().iterator();it.hasNext();){  
							            Entry<Object, String> entry = (Entry<Object, String>)it.next();
							            //判断是否选中
							            sb.append("<li rel=\"").append(entry.getKey()).append("\"");
							            sb.append(">").append(entry.getValue()).append("</li>");
							        }
						        }
						        sb.append(" </ul></dd>");
								sb.append(" </dl>");
								sb.append(" </div>");
								sb.append("	 <script type=\"text/javascript\">");
								sb.append("	 $(document).ready(function(){");
								sb.append("	 $(\"#"+metadataDefinition.getFieldName()+"_Div\").xMenu({");
								sb.append("	 width :320, ");
								sb.append("	 eventType: \"click\",");
								sb.append("	 dropmenu:\"#"+metadataDefinition.getFieldName()+"m2\",");
								sb.append("	 hiddenID : \""+metadataDefinition.getFieldName()+"\"");
								sb.append("	  });");
								sb.append("	  });<\\/script>");
						    	
						    	
						    	
						    	/*sb.append("       <div class=\"by-form-row clearfix\">");
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
								queryContion +=metadataDefinition.getFieldName()+":"+fieldType+",";*/
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
								sb.append("             $(\"#").append(metadataDefinition.getFieldName()+"Name").append("\").val(\"\");");
								sb.append("             $(\"#").append(metadataDefinition.getFieldName()).append("\").val(\"\");");
								sb.append("         }");
								sb.append("         function show").append(metadataDefinition.getFieldName()).append("Category() {");
								sb.append("             $.openWindow(\"").append(basePath).append("/system/dataManagement/classification/ztflSelect.jsp?fieldName=").append(metadataDefinition.getFieldName()).append("&typeId=").append(metadataDefinition.getValueRange()).append("\",\"选择分类\",500,450);");
								sb.append("         }");
								sb.append("    <\\/script>");
								sb.append("       <div class=\"by-form-row clearfix\" style=\"margin-top: 10px;margin-left:50px;\">");
								sb.append("           <div class=\"form-group\">");
								sb.append("					<label class=\"control-label col-md-3\">").append(metadataDefinition.getFieldZhName()).append(":</label>");
								sb.append("           		<div class=\"col-md-9\" style=\"padding-left:0px;\">");
								sb.append("                 	 <input class=\"form-control\" readonly=\"readonly\" style=\"width:120px;display: inline;\" id=\"").append(metadataDefinition.getFieldName()+"Name").append("\" name=\"").append(metadataDefinition.getFieldName()+"Name").append("\"/>");
								sb.append("                		 <input type=\"hidden\" name=\"").append(metadataDefinition.getFieldName()).append("\" id=\"").append(metadataDefinition.getFieldName()).append("\"/>");
							    sb.append("				   		 <a onclick=\"show").append(metadataDefinition.getFieldName()).append("Category();\" href=\"###\" class=\"btn-primary\" role=\"button\" style=\"display: inline;\"><img src=\""+basePath+"/appframe/main/images/select.png\" alt=\"选择\"></a>");
								sb.append("				   		 <a onclick=\"clear").append(metadataDefinition.getFieldName()).append("Category();\" href=\"###\" class=\"btn-primary\" role=\"button\" style=\"display: inline;\"><img src=\""+basePath+"/appframe/main/images/clean.png\" alt=\"清除\"></a>");
								sb.append("           		</div>");
								sb.append("          </div>");
								sb.append("      </div>");
								
							//	sb = MetadataUtil.createLookup(sb, metadataDefinition,object,true,basePath);
						    	break;
						    case 7:	
						    	/*if(metadataDefinition.getQueryModel()==4){*/
						    		sb.append("       <div class=\"by-form-row clearfix\" style=\"margin-top: 10px;margin-left:50px;\">");
						    		String fieldZhName = metadataDefinition.getFieldZhName();
						    		if(fieldZhName.length()>2){
						    			String newName = "开始"+fieldZhName;
						    			String subName = newName.substring(0,4)+".";
						    			sb.append("           <div class=\"form-group\">");
						    			sb.append("					<label class=\"control-label col-md-3\"><span data-toggle=\"tooltip\" data-placement=\"top\" title=\"").append(newName).append("\"> ").append(subName).append(":</span></label>");
						    		}else{
						    			sb.append("           <div class=\"form-group\">");
						    			sb.append("					<label class=\"control-label col-md-3\">").append(metadataDefinition.getFieldZhName()).append(":</label>");
						    		}
									sb.append("           <div class=\"by-form-val\" style=\"margin-top: 10px;\">");
									sb.append("                      <input type=\"text\" style=\"width:170px;\" id=\"").append(metadataDefinition.getFieldName()+"_metaField_StartDate");
									
									sb.append("\" class=\"").append(MetadataUtil.controlCSS).append(" Wdate");
									if(metadataDefinition.getValueRange().equals("yyyy-MM-dd HH:mm:ss")){
										sb.append("\" onClick=\"WdatePicker({dateFmt:/").append("yyyy-MM-dd HH:mm:ss").append("/,readOnly:true})\"/>");
									}else if(metadataDefinition.getValueRange().equals("yyyy-MM-dd HH:mm")){
										sb.append("\" onClick=\"WdatePicker({dateFmt:/").append("yyyy-MM-dd HH:mm").append("/,readOnly:true})\"/>");
									}else if(metadataDefinition.getValueRange().equals("yyyy-MM-dd HH")){
										sb.append("\" onClick=\"WdatePicker({dateFmt:/").append("yyyy-MM-dd HH").append("/,readOnly:true})\"/>");
									}else if(metadataDefinition.getValueRange().equals("yyyy-MM-dd")){
										sb.append("\" onClick=\"WdatePicker({dateFmt:/").append("yyyy-MM-dd").append("/,readOnly:true})\"/>");
									}else if(metadataDefinition.getValueRange().equals("yyyy-MM")){
										sb.append("\" onClick=\"WdatePicker({dateFmt:/").append("yyyy-MM").append("/,readOnly:true})\"/>");
									}else if(metadataDefinition.getValueRange().equals("hh:mm:ss")){
										sb.append("\" onClick=\"WdatePicker({dateFmt:/").append("hh:mm:ss").append("/,readOnly:true})\"/>");
									}
									sb.append("			  </div>");
									sb.append("     </div>");
									sb.append("  </div>");
									sb.append(" <div class=\"by-form-row clearfix\" style=\"margin-top: 10px;margin-left:50px;\">");
									if(fieldZhName.length()>2){
										String endName = "结束"+fieldZhName;
						    			String endSubName = endName.substring(0,4)+".";
										sb.append("   <div class=\"form-group\">");
										sb.append("			<label class=\"control-label col-md-3\"><span data-toggle=\"tooltip\" data-placement=\"top\" title=\"").append(endName).append("\"> ").append(endSubName).append(":</label>");
									}else{
										sb.append("    <div class=\"form-group\">");
										sb.append("			<label class=\"control-label col-md-3\">").append("结束"+metadataDefinition.getFieldZhName()).append(":</label>");
									}
									sb.append("          		 <div class=\"by-form-val\" style=\"margin-top: 10px;\">");
									sb.append("                 	 <input type=\"text\" style=\"width:170px;\" id=\"").append(metadataDefinition.getFieldName()+"_metaField_EndDate");
									sb.append("\" class=\"").append(MetadataUtil.controlCSS).append(" Wdate");
									if(metadataDefinition.getValueRange().equals("yyyy-MM-dd HH:mm:ss")){
										sb.append("\" onClick=\"WdatePicker({dateFmt:/").append("yyyy-MM-dd HH:mm:ss").append("/,readOnly:true})\"/>");
									}else if(metadataDefinition.getValueRange().equals("yyyy-MM-dd HH:mm")){
										sb.append("\" onClick=\"WdatePicker({dateFmt:/").append("yyyy-MM-dd HH:mm").append("/,readOnly:true})\"/>");
									}else if(metadataDefinition.getValueRange().equals("yyyy-MM-dd HH")){
										sb.append("\" onClick=\"WdatePicker({dateFmt:/").append("yyyy-MM-dd HH").append("/,readOnly:true})\"/>");
									}else if(metadataDefinition.getValueRange().equals("yyyy-MM-dd")){
										sb.append("\" onClick=\"WdatePicker({dateFmt:/").append("yyyy-MM-dd").append("/,readOnly:true})\"/>");
									}else if(metadataDefinition.getValueRange().equals("yyyy-MM")){
										sb.append("\" onClick=\"WdatePicker({dateFmt:/").append("yyyy-MM").append("/,readOnly:true})\"/>");
									}else if(metadataDefinition.getValueRange().equals("hh:mm:ss")){
										sb.append("\" onClick=\"WdatePicker({dateFmt:/").append("hh:mm:ss").append("/,readOnly:true})\"/>");
									}
									sb.append("           </div>");
									sb.append("   </div>");
									sb.append("</div>");
						    	/*}else{
						    		sb.append("       <div class=\"by-form-row clearfix\">");
									sb.append("           <div class=\"by-form-title\"><label for=\"title\">").append(metadataDefinition.getFieldZhName()).append(":</label></div>");
									sb.append("           <div class=\"by-form-val\">");
									sb.append("                               <input type=\"text\" id=\"").append(metadataDefinition.getFieldName());
									sb.append("\" class=\"").append(MetadataUtil.controlCSS).append(" Wdate");
									sb.append("\" onClick=\"WdatePicker({dateFmt:'").append("yyyy-MM-dd").append("',readOnly:true})\"/>");
									sb.append("           </div>");
									sb.append("       </div>");
						    	}*/
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
						    case 10:
						    	sb.append("   <script type=\"text/javascript\">");
								sb.append("         function clear").append(metadataDefinition.getFieldName()).append("Category() {");
								sb.append("             $(\"#").append(metadataDefinition.getFieldName()+"Name").append("\").val(\"\");");
								sb.append("             $(\"#").append(metadataDefinition.getFieldName()).append("\").val(\"\");");
								sb.append("         }");
								sb.append("         function show").append(metadataDefinition.getFieldName()).append("Category() {");
								sb.append("          $.openWindow(\"").append(basePath).append("/system/peopleUnit/peopleUnit.jsp?divWidth=1150px&fieldName="+metadataDefinition.getFieldName()+"&valueLength="+metadataDefinition.getValueLength()+"\",\""+metadataDefinition.getFieldZhName()+"\",1200,550)");
								sb.append("         }");
								sb.append("   <\\/script>");
								sb.append("    <div class=\"by-form-row clearfix\" style=\"margin-top: 10px;margin-left:50px;\">");
								sb.append("            <div class=\"form-group\">");
								sb.append("					<label class=\"control-label col-md-3\">").append(metadataDefinition.getFieldZhName()).append(":</label>");
								sb.append("           		<div class=\"col-md-9\" style=\"padding-left:0px;\">");
								sb.append("           			<input class=\"form-control\" readonly=\"readonly\" style=\"width:120px;display: inline;\" id=\"").append(metadataDefinition.getFieldName()+"Name").append("\" name=\"").append(metadataDefinition.getFieldName()+"Name").append("\"/>");
								sb.append("           			<input class=\"form-control\" type=\"hidden\" id=\"").append(metadataDefinition.getFieldName()).append("\" name=\"").append(metadataDefinition.getFieldName()).append("\"/>");
								sb.append("						<a onclick=\"show").append(metadataDefinition.getFieldName()).append("Category();\" href=\"###\" class=\"btn-primary\" role=\"button\" style=\"display: inline;\"><img src=\""+basePath+"/appframe/main/images/select.png\" alt=\"选择\"></a>");
								sb.append("						<a onclick=\"clear").append(metadataDefinition.getFieldName()).append("Category();\" href=\"###\" class=\"btn-primary\" role=\"button\" style=\"display: inline;\"><img src=\""+basePath+"/appframe/main/images/clean.png\" alt=\"清除\"></a>");
								sb.append("           		</div>");
								sb.append("            </div>");
								sb.append("   </div>");
								
								
								
								
								break;
						    case 11:
						    	sb.append("   <script type=\"text/javascript\">");
								sb.append("         function clear").append(metadataDefinition.getFieldName()).append("Category() {");
								sb.append("             $(\"#").append(metadataDefinition.getFieldName()+"Name").append("\").val(\"\");");
								sb.append("             $(\"#").append(metadataDefinition.getFieldName()).append("\").val(\"\");");
								sb.append("         }");
								sb.append("         function show").append(metadataDefinition.getFieldName()).append("Category() {");
								sb.append("          $.openWindow(\"").append(basePath).append("/system/peopleUnit/companyUnit.jsp?divWidth=1150px&fieldName="+metadataDefinition.getFieldName()+"&valueLength="+metadataDefinition.getValueLength()+"\",\""+metadataDefinition.getFieldZhName()+"\",1200,550)");
								sb.append("         }");
								sb.append("   <\\/script>");
								sb.append("    <div class=\"by-form-row clearfix\" style=\"margin-top: 10px;margin-left:50px;\">");
								sb.append("            <div class=\"form-group\">");
								sb.append("					<label class=\"control-label col-md-3\">").append(metadataDefinition.getFieldZhName()).append(":</label>");
								sb.append("           		<div class=\"col-md-9\" style=\"padding-left:0px;\">");
								sb.append("           			<input class=\"form-control\" readonly=\"readonly\" style=\"width:120px;display: inline;\" id=\"").append(metadataDefinition.getFieldName()+"Name").append("\" name=\"").append(metadataDefinition.getFieldName()+"Name").append("\"/>");
								sb.append("           			<input class=\"form-control\" type=\"hidden\" id=\"").append(metadataDefinition.getFieldName()).append("\" name=\"").append(metadataDefinition.getFieldName()).append("\"/>");
								sb.append("						<a onclick=\"show").append(metadataDefinition.getFieldName()).append("Category();\" href=\"###\" class=\"btn-primary\" role=\"button\" style=\"display: inline;\"><img src=\""+basePath+"/appframe/main/images/select.png\" alt=\"选择\"></a>");
								sb.append("						<a onclick=\"clear").append(metadataDefinition.getFieldName()).append("Category();\" href=\"###\" class=\"btn-primary\" role=\"button\" style=\"display: inline;\"><img src=\""+basePath+"/appframe/main/images/clean.png\" alt=\"清除\"></a>");
								sb.append("           		</div>");
								sb.append("            </div>");
								sb.append("   </div>");
						    	break;
						}
					//}
				}
			}
		}
		
		
		sb.append("<div style=\"margin-button: 50px; margin-left:170px;\"><input type=\"button\" onclick=\"checkSecond();\" value=\"查询\" class=\"btn btn-primary\">   <input type=\"button\" onclick=\"closelayer();\" value=\"关闭\" class=\"btn btn-primary\"></div>");
		writer.append(sb.toString());
		System.out.println(sb.toString());
		writer.flush();
		return 0;
	}

}

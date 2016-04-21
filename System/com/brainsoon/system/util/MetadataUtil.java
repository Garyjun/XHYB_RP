package com.brainsoon.system.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.brainsoon.common.support.OperDbUtils;
import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.resource.util.ResUtils;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.semantic.schema.ontology.MetadataDefinition;
import com.brainsoon.system.service.ICompanyService;
import com.brainsoon.system.service.IDictNameService;
import com.brainsoon.system.service.IDictValueService;
import com.brainsoon.system.service.IFLTXService;
import com.brainsoon.system.service.IStaffService;
import com.brainsoon.system.service.IZTFLService;
/**
 * 动态生成元数据内容帮助类
 * @author zhangpeng
 *
 */
public class MetadataUtil {
	public static String COL_MD_2="col-md-2";
	public static String COL_MD_3="col-md-3";
	public static String COL_MD_4="col-md-4";
	public static String COL_MD_6="col-md-6";
	public static String COL_MD_8="col-md-8";
	public static String COL_MD_9="col-md-9";
	public static String COL_MD_10="col-md-10";
	public static String COL_MD_12="col-md-12";
	public static String COL_MD_125="col-md-125";
	public static String COL_MD_875="col-md-875";
	public static String nessaryCSS = "<i class=\"must\">*</i>";
	public static String groupCSS = "form-group";
	public static String labelCSS = "control-label";
	public static String radioInline = "radio-inline";
	public static String checkboxInline = "checkbox-inline";
	public static String controlCSS = "form-control";
	public static int numPerLine = 3;
	/**
	 * 创建分组标题
	 * @param sb
	 * @param bigTitle
	 * @return
	 */
	public static StringBuffer createTitle(StringBuffer sb,String bigTitle){
		sb.append("   <div class=\"portlet-title\">");
		sb.append("       <div class=\"caption\">").append(bigTitle).append("<a href=\"javascript:;\" onclick=\"togglePortlet(this)\">");
		sb.append("         <i class=\"fa fa-angle-up\"></i></a></div>");
		sb.append("   </div>");
		return sb;
	}
	/**
	 * 创建Input输入框
	 * @param sb
	 * @param metadataDefinition
	 * @param isEdit 是否是编辑
	 * @return
	 */
	public static StringBuffer createInput(StringBuffer sb,String flag,
			MetadataDefinition metadataDefinition, Object object, boolean isEdit) {
		Ca ca =null;
		Map fileMap = null;
		@SuppressWarnings("unused")
		com.brainsoon.semantic.ontology.model.File fileMetata = null;
		if(StringUtils.isNotBlank(flag)){
			fileMetata = (com.brainsoon.semantic.ontology.model.File) object;
			fileMap =fileMetata.getFileMetadataMap();
		}else{
			ca = (Ca) object;
		}
		String value = "";
		if (ca != null && ca.getObjectId() != null) {
			Map<String, String> metaDateMap = ca.getMetadataMap();
			value = (String) metaDateMap.get(metadataDefinition.getFieldName());
		}else if(fileMetata != null&&StringUtils.isNotBlank((String) fileMap.get(metadataDefinition.getFieldName()))){
			value = fileMap.get(metadataDefinition.getFieldName()).toString();
		}
		if (!"1".equals(metadataDefinition.getShowField())) {
			sb.append("               <div class=\"").append(COL_MD_6)
					.append("\">");
			sb.append("                   <div class=\"").append(groupCSS)
					.append("\">");
			sb.append("                           <label class=\"")
					.append(labelCSS).append(" ").append(COL_MD_3)
					.append("\">");
			if ( isEdit) {
				if(metadataDefinition.getAllowNull() == 0){
					sb.append(nessaryCSS);
				}
				sb.append(metadataDefinition.getFieldZhName()).append(
						" :</label>");
				sb.append("                           <div class=\"")
						.append(COL_MD_9).append("\">");
				String fieldModel = "";
				int valueLength = 0;
				int o = 0;
				if(fileMetata != null){
					sb.append(
							"                               <input type=\"text\" name=\"fileMetadataMap['")
							.append(metadataDefinition.getFieldName())
							.append("']\" id=\"")
							.append(metadataDefinition.getFieldName())
							.append("\" class=\"").append(controlCSS);
				}else{
					o++;
					if(metadataDefinition.getOpenAutoComple()!=null && metadataDefinition.getOpenAutoComple().equals("true")){
						sb.append(" <input type=\"hidden\" name=\"completeField"+o+"\" id=\"completeField"+o+"\" value=\""+metadataDefinition.getFieldName()+"\"/>");
						sb.append(
							"                               <input type=\"text\" name=\"metadataMap['")
							.append(metadataDefinition.getFieldName())
							.append("']\" id=\"")
							.append(metadataDefinition.getFieldName())
							//onfocus=\"showHide('"+metadataDefinition.getFieldName()+"');\"
							.append("\" onkeyup=\"searchComplete('"+metadataDefinition.getFieldName()+"');\" class=\"").append(controlCSS);
					}else{
					sb.append(
							"                               <input type=\"text\" name=\"metadataMap['")
							.append(metadataDefinition.getFieldName())
							.append("']\" id=\"")
							.append(metadataDefinition.getFieldName())
							.append("\" class=\"").append(controlCSS);
					}
				}
				// sb.append("                          validate[required]");
				if (metadataDefinition.getValidateModel() != null) {
					fieldModel = metadataDefinition.getValidateModel();
				}
				if (metadataDefinition.getValueLength() != null && !"".equals(metadataDefinition.getValueLength())) {
					String val = metadataDefinition.getValueLength();
					if (StringUtils.isNotBlank(val)) {
						if (val.startsWith(",")) {
							val = val.substring(1, val.length());
						} else if (val.indexOf(",") > 0) {
							val = val.substring(val.indexOf(",") + 1,
									val.length());
						}
						valueLength = Integer.parseInt(val);
					}
				} else {
					valueLength = 32;
				}
//				if(StringUtils.isNotBlank(fieldModel)){
//					String rule = "";
//					if(fieldModel.startsWith("js_")){
//						rule = fieldModel.substring(3,fieldModel.length());
//						sb.append("         validate[maxSize[" + valueLength
//								+ "],");
//						if (metadataDefinition.getAllowNull() == 0) {
//							sb.append(" required,");
//						}
//						sb.append("custom[").append(rule).append("]]");
//					}else if(fieldModel.startsWith("ajax_")){
//						rule = fieldModel.substring(5,fieldModel.length());
//						sb.append("         validate[maxSize[" + valueLength
//								+ "],");
//						if (metadataDefinition.getAllowNull() == 0) {
//							sb.append(" required,");
//						}
//						sb.append("ajax[").append(rule).append("]]");
//					}else{
//						sb.append(" ");
//						if (metadataDefinition.getAllowNull() == 0) {
//							sb.append(" validate[required]");
//						}
//					}
//				}
//				if (metadataDefinition.getAllowNull() == 0
//						&& "1".equals(fieldModel)) {
//					sb.append(" validate[required]");
//				}
				if(!"".equals(fieldModel)){
					int valiate = Integer.parseInt(fieldModel);
					switch (valiate) {
					case 1:
						sb.append(" ");
						if (metadataDefinition.getAllowNull() == 0) {
							sb.append(" validate[required]");
						}
						break;
					case 2:
						sb.append("         validate[maxSize[" + valueLength
								+ "],");
						if (metadataDefinition.getAllowNull() == 0) {
							sb.append(" required,");
						}
						sb.append("custom[number]]");
						break;
					case 3:
						sb.append("         validate[maxSize[" + valueLength
								+ "],");
						if (metadataDefinition.getAllowNull() == 0) {
							sb.append(" required,");
						}
						sb.append("custom[onlyLetterSp]]");
						break;
					case 4:
						sb.append("         validate[maxSize[" + valueLength
								+ "],");
						if (metadataDefinition.getAllowNull() == 0) {
							sb.append(" required,");
						}
						sb.append("custom[checkISBN]]");
						break;
					case 5:
						sb.append("         validate[maxSize[" + valueLength
								+ "],");
						if (metadataDefinition.getAllowNull() == 0) {
							sb.append(" required,");
						}
						sb.append("custom[isCN]]");
						break;
					case 6:
						sb.append("         validate[maxSize[" + valueLength
								+ "],");
						if (metadataDefinition.getAllowNull() == 0) {
							sb.append(" required,");
						}
						sb.append("custom[email]]");
						break;
					case 7:
						sb.append("         validate[maxSize[" + valueLength
								+ "],");
						if (metadataDefinition.getAllowNull() == 0) {
							sb.append(" required,");
						}
						sb.append("custom[onlyLetterSp]]");
						break;
					case 8:
						sb.append("         validate[maxSize[" + valueLength
								+ "],");
						if (metadataDefinition.getAllowNull() == 0) {
							sb.append(" required,");
						}
						sb.append("ajax[isbn]]");
						break;
					}
				}
				if (StringUtils.isNotBlank(value)&&!value.contains("http")) {
					sb.append("\" value=\"").append(value);
				}else if(StringUtils.isNotBlank(metadataDefinition.getDefaultValue())){
					sb.append("\" value=\"").append(metadataDefinition.getDefaultValue());
				}
				sb.append("\"");
				if("true".equals(metadataDefinition.getReadOnly())){
					
					sb.append(" readonly=\"readonly\"");
				}
				sb.append("\" />");
			} else {
				sb.append(metadataDefinition.getFieldZhName()).append(
						" :</label>");
				sb.append("                           <div class=\"")
						.append(COL_MD_9).append("\">");
				sb.append("                           <div class=\"form-control-static\">");
				if (StringUtils.isNotBlank(value)&&!value.contains("http")) {
					if(value.length()>30){
						String newName= value.substring(0,30)+"......";
						sb.append("<span data-toggle=\"tooltip\" data-placement=\"top\" title=\"").append(value).append("\"> ").append(newName).append("</span>");
					}else{
						sb.append(value);
					}
				}
				sb.append("                     	  </div>");
//				sb.append(metadataDefinition.getFieldZhName()).append(
//						" :</label>");
//				sb.append("                           <div class=\"")
//						.append(COL_MD_9).append("\">");
//				int fieldModel = 0;
//				int valueLength = 0;
//				sb.append(
//						"                               <input type=\"text\" name=\"metadataMap['")
//						.append(metadataDefinition.getFieldName())
//						.append("']\" id=\"")
//						.append(metadataDefinition.getFieldName())
//						.append("\" class=\"").append(controlCSS);
//				if (metadataDefinition.getValidateModel() != null) {
//					fieldModel = metadataDefinition.getValidateModel();
//				}
//				if (StringUtils.isNotBlank(metadataDefinition.getValueLength())) {
//					String val = metadataDefinition.getValueLength();
//					if (val.startsWith(",")) {
//						val = val.substring(1, val.length());
//					} else if (val.indexOf(",") > 0) {
//						val = val.substring(val.indexOf(",") + 1, val.length());
//					}
//					valueLength = Integer.parseInt(val);
//				} else {
//					valueLength = 32;
//				}
//				switch (fieldModel) {
//				case 1:
//					sb.append(" ");
//					break;
//				case 2:
//					sb.append("         validate[maxSize[" + valueLength
//							+ "],custom[number]]");
//					break;
//				case 3:
//					sb.append("         validate[maxSize[" + valueLength
//							+ "],custom[onlyLetterSp]]");
//					break;
//				case 4:
//					sb.append("         validate[maxSize[" + valueLength
//							+ "],custom[checkISBN]]");
//					break;
//				case 5:
//					sb.append("         validate[maxSize[" + valueLength
//							+ "],custom[isCN]]]");
//					break;
//				case 6:
//					sb.append("         validate[maxSize[" + valueLength
//							+ "],custom[email]]");
//					break;
//				case 7:
//					sb.append("         validate[maxSize[" + valueLength + "]]");
//					break;
//				}
//				if (StringUtils.isNotBlank(value)) {
//					sb.append("\" value=\"").append(value);
//				}
//				sb.append("\" />");
			}
			sb.append("                     	  </div>");
			sb.append("                   </div>");
			sb.append("               </div>");
		}
		return sb;
	}
	/**
	 * 创建select选择框
	 * @param sb
	 * @param metadataDefinition
	 * @return
	 */
	public static StringBuffer createSelect(StringBuffer sb,String flag,MetadataDefinition metadataDefinition,Object object,boolean isEdit,boolean multipleSelect){
		Ca ca =null;
		Map fileMap = null;
		String[] mulValue = null;
		@SuppressWarnings("unused")
		com.brainsoon.semantic.ontology.model.File fileMetata = null;
		if(StringUtils.isNotBlank(flag)){
			fileMetata = (com.brainsoon.semantic.ontology.model.File) object;
			fileMap =fileMetata.getFileMetadataMap();
		}else{
			ca = (Ca) object;
		}
		String value = "";
		if (ca != null && ca.getObjectId() != null) {
			Map<String, String> metaDateMap = ca.getMetadataMap();
			value = (String) metaDateMap.get(metadataDefinition.getFieldName());
		}else if(fileMetata != null&&StringUtils.isNotBlank((String) fileMap.get(metadataDefinition.getFieldName()))){
			value = fileMap.get(metadataDefinition.getFieldName()).toString();
		}
		if(StringUtils.isNotBlank(value)){
			mulValue = value.split(",");
		}
		sb.append("               <div class=\"").append(COL_MD_6).append("\">");
		sb.append("                   <div class=\"").append(groupCSS).append("\">");
		sb.append("                           <label class=\"").append(labelCSS).append(" ").append(COL_MD_3).append("\">");
		if(metadataDefinition.getAllowNull()==0 && isEdit){
			sb.append(nessaryCSS);
		}
		sb.append(metadataDefinition.getFieldZhName()).append(" :</label>");	
		sb.append("                           <div class=\"").append(COL_MD_9).append("\">");
		LinkedHashMap<String, String> childMap = OperDbUtils.queryValueIdByKey(metadataDefinition.getValueRange());
		if(isEdit){
			if(fileMetata != null){
				sb.append("     <select name=\"fileMetadataMap['").append(metadataDefinition.getFieldName()).append("']\" id='").append(metadataDefinition.getFieldName()).append("'");
				sb.append("     class='").append(controlCSS);
			}else{
				sb.append("     <select name=\"metadataMap['").append(metadataDefinition.getFieldName()).append("']\" id='").append(metadataDefinition.getFieldName()).append("'");
				sb.append("     class='").append(controlCSS);	
			}
			if(metadataDefinition.getAllowNull()==0 && isEdit){
				sb.append("                          validate[required]");
			}
			if(multipleSelect){
				sb.append("                               ' multiple=\"multiple\">");
			}else{
				sb.append("                               '>");
			}
//			String[] options = valueRange.split(",");
//			for(String option:options){
//				boolean isSelected = false;
//				if(mulValue!=null){
//					for(String oneValue:mulValue){
//						if(oneValue.equals(option)){
//							isSelected = true;
//							break;
//						}
//					}
//				}
//				if(isSelected){
//					sb.append("<option value='").append(option).append("' selected>").append(option).append("</option>");
//				}else{
//					sb.append("<option value='").append(option).append("'>").append(option).append("</option>");
//				}
//			}
			 if(childMap != null){
		        	for(Iterator it = childMap.entrySet().iterator();it.hasNext();){ 
		        		boolean isSelected = false;
			            Entry<Object, String> entry = (Entry<Object, String>)it.next();
			            if(StringUtils.isNotBlank(value)){
			            	for(String oneValue:mulValue){
								if(oneValue.equals(entry.getKey().toString())){
									isSelected = true;
									break;
								}
							}
			            }
			            if(isSelected){
							sb.append("<option value='").append(entry.getKey().toString()).append("' selected>").append(entry.getValue().toString()).append("</option>");
						}else{
							sb.append("<option value='").append(entry.getKey().toString()).append("'>").append(entry.getValue().toString()).append("</option>");
						}
			        }
		        }
			sb.append("</select>");
		}else{
			sb.append("                           <div class=\"form-control-static\">");
			IDictValueService dictValueService = MetadataSupport.getDictValueService();
			if(StringUtils.isNotBlank(value)){
				value = dictValueService.getDictValueById(value);
			}
			if(StringUtils.isBlank(value)){
				value = "";
			}
			sb.append(value);
			sb.append("                     	  </div>");
		}
		sb.append("                     	  </div>");
		sb.append("                   </div>");
		sb.append("               </div>");
		return sb;
	}
	/**
	 * 创建radio单择框
	 * @param sb
	 * @param metadataDefinition
	 * @return
	 */
	public static StringBuffer createRadio(StringBuffer sb,String flag,MetadataDefinition metadataDefinition,Object object,boolean isEdit){
		Ca ca =null;
		Map fileMap = null;
		String[] mulValue = null;
		@SuppressWarnings("unused")
		com.brainsoon.semantic.ontology.model.File fileMetata = null;
		if(StringUtils.isNotBlank(flag)){
			fileMetata = (com.brainsoon.semantic.ontology.model.File) object;
			fileMap =fileMetata.getFileMetadataMap();
		}else{
			ca = (Ca) object;
		}
		String value = "";
		if (ca != null && ca.getObjectId() != null) {
			Map<String, String> metaDateMap = ca.getMetadataMap();
			value = (String) metaDateMap.get(metadataDefinition.getFieldName());
		}else if(fileMetata != null&&StringUtils.isNotBlank((String) fileMap.get(metadataDefinition.getFieldName()))){
			value = fileMap.get(metadataDefinition.getFieldName()).toString();
		}
		String valueRange = metadataDefinition.getValueRange();
		String[] options = valueRange.split(",");
		sb.append("               <div class=\"").append(COL_MD_6).append("\">");
		sb.append("                   <div class=\"").append(groupCSS).append("\">");
		sb.append("                           <label class=\"").append(labelCSS).append(" ").append(COL_MD_3).append("\">");
		if(metadataDefinition.getAllowNull()==0 && isEdit){
			sb.append(nessaryCSS);
		}
		sb.append(metadataDefinition.getFieldZhName()).append(" :</label>");	
		sb.append("<div class=\"").append(COL_MD_9).append("\">");
		int num = 0;
		if(isEdit){
			int i=0;
			for(String option:options){
				if(i>0 && i%numPerLine == 0){
					sb.append("                           <br>");
				}
				sb.append(" <label class=\"").append(radioInline).append("\">");
				if(value!=null && value.equals(option)){
					if(fileMetata != null){
						if(metadataDefinition.getAllowNull()==0){ //必填
							sb.append(" <input type='radio'  class=\"validate[minCheckbox[1]]\" name=\"fileMetadataMap[").append(metadataDefinition.getFieldName()).append("]\" id='").append(metadataDefinition.getFieldName()).append("' ");
							sb.append(" value='").append(option).append("' checked/>").append(option);
						}else{
							sb.append(" <input type='radio'  name=\"fileMetadataMap[").append(metadataDefinition.getFieldName()).append("]\" id='").append(metadataDefinition.getFieldName()).append("' ");
							sb.append(" value='").append(option).append("' checked/>").append(option);
						}
					}else{
						if(metadataDefinition.getAllowNull()==0){ //必填
							sb.append(" <input type='radio' class=\"validate[minCheckbox[1]]\" name=\"metadataMap[").append(metadataDefinition.getFieldName()).append("]\" id='").append(metadataDefinition.getFieldName()).append("' ");
							sb.append(" value='").append(option).append("' checked/>").append(option);	
						}else{
							sb.append(" <input type='radio' name=\"metadataMap[").append(metadataDefinition.getFieldName()).append("]\" id='").append(metadataDefinition.getFieldName()).append("' ");
							sb.append(" value='").append(option).append("' checked/>").append(option);	
						}
					}
				}else{
					if(fileMetata != null){
						if(metadataDefinition.getAllowNull()==0){ //必填
							sb.append(" <input type='radio' class=\"validate[minCheckbox[1]]\" name=\"fileMetadataMap[").append(metadataDefinition.getFieldName()).append("]\" id='").append(metadataDefinition.getFieldName()).append("' ");
							sb.append(" value='").append(option).append("' />").append(option);
						}else{
							sb.append(" <input type='radio' name=\"fileMetadataMap[").append(metadataDefinition.getFieldName()).append("]\" id='").append(metadataDefinition.getFieldName()).append("' ");
							sb.append("  value='").append(option).append("' />").append(option);
						}
					}else{
						if(metadataDefinition.getAllowNull()==0){ //必填
							sb.append(" <input type='radio' class=\"validate[minCheckbox[1]]\" name=\"metadataMap[").append(metadataDefinition.getFieldName()).append("]\" id='").append(metadataDefinition.getFieldName()).append("' ");
							sb.append(" value='").append(option).append("'");
							//此代码为默认选中第一个radio，暂时去掉
//							if(num ==0){
//								sb.append(" checked=\"checked\"");
//								num++;
//							}
						}else{
							sb.append(" <input type='radio' name=\"metadataMap[").append(metadataDefinition.getFieldName()).append("]\" id='").append(metadataDefinition.getFieldName()).append("' ");
							sb.append(" value='").append(option).append("'");
						}
						sb.append("/>").append(option);
					}
				}
				sb.append("         </label>");
				i++;
			}
		}else{
			sb.append("                           <div class=\"form-control-static\">");
			if(StringUtils.isBlank(value)){
				value = "";
			}
			sb.append(value);
			sb.append("                     	  </div>");
		}
		sb.append("                     	  </div>");
		sb.append("                   </div>");
		sb.append("               </div>");
		return sb;
	}
	/**
	 * 创建弹出分类框
	 * @param sb
	 * @param metadataDefinition
	 * @return
	 */
	public static StringBuffer createLookup(StringBuffer sb,String flag,MetadataDefinition metadataDefinition,Object object,boolean isEdit,String basePath){
		Ca ca =null;
		Map fileMap = null;
		String[] mulValue = null;
		@SuppressWarnings("unused")
		com.brainsoon.semantic.ontology.model.File fileMetata = null;
		if(StringUtils.isNotBlank(flag)){
			fileMetata = (com.brainsoon.semantic.ontology.model.File) object;
			fileMap =fileMetata.getFileMetadataMap();
		}else{
			ca = (Ca) object;
		}
		String value = "";
		if (ca != null && ca.getObjectId() != null) {
			Map<String, String> metaDateMap = ca.getMetadataMap();
			value = (String) metaDateMap.get(metadataDefinition.getFieldName());
		}else if(fileMetata != null&&StringUtils.isNotBlank((String) fileMap.get(metadataDefinition.getFieldName()))){
			value = fileMap.get(metadataDefinition.getFieldName()).toString();
		}
		sb.append("   <script type=\"text/javascript\">");
		sb.append("         function clear").append(metadataDefinition.getFieldName()).append("Category() {");
		sb.append("             $(\"#").append(metadataDefinition.getFieldName()+"Name").append("\").val('');");
		sb.append("             $(\"#").append(metadataDefinition.getFieldName()).append("\").val('');");
		sb.append("         }");
		sb.append("         function show").append(metadataDefinition.getFieldName()).append("Category() {");
		sb.append("             $.openWindow(\"").append(basePath).append("/system/dataManagement/classification/ztflSelect.jsp?fieldName=").append(metadataDefinition.getFieldName()).append("&typeId=").append(metadataDefinition.getValueRange()).append("\",'选择分类',500,450);");
		sb.append("         }");
		sb.append("    </script>");
		sb.append("               <div class=\"").append(COL_MD_6).append("\">");
		sb.append("                   <div class=\"").append(groupCSS).append("\">");
		sb.append("                           <label class=\"").append(labelCSS).append(" ").append(COL_MD_3).append("\">");
		if(metadataDefinition.getAllowNull()==0 && isEdit){
			sb.append(nessaryCSS);
		}
		sb.append(metadataDefinition.getFieldZhName()).append(" :</label>");	
		sb.append("                           <div class=\"").append(COL_MD_9).append("\">");
		if(isEdit){
			sb.append("                              <input type=\"text\" name=\"").append(metadataDefinition.getFieldName()+"Name").append("\" id=\"").append(metadataDefinition.getFieldName()+"Name").append("\" style=\"width:62%;display: inline;\" readonly=\"readonly\" class=\"").append(controlCSS);
			if(metadataDefinition.getAllowNull()==0){
				sb.append("                          validate[required]");
			}
			if(StringUtils.isNotBlank(value)){
				IFLTXService fLTXService = MetadataSupport.getFLTXService();
				sb.append("\" value=\"").append(fLTXService.queryCatagoryCnName(value));
			}
			sb.append("\" />");
			if(fileMetata != null){
				sb.append("            <input type=\"hidden\" name=\"fileMetadataMap['").append(metadataDefinition.getFieldName()).append("']\" id=\"").append(metadataDefinition.getFieldName()).append("\" value=\"").append(value).append("\" />");
			}else{
				sb.append("            <input type=\"hidden\" name=\"metadataMap['").append(metadataDefinition.getFieldName()).append("']\" id=\"").append(metadataDefinition.getFieldName()).append("\" value=\"").append(value).append("\" />");	
			}
			sb.append("<a onclick=\"show").append(metadataDefinition.getFieldName()).append("Category();\" href=\"###\"  role=\"button\" style=\"margin-left: 1%;display: inline;\"><img src=\""+basePath+"appframe/main/images/select.png\" alt=\"选择\"></a>");
			sb.append("<a onclick=\"clear").append(metadataDefinition.getFieldName()).append("Category();\" href=\"###\"  role=\"button\" style=\"margin-left: 1%;display: inline;\"><img src=\""+basePath+"appframe/main/images/clean.png\" alt=\"清除\"></a>");
		}else{
			sb.append("                           <div class=\"form-control-static\">");
			if(StringUtils.isNotBlank(value)){
				IFLTXService fLTXService = MetadataSupport.getFLTXService();
				String categoryName = fLTXService.queryCatagoryCnName(value);
				if(categoryName.length()>30){
					String newName= categoryName.substring(0,30)+"......";
					sb.append("<span data-toggle=\"tooltip\" data-placement=\"top\" title=\"").append(categoryName).append("\"> ").append(newName).append("</span>");
				}else{
					sb.append(categoryName);
				}
			}
			sb.append("                     	  </div>");
		}
		sb.append("                     	  </div>");
		sb.append("                   </div>");
		sb.append("               </div>");
		return sb;
	}
	/**
	 * 创建checkbox多择框
	 * @param sb
	 * @param metadataDefinition
	 * @return
	 */
	public static StringBuffer createCheckbox(StringBuffer sb,String flag,MetadataDefinition metadataDefinition,Object object,boolean isEdit){
		Ca ca =null;
		Map fileMap = null;
		String[] mulValue = null;
		@SuppressWarnings("unused")
		com.brainsoon.semantic.ontology.model.File fileMetata = null;
		if(StringUtils.isNotBlank(flag)){
			fileMetata = (com.brainsoon.semantic.ontology.model.File) object;
			fileMap =fileMetata.getFileMetadataMap();
		}else{
			ca = (Ca) object;
		}
		String value = "";
		if (ca != null && ca.getObjectId() != null) {
			Map<String, String> metaDateMap = ca.getMetadataMap();
			value = (String) metaDateMap.get(metadataDefinition.getFieldName());
		}else if(fileMetata != null&&StringUtils.isNotBlank((String) fileMap.get(metadataDefinition.getFieldName()))){
			value = fileMap.get(metadataDefinition.getFieldName()).toString();
		}
		if(StringUtils.isNotBlank(value)){
			mulValue = value.split(",");
		}
		String valueRange = metadataDefinition.getValueRange();
		String[] options = valueRange.split(",");
		sb.append("               <div class=\"").append(COL_MD_6).append("\">");
		sb.append("                   <div class=\"").append(groupCSS).append("\">");
		sb.append("                           <label class=\"").append(labelCSS).append(" ").append(COL_MD_3).append("\">");
		if(metadataDefinition.getAllowNull()==0 && isEdit){
			sb.append(nessaryCSS);
		}
		sb.append(metadataDefinition.getFieldZhName()).append(" :</label>");	
		sb.append("                           <div class=\"").append(COL_MD_9).append("\">");
		if(isEdit){
			int i=0;
			for(String option:options){
				if(i>0 && i%numPerLine == 0){
					sb.append("                           <br>");
				}
				boolean isChecked = false;
				if(mulValue!=null){
					for(String oneValue:mulValue){
						if(oneValue.equals(option)){
							isChecked = true;
							break;
						}
					}
				}
				sb.append("<label class=\"").append(checkboxInline).append("\">");
				if(isChecked){
					if(fileMetata != null){
						if(metadataDefinition.getAllowNull()==0){ //必填
							sb.append("<input class=\"validate[minCheckbox[1]]\" type='checkbox' name=\"fileMetadataMap[").append(metadataDefinition.getFieldName()).append("]\" id='").append(metadataDefinition.getFieldName()).append("' ");
							sb.append(" value='").append(option).append("' checked/>").append(option);
						}else{
							sb.append("<input type='checkbox' name=\"fileMetadataMap[").append(metadataDefinition.getFieldName()).append("]\" id='").append(metadataDefinition.getFieldName()).append("' ");
							sb.append(" value='").append(option).append("' checked/>").append(option);
						}
						
					}else{
						if(metadataDefinition.getAllowNull()==0){ //必填
							sb.append("<input class=\"validate[minCheckbox[1]]\" type='checkbox' name=\"metadataMap[").append(metadataDefinition.getFieldName()).append("]\" id='").append(metadataDefinition.getFieldName()).append("' ");
							sb.append(" value='").append(option).append("' checked/>").append(option);	
						}else{
							sb.append("<input type='checkbox' name=\"metadataMap[").append(metadataDefinition.getFieldName()).append("]\" id='").append(metadataDefinition.getFieldName()).append("' ");
							sb.append(" value='").append(option).append("' checked/>").append(option);	
						}
					}
				}else{
					if(fileMetata != null){
						if(metadataDefinition.getAllowNull()==0){ //必填
							sb.append("<input class=\"validate[minCheckbox[1]]\" type='checkbox' name=\"fileMetadataMap[").append(metadataDefinition.getFieldName()).append("]\" id='").append(metadataDefinition.getFieldName()).append("' ");
							sb.append(" value='").append(option).append("' />").append(option);
						}else{
							sb.append("<input type='checkbox' name=\"fileMetadataMap[").append(metadataDefinition.getFieldName()).append("]\" id='").append(metadataDefinition.getFieldName()).append("' ");
							sb.append(" value='").append(option).append("' />").append(option);
						}
					}else{
						if(metadataDefinition.getAllowNull()==0){ //必填
							sb.append("<input  class=\"validate[minCheckbox[1]]\" type='checkbox' name=\"metadataMap[").append(metadataDefinition.getFieldName()).append("]\" id='").append(metadataDefinition.getFieldName()).append("' ");
							sb.append(" value='").append(option).append("' />").append(option);	
						}else{
							sb.append("<input type='checkbox' name=\"metadataMap[").append(metadataDefinition.getFieldName()).append("]\" id='").append(metadataDefinition.getFieldName()).append("' ");
							sb.append(" value='").append(option).append("' />").append(option);	
						}
					}
				}
				sb.append("                           </label>");
				i++;
			}
		}else{
			sb.append("                           <div class=\"form-control-static\">");
			if(value!=null && value.contains("http")){
				value = "";
			}
			if(StringUtils.isBlank(value)){
				value = "";
			}else{
				if(value.length()>30){
					String newName= value.substring(0,30)+"......";
					sb.append("<span data-toggle=\"tooltip\" data-placement=\"top\" title=\"").append(value).append("\"> ").append(newName).append("</span>");
				}else{
					sb.append("<span> ").append(value).append("</span>");
				}
			}
			sb.append("                     	  </div>");
		}
		sb.append("                     	  </div>");
		sb.append("                   </div>");
		sb.append("               </div>");
		return sb;
	}
	/**
	 * 创建Input输入框
	 * @param sb
	 * @param metadataDefinition
	 * @return
	 */
	public static StringBuffer createTextarea(StringBuffer sb,String flag,MetadataDefinition metadataDefinition,Object object,boolean isEdit){
		Ca ca =null;
		Map fileMap = null;
		String[] mulValue = null;
		@SuppressWarnings("unused")
		com.brainsoon.semantic.ontology.model.File fileMetata = null;
		if(StringUtils.isNotBlank(flag)){
			fileMetata = (com.brainsoon.semantic.ontology.model.File) object;
			fileMap =fileMetata.getFileMetadataMap();
		}else{
			ca = (Ca) object;
		}
		String value = "";
		if (ca != null && ca.getObjectId() != null) {
			Map<String, String> metaDateMap = ca.getMetadataMap();
			value = (String) metaDateMap.get(metadataDefinition.getFieldName());
		}else if(fileMetata != null&&StringUtils.isNotBlank((String) fileMap.get(metadataDefinition.getFieldName()))){
			value = fileMap.get(metadataDefinition.getFieldName()).toString();
		}
		sb.append("               <div class=\"").append(COL_MD_12).append("\">");
		sb.append("                   <div class=\"").append(groupCSS).append("\">");
		sb.append("                           <label class=\"").append(labelCSS).append(" ").append(COL_MD_125).append("\">");
		if(metadataDefinition.getAllowNull()==0 && isEdit){
			sb.append(nessaryCSS);
		}
		sb.append(metadataDefinition.getFieldZhName()).append(" :</label>");	
		sb.append("                           <div class=\"").append(COL_MD_875).append("\">");
		if(isEdit){
			if(fileMetata != null){
				sb.append("                               <textarea  name=\"fileMetadataMap['").append(metadataDefinition.getFieldName()).append("']\" id=\"").append(metadataDefinition.getFieldName()).append("\" rows=4  class=\"").append(controlCSS);
			}else{
				sb.append("                               <textarea  name=\"metadataMap['").append(metadataDefinition.getFieldName()).append("']\" id=\"").append(metadataDefinition.getFieldName()).append("\" rows=4  class=\"").append(controlCSS);	
			}
			if(metadataDefinition.getAllowNull()==0){
				sb.append("                          validate[required]");
			}
			sb.append("\">");
			if(StringUtils.isNotBlank(value)){
				sb.append(value);
			}
			sb.append("</textarea>");
		}else{
			sb.append("                               <textarea  name=\"").append(metadataDefinition.getFieldName()).append("\" id=\"").append(metadataDefinition.getFieldName()).append("\" rows=4 readonly=\"readonly\" class=\"").append(controlCSS);
			sb.append("\">");
			if(StringUtils.isNotBlank(value)){
				sb.append(value);
			}
			sb.append("</textarea>");
		}
		sb.append("                     	  </div>");
		sb.append("                   </div>");
		sb.append("               </div>");
		return sb;
	}
	/**
	 * 创建日期类型选择框
	 * @param sb
	 * @param metadataDefinition
	 * @return
	 */
	public static StringBuffer createDateTime(StringBuffer sb,String flag,MetadataDefinition metadataDefinition,Object object,boolean isEdit,String dateFmt){
		Ca ca =null;
		Map fileMap = null;
		@SuppressWarnings("unused")
		com.brainsoon.semantic.ontology.model.File fileMetata = null;
		if(StringUtils.isNotBlank(flag)){
			fileMetata = (com.brainsoon.semantic.ontology.model.File) object;
			fileMap =fileMetata.getFileMetadataMap();
		}else{
			ca = (Ca) object;
		}
		String value = "";
		String format = metadataDefinition.getValueRange();
		if (ca != null && ca.getObjectId() != null) {
			Map<String, String> metaDateMap = ca.getMetadataMap();
			String dicValue = (String) metaDateMap.get(metadataDefinition.getFieldName());
			if (StringUtils.isNotBlank(dicValue)) {
				try {
					Date dateOld = new Date(Long.parseLong(dicValue)); // 根据long类型的毫秒数生命一个date类型的时间
					value = new SimpleDateFormat(format).format(dateOld);
				} catch (Exception e) {
					value = dicValue;
				}
			}
		}else if(fileMetata != null&&StringUtils.isNotBlank((String) fileMap.get(metadataDefinition.getFieldName()))){
			String dicValue = fileMap.get(metadataDefinition.getFieldName()).toString();
			if (StringUtils.isNotBlank(dicValue)) {
				try {
					Date dateOld = new Date(Long.parseLong(dicValue)); // 根据long类型的毫秒数生命一个date类型的时间
					value = new SimpleDateFormat(format).format(dateOld);
				} catch (Exception e) {
					value = dicValue;
				}
			}
		}
		if("null".equals(value)){
			value = "";
		}
		sb.append("               <div class=\"").append(COL_MD_6).append("\">");
		sb.append("                   <div class=\"").append(groupCSS).append("\">");
		sb.append("                           <label class=\"").append(labelCSS).append(" ").append(COL_MD_3).append("\">");
		if(metadataDefinition.getAllowNull()==0 && isEdit){
			sb.append(nessaryCSS);
		}
		sb.append(metadataDefinition.getFieldZhName()).append(" :</label>");	
		sb.append("                           <div class=\"").append(COL_MD_9).append("\">");
		if(isEdit){
			if(fileMetata != null){
				sb.append("                   <input type=\"text\" name=\"fileMetadataMap['").append(metadataDefinition.getFieldName()).append("']\" id=\"").append(metadataDefinition.getFieldName());
				sb.append("					  \" class=\"").append(controlCSS).append(" Wdate");
			}else{
				sb.append("                   <input type=\"text\" name=\"metadataMap['").append(metadataDefinition.getFieldName()).append("']\" id=\"").append(metadataDefinition.getFieldName());
				sb.append("					  \" class=\"").append(controlCSS).append(" Wdate");	
			}
			if(metadataDefinition.getAllowNull()==0){
				sb.append("                          validate[required]");
			}
			if(StringUtils.isBlank(value) ||"null".equals(value)){
				value = "";
			}
			sb.append("\"  value=\"").append(value);
			if(StringUtils.isNotBlank(format)){
				dateFmt = format;
			} 
			sb.append("\" onClick=\"WdatePicker({dateFmt:'").append(dateFmt).append("',readOnly:true})\"/>");
		}else{
			sb.append("                           <div class=\"form-control-static\">");
			if(StringUtils.isBlank(value)){
				value = "";
			}
			sb.append(value);
			sb.append("                     	  </div>");
		}
		sb.append("                     	  </div>");
		sb.append("                   </div>");
		sb.append("               </div>");
		return sb;
	}
	/**
	 * 创建弹出分类框
	 * @param sb
	 * @param metadataDefinition
	 * @return
	 */
	public static StringBuffer createLookupPeopleCompany(StringBuffer sb,String flag,MetadataDefinition metadataDefinition,Object object,boolean isEdit,String basePath,String company){
		Ca ca =null;
		Map fileMap = null;
		String[] mulValue = null;
		@SuppressWarnings("unused")
		com.brainsoon.semantic.ontology.model.File fileMetata = null;
		if(StringUtils.isNotBlank(flag)){
			fileMetata = (com.brainsoon.semantic.ontology.model.File) object;
			fileMap =fileMetata.getFileMetadataMap();
		}else{
			ca = (Ca) object;
		}
		String value = "";
		if (ca != null && ca.getObjectId() != null) {
			Map<String, String> metaDateMap = ca.getMetadataMap();
			value = (String) metaDateMap.get(metadataDefinition.getFieldName());
		}else if(fileMetata != null&&StringUtils.isNotBlank((String) fileMap.get(metadataDefinition.getFieldName()))){
			value = fileMap.get(metadataDefinition.getFieldName()).toString();
		}
		sb.append("   <script type=\"text/javascript\">");
		sb.append("         function clear").append(metadataDefinition.getFieldName()).append("Category() {");
		sb.append("             $(\"#").append(metadataDefinition.getFieldName()+"Name").append("\").val('');");
		sb.append("             $(\"#").append(metadataDefinition.getFieldName()).append("\").val('');");
		sb.append("         }");
		sb.append("         function show").append(metadataDefinition.getFieldName()).append("Category() {");
		if(StringUtils.isNotBlank(company)){
			sb.append("          $.openWindow(\"").append(basePath).append("/system/peopleUnit/companyUnit.jsp?divWidth=1150px&fieldName="+metadataDefinition.getFieldName()+"&valueLength="+metadataDefinition.getValueLength()+"\",\""+metadataDefinition.getFieldZhName()+"\",1200,550)");
		}else{
			sb.append("          $.openWindow(\"").append(basePath).append("/system/peopleUnit/peopleUnit.jsp?divWidth=1150px&fieldName="+metadataDefinition.getFieldName()+"&valueLength="+metadataDefinition.getValueLength()+"\",\""+metadataDefinition.getFieldZhName()+"\",1200,550)");
		}
		sb.append("         }");
		sb.append("    </script>");
		sb.append("               <div class=\"").append(COL_MD_6).append("\">");
		sb.append("                   <div class=\"").append(groupCSS).append("\">");
		sb.append("                           <label class=\"").append(labelCSS).append(" ").append(COL_MD_3).append("\">");
		if(metadataDefinition.getAllowNull()==0 && isEdit){
			sb.append(nessaryCSS);
		}
		sb.append(metadataDefinition.getFieldZhName()).append(" :</label>");	
		sb.append("                           <div class=\"").append(COL_MD_9).append("\">");
		if(isEdit){
			sb.append("                              <input type=\"text\" name=\"").append(metadataDefinition.getFieldName()+"Name").append("\" id=\"").append(metadataDefinition.getFieldName()+"Name").append("\" style=\"width:62%;display: inline;\" readonly=\"readonly\" class=\"").append(controlCSS);
			if(metadataDefinition.getAllowNull()==0){
				sb.append("                          validate[required]");
			}
			if(StringUtils.isNotBlank(company)){
				if(StringUtils.isNotBlank(value)&&!value.contains("http")){
					ICompanyService companyService = MetadataSupport.getCompanyService();
					sb.append("\" value=\"").append(companyService.searchName(value));
				}
			}else{
				if(StringUtils.isNotBlank(value)&&!value.contains("http")){
					IStaffService staffService = MetadataSupport.getStaffService();
					sb.append("\" value=\"").append(staffService.searchName(value));
				}
			}
			sb.append("\" />");
			if(fileMetata != null){
				sb.append("            <input type=\"hidden\" name=\"fileMetadataMap['").append(metadataDefinition.getFieldName()).append("']\" id=\"").append(metadataDefinition.getFieldName()).append("\" value=\"").append(value).append("\" />");
			}else{
				sb.append("            <input type=\"hidden\" name=\"metadataMap['").append(metadataDefinition.getFieldName()).append("']\" id=\"").append(metadataDefinition.getFieldName()).append("\" value=\"").append(value).append("\" />");	
			}
			sb.append("<a onclick=\"show").append(metadataDefinition.getFieldName()).append("Category();\" href=\"###\"  role=\"button\" style=\"margin-left: 1%;display: inline;\"><img src=\""+basePath+"appframe/main/images/select.png\" alt=\"选择\"></a>");
			sb.append("<a onclick=\"clear").append(metadataDefinition.getFieldName()).append("Category();\" href=\"###\" role=\"button\" style=\"margin-left: 1%;display: inline;\"><img src=\""+basePath+"appframe/main/images/clean.png\" alt=\"清除\"></a>");
		}else{
			sb.append("                           <div class=\"form-control-static\">");
			if(StringUtils.isNotBlank(company)){
				if(StringUtils.isNotBlank(value)){
					ICompanyService companyService = MetadataSupport.getCompanyService();
//					IFLTXService fLTXService = MetadataSupport.getFLTXService();
					String categoryName = companyService.searchName(value);
					if(categoryName.length()>30){
						String newName= categoryName.substring(0,30)+"......";
						sb.append("<span data-toggle=\"tooltip\" data-placement=\"top\" title=\"").append(categoryName).append("\"> ").append(newName).append("</span>");
					}else{
						sb.append(categoryName);
					}
				}
			}else{
				if(StringUtils.isNotBlank(value)){
					IStaffService staffService = MetadataSupport.getStaffService();
					String categoryName = staffService.searchName(value);
					if(categoryName.length()>30){
						String newName= categoryName.substring(0,30)+"......";
						sb.append("<span data-toggle=\"tooltip\" data-placement=\"top\" title=\"").append(categoryName).append("\"> ").append(newName).append("</span>");
					}else{
						sb.append(categoryName);
					}
				}
			}
			sb.append("                     	  </div>");
		}
		sb.append("                     	  </div>");
		sb.append("                   </div>");
		sb.append("               </div>");
		return sb;
	}
	/**
	 * 创建URL输入框
	 * @param sb
	 * @param metadataDefinition
	 * @return
	 */
	public static StringBuffer createURL(StringBuffer sb,String flag,MetadataDefinition metadataDefinition,Object object,boolean isEdit){
		Ca ca =null;
		Map fileMap = null;
		String[] mulValue = null;
		@SuppressWarnings("unused")
		com.brainsoon.semantic.ontology.model.File fileMetata = null;
		if(StringUtils.isNotBlank(flag)){
			fileMetata = (com.brainsoon.semantic.ontology.model.File) object;
			fileMap =fileMetata.getFileMetadataMap();
		}else{
			ca = (Ca) object;
		}
		String value = "";
		if (ca != null && ca.getObjectId() != null) {
			Map<String, String> metaDateMap = ca.getMetadataMap();
			value = (String) metaDateMap.get(metadataDefinition.getFieldName());
		}else if(fileMetata != null&&StringUtils.isNotBlank((String) fileMap.get(metadataDefinition.getFieldName()))){
			value = fileMap.get(metadataDefinition.getFieldName()).toString();
		}
		sb.append("   <script type=\"text/javascript\">");
		sb.append("         function showURL() {");
		sb.append("           var url = $(\"#").append(metadataDefinition.getFieldName()).append("\").val();");
		sb.append("           window.open(url,'window');");
		sb.append("         }");
		sb.append("    </script>");
		sb.append("               <div class=\"").append(COL_MD_6).append("\">");
		sb.append("                   <div class=\"").append(groupCSS).append("\">");
		sb.append("                           <label class=\"").append(labelCSS).append(" ").append(COL_MD_3).append("\">");
		if(metadataDefinition.getAllowNull()==0 && isEdit){
			sb.append(nessaryCSS);
		}
		sb.append(metadataDefinition.getFieldZhName()).append(" :</label>");	
		sb.append("                           <div class=\"").append(COL_MD_9).append("\">");
		if(isEdit){
			if(fileMetata != null){
				sb.append("                   <input type=\"text\" name=\"fileMetadataMap['").append(metadataDefinition.getFieldName()).append("']\" id=\"").append(metadataDefinition.getFieldName()).append("\" style=\"width:85%;display: inline;\" class=\"").append(controlCSS);
			}else{
				sb.append("                   <input type=\"text\" name=\"metadataMap['").append(metadataDefinition.getFieldName()).append("']\" id=\"").append(metadataDefinition.getFieldName()).append("\" style=\"width:85%;display: inline;\" class=\"").append(controlCSS);	
			}
			if(metadataDefinition.getAllowNull()==0){
				sb.append("                          validate[required]");
			}
			if(StringUtils.isNotBlank(value)){
				sb.append("\" value=\"").append(value).append("\"/>");
			}else{
				sb.append("\" value=\"http://\"/>");
			}
			sb.append("<a onclick=\"showURL();\" href=\"###\" class=\"btn btn-primary\" role=\"button\" style=\"margin-left: 1%;width:14%;padding:7px 2%;\">查看</a>");
		}else{
			sb.append("                           <div class=\"form-control-static\">");
			sb.append("<a href=\"").append(value).append("\" target=\"_blank\">").append(value).append("</a>");
			sb.append("                     	  </div>");
		}
		
		sb.append("                     	  </div>");
		sb.append("                   </div>");
		sb.append("               </div>");
		return sb;
	}
	/**
	 * 创建多选下拉框
	 * 
	 */
	public static StringBuffer createMultiSelect(StringBuffer sb,MetadataDefinition metadataDefinition,Object object,boolean isEdit,boolean multipleSelect){
		Ca ca =null;
		Map fileMap = null;
		@SuppressWarnings("unused")
		com.brainsoon.semantic.ontology.model.File fileMetata = null;
		if(StringUtils.isNotBlank("")){
			fileMetata = (com.brainsoon.semantic.ontology.model.File) object;
			fileMap =fileMetata.getFileMetadataMap();
		}else{
			ca = (Ca) object;
		}
		String value = "";
		if (ca != null && ca.getObjectId() != null) {
			Map<String, String> metaDateMap = ca.getMetadataMap();
			value = (String) metaDateMap.get(metadataDefinition.getFieldName());
		}else if(fileMetata != null&&StringUtils.isNotBlank((String) fileMap.get(metadataDefinition.getFieldName()))){
			value = fileMap.get(metadataDefinition.getFieldName()).toString();
		}
		sb.append("               <div class=\"").append(COL_MD_6).append("\">");
		sb.append("                   <div class=\"").append(groupCSS).append("\">");
		sb.append("                           <label class=\"").append(labelCSS).append(" ").append(COL_MD_3).append("\">");
		if(metadataDefinition.getAllowNull()==0 && isEdit){
			sb.append(nessaryCSS);
		}
		sb.append(metadataDefinition.getFieldZhName()).append(" :</label>");	
		//sb.append("                           <div onclick='dyAddOpts(\""+metadataDefinition.getValueRange()+"\",\"edit\",\"" + value +"\");' class=\"").append(COL_MD_9).append("\">");
		sb.append("                           <div class=\"").append(COL_MD_9).append("\">");
		if(isEdit){
			//sb.append("                               <select multiple=\"multiple\" name=\"multiSelEle\" id='").append(metadataDefinition.getFieldName()).append("'");
			sb.append("                               <select type=\"multiSelEle\" multiple=\"multiple\" name=\"metadataMap['").append(metadataDefinition.getFieldName()).append("']\" id='").append(metadataDefinition.getFieldName()).append("'");
			sb.append("                             ").append("");
			if(metadataDefinition.getAllowNull()==0 && isEdit){
				sb.append("                          ");
			}
			sb.append("                               >");
			sb.append("</select>");
		}else{
			sb.append("                           <div class=\"form-control-static\">");
		//	sb.append(value);
			sb.append("                     	  </div>");
		}
		sb.append("                     	  </div>");
		sb.append("                   </div>");
		sb.append("               </div>");
		sb.append("   <script type=\"text/javascript\">");
		sb.append("   dyAddOpts(\""+metadataDefinition.getValueRange()+"\",\"edit\",\"" + value +"\", \"" +  metadataDefinition.getFieldName() + "\""
				+ ");");
		sb.append("   </script>");
		return sb;
	}
	public static StringBuffer createDicValue(StringBuffer sb,String flag,
			MetadataDefinition metadataDefinition, Object object, boolean isEdit) throws Exception {
		Ca ca =null;
		Map fileMap = null;
		IZTFLService zTFLService = (IZTFLService)BeanFactoryUtil.getBean("ZTFLService");
		@SuppressWarnings("unused")
		com.brainsoon.semantic.ontology.model.File fileMetata = null;
		if(StringUtils.isNotBlank(flag)){
			fileMetata = (com.brainsoon.semantic.ontology.model.File) object;
			fileMap =fileMetata.getFileMetadataMap();
		}else{
			ca = (Ca) object;
		}
		String value = "";
		if (ca != null && ca.getObjectId() != null) {
			Map<String, String> metaDateMap = ca.getMetadataMap();
			value = (String) metaDateMap.get(metadataDefinition.getFieldName());
		}else if(fileMetata != null&&StringUtils.isNotBlank((String) fileMap.get(metadataDefinition.getFieldName()))){
			value = fileMap.get(metadataDefinition.getFieldName()).toString();
		}
		
		if (!"1".equals(metadataDefinition.getShowField())) {
			sb.append("               <div class=\"").append(COL_MD_6)
					.append("\">");
			sb.append("                   <div class=\"").append(groupCSS)
					.append("\">");
			sb.append("                           <label class=\"")
					.append(labelCSS).append(" ").append(COL_MD_3)
					.append("\">");
			if ( isEdit) {
				if(metadataDefinition.getAllowNull() == 0){
					sb.append(nessaryCSS);
				}
				sb.append(metadataDefinition.getFieldZhName()).append(
						" :</label>");
				sb.append("                           <div class=\"")
						.append(COL_MD_9).append("\">");
				String fieldModel = "";
				int valueLength = 0;
				int o = 0;
				if(fileMetata != null){
					sb.append(
							"                               <input type=\"text\" name=\"fileMetadataMap['")
							.append(metadataDefinition.getFieldName())
							.append("']\" id=\"")
							.append(metadataDefinition.getFieldName())
							.append("\" class=\"").append(controlCSS);
				}else{
					o++;
					if(metadataDefinition.getOpenAutoComple()!=null && metadataDefinition.getOpenAutoComple().equals("true")){
						sb.append(" <input type=\"hidden\" name=\"completeField"+o+"\" id=\"completeField"+o+"\" value=\""+metadataDefinition.getFieldName()+"\"/>");
						sb.append(
							"                               <input type=\"text\" name=\"metadataMap['")
							.append(metadataDefinition.getFieldName())
							.append("']\" id=\"")
							.append(metadataDefinition.getFieldName())
							//onfocus=\"showHide('"+metadataDefinition.getFieldName()+"');\"
							.append("\" onkeyup=\"searchComplete('"+metadataDefinition.getFieldName()+"');\" class=\"").append(controlCSS);
					}else{
						String categoryName = "";
						try {
							categoryName = zTFLService.queryCatagoryCnName(value);
							sb.append(" <input type=\"text\" name=\"metadataMap['")
									.append(metadataDefinition.getFieldName())
									.append("']\" id=\"")
									.append(categoryName)
									.append("\" class=\"").append(controlCSS);
						} catch (Exception e) {
							sb.append(" <input type=\"text\" name=\"metadataMap['")
									.append(metadataDefinition.getFieldName())
									.append("']\" id=\"")
									.append(value)
									.append("\" class=\"").append(controlCSS);
						}
					}
				}
				// sb.append("                          validate[required]");
				if (metadataDefinition.getValidateModel() != null) {
					fieldModel = metadataDefinition.getValidateModel();
				}
				if (metadataDefinition.getValueLength() != null && !"".equals(metadataDefinition.getValueLength())) {
					String val = metadataDefinition.getValueLength();
					if (StringUtils.isNotBlank(val)) {
						if (val.startsWith(",")) {
							val = val.substring(1, val.length());
						} else if (val.indexOf(",") > 0) {
							val = val.substring(val.indexOf(",") + 1,
									val.length());
						}
						valueLength = Integer.parseInt(val);
					}
				} else {
					valueLength = 32;
				}
//				if(StringUtils.isNotBlank(fieldModel)){
//					String rule = "";
//					if(fieldModel.startsWith("js_")){
//						rule = fieldModel.substring(3,fieldModel.length());
//						sb.append("         validate[maxSize[" + valueLength
//								+ "],");
//						if (metadataDefinition.getAllowNull() == 0) {
//							sb.append(" required,");
//						}
//						sb.append("custom[").append(rule).append("]]");
//					}else if(fieldModel.startsWith("ajax_")){
//						rule = fieldModel.substring(5,fieldModel.length());
//						sb.append("         validate[maxSize[" + valueLength
//								+ "],");
//						if (metadataDefinition.getAllowNull() == 0) {
//							sb.append(" required,");
//						}
//						sb.append("ajax[").append(rule).append("]]");
//					}else{
//						sb.append(" ");
//						if (metadataDefinition.getAllowNull() == 0) {
//							sb.append(" validate[required]");
//						}
//					}
//				}
//				if (metadataDefinition.getAllowNull() == 0
//						&& "1".equals(fieldModel)) {
//					sb.append(" validate[required]");
//				}
				if(!"".equals(fieldModel)){
					int valiate = Integer.parseInt(fieldModel);
					switch (valiate) {
					case 1:
						sb.append(" ");
						if (metadataDefinition.getAllowNull() == 0) {
							sb.append(" validate[required]");
						}
						break;
					case 2:
						sb.append("         validate[maxSize[" + valueLength
								+ "],");
						if (metadataDefinition.getAllowNull() == 0) {
							sb.append(" required,");
						}
						sb.append("custom[number]]");
						break;
					case 3:
						sb.append("         validate[maxSize[" + valueLength
								+ "],");
						if (metadataDefinition.getAllowNull() == 0) {
							sb.append(" required,");
						}
						sb.append("custom[onlyLetterSp]]");
						break;
					case 4:
						sb.append("         validate[maxSize[" + valueLength
								+ "],");
						if (metadataDefinition.getAllowNull() == 0) {
							sb.append(" required,");
						}
						sb.append("custom[checkISBN]]");
						break;
					case 5:
						sb.append("         validate[maxSize[" + valueLength
								+ "],");
						if (metadataDefinition.getAllowNull() == 0) {
							sb.append(" required,");
						}
						sb.append("custom[isCN]]");
						break;
					case 6:
						sb.append("         validate[maxSize[" + valueLength
								+ "],");
						if (metadataDefinition.getAllowNull() == 0) {
							sb.append(" required,");
						}
						sb.append("custom[email]]");
						break;
					case 7:
						sb.append("         validate[maxSize[" + valueLength
								+ "],");
						if (metadataDefinition.getAllowNull() == 0) {
							sb.append(" required,");
						}
						sb.append("custom[onlyLetterSp]]");
						break;
					case 8:
						sb.append("         validate[maxSize[" + valueLength
								+ "],");
						if (metadataDefinition.getAllowNull() == 0) {
							sb.append(" required,");
						}
						sb.append("ajax[isbn]]");
						break;
					}
				}
				if (StringUtils.isNotBlank(value)) {
					sb.append("\" value=\"").append(value);
				}else if(StringUtils.isNotBlank(metadataDefinition.getDefaultValue())){
					sb.append("\" value=\"").append(metadataDefinition.getDefaultValue());
				}
				sb.append("\"");
				if("true".equals(metadataDefinition.getReadOnly())){
					
					sb.append(" readonly=\"readonly\"");
				}
				sb.append("\" />");
			} else {
				sb.append(metadataDefinition.getFieldZhName()).append(
						" :</label>");
				sb.append("                           <div class=\"")
						.append(COL_MD_9).append("\">");
				sb.append("                           <div class=\"form-control-static\">");
				if (StringUtils.isNotBlank(value)) {
						String categoryName = "";
						try {
							categoryName = zTFLService.queryDictValue(value);
							if(categoryName.length()>30){
								String newName= categoryName.substring(0,30)+"......";
								sb.append("<span data-toggle=\"tooltip\" data-placement=\"top\" title=\"").append(categoryName).append("\"> ").append(newName).append("</span>");
							}else{
								sb.append(categoryName);
							}
						} catch (Exception e) {
							if(value.length()>30){
								String newName= value.substring(0,30)+"......";
								sb.append("<span data-toggle=\"tooltip\" data-placement=\"top\" title=\"").append(categoryName).append("\"> ").append(newName).append("</span>");
							}else{
								sb.append(value);
							}
						}
				}
				sb.append("                     	  </div>");
//				sb.append(metadataDefinition.getFieldZhName()).append(
//						" :</label>");
//				sb.append("                           <div class=\"")
//						.append(COL_MD_9).append("\">");
//				int fieldModel = 0;
//				int valueLength = 0;
//				sb.append(
//						"                               <input type=\"text\" name=\"metadataMap['")
//						.append(metadataDefinition.getFieldName())
//						.append("']\" id=\"")
//						.append(metadataDefinition.getFieldName())
//						.append("\" class=\"").append(controlCSS);
//				if (metadataDefinition.getValidateModel() != null) {
//					fieldModel = metadataDefinition.getValidateModel();
//				}
//				if (StringUtils.isNotBlank(metadataDefinition.getValueLength())) {
//					String val = metadataDefinition.getValueLength();
//					if (val.startsWith(",")) {
//						val = val.substring(1, val.length());
//					} else if (val.indexOf(",") > 0) {
//						val = val.substring(val.indexOf(",") + 1, val.length());
//					}
//					valueLength = Integer.parseInt(val);
//				} else {
//					valueLength = 32;
//				}
//				switch (fieldModel) {
//				case 1:
//					sb.append(" ");
//					break;
//				case 2:
//					sb.append("         validate[maxSize[" + valueLength
//							+ "],custom[number]]");
//					break;
//				case 3:
//					sb.append("         validate[maxSize[" + valueLength
//							+ "],custom[onlyLetterSp]]");
//					break;
//				case 4:
//					sb.append("         validate[maxSize[" + valueLength
//							+ "],custom[checkISBN]]");
//					break;
//				case 5:
//					sb.append("         validate[maxSize[" + valueLength
//							+ "],custom[isCN]]]");
//					break;
//				case 6:
//					sb.append("         validate[maxSize[" + valueLength
//							+ "],custom[email]]");
//					break;
//				case 7:
//					sb.append("         validate[maxSize[" + valueLength + "]]");
//					break;
//				}
//				if (StringUtils.isNotBlank(value)) {
//					sb.append("\" value=\"").append(value);
//				}
//				sb.append("\" />");
			}
			sb.append("                     	  </div>");
			sb.append("                   </div>");
			sb.append("               </div>");
		}
		return sb;
	}
	public static StringBuffer createMoreSelect(StringBuffer sb,String flag,
			MetadataDefinition metadataDefinition, Object object, boolean isEdit,String singleSelect) {
		Ca ca =null;
		Map fileMap = null;
		@SuppressWarnings("unused")
		com.brainsoon.semantic.ontology.model.File fileMetata = null;
		if(StringUtils.isNotBlank(flag)){
			fileMetata = (com.brainsoon.semantic.ontology.model.File) object;
			fileMap =fileMetata.getFileMetadataMap();
		}else{
			ca = (Ca) object;
		}
		String value = "";
		boolean flagTwo = false;
		if (ca != null && ca.getObjectId() != null) {
			Map<String, String> metaDateMap = ca.getMetadataMap();
			value = (String) metaDateMap.get(metadataDefinition.getFieldName());
		}else if(fileMetata != null&&StringUtils.isNotBlank((String) fileMap.get(metadataDefinition.getFieldName()))){
			value = fileMap.get(metadataDefinition.getFieldName()).toString();
		}
		if (!"1".equals(metadataDefinition.getShowField())) {
			sb.append("               <div class=\"").append(COL_MD_6)
					.append("\">");
			sb.append("                   <div class=\"").append(groupCSS)
					.append("\">");
			sb.append("                           <label class=\"")
					.append(labelCSS).append(" ").append(COL_MD_3)
					.append("\">");
			if ( isEdit) {
				if(metadataDefinition.getAllowNull() == 0){
					sb.append(nessaryCSS);
				}
				sb.append(metadataDefinition.getFieldZhName()).append(
						" :</label>");
				sb.append("                           <div class=\"")
						.append(COL_MD_9).append("\">");
				int o = 0;
				if(fileMetata != null){
//					sb.append(
//							"                               <input type=\"text\" name=\"fileMetadataMap['")
//							.append(metadataDefinition.getFieldName())
//							.append("']\" id=\"")
//							.append(metadataDefinition.getFieldName())
//							.append("\" class=\"").append(controlCSS);
				}else{
					o++;
					if(metadataDefinition.getOpenAutoComple()!=null && metadataDefinition.getOpenAutoComple().equals("true")){
//						sb.append(" <input type=\"hidden\" name=\"completeField"+o+"\" id=\"completeField"+o+"\" value=\""+metadataDefinition.getFieldName()+"\"/>");
//						sb.append(
//							"                               <input type=\"text\" name=\"metadataMap['")
//							.append(metadataDefinition.getFieldName())
//							.append("']\" id=\"")
//							.append(metadataDefinition.getFieldName())
//							//onfocus=\"showHide('"+metadataDefinition.getFieldName()+"');\"
//							.append("\" onkeyup=\"searchComplete('"+metadataDefinition.getFieldName()+"');\" class=\"").append(controlCSS);
					}else{
						
					LinkedHashMap<String, String> childMap = OperDbUtils.queryValueIdByKey(metadataDefinition.getValueRange());
					String id = "";
					String nowField = "";
					id = "metadataMap['"+metadataDefinition.getFieldName()+"']";
					nowField = "metadataMap['"+metadataDefinition.getFieldName()+"']";
					if(id.contains("metadataMap")){
						id = id.replace("metadataMap['", "");
						id = id.replace("']", "");
						flagTwo = true;
					}
					String spanValue = "";
					sb.append("  <div id=\"lead\" class=\"card\">	  ");
					sb.append("  <div class=\"topnav\"> ");
					sb.append("  <a id=\""+id+"_Div\" href=\"javascript:void(0);\" class=\"as\" > ");
					sb.append("  <span id=\"spanValue"+id+"\" title=\"请选择\">请选择</span>  ");
					sb.append("  </a> ");
					sb.append("  </div> ");
					//给隐藏域赋值
					if(flagTwo){
						sb.append(" <input type=\"hidden\" value=\"\" id=\""+id+"\" name=\""+nowField+"\"/>  ");
					}else{
						sb.append(" <input type=\"hidden\" value=\"\" id=\""+id+"\" name=\""+nowField+"\" />  ");
					}
					//单选
					if(StringUtils.isNotBlank(singleSelect)){
						sb.append(" <input type=\"hidden\" value=\"1\" id=\""+id+"MaxNumber\"/>  ");
					//多选
					}else{
						sb.append(" <input type=\"hidden\" value=\"" + metadataDefinition.getValueLength() + "\" id=\""+id+"MaxNumber\"/>  ");
					}
					sb.append(" </div> ");
					sb.append(" <div id=\""+id+"m2\" class=\"xmenu\" style=\"display: none;\"> ");
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
					sb.append(" <dd><ul style=\"width:380px;overflow-y:scroll;\" id=\"selectValue\">");
			        if(childMap != null){
			        	for(Iterator it = childMap.entrySet().iterator();it.hasNext();){  
				            Entry<Object, String> entry = (Entry<Object, String>)it.next();
				            if(StringUtils.isNotBlank(value)){
				            	if(value.contains(entry.getKey().toString())){
				            		spanValue = spanValue+entry.getValue().toString()+",";
				            	}
				            }
				          //判断是否选中
				            sb.append("<li rel=\"").append(entry.getKey()).append("\"");
				            sb.append(">").append(entry.getValue()).append("</li>");
				        }
			        }
					if(spanValue.endsWith(",")){
						spanValue = spanValue.substring(0,spanValue.length()-1);
					}
					if(spanValue.startsWith(",")){
						spanValue = spanValue.substring(1,spanValue.length());
					}
			        sb.append(" </ul></dd>");
					sb.append(" </dl>");
					sb.append(" </div>");
					sb.append("	 <script type=\"text/javascript\">");
//					sb.append("	 var tri_l = 0; var tri_t = 0;  ");
					sb.append("	 $(document).ready(function(){");
					sb.append("	 $(\"#"+id+"_Div\").xMenu({");
					sb.append("	 width :400, ");
					sb.append("	 eventType: \"click\",");
					sb.append("	 dropmenu:\"#"+id+"m2\",");
					sb.append("	 hiddenID : \""+id+"\"");
					sb.append("	  });");
					if(StringUtils.isNotBlank(value)){
						//给隐藏域赋值，给title赋值属性
						sb.append("	if(('"+value+"')!=null){$(\"#"+id+"\").val(\""+value+"\"); $(\"#spanValue"+id+"\").attr(\"title\",\""+spanValue+"\"); $(\"#spanValue"+id+"\").text(\""+spanValue+"\") }");
					}
					sb.append("	if($(\"#"+id+"m2\").height()>300){ $(\"#selectValue\").height('300px');} ");
//					if(StringUtils.isNotBlank(singleSelect)){
//					sb.append("$(\"#"+id+"singleSelect\").val('singleSelect');");
//					}
					sb.append("	  });");
					sb.append("  </script>");
//					sb.append(
//							"                               <input type=\"text\" name=\"metadataMap['")
//							.append(metadataDefinition.getFieldName())
//							.append("']\" id=\"")
//							.append(metadataDefinition.getFieldName())
//							.append("\" class=\"").append(controlCSS);
					}
				}
				// sb.append("                          validate[required]");
//				if (metadataDefinition.getValidateModel() != null) {
//					fieldModel = metadataDefinition.getValidateModel();
//				}
//				if (metadataDefinition.getValueLength() != null && !"".equals(metadataDefinition.getValueLength())) {
//					String val = metadataDefinition.getValueLength();
//					if (StringUtils.isNotBlank(val)) {
//						if (val.startsWith(",")) {
//							val = val.substring(1, val.length());
//						} else if (val.indexOf(",") > 0) {
//							val = val.substring(val.indexOf(",") + 1,
//									val.length());
//						}
//						valueLength = Integer.parseInt(val);
//					}
//				} else {
//					valueLength = 32;
//				}
//				if(!"".equals(fieldModel)){
//					int valiate = Integer.parseInt(fieldModel);
//					switch (valiate) {
//					case 1:
//						sb.append(" ");
//						if (metadataDefinition.getAllowNull() == 0) {
//							sb.append(" validate[required]");
//						}
//						break;
//					case 2:
//						sb.append("         validate[maxSize[" + valueLength
//								+ "],");
//						if (metadataDefinition.getAllowNull() == 0) {
//							sb.append(" required,");
//						}
//						sb.append("custom[number]]");
//						break;
//					case 3:
//						sb.append("         validate[maxSize[" + valueLength
//								+ "],");
//						if (metadataDefinition.getAllowNull() == 0) {
//							sb.append(" required,");
//						}
//						sb.append("custom[onlyLetterSp]]");
//						break;
//					case 4:
//						sb.append("         validate[maxSize[" + valueLength
//								+ "],");
//						if (metadataDefinition.getAllowNull() == 0) {
//							sb.append(" required,");
//						}
//						sb.append("custom[checkISBN]]");
//						break;
//					case 5:
//						sb.append("         validate[maxSize[" + valueLength
//								+ "],");
//						if (metadataDefinition.getAllowNull() == 0) {
//							sb.append(" required,");
//						}
//						sb.append("custom[isCN]]");
//						break;
//					case 6:
//						sb.append("         validate[maxSize[" + valueLength
//								+ "],");
//						if (metadataDefinition.getAllowNull() == 0) {
//							sb.append(" required,");
//						}
//						sb.append("custom[email]]");
//						break;
//					case 7:
//						sb.append("         validate[maxSize[" + valueLength
//								+ "],");
//						if (metadataDefinition.getAllowNull() == 0) {
//							sb.append(" required,");
//						}
//						sb.append("custom[onlyLetterSp]]");
//						break;
//					case 8:
//						sb.append("         validate[maxSize[" + valueLength
//								+ "],");
//						if (metadataDefinition.getAllowNull() == 0) {
//							sb.append(" required,");
//						}
//						sb.append("ajax[isbn]]");
//						break;
//					}
//				}
//				if (StringUtils.isNotBlank(value)) {
//					sb.append("\" value=\"").append(value);
//				}else if(StringUtils.isNotBlank(metadataDefinition.getDefaultValue())){
//					sb.append("\" value=\"").append(metadataDefinition.getDefaultValue());
//				}
//				sb.append("\"");
//				if("true".equals(metadataDefinition.getReadOnly())){
//					
//					sb.append(" readonly=\"readonly\"");
//				}
//				sb.append("\" />");
			} else {
				sb.append(metadataDefinition.getFieldZhName()).append(
						" :</label>");
				sb.append("                           <div class=\"")
						.append(COL_MD_9).append("\">");
				sb.append("                           <div class=\"form-control-static\">");
				if (StringUtils.isNotBlank(value)) {
					String spanValue = "";
					LinkedHashMap<String, String> childMap = OperDbUtils.queryValueIdByKey(metadataDefinition.getValueRange());
					for(Iterator it = childMap.entrySet().iterator();it.hasNext();){  
					 Entry<Object, String> entry = (Entry<Object, String>)it.next();
			            if(StringUtils.isNotBlank(value)){
			            	if(value.contains(entry.getKey().toString())){
			            		spanValue = spanValue+entry.getValue().toString()+",";
			            	}
			            }
					}
					if(spanValue.endsWith(",")){
						spanValue = spanValue.substring(0,spanValue.length()-1);
					}
					if(spanValue.length()>30){
						String newName= value.substring(0,30)+"......";
						sb.append("<span data-toggle=\"tooltip\" data-placement=\"top\" title=\"").append(spanValue).append("\"> ").append(newName).append("</span>");
					}else{
						sb.append(spanValue);
					}
				}
				sb.append("                     	  </div>");
				
			}
			sb.append("                     	  </div>");
			sb.append("                   </div>");
			sb.append("               </div>");
		}
		return sb;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

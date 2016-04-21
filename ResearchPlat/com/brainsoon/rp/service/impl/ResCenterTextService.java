package com.brainsoon.rp.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import net.sf.json.JSONObject;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Service;

import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.common.util.dofile.util.ConstantsDef;
import com.brainsoon.common.util.dofile.util.WebAppUtils;
import com.brainsoon.rp.service.IResCenterTextService;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.semantic.ontology.model.CustomMetaDataList;
import com.brainsoon.semantic.ontology.model.Entry;
import com.brainsoon.semantic.ontology.model.Sco;
import com.brainsoon.semantic.schema.ontology.CustomMetaData;
import com.brainsoon.semantic.schema.ontology.MetadataDefinition;
import com.brainsoon.system.support.SystemConstants.ResearchPlatResType;
import com.google.gson.Gson;

/**
 * @ClassName: ResCenterTextService
 * @Description: TODO
 * @author 
 * @date 2016年3月14日 下午3:58:22
 *
 */
@Service
public class ResCenterTextService implements IResCenterTextService {
	
	//根据资源id查询文章资源详细
	private final static String Article_DETAIL_CONTENT = WebappConfigUtil.getParameter("Article_DETAIL_CONTENT");
	//根据资源id查询期刊资源详细
	private final static String JOURNAL_DETAIL_URL = WebappConfigUtil.getParameter("JOURNAL_DETAIL_URL");
	//根据资源id查询大事辑览资源详细(条目只需要元数据，所以查solr)
	private final static String SEARCH_ENTRY_DETAIL = WebappConfigUtil.getParameter("SEARCH_ENTRY_DETAIL");
	//获取资源根路径
	private static final String fileRoot = WebAppUtils.getWebRootBaseDir(ConstantsDef.fileRoot).replaceAll("\\\\", "\\/");
	//查询元数据
	private final static String METADATA_PATH = WebappConfigUtil.getParameter("METADATA_PATH");
	@Override
	public String findByResIdaAndType(String resId, String resType)throws Exception{
		HttpClientUtil http  = new HttpClientUtil();
		String result = null;
		Gson gson = new Gson();
		JSONObject jsonObject = new JSONObject();
		if(resType.equals(ResearchPlatResType.TYPE2)){//大事辑览
			result = http.executeGet(SEARCH_ENTRY_DETAIL+"?id="+resId);
			Entry entry = gson.fromJson(result, Entry.class);
			String sb = entryTabsDiv(entry);
			jsonObject.put("title", entry.getMetadataMap().get("title"));
			jsonObject.put("tabsDiv", sb);
			result = jsonObject.toString();
		}else if(resType.equals(ResearchPlatResType.TYPE1)){//文章
			result = http.executeGet(Article_DETAIL_CONTENT+"?id="+resId);
			Sco sco = gson.fromJson(result, Sco.class);
			List<com.brainsoon.semantic.ontology.model.File> files = sco.getRealFiles();
			if(files!=null){
				for (com.brainsoon.semantic.ontology.model.File file : files) {
					String srcFilePath = fileRoot +"文章/"+ file.getPath();
					String fileEncode = System.getProperty("file.encoding");
					File resFile = new File(new String(srcFilePath.getBytes("UTF-8"),"UTF-8"));
					String contentText = parseXML(resFile);
					sco.getMetadataMap().put("contentText", contentText);
				}
			}
			String sb = scoTabsDiv(sco);
			jsonObject.put("title", sco.getMetadataMap().get("title"));
			jsonObject.put("tabsDiv", sb);
			result = jsonObject.toString();
		}else if(resType.equals(ResearchPlatResType.TYPE0)){//期刊
			result = http.executeGet(JOURNAL_DETAIL_URL+"?id="+resId);
			Ca ca = gson.fromJson(result, Ca.class);
			String sb = caTabsDiv(ca);
			jsonObject.put("title", ca.getMetadataMap().get("title"));
			String path ="/期刊/"+ ca.getRealFiles().get(0).getPath().replaceAll("\\\\", "\\/");
			jsonObject.put("filePathPdf", path);
			path = path.substring(0, path.lastIndexOf("/")+1)+"swf/";
			jsonObject.put("filePath", path);
			jsonObject.put("fileObjectId", ca.getObjectId());
			jsonObject.put("fileType", "PDF");
			jsonObject.put("tabsDiv", sb);
			result = jsonObject.toString();
		}
		return result;
	}
	//期刊拼接div阅读pdf
	private String caTabsDiv(Ca ca) {
		StringBuffer sb = new StringBuffer();
		sb.append("<div>"
				+ "<fieldset><legend>基本信息<a href='javascript:;' onclick='togglePortlet(this)'><i class='fa fa-angle-up'></i></a></legend>"
				+ "<center>"
				+ "<table border='1' width='100%' align='center' style='text-align:center;'>"
				+ "<tr>"
				+ "<td>"
				+ "<span>期刊名称:</span>"
				+ "</td>"
				+"<td>");
		if(ca.getMetadataMap().get("magazine")!=null){
			sb.append("<input class=\"form-control\" readonly='readonly' value="+ca.getMetadataMap().get("magazine")+">");
		}else{
			sb.append("<input class=\"form-control\" readonly='readonly' value=''>");
		}
		sb.append("</td>"
				+ "<td>"
				+ "<span>刊次:</span>"
				+ "</td>"
				+"<td>");
		if(ca.getMetadataMap().get("title")!=null){
			sb.append("<input class=\"form-control\" readonly='readonly' value="+ca.getMetadataMap().get("title")+">");
		}else{
			sb.append("<input class=\"form-control\" readonly='readonly' value=''>");
		}
		sb.append("</td>"
				+ "</tr>"
				+ "<tr>"
				+ "<td>"
				+ "<span>主办部门:</span>"
				+ "</td>"
				+"<td>");
		if(ca.getMetadataMap().get("sponsor")!=null){
			sb.append("<input class=\"form-control\" readonly='readonly' value="+ca.getMetadataMap().get("sponsor")+">");
		}else{
			sb.append("<input class=\"form-control\" readonly='readonly' value=''>");
		}
		sb.append( "</td>"
				+ "<td>"
				+ "<span>主管部门:</span>"
				+ "</td>"
				+"<td>");
		if(ca.getMetadataMap().get("competentDepartment")!=null){
			sb.append("<input class=\"form-control\" readonly='readonly' value="+ca.getMetadataMap().get("competentDepartment")+">");
		}else{
			sb.append("<input class=\"form-control\" readonly='readonly' value=''>");
		}
		sb.append("</td>"
				+ "</tr>"
				+ "<tr>"
				+ "<td>"
				+ "<span>主编:</span>"
				+ "</td>"
				+"<td>");
		if(ca.getMetadataMap().get("chiefEditor")!=null){
			sb.append("<input class=\"form-control\" readonly='readonly' value="+ca.getMetadataMap().get("chiefEditor")+">");
		}else{
			sb.append("<input class=\"form-control\" readonly='readonly' value=''>");
		}
		sb.append( "</td>"
				+ "<td>"
				+ "<span>副主编:</span>"
				+ "</td>"
				+"<td>");
		if(ca.getMetadataMap().get("subeditor")!=null){
			sb.append("<input class=\"form-control\" readonly='readonly' value="+ca.getMetadataMap().get("subeditor")+">");
		}else{
			sb.append("<input class=\"form-control\" readonly='readonly' value=''>");
		}
		sb.append("</td>"
				+ "</tr>"
				+ "<tr>"
				+ "<td>"
				+ "<span>国内发行:</span>"
				+ "</td>"
				+"<td>");
		if(ca.getMetadataMap().get("localContributor")!=null){
			sb.append("<input class=\"form-control\" readonly='readonly' value="+ca.getMetadataMap().get("localContributor")+">");
		}else{
			sb.append("<input class=\"form-control\" readonly='readonly' value=''>");
		}
		sb.append( "</td>"
				+ "<td>"
				+ "<span>国内刊号:</span>"
				+ "</td>"
				+"<td>");
		if(ca.getMetadataMap().get("localSerialNumber")!=null){
			sb.append("<input class=\"form-control\" readonly='readonly' value="+ca.getMetadataMap().get("localSerialNumber")+">");
		}else{
			sb.append("<input class=\"form-control\" readonly='readonly' value=''>");
		}
		sb.append("</td>"
				+ "</tr>"
				+ "<tr>"
				+ "<td>"
				+ "<span>国外发行:</span>"
				+ "</td>"
				+"<td>");
		if(ca.getMetadataMap().get("overseasContributor")!=null){
			sb.append("<input class=\"form-control\" readonly='readonly' value="+ca.getMetadataMap().get("overseasContributor")+">");
		}else{
			sb.append("<input class=\"form-control\" readonly='readonly' value=''>");
		}
		sb.append("</td>"
				+ "<td>"
				+ "<span>国外刊号:</span>"
				+ "</td>"
				+"<td>");
		if(ca.getMetadataMap().get("overseasSerialNumber")!=null){
			sb.append("<input class=\"form-control\" readonly='readonly' value="+ca.getMetadataMap().get("overseasSerialNumber")+">");
		}else{
			sb.append("<input class=\"form-control\" readonly='readonly' value=''>");
		}
		sb.append("</td>"
				+ "</tr>"
				+ "<tr>"
				+ "<td>"
				+ "<span>广告许可证:</span>"
				+ "</td>"
				+"<td>");
		if(ca.getMetadataMap().get("liceseAD")!=null){
			sb.append("<input class=\"form-control\" readonly='readonly' value="+ca.getMetadataMap().get("liceseAD")+">");
		}else{
			sb.append("<input class=\"form-control\" readonly='readonly' value=''>");
		}
		sb.append("</td>"
				+ "<td>"
				+ "<span>印刷者:</span>"
				+ "</td>"
				+"<td>");
		if(ca.getMetadataMap().get("printer")!=null){
			sb.append("<input class=\"form-control\" readonly='readonly' value="+ca.getMetadataMap().get("printer")+">");
		}else{
			sb.append("<input class=\"form-control\" readonly='readonly' value=''>");
		}
		sb.append("</td>"
				+ "</tr>"
				+ "</table>"
				+ "</center>"
				+"</fieldset>"
				+ "</div>"
				+ "<div class='form-group' style='margin-top: 30px;height: 100%;'>"
				+ "<fieldset style='height: 100%;'><legend>正文</legend><div style='height: 100%;'><a id='viewerPlaceHolder"+ca.getObjectId()+"' style='width: 100%; height: 100%; display: block'></a></div>"
				+"</fieldset>"
				+ "</div>");
		return sb.toString();
	}
	//拼接文章div
	private String scoTabsDiv(Sco sco) {
		StringBuffer sb = new StringBuffer();
		sb.append("<div class='portlet'>"
				//+"<div class=\"portlet-title\"><div class=\"caption\">资源信息 <a href=\"javascript:;\" onclick=\"togglePortlet(this)\"><i class=\"fa fa-angle-up\"></i></a></div></div>"
				+ "<fieldset><legend>基本信息<a href='javascript:;' onclick='togglePortlet(this)'><i class='fa fa-angle-up'></i></a></legend>"
				+ "<center class='portlet-body'>"
				+ "<table border='1' width='100%' align='center' style='text-align:center;'>"
				+ "<tr>"
				+ "<td>"
				+ "<span>标题:</span>"
				+ "</td>"
				+"<td>");
		if(sco.getMetadataMap().get("title")!=null){
			sb.append("<input class=\"form-control\" readonly='readonly' value="+sco.getMetadataMap().get("title")+">");
		}else{
			sb.append("<input class=\"form-control\" readonly='readonly' value=''>");
		}
		sb.append("</td>"
				+ "<td>"
				+ "<span>作者:</span>"
				+ "</td>"
				+"<td>");
		if(sco.getMetadataMap().get("wzAuthor")!=null){
			sb.append("<input class=\"form-control\" readonly='readonly' value="+sco.getMetadataMap().get("wzAuthor")+">");
		}else{
			sb.append("<input class=\"form-control\" readonly='readonly' value=''>");
		}
		sb.append("</td>"
				+ "</tr>"
				+ "<tr>"
				+ "<td>"
				+ "<span>期刊名称:</span>"
				+ "</td>"
				+"<td>");
		if(sco.getMetadataMap().get("magazine")!=null){
			sb.append("<input class=\"form-control\" readonly='readonly' value="+sco.getMetadataMap().get("magazine")+">");
		}else{
			sb.append("<input class=\"form-control\" readonly='readonly' value=''>");
		}
		sb.append( "</td>"
				+ "<td>"
				+ "<span>期刊分类:</span>"
				+ "</td>"
				+"<td>");
		if(sco.getMetadataMap().get("wzJournalClass")!=null){
			sb.append("<input class=\"form-control\" readonly='readonly' value="+sco.getMetadataMap().get("wzJournalClass")+">");
		}else{
			sb.append("<input class=\"form-control\" readonly='readonly' value=''>");
		}
		sb.append("</td>"
				+ "</tr>"
				+ "<tr>"
				+ "<td>"
				+ "<span>关键字:</span>"
				+ "</td>"
				+"<td>");
		if(sco.getMetadataMap().get("keywords")!=null){
			sb.append("<input class=\"form-control\" readonly='readonly' value="+sco.getMetadataMap().get("keywords")+">");
		}else{
			sb.append("<input class=\"form-control\" readonly='readonly' value=''>");
		}
		sb.append("</td>"
				+ "<td>"
				+ "<span>网站栏目:</span>"
				+ "</td>"
				+"<td>");
		if(sco.getMetadataMap().get("wzWebsiteCate")!=null){
			sb.append("<input class=\"form-control\" readonly='readonly' value="+sco.getMetadataMap().get("wzWebsiteCate")+">");
		}else{
			sb.append("<input class=\"form-control\" readonly='readonly' value=''>");
		}
		sb.append("</td>"
				+ "</tr>"
				+ "</table>"
				+ "</center>"
				+"</fieldset>"
				+ "</div>"
				+ "<div class='form-group' style='margin-top: 30px;'>"
				+ "<fieldset><legend>正文</legend>"+sco.getMetadataMap().get("contentText")
				+"</fieldset>"
				+ "</div>");
		return sb.toString();
		
	}
	/**
	 * 拼接大事辑览前台div
	 */
	private String entryTabsDiv(Entry entry) {
		StringBuffer sb = new StringBuffer();
		sb.append("<div>"
				+ "<fieldset><legend>基本信息<a href='javascript:;' onclick='togglePortlet(this)'><i class='fa fa-angle-up'></i></a></legend>"
				+ "<center>"
				+ "<table border='1' width='100%' align='center' style='text-align:center;'>"
				+ "<tr>"
				+ "<td>"
				+ "<span>标题:</span>"
				+ "</td>"
				+"<td>");
		if(entry.getMetadataMap().get("title")!=null){
			sb.append("<input class=\"form-control\" readonly='readonly' value="+entry.getMetadataMap().get("title")+">");
		}else{
			sb.append("<input class=\"form-control\" readonly='readonly' value=''>");
		}
		sb.append("</td>"
				+ "</tr>"
				+ "<tr>"
				+ "<td>"
				+ "<span>期刊名称:</span>"
				+ "</td>"
				+"<td>");
		if(entry.getMetadataMap().get("magazine")!=null){
			sb.append("<input class=\"form-control\" readonly='readonly' value="+entry.getMetadataMap().get("magazine")+">");
		}else{
			sb.append("<input class=\"form-control\" readonly='readonly' value=''>");
		}
		sb.append("</td>"
				+ "<td>"
				+ "<span>期刊分类:</span>"
				+ "</td>"
				+"<td>");
		if(entry.getMetadataMap().get("tmJournalClass")!=null){
			sb.append("<input class=\"form-control\" readonly='readonly' "
					+ "value="+entry.getMetadataMap().get("tmJournalClass")+">");
		}else{
			sb.append("<input class=\"form-control\" readonly='readonly' value=''>");
		}
		sb.append("</td>"
				+ "</tr>"
				+ "</table>"
				+ "</center>"
				+"</fieldset>"
				+ "</div>"
				+ "<div class='form-group' style='margin-top: 30px;'>"
				+ "<fieldset><legend>正文</legend>"+entry.getMetadataMap().get("tmevent")
				+"</fieldset>"
				+ "</div>");
		return sb.toString();
	}
	public String parseXML(File file){
		String contentText = "";
		try {
			SAXReader reader = new SAXReader();
			FileInputStream inputStream = new FileInputStream(file);
			Document document = reader.read(inputStream);
			Element element = document.getRootElement();
			List<Element> result = element.elements();
			for (Element elementResult : result) {
				if(elementResult.getName().toLowerCase().equals("content")){
					contentText = elementResult.getText().replaceAll("\n", "</p>\n<p>").replaceAll("　", "");
					contentText = "<p>"+contentText+"</p>";
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return contentText;
	}
	public String metaDataUtil(String publishType){
		HttpClientUtil http = new HttpClientUtil();
		Gson gson = new Gson();
		String result = http.executeGet(METADATA_PATH+"?publishType="+publishType);
		CustomMetaDataList customMetaDataLists = gson.fromJson(result, CustomMetaDataList.class);
		List<CustomMetaData> metaDatas = customMetaDataLists.getCustomMetaDatas();
		StringBuffer sb = new StringBuffer();
		for (CustomMetaData customMetaData : metaDatas) {
			System.out.println(customMetaData.getNameCN());
			if(customMetaData.getNameCN().equals("通用元数据")){
				List<MetadataDefinition> metadataDefinitionLists = customMetaData.getCustomPropertys();
				for (MetadataDefinition metadataDefinition : metadataDefinitionLists) {
					appendStringDiv(metadataDefinition,sb);
				}
			}
			
		}
		System.out.println(customMetaDataLists.getCustomMetaDatas().size());
		
		return "";
	}
	private void appendStringDiv(MetadataDefinition metadataDefinition,StringBuffer sb) {
		
	}
}

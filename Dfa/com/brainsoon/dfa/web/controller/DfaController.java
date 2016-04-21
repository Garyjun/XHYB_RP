package com.brainsoon.dfa.web.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.brainsoon.appframe.action.BaseAction;
import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.util.dofile.util.WebAppUtils;
import com.brainsoon.dfa.util.FileUtil;
import com.brainsoon.dfa.util.WordsUtil;
import com.brainsoon.resource.service.IBaseSemanticSerivce;
import com.brainsoon.semantic.ontology.model.Asset;
import com.brainsoon.semantic.ontology.model.File;

@Controller
public class DfaController extends BaseAction {
	/** 默认命名空间 **/
	private static final String baseUrl = "/DFA/";
	private String[] fileType = { "pdf", "txt", "ncx", "doc", "docx", "xml","epub" };
	@Autowired
	private IBaseSemanticSerivce baseSemanticSerivce;

	@RequestMapping(baseUrl + "testFile")
	public @ResponseBody String testFile(HttpServletRequest request,HttpServletResponse response) {
		JSONObject result = new JSONObject();
		JSONArray array = new JSONArray();
		String filePath = WebAppUtils.getWebAppBaseFileDirFR()
				+ request.getParameter("path");
		filePath = filePath.replaceAll("\\\\", "/");
		String level = request.getParameter("level");
		String fileName = request.getParameter("fileName");
		 try {
			 fileName=  URLDecoder.decode(fileName,"UTF-8");
			 logger.info("文件中含有敏感词=====----------------------------====="+fileName+"========---------------------=======");
		 	 }catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		String name = filePath.substring(filePath.lastIndexOf(".") + 1,
				filePath.length());
		logger.info("===============filePath=================" + filePath);
		logger.info("===============name=================" + name);
		try {
			//检索文件的扩展名是否在符合要求的扩展名中包含
			if (Arrays.asList(fileType).contains(name)) {
				Set<String> pathSet = new HashSet<String>();
				pathSet.add(filePath);
				for (String path : pathSet) {
					Map<Integer, String[]> checkMaps = WordsUtil
							.checkWordsByPage(path, level.split(","));

					// 排序处理
					Map<Integer, String[]> shortCheckMaps = new TreeMap<Integer, String[]>();
					if(checkMaps!=null){
						logger.info("==============checkMaps=========" + checkMaps.size());
						shortCheckMaps.putAll(checkMaps);
					}
					boolean hasKeyWord = false;
					for (Integer pageKey : shortCheckMaps.keySet()) {
						logger.info("==============shortCheckMaps.get(pageKey)=========" + shortCheckMaps.get(pageKey));
						logger.info("==============shortCheckMaps.get(pageKey).[0]=========" + shortCheckMaps.get(pageKey)[0]);
						if (shortCheckMaps.get(pageKey) != null
								&& shortCheckMaps.get(pageKey).length > 0
								&& !StringUtils.isBlank(shortCheckMaps
										.get(pageKey)[0])) {
							logger.info("==============hasKeyWord=========" + hasKeyWord);
							if (!hasKeyWord) {
								hasKeyWord = true;
								
								//获取含有敏感词的文件名称
								logger.info("文件中含有敏感词=========="+fileName+"===============");
								logger.info(path);
								result.put("head", "资源文件[" + fileName
										+ "]中存在敏感词：");
							}
							fileName ="";
							String item = "";
							if (path.endsWith(".pdf")) {
								item += "第" + pageKey + "页:";
							}
							item += FileUtil.formatTestResult(checkMaps
									.get(pageKey));
							array.add(item);
						}
					}
					if(array.isEmpty()){
						result.put("head", "资源文件[" + fileName
								+ "]中不存在敏感词：");	
						result.put("pdf", array);
					}else{
						result.put("pdf", array);
					}
				}
			} else {
				result.put("error", "格式不支持");
			}
		} catch (Exception e) {
			result.put("error", "过滤敏感词出错");
		}
		return result.toString();
	}
}

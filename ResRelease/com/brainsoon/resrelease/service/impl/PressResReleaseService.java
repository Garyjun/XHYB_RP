package com.brainsoon.resrelease.service.impl;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import magick.MagickException;
import net.sf.json.JSONObject;

import com.brainsoon.appframe.util.StringUtil;
import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.common.util.dofile.conver.ConverUtils;
import com.brainsoon.common.util.dofile.image.ImageUtils;
import com.brainsoon.common.util.dofile.util.ConstantsDef;
import com.brainsoon.common.util.dofile.util.DoFileUtils;
import com.brainsoon.common.util.dofile.util.PropertiesReader;
import com.brainsoon.resource.service.IBaseSemanticSerivce;
import com.brainsoon.resrelease.po.ParamsTempEntity;
import com.brainsoon.resrelease.po.ResOrder;
import com.brainsoon.resrelease.po.ResReleaseDetail;
import com.brainsoon.resrelease.service.IPressResReleaseService;
import com.brainsoon.resrelease.support.FileUtil;
import com.brainsoon.resrelease.support.ResReleaseConstant;
import com.brainsoon.resrelease.support.SysParamsTemplateConstants.ImgFormat;
import com.brainsoon.resrelease.support.SysParamsTemplateConstants.WmFont;
import com.brainsoon.resrelease.support.XmlUtil;
import com.brainsoon.semantic.ontology.model.Asset;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.semantic.ontology.model.CommonMetaData;
import com.brainsoon.semantic.ontology.model.Organization;
import com.brainsoon.semantic.ontology.model.OrganizationItem;
import com.google.gson.Gson;

/**
 * @ClassName: PressResReleaseService
 * @author xiehewei
 * @date 2014年9月17日 下午4:56:44
 *
 */
public class PressResReleaseService extends BaseService implements
		IPressResReleaseService {

	@Override
	public void doProcess(String processTaskId) {
		
	}
	
	@Override
	public void processResource(ResOrder resOrder, Long releaseId, String fileRoot, ResReleaseDetail resRelDetail, String header,
			ParamsTempEntity paramsTempEntity) throws NumberFormatException, URISyntaxException, IOException, InterruptedException {
		HttpClientUtil http = new HttpClientUtil();
		String pathContent = "";
		//获得资源的相关信息返回字符串
		String paramJson = resOrder.getTemplate().getParamsJson();
		JSONObject paramObj = JSONObject.fromObject(paramJson);
		Object flags = paramObj.get("book");
		String bookRes = null;
		Asset res = null;
		String eduRes = null;
		Gson gson = new Gson();
		String resId = resRelDetail.getResId();
		// 教育资源
		if (flags == null) {
			eduRes = http.executeGet(WebappConfigUtil.getParameter("RES_DETAIL_URL")+ "?id="+resId);
			JSONObject json = JSONObject.fromObject(eduRes);
			String type = (String) json.get("type");
			if("CA".equals(type)){
				Ca eduCa = gson.fromJson(eduRes, Ca.class);
			}else{
				Asset eduAsset = gson.fromJson(eduRes, Asset.class);
				IBaseSemanticSerivce baseSemanticSerivce = null;
				try {
					baseSemanticSerivce = (IBaseSemanticSerivce) BeanFactoryUtil.getBean("baseSemanticSerivce");
				res = baseSemanticSerivce.getResourceById(resRelDetail.getResId());
//				res.getFiles()
				List<com.brainsoon.semantic.ontology.model.File> files = null;
				for(com.brainsoon.semantic.ontology.model.File file:files){
					String eduSrcPath = fileRoot.replace("/", "\\") + file.getPath().replace("/", "\\");
					File srcEduFile = new File(eduSrcPath);
					if(srcEduFile.exists() && !srcEduFile.isDirectory()){
						String title = eduAsset.getCommonMetaData().getTitle();
						pathContent = FileUtil.makeDir(header, title+"_"+resId.replace("-", "-").replace(":", "："));
						try {
							org.apache.commons.io.FileUtils.copyFileToDirectory(srcEduFile, new File(pathContent));
						} catch (IOException e) {
							resRelDetail.setStatus(ResReleaseConstant.PublishingStatus.FAIL_PUBLISH);
							resRelDetail.setRemark("资源文件不存在！");
							update(resRelDetail);
							e.printStackTrace();
						}
						int pos = eduSrcPath.lastIndexOf("\\");
						String proFilePath = pathContent+File.separator+eduSrcPath.substring(pos+1, eduSrcPath.length());
//						try {
//							processFile(null, proFilePath.replace("/", File.separator), paramsTempEntity);
//						} catch (NumberFormatException e) {
//							resRelDetail.setRemark("发布模板数据格式错误！");
//							update(resRelDetail);
//							e.printStackTrace();
//						} catch (IOException e) {
//							resRelDetail.setRemark("资源文件不存在！");
//							update(resRelDetail);
//							e.printStackTrace();
//						} catch (MagickException e) {
//							resRelDetail.setRemark("资源文件加工出错！");
//							update(resRelDetail);
//							e.printStackTrace();
//						} catch (URISyntaxException e) {
//							resRelDetail.setRemark("资源文件路径存在非法字符，无法获得资源文件！");
//							update(resRelDetail);
//							e.printStackTrace();
//						} catch (InterruptedException e) {
//							resRelDetail.setRemark("资源文件加工出错！");
//							update(resRelDetail);
//							e.printStackTrace();
//						} catch (Exception e) {
//							resRelDetail.setRemark(e.getMessage());
//							update(resRelDetail);
//						} finally{
//							resRelDetail.setStatus(ResReleaseConstant.PublishingStatus.FAIL_PUBLISH);
//							resRelDetail.setProcessTimes(resRelDetail.getProcessTimes()+1);
//							update(resRelDetail);
//						}
					}
				}
				resRelDetail.setStatus(ResReleaseConstant.PublishingStatus.SUCCESS_PUBLISH);
				update(resRelDetail);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			}
		} else {
			// 图书资源都是ca
			bookRes = http.executeGet(WebappConfigUtil.getParameter("PUBLISH_DETAIL_URL")+ "?id="+ resRelDetail.getResId());
			Ca ca = gson.fromJson(bookRes, Ca.class);
			//资源名称
			String title = ca.getCommonMetaData().getTitle();
			
		}
	}
	
	
	public String getContent(String[] pathArr, String[] sysDirs){
		String str = "";
		for(String sysDir :sysDirs){
			if(str!=""){
				break;
			}
			for(String arr: pathArr){
				if(sysDir.equals(arr)){
					str = sysDir;
					break;
				}
				
			}
		}
		return str;
	}
	
	/**
	 * 结果为 0 ：视频资源
	 * 结果为 1 ：图片资源
	 * 结果为 2 ：文本资源
	 * @param suffix  文件后缀名
 	 * @param typeList 后缀名list集合
	 * @return
	 */
	public int getProcessType(String suffix, List<String[]> typeList){
		int count = 0;
		int flag = -1;
		for(String[] types:typeList){
			for(String type :types){
				if(type.equals(suffix.trim())){
					flag = count;
					break;
				}
			}
			count++;
		}
		return flag;
	}
}

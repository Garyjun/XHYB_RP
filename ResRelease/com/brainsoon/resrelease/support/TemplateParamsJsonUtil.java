package com.brainsoon.resrelease.support;

import org.apache.commons.lang.StringUtils;

import net.sf.json.JSONObject;

import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.resrelease.po.ParamsTempEntity;
import com.brainsoon.resrelease.po.ProdParamsTemplate;

public class TemplateParamsJsonUtil {
	
	public static ParamsTempEntity Json2TemplateEntity (ProdParamsTemplate ppt){
		ParamsTempEntity pt = new ParamsTempEntity();
		
		if(!ppt.getParamsJson().isEmpty() && !"{}".equals(ppt.getParamsJson())){
			JSONObject json = JSONObject.fromObject(ppt.getParamsJson());
		
				JSONObject object = new JSONObject();
				object = json.getJSONObject(ppt.getType());
				pt.setResourceType(ppt.getType());
		
				if (object.has("fileType")) {
					String fileType = object.getString("fileType");
					pt.setFileType(fileType);
				}
				if (object.has("waterMarkFileType")) {
					String waterMarkFileType = object.getString("waterMarkFileType");
					pt.setWaterMarkFileType(waterMarkFileType);
				}		
				if (object.has("hasFileList")) {
					String hasFileList = object.getString("hasFileList");
					pt.setHasFileList(hasFileList);
				}
				if (object.has("waterMarkType")) {
					String waterMarkType = object.getString("waterMarkType");
					pt.setWaterMarkType(waterMarkType);
				}
				
				if (object.has("height")) {
					String imgHeight = object.getString("height");
					pt.setImgHeight(imgHeight);
				}
				if (object.has("width")) {
					String imgWidth = object.getString("width");
					pt.setImgWidth(imgWidth);
				}
				if (object.has("imageType")) {
					String imgType = object.getString("imageType");
					pt.setImgType(imgType);
				}
				if (object.has("videoType")) {
					String videoType = object.getString("videoType");
					pt.setVideoType(videoType);
				}
				if (object.has("textType")) {
					String textType = object.getString("textType");
					pt.setTextType(textType);
				}
				if (object.has("waterMarkOpacity")) {
					String waterMarkOpacity = object.getString("waterMarkOpacity");
					pt.setWaterMarkOpacity(waterMarkOpacity);
				}
				if (object.has("imgWaterMarkPos")) {
					String imgWaterMarkPos = object.getString("imgWaterMarkPos");
					pt.setImgWaterMarkPos(imgWaterMarkPos);
				}
				if (object.has("videoWaterMarkPos")) {
					String videoWaterMarkPos = object.getString("videoWaterMarkPos");
					pt.setVideoWaterMarkPos(videoWaterMarkPos);
				}
				if (object.has("wordWaterMarkPos")) {
					String wordWaterMarkPos = object.getString("wordWaterMarkPos");
					pt.setWordWaterMarkPos(wordWaterMarkPos);
				}
				if(object.has("waterMarkText")){
					String waterMarkText = object.getString("waterMarkText");
					pt.setWaterMarkText(waterMarkText);
					if (object.has("waterMarkColor")) {
						String waterMarkColor = object.getString("waterMarkColor");
						pt.setWaterMarkColor(waterMarkColor);
					}
					if (object.has("waterMarkTextSize")) {
						String waterMarkTextSize = object.getString("waterMarkTextSize");
						pt.setWaterMarkTextSize(waterMarkTextSize);
					}
					if (object.has("waterMarkTextFont")) {
						String waterMarkTextFont = object.getString("waterMarkTextFont");
						pt.setWaterMarkTextFont(waterMarkTextFont);
					}
					if (object.has("waterMarkTextBold")) {
						String waterMarkTextBold = object.getString("waterMarkTextBold");
						pt.setWaterMarkTextBold(waterMarkTextBold);
					}
					
					if (object.has("waterMarkURL")) {
						String imgWaterMarkURL = object.getString("waterMarkURL");
						pt.setImgWaterMarkURL(imgWaterMarkURL);
					}
					
				}else{
					if (object.has("waterMarkURL")) {
						String imgWaterMarkURL = object.getString("waterMarkURL");
						pt.setImgWaterMarkURL(imgWaterMarkURL);
					}
				}
		}
		

		return pt;
	}
	
	public static JSONObject TemplateEntity2Json(ParamsTempEntity pt){
		JSONObject json = new JSONObject();
		
		//判断水印类型 若为空  则不存此数据
		if(!StringUtils.isBlank(pt.getWaterMarkFileType())){
			
			//String resourceType = pt.getResourceType();
			//获取资源类型
			String code = pt.getMetaDatasCode();
			String codes[] = code.split("!");
			String resourceType = "";
			for (String strs : codes) {
				resourceType +=strs.substring(2, strs.indexOf("\":"))+",";
			}
			if(resourceType.length()>1){
				resourceType = resourceType.substring(0, resourceType.length()-1);
			}
			JSONObject object = new JSONObject();
			
			//加水印文件类型
			String waterMarkFileType = pt.getWaterMarkFileType();
			if (waterMarkFileType.endsWith(",")) {//如果后面有，去掉 如：video,
				waterMarkFileType = waterMarkFileType.substring(0, waterMarkFileType.length()-1);
			}
			object.put("waterMarkFileType", waterMarkFileType);
			
			
			//加水印文件类型为图片
			if (waterMarkFileType.contains("image")) {
				
				//加水印文件类型为图片的参数
				if (!StringUtils.isBlank(pt.getImgHeight())) {
					object.put("height", pt.getImgHeight());
				}
				if (!StringUtils.isBlank(pt.getImgWidth())) {
					object.put("width", pt.getImgWidth());
				}
				if (!StringUtils.isBlank(pt.getImgType())) {
					object.put("imageType", pt.getImgType());
				}
				if (!StringUtils.isBlank(pt.getImgWaterMarkPos())) {
					object.put("imgWaterMarkPos", pt.getImgWaterMarkPos());
				}
				
				
				//水印类型为图片的参数
				if (pt.getWaterMarkType().contains("图片")) {
					
					object.put("waterMarkType", pt.getWaterMarkType());
					
					if (!StringUtils.isBlank(pt.getWaterMarkOpacity())) {
						object.put("waterMarkOpacity", pt.getWaterMarkOpacity());
					}
					
					if (!StringUtils.isBlank(pt.getImgWaterMarkURL())) {
						object.put("waterMarkURL", pt.getImgWaterMarkURL());
					}
				
				//水印类型为文字的参数
				}else if (pt.getWaterMarkType().contains("文字")){
					object.put("waterMarkType", pt.getWaterMarkType());
					
					if (!StringUtils.isBlank(pt.getWaterMarkOpacity())) {
						object.put("waterMarkOpacity", pt.getWaterMarkOpacity());
					}
					if (!StringUtils.isBlank(pt.getWaterMarkTextBold())) {
						object.put("waterMarkTextBold", pt.getWaterMarkTextBold());
					}
					if (!StringUtils.isBlank(pt.getWaterMarkText())) {
						object.put("waterMarkText", pt.getWaterMarkText());
					}
					if (!StringUtils.isBlank(pt.getWaterMarkColor())) {
						object.put("waterMarkColor", pt.getWaterMarkColor());
					}
					if (!StringUtils.isBlank(pt.getWaterMarkTextSize())) {
						object.put("waterMarkTextSize", pt.getWaterMarkTextSize());
					}
					if (!StringUtils.isBlank(pt.getWaterMarkTextFont())) {
						object.put("waterMarkTextFont", pt.getWaterMarkTextFont());
					}
				}
			}
			
			
			//加水印文件类型为视频
			if (waterMarkFileType.contains("video")){
				//加水印文件类型为视频的参数
				if (!StringUtils.isBlank(pt.getVideoType())) {
					object.put("videoType", pt.getVideoType());
				}
				if (!StringUtils.isBlank(pt.getVideoWaterMarkPos())) {
					object.put("videoWaterMarkPos", pt.getVideoWaterMarkPos());
				}
				
				//水印类型为图片的参数
				if (pt.getWaterMarkType().contains("图片")) {
					
					object.put("waterMarkType", pt.getWaterMarkType());
					
					if (!StringUtils.isBlank(pt.getWaterMarkOpacity())) {
						object.put("waterMarkOpacity", pt.getWaterMarkOpacity());
					}
					
					if (!StringUtils.isBlank(pt.getImgWaterMarkURL())) {
						object.put("waterMarkURL", pt.getImgWaterMarkURL());
					}
				
				//水印类型为文字的参数
				}else if (pt.getWaterMarkType().contains("文字")){
					object.put("waterMarkType", pt.getWaterMarkType());
					
					if (!StringUtils.isBlank(pt.getWaterMarkOpacity())) {
						object.put("waterMarkOpacity", pt.getWaterMarkOpacity());
					}
					if (!StringUtils.isBlank(pt.getWaterMarkTextBold())) {
						object.put("waterMarkTextBold", pt.getWaterMarkTextBold());
					}
					if (!StringUtils.isBlank(pt.getWaterMarkText())) {
						object.put("waterMarkText", pt.getWaterMarkText());
					}
					if (!StringUtils.isBlank(pt.getWaterMarkColor())) {
						object.put("waterMarkColor", pt.getWaterMarkColor());
					}
					if (!StringUtils.isBlank(pt.getWaterMarkTextSize())) {
						object.put("waterMarkTextSize", pt.getWaterMarkTextSize());
					}
					if (!StringUtils.isBlank(pt.getWaterMarkTextFont())) {
						object.put("waterMarkTextFont", pt.getWaterMarkTextFont());
					}
				}
			}
			
			
			//其它数据
			if (!StringUtils.isBlank(pt.getUrl())) {
				object.put("url", pt.getUrl());
			}
			if (!StringUtils.isBlank(pt.getFileType())) {
				object.put("fileType", pt.getFileType());
			}
			if (!StringUtils.isBlank(pt.getHasFileList())) {
				object.put("hasFileList", pt.getHasFileList());
			}
			if (!StringUtils.isBlank(pt.getTextType())) {
				object.put("textType", pt.getTextType());
			}
			
			if(!StringUtils.isBlank(pt.getMetaInfo())){
				object.put("metaInfo", pt.getMetaInfo());
			}
		
			//将数据转化为json
			if (!StringUtils.isBlank(pt.getWaterMarkFileType())) {
				json.put(resourceType, object);
			}
		}
		return json;
	}
}

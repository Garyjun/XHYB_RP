package com.brainsoon.resrelease.po;

import org.apache.commons.lang.StringUtils;

import com.brainsoon.common.support.GlobalDataCacheMap;
import com.brainsoon.resrelease.support.SysParamsTemplateConstants;


public class ParamsTempEntity {
	private String id;
	//模板名称
	private String name;
	//启用状态
	private String status;
	private String statusDesc;
	//备注
	private String remark;

	//集成方
	private String supplier;
	//元数据文件
	private String metaInfo;
	
	//资源类型
	private String resourceType;
	private String resourceTypeDesc;
	//图书目录
	private String fileType;
	//是否生成文件清单
	private String hasFileList;
	//文件类型
	private String waterMarkFileType;
	private String waterMarkFileTypeDesc;
	
	//元数据项
	private String metaDatasCode;
	
	private String url;
	
	private String publishType;
	private String publishTypeDesc;
	
	//图片限高
	private String imgHeight;
	//图片限宽
	private String imgWidth;
	//图片格式
	private String imgType;
	private String imgTypeDesc;

	//视频格式
	private String videoType;
	private String videoTypeDesc;
	//文本格式
	private String textType;
	private String textTypeDesc;
	//水印类型
	private String waterMarkType;
	//水印文字
	private String waterMarkText;
	//图片水印位置
	private String imgWaterMarkPos;
	private String imgWaterMarkPosDesc;
	//视频水印位置
	private String videoWaterMarkPos;
	private String videoWaterMarkPosDesc;
	//文字水印位置
	private String wordWaterMarkPos;
	private String wordWaterMarkPosDesc;
	//字体颜色
	private String waterMarkColor;
	//文字大小
	private String waterMarkTextSize;
	private String waterMarkTextSizeDesc;
	//文字字体
	private String waterMarkTextFont;
	private String waterMarkTextFontDesc;
	//水印透明度
	private String waterMarkOpacity;
	private String waterMarkOpacityDesc;
	//文字加粗
	private String waterMarkTextBold;
	private String waterMarkTextBoldDesc;
	//图片url
	private String imgWaterMarkURL;
	private String posttype;
	private String posttypeDesc;
	public String getId() {
		return id;
	}
	public String getWaterMarkTextBoldDesc() {
		return SysParamsTemplateConstants.JudgeWhether.getValueByKey(waterMarkTextBold);
	}
	public void setWaterMarkTextBoldDesc(String waterMarkTextBoldDesc) {
		this.waterMarkTextBoldDesc = waterMarkTextBoldDesc;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getStatusDesc() {
		return SysParamsTemplateConstants.ParamsTempStatus.getValueByKey(Integer.parseInt(status));
	}
	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getResourceType() {
		return resourceType;
	}
	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}
	public String getResourceTypeDesc() {
		if(resourceType.matches("[a-zA-Z]+$")){
			return SysParamsTemplateConstants.ResourceType.getValueByKey(resourceType);
		}
		return resourceType;
	}
	public void setResourceTypeDesc(String resourceTypeDesc) {
		this.resourceTypeDesc = resourceTypeDesc;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public String getHasFileList() {
		return hasFileList;
	}
	public void setHasFileList(String hasFileList) {
		this.hasFileList = hasFileList;
	}
	public String getWaterMarkFileType() {
		return waterMarkFileType;
	}
	public void setWaterMarkFileType(String waterMarkFileType) {
		this.waterMarkFileType = waterMarkFileType;
	}
	public String getWaterMarkFileTypeDesc() {
		String fileType = "";
		if(StringUtils.isNotEmpty(waterMarkFileType)){
			if(waterMarkFileType.contains("image"))
				fileType += "图片,";
			if(waterMarkFileType.contains("video"))
				fileType += "视频,";
			if(waterMarkFileType.contains("text"))
				fileType += "文本(pdf)";	
		}
		if((!"".equals(fileType))&&fileType.lastIndexOf(",") == fileType.length()-1)
			return fileType.substring(0, fileType.length()-1);
		else
			return fileType;
	}
	public void setWaterMarkFileTypeDesc() {
		String fileType = "";
		if(StringUtils.isNotEmpty(waterMarkFileType)){
			if(waterMarkFileType.contains("image"))
				fileType += "图片,";
			if(waterMarkFileType.contains("video"))
				fileType += "视频,";
			if(waterMarkFileType.contains("text"))
				fileType += "文本(pdf)";	
		}
		if((!"".equals(fileType))&&fileType.lastIndexOf(",") == fileType.length()-1)
			this.waterMarkFileTypeDesc = fileType.substring(0, fileType.length()-1);
		else
			this.waterMarkFileTypeDesc = fileType;
	}
	public String getImgHeight() {
		return imgHeight;
	}
	public void setImgHeight(String imgHeight) {
		this.imgHeight = imgHeight;
	}
	public String getImgWidth() {
		return imgWidth;
	}
	public void setImgWidth(String imgWidth) {
		this.imgWidth = imgWidth;
	}
	public String getImgType() {
		return imgType;
	}
	public void setImgType(String imgType) {
		this.imgType = imgType;
	}
	public String getImgTypeDesc() {
		return SysParamsTemplateConstants.ImgFormat.getValueByKey(imgType);
	}
	public void setImgTypeDesc(String imgTypeDesc) {
		this.imgTypeDesc = imgTypeDesc;
	}
	public String getWaterMarkType() {
		return waterMarkType;
	}
	public void setWaterMarkType(String waterMarkType) {
		this.waterMarkType = waterMarkType;
	}
	public String getWaterMarkText() {
		return waterMarkText;
	}
	public void setWaterMarkText(String waterMarkText) {
		this.waterMarkText = waterMarkText;
	}
//	public String getWaterMarkPos() {
//		return waterMarkPos;
//	}
//	public void setWaterMarkPos(String waterMarkPos) {
//		this.waterMarkPos = waterMarkPos;
//	}
//	public String getWaterMarkPosDesc() {
//		return SysParamsTemplateConstants.WmTxtPosition.getValueByKey(waterMarkPos);
//	}
//	public void setWaterMarkPosDesc(String waterMarkPosDesc) {
//		this.waterMarkPosDesc = waterMarkPosDesc;
//	}
	public String getImgWaterMarkPos() {
		return imgWaterMarkPos;
	}
	public void setImgWaterMarkPos(String imgWaterMarkPos) {
		this.imgWaterMarkPos = imgWaterMarkPos;
	}
	public String getImgWaterMarkPosDesc() {
		return SysParamsTemplateConstants.WmPosition.getValueByKey(imgWaterMarkPos);
	}
	public void setImgWaterMarkPosDesc(String imgWaterMarkPosDesc) {
		this.imgWaterMarkPosDesc = imgWaterMarkPosDesc;
	}
	public String getVideoWaterMarkPos() {
		return videoWaterMarkPos;
	}
	public void setVideoWaterMarkPos(String videoWaterMarkPos) {
		this.videoWaterMarkPos = videoWaterMarkPos;
	}
	public String getVideoWaterMarkPosDesc() {
		return SysParamsTemplateConstants.WmPosition.getValueByKey(videoWaterMarkPos);
	}
	public void setVideoWaterMarkPosDesc(String videoWaterMarkPosDesc) {
		this.videoWaterMarkPosDesc = videoWaterMarkPosDesc;
	}
	public String getWordWaterMarkPos() {
		return wordWaterMarkPos;
	}
	public void setWordWaterMarkPos(String wordWaterMarkPos) {
		this.wordWaterMarkPos = wordWaterMarkPos;
	}
	public String getWordWaterMarkPosDesc() {
		return SysParamsTemplateConstants.WmPosition.getValueByKey(wordWaterMarkPos);
	}
	public void setWordWaterMarkPosDesc(String wordWaterMarkPosDesc) {
		this.wordWaterMarkPosDesc = wordWaterMarkPosDesc;
	}
	
	public String getWaterMarkColor() {
		return waterMarkColor;
	}
	public void setWaterMarkColor(String waterMarkColor) {
		this.waterMarkColor = waterMarkColor;
	}
	public String getWaterMarkTextSize() {
		return waterMarkTextSize;
	}
	public void setWaterMarkTextSize(String waterMarkTextSize) {
		this.waterMarkTextSize = waterMarkTextSize;
	}
	public String getWaterMarkTextSizeDesc() {
		return SysParamsTemplateConstants.TextSize.getValueByKey(waterMarkTextSize);
	}
	public void setWaterMarkTextSizeDesc(String waterMarkTextSizeDesc) {
		this.waterMarkTextSizeDesc = waterMarkTextSizeDesc;
	}
	public String getWaterMarkTextFont() {
		return waterMarkTextFont;
	}
	public void setWaterMarkTextFont(String waterMarkTextFont) {
		this.waterMarkTextFont = waterMarkTextFont;
	}
	public String getWaterMarkTextFontDesc() {
		return SysParamsTemplateConstants.WmFont.getValueByKey(waterMarkTextFont);
	}
	public void setWaterMarkTextFontDesc(String waterMarkTextFontDesc) {
		this.waterMarkTextFontDesc = waterMarkTextFontDesc;
	}
	public String getWaterMarkOpacity() {
		return waterMarkOpacity;
	}
	public void setWaterMarkOpacity(String waterMarkOpacity) {
		this.waterMarkOpacity = waterMarkOpacity;
	}
	public String getWaterMarkOpacityDesc() {
		return SysParamsTemplateConstants.WmAlpha.getValueByKey(waterMarkOpacity);
	}
	public void setWaterMarkOpacityDesc(String waterMarkOpacityDesc) {
		this.waterMarkOpacityDesc = waterMarkOpacityDesc;
	}
	public String getWaterMarkTextBold() {
		return waterMarkTextBold;
	}
	public void setWaterMarkTextBold(String waterMarkTextBold) {
		this.waterMarkTextBold = waterMarkTextBold;
	}
	public String getImgWaterMarkURL() {
		return imgWaterMarkURL;
	}
	public void setImgWaterMarkURL(String imgWaterMarkURL) {
		this.imgWaterMarkURL = imgWaterMarkURL;
	}
	public String getVideoType() {
		return videoType;
	}
	public void setVideoType(String videoType) {
		this.videoType = videoType;
	}
	public String getVideoTypeDesc() {
		return SysParamsTemplateConstants.VideoFormat.getValueByKey(videoType);
	}
	public void setVideoTypeDesc(String videoTypeDesc) {
		this.videoTypeDesc = videoTypeDesc;
	}
	public String getTextType() {
		return textType;
	}
	public void setTextType(String textType) {
		this.textType = textType;
	}
	public String getTextTypeDesc() {
		return SysParamsTemplateConstants.TextFormat.getValueByKey(textType);
	}
	public void setTextTypeDesc(String textTypeDesc) {
		this.textTypeDesc = textTypeDesc;
	}
	public String getSupplier() {
		return supplier;
	}
	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}
	public String getMetaInfo() {
		return metaInfo;
	}
	public void setMetaInfo(String metaInfo) {
		this.metaInfo = metaInfo;
	}
	public String getMetaDatasCode() {
		return metaDatasCode;
	}
	public void setMetaDatasCode(String metaDatasCode) {
		this.metaDatasCode = metaDatasCode;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getPublishType() {
		return publishType;
	}
	public void setPublishType(String publishType) {
		this.publishType = publishType;
	}
	public String getPublishTypeDesc() {
		return SysParamsTemplateConstants.PublishType.getValueByKey(publishType) ;
	}
	public String getPosttype() {
		return posttype;
	}
	public void setPosttype(String posttype) {
		this.posttype = posttype;
	}
	public String getPosttypeDesc() {
		String map =GlobalDataCacheMap.getNameValueWithNameByKeyAndChildKey("XqdZtk", getPosttype());
		return map;
	}
	
	
}

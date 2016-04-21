package com.brainsoon.resrelease.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.common.util.dofile.util.ConstantsDef;
import com.brainsoon.common.util.dofile.util.WebAppUtils;
import com.brainsoon.resrelease.po.ParamsTempEntity;
import com.brainsoon.resrelease.po.ProdParamsTemplate;
import com.brainsoon.resrelease.service.IPublishTempService;
import com.brainsoon.resrelease.support.TemplateParamsJsonUtil;
import com.brainsoon.system.model.MetadataDefinitionGroup;
import com.brainsoon.system.model.User;
@Service
public class PublishTempService extends BaseService implements
		IPublishTempService {
	public final static String TEMPLATEFILE_TEMP = WebAppUtils.getWebRootBaseDir(ConstantsDef.sysUpLoadFile);

	@Override
	public ParamsTempEntity convertEntity(ProdParamsTemplate prodParamsTemplate) {
		ParamsTempEntity pte = TemplateParamsJsonUtil.Json2TemplateEntity(prodParamsTemplate);
		pte.setSupplier(prodParamsTemplate.getSupplier());
		pte.setRemark(prodParamsTemplate.getRemark());
		pte.setId(prodParamsTemplate.getId()+"");
		pte.setName(prodParamsTemplate.getName());
		pte.setResourceType(prodParamsTemplate.getType());
		pte.setStatus(prodParamsTemplate.getStatus());
		pte.setMetaInfo(prodParamsTemplate.getMetaInfo());
		pte.setMetaDatasCode(prodParamsTemplate.getMetaDatasCode());
		pte.setUrl(prodParamsTemplate.getUrl());
		pte.setPublishType(prodParamsTemplate.getPublishType());
		pte.setPosttype(prodParamsTemplate.getPosttype());
		return pte;
	}

	@Override
	public ProdParamsTemplate entity2ProdTemplate(ParamsTempEntity entity) {
		ProdParamsTemplate ppt = new ProdParamsTemplate();
		if(!StringUtils.isBlank(entity.getId()))
			ppt.setId(Long.parseLong(entity.getId()));
		ppt.setName(entity.getName());
		
		ppt.setStatus(entity.getStatus());
		ppt.setRemark(entity.getRemark());
		String code = entity.getMetaDatasCode();
		String codes[] = code.split("!");
		String codesrestype = "";
		for (String strs : codes) {
			codesrestype +=strs.substring(2, strs.indexOf("\":"))+",";
		}
		if(codesrestype.length()>1){
			codesrestype = codesrestype.substring(0, codesrestype.length()-1);
		}
		ppt.setType(codesrestype);
		ppt.setSupplier(entity.getSupplier());
		ppt.setMetaInfo(entity.getMetaInfo());
		ppt.setPosttype(entity.getPosttype());
		ppt.setMetaDatasCode(entity.getMetaDatasCode());
		String publishType = entity.getPublishType();
		if(StringUtils.isNotEmpty(publishType)){
			if("onLine".equals(publishType)){
				ppt.setUrl(entity.getUrl());
			}else{
				ppt.setUrl(null);
			}
		}
		String metaInfo = entity.getMetaInfo();
		if(StringUtils.isNotEmpty(metaInfo)){
			if(metaInfo.contains("元数据")){
				ppt.setMetaDatasCode(entity.getMetaDatasCode());
			}else{
				ppt.setMetaDatasCode(null);
			}
		}
		ppt.setPublishType(entity.getPublishType());
		ppt.setParamsJson(TemplateParamsJsonUtil.TemplateEntity2Json(entity).toString());
		return ppt;
	}
	

	public void filterParams(ParamsTempEntity entity) {
		/*if(!StringUtils.isBlank(entity.getWaterMarkPos())){
			if(entity.getWaterMarkType().equals("文字")){
				entity.setWaterMarkPos(entity.getWaterMarkPos().split(",")[1]);
			}else{
				entity.setWaterMarkPos(entity.getWaterMarkPos().split(",")[0]);
			}
			
		}*/
		String code = entity.getMetaDatasCode();
		String waterFileType = entity.getWaterMarkFileType();//加水印文件类型  为空则params_json不存数据 
		String url = entity.getUrl();
		String publishType = entity.getPublishType();
		if(StringUtils.isNotBlank(code)){
			String metaInfo = entity.getMetaInfo();
			if(StringUtils.isNotEmpty(metaInfo)&&metaInfo.contains("元数据Excel")){
				entity.setMetaDatasCode(code);
			}
		}
//		if(StringUtils.isNotBlank(url)){
//			entity.setUrl(url);
//		}
		if(StringUtils.isNotBlank(publishType)){
			entity.setPublishType(publishType);
			if("onLine".equals(publishType)){
				String metaInfo = entity.getMetaInfo();
				metaInfo = metaInfo.replace("Excel", "Json");
				entity.setUrl(url);
				entity.setMetaInfo(metaInfo);
			}else{
				entity.setUrl(null);
			}
		}
		if(StringUtils.isNotBlank(waterFileType)){  //image,video,text
			if(waterFileType.contains("image")){
				entity.setImgWaterMarkPos(entity.getImgWaterMarkPos());
			}else{
				entity.setImgWaterMarkPos(null);
			}
			if(waterFileType.contains("video")){
				entity.setVideoWaterMarkPos(entity.getVideoWaterMarkPos());
			}else{
				entity.setVideoWaterMarkPos(null);
			}
			if(waterFileType.contains("text")){
				entity.setWordWaterMarkPos(entity.getWordWaterMarkPos());
			}else{
				entity.setWordWaterMarkPos(null);
			}
		}
		
		if(!StringUtils.isBlank(entity.getWaterMarkOpacity())){
			if(entity.getWaterMarkType().equals("文字")){
				entity.setWaterMarkOpacity(entity.getWaterMarkOpacity().split(",")[1]);
			}else{
				entity.setWaterMarkOpacity(entity.getWaterMarkOpacity().split(",")[0]);
			}
		}
		if (!StringUtils.isBlank(entity.getWaterMarkText())) {
			/*entity.setWaterMarkColor(null);
			entity.setWaterMarkTextFont(null);
			entity.setWaterMarkTextSize(null);
			entity.setWaterMarkTextBold(null);*/
			entity.setWaterMarkColor(entity.getWaterMarkColor());
			entity.setWaterMarkTextFont(entity.getWaterMarkTextFont());
			entity.setWaterMarkTextSize(entity.getWaterMarkTextSize());
			entity.setWaterMarkTextBold(entity.getWaterMarkTextBold());
		} else {
			entity.setImgWaterMarkURL(entity.getImgWaterMarkURL().replace("\\", "/"));
		}
		if(!waterFileType.contains("image"))
			entity.setImgType(null);
		if(!waterFileType.contains("video"))
			entity.setVideoType(null);
		if(!waterFileType.contains("text"))
			entity.setTextType(null);
	}
	
	public void addJsonId(ProdParamsTemplate ppt){
		JSONObject json = JSONObject.fromObject(ppt.getParamsJson());
		json.put("id", ppt.getId().toString());
		ppt.setParamsJson(json.toString());
	}

	@Override
	public String checkTemplateName(String templateName, String templateId) {
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		int platformId = userInfo.getPlatformId();
		String hql = " from ProdParamsTemplate where name='"+templateName+"' and platformId="+platformId;
		List<ProdParamsTemplate> list = getBaseDao().query(hql);
		String flag = "1";
		if(list.size()==0){
			flag = "0";
		}else{
			int count = 0;
			for(ProdParamsTemplate template:list){
				if(!template.getId().toString().equals(templateId)){
					count++;
					break;
				}
			}
			if(count==0){
				flag = "0";
			}else{
				flag = "1";
			}
		}
		return flag;
	}

	/**
	 * 在添加新图片是，将原来的图片删除
	 */
	@Override
	public void delImage(String id) {
		if (StringUtils.isNotEmpty(id)) {
			ProdParamsTemplate prodParamsTemplate = (ProdParamsTemplate) getByPk(ProdParamsTemplate.class, Long.parseLong(id));
			String oldPath = prodParamsTemplate.getUrl();//原图片相对路径
			
			try {
				
				File oldImage = new File(TEMPLATEFILE_TEMP + oldPath);
				oldImage.delete();
				
			} catch (Exception e) {
				logger.info("----------修改发布模版----原图片："+oldPath+"---------------");
				e.printStackTrace();
			}
		}
				
	}
	
	/**
	 * 根据resType返回对应的字段权限字符串
	 * 			    元数据Id   资源ID
	 * 核心元数据		1		0
	 * 文件元数据		2		0
	 * 书目元数据		61		1
	 * 组图元数据		66		36
	 * 基本元数据		67		37
	 * 有声元数据		68		38
	 * 漫画源数据		69		40
	 * 动画元数据		70		41
	 * 基础元数据		71		44
	 * 
	 * @param resTypeId
	 * @return
	 */
	public String getCheckedCoreMetadataByresId(String resTypeId){
		//   1\  获取该登陆用户的字段权限*********该权限在系统管理-》用户管理-》字段权限中配置
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		User user = null;
		String checkedCoreMetadata =null;
		if (userInfo.getUserId() >0) {
			user = (User) getByPk(User.class, userInfo.getUserId());
			checkedCoreMetadata = user.getDataPreJson();
			logger.info("---------获取该登陆名"+user.getLoginName()+",用户名"+user.getUserName()+",字段权限:*" + checkedCoreMetadata + "------------");
		}
		
		String checkData = "";
		String[] checkDatas = checkedCoreMetadata.split("\"id\"");
		for (int i = 0; i < checkDatas.length; i++) {
			//核心元数据 
			if (checkDatas[i].indexOf("\"1\"") !=-1) {
				if ("0".equals(resTypeId)) {
					 return checkDatas[i];
				}
			}
			//文件元数据
			if (checkDatas[i].indexOf("\"2\"") !=-1) {
				if ("0".equals(resTypeId)) {
					return checkDatas[i];
				}
			}
			//书目元数据
			if (checkDatas[i].indexOf("\"61\"") !=-1) {
				if ("1".equals(resTypeId)) {
					return checkDatas[i];
				}
			}
			//组图元数据
			if (checkDatas[i].indexOf("\"66\"") !=-1) {
				if ("36".equals(resTypeId)) {
					return checkDatas[i];
				}
			}
			//基本元数据
			if (checkDatas[i].indexOf("\"67\"") !=-1) {
				if ("37".equals(resTypeId)) {
					return checkDatas[i];
				}
			}
			//有声元数据
			if (checkDatas[i].indexOf("\"68\"") !=-1) {
				if ("38".equals(resTypeId)) {
					return checkDatas[i];
				}
			}
			//漫画源数据
			if (checkDatas[i].indexOf("\"69\"") !=-1) {
				if ("40".equals(resTypeId)) {
					return checkDatas[i];
				}
			}
			//动画元数据
			if (checkDatas[i].indexOf("\"70\"") !=-1) {
				if ("41".equals(resTypeId)) {
					return checkDatas[i];
				}
			}
			//基础元数据
			if (checkDatas[i].indexOf("\"71\"") !=-1) {
				if ("44".equals(resTypeId)) {
					return checkDatas[i];
				}
			}
		}
		
		return checkData;
	}
	/**
	 * 根据resType返回对应的字段权限字符串
	 * 			    元数据id	        资源id
	 * 核心元数据                   1       0
	 * 书目元数据                   2       1
	 * 版权元数据		73      1
	 * 基本元数据		67		37
	 * 有声元数据		68		38
	 * @param resTypeId
	 * @return
	 */
	public List<String> getCheckedCoreMetadataByres(String resTypeId){
		//   1\  获取该登陆用户的字段权限*********该权限在系统管理-》用户管理-》字段权限中配置
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		User user = null;
		String checkedCoreMetadata =null;
		if (userInfo.getUserId() >0) {
			user = (User) getByPk(User.class, userInfo.getUserId());
			checkedCoreMetadata = user.getDataPreJson();
			logger.info("---------获取该登陆名"+user.getLoginName()+",用户名"+user.getUserName()+",字段权限:*" + checkedCoreMetadata + "------------");
		}
		JSONArray array = null;
		try {
			array = JSONArray.fromObject(checkedCoreMetadata);
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<MetadataDefinitionGroup> definitionGroupslist = getBaseDao().query("from MetadataDefinitionGroup t where t.sysResMetadataTypeId="+Long.decode(resTypeId));
		List<String> list = new ArrayList<String>();
		if(array!=null){
			for (int i = 0; i < array.size(); i++) {
				JSONObject jsonObject = array.getJSONObject(i);
				String id = jsonObject.getString("id");
				
				for (MetadataDefinitionGroup definitionGroup : definitionGroupslist) {
					if(definitionGroup.getSysResMetadataTypeId().toString().equals(resTypeId)){
						if(id.equals(definitionGroup.getId().toString())){
							String field = jsonObject.getString("field");
							list.add(field);
						}
					}
				}
			}
		}
		return list;
	}
	
}

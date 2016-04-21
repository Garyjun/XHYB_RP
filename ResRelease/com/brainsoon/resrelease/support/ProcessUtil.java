package com.brainsoon.resrelease.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.common.util.dofile.util.DoFileUtils;
import com.brainsoon.common.util.dofile.util.FilePathUtil;
import com.brainsoon.common.util.dofile.util.WebAppUtils;
import com.brainsoon.resrelease.po.ParamsTempEntity;
import com.brainsoon.resrelease.po.ProdParamsTemplate;
import com.brainsoon.resrelease.po.ResFileRelation;
import com.brainsoon.resrelease.po.ResOrder;
import com.brainsoon.resrelease.service.IPublishTempService;
import com.brainsoon.resrelease.service.IResOrderService;
import com.brainsoon.resrelease.service.IResReleaseService;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.semantic.ontology.model.DoFileQueue;
import com.brainsoon.semantic.ontology.model.DoFileQueueList;
import com.brainsoon.semantic.ontology.model.File;
import com.brainsoon.system.util.MetadataSupport;
import com.google.gson.Gson;

/**
 * @ClassName: ProcessUtil
 * @Description: 资源发布加工工具类
 * @author xiehewei
 * @date 2015年6月18日 上午11:44:25
 *
 */
public class ProcessUtil {

	private static final Logger logger = Logger.getLogger(ProcessUtil.class);
	
	public  static final String FILE_ROOT = StringUtils.replace(WebAppUtils.getWebAppBaseFileDirFR(),"\\", "/");
	public  static final String CONVER_FILE_ROOT = StringUtils.replace(WebAppUtils.getWebAppBaseFileDirCFR(),"\\", "/");
	
	
	/**
	 * 取得 Map<String, List<String>>形式的对象
	 * @param resOrder
	 * @param idStrs
	 * @return
	 */
	public static Map<String, List<String>> getResIdAndFileIdsMap(String idStrs,List<ResFileRelation> fileList){
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		if(fileList != null){
			String[] idStrArray = StringUtils.split(idStrs,",");
			for (int i = 0; i < idStrArray.length; i++) {
				String resId = idStrArray[i];
				List<String> list = new ArrayList<String>();
				for(ResFileRelation detail : fileList){
					if(resId.equals(detail.getResId())){
						String fileId = detail.getFileId();
						list.add(fileId);
					}
				}
				map.put(resId, list);
			}
		}
		return map;
	}
	
	
	/**
	 * 获取每个资源对象下已选取的文件列表，如：文件ide，文件名称、文件路径等
	 * @param ca
	 * @param map
	 * @return
	 */
	public static List<FileBo> getResIdList(Ca ca, Map<String, List<String>> map){
		List<FileBo> strList = new ArrayList<FileBo>();
		List<com.brainsoon.semantic.ontology.model.File> allFileList = ca.getRealFiles(); //资源下所有文件
		String resId = ca.getObjectId();
		String resName = MetadataSupport.getTitle(ca);
		List<String> fileIdList = map.get(resId);//资源中被添加的文件
		if(fileIdList != null && fileIdList.size() > 0){
			for(com.brainsoon.semantic.ontology.model.File f : allFileList){
				if(f.getIsDir().equals("2")){//是文件
					FileBo fileBo = new FileBo();
					String path = f.getPath();
					String fileId = f.getObjectId();
					String name = f.getName();
					if(fileIdList.contains(fileId)){
						fileBo.setResId(resId);//资源id
						fileBo.setFileId(fileId!=null?fileId:"");//文件id
						fileBo.setFileRealPath(path!=null?path:"");//文件路径
						fileBo.setFileName(name!=null?name:"");//文件中文名
						fileBo.setResName(resName!=null?resName:"");//资源名称
						fileBo.setId(f.getId());
						fileBo.setMd5(f.getMd5());
	 					strList.add(fileBo);
					}
				}
			}
		}
		return strList;
	}
	private IResOrderService getResOrderService(){
		IResOrderService resOrderService = null;
		try {
			resOrderService = (IResOrderService) BeanFactoryUtil.getBean("resOrderService");
		} catch (Exception e) {
			logger.debug("bean['resOrderService']尚未装载到容器中！");
			e.printStackTrace();
		}
		return resOrderService;
	}
	
	public static DoFileQueueList processFile(ResOrder resOrder, String fileId, String objectId) {
		logger.info("资源id为：" + objectId + "的文件加工......");
		DoFileQueueList doFileList = new DoFileQueueList();
		if(StringUtils.isNotEmpty(fileId)){
			String url = WebappConfigUtil.getParameter("PUBLISH_FILEDETAIL_URL") + "?id=" + fileId;
			HttpClientUtil http = new HttpClientUtil();
			String fileDetail = http.executeGet(url);
			if(StringUtils.isNotEmpty(fileDetail)){
				Gson gson = new Gson();
				File file = gson.fromJson(fileDetail, File.class);
				if("2".equals(file.getIsDir())){
					DoFileQueue doFile= new DoFileQueue();
					doFile.setFileId(fileId);
					
					String fileFormat = file.getFileType();
					String fileType[] = {"flv","mp3","mp4"};
					boolean type = false;
					for(int i=0;i<fileType.length;i++){
						if(fileType[i].indexOf(fileFormat)!=-1){
							type = true;
						}
					}
					if(!type){
						doFile.setFileFormat(fileFormat);
						String srcTempPath = FILE_ROOT + file.getPath();
						srcTempPath = srcTempPath.replaceAll("\\\\", "\\/");
						doFile.setSrcPath(srcTempPath);
						String converPath = CONVER_FILE_ROOT + FilePathUtil.getConverFileSaveRelPath(file.getPath()) + file.getAliasName();
						doFile = DoFileUtils.getConverPath(converPath, file.getFileType(), doFile);
						doFile.setResId(objectId);
						doFile.setPendingType("0");
						doFileList.addDoFileQueue(doFile);
					}
				}
			}
		}
		return doFileList;
	}
	
	public static void skdf(ResOrder resOrder){
		ProdParamsTemplate template = resOrder.getTemplate();
		ParamsTempEntity paramsTempEntity = new ParamsTempEntity();
		String waterFiles = "";
		if(template!=null){
			try {
				IPublishTempService publishTempService = (IPublishTempService)BeanFactoryUtil.getBean("publishTempService");
				paramsTempEntity = publishTempService.convertEntity(template);
				waterFiles = paramsTempEntity.getWaterMarkFileType();//加水印文件类型 text video pdf
				String markType = paramsTempEntity.getWaterMarkType();//水印类型   图片水印 、文字水印
				String markOpacity = paramsTempEntity.getWaterMarkOpacity();//水印透明度
				if(StringUtils.isNotEmpty(markType)){
					if("图片".equals(markType)){
						String imgUrl = paramsTempEntity.getImgWaterMarkURL(); //水印图片位置 相对位置
						if(StringUtils.isNotEmpty(waterFiles)){
							if("text".equals(waterFiles)){
								
							}else if("video".equals(waterFiles)){
								
							}else if("pdf".equals(waterFiles)){
								
							}
						}
					}else{
						String waterText = paramsTempEntity.getWaterMarkText();//加水印文字
						String isBold = paramsTempEntity.getWaterMarkTextBold();//文字是否加粗
						String textCol = paramsTempEntity.getWaterMarkColor();//文字颜色
						String textSize = paramsTempEntity.getWaterMarkTextSize();//文字大小
						String textFont = paramsTempEntity.getWaterMarkTextFont();//文字字体
						if(StringUtils.isNotEmpty(waterFiles)){
							if("image".equals(waterFiles)){
								String imgHeight = paramsTempEntity.getImgHeight();//处理后文件图片限高
								String imgWidth = paramsTempEntity.getImgWidth();//处理后文件图片限宽
								String imgFileType = paramsTempEntity.getImgType();//要处理的图片文件格式
								String imgMarkPos = paramsTempEntity.getImgWaterMarkPos();//水印图片的位置
							}else if("video".equals(waterFiles)){
								String videoFileType = paramsTempEntity.getVideoType();//要处理的视频文件格式
								String videoMarkPos = paramsTempEntity.getVideoWaterMarkPos();//视频水印位置
							}else if("text".equals(waterFiles)){
								String textFileType = paramsTempEntity.getTextType();//要处理的文本文件格式
								String textMakPos = paramsTempEntity.getWordWaterMarkPos();//文本水印位置
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}

package com.brainsoon.resrelease.processThread;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.support.GlobalDataCacheMap;
import com.brainsoon.common.support.OperDbUtils;
import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.common.util.dofile.content.PdfUtil;
import com.brainsoon.common.util.dofile.conver.ConverUtils;
import com.brainsoon.common.util.dofile.image.ImageUtils;
import com.brainsoon.common.util.dofile.image.ImgCoverUtil;
import com.brainsoon.common.util.dofile.util.ColorUtil;
import com.brainsoon.common.util.dofile.util.ConstantsDef;
import com.brainsoon.common.util.dofile.util.DoFileUtils;
import com.brainsoon.common.util.dofile.util.PropertiesReader;
import com.brainsoon.common.util.dofile.util.WebAppUtils;
import com.brainsoon.resource.po.SubjectStore;
import com.brainsoon.resource.service.ISubjectService;
import com.brainsoon.resource.support.ExcelUtil;
import com.brainsoon.resrelease.po.ParamsTempEntity;
import com.brainsoon.resrelease.po.ProdParamsTemplate;
import com.brainsoon.resrelease.po.ResFileRelation;
import com.brainsoon.resrelease.po.ResOrder;
import com.brainsoon.resrelease.po.ResOrderDetail;
import com.brainsoon.resrelease.po.ResRelease;
import com.brainsoon.resrelease.po.ResReleaseDetail;
import com.brainsoon.resrelease.service.IPublishTempService;
import com.brainsoon.resrelease.service.IResOrderService;
import com.brainsoon.resrelease.service.IResReleaseService;
import com.brainsoon.resrelease.support.FileBo;
import com.brainsoon.resrelease.support.ProcessUtil;
import com.brainsoon.resrelease.support.ResReleaseConstant;
import com.brainsoon.resrelease.support.SysParamsTemplateConstants;
import com.brainsoon.resrelease.support.XmlUtil;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.semantic.ontology.model.CaList;
import com.brainsoon.semantic.ontology.model.SearchParamCa;
import com.brainsoon.semantic.schema.ontology.MetadataDefinition;
import com.brainsoon.system.model.Company;
import com.brainsoon.system.model.Staff;
import com.brainsoon.system.model.User;
import com.brainsoon.system.service.IFLTXService;
import com.brainsoon.system.util.MetadataSupport;
import com.google.gson.Gson;

/**
 * @ClassName: BatchProcessThread
 * @Description: 需求单资源加工线程
 * @author tanghui
 * @date 2015年5月27日 上午10:30:31
 *
 */
public class BatchProcessThread extends Thread {

	private static Logger logger = Logger.getLogger(BatchProcessThread.class);
	//资源明细列表接口
	private final static String PUBLISH_DETAILLIST_URL = WebappConfigUtil.getParameter("PUBLISH_DETAILLIST_URL");
	private final static String PUBLISH_DETAIL_URL =  WebappConfigUtil.getParameter("PUBLISH_DETAIL_URL");
	//pdf抽文本和pdf转swf
	private final static String PUBLISH_PDF_SWF_TXT =  WebappConfigUtil.getParameter("PUBLISH_PDF_SWF_TXT");
	//获取资源根路径
	private static final String fileRoot = WebAppUtils.getWebRootBaseDir(ConstantsDef.fileRoot).replaceAll("\\\\", "\\/");
	//获得发布的根路径
	//private static final String publishRoot = WebAppUtils.getWebRootBaseDir(ConstantsDef.prodFile).replaceAll("\\\\", "\\/");
	//图片
	private static final String pictureFormat = PropertiesReader.getInstance().getProperty(ConstantsDef.pictureFormat);
	//视频
	private static final String videoFormat = PropertiesReader.getInstance().getProperty(ConstantsDef.videoFormat);
	//音频
	//private static final String audioFormat = PropertiesReader.getInstance().getProperty(ConstantsDef.audioFormat);
	//动画
	//private static final String animaFormat = PropertiesReader.getInstance().getProperty(ConstantsDef.animaFormat);
	//文档
	//private static final String documentFormat = PropertiesReader.getInstance().getProperty(ConstantsDef.documentFormat);
	
	
	@Override
	public void run() {
		while (true) {
			//TaskInfo taskData = null;
			String objectId = "";
			String posttype = "";
			try {
				sleep(60000);//等待60秒
				//查询主题库表or需求单表，查询到状态为待加工的单据，则进行加工
				List<SubjectStore> stores =getSubjectService().query("from SubjectStore t where t.status in('"+ResReleaseConstant.OrderStatus.WAITING_PROCESS+"','"+ResReleaseConstant.OrderStatus.PROCESSING+"')");
				if(stores.isEmpty()){
					List<ResOrder> orders = getResOrderService().query("from ResOrder t where t.status in('"+ResReleaseConstant.OrderStatus.WAITING_PROCESS+"','"+ResReleaseConstant.OrderStatus.PROCESSING+"')");
					if(orders.size()>0){
						for (ResOrder resOrder : orders) {
							objectId = resOrder.getOrderId().toString();
							posttype = "1";
							break;
						}
					}
				}else{
					for (SubjectStore subjectStore : stores) {
						objectId = subjectStore.getId().toString();
						posttype = "2";
						break;
					}
				}
				// 获取任务队列
				//TaskQueue queue = TaskQueue.getInst();
				// 启动任务
				//taskData = queue.getMessage(5000);
				if (StringUtils.isNotBlank(objectId)&&StringUtils.isNotBlank(posttype)) {
					IResOrderService resOrderService = getResOrderService();
					IResReleaseService resReleaseService = getResReleaseService();
					ISubjectService subjectService = getSubjectService();
					logger.info("get message=============");
					//====================start========更改状态========================
					//模板
					ProdParamsTemplate template  = new ProdParamsTemplate();
					ResRelease resRelease = new ResRelease();
					//要发布的元数据文件类型
					String metaInfos ="";
					//发布路径
					String publishDir="";
					if(posttype.equals("1")){
						//需求单
						//========================end======更改状态======================
						ResOrder resOrder = (ResOrder) resOrderService.getByPk(ResOrder.class, Long.decode(objectId));
						resOrder.setStatus(ResReleaseConstant.OrderStatus.PROCESSING);
						resOrderService.update(resOrder);
						//====================start==获取对象参数==============================
						template = resOrder.getTemplate();
						metaInfos = template.getMetaInfo(); //要发布的元数据文件类型
						//====================end==获取对象参数==============================
						
						//====================start==文件处理==============================
						
						//获取发布路径
						publishDir = com.brainsoon.resrelease.support.FileUtil.getPublishPathsxqd(resOrder);
						List<ResRelease> resReleases = resReleaseService.query("from ResRelease t where t.orderId='"+objectId+"' and t.posttype='"+posttype+"'");
						//查询是不是加工失败的单据，如果是失败的单据跳过增加主表记录
						if(resReleases==null || resReleases.size()==0){
							/*for (int i = 0; i < resReleases.size(); i++) {
								List<ResReleaseDetail> ResReleaseDetaillist =resReleaseService.query("from ResReleaseDetail t where t.releaseId="+resReleases.get(i).getId());
								for (ResReleaseDetail resReleaseDetail : ResReleaseDetaillist) {
									getResReleaseService().delete(resReleaseDetail);
								}
								getResReleaseService().delete(resReleases.get(i));
							}*/
							//写记录：往发布记录表中添加记录
							resRelease.setProcessTime(new Date());
							resRelease.setOrderId(resOrder.getOrderId());
							resRelease.setChannelName(resOrder.getChannelName());
							resRelease.setStatus(ResReleaseConstant.ResReleaseStatus.WAITING_PUBLISH);//需求单文件加工后将生成发布记录，此时发布状态为0   表示待发布
							resRelease.setPlatformId(resOrder.getPlatformId());
							resRelease.setCreateTime(new Date());
							//UserInfo users = taskData.getUserInfo();
							User user = new User();
							user.setId(resOrder.getAuditor().getId());
							resRelease.setCreateUser(user);
							resRelease.setPosttype(posttype);
							resRelease.setReasonDesc("资源需求单计划创建!");
							resRelease.setBatchNum("xqd-" + getName(resOrder.getOrderId().intValue()));
							resRelease.setDescription("由资源需求单号" + resOrder.getOrderId() + "生成的发布清单!");
							resReleaseService.save(resRelease);
						}else{
							if(resReleases.size()>0 && resReleases!=null){
								for (ResRelease resRelease2 : resReleases) {
									resRelease = resRelease2;
									break;
								}
							}
						}
						
					}else if(posttype.equals("2")){
						//主题库
						//========================end======更改状态======================
						SubjectStore subjectStore = (SubjectStore) subjectService.getByPk(SubjectStore.class, Long.decode(objectId));
						subjectStore.setStatus(ResReleaseConstant.OrderStatus.PROCESSING);
						subjectService.update(subjectStore);
						//====================start==获取对象参数==============================
						template = subjectStore.getTemplate();
						metaInfos = template.getMetaInfo(); //要发布的元数据文件类型
						//====================end==获取对象参数==============================
						
						//====================start==文件处理==============================
						
						//获取发布路径
						publishDir = com.brainsoon.resrelease.support.FileUtil.getPublishPathsztk(subjectStore);
						List<ResRelease> resReleases = resReleaseService.query("from ResRelease t where t.orderId='"+objectId+"' and t.posttype='"+posttype+"'");
						//查询是不是加工失败的单据，如果是失败的单据跳过增加主表记录
						if(resReleases==null || resReleases.size()==0){
							/*for (int i = 0; i < resReleases.size(); i++) {
								List<ResReleaseDetail> ResReleaseDetaillist =resReleaseService.query("from ResReleaseDetail t where t.releaseId="+resReleases.get(i).getId());
								for (ResReleaseDetail resReleaseDetail : ResReleaseDetaillist) {
									getResReleaseService().delete(resReleaseDetail);
								}
								getResReleaseService().delete(resReleases.get(i));
							}*/
							//写记录：往发布记录表中添加记录
							resRelease.setProcessTime(new Date());
							resRelease.setOrderId(subjectStore.getId());
							resRelease.setChannelName(subjectStore.getName());
							resRelease.setStatus(ResReleaseConstant.ResReleaseStatus.WAITING_PUBLISH);//需求单文件加工后将生成发布记录，此时发布状态为0   表示待发布
							resRelease.setPlatformId(subjectStore.getPlatformId());
							resRelease.setCreateTime(new Date());
							User user = new User();
							user.setId(subjectStore.getAuditor().getId());
							resRelease.setCreateUser(user);
							resRelease.setPosttype(posttype);
							resRelease.setReasonDesc("资源主题库计划创建!");
							resRelease.setBatchNum("ztk-" + getName(subjectStore.getId().intValue()));
							resRelease.setDescription("由资源主题库号" + subjectStore.getId() + "生成的发布清单!");
							resReleaseService.save(resRelease);
						}else{
							if(resReleases.size()>0 && resReleases!=null){
								for (ResRelease resRelease2 : resReleases) {
									resRelease = resRelease2;
									break;
								}
							}
						}
						
					}
					
		
					//第一步：按照要求生成元数据文件及生成水印，如：元数据Excel,文件清单Xml,资源文件
					doMetaDataAndResFile(objectId,posttype,template, metaInfos,publishDir,resRelease);
					
					//========================end===文件处理=========================
					//发布
					if(posttype.equals("1")){
						ResOrder resOrder = (ResOrder) resOrderService.getByPk(ResOrder.class, Long.decode(objectId));
						if(resOrder.getTemplate().getType().equals("offLine")){//线下直接更改为已发布
							resOrder.setStatus(ResReleaseConstant.OrderStatus.ORDERADD);
							resOrder.setProcessRemark("");
							resOrderService.update(resOrder);
							//发布表状态改为已发布
							resRelease.setStatus(ResReleaseConstant.ResReleaseStatus.PUBLISHED);
							resOrderService.saveOrUpdate(resRelease);
							//发布明细表插入数据并更改状态为发布成功
							List<ResReleaseDetail> resReleaseDetailList =resReleaseService.getResReleaseDetailByCnodition(resRelease.getId());
							for (ResReleaseDetail resReleaseDetail : resReleaseDetailList) {
								resReleaseDetail.setStatus(ResReleaseConstant.PublishingStatus.SUCCESS_PUBLISH);
								resReleaseService.saveOrUpdate(resReleaseDetail);
							}
						}else{
							resOrder.setStatus(ResReleaseConstant.OrderStatus.PUBLISHING);  
							resOrder.setProcessRemark("");
							resOrderService.update(resOrder);
							//发布表状态改为发布中
							resRelease.setStatus(ResReleaseConstant.ResReleaseStatus.PUBLISHING);
							resOrderService.saveOrUpdate(resRelease);
							//发布明细表插入数据并更改状态为发布中
							List<ResReleaseDetail> resReleaseDetailList =resReleaseService.getResReleaseDetailByCnodition(resRelease.getId());
							for (ResReleaseDetail resReleaseDetail : resReleaseDetailList) {
								resReleaseDetail.setStatus(ResReleaseConstant.PublishingStatus.PUBLISHING);
								resReleaseService.saveOrUpdate(resReleaseDetail);
							}
						}
					}else if(posttype.equals("2")){
						SubjectStore subjectStore = (SubjectStore) subjectService.getByPk(SubjectStore.class, Long.decode(objectId));
						subjectStore.setStatus(ResReleaseConstant.OrderStatus.PUBLISHING);
						subjectStore.setProcessremark("");
						subjectService.update(subjectStore);
						//发布表状态改为发布中
						resRelease.setStatus(ResReleaseConstant.ResReleaseStatus.PUBLISHING);
						resOrderService.saveOrUpdate(resRelease);
						//发布明细表插入数据并更改状态为发布中
						List<ResReleaseDetail> resReleaseDetailList =resReleaseService.getResReleaseDetailByCnodition(resRelease.getId());
						for (ResReleaseDetail resReleaseDetail : resReleaseDetailList) {
							resReleaseDetail.setStatus(ResReleaseConstant.PublishingStatus.PUBLISHING);
							resReleaseService.saveOrUpdate(resReleaseDetail);
						}
					}
				}
			}catch (Exception e) {
				//线程异常时，更改状态为加工失败，并添加加工备注
				/*String objectids = taskData.getObjectId();
				String posttype = taskData.getPosttype();*/
				//需求单
				if(posttype.equals("1")){
					ResOrder order = (ResOrder) getResOrderService().getByPk(ResOrder.class, Long.decode(objectId));
					order.setStatus(ResReleaseConstant.OrderStatus.PROCESSEDWRONG);
					order.setProcessRemark("资源加工线程异常："+e.getMessage());
					getResOrderService().update(order);
				}
				//主题库
				if(posttype.equals("2")){
					SubjectStore store = (SubjectStore) getSubjectService().getByPk(SubjectStore.class, Long.decode(objectId));
					store.setStatus(ResReleaseConstant.OrderStatus.PROCESSEDWRONG);
					store.setProcessremark("资源加工线程异常："+e.getMessage());
					getSubjectService().update(store);
				}
				logger.info("资源加工线程异常"+e.getMessage());
				e.printStackTrace();
			}
		}		
	}
	
	
	private static String getName(int n) {
		String name = String.valueOf(n);
		int len = 5 - name.length();
		for (int i = 0; i < len; i++) {
			name = "0" + name;
		}
		return name;
	}
	
	/**
	 * 按照要求生成元数据文件、加水印等操作
	 * @param template
	 * @param metaInfos
	 * @param publishDir 发布后的文件根目录
	 */
	public void doMetaDataAndResFile(String objectId,String posttype,ProdParamsTemplate template,String metaInfos,String publishDir,ResRelease resRelease){
		try {
			if(metaInfos!=null){
				//第一步：按照要求生成元数据文件，如：元数据Excel,文件清单Xml,资源文件
				
				//取得所有的资源id
				String idStrs = getIdsString(objectId,posttype);
				//取得资源列表详细信息
				CaList caList = obtainCaList(idStrs);
				//获取所有的资源文件
				List<ResFileRelation> fileList = getResOrderService().queryFileByOrdeIdAndposttype(Long.decode(objectId), posttype);
				//取得 Map<resid, List<file>>形式的对象
				Map<String, List<String>> map = ProcessUtil.getResIdAndFileIdsMap(idStrs,fileList);
				
				//拷贝：资源文件
				if(metaInfos.contains("资源文件")){//拷贝资源文件到指定的位置
					if(posttype.equals("1")){
						IResOrderService resOrderService = getResOrderService();
						ResOrder resOrder= (ResOrder) resOrderService.getByPk(ResOrder.class, Long.decode(objectId));
						if(resOrder.getTemplate().getPublishType().equals("offLine")){
							publishResources(resOrder,posttype,caList, map,publishDir,resRelease);
							addWaterMark(objectId,posttype,template,publishDir,caList,map);
						}
						if(resOrder.getTemplate().getPublishType().equals("onLine")){
							publishResourcesResorderonline(resOrder,posttype,caList, map,publishDir,resRelease);
							addWaterMarkonline(objectId,posttype,template,publishDir,caList,map,resRelease);
						}
					}
					if(posttype.equals("2")){
						ISubjectService subjectService = getSubjectService();
						SubjectStore subjectStore = (SubjectStore) subjectService.getByPk(SubjectStore.class, Long.decode(objectId));
						try {
							//移动主题库logo图片
							//原始路径
							String paths = WebAppUtils.getWebAppRoot()+subjectStore.getLogo();
							//移动到的路径
							String logopaths = subjectStore.getLogo();
							logopaths = logopaths.substring(logopaths.lastIndexOf("/")+1, logopaths.length());
							String path = publishDir+logopaths;
							FileUtils.copyFile(new File(paths), new File(path));
						} catch (Exception e) {
							e.printStackTrace();
							throw new ServiceException("Logo移动失败!");
						}
						publishResourcesSubjectonline(subjectStore,posttype,caList, map,publishDir,resRelease);
						addWaterMarkonline(objectId,posttype,template,publishDir,caList,map,resRelease);
					}
				}
				//资源文件加水印：判断是否加水印，如果要加水印则添加，否则不添加，返回的fileMap主要是为生成文件清单xml而使用的
				//Map<String,String> fileMap = addWaterMark(objectId,posttype,template,publishDir,caList,map);
				
				//生成：元数据Excel
				if(metaInfos.contains("元数据Excel")){
					if(posttype.equals("1")){
						IResOrderService resOrderService = getResOrderService();
						ResOrder resOrder= (ResOrder) resOrderService.getByPk(ResOrder.class, Long.decode(objectId));
						resOrder = createMetadataExcelResorder(resOrder, template, caList,publishDir);
					}
					//主题库没有线下发布，故没有元数据Excel
					/*if(posttype.equals("2")){
						ISubjectService subjectService = getSubjectService();
						SubjectStore subjectStore= (SubjectStore) subjectService.getByPk(SubjectStore.class, Long.decode(objectId));
						subjectStore = createMetadataExcelSubject(subjectStore, template, caList, publishDir);
					}*/
				}
				
				if(metaInfos.contains("元数据Json")){
					if(posttype.equals("1")){
						IResOrderService resOrderService = getResOrderService();
						ResOrder resOrder= (ResOrder) resOrderService.getByPk(ResOrder.class, Long.decode(objectId));
						List<ResOrderDetail> list=resOrderService.getResOrderDetailByOrderIdAndtype(objectId, posttype);
						resOrder = createMetadataJson(resOrder,list, template, caList,publishDir);
					}
					if(posttype.equals("2")){
						ISubjectService subjectService = getSubjectService();
						IResOrderService resOrderService = getResOrderService();
						SubjectStore subjectStore= (SubjectStore) subjectService.getByPk(SubjectStore.class, Long.decode(objectId));
						List<ResOrderDetail> list=resOrderService.getResOrderDetailByOrderIdAndtype(objectId, posttype);
						subjectStore = createMetadataJsonSubject(subjectStore,list, template, caList,publishDir);
					}
				}
				//生成opf文件/放在pdf文件目录下
				if(posttype.equals("1") || posttype.equals("2")){
					logger.info("这里错了-----------3333333333333333333333333333333333333333333----------------------");
					IResOrderService resOrderService = getResOrderService();
					ResOrder resOrder = null;
					if(posttype.equals("1")){
						resOrder= (ResOrder) resOrderService.getByPk(ResOrder.class, Long.decode(objectId));
					}
					if((resOrder != null && resOrder.getTemplate().getPublishType().equals("onLine")) || posttype.equals("2")){
						List<ResOrderDetail> list=resOrderService.getResOrderDetailByOrderIdAndtype(objectId, posttype);
						logger.info("资源文件数量---------------------------------"+list.size());
						logger.info("发布id---------------------------------"+resRelease.getId());
						List<ResReleaseDetail> resReleaseDetails = getResReleaseService().getResReleaseDetailByCnodition(resRelease.getId());
						List<ResOrderDetail> details = new ArrayList<ResOrderDetail>();
						List<String> orderslist = new ArrayList<String>();
						List<String> reslist = new ArrayList<String>();
						List<String> orderlists = new ArrayList<String>();
						for (ResOrderDetail resOrderDetail : list) {
							orderslist.add(resOrderDetail.getResId());
						}
						for (ResReleaseDetail resReleaseDetail : resReleaseDetails) {
							reslist.add(resReleaseDetail.getResId());
						}
						for (String orderslists : orderslist) {
							if(!reslist.contains(orderslists)){
								orderlists.add(orderslists);
							}
						}
						for (ResOrderDetail resOrderDetail : list) {
							if(orderlists.contains(resOrderDetail.getResId())){
								details.add(resOrderDetail);
							}
						}
						logger.info("到这里了---------------------------------=======================");
						if(!details.isEmpty()){
							logger.info("到这里了---------------------交换机交换机------------=======================");
							creatopf(details, publishDir);
						}else{
							logger.info("到这里了----------------------122222222222-----------=======================");
							creatopf(list, publishDir);
						}
					}
				}
				logger.info("到这里了--------------------------88888888888888888888888-------=======================");
				//将pdf转换成swf
				String release2swf = OperDbUtils.queryParamValueByKey("release2swf");
				//只有为1才代表处理
				if(posttype.equals("1")){
					IResOrderService resOrderService = getResOrderService();
					ResOrder resOrder= (ResOrder) resOrderService.getByPk(ResOrder.class, Long.decode(objectId));
					if(resOrder.getTemplate().getPublishType().equals("onLine")){
						if(StringUtils.isNotBlank(release2swf)){
							if(release2swf.equals("1")){
								createswftopdf(objectId,posttype,caList, map,publishDir,resRelease);
							}
						}else{
							logger.error("未配置是否生成swf参数：【release2swf】");
							throw new ServiceException("未配置是否生成swf参数：【release2swf】");
						}
						
					}
				}
				if(posttype.equals("2")){
					if(StringUtils.isNotBlank(release2swf)){
						if(release2swf.equals("1")){
							createswftopdf(objectId,posttype,caList, map,publishDir,resRelease);
						}
					}else{
						logger.error("未配置是否生成swf参数：【release2swf】");
						throw new ServiceException("未配置是否生成swf参数：【release2swf】");
					}
				}
				
				//生成：文件清单Xml
				/*if(metaInfos.contains("文件清单Xml")){
					createResourcesList(resOrder, caList,publishDir,map,fileMap);
				}*/
			}
		} catch (Exception e) {
			throw new ServiceException("加工任务第一步失败！---->"+e.getMessage());
		}
	}
	private void creatopf(List<ResOrderDetail> list,String publishDir){
		try {
			if(list!=null && list.size()>0){
				for (ResOrderDetail resOrderDetail : list) {
					String restype = resOrderDetail.getResType();
					HttpClientUtil http = new HttpClientUtil();
					String resource = http.executeGet(PUBLISH_DETAIL_URL + "?id=" + resOrderDetail.getResId());
					if(resource!=null){
						Gson gson = new Gson();
						Ca oldCa = gson.fromJson(resource, Ca.class);
						JSONObject jsonObject=JSONObject.fromObject(resource);
						JSONObject objects=jsonObject.getJSONObject("metadataMap");
						StringBuffer sb = new StringBuffer();
						List<MetadataDefinition> metadataDefinitions = MetadataSupport.getAllMetadataDefinitionByResType(restype);
						if(metadataDefinitions!=null){
							int creatornotsize = 0;
							int creatorsize = 0;
							for (MetadataDefinition metadataDefinition : metadataDefinitions) {
								System.out.println(metadataDefinition.getIdentifier()+"**********");
								if(StringUtils.isNotBlank(metadataDefinition.getIdentifier().toString()) && metadataDefinition.getIdentifier().toString().equals("3")){
									//3代表资源名
									Object values=objects.get(metadataDefinition.getFieldName());
									if(values!=null){
										String names = objects.getString(metadataDefinition.getFieldName());
										sb.append("<dc:bookname>"+names+"</dc:bookname>\n");
									}else{
										sb.append("<dc:bookname></dc:bookname>\n");
									}
								}
								if(StringUtils.isNotBlank(metadataDefinition.getIdentifier().toString()) && metadataDefinition.getIdentifier().toString().equals("8")){
									//8代表作者
									Object values=objects.get(metadataDefinition.getFieldName());
									if(values!=null){
										String names = objects.getString(metadataDefinition.getFieldName());
										String namevalues [] = names.split(",");
										String staffname = "";
										if(StringUtils.isNotBlank(names)){
											if(creatorsize==0){
												for (String namevalue : namevalues) {
													Staff staff= (Staff) getSubjectService().getByPk(Staff.class, Long.decode(namevalue));
													staffname += staff.getName()+",";
												}
												if(staffname.length()>1){
													staffname = staffname.substring(0, staffname.length()-1);
												}
												creatorsize++;
												sb.append("<dc:creator>"+staffname+"</dc:creator>\n");
											}
										}
									}else{
										creatornotsize++;
										if(creatornotsize==3){
											sb.append("<dc:creator></dc:creator>\n");
										}
									}
								}
								if(StringUtils.isNotBlank(metadataDefinition.getIdentifier().toString()) && metadataDefinition.getIdentifier().toString().equals("18")){
									//18代表图书价格
									Object values=objects.get(metadataDefinition.getFieldName());
									if(values!=null){
										String names = objects.getString(metadataDefinition.getFieldName());
										sb.append("<dc:paperprice>"+names+"</dc:paperprice>\n");
									}else{
										sb.append("<dc:paperprice></dc:paperprice>\n");
									}
								}
								if(StringUtils.isNotBlank(metadataDefinition.getIdentifier().toString()) && metadataDefinition.getIdentifier().toString().equals("16")){
									//16代表摘要（描述）
									Object values=objects.get(metadataDefinition.getFieldName());
									if(values!=null){
										String names = objects.getString(metadataDefinition.getFieldName());
										sb.append("<dc:description>"+names+"</dc:description>\n");
									}else{
										sb.append("<dc:description></dc:description>\n");
									}
								}
								if(StringUtils.isNotBlank(metadataDefinition.getIdentifier().toString()) && metadataDefinition.getIdentifier().toString().equals("7")){
									//7代表出版单位
									Object values=objects.get(metadataDefinition.getFieldName());
									if(values!=null){
										String names = objects.getString(metadataDefinition.getFieldName());
										String namevalues [] = names.split(",");
										String companyname = "";
										if(StringUtils.isNotBlank(names)){
											for (String namevalue : namevalues) {
												Company company= (Company) getSubjectService().getByPk(Company.class, Long.decode(namevalue));
												companyname += company.getName()+",";
											}
											if(companyname.length()>1){
												companyname = companyname.substring(0, companyname.length()-1);
											}
										}
										sb.append("<dc:press>"+companyname+"</dc:press>\n");
									}else{
										sb.append("<dc:press></dc:press>\n");
									}
								}
								if(StringUtils.isNotBlank(metadataDefinition.getIdentifier().toString()) && metadataDefinition.getIdentifier().toString().equals("17")){
									//17代表出版日期
									Object values=objects.get(metadataDefinition.getFieldName());
									if(values!=null){
										String namesdate = objects.getString(metadataDefinition.getFieldName());
										Date dateOld = new Date(Long.parseLong(namesdate));
										namesdate = new SimpleDateFormat(metadataDefinition.getValueRange()).format(dateOld);
										sb.append("<dc:publishdate>"+namesdate+"</dc:publishdate>\n");
									}else{
										sb.append("<dc:publishdate></dc:publishdate>\n");
									}
								}
								if(StringUtils.isNotBlank(metadataDefinition.getIdentifier().toString()) && metadataDefinition.getIdentifier().toString().equals("6")){
									//6代表ISBN
									Object values=objects.get(metadataDefinition.getFieldName());
									if(values!=null){
										String namesdate = objects.getString(metadataDefinition.getFieldName());
										sb.append("<dc:isbn>"+namesdate+"</dc:isbn>\n");
									}else{
										sb.append("<dc:isbn></dc:isbn>\n");
									}
								}
							}
						}
						StringBuffer buffer = new StringBuffer();
						buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
						buffer.append("<package xmlns=\"http://www.idpf.org/2007/opf\" version=\"2.0\" unique-identifier=\""+jsonObject.getString("objectId")+"\">\n");
						buffer.append("<metadata xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:opf=\"http://www.idpf.org/2007/opf\" xmlns:dcterms=\"http://purl.org/dc/terms/\" xmlns:calibre=\"http://calibre.kovidgoyal.net/2009/metadata\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\">\n");
						buffer.append(sb.toString());
						buffer.append("</metadata>\n");
						buffer.append("</package>");
						List<com.brainsoon.semantic.ontology.model.File> files = oldCa.getRealFiles();
						//这里path是查找到该资源文件目录
						/*for (com.brainsoon.semantic.ontology.model.File file : files) {
							if (file.getIsDir().equals("1")) {
								String paths = file.getPath();
								path = paths.replace(oldCa.getRootPath().replaceAll("\\\\", "\\/"), "");
								logger.info(""+path);
								
								path = path.replaceAll("\\\\", "\\/");
								if(path.startsWith("/")){
									path = path.substring(1, path.length());
								}if(path.contains("/")){
									path = path.substring(0, path.indexOf("/"));
								}
								if(path.endsWith("/")){
									path = path.substring(0, path.length()-1);
								}
								break;
							}
						}*/
						String soupath = publishDir+resOrderDetail.getResId().substring(4);
						logger.info("我是拼接后的地址--"+soupath);
						File file = new File(soupath);
						logger.info("我是拼接后的地址--"+file.getPath());
						if(file.exists()){
							logger.info("我进来了----》");
							File filelists[] = file.listFiles();
							for (File filelist : filelists) {
								if(filelist.isDirectory()){
									String paths = filelist.getPath();
									paths = paths.replaceAll("\\\\", "\\/");
									paths = paths.substring(paths.lastIndexOf("/")+1, paths.length());
									if(paths.contains("pdf")){
										File pdffile[] = filelist.listFiles();
										for (int i = 0; i < pdffile.length; i++) {
											//创建文件
											//生成文件，并放在指定目录
											String pdfpaths = pdffile[i].getPath()+"/book.opf";
											File pdfpath = new File(pdfpaths);
											if(!pdfpath.exists()){
												try {
													pdfpath.createNewFile();
												} catch (IOException e) {
													throw new ServiceException("生成opf文件失败！");
												}
											}
											
											byte[] b = null;
											try {
												b = buffer.toString().getBytes("UTF-8");
											} catch (UnsupportedEncodingException e1) {
												throw new ServiceException("生成opf文件失败！");
											}
											FileOutputStream outputStream = null;
											try {
												outputStream = new FileOutputStream(pdfpath);
												outputStream.write(b, 0, b.length);
												outputStream.close();
											} catch (FileNotFoundException e) {
												throw new ServiceException("生成opf文件失败！");
											} catch (IOException e) {
												throw new ServiceException("生成opf文件失败！");
											}
										}
									}
								}
							}
						}else{
							throw new ServiceException("生成opf未找到根目录----");
						}
					}
				}
				
			}
		} catch (Exception e) {
			throw new ServiceException("opf生成失败！---->"+e.getMessage());
		}
	}
	
	private void createswftopdf(String objectId,String posttype,CaList caList,Map<String, List<String>> map,String publishDir,ResRelease resRelease){
		try {
			if(caList!=null){
				ResOrder resOrder = new ResOrder();
				SubjectStore subjectStore = new SubjectStore();
				List<ResReleaseDetail> resReleaseDetails = getResReleaseService().getResReleaseDetailByCnodition(resRelease.getId());
				List<String> list = new ArrayList<String>();
				for (ResReleaseDetail resReleaseDetail : resReleaseDetails) {
					list.add(resReleaseDetail.getResId());
				}
				logger.info("集合的大小----------------->"+list.size());
				String onLineType = OperDbUtils.queryParamValueByKey("onLineType");
				logger.info("这是系统配置---------------------->"+onLineType);
				List<Ca> calListStr = caList.getCas();
				if(calListStr != null && calListStr.size() > 0){
					for(Ca ca : calListStr){
						if(!list.contains(ca.getObjectId())){
							//资源拷贝的路径
							logger.info("进来了--------------------------------------------");
							//获取每个资源对象下已选取的文件列表
							List<FileBo> fileList = ProcessUtil.getResIdList(ca, map);
							
							if(fileList != null && fileList.size() > 0){
								
  								for (FileBo fileBo : fileList) {
									//获取拷贝后的文件绝对地址
									String filePath = fileBo.getId().replaceAll("\\\\", "\\/");
									logger.info("获取拷贝后的文件绝对地址----------------------"+filePath);
									String pathss = fileBo.getResId().replaceAll("\\\\", "\\/").substring(4);
									String paths = publishDir+ URLEncoder.encode(pathss, "UTF-8");
									logger.info("拼接后的----------------------"+paths);
									if(filePath.substring(filePath.lastIndexOf("/")+1).toLowerCase().contains(".pdf")){
										if(onLineType.contains("2")){
											if(fileBo.getFileName().toLowerCase().contains("h") || fileBo.getFileName().contains("印刷")){
												paths = paths+"/pdf/H/"+URLEncoder.encode(fileBo.getFileName(), "UTF-8");
												HttpClientUtil http = new HttpClientUtil();
												///
												String path=PUBLISH_PDF_SWF_TXT + "?resId="+fileBo.getResId()+"&objectId="+fileBo.getFileId()+"&pdfPath="+paths+"&convertPath="+paths.substring(0, paths.lastIndexOf("/")+1)+""+"&swfFormat=nswf&hasFileName=false";
												logger.info("最终地址--------------------->"+path);
												String resource = http.executeGet(path);
												if(resource.toLowerCase().equals("success")){
													logger.info("生成swf成功");
												}else {
													logger.info("生成swf失败");
													throw new ServiceException("生成swf失败!");
												}

											}
										}
										if(onLineType.contains("3")){
											if(fileBo.getFileName().toLowerCase().contains("l") || fileBo.getFileName().contains("宣传")){
												paths = paths+"/pdf/L/"+URLEncoder.encode(fileBo.getFileName(), "UTF-8");
												HttpClientUtil http = new HttpClientUtil();
												//
												String path=PUBLISH_PDF_SWF_TXT + "?resId="+fileBo.getResId()+"&objectId="+fileBo.getFileId()+"&pdfPath="+paths+"&convertPath="+paths.subSequence(0, paths.lastIndexOf("/")+1)+""+"&swfFormat=nswf&hasFileName=false";
												String resource = http.executeGet(path);
												if(resource.toLowerCase().equals("success")){
													logger.info("生成swf成功");
												}else {
													logger.info("生成swf失败");
													throw new ServiceException("生成swf失败!");
												}
											}
										}
										
										
									}
									
								}
							}
							ResReleaseDetail  detail = new ResReleaseDetail();
							if(posttype.equals("1")){
								//需求单
								resOrder = (ResOrder) getResOrderService().getByPk(ResOrder.class, Long.decode(objectId));
								String channelName = resOrder.getChannelName();
								detail.setChannelName(channelName);
							}
							if(posttype.equals("2")){
								subjectStore = (SubjectStore) getSubjectService().getByPk(SubjectStore.class, Long.decode(objectId));
								String channelName = subjectStore.getName();
								detail.setChannelName(channelName);
							}
							//写记录到发布明细表中，并更新发布明细表中的资源加工状态
							Long releaseId = resRelease.getId();
							detail.setReleaseId(releaseId);
							detail.setCreateTime(new Date());
							detail.setResId(ca.getObjectId());
							String resType = ca.getPublishType();
							detail.setResType(resType);
							detail.setProcessFileSuccess(ResReleaseConstant.FileProcessStatus.FILE_PROCESS_SUCCESS);
							detail.setRemark("加工成功");
							getResReleaseService().saveOrUpdate(detail);
						}
						
					}
				}
			}
		} catch (Exception e) {
			throw new ServiceException("生成swf文件失败！");
		}
	}
	/**
	 * 拷贝资源文件
	 * @param resOrder 需求单对象
	 * @param caList 资源明细集合
	 * @param map  Map<resid, List<file>>形式的对象
	 * @param publishDir 拷贝的根目录
	 */
	private void publishResources(ResOrder resOrder,String posttype, CaList caList,Map<String, List<String>> map,String publishDir,ResRelease resRelease) {
		try {
			if(caList!=null){
				List<Ca> calListStr = caList.getCas();
				List<String> list = new ArrayList<String>();
				List<ResReleaseDetail> resReleaseDetails = getResReleaseService().getResReleaseDetailByCnodition(resRelease.getId());
				for (ResReleaseDetail resReleaseDetail : resReleaseDetails) {
					list.add(resReleaseDetail.getResId());
				}
				if(calListStr != null && calListStr.size() > 0){
					for(Ca ca : calListStr){
						if(!list.contains(ca.getObjectId())){
						//资源拷贝的路径
						//获取每个资源对象下已选取的文件列表
						List<FileBo> fileList = ProcessUtil.getResIdList(ca, map);
						
						int num = 0;//计数器
						
						if(fileList != null && fileList.size() > 0){
							
							//遍历对文件的拷贝处理
							for (FileBo fileBo : fileList) {
								
								//获取源文件的绝对地址
								String srcFilePath = fileRoot + fileBo.getFileRealPath();
								//获取拷贝后的文件绝对地址
								String filePath = fileBo.getId().replaceAll("\\\\", "\\/");
								filePath = filePath.substring(0, filePath.lastIndexOf("/")+1);
								filePath += fileBo.getFileName();
								String publishBaseDir = publishDir + fileBo.getResId().substring(4) + "/" + filePath;
								ResFileRelation rf =  getResOrderService().queryFileByfileId(resOrder.getOrderId(),fileBo.getFileId(),posttype);
								if(rf != null){
									try {
										File resFile = new File(srcFilePath);
										if(resFile.exists()){
											String fileEncode = System.getProperty("file.encoding");
											File newfile = new File(new String(publishBaseDir.getBytes("UTF-8"), fileEncode));
											FileUtils.copyFile(resFile, newfile);
											
											//FileUtils.copyFile(resFile, new File(publishBaseDir));
											rf.setProcessStatus(ResReleaseConstant.FileProcessStatus.FILE_PROCESS_SUCCESS);
											rf.setProcessRemark("文件拷贝成功。");
											num++;
										}else{
											rf.setProcessStatus(ResReleaseConstant.FileProcessStatus.FILE_PROCESS_FAILED);
											rf.setProcessRemark("文件拷贝失败:文件不存在");
											throw new ServiceException("文件拷贝失败:文件不存在！");
										}
									} catch (Exception e) {
										rf.setProcessStatus(ResReleaseConstant.FileProcessStatus.FILE_PROCESS_FAILED);
										rf.setProcessRemark("文件拷贝失败:拷贝文件异常");
										throw new ServiceException("文件拷贝异常！"); 
									}
									getResOrderService().update(rf);
								}
							}
						}
						
						//写记录到发布明细表中，并更新发布明细表中的资源加工状态
						ResReleaseDetail  detail = new ResReleaseDetail();
						Long releaseId = resRelease.getId();
						detail.setReleaseId(releaseId);
						detail.setCreateTime(new Date());
						detail.setResId(ca.getObjectId());
						String channelName = resOrder.getChannelName();
						String resType = resOrder.getTemplate().getType();
						detail.setChannelName(channelName);
						detail.setResType(resType);
						if(fileList == null || (fileList != null && fileList.size()  == num)){
							detail.setProcessFileSuccess(ResReleaseConstant.FileProcessStatus.FILE_PROCESS_SUCCESS);
							detail.setRemark("加工成功");
						}else{
							detail.setProcessFileSuccess(ResReleaseConstant.FileProcessStatus.FILE_PROCESS_FAILED);
							detail.setRemark("加工失败");
							throw new ServiceException("加工失败");
						}
						getResReleaseService().saveOrUpdate(detail);
					}
						if(list.contains(ca.getObjectId())){
							//资源拷贝的路径
							//获取每个资源对象下已选取的文件列表
							List<FileBo> fileList = ProcessUtil.getResIdList(ca, map);
							
							int num = 0;//计数器
							
							if(fileList != null && fileList.size() > 0){
								//遍历对文件的拷贝处理
								for (FileBo fileBo : fileList) {
									//获取源文件的绝对地址
									String srcFilePath = fileRoot + fileBo.getFileRealPath();
									//获取拷贝后的文件绝对地址
									String filePath = fileBo.getId().replaceAll("\\\\", "\\/");
									filePath = filePath.substring(0, filePath.lastIndexOf("/")+1);
									filePath += fileBo.getFileName();
									String publishBaseDir = publishDir + fileBo.getResId().substring(4) + "/" + filePath;
									ResFileRelation rf =  getResOrderService().queryFileByfileId(resOrder.getOrderId(),fileBo.getFileId(),posttype);
									if(rf != null){
										if(rf.getProcessStatus()!=ResReleaseConstant.FileProcessStatus.FILE_PROCESS_SUCCESS){
										try {
											File resFile = new File(srcFilePath);
											if(resFile.exists()){
												String fileEncode = System.getProperty("file.encoding");
												File newfile = new File(new String(publishBaseDir.getBytes("UTF-8"), fileEncode));
												FileUtils.copyFile(resFile, newfile);
												
												//FileUtils.copyFile(resFile, new File(publishBaseDir));
												rf.setProcessStatus(ResReleaseConstant.FileProcessStatus.FILE_PROCESS_SUCCESS);
												rf.setProcessRemark("文件拷贝成功。");
												num++;
											}else{
												rf.setProcessStatus(ResReleaseConstant.FileProcessStatus.FILE_PROCESS_FAILED);
												rf.setProcessRemark("文件拷贝失败:文件不存在");
												throw new ServiceException("文件拷贝失败:文件不存在！");
											}
										} catch (Exception e) {
											rf.setProcessStatus(ResReleaseConstant.FileProcessStatus.FILE_PROCESS_FAILED);
											rf.setProcessRemark("文件拷贝失败:拷贝文件异常");
											throw new ServiceException("文件拷贝异常！"); 
										}
										getResOrderService().update(rf);
									}
									}
								}
							}
							
						}
				}
				}
			}
		} catch (Exception e) {
			throw new ServiceException("线下拷贝文件失败！--->"+e.getMessage());
		}
	}
	
	
	
	
	//需求单生成json
	private ResOrder createMetadataJson(ResOrder resOrder,List<ResOrderDetail> list,ProdParamsTemplate template,CaList caList,String publishDir){
		try {
			String remarks = resOrder.getRemark();
			//定义Json实体为JSONObject
			JSONObject jsonObjects = new JSONObject();
			//x代表需求单，z代表主题库
			jsonObjects.put("xreleaseId", resOrder.getOrderId().toString());
			jsonObjects.put("totalNum",String.valueOf(list.size()));//查子表的资源数
			//多种资源，故用Array集合
			JSONArray jsonArray = new JSONArray();
			String objectId = resOrder.getOrderId().toString();
			for (int i = 0;i < list.size();i++) {
				HttpClientUtil http = new HttpClientUtil();
				String resource = http.executeGet(PUBLISH_DETAIL_URL + "?id=" + list.get(i).getResId());
				if(resource!=null){
					//拼写json串,每一种资源元数据，故用JSONObject实体
					JSONObject zobject= postJson(resource,objectId,list.get(i).getResId().substring(4));
					//统一放入集合
					jsonArray.add(i, zobject);
				}
			}
			//再把集合让入总实体
			jsonObjects.put("resList", jsonArray);
			//生成文件，并放在指定目录
			String paths = publishDir+"releaseInfo.json";
			File file = new File(paths);
			if(!file.exists()){
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			byte[] b = null;
			try {
				b = jsonObjects.toString().getBytes("UTF-8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			FileOutputStream outputStream = null;
			boolean fal = true;
			try {
				outputStream = new FileOutputStream(file);
				outputStream.write(b, 0, b.length);
				outputStream.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				fal = false;
			} catch (IOException e) {
				e.printStackTrace();
				fal = false;
			}
			//打日志
			if(fal){
				resOrder.setJsonOkStatus(ResReleaseConstant.FileProcessStatus.FILE_PROCESS_SUCCESS);
				if(StringUtils.isNotBlank(remarks)){
					resOrder.setRemark(remarks + ";生成资源json文件成功。");
				}else{
					resOrder.setRemark("生成资源json文件成功。");
				}
			}else{
				resOrder.setJsonOkStatus(ResReleaseConstant.FileProcessStatus.FILE_PROCESS_FAILED);
				if(StringUtils.isNotBlank(remarks)){
					resOrder.setRemark(remarks + ";生成资源json文件失败。");
				}else{
					resOrder.setRemark("生成资源json文件失败。");
				}
			}
			getResOrderService().update(resOrder);
			if(!fal){
				throw new ServiceException("生成资源json文件失败。");
			}
		} catch (Exception e) {
			throw new ServiceException("生成资源json文件失败--->"+e.getMessage());
		}
		return resOrder;
	}
	//主题库生成json
	private SubjectStore createMetadataJsonSubject(SubjectStore subjectStore,List<ResOrderDetail> list,ProdParamsTemplate template,CaList caList,String publishDir){
		try {
			String remarks = subjectStore.getRemark();
			//定义Json实体为JSONObject
			JSONObject jsonObjects = new JSONObject();
			//x代表需求单，z代表主题库
			jsonObjects.put("zreleaseId", subjectStore.getId().toString());
			jsonObjects.put("totalNum",String.valueOf(list.size()));//查子表的资源数
			jsonObjects.put("name", subjectStore.getName());
			jsonObjects.put("nameEn", subjectStore.getNameEn());
			jsonObjects.put("logo", subjectStore.getId()+"/"+subjectStore.getLogo().substring(subjectStore.getLogo().lastIndexOf("/")+1, subjectStore.getLogo().length()));
			jsonObjects.put("trade", GlobalDataCacheMap.getNameValueWithNameByKeyAndChildKey("trade", subjectStore.getTrade().toString()));
			jsonObjects.put("subject",GlobalDataCacheMap.getNameValueWithNameByKeyAndChildKey("subject", subjectStore.getSubject().toString()));
			jsonObjects.put("bookman",GlobalDataCacheMap.getNameValueWithNameByKeyAndChildKey("presshose", subjectStore.getBookman().toString()));
			jsonObjects.put("storeType",GlobalDataCacheMap.getNameValueWithNameByKeyAndChildKey("kuCategory", subjectStore.getStoreType().toString()));
			jsonObjects.put("audience",GlobalDataCacheMap.getNameValueWithNameByKeyAndChildKey("audience", subjectStore.getAudience().toString()));
			jsonObjects.put("keyword", subjectStore.getKeyword());
			jsonObjects.put("collectionStart", String.valueOf(subjectStore.getCollectionStart()));
			jsonObjects.put("collectionEnd", String.valueOf(subjectStore.getCollectionEnd()));
			jsonObjects.put("language", GlobalDataCacheMap.getNameValueWithNameByKeyAndChildKey("relation_language", subjectStore.getLanguage().toString()));
			jsonObjects.put("synopsis", subjectStore.getSynopsis());
			jsonObjects.put("subLibclassify", GlobalDataCacheMap.getNameValueWithNameByKeyAndChildKey("ZTKtypes", subjectStore.getSubLibclassify().toString()));
			//多种资源，故用Array集合
			JSONArray jsonArray = new JSONArray();
			String objectId = subjectStore.getId().toString();
			for (int i = 0;i < list.size();i++) {
				HttpClientUtil http = new HttpClientUtil();
				String resource = http.executeGet(PUBLISH_DETAIL_URL + "?id=" + list.get(i).getResId());
				if(resource!=null){
					//拼写json串,每一种资源元数据，故用JSONObject实体
					JSONObject zobject= postJson(resource,objectId,list.get(i).getResId().substring(4));
					//统一放入集合
					jsonArray.add(i, zobject);
				}
			}
			//再把集合让入总实体
			jsonObjects.put("resList", jsonArray);
			//生成文件，并放在指定目录
			String paths = publishDir+"releaseInfo.json";
			File file = new File(paths);
			if(!file.exists()){
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			byte[] b = null;
			try {
				b = jsonObjects.toString().getBytes("UTF-8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			FileOutputStream outputStream = null;
			boolean fal = true;
			try {
				outputStream = new FileOutputStream(file);
				outputStream.write(b, 0, b.length);
				outputStream.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				fal = false;
			} catch (IOException e) {
				e.printStackTrace();
				fal = false;
			}
			//打日志
			if(fal){
				subjectStore.setJsonOkStatus(ResReleaseConstant.FileProcessStatus.FILE_PROCESS_SUCCESS);
				if(StringUtils.isNotBlank(remarks)){
					subjectStore.setRemark(remarks + ";生成资源json文件成功。");
				}else{
					subjectStore.setRemark("生成资源json文件成功。");
				}
			}else{
				subjectStore.setJsonOkStatus(ResReleaseConstant.FileProcessStatus.FILE_PROCESS_FAILED);
				if(StringUtils.isNotBlank(remarks)){
					subjectStore.setRemark(remarks + ";生成资源json文件失败。");
				}else{
					subjectStore.setRemark("生成资源json文件失败。");
				}
			}
			getResOrderService().update(subjectStore);
			if(!fal){
				throw new ServiceException("生成资源json文件失败!");
			}
		} catch (Exception e) {
			throw new ServiceException("生成资源json文件失败!--->"+e.getMessage());
		}
		return subjectStore;
	}
	
	//解析json串
	private JSONObject postJson(String jsons,String objectId,String resId){
		try {
			Gson gson= new Gson();
			Ca oldCa = gson.fromJson(jsons, Ca.class);
			JSONObject object = JSONObject.fromObject(jsons);
			String objectid = object.getString("objectId");
			JSONObject zobject = new JSONObject();
			zobject.put("objectId", objectid);
			JSONObject metadataMap=object.getJSONObject("metadataMap");
			Iterator iterator= metadataMap.keys();
			while (iterator.hasNext()) {
				String  keys = (String) iterator.next();
				if(keys.equals("importCoverType")){
					continue;
				}
				if(keys.equals("type")){
					continue;
				}
				MetadataDefinition metadataDefinitions = MetadataSupport.getMetadataDefinitionByName(keys);
				if(metadataDefinitions!=null){
					String value= metadataMap.getString(keys);
					if(metadataDefinitions.getFieldType().toString().equals("1")){
						//元数据输入类型为1，是文本，直接取值
						zobject.put(keys, value);
					}else if(metadataDefinitions.getFieldType().toString().equals("2")){
						//元数据输入类型为2，是下拉选择，是单选，转换一次直接取值
						String valuerange =  metadataDefinitions.getValueRange();
						String valueName = "";
						valueName =  GlobalDataCacheMap.getNameValueWithIdByIdAndChildValue(valuerange, value);
						if(StringUtils.isBlank(valueName)){
							valueName = GlobalDataCacheMap.getNameValueWithNameByKeyAndChildKey(valuerange, value);
						}
						zobject.put(keys, valueName);
					}else if(metadataDefinitions.getFieldType().toString().equals("3")){
						//元数据输入类型为3，是多选，循环取值
						String valuerange =  metadataDefinitions.getValueRange();
						String key[] = value.split(",");
						String valueName = "";
						for (int j = 0; j < key.length; j++) {
							String name = "";
							name = GlobalDataCacheMap.getNameValueWithIdByIdAndChildValue(valuerange,key[j].toString());
							if(StringUtils.isBlank(name)){
								name = GlobalDataCacheMap.getNameValueWithNameByKeyAndChildKey(valuerange, key[j].toString());
							}
							valueName +=  name+",";
						}
						if(valueName.length()>0){
							valueName = valueName.substring(0, valueName.length()-1);
						}
						zobject.put(keys, valueName);
					}else if(metadataDefinitions.getFieldType().toString().equals("4")){
						//元数据输入类型为4，是单选，转换一次直接取值
						String valuerange =  metadataDefinitions.getValueRange();
						String valueName = "";
						valueName =  GlobalDataCacheMap.getNameValueWithIdByIdAndChildValue(valuerange, value);
						if(StringUtils.isBlank(valueName)){
							valueName = GlobalDataCacheMap.getNameValueWithNameByKeyAndChildKey(valuerange, value);
						}
						zobject.put(keys, valueName);
					}else if(metadataDefinitions.getFieldType().toString().equals("5")){
						//元数据输入类型为5，是文本域，直接取值
						zobject.put(keys, value);
					}else if(metadataDefinitions.getFieldType().toString().equals("6")){
						//元数据输入类型为6，是树，
						try {
							IFLTXService service = (IFLTXService)BeanFactoryUtil.getBean("FLTXService");
							String code = service.queryCatagoryCode(value);
							zobject.put(keys, code);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}else if(metadataDefinitions.getFieldType().toString().equals("7")){
						//元数据输入类型为7，是日期，直接取值
						if(StringUtils.isNotBlank(value)){
							Date dateOld = new Date(Long.parseLong(value));
							value = new SimpleDateFormat(metadataDefinitions.getValueRange()).format(dateOld);
							zobject.put(keys, value);
						}
					}else if(metadataDefinitions.getFieldType().toString().equals("8")){
						//元数据输入类型为8，是URL，直接取值
						//暂未处理
					}else if(metadataDefinitions.getFieldType().toString().equals("10")){
						//元数据输入类型为10，是人员，
						ISubjectService subjectService = getSubjectService();
					    String values[]= value.split(",");
					    String staffName = "";
				    	for (String names : values) {
				    		try {
				    			Staff staff = (Staff) subjectService.getByPk(Staff.class, Long.decode(names));
				    			staffName += staff.getName()+",";
							} catch (Exception e) {
								e.printStackTrace();
							}
					    	
						}
						if(staffName.length()>0){
							staffName = staffName.substring(0, staffName.length()-1);
						}
						zobject.put(keys, staffName);
						if(zobject.get("author")==null){
							if(keys.equals("bookAuthor")){
								if(StringUtils.isNotBlank(staffName)){
									zobject.remove("author");
									zobject.put("author", staffName);
								}
							}
						}
						if(zobject.get("author")==null){
							if(keys.equals("contributor")){
								if(StringUtils.isNotBlank(staffName)){
									zobject.remove("author");
									zobject.put("author", staffName);
								}
							}
						}
						if(zobject.get("author")==null){
							if(keys.equals("editor")){
								if(StringUtils.isNotBlank(staffName)){
									zobject.remove("author");
									zobject.put("author", staffName);
								}
							}
						}
						
					}else if(metadataDefinitions.getFieldType().toString().equals("11")){
						//元数据输入类型为11，是单位，直接查表
						ISubjectService subjectService = getSubjectService();
						String values[]= value.split(",");
					    String companyName = "";
				    	for (String names : values) {
				    		try {
				    			Company company = (Company) subjectService.getByPk(Company.class, Long.decode(names));
				    			companyName +=company.getName() +",";
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						if(companyName.length()>0){
							companyName = companyName.substring(0, companyName.length()-1);
						}
						zobject.put(keys, companyName);
					}
				}
			}
			List<com.brainsoon.semantic.ontology.model.File> files = oldCa.getRealFiles();
			/*String path ="";
			for (com.brainsoon.semantic.ontology.model.File file : files) {
				if (file.getIsDir().equals("1")) {
					String paths = file.getPath();
					path = paths.replace(oldCa.getRootPath().replaceAll("\\\\", "\\/"), "");
					logger.info(""+path);
					
					path = path.replaceAll("\\\\", "\\/");
					if(path.startsWith("/")){
						path = path.substring(1, path.length());
					}if(path.contains("/")){
						path = path.substring(0, path.indexOf("/"));
					}
					if(path.endsWith("/")){
						path = path.substring(0, path.length()-1);
					}
					break;
				}
			}*/
			zobject.put("resPath", objectId+"/"+resId+"/");
			return zobject;
		} catch (Exception e) {
			throw new ServiceException("json串解析失败！");
		}
	}
	
	/**
	 * 线上
	 * 拷贝资源文件
	 * @param resOrder 需求单对象
	 * @param caList 资源明细集合
	 * @param map  Map<resid, List<file>>形式的对象
	 * @param publishDir 拷贝的根目录
	 */
	private void publishResourcesResorderonline(ResOrder resOrder,String posttype, CaList caList,Map<String, List<String>> map,String publishDir,ResRelease resRelease) {
		try {
			if(caList!=null){
				List<Ca> calListStr = caList.getCas();
				if(calListStr != null && calListStr.size() > 0){
					for(Ca ca : calListStr){
						//资源拷贝的路径
						//获取每个资源对象下已选取的文件列表
						List<FileBo> fileList = ProcessUtil.getResIdList(ca, map);
						
						if(fileList != null && fileList.size() > 0){
							
							//遍历对文件的拷贝处理
							for (FileBo fileBo : fileList) {
								
								//获取源文件的绝对地址
								String srcFilePath = fileRoot + fileBo.getFileRealPath();
								//获取拷贝后的文件绝对地址
								String filePath = fileBo.getId().replaceAll("\\\\", "\\/");
								String paths = publishDir+fileBo.getResId().replaceAll("\\\\", "\\/").substring(4);
								File file = new File(paths);
								if(!file.exists()){
									file.mkdirs();
								}
								if(filePath.substring(filePath.lastIndexOf("/")+1).toLowerCase().contains(".pdf")){
									if(fileBo.getFileName().toLowerCase().contains("h") || fileBo.getFileName().contains("印刷")){
										paths = paths+"/pdf/H/"+fileBo.getFileName();
									}
									if(fileBo.getFileName().toLowerCase().contains("l") || fileBo.getFileName().contains("宣传")){
										paths = paths+"/pdf/L/"+fileBo.getFileName();
									}
									
								}else if(filePath.substring(filePath.lastIndexOf("/")+1).toLowerCase().contains("cover") || filePath.substring(filePath.lastIndexOf("/")+1).toLowerCase().contains("epub")){
									paths = paths+"/"+fileBo.getFileName();
								}
								ResFileRelation rf =  getResOrderService().queryFileByfileId(resOrder.getOrderId(),fileBo.getFileId(),posttype);
								if(rf != null){
									if(rf.getProcessStatus()!=ResReleaseConstant.FileProcessStatus.FILE_PROCESS_SUCCESS){
										try {
											File resFile = new File(srcFilePath);
											if(resFile.exists()){
												String fileEncode = System.getProperty("file.encoding");
												File newfile = new File(new String(paths.getBytes("UTF-8"), fileEncode));
												FileUtils.copyFile(resFile, newfile);
												//FileUtils.copyFile(resFile, new File(paths));
												rf.setProcessStatus(ResReleaseConstant.FileProcessStatus.FILE_PROCESS_SUCCESS);
												rf.setProcessRemark("文件拷贝成功。");
											}else{
												rf.setProcessStatus(ResReleaseConstant.FileProcessStatus.FILE_PROCESS_FAILED);
												rf.setProcessRemark("文件拷贝失败:文件不存在");
												throw new ServiceException("文件拷贝失败:文件不存在！");
											}
										} catch (Exception e) {
											rf.setProcessStatus(ResReleaseConstant.FileProcessStatus.FILE_PROCESS_FAILED);
											rf.setProcessRemark("文件拷贝失败:拷贝文件异常");
											throw new ServiceException("文件拷贝异常！");
										}
										getResOrderService().update(rf);
									}
									
								}
							}
						}
						
						
					}
				}
			}
		} catch (Exception e) {
			throw new ServiceException("文件拷贝失败---->"+e.getMessage());
		}
	}
	/**
	 * 线上
	 * 拷贝资源文件
	 * @param resOrder 需求单对象
	 * @param caList 资源明细集合
	 * @param map  Map<resid, List<file>>形式的对象
	 * @param publishDir 拷贝的根目录
	 */
	private void publishResourcesSubjectonline(SubjectStore subjectStore,String posttype, CaList caList,Map<String, List<String>> map,String publishDir,ResRelease resRelease) {
		try {
			if(caList!=null){
				List<Ca> calListStr = caList.getCas();
				if(calListStr != null && calListStr.size() > 0){
					for(Ca ca : calListStr){
						//资源拷贝的路径
						//获取每个资源对象下已选取的文件列表
						List<FileBo> fileList = ProcessUtil.getResIdList(ca, map);
						
						if(fileList != null && fileList.size() > 0){
							
							//遍历对文件的拷贝处理
							for (FileBo fileBo : fileList) {
								
								//获取源文件的绝对地址
								String srcFilePath = fileRoot + fileBo.getFileRealPath();
								//获取拷贝后的文件绝对地址
								String filePath = fileBo.getId().replaceAll("\\\\", "\\/");
								String paths = publishDir+fileBo.getResId().replaceAll("\\\\", "\\/").substring(4);
								File file = new File(paths);
								if(!file.exists()){
									file.mkdirs();
								}
								if(filePath.substring(filePath.lastIndexOf("/")+1).toLowerCase().contains(".pdf")){
									if(fileBo.getFileName().toLowerCase().contains("h") || fileBo.getFileName().contains("印刷")){
										paths = paths+"/pdf"+"/H/"+fileBo.getFileName();
									}
									if(fileBo.getFileName().toLowerCase().contains("l") || fileBo.getFileName().contains("宣传")){
										paths = paths+"/pdf"+"/L/"+fileBo.getFileName();
									}
									
								}else if(filePath.substring(filePath.lastIndexOf("/")+1).toLowerCase().contains("cover") || filePath.substring(filePath.lastIndexOf("/")+1).toLowerCase().contains("epub")){
									paths = paths+"/"+fileBo.getFileName();
								}
								ResFileRelation rf =  getResOrderService().queryFileByfileId(subjectStore.getId(),fileBo.getFileId(),posttype);
								if(rf != null){
									if(rf.getProcessStatus()!=ResReleaseConstant.FileProcessStatus.FILE_PROCESS_SUCCESS){
										try {
											File resFile = new File(srcFilePath);
											if(resFile.exists()){
												String fileEncode = System.getProperty("file.encoding");
												File newfile = new File(new String(paths.getBytes("UTF-8"), fileEncode));
												FileUtils.copyFile(resFile, newfile);
												rf.setProcessStatus(ResReleaseConstant.FileProcessStatus.FILE_PROCESS_SUCCESS);
												rf.setProcessRemark("文件拷贝成功。");
											}else{
												rf.setProcessStatus(ResReleaseConstant.FileProcessStatus.FILE_PROCESS_FAILED);
												rf.setProcessRemark("文件拷贝失败:文件不存在");
												throw new ServiceException("文件拷贝失败:文件不存在！");
											}
										} catch (Exception e) {
											rf.setProcessStatus(ResReleaseConstant.FileProcessStatus.FILE_PROCESS_FAILED);
											rf.setProcessRemark("文件拷贝失败:拷贝文件异常");
											throw new ServiceException("文件拷贝异常！");
										}
										getResOrderService().update(rf);
									}
									
								}
							}
						}
						
						
					}
				}
			}
		} catch (Exception e) {
			throw new ServiceException("资源文件拷贝失败！--->"+e.getMessage());
		}
	}
	
	
	/**
	 * 资源文件添加水印
	 * @param resOrder
	 * @param publishDir 发布资源根路径
	 */
	public Map<String,String> addWaterMark(String objectId,String posttype,ProdParamsTemplate template,String publishDir,CaList caList,Map<String, List<String>> map){
		Map<String,String> fileMap =  new HashMap<String,String>();
		try {
			//获取每个资源对象下已选取的文件列表
			if(caList!=null){
				if(template!=null){
					IPublishTempService publishTempService = (IPublishTempService)BeanFactoryUtil.getBean("publishTempService");
					ParamsTempEntity paramsTempEntity = publishTempService.convertEntity(template);
					if(paramsTempEntity != null){ //只有水印参数都存在的情况下才会添加水印，否则不添加
						String waterFiles = paramsTempEntity.getWaterMarkFileType(); //加水印文件类型(可多选) ：image、video、text
						String markType = paramsTempEntity.getWaterMarkType(); //水印类型(二选一)：图片 、文字 
						//第一步：判断是否给视频、图片、文本（pdf）加水印
						if(StringUtils.isNotBlank(waterFiles) && StringUtils.isNotEmpty(markType)){
							List<Ca> calListStr = caList.getCas();
							if(calListStr != null && calListStr.size() > 0){
								for(Ca ca : calListStr){
								List<FileBo> fileList = ProcessUtil.getResIdList(ca, map);
									if(fileList != null && fileList.size() > 0){
										for (FileBo fileBo : fileList) {
											
											//===========获取参数值=============================
											
											//获取拷贝后的文件绝对地址
											String filePath = fileBo.getId().replaceAll("\\\\", "\\/");
											filePath = filePath.substring(0, filePath.lastIndexOf("/")+1);
											filePath += fileBo.getFileName();
											String srcPath = (publishDir + fileBo.getResId().substring(4) + "/" + filePath).replaceAll("\\\\", "\\/"); //源文件绝对路径
											//文件objectid
											String fileId = fileBo.getFileId(); 
											//查询对象
											ResFileRelation rf =  getResOrderService().queryFileByfileId(Long.decode(objectId),fileBo.getFileId(),posttype);
											//获取文件扩展名
											String fileType = DoFileUtils.getExtensionName(srcPath);
											//文件名称
											String fileName = DoFileUtils.getFileNameNoEx(srcPath); //获取不带扩展名的文件名
											String fileParentPath = new File(srcPath).getParent().replaceAll("\\\\", "\\/"); //获取父目录
											String fileNewName = fileName + "_1" ; //新资源名称
											String targetPath = fileParentPath + "/" + fileNewName + "." + fileType; //临时的目标文件绝对路径
											//最终目标文件路径名
											String targetFilePath = fileParentPath + "/" + fileName + "." + fileType; 
											
											//水印位置
											String positionStr = paramsTempEntity.getImgWaterMarkPos(); 
											int position = 0; //默认左上角
											if(StringUtils.isNotBlank(positionStr)){
												position = Integer.parseInt(positionStr); 
											}
											
											//水印的透明度
											String markOpacity = paramsTempEntity.getWaterMarkOpacity();
											
											//掉用加水印方法加水印
											if(DoFileUtils.checkArrContainsSoStr(pictureFormat,fileType) && waterFiles.contains("image")){
												
												//要处理的图片文件格式
												String destFileType = paramsTempEntity.getImgType();
												if(StringUtils.isNotBlank(destFileType)){
													destFileType = SysParamsTemplateConstants.ImgFormat.getValueByKey(destFileType);
													if(!fileType.equals(destFileType)){//主要针对的是图片
														targetPath =  fileParentPath + "/" + fileNewName + "." + destFileType;
													}
												}
												
												//最终目标文件路径名
												if(StringUtils.isNotBlank(destFileType)){
													targetFilePath = fileParentPath + "/" + fileName + "." + destFileType; 
												}
												
												fileMap = doImage(paramsTempEntity, fileMap, markType, srcPath, rf, targetPath, position, markOpacity, targetFilePath, fileId);
										    
											}else if(DoFileUtils.checkArrContainsSoStr(videoFormat,fileType)  && waterFiles.contains("video")){
										    	
												//要处理的视频文件格式
												String destFileType = paramsTempEntity.getVideoType();
												if(StringUtils.isNotBlank(destFileType)){
													destFileType = SysParamsTemplateConstants.VideoFormat.getValueByKey(destFileType);
													if(!fileType.equals(destFileType)){//主要针对的是图片
														targetPath =  fileParentPath + "/" + fileNewName + "." + destFileType;
													}
												}
												
												//最终目标文件路径名
												if(StringUtils.isNotBlank(destFileType)){
													targetFilePath = fileParentPath + "/" + fileName + "." + destFileType; 
												}
												
												fileMap = doVideo(waterFiles, paramsTempEntity, fileMap, markType, srcPath, rf, targetPath, position, markOpacity, targetFilePath, fileId);
										   
											}else if(DoFileUtils.checkArrContainsSoStr("pdf",fileType)  && waterFiles.contains("text")){
										    	
												fileMap = doTxtOfPdf(paramsTempEntity, fileMap, markType, srcPath, rf, targetPath, position, markOpacity, targetFilePath, fileId);
									       
											} 
									 }
								  }
							  }
						  }
					  }
				  }
		   	   }
		   }
		} catch (Exception e) {
			throw new ServiceException("资源文件加工添加水印失败！");
		}
		return fileMap;
	}
	/**
	 * 资源文件添加水印
	 * @param resOrder
	 * @param publishDir 发布资源根路径
	 */
	public Map<String,String> addWaterMarkonline(String objectId,String posttype,ProdParamsTemplate template,String publishDir,CaList caList,Map<String, List<String>> map,ResRelease resRelease){
		Map<String,String> fileMap =  new HashMap<String,String>();
		try {
			//获取每个资源对象下已选取的文件列表
			if(caList!=null){
				if(template!=null){
					List<String> list = new ArrayList<String>();
					List<ResReleaseDetail> resReleaseDetails = getResReleaseService().getResReleaseDetailByCnodition(resRelease.getId());
					for (ResReleaseDetail resReleaseDetail : resReleaseDetails) {
						list.add(resReleaseDetail.getResId());
					}
					IPublishTempService publishTempService = (IPublishTempService)BeanFactoryUtil.getBean("publishTempService");
					ParamsTempEntity paramsTempEntity = publishTempService.convertEntity(template);
					if(paramsTempEntity != null){ //只有水印参数都存在的情况下才会添加水印，否则不添加
						String waterFiles = paramsTempEntity.getWaterMarkFileType(); //加水印文件类型(可多选) ：image、video、text
						String markType = paramsTempEntity.getWaterMarkType(); //水印类型(二选一)：图片 、文字 
						//第一步：判断是否给视频、图片、文本（pdf）加水印
						if(StringUtils.isNotBlank(waterFiles) && StringUtils.isNotEmpty(markType)){
							List<Ca> calListStr = caList.getCas();
							if(calListStr != null && calListStr.size() > 0){
								for(Ca ca : calListStr){
								if(!list.contains(ca.getObjectId())){
								List<FileBo> fileList = ProcessUtil.getResIdList(ca, map);
									if(fileList != null && fileList.size() > 0){
										for (FileBo fileBo : fileList) {
											
											//===========获取参数值=============================
											
											//获取拷贝后的文件绝对地址
											//String filePath = fileBo.getId().replaceAll("\\\\", "\\/");
											//filePath = filePath.substring(0, filePath.lastIndexOf("/")+1);
											//filePath += fileBo.getFileName();
											//String srcPath = (publishDir + fileBo.getResName() + "/" + filePath).replaceAll("\\\\", "\\/"); //源文件绝对路径
											String filePath = fileBo.getId().replaceAll("\\\\", "\\/");
											String paths = publishDir+fileBo.getResId().replaceAll("\\\\", "\\/").substring(4);
											if(filePath.substring(filePath.lastIndexOf("/")+1).toLowerCase().contains(".pdf")){
												//filePath = filePath.substring(filePath.indexOf("/"), filePath.lastIndexOf("/"));
												if(fileBo.getFileName().toLowerCase().contains("h") || fileBo.getFileName().contains("印刷")){
													paths = paths+"/pdf/H/"+fileBo.getFileName();
												}
												
												if(fileBo.getFileName().toLowerCase().contains("l") || fileBo.getFileName().contains("宣传")){
													paths = paths+"/pdf/L/"+fileBo.getFileName();
												}
												
											}else if(filePath.substring(filePath.lastIndexOf("/")+1).toLowerCase().contains("cover") || filePath.substring(filePath.lastIndexOf("/")+1).toLowerCase().contains(".epub")){
												paths = paths+"/"+fileBo.getFileName();
											}
											
											//文件objectid
											String fileId = fileBo.getFileId(); 
											//查询对象
											ResFileRelation rf =  getResOrderService().queryFileByfileId(Long.decode(objectId),fileBo.getFileId(),posttype);
											//获取文件扩展名
											String fileType = DoFileUtils.getExtensionName(paths);
											//文件名称
											String fileName = DoFileUtils.getFileNameNoEx(paths); //获取不带扩展名的文件名
											String fileParentPath = new File(paths).getParent().replaceAll("\\\\", "\\/"); //获取父目录
											String fileNewName = fileName + "_1" ; //新资源名称
											String targetPath = fileParentPath + "/" + fileNewName + "." + fileType; //临时的目标文件绝对路径
											//最终目标文件路径名
											String targetFilePath = fileParentPath + "/" + fileName + "." + fileType; 
											
											//水印位置
											String positionStr = paramsTempEntity.getImgWaterMarkPos(); 
											int position = 0; //默认左上角
											if(StringUtils.isNotBlank(positionStr)){
												position = Integer.parseInt(positionStr); 
											}
											
											//水印的透明度
											String markOpacity = paramsTempEntity.getWaterMarkOpacity();
											
											//掉用加水印方法加水印
											if(DoFileUtils.checkArrContainsSoStr(pictureFormat,fileType) && waterFiles.contains("image")){
												
												//要处理的图片文件格式
												String destFileType = paramsTempEntity.getImgType();
												if(StringUtils.isNotBlank(destFileType)){
													destFileType = SysParamsTemplateConstants.ImgFormat.getValueByKey(destFileType);
													if(!fileType.equals(destFileType)){//主要针对的是图片
														targetPath =  fileParentPath + "/" + fileNewName + "." + destFileType;
													}
												}
												
												//最终目标文件路径名
												if(StringUtils.isNotBlank(destFileType)){
													targetFilePath = fileParentPath + "/" + fileName + "." + destFileType; 
												}
												
												fileMap = doImage(paramsTempEntity, fileMap, markType, paths, rf, targetPath, position, markOpacity, targetFilePath, fileId);
										    
											}else if(DoFileUtils.checkArrContainsSoStr(videoFormat,fileType)  && waterFiles.contains("video")){
										    	
												//要处理的视频文件格式
												String destFileType = paramsTempEntity.getVideoType();
												if(StringUtils.isNotBlank(destFileType)){
													destFileType = SysParamsTemplateConstants.VideoFormat.getValueByKey(destFileType);
													if(!fileType.equals(destFileType)){//主要针对的是图片
														targetPath =  fileParentPath + "/" + fileNewName + "." + destFileType;
													}
												}
												
												//最终目标文件路径名
												if(StringUtils.isNotBlank(destFileType)){
													targetFilePath = fileParentPath + "/" + fileName + "." + destFileType; 
												}
												
												fileMap = doVideo(waterFiles, paramsTempEntity, fileMap, markType, paths, rf, targetPath, position, markOpacity, targetFilePath, fileId);
										   
											}else if(DoFileUtils.checkArrContainsSoStr("pdf",fileType)  && waterFiles.contains("text")){
										    	
												fileMap = doTxtOfPdf(paramsTempEntity, fileMap, markType, paths, rf, targetPath, position, markOpacity, targetFilePath, fileId);
									 
											} 
									 }
								  }
							  }
							}
						  }
					  }
				  }
		   	   }
		   }
		} catch (Exception e) {
			throw new ServiceException("资源文件加工添加水印失败！");
		}
		return fileMap;
	}
	
	
	/**
	 * 处理图片加水印
	 * @param paramsTempEntity
	 * @param fileMap  返回file map对象
	 * @param markType  水印类型
	 * @param srcPath 源文件路径
	 * @param rf 
	 * @param targetPath 目标文件路径（临时）
	 * @param position 水印位置
	 * @param markOpacity 水印透明度
	 * @param targetFilePath 最终的文件路径
	 * @return
	 */
	public Map<String,String> doImage(ParamsTempEntity paramsTempEntity,Map<String,String> fileMap,String markType,String srcPath,ResFileRelation rf,String targetPath,int position,String markOpacity,String targetFilePath,String fileId){
		try {
			
			boolean b = false; //判断水印是否添加成功
			
			String imgHeightStr = paramsTempEntity.getImgHeight();//处理后文件图片限高
			int imgHeight = 0;
			if(StringUtils.isNotBlank(imgHeightStr)){
				imgHeight = Integer.parseInt(imgHeightStr);
			}
			String imgWidthStr = paramsTempEntity.getImgWidth();//处理后文件图片限宽
			int imgWidth =0;
			if(StringUtils.isNotBlank(imgWidthStr)){
				imgWidth = Integer.parseInt(imgWidthStr);
			}
			
			if("图片".equals(markType)){//如果是图片水印
				
				try {
					String wmImg = WebAppUtils.getWebRootBaseDir(ConstantsDef.sysUpLoadFile) + paramsTempEntity.getImgWaterMarkURL(); //水印图片绝对路径
					if(!new File(wmImg).exists()){
						b = false;
					}else{
						//图像加图片水印
						b = ImgCoverUtil.conver2Other(srcPath, targetPath);//先转换，如果相同则拷贝
						if(b){
							ImageUtils.createNewImgByImg(wmImg, srcPath, targetPath, position, markOpacity, imgHeight,imgWidth);
							b = new File(targetPath).exists();
						}
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					b = false;
				} finally{
					fileMap = doLastStep(fileMap, srcPath, targetPath, rf, b, targetFilePath, fileId);
				}
			}else{//文字水印
				
				String markTxt = paramsTempEntity.getWaterMarkText();//加水印文字
				
				String isBoldStr = paramsTempEntity.getWaterMarkTextBold();//文字是否加粗
				boolean isBold = false;
				if(StringUtils.isNotBlank(isBoldStr) && isBoldStr.equals("01")){//01：是  02：否
					isBold = true;
				}
				
				String fontColorStr = paramsTempEntity.getWaterMarkColor();//文字颜色
				Color fontColor = null;
				if(StringUtils.isNotBlank(fontColorStr)){
					fontColor = ColorUtil.parseToColor(fontColorStr);//颜色编码 如：#012323
				}
				
				String fontSizeStr = paramsTempEntity.getWaterMarkTextSize();//文字大小
				int fontSize = 20; //默认20
				if(StringUtils.isNotBlank(fontSizeStr)){
					fontSize = ImageUtils.getFontSize(Integer.parseInt(fontSizeStr));// 01：大   02：中   03：小
				}
	
				String textFont = paramsTempEntity.getWaterMarkTextFont();//文字字体
				
				//图像加文本水印
				try {
					b = ImgCoverUtil.conver2Other(srcPath, targetPath);//先转换，如果相同则拷贝
					if(b){
					    ImageUtils.createNewImgByMark(markTxt, srcPath, targetPath, position,fontSize, fontColor, textFont, isBold, markOpacity, imgHeight,imgWidth);
						b = new File(targetPath).exists();
					}
				} catch (Exception e) {
					e.printStackTrace();
					b = false;
				} finally{
					//后更新记录
					fileMap = doLastStep(fileMap, srcPath, targetPath, rf, b, targetFilePath, fileId);
				}
		   }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileMap;
	}
	
	/**
	 * 处理视频加水印
	 * @param paramsTempEntity
	 * @param fileMap  返回file map对象
	 * @param markType  水印类型
	 * @param srcPath 源文件路径
	 * @param rf 
	 * @param targetPath 目标文件路径（临时）
	 * @param position 水印位置
	 * @param markOpacity 水印透明度
	 * @param targetFilePath 最终的文件路径
	 * @return
	 */
	public Map<String,String> doVideo(String waterFiles,ParamsTempEntity paramsTempEntity,Map<String,String> fileMap,String markType,String srcPath,ResFileRelation rf,String targetPath,int position,String markOpacity,String targetFilePath,String fileId){
		try {
			
			boolean b = false; //判断水印是否添加成功
			
			if("图片".equals(markType)){//如果是图片水印
				
				try {
					String wmImg = WebAppUtils.getWebRootBaseDir(ConstantsDef.sysUpLoadFile) + paramsTempEntity.getImgWaterMarkURL(); //水印图片绝对路径
					if(!new File(wmImg).exists()){
						b = false;
					}else{
						//视频加图片水印
						b = ConverUtils.processFfmpegWatermarkByImg(srcPath, targetPath, wmImg, position, markOpacity);
					}
				} catch (Exception e) {
					e.printStackTrace();
					b = false;
				} finally{
					fileMap = doLastStep(fileMap, srcPath, targetPath, rf, b, targetFilePath, fileId);
				}
				
			}else{//文字水印
				
				String markTxt = paramsTempEntity.getWaterMarkText();//加水印文字
				
				String isBoldStr = paramsTempEntity.getWaterMarkTextBold();//文字是否加粗
				boolean isBold = false;
				if(StringUtils.isNotBlank(isBoldStr) && isBoldStr.equals("01")){//01：是  02：否
					isBold = true;
				}
				
				String fontColorStr = paramsTempEntity.getWaterMarkColor();//文字颜色
				Color fontColor = null;
				if(StringUtils.isNotBlank(fontColorStr)){
					fontColor = ColorUtil.parseToColor(fontColorStr);//颜色编码 如：#012323
				}
				
				String fontSizeStr = paramsTempEntity.getWaterMarkTextSize();//文字大小
				int fontSize = 20; //默认20
				if(StringUtils.isNotBlank(fontSizeStr)){
					ImageUtils.getFontSize(Integer.parseInt(fontSizeStr));// 01：大   02：中   03：小
				}
				
				String textFont = paramsTempEntity.getWaterMarkTextFont();//文字字体
				
				try {
					//视频加文本水印
					b = ConverUtils.processFfmpegWatermkByFont(srcPath, targetPath, position, markOpacity, markTxt, fontSize, fontColor, isBold, textFont, "");
				} catch (Exception e) {
					e.printStackTrace();
					b = false;
				} finally{
					fileMap = doLastStep(fileMap, srcPath, targetPath, rf, b, targetFilePath, fileId);
				}
			 }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileMap;
	}
	
	
	/**
	 * 处理文本（pdf）
	 * @param paramsTempEntity
	 * @param fileMap  返回file map对象
	 * @param markType  水印类型
	 * @param srcPath 源文件路径
	 * @param rf 
	 * @param fileType 文件类型
	 * @param targetPath 目标文件路径（临时）
	 * @param position 水印位置
	 * @param markOpacity 水印透明度
	 * @param targetFilePath 最终的文件路径
	 * @return
	 */
	public Map<String,String> doTxtOfPdf(ParamsTempEntity paramsTempEntity,Map<String,String> fileMap,String markType,String srcPath,ResFileRelation rf,String targetPath,int position,String markOpacity,String targetFilePath,String fileId){
		try {
			boolean b = false; //判断水印是否添加成功
			if("图片".equals(markType)){//如果是图片水印
				try {
					String wmImg = WebAppUtils.getWebRootBaseDir(ConstantsDef.sysUpLoadFile) + paramsTempEntity.getImgWaterMarkURL(); //水印图片绝对路径
					if(!new File(wmImg).exists()){
						b = false;
					}else{
						//PDF加图片水印
						PdfUtil.addPdfImgMark(srcPath, targetPath, wmImg);
						b = new File(targetPath).exists();
					}
				} catch (Exception e) {
					e.printStackTrace();
					b = false;
				} finally{
					fileMap = doLastStep(fileMap, srcPath, targetPath, rf, b, targetFilePath, fileId);
				}	
			}else{//文字水印
				   String markTxt = paramsTempEntity.getWaterMarkText();//加水印文字
					try {
						//PDF加文本水印
						PdfUtil.addPdfTxtMark(srcPath, targetPath, markTxt);
						b = new File(targetPath).exists();
					} catch (Exception e) {
						e.printStackTrace();
						b = false;
					} finally{
						fileMap = doLastStep(fileMap, srcPath, targetPath, rf, b, targetFilePath, fileId);
					}
			    }
			} catch (Exception e) {
				e.printStackTrace();
			}
		return fileMap;
	}
	
	
	/**
	 * 删除源记录、改名、更新记录
	 * @param srcPath
	 * @param targetPath
	 * @param rf
	 * @return
	 */
	public Map<String,String> doLastStep(Map<String,String> fileMap,String srcPath,String targetPath,ResFileRelation rf,boolean b,String targetFilePath,String fileId){
		try {
			//先删除原来的文件
			if(new File(targetPath).exists()){
				DoFileUtils.deleteDir(srcPath);
				try {
					//再改名
					if(new File(targetFilePath).exists()){
						DoFileUtils.deleteDir(srcPath);
					}
					FileUtils.moveFile(new File(targetPath), new File(targetFilePath));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else{
				targetFilePath = srcPath;
			}
		} catch (Exception e2) {
			e2.printStackTrace();
			b = false;
		} finally{
			//后更新记录
			updateVo(rf, b);
			if(!new File(targetFilePath).exists()){
				if(!new File(targetPath).exists()){
					targetFilePath = srcPath;
				}else{
					//再改名
					try {
						FileUtils.moveFile(new File(targetPath), new File(targetFilePath));
					} catch (IOException e) {
						e.printStackTrace();
						//如果始终无法成功，则删除掉
						DoFileUtils.deleteDir(targetPath);
						if(new File(targetPath).exists()){
							targetFilePath = srcPath;
						}
					}
				}
			}
			fileMap.put(fileId, targetFilePath);
		}
		return fileMap;
	}
	
	
	
	/**
	 * 更新加水印状态
	 * @param rf
	 * @param b
	 */
	public void  updateVo(ResFileRelation rf,boolean b){
		if(b){
			rf.setProcessWatermarkStatus(ResReleaseConstant.FileProcessStatus.FILE_PROCESS_SUCCESS);
			rf.setProcessWatermarkRemark("文件加水印成功");
		}else{
			rf.setProcessWatermarkStatus(ResReleaseConstant.FileProcessStatus.FILE_PROCESS_FAILED);
			rf.setProcessWatermarkRemark("文件加水印失败");
		}
		getResOrderService().update(rf);
	}
	
	/**
	 * 生成元数据Excel
	 */
	private ResOrder createMetadataExcelResorder(ResOrder resOrder, ProdParamsTemplate template,
			CaList caList,String publishDir) {
		try {
			logger.debug("生成元数据Excel......");
			String publishType = template.getType();
			String metadataCodes = template.getMetaDatasCode(); //元数据项
			/*List<MetadataDefinition> metaDataList = new ArrayList<MetadataDefinition>();
			if(StringUtils.isNotEmpty(metadataCodes)){
				String[] arrs = metadataCodes.split(",");
				for(String metadataCode: arrs){
					MetadataDefinition metaDataDefine = MetadataSupport.getMetadateDefineByUri(metadataCode);
					metaDataList.add(metaDataDefine);
				}
			}*/
			Map<String, List<MetadataDefinition>> map = new HashMap<String, List<MetadataDefinition>>();
			String publishtypes[] = publishType.split(",");
			if(StringUtils.isNotEmpty(metadataCodes)){
				String[] arrs = metadataCodes.split("!");
					for (int j = 0; j < publishtypes.length; j++) {
						for (int i = 0; i < arrs.length; i++) {
						JSONObject json = JSONObject.fromObject(arrs[i]);
						String jsons =  json.getString(publishtypes[j]);
						if(jsons!=null){
							String metas[] = jsons.split(",");
							List<MetadataDefinition> metaDataList = new ArrayList<MetadataDefinition>();
							for (String meta : metas) {
								MetadataDefinition metaDataDefine = MetadataSupport.getMetadateDefineByUri(meta);
								metaDataList.add(metaDataDefine);
							}
							map.put(publishtypes[j], metaDataList);
						}
					}
				}
				
			}
			
			
			
			
			try {
				File file = null;
				//成功数
				int succ = 0;
				Set set = map.keySet();
				for (Object keys : set) {
					file = ExcelUtil.createExcelBeachByRes(caList, map.get(keys),keys.toString(), null,publishDir);
					if(file.exists()){
						succ++;
					}
				}
				if(succ==publishtypes.length){
					resOrder.setExcelOkStatus(ResReleaseConstant.FileProcessStatus.FILE_PROCESS_SUCCESS);
					resOrder.setRemark("生成元数据excel成功。");
				}else{
					resOrder.setExcelOkStatus(ResReleaseConstant.FileProcessStatus.FILE_PROCESS_FAILED);
					resOrder.setRemark("元数据excel生成失败。");
					throw new ServiceException("元数据excel生成失败！");
				}
			} catch (Exception e) {
				resOrder.setExcelOkStatus(ResReleaseConstant.FileProcessStatus.FILE_PROCESS_FAILED);
				resOrder.setRemark("元数据excel生成失败。");
				throw new ServiceException("元数据excel生成失败！");
			}
			getResOrderService().update(resOrder);
		} catch (Exception e) {
			throw new ServiceException("生成元数据excel失败！---》"+e.getMessage());
		}
		 return resOrder;
	}
	/**
	 * 生成元数据Excel
	 */
	private SubjectStore createMetadataExcelSubject(SubjectStore subjectStore, ProdParamsTemplate template,
			CaList caList,String publishDir) {
		try {
			logger.debug("生成元数据Excel......");
			String publishType = template.getType();
			String metadataCodes = template.getMetaDatasCode(); //元数据项
			/*List<MetadataDefinition> metaDataList = new ArrayList<MetadataDefinition>();
			if(StringUtils.isNotEmpty(metadataCodes)){
				String[] arrs = metadataCodes.split(",");
				for(String metadataCode: arrs){
					MetadataDefinition metaDataDefine = MetadataSupport.getMetadateDefineByUri(metadataCode);
					metaDataList.add(metaDataDefine);
				}
			}*/
			Map<String, List<MetadataDefinition>> map = new HashMap<String, List<MetadataDefinition>>();
			String publishtypes[] = publishType.split(",");
			if(StringUtils.isNotEmpty(metadataCodes)){
				String[] arrs = metadataCodes.split("!");
					for (int j = 0; j < publishtypes.length; j++) {
						for (int i = 0; i < arrs.length; i++) {
						JSONObject json = JSONObject.fromObject(arrs[i]);
						String jsons =  json.getString(publishtypes[j]);
						if(jsons!=null){
							String metas[] = jsons.split(",");
							List<MetadataDefinition> metaDataList = new ArrayList<MetadataDefinition>();
							for (String meta : metas) {
								MetadataDefinition metaDataDefine = MetadataSupport.getMetadateDefineByUri(meta);
								metaDataList.add(metaDataDefine);
							}
							map.put(publishtypes[j], metaDataList);
						}
					}
				}
				
			}
			
			try {
				File file = null;
				//成功数
				int succ = 0;
				Set set = map.keySet();
				for (Object keys : set) {
					file = ExcelUtil.createExcelBeachByRes(caList, map.get(keys),keys.toString(), null,publishDir);
					if(file.exists()){
						succ++;
					}
				}
				if(succ==publishtypes.length){
					subjectStore.setExcelOkStatus(ResReleaseConstant.FileProcessStatus.FILE_PROCESS_SUCCESS);
					subjectStore.setRemark("生成元数据excel成功。");
				}else{
					subjectStore.setExcelOkStatus(ResReleaseConstant.FileProcessStatus.FILE_PROCESS_FAILED);
					subjectStore.setRemark("元数据excel生成失败。");
					throw new ServiceException("元数据excel生成失败。");
				}
			} catch (Exception e) {
				subjectStore.setExcelOkStatus(ResReleaseConstant.FileProcessStatus.FILE_PROCESS_FAILED);
				subjectStore.setRemark("元数据excel生成失败。");
				throw new ServiceException("元数据excel生成失败。");
			}
			getSubjectService().update(subjectStore);
		} catch (Exception e) {
			throw new ServiceException("生成元数据Excel失败!--->"+e.getMessage());
		}
		 return subjectStore;
	}
	
	/**
	 * 生成资源XML文件
	 */
	private void createResourcesList(ResOrder resOrder, CaList caList,String publishDir,Map<String, List<String>> map,Map<String,String> fileMap) {
		String remark = resOrder.getRemark();
		try {
			XmlUtil.createResourceListXMLFile(resOrder, caList,publishDir,map,fileMap);
			//判断是否成功生成资源XML文件
			File file = new File(publishDir + "资源文件清单.xml");
			if(file.exists()){
				resOrder.setXmlOkStatus(ResReleaseConstant.FileProcessStatus.FILE_PROCESS_SUCCESS);
				if(StringUtils.isNotBlank(remark)){
					resOrder.setRemark(remark + ";生成资源xml文件成功。");
				}else{
					resOrder.setRemark("生成资源xml文件成功。");
				}
			}else{
				resOrder.setXmlOkStatus(ResReleaseConstant.FileProcessStatus.FILE_PROCESS_FAILED);
				resOrder.setRemark("资源xml文件生成失败。");
				if(StringUtils.isNotBlank(remark)){
					resOrder.setRemark(remark + ";资源xml文件生成失败。");
				}else{
					resOrder.setRemark("资源xml文件生成失败。");
				}
			}
		} catch (Exception e) {
			resOrder.setXmlOkStatus(ResReleaseConstant.FileProcessStatus.FILE_PROCESS_FAILED);
			if(StringUtils.isNotBlank(remark)){
				resOrder.setRemark(remark + ";资源xml文件生成失败。");
			}else{
				resOrder.setRemark("资源xml文件生成失败。");
			}
		}
		getResOrderService().update(resOrder);
		
	}
	
	
	private String getIdsString(String objectId,String posttype) {
		String idStrs = "";
		List<ResOrderDetail> orderDetailList = getResOrderService().getResOrderDetailByOrderIdAndtype(objectId, posttype);
		if(orderDetailList != null){
			StringBuffer ids = new StringBuffer();
			for(ResOrderDetail detail : orderDetailList){
				ids.append(detail.getResId() + ",");
			}
			if(ids.length() > 0){
				idStrs = ids.toString().substring(0, ids.toString().length()-1);
			}
		}
		return idStrs;
	}
	
	private CaList obtainCaList(String idStrs) {
		CaList caList = null;
		if(StringUtils.isNotBlank(idStrs) && idStrs.length() > 0){
			Gson gson = new Gson();
			HttpClientUtil http = new HttpClientUtil();
			SearchParamCa searchParamCa = new SearchParamCa();
			searchParamCa.setIds(idStrs);
			idStrs = new Gson().toJson(searchParamCa);
			String resource = http.postJson(PUBLISH_DETAILLIST_URL, idStrs);
			caList = gson.fromJson(resource, CaList.class);
		}
		return caList;
	}
	
	
	private IResReleaseService getResReleaseService(){
		IResReleaseService resReleaseService = null;
		try {
			resReleaseService = (IResReleaseService) BeanFactoryUtil.getBean("resReleaseService");
		} catch (Exception e) {
			logger.debug("bean['resReleaseService']尚未装载到容器中！");
			e.printStackTrace();
		}
		return resReleaseService;
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
	private ISubjectService getSubjectService(){
		ISubjectService subjectService = null;
		try {
			subjectService = (ISubjectService) BeanFactoryUtil.getBean("subjectService");
		} catch (Exception e) {
			logger.debug("bean['subjectService']尚未装载到容器中！");
			e.printStackTrace();
		}
		return subjectService;
	}
	
}

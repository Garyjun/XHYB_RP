package com.brainsoon.resrelease.processThread;

import java.util.List;

import org.apache.log4j.Logger;

import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.resrelease.po.ProdParamsTemplate;
import com.brainsoon.resrelease.po.ResFileRelation;
import com.brainsoon.resrelease.po.ResOrder;
import com.brainsoon.resrelease.po.ResRelease;
import com.brainsoon.resrelease.po.ResReleaseDetail;
import com.brainsoon.resrelease.service.IResOrderService;
import com.brainsoon.resrelease.service.IResReleaseService;
import com.brainsoon.resrelease.support.ResReleaseConstant;
import com.brainsoon.system.model.User;

/**
 * @ClassName: PublishThread
 * @Description: 资源发布线程
 * @author xiehewei
 * @date 2015年5月28日 下午1:33:06
 * @date 2015年08月14日 唐辉修改 pm
 *
 */
public class PublishThread implements Runnable {

	private static Logger logger = Logger.getLogger(PublishThread.class);

	private ResOrder resOrder;
	private ResRelease resRelease;
	private long userInfoId;
	
	public PublishThread(ResOrder resOrder, ResRelease resRelease,long userId) {
		this.resOrder = resOrder;
		this.resRelease = resRelease;
		this.userInfoId = userId;
	}

	@Override
	public void run() {
		logger.info("发布开始.............");
		ProdParamsTemplate template = resOrder.getTemplate();
		IResReleaseService resReleaseService = getResReleaseService();
		IResOrderService resOrderService = getResOrderService();
		resOrder.setStatus(ResReleaseConstant.OrderStatus.PUBLISHING);
		resOrderService.update(resOrder);
		resRelease.setStatus(ResReleaseConstant.ResReleaseStatus.PUBLISHING);
		resReleaseService.update(resRelease);
		String publishType = template.getPublishType();//发布方式
		if("offLine".equals(publishType)){//线下发布
			publishOffLine(template, resReleaseService, resOrderService);
		}else{//线上发布
			publishONLine(template, resReleaseService, resOrderService);
		}
	}

	/**
	 * 线下发布：通过线下的方式拷贝给他的客户
	 * 那么，只要加工成功的资源，则都会拷贝给客户，所以我们只需要更改状态即可，并记录每条的文件清单是否发布成
	 * 更改所有的文件清单表的状态为：是否发布成功（0：失败  1：成功）
	 * 更改需求单表的状态为：已发布
	 * 更改发布记录表的状态为：已发布
	 * @param template
	 * @param resReleaseService
	 * @param resOrderService
	 */
	private void publishOffLine(ProdParamsTemplate template,
			IResReleaseService resReleaseService,
			IResOrderService resOrderService) {
			//第一步：更改所有的文件清单表的状态为：是否发布成功（0：失败  1：成功）
			List<ResFileRelation> rfList =  getResOrderService().queryFileByOrdeId(resOrder.getOrderId());
			for (ResFileRelation resFileRelation : rfList) {
				if(resFileRelation.getProcessStatus() == 1){//表示加工成功， 则说明文件存在，则直接发布成功。
					resFileRelation.setPublishStatus(1);
					resFileRelation.setPublishRemark("发布成功。");
				}else{
					resFileRelation.setPublishStatus(0);
					resFileRelation.setPublishRemark("发布失败。【由于：" + resFileRelation.getProcessRemark() + " 】");
				}
				resOrderService.saveOrUpdate(resFileRelation);
			}
			
			//第二步：更改需求单表的状态为：已发布
			resOrder.setStatus(ResReleaseConstant.OrderStatus.PUBLISHED);
			resOrderService.update(resOrder);
			
			//第三步：更改发布记录表的状态为：已发布
			resRelease.setStatus(ResReleaseConstant.ResReleaseStatus.PUBLISHED);
			resReleaseService.update(resRelease);
			
			//第四步：更改发布记录明细表的每条资源的状态（成功or失败）
			List<ResReleaseDetail> rrdList =  getResReleaseService().getResReleaseDetailByCnodition(resRelease.getId());
			if(rrdList != null && rrdList.size() > 0){
				for (ResReleaseDetail resReleaseDetail : rrdList) {
					if(resReleaseDetail.getProcessFileSuccess() == 1){//加工成功= 拷贝文件成功+加水印成功（注：暂时未将加水印成功加入，只要拷贝文件成功就算加工成功了）
						resReleaseDetail.setPublishFileSuccess(ResReleaseConstant.ResPublishStatus.RES_PUBLISH_SUCCESS);
						resReleaseDetail.setRemark(resReleaseDetail.getRemark() + "，" + ResReleaseConstant.ResPublishStatus.RES_PUBLISH_SUCCESS_DESC);
					}else{
						resReleaseDetail.setPublishFileSuccess(ResReleaseConstant.ResPublishStatus.RES_PUBLISH_FAILED);
						resReleaseDetail.setRemark(resReleaseDetail.getRemark() + "，" + ResReleaseConstant.ResPublishStatus.RES_PUBLISH_FAILED_DESC);
					}
					User user = new User();
					user.setId(userInfoId);
					resReleaseDetail.setCreateUser(user);
					resReleaseService.update(resReleaseDetail);
				}
			}
			
			
			
	}
	
	/**
	 * 线上发布：通过线上的方式将加工后的资源元数据和资源文件通过接口的方式发送给调用方。
	 * 传输协议:http
	 * 传输数据格式：JSON
	 * 更改所有的文件清单表的状态为：是否发布成功（0：失败  1：成功）
	 * 更改需求单表的状态为：已发布
	 * 更改发布记录表的状态为：已发布
	 * @param template
	 * @param resReleaseService
	 * @param resOrderService
	 */
	private void publishONLine(ProdParamsTemplate template,
			IResReleaseService resReleaseService,
			IResOrderService resOrderService) {
//		String metaInfos = template.getMetaInfo();//发布内容
//		if(metaInfos!=null){
//			String time2Str = DateUtil.convertDateTimeToString(resOrder.getCreateTime()).replace(":", "").replace(" ", "");
//			String publishDir = publishRoot + time2Str +"/"+ resOrder.getOrderId() + "/releasedResources";//源文件发布路径
//			java.io.File publishFile = new java.io.File(publishDir);
//			if(!publishFile.exists()){
//				publishFile.mkdirs();
//			}
//			List<Ca> caList = obtainCaList(resOrder);
//			Map<String, List<String>> map = getResIdAndFileIdsMap(resOrder);
//			Long orderId = resOrder.getOrderId();
//			if(metaInfos.contains("资源文件")){
//				
//				publishResources(resReleaseService, resOrderService,
//					caList, map, orderId);
//			}
//			if(metaInfos.contains("元数据Excel")){
//				createMetadataExcel(resOrder, template, caList);
//			}
//			if(metaInfos.contains("文件清单Xml")){
//				createResourcesList(resOrder, caList);
//			}
//			resOrder.setStatus(ResReleaseConstant.OrderStatus.PUBLISHED);
//			resOrderService.update(resOrder);
//			resRelease.setStatus(ResReleaseConstant.ResReleaseStatus.PUBLISHED);
//			resReleaseService.update(resRelease);
//		}
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
	
	public static void main(String[] args) {
		/*List<ResFileRelation> list1 = new ArrayList<ResFileRelation>();
		ResFileRelation rf0 = new ResFileRelation();
		ResFileRelation rf1 = new ResFileRelation();
		ResFileRelation rf2 = new ResFileRelation();
		ResFileRelation rf3 = new ResFileRelation();
		ResFileRelation rf4 = new ResFileRelation();
		ResFileRelation rf5 = new ResFileRelation();
		ResFileRelation rf6 = new ResFileRelation();
		ResFileRelation rf7 = new ResFileRelation();
		ResFileRelation rf8 = new ResFileRelation();
		rf0.setResId("resId1");
		rf0.setFileId("fileId11");
		list1.add(rf0);
		rf1.setResId("resId1");
		rf1.setFileId("fileId12");
		list1.add(rf1);
		rf2.setResId("resId1");
		rf2.setFileId("fileId13");
		list1.add(rf2);
		rf3.setResId("resId1");
		rf3.setFileId("fileId14");
		list1.add(rf3);
		rf4.setResId("resId2");
		rf4.setFileId("fileId21");
		list1.add(rf4);
		rf5.setResId("resId2");
		rf5.setFileId("fileId22");
		list1.add(rf5);
		rf6.setResId("resId2");
		rf6.setFileId("fileId23");
		list1.add(rf6);
		rf7.setResId("resId2");
		rf7.setFileId("fileId24");
		list1.add(rf7);
		rf8.setResId("resId3");
		rf8.setFileId("fileId31");
		list1.add(rf8);
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		if(list1 != null){
			Set<String> resIdSet = new HashSet<String>();
			for(ResFileRelation detail : list1){
				String resId = detail.getResId();
				resIdSet.add(resId);
			}
			Iterator<String> it = resIdSet.iterator();
			while(it.hasNext()){
				String key = it.next();
				List<String> list = new ArrayList<String>();
				for(ResFileRelation detail : list1){
					String resId = detail.getResId();
					if(key.equals(resId)){
						String fileId = detail.getFileId();
						list.add(fileId);
					}
				}
				map.put(key, list);
			}
		}
		
		Set<String> set = map.keySet();
		Iterator<String> it = set.iterator();
		while(it.hasNext()){
			String key = it.next();
			List<String> values = map.get(key);
			for(String val : values){
				System.out.println(key + "   " + val);
			}
		}*/
	}
}

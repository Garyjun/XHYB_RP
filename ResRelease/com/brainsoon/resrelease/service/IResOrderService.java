package com.brainsoon.resrelease.service;

import java.util.List;

import com.brainsoon.appframe.query.QueryConditionList;
import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.common.pagination.PageInfo;
import com.brainsoon.common.service.IBaseService;
import com.brainsoon.resrelease.po.ResFileRelation;
import com.brainsoon.resrelease.po.ResOrder;
import com.brainsoon.resrelease.po.ResOrderDetail;

public interface IResOrderService extends IBaseService {

	public ResOrder save(ResOrder resOrder);
	public void saveResource(String resIds, String orderId,String posttype,String restype);
	public void saveAllResource(String conditions, String dirs, String orderId,String restype,String posttype);
	public String addResResource(String resId,String orderId,String posttype,String restype);
	public void deleteResList(String orderId, String clearType, String toMove,String posttype);
	public List<ResOrderDetail> getResOrderDetailByOrderIdAndResType(String orderId,String libType);
	public List<ResOrderDetail> getResOrderDetailByOrderId(String orderId);
	public List<ResOrderDetail> getResOrderDetailByOrderIdAndtype(String orderId,String posttype);
	public List<ResOrderDetail> getResOrderDetailByOrderIds(String orderId,String posttype,String restype);
	public List<ResOrderDetail> getResOrderDetailByOrderId(String orderId,String resid,String posttype);
	public void deleteResource(String orderId, String resId); 
	
	public List<ResOrder> getResOrderByTemplateId(Long templateId);
	public PageResult queryResOrderDetail(PageInfo pageInfo, ResOrderDetail resOrderDetail);
	
	public String getUserByName(String name);
	
	public String canDelByResId(String resId);
	
	public List<ResOrderDetail> getResOrderDetailByPage(String orderId,int page, int size);
	
	public void caculateResource(String orderIds);
	public List<Object[]> getObjByGroup(String orderId);
	
	public String getFileTree(String contentType, String checkedFile, int page, int size, String param, Long orderId,String restype);
	

	public String getDirTree(Long orderId,String restype);
	
	public PageResult getPageResultByOrderId(String orderId, QueryConditionList conditionList);
		
	
	public String queryResforTreePage(int page, int size);
	
	/**
	 * 查询多条资源列表信息
	 */
	public String queryResourceList(String resIds, String page, String size);
	
	/**
	 * 查询多条资源列表信息
	 */
	public String queryResourceLists(String resIds, String page, String size);
	
	public List<ResFileRelation> queryFileDetail(Long orderId, String resId,String posttype);
	public List<String> getResIdByOrderId(String orderId);
	public List<String> getResIdByOrderIds(String orderId,String restype,String posttype);
	public List<ResFileRelation> queryFileByOrdeId(Long orderId);
	public List<ResFileRelation> queryFileByOrdeIdAndposttype(Long orderId,String posttype);
	public List<ResFileRelation> queryFileByOrdeIdAndProcessStatus(Long orderId);
	
	public ResFileRelation queryFileByOrdeIdAndFileId(Long orderId, String fileId);
	
	public ResFileRelation queryFileByOrdeIdAndFileIdAndResId(Long orderId, String fileId, String resId);
	
	public List<ResFileRelation> queryFileByOrdeIdAndResId(Long orderId, String resId);
	
	public String getFileTreeJsonByQueryCondition(int page, 
			int size, String param, Long orderId);
	
	public ResFileRelation queryFileByfileId(Long orderId,String fileId,String posttype);
	
	/**
	 * 按查询条件和资源目录批量添加资源
	 * @param conditions 查询条件
	 * @param dirs 选中的扩展名
	 * @param orderId 需求单id
	 * 
	 * create 2015-9-21 19:00:28
	 */
	public void saveAllResourceByExt(String conditions, String dirs, String orderId,String restype,String posttype);
	//查询文件表中（根据单个资源id查询文件加工状态为-1的）
	public List<ResFileRelation> queryResFileRelationList(String orderId,String resId,String posttype);
}

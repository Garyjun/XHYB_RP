package com.brainsoon.resrelease.service.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.brainsoon.appframe.query.QueryConditionList;
import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.pagination.PageInfo;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.common.util.dofile.util.DoFileUtils;
import com.brainsoon.resource.po.SubjectStore;
import com.brainsoon.resrelease.dao.IResOrderDao;
import com.brainsoon.resrelease.po.ProdParamsTemplate;
import com.brainsoon.resrelease.po.ResFileRelation;
import com.brainsoon.resrelease.po.ResOrder;
import com.brainsoon.resrelease.po.ResOrderDetail;
import com.brainsoon.resrelease.po.ResRelease;
import com.brainsoon.resrelease.po.ResReleaseDetail;
import com.brainsoon.resrelease.service.IResOrderService;
import com.brainsoon.resrelease.service.IResReleaseService;
import com.brainsoon.resrelease.support.PublishExcelUtil;
import com.brainsoon.resrelease.support.ResReleaseConstant;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.semantic.ontology.model.File;
import com.brainsoon.semantic.ontology.model.SearchParamCa;
import com.brainsoon.system.model.SysDir;
import com.brainsoon.system.model.SysParameter;
import com.brainsoon.system.model.User;
import com.brainsoon.system.service.ISysParameterService;
import com.brainsoon.system.support.SysOperateLogUtils;
import com.brainsoon.system.support.SystemConstants;
import com.brainsoon.system.support.SystemConstants.Status;
import com.google.gson.Gson;
@Service
public class ResOrderService extends BaseService implements IResOrderService {

	@Autowired
	private IResOrderDao resOrderDao;
	@Autowired
	private ISysParameterService sysParameterService;
	@Autowired
	private IResReleaseService resReleaseService;
	
	private static String PUBLISH_QUERY_URL = WebappConfigUtil.getParameter("PUBLISH_QUERY_URL");
	private static String PUBLISH_DETAIL_URL = WebappConfigUtil.getParameter("PUBLISH_DETAIL_URL");
	private static String PUBLISH_QUERYBYPOST_URL = WebappConfigUtil.getParameter("PUBLISH_QUERYBYPOST_URL");
	
	
	@Override
	public ResOrder save(ResOrder resOrder) {
		return (ResOrder)this.getBaseDao().create(resOrder);
	}

	/**
	 * 根据选中的文件添加资源和文件
	 * @param resIdsAndFileIds 资源id和文件id的json串
	 * @param orderId 需求单id
	 * 
	 */
	@Override
	public void saveResource(String resIdsAndFileIds, String orderId,String posttype,String restype) {
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		if("".equals(resIdsAndFileIds)){
			logger.info("需求单中未添加资源！");
		}else{
			List<ResOrderDetail> detailList = getResOrderDetailByOrderIds(orderId,posttype,restype);
			List<String> resIdList = new ArrayList<String>();
			for(ResOrderDetail detail : detailList){
				resIdList.add(detail.getResId());
			}
			Map<String, List<String>> map = new HashMap<String, List<String>>();
			JSONArray array = JSONArray.fromObject(resIdsAndFileIds);
			long size = array.size();
			List<String> resIdLists = new ArrayList<String>();
			for(int i=0;i<size;i++){
				JSONObject arr = (JSONObject) array.get(i);
				String fileId = (String) arr.get("fileId");//文件id
				String resId = (String) arr.get("objectId");//资源id
				map = getListByOrderIdAndResIds(Long.valueOf(orderId), resId,posttype,restype);
				
				User u = new User();
				u.setId(userInfo.getUserId());
				/*if(resIdList.contains(o)){
					ResOrderDetail resDetail = new ResOrderDetail();
					resDetail.setResId(resId);
					resDetail.setCreateUser(u);
					resDetail.setOrderId(Long.valueOf(orderId));
					getBaseDao().create(resDetail);
				}*/
				
				if(!resIdList.contains(resId)&&!resIdLists.contains(resId)){
					ResOrderDetail resDetail = new ResOrderDetail();
					resDetail.setResId(resId);
					resDetail.setCreateUser(u);
					resDetail.setPosttype(posttype);
					resDetail.setResType(restype);
					resDetail.setOrderId(Long.valueOf(orderId));
					resDetail.setPlatformId(userInfo.getPlatformId());
					getBaseDao().create(resDetail);
				}
				if(map.get(resId)==null||!map.get(resId).contains(fileId)){
					ResFileRelation resFileRel = new ResFileRelation();
					resFileRel.setFileId(fileId);
					resFileRel.setResId(resId);
					resFileRel.setPosttype(posttype);
					resFileRel.setRestype(restype);
					resFileRel.setOrderId(Long.valueOf(orderId));
					getBaseDao().create(resFileRel);
				}
				resIdLists.add(resId);
			}
			SysOperateLogUtils.addLog("resOrder_addResource", "添加资源-选中文件", userInfo);
		}
	}
	
	@Override
	public void deleteResList(String orderId, String clearType, String resId,String posttype) {
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		ResOrder resOrder = new ResOrder();
		SubjectStore store = new SubjectStore();
		String status = "";
		if(posttype.equals("1")){
			resOrder = (ResOrder) getBaseDao().getByPk(ResOrder.class, Long.parseLong(orderId));
			status = resOrder.getStatus();
		}if(posttype.equals("2")){
			store = (SubjectStore) getBaseDao().getByPk(SubjectStore.class, Long.parseLong(orderId));
			status = store.getStatus();
		}
		
		String publishtype = resOrder.getTemplate().getPublishType();
		if((posttype.equals("1") && publishtype.equals("onLine")) || posttype.equals("2")){
			List<ResReleaseDetail> resReleaseDetails = new ArrayList<ResReleaseDetail>();
			if(status.equals(ResReleaseConstant.OrderStatus.ORDERADD)){
				String resids = "";
				ResRelease resRelease = resReleaseService.getResRelease(Long.parseLong(orderId), posttype);
				resReleaseDetails = resReleaseService.getResReleaseDetailByCnodition(resRelease.getId());
				if(StringUtils.isNotBlank(resId)){
					String[] resIdArr = resId.split(",");
					List<String> list = new ArrayList<String>();
					for (ResReleaseDetail resReleaseDetail : resReleaseDetails) {
						list.add(resReleaseDetail.getResId());
					}
					if(!list.isEmpty()){
						for (int i = 0; i < resIdArr.length; i++) {
							String string = resIdArr[i];
							if(!list.contains(string)){
								resids += string + ","; 
							}
						}
					}
					if(resids.length()>0){
						resId = resids.substring(0, resids.length()-1);
					}else{
						resId = "";
					}
				}
			}
			
			if ("part".equals(clearType)) {
				List<ResOrderDetail> res = getResOrderDetailByOrderIdAndResId(orderId, resId,posttype);
				for (ResOrderDetail detail : res) {
					String rId = detail.getResId();
					getBaseDao().delete(detail);
					
					List<ResFileRelation> fileList = queryFileDetail(Long.valueOf(orderId), rId,posttype);
					if(fileList!=null){
						for(ResFileRelation rf : fileList){
							getBaseDao().delete(rf);
						}
					}
					String detailUrl = PUBLISH_DETAIL_URL + "?id=" + rId;
					HttpClientUtil http = new HttpClientUtil();
					String resDetail = http.executeGet(detailUrl);
					if(resDetail!=null){
						Gson gson = new Gson();
						Ca resDetailCa = gson.fromJson(resDetail, Ca.class);//资源详细
						if(posttype.equals("1")){
							SysOperateLogUtils.addLog("resOrder_delResource", resDetailCa.getMetadataMap().get("title"), userInfo);
						}
						if(posttype.equals("2")){
							SysOperateLogUtils.addLog("subject_delResource", resDetailCa.getMetadataMap().get("title"), userInfo);
						}
					}
				}
			} else if ("all".equals(clearType)) {
				List<ResOrderDetail> list = getResOrderDetailByOrderIdAndtype(orderId,posttype);
				if(status.equals(ResReleaseConstant.OrderStatus.ORDERADD)){
					List<String> lists = new ArrayList<String>();
					for (ResReleaseDetail resReleaseDetail: resReleaseDetails) {
						lists.add(resReleaseDetail.getResId());
					}
					for(ResOrderDetail detail : list){
						String resIdTemp = detail.getResId();
						//如果发布表中不包含这个资源，则这个资源没有发布，能删除
						if(!lists.contains(resIdTemp)){
							getBaseDao().delete(detail);
							String detailUrl = PUBLISH_DETAIL_URL + "?id=" + resIdTemp;
							HttpClientUtil http = new HttpClientUtil();
							String resDetail = http.executeGet(detailUrl);
							if(resDetail!=null){
								Gson gson = new Gson();
								Ca resDetailCa = gson.fromJson(resDetail, Ca.class);//资源详细
								if(posttype.equals("1")){
									SysOperateLogUtils.addLog("resOrder_delResource", resDetailCa.getMetadataMap().get("title"), userInfo);
								}
								if(posttype.equals("2")){
									SysOperateLogUtils.addLog("subject_delResource", resDetailCa.getMetadataMap().get("title"), userInfo);
								}
							}
							//删除文件表
							List<ResFileRelation> details = queryFileDetail(Long.parseLong(orderId), resIdTemp, posttype);
							if(details!=null){
								for(ResFileRelation file : details){
									getBaseDao().delete(file);
								}
							}
						}
					}
					
				}else{
					for(ResOrderDetail detail : list){
						String resIdTemp = detail.getResId();
						getBaseDao().delete(detail);
						String detailUrl = PUBLISH_DETAIL_URL + "?id=" + resIdTemp;
						HttpClientUtil http = new HttpClientUtil();
						String resDetail = http.executeGet(detailUrl);
						if(resDetail!=null){
							Gson gson = new Gson();
							Ca resDetailCa = gson.fromJson(resDetail, Ca.class);//资源详细
							if(posttype.equals("1")){
								SysOperateLogUtils.addLog("resOrder_delResource", resDetailCa.getMetadataMap().get("title"), userInfo);
							}
							if(posttype.equals("2")){
								SysOperateLogUtils.addLog("subject_delResource", resDetailCa.getMetadataMap().get("title"), userInfo);
							}
						}
					}
					List<ResFileRelation> fileList = queryFileByOrdeIdAndposttype(Long.valueOf(orderId),posttype);
					if(fileList!=null){
						for(ResFileRelation file : fileList){
							getBaseDao().delete(file);
						}
					}
				}
			}
		}else{
			List<ResReleaseDetail> resReleaseDetails = new ArrayList<ResReleaseDetail>();
			if(status.equals(ResReleaseConstant.OrderStatus.ORDERADD)){
				List<String> resids = new ArrayList<String>();
				ResRelease resRelease = resReleaseService.getResRelease(Long.parseLong(orderId), posttype);
				resReleaseDetails = resReleaseService.getResReleaseDetailByCnodition(resRelease.getId());
				Map<String, List<ResFileRelation>> map = new HashMap<String, List<ResFileRelation>>();
				if(StringUtils.isNotBlank(resId)){
					String[] resIdArr = resId.split(",");
					List<String> list = new ArrayList<String>();
					for (ResReleaseDetail resReleaseDetail : resReleaseDetails) {
						list.add(resReleaseDetail.getResId());
					}
					if(!list.isEmpty()){
						for (int i = 0; i < resIdArr.length; i++) {
							String resid = resIdArr[i];
							if(!list.contains(resid)){
								resids.add(resid); 
							}else{
								List<ResFileRelation> fileRelations = queryResFileRelationList(orderId, resid, posttype);
								if(fileRelations.isEmpty()){
									continue;
								}else{
									List<ResFileRelation> list2 = new ArrayList<ResFileRelation>();
									for (ResFileRelation resFileRelation : fileRelations) {
										list2.add(resFileRelation);
									}
									map.put(resid, list2);
								}
							}
						}
					}
				}
				if ("part".equals(clearType)) {
					if(StringUtils.isNotBlank(resId)){
						String resIds[] = resId.split(",");
						for (int i = 0; i < resIds.length; i++) {
							if(resids.contains(resIds[i])){
								List<ResOrderDetail> res = getResOrderDetailByOrderIdAndResId(orderId, resIds[i],posttype);
								for (ResOrderDetail detail : res) {
									String rId = detail.getResId();
									getBaseDao().delete(detail);
									
									List<ResFileRelation> fileList = queryFileDetail(Long.valueOf(orderId), rId,posttype);
									if(fileList!=null){
										for(ResFileRelation rf : fileList){
											getBaseDao().delete(rf);
										}
									}
									String detailUrl = PUBLISH_DETAIL_URL + "?id=" + rId;
									HttpClientUtil http = new HttpClientUtil();
									String resDetail = http.executeGet(detailUrl);
									if(resDetail!=null){
										Gson gson = new Gson();
										Ca resDetailCa = gson.fromJson(resDetail, Ca.class);//资源详细
										SysOperateLogUtils.addLog("resOrder_delResource", resDetailCa.getMetadataMap().get("title"), userInfo);
									}
								}
							}else{
							List<ResFileRelation> fileRelations = map.get(resIds[i]);
							if(fileRelations != null){
								for (ResFileRelation resFileRelation : fileRelations) {
									getBaseDao().delete(resFileRelation);
								}
								List<ResFileRelation> relations = queryFileDetail(Long.parseLong(orderId), resIds[i], posttype);
								if(relations.isEmpty()){
									List<ResOrderDetail> res = getResOrderDetailByOrderIdAndResId(orderId, resIds[i],posttype);
									for (ResOrderDetail resOrderDetail : res) {
										getBaseDao().delete(resOrderDetail);
										//每删一个资源则加一个日志
										String detailUrl = PUBLISH_DETAIL_URL + "?id=" + resIds[i];
										HttpClientUtil http = new HttpClientUtil();
										String resDetail = http.executeGet(detailUrl);
										if(resDetail!=null){
											Gson gson = new Gson();
											Ca resDetailCa = gson.fromJson(resDetail, Ca.class);//资源详细
											SysOperateLogUtils.addLog("resOrder_delResource", resDetailCa.getMetadataMap().get("title"), userInfo);
										}
									}
								}
							}
						}
						}
					}
				}else if ("all".equals(clearType)) {
					for (int i = 0; i < resids.size(); i++) {
						List<ResOrderDetail> res = getResOrderDetailByOrderIdAndResId(orderId, resids.get(i),posttype);
						for (ResOrderDetail detail : res) {
							String rId = detail.getResId();
							getBaseDao().delete(detail);
							
							List<ResFileRelation> fileList = queryFileDetail(Long.valueOf(orderId), rId,posttype);
							if(fileList!=null){
								for(ResFileRelation rf : fileList){
									getBaseDao().delete(rf);
								}
							}
							String detailUrl = PUBLISH_DETAIL_URL + "?id=" + rId;
							HttpClientUtil http = new HttpClientUtil();
							String resDetail = http.executeGet(detailUrl);
							if(resDetail!=null){
								Gson gson = new Gson();
								Ca resDetailCa = gson.fromJson(resDetail, Ca.class);//资源详细
								SysOperateLogUtils.addLog("resOrder_delResource", resDetailCa.getMetadataMap().get("title"), userInfo);
							}
						}
					}
					Set<String> set =  map.keySet();
					for (String setResIds : set) {
						List<ResFileRelation> fileRelations = map.get(setResIds);
						if(fileRelations != null){
							for (ResFileRelation resFileRelation : fileRelations) {
								getBaseDao().delete(resFileRelation);
							}
							List<ResFileRelation> relations = queryFileDetail(Long.parseLong(orderId), setResIds, posttype);
							if(relations.isEmpty()){
								List<ResOrderDetail> res = getResOrderDetailByOrderIdAndResId(orderId, setResIds,posttype);
								for (ResOrderDetail resOrderDetail : res) {
									getBaseDao().delete(resOrderDetail);
									//每删一个资源则加一个日志
									String detailUrl = PUBLISH_DETAIL_URL + "?id=" + setResIds;
									HttpClientUtil http = new HttpClientUtil();
									String resDetail = http.executeGet(detailUrl);
									if(resDetail!=null){
										Gson gson = new Gson();
										Ca resDetailCa = gson.fromJson(resDetail, Ca.class);//资源详细
										SysOperateLogUtils.addLog("resOrder_delResource", resDetailCa.getMetadataMap().get("title"), userInfo);
									}
								}
							}
						}
					}
					
				}
				
				
			}
		}
		
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ResOrderDetail> getResOrderDetailByOrderIdAndResType(String orderId,String libType){
		List<ResOrderDetail> list = new ArrayList<ResOrderDetail>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		int platformId = LoginUserUtil.getLoginUser().getPlatformId();
		String hql = "";
		if(platformId==1){
			hql = " from ResOrderDetail where orderId=:orderId and resTypeId=:libType";
			parameters.put("libType", libType);
		}else{
			hql = " from ResOrderDetail where orderId=:orderId";
		}
		parameters.put("orderId", Long.valueOf(orderId));
		
		list = getBaseDao().query(hql, parameters);
		return list;
	}
	
	@Override
	public void deleteResource(String orderId, String resId){
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("orderId", orderId);
		parameters.put("resId", resId);
		baseDao.executeUpdate("DELETE FROM res_order_detail rod WHERE rod.order_id=:orderId and res_id=:resId", parameters);
	}

	@Override
	public List<ResOrder> getResOrderByTemplateId(Long templateId) {
		String hql = " from ResOrder where template.id="+templateId;
		List<ResOrder> resOrderList = query(hql);
		return resOrderList;
	}

	public List<ResOrderDetail> getResOrderDetailByOrderIdAndResId(
			String orderId, String resId,String posttype) {
		List<ResOrderDetail> list = new ArrayList<ResOrderDetail>();
		ResOrderDetail resOrderDetail = null;
		String[] resIdArr = resId.split(",");
		for(int i=0;i<resIdArr.length;i++){
			if(resIdArr[i].equals("")){
				continue;
			}
			String sql = "select * from res_order_detail where order_id="+Long.valueOf(orderId)+" and res_id="+"'"+resIdArr[i]+"' and posttype='"+posttype+"'";
			resOrderDetail = (ResOrderDetail) getBaseDao().queryBySql(sql, ResOrderDetail.class).get(0);
			list.add(resOrderDetail);
		}
		return list;
	}
	
	public List<List<String>> getNewList(List<String> list1, List<String> list2,List<String> list){
		List<List<String>> li = new ArrayList<List<String>>();
		List<String> list3 = new ArrayList<String>();
		List<String> list4 = new ArrayList<String>();
		int count = 0;
		for(String s: list2){
			if(!list1.contains(s)){
				list3.add(s);
//				list4.add(list.get(count));
				list4.add("2");
			}
			count++;
		}
		li.add(list3);
		li.add(list4);
		return li;
	}
	
	
	@Override
	public List<ResOrderDetail> getResOrderDetailByOrderId(String orderId) {
		List<ResOrderDetail> list = new ArrayList<ResOrderDetail>();
		String hql = " from ResOrderDetail where orderId="+Long.valueOf(orderId);
		list = getBaseDao().query(hql);
		return list;
	}
	@Override
	public List<ResOrderDetail> getResOrderDetailByOrderIdAndtype(String orderId,String posttype) {
		List<ResOrderDetail> list = new ArrayList<ResOrderDetail>();
		String hql = " from ResOrderDetail where orderId="+Long.valueOf(orderId)+" and posttype='"+posttype+"'";
		list = getBaseDao().query(hql);
		return list;
	}
	
	@Override
	public List<ResOrderDetail> getResOrderDetailByOrderIds(String orderId,String posttype,String restype) {
		List<ResOrderDetail> list = new ArrayList<ResOrderDetail>();
		String hql = " from ResOrderDetail where orderId="+Long.valueOf(orderId)+" and posttype='"+posttype+"' and resType='"+restype+"'";
		list = getBaseDao().query(hql);
		return list;
	}

	@Override
	public List<ResOrderDetail> getResOrderDetailByPage(String orderId,int page, int size) {
		List<ResOrderDetail> list = new ArrayList<ResOrderDetail>();
		String hql = " from ResOrderDetail where orderId="+Long.valueOf(orderId);
		list = getBaseDao().query(hql);
		return list;
	}
	
	@Override
	public PageResult queryResOrderDetail(PageInfo pageInfo, ResOrderDetail resOrderDetail) {
		String hql=" from ResOrderDetail rod";
    	Map<String, Object> params=new HashMap<String, Object>();
    	if(StringUtils.isNotBlank(resOrderDetail.getOrderId().toString())){
    		hql=hql+" and rod.orderId =:orderId ";
    		params.put("orderId", resOrderDetail.getOrderId());
    	}
    	try {
			baseDao.query4Page(hql, pageInfo, params);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ServiceException(e.getMessage());
		}
		return pageInfo.getPageResult();
	}
	
	public String getUserByName(String name) {
		Map<String, Object> params=new HashMap<String, Object>();
		String hql = " from User u where 1=1";
		if(StringUtils.isNotBlank(name)){
			hql=hql+" and u.name like '% "+name+"%'";
			params.put("name", name);
		}
		String ids = "";
		List<User> list = baseDao.query(hql);
		if(list!=null&&list.size()>0){
			for(User u:list){
				ids += u.getId()+",";
			}
		}
		return ids.substring(0,ids.length()-1);
	}

	@Override
	public String canDelByResId(String resId) {
		String result = "";
		String[] resIds = resId.split(",");
		String hql = " from ResOrderDetail where 1=1 and resId='";
		for(String id:resIds){
			hql += id+"'";
			List<ResOrderDetail> list = baseDao.query(hql);
			if(list!=null&&list.size()>0){
				result += id + ",";
			}
			hql = " from ResOrderDetail where 1=1 and resId='";
		}
		if(result.contains(",")){
			result = result.substring(0,result.length()-1);
		}
		return result;
	}

	@Override
	public void caculateResource(String orderIds) {
		Map<String,List<Object[]>> map = new HashMap<String,List<Object[]>>();
		map = resOrderDao.caculateResource(orderIds);
		Set<String> set = map.keySet();
		Iterator<String> it = set.iterator();
		while(it.hasNext()){
			String orderId = it.next();
			ResOrder resOrder = (ResOrder) getByPk(ResOrder.class, Long.valueOf(orderId));
			PublishExcelUtil.writeCaculateExcel(map.get(orderId), resOrder);
		}
	}
	
	@Override
	public List<Object[]> getObjByGroup(String orderId) {
		Map<String,List<Object[]>> map = new HashMap<String,List<Object[]>>();
		map = resOrderDao.caculateResource(orderId);
		Set<String> set = map.keySet();
		Iterator<String> it = set.iterator();
		List<Object[]> obj = null;
		while(it.hasNext()){
			String id = it.next();
			ResOrder resOrder = (ResOrder) getByPk(ResOrder.class, Long.valueOf(id));
			obj = map.get(orderId);
		}
		return obj;
	}
	 
	/**
	 * 按查询条件和资源目录批量添加资源
	 * @param conditions 查询条件
	 * @param dirs 选中的扩展名
	 * @param orderId 需求单id
	 * 
	 * create 2015-9-21 19:00:28
	 */
	public void saveAllResourceByExt(String conditions, String dirs, String orderId,String restype,String posttype){
		ResOrder resOrder = (ResOrder) baseDao.getByPk(ResOrder.class, Long.valueOf(orderId));
		UserInfo userInfo = LoginUserUtil.getLoginUser(); 
		User u = new User();
		u.setId(userInfo.getUserId());
		int platformId = userInfo.getPlatformId();
		User user = (User) getByPk(User.class, userInfo.getUserId());
		String resourceDataJson = user.getResourceDataJson();
		JSONArray jsonArray = JSONArray.fromObject(resourceDataJson);
		String fields = "";
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			if(jsonObject.getString("id").equals(restype)){
				fields = jsonObject.getString("field");
			}
		}
		if(resOrder!=null){
			/*ProdParamsTemplate template = resOrder.getTemplate();
			String publishType = template.getType();*/
			JSONArray resArr = getResourcesByCondition(conditions, restype);//获得符合查询条件的资源
			if(resArr!=null){
				List<String> dbResIdList = getResIdByOrderIds(orderId,restype,posttype);//获得数据库中已经添加的资源id
				List<String> addResIdList = new ArrayList<String>();//考虑到事务 ，需要缓存需要添加的资源id
				
				//List<String> addFileIdlList = new ArrayList<String>();//考虑到事务 ，需要缓存需要添加的文件id
				Map<String, List<String>> addResIdAndFileIdListmap = new HashMap<String, List<String>>();//考虑到事务 ，需要缓存需要添加的资源fileIds
				for(int i=0;i<resArr.size();i++){//循环资源
					Gson gson = new Gson();
					Ca ca = gson.fromJson(resArr.get(i).toString(), Ca.class);//资源Ca
					String resId = ca.getObjectId();//资源id
					Map<String, List<String>> dbResIdAndFileIdListmap = 
							getListByOrderIdAndResIds(Long.valueOf(orderId), resId,posttype,restype);//获得所有的文件 key:resId,   value:fileIds
					
					String detailUrl = PUBLISH_DETAIL_URL + "?id=" + resId;
					HttpClientUtil http = new HttpClientUtil();
					String resDetail = http.executeGet(detailUrl);
					
					if(resDetail!=null){
						Ca resDetailCa = gson.fromJson(resDetail, Ca.class);//资源详细
						List<File> fileList = resDetailCa.getRealFiles();
						String rootpath = "";
						String fieldpath = "";
						if(fileList!=null){
							for(File file : fileList){//循环单个资源下的文件
								String isDir = file.getIsDir();
								String fileId = file.getObjectId();//文件id
								String fileType = file.getFileType();//文件id
								if(file.getPid().equals("-1")){
									rootpath = file.getId();
								}
								else if(file.getPid().equals(rootpath)){
									String fieldss[]=fields.split(",");
									for (String field : fieldss) {
										if(file.getId().toLowerCase().contains("/"+field.trim().toLowerCase())){
											fieldpath = file.getId();
										}
									}
								}else if(file.getPid().indexOf(fieldpath)==0){
									if("2".equals(isDir)){//文件
										if (dirs.contains(fileType)) {//如果该文件类型包含在 选择的扩展名之内，那么添加
											//检查能不能添加
											addResOrderDetailToDBs(orderId, u,platformId, dbResIdList,addResIdList, resId,restype,posttype);
											
											logger.info("添加资源-全部页-按扩展名  saveAllResourceByExt if dirs.contains(fileType) dirs:"+dirs+" fileType:"+fileType);
											if(dbResIdAndFileIdListmap==null){//数据库res_file_relation中为空
												List<String> fileIds = addResIdAndFileIdListmap.get(resId);
												if(fileIds==null){
													ResFileRelation resFileRel = new ResFileRelation();
													resFileRel.setFileId(fileId);
													resFileRel.setResId(resId);
													resFileRel.setRestype(restype);
													resFileRel.setPosttype(posttype);
													resFileRel.setOrderId(Long.valueOf(orderId));
													getBaseDao().create(resFileRel);
													List<String> tempList = new ArrayList<String>();
													tempList.add(fileId);
													addResIdAndFileIdListmap.put(resId, tempList);
												}else{
													ResFileRelation resFileRel = new ResFileRelation();
													resFileRel.setFileId(fileId);
													resFileRel.setResId(resId);
													resFileRel.setRestype(restype);
													resFileRel.setPosttype(posttype);
													resFileRel.setOrderId(Long.valueOf(orderId));
													getBaseDao().create(resFileRel);
													//List<String> tempList = addResIdAndFileIdListmap.get(resId);
													fileIds.add(fileId);
													addResIdAndFileIdListmap.put(resId, fileIds);
												}
											}else{//数据库res_file_relation中不为空
												List<String> dbFileIds = dbResIdAndFileIdListmap.get(resId);
												List<String> addFileIds = addResIdAndFileIdListmap.get(resId);
												if(!dbFileIds.contains(fileId)){
													if(addFileIds==null){
														ResFileRelation resFileRel = new ResFileRelation();
														resFileRel.setFileId(fileId);
														resFileRel.setResId(resId);
														resFileRel.setRestype(restype);
														resFileRel.setPosttype(posttype);
														resFileRel.setOrderId(Long.valueOf(orderId));
														getBaseDao().create(resFileRel);
														List<String> tempList = new ArrayList<String>();
														tempList.add(fileId);
														addResIdAndFileIdListmap.put(resId, tempList);
													}else{
														if(!addFileIds.contains(fileId)){
															ResFileRelation resFileRel = new ResFileRelation();
															resFileRel.setFileId(fileId);
															resFileRel.setResId(resId);
															resFileRel.setRestype(restype);
															resFileRel.setPosttype(posttype);
															resFileRel.setOrderId(Long.valueOf(orderId));
															getBaseDao().create(resFileRel);
															addFileIds.add(fileId);
															addResIdAndFileIdListmap.put(resId, addFileIds);
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
				}
				SysOperateLogUtils.addLog("resOrder_addResource", "添加资源-全部页-按扩展名", userInfo);
			}
		}
	}

	/**
	 * 按查询条件和资源目录批量添加资源
	 * @param conditions 查询条件
	 * @param dirs 目录
	 * @param orderId 需求单id
	 * 
	 * mod 添加全部页 - 资源目录 2015-9-21 18:58:52
	 */
	@Override
	public void saveAllResource(String conditions, String dirs, String orderId,String restype,String posttype) {
		//TODO
		ResOrder resOrder = (ResOrder) baseDao.getByPk(ResOrder.class, Long.valueOf(orderId));
		UserInfo userInfo = LoginUserUtil.getLoginUser(); 
		User u = new User();
		u.setId(userInfo.getUserId());
		int platformId = userInfo.getPlatformId();
		
		if(resOrder!=null){
			/*ProdParamsTemplate template = resOrder.getTemplate();
			String publishType = template.getType();*/
			JSONArray resArr = getResourcesByCondition(conditions, restype);//获得符合查询条件的资源
			if(resArr!=null){
				List<String> dbResIdList = getResIdByOrderIds(orderId,restype,posttype);//获得数据库中已经添加的资源id
				List<String> addResIdList = new ArrayList<String>();//考虑到事务 ，需要缓存需要添加的资源id
				
				//List<String> addFileIdlList = new ArrayList<String>();//考虑到事务 ，需要缓存需要添加的文件id
				Map<String, List<String>> addResIdAndFileIdListmap = new HashMap<String, List<String>>();//考虑到事务 ，需要缓存需要添加的资源fileIds
				if("''".equals(dirs)){//没有选则目录
					for(int i=0;i<resArr.size();i++){//循环资源
						Gson gson = new Gson();
						Ca ca = gson.fromJson(resArr.get(i).toString(), Ca.class);//资源Ca
						String resId = ca.getObjectId();//资源id
						Map<String, List<String>> dbResIdAndFileIdListmap = 
								getListByOrderIdAndResIds(Long.valueOf(orderId), resId,posttype,restype);//获得所有的文件 key:resId,   value:fileIds
						
						String detailUrl = PUBLISH_DETAIL_URL + "?id=" + resId;
						HttpClientUtil http = new HttpClientUtil();
						String resDetail = http.executeGet(detailUrl);
						
						if(resDetail!=null){
							Ca resDetailCa = gson.fromJson(resDetail, Ca.class);//资源详细
							List<File> fileList = resDetailCa.getRealFiles();
							if(fileList!=null){
								for(File file : fileList){//循环单个资源下的文件
									String isDir = file.getIsDir();
									String fileId = file.getObjectId();//文件id
									if("2".equals(isDir)){//文件
										addResOrderDetailToDBs(orderId, u,
												platformId, dbResIdList,
												addResIdList, resId,restype,posttype);
										
										//向res_file_relation插入记录
										if(dbResIdAndFileIdListmap==null){//数据库res_file_relation中为空
											List<String> fileIds = addResIdAndFileIdListmap.get(resId);
											if(fileIds==null){
												ResFileRelation resFileRel = new ResFileRelation();
												resFileRel.setFileId(fileId);
												resFileRel.setResId(resId);
												resFileRel.setRestype(restype);
												resFileRel.setPosttype(posttype);
												resFileRel.setOrderId(Long.valueOf(orderId));
												getBaseDao().create(resFileRel);
												List<String> tempList = new ArrayList<String>();
												tempList.add(fileId);
												addResIdAndFileIdListmap.put(resId, tempList);
											}else{
												ResFileRelation resFileRel = new ResFileRelation();
												resFileRel.setFileId(fileId);
												resFileRel.setResId(resId);
												resFileRel.setRestype(restype);
												resFileRel.setPosttype(posttype);
												resFileRel.setOrderId(Long.valueOf(orderId));
												getBaseDao().create(resFileRel);
												//List<String> tempList = addResIdAndFileIdListmap.get(resId);
												fileIds.add(fileId);
												addResIdAndFileIdListmap.put(resId, fileIds);
											}
										}else{//数据库res_file_relation中不为空
											List<String> dbFileIds = dbResIdAndFileIdListmap.get(resId);
											List<String> addFileIds = addResIdAndFileIdListmap.get(resId);
											if(!dbFileIds.contains(fileId)){
												if(addFileIds==null){
													ResFileRelation resFileRel = new ResFileRelation();
													resFileRel.setFileId(fileId);
													resFileRel.setResId(resId);
													resFileRel.setRestype(restype);
													resFileRel.setPosttype(posttype);
													resFileRel.setOrderId(Long.valueOf(orderId));
													getBaseDao().create(resFileRel);
													List<String> tempList = new ArrayList<String>();
													tempList.add(fileId);
													addResIdAndFileIdListmap.put(resId, tempList);
												}else{
													if(!addFileIds.contains(fileId)){
														ResFileRelation resFileRel = new ResFileRelation();
														resFileRel.setFileId(fileId);
														resFileRel.setResId(resId);
														resFileRel.setRestype(restype);
														resFileRel.setPosttype(posttype);
														resFileRel.setOrderId(Long.valueOf(orderId));
														getBaseDao().create(resFileRel);
														addFileIds.add(fileId);
														addResIdAndFileIdListmap.put(resId, addFileIds);
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}else{//选择目录
					Map<String, List<String>> dirMap = getDirsMap(dirs);//获得目录map
					for(int i=0;i<resArr.size();i++){//循环资源
						//循环资源
						Gson gson = new Gson();
						Ca ca = gson.fromJson(resArr.get(i).toString(), Ca.class);//资源Ca
						String resId = ca.getObjectId();//资源id
						Map<String, List<String>> dbResIdAndFileIdListmap = 
								getListByOrderIdAndResIds(Long.valueOf(orderId), resId,posttype,restype);//获得所有的文件 key:resId,   value:fileIds
						
						String detailUrl = PUBLISH_DETAIL_URL + "?id=" + resId;
						HttpClientUtil http = new HttpClientUtil();
						String resDetail = http.executeGet(detailUrl);
						if(resDetail!=null){
							Ca resDetailCa = gson.fromJson(resDetail, Ca.class);//资源详细
							List<File> fileList = resDetailCa.getRealFiles();
							Map<String, String> pathNameMap = getAliaPathName(resDetailCa);
							if(fileList!=null){
								for(File file : fileList){//循环单个资源下的文件
									String isDir = file.getIsDir();
									String fileId = file.getObjectId();//文件id
									String id = file.getId();
									String fileType = file.getFileType();
									if("2".equals(isDir)){//文件
										//向res_order_detail插入记录
										/*canAddResOrderDetailToDB(orderId, u,
												platformId, dbResIdList,
												addResIdList, resId);*/
										
										//向res_file_relation插入记录
										if(dbResIdAndFileIdListmap.isEmpty()){////据库res_file_relation中为空
											List<String> fileIds = addResIdAndFileIdListmap.get(resId);
											if(fileIds==null){
												int flag = canInsertToDB(
														dirMap, pathNameMap,
														id, fileType);
												if(flag==1){
													//向res_order_detail插入记录
													addResOrderDetailToDBs(orderId, u,
															platformId, dbResIdList,
															addResIdList, resId,restype,posttype);
													ResFileRelation resFileRel = new ResFileRelation();
													resFileRel.setFileId(fileId);
													resFileRel.setResId(resId);
													resFileRel.setRestype(restype);
													resFileRel.setPosttype(posttype);
													resFileRel.setOrderId(Long.valueOf(orderId));
													getBaseDao().create(resFileRel);
													List<String> tempList = new ArrayList<String>();
													tempList.add(fileId);
													addResIdAndFileIdListmap.put(resId, tempList);
												}
												
											}else{
												int flag = canInsertToDB(
														dirMap, pathNameMap,
														id, fileType);
												if(flag==1){
													//向res_order_detail插入记录
													addResOrderDetailToDBs(orderId, u,
															platformId, dbResIdList,
															addResIdList, resId,restype,posttype);
													ResFileRelation resFileRel = new ResFileRelation();
													resFileRel.setFileId(fileId);
													resFileRel.setResId(resId);
													resFileRel.setRestype(restype);
													resFileRel.setPosttype(posttype);
													resFileRel.setOrderId(Long.valueOf(orderId));
													getBaseDao().create(resFileRel);
													//List<String> tempList = addResIdAndFileIdListmap.get(resId);
													fileIds.add(fileId);
													addResIdAndFileIdListmap.put(resId, fileIds);
												}
											}
										}else{//据库res_file_relation中不为空
											List<String> dbFileIds = dbResIdAndFileIdListmap.get(resId);
											List<String> addFileIds = addResIdAndFileIdListmap.get(resId);
											if(!dbFileIds.contains(fileId)){
												if(addFileIds == null){
													int flag = canInsertToDB(
															dirMap, pathNameMap,
															id, fileType);
													if(flag==1){
														//向res_order_detail插入记录
														addResOrderDetailToDBs(orderId, u,
																platformId, dbResIdList,
																addResIdList, resId,restype,posttype);
														ResFileRelation resFileRel = new ResFileRelation();
														resFileRel.setFileId(fileId);
														resFileRel.setResId(resId);
														resFileRel.setRestype(restype);
														resFileRel.setPosttype(posttype);
														resFileRel.setOrderId(Long.valueOf(orderId));
														getBaseDao().create(resFileRel);
														List<String> tempList = new ArrayList<String>();
														tempList.add(fileId);
														addResIdAndFileIdListmap.put(resId, tempList);
													}
													
												}else{
													if(!addFileIds.contains(fileId)){
														int flag = canInsertToDB(
																dirMap, pathNameMap,
																id, fileType);
														if(flag==1){
															//向res_order_detail插入记录
															addResOrderDetailToDBs(orderId, u,
																	platformId, dbResIdList,
																	addResIdList, resId,restype,posttype);
															ResFileRelation resFileRel = new ResFileRelation();
															resFileRel.setFileId(fileId);
															resFileRel.setResId(resId);
															resFileRel.setRestype(restype);
															resFileRel.setPosttype(posttype);
															resFileRel.setOrderId(Long.valueOf(orderId));
															getBaseDao().create(resFileRel);
															addFileIds.add(fileId);
															addResIdAndFileIdListmap.put(resId, addFileIds);
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
				}
				SysOperateLogUtils.addLog("resOrder_addResource", "添加资源-全部页-按文件目录", userInfo);
			}
		}
	}

	private void canAddResOrderDetailToDB(String orderId, User u,
			int platformId, List<String> dbResIdList,
			List<String> addResIdList, String resId) {
		if(dbResIdList!=null){//数据库res_order_detail中不为空
			if(!dbResIdList.contains(resId)){//数据库res_order_detail中不存在resId
				if(!addResIdList.contains(resId)){//add中也没有resId
					ResOrderDetail rd = new ResOrderDetail();
					rd.setResId(resId);
					rd.setCreateUser(u);
					rd.setOrderId(Long.valueOf(orderId));
					rd.setPlatformId(platformId);
					getBaseDao().create(rd);
					addResIdList.add(resId);
				}
			}
		}else{//据库res_order_detail中为空
			if(!addResIdList.contains(resId)){//add中也没有resId
				ResOrderDetail rd = new ResOrderDetail();
				rd.setResId(resId);
				rd.setCreateUser(u);
				rd.setOrderId(Long.valueOf(orderId));
				rd.setPlatformId(platformId);
				getBaseDao().create(rd);
				addResIdList.add(resId);
			}
		}
	}
	public void addResOrderDetailToDBs(String orderId, User u,int platformId, List<String> dbResIdList,List<String> addResIdList, String resId,String restype,String posttype) {
		if(dbResIdList!=null){//数据库res_order_detail中不为空
			if(!dbResIdList.contains(resId)){//数据库res_order_detail中不存在resId
				if(!addResIdList.contains(resId)){//add中也没有resId
					ResOrderDetail rd = new ResOrderDetail();
					rd.setResId(resId);
					rd.setCreateUser(u);
					rd.setResType(restype);
					rd.setPosttype(posttype);
					rd.setOrderId(Long.valueOf(orderId));
					rd.setPlatformId(platformId);
					getBaseDao().create(rd);
					addResIdList.add(resId);
				}
			}
		}else{//据库res_order_detail中为空
			if(!addResIdList.contains(resId)){//add中也没有resId
				ResOrderDetail rd = new ResOrderDetail();
				rd.setResId(resId);
				rd.setCreateUser(u);
				rd.setResType(restype);
				rd.setPosttype(posttype);
				rd.setOrderId(Long.valueOf(orderId));
				rd.setPlatformId(platformId);
				getBaseDao().create(rd);
				addResIdList.add(resId);
			}
		}
	}
	

	private int canInsertToDB(Map<String, List<String>> dirMap,
			Map<String, String> pathNameMap, String id, String fileType) {
		logger.info("添加全部页 canInsertToDB  dirMap:"+dirMap.toString()+" pathNameMap:"+pathNameMap.toString()+" id:"+id+" fileType:"+fileType);
		Set<String> set = dirMap.keySet();
		Iterator<String> it = set.iterator();
		String tempId = id;
		tempId = tempId.replaceAll("\\\\", "\\/");
		String filePathName = "";
		if(tempId.contains("/")){
			String fileTempPathName = tempId.substring(0, tempId.lastIndexOf("/"));
			filePathName = pathNameMap.get(fileTempPathName);
		}
		int flag = 0;
		while(it.hasNext()){
			String content = it.next();
			List<String> types = dirMap.get(content);
			
			if (StringUtils.isNotBlank(filePathName)) {
				if (types!=null && types.size()>0) {
					if(filePathName.contains(content)){
						logger.info("添加全部页 canInsertToDB  if1 filePathName.contains(content) filePathName:"+filePathName+" content:"+content);
						if(types.contains(fileType)){
							logger.info("添加全部页 canInsertToDB  if2 types.contains(fileType) types:"+types+" fileType:"+fileType);
							flag = 1;
						}
					}
				}
			}
			
		}
		return flag;
	}
	
	public void setValToObj(ResOrderDetail resDetail, Map<String, JSONObject> map, String orderId){
		Set<String> set = map.keySet();
		Iterator<String> it = set.iterator();
		while(it.hasNext()){
			String key = it.next();
			JSONObject data = (JSONObject) map.get(key);
			String resType = data.get("libType")+"";
			String module = data.get("module")+"";
			String version = data.get("version")+"";
			String educational_phase = data.get("educational_phase")+"";
			String subject = data.get("subject")+"";
			String grade = data.get("grade")+"";
			String fascicule = data.get("fascicule")+"";
			String format = data.get("format")+"";
			String keywords = data.get("keywords")+"";
			String title = data.get("title")+"";
			String creator = data.get("creator")+"";
			String modified_time = data.get("modified_time")+"";
			String unit = data.get("unit")+"";
			addValToResOrderDetail(resDetail, orderId, resType, module,
					version, educational_phase, subject, grade, fascicule,
					format, keywords, title, creator, modified_time, unit);
		}
	}
	
	
	public void setValToObjByDetail(ResOrderDetail resDetail, JSONObject data, String orderId){
			String libType = data.getJSONObject("commonMetaDatas").get("libType")+"";
			String resType = data.getJSONObject("commonMetaDatas").get("type")+"";
			resType = SystemConstants.ResourceType.getValueByKey(resType);
			String module = data.getJSONObject("commonMetaDatas").get("module")+"";
			module = SystemConstants.ResourceMoudle.getValueByKey(module);
			String version = data.getJSONObject("commonMetaDatas").get("versionName")+"";
			String educational_phase = data.getJSONObject("commonMetaDatas").get("educational_phase_name")+"";
			String subject = data.getJSONObject("commonMetaDatas").get("subjectName")+"";
			String grade = data.getJSONObject("commonMetaDatas").get("gradeName")+"";
			String fascicule = data.getJSONObject("commonMetaDatas").get("fasciculeName")+"";
			String format = data.getJSONObject("commonMetaDatas").get("format")+"";
			String keywords = data.getJSONObject("commonMetaDatas").get("keywords")+"";
			String title = data.getJSONObject("commonMetaDatas").get("title")+"";
			String creator = data.getJSONObject("commonMetaDatas").get("creator")+"";
			String modified_time = data.getJSONObject("commonMetaDatas").get("modified_time")+"";
			String unit = data.getJSONObject("commonMetaDatas").get("unitName")+"";
			resDetail.setResType(resType);
			addValToResOrderDetail(resDetail, orderId, libType, module,
					version, educational_phase, subject, grade, fascicule,
					format, keywords, title, creator, modified_time, unit);
	}

	private void addValToResOrderDetail(ResOrderDetail resDetail,
			String orderId, String resType, String module, String version,
			String educational_phase, String subject, String grade,
			String fascicule, String format, String keywords, String title,
			String creator, String modified_time, String unit) {
		resDetail.setOrderId(Long.valueOf(orderId));
		resDetail.setCreateTime(new Date());
		resDetail.setStatus("1");
		resDetail.setResTypeId(resType);
		resDetail.setModule(module);
		resDetail.setVersion(version);
		resDetail.setEducationalPhase(educational_phase);
		resDetail.setSubject(subject);
		resDetail.setGrade(grade);
		resDetail.setFascicule(fascicule);
		System.out.println(StringUtils.isNotBlank(keywords));
		if(StringUtils.isNotBlank(format)){
			resDetail.setFormat(format);
		}else{
			resDetail.setFormat("");
		}
		if(StringUtils.isNotEmpty(keywords)){
			resDetail.setKeywords(keywords);
		}else{
			resDetail.setKeywords("");
		}
		resDetail.setTitle(title);
		if(StringUtils.isNotBlank(creator)){
			resDetail.setCreator(creator);
		}else{
			resDetail.setCreator("");
		}
		if(StringUtils.isNotBlank(modified_time)){
			resDetail.setModifiedTime(modified_time);
		}else{
			resDetail.setModifiedTime("");
		}
		if(StringUtils.isNotBlank(unit)){
			resDetail.setUnit(unit);
		}else{
			resDetail.setUnit("");
		}
	}
	
	@Override
	public String getFileTree(String contentType, String checkedFile, int page, int size, String param, Long orderId,String restype){
		HttpClientUtil http = new HttpClientUtil();
		JSONArray array = new JSONArray();
		//ResOrder resOrder = (ResOrder) getBaseDao().getByPk(ResOrder.class, orderId);
		
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		User user = (User) getByPk(User.class, userInfo.getUserId());
		String resourceDataJson = user.getResourceDataJson();
		JSONArray jsonArray = JSONArray.fromObject(resourceDataJson);
		String fields = "";
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			if(jsonObject.getString("id").equals(restype)){
				fields = jsonObject.getString("field");
			}
		}
		/*ProdParamsTemplate template = resOrder.getTemplate();
		String publishType = template.getType();*/
		String allUrl = PUBLISH_QUERY_URL + "?queryType=0&publishType=" + restype  + "&page="+ page + "&size=1000000" + "&status=3";
		String url = PUBLISH_QUERY_URL + "?queryType=0&publishType=" + restype  + "&page="+ page + "&size=" + size + "&status=3";
		
		//判断当前登录人是否被授权,授权后只能在添加可选资源中查看与自己模板相对应的自己上传的资源
		String userIds = userInfo.getDeptUserIds();
		int isPrivate = userInfo.getIsPrivate();
		if (isPrivate == 1) {
			if(StringUtils.isNotBlank(userIds)){
			}else{
				userIds = userInfo.getUserId()+"";
			}
		}
//		else{
//			userIds = userInfo.getDeptUserIds();
//		}
		
		
		if (StringUtils.isNotBlank(userIds)) {
			if(userIds.endsWith(",")){
				userIds = userIds.substring(0,userIds.length()-1);
			}
			allUrl+="&creator="+userIds;
			url+="&creator="+userIds;
		}else{
			allUrl+="&creator=-2";
			url+="&creator=-2";
		}
		try{
			String allResources = http.executeGet(allUrl);//超时
			JSONObject allJson = JSONObject.fromObject(allResources);
			if(allJson!=null){
				JSONArray allResult = (JSONArray) allJson.get("rows");
				String resources = http.executeGet(url);
				Gson gson = new Gson();
				JSONObject json = JSONObject.fromObject(resources);
				if(json!=null){
					JSONArray result = (JSONArray) json.get("rows");
					int count = 0;
					if(result!=null){
						for(int i=0;i<result.size();i++){
							Ca ca = gson.fromJson(result.get(i).toString(), Ca.class);
							Map<String, String> metadataMap = ca.getMetadataMap();
							String resName = metadataMap.get("title");
							//String title = metadataMap.get("title");
							String resId = ca.getObjectId();
							String detailUrl= PUBLISH_DETAIL_URL + "?id="+ resId;
							String detail = http.executeGet(detailUrl);
							Ca caDetail = gson.fromJson(detail, Ca.class);
							List<File> listFile = caDetail.getRealFiles();
							Map<String, Integer> map = getTreeLevel(listFile);
							if(listFile!=null){
								String rootpath="";
								String fieldspath = "";
								for(File file : listFile){
									
									if(file.getPid().equals("-1")){
										rootpath = file.getId();
										JSONObject jsonObj = new JSONObject();
										String id = file.getId();
										String fileType = file.getFileType();
										String path = file.getPath();
										if(map!=null){
											if(map.get(id)!=null){
												if(map.get(id)==2||map.get(id)==3||map.get(id)==4){
													jsonObj.put("open", true);
												}
											}
										}
										String fileId = file.getObjectId();
										String pId = file.getPid();
										String name = file.getName();
										String isDir = file.getIsDir();//1：目录， 2：文件
										String tempId = id;
										if(StringUtils.isNotEmpty(id)){
											id = addWord(count, id);
											if("".equals(pId)||"-1".equals(pId)){
												pId = resId;
											}else{
												pId = addWord(count, pId);
											}
											
											jsonObj.put("id", id);
											jsonObj.put("pId", pId);
											jsonObj.put("name", name);
											jsonObj.put("flag", isDir);
											jsonObj.put("fileType", fileType);
											jsonObj.put("path", path);
											jsonObj.put("objectId", fileId);
											jsonObj.put("caId", resId);
												jsonObj.put("filePathName", "");
											array.add(jsonObj);
										}
									}else if(file.getPid().equals(rootpath)){
										String fieldss[]=fields.split(",");
										for (String field : fieldss) {
											if(file.getId().toLowerCase().contains("/"+field.trim().toLowerCase())){
												fieldspath = file.getId();
												JSONObject jsonObj = new JSONObject();
												String id = file.getId();
												String fileType = file.getFileType();
												String path = file.getPath();
												if(map!=null){
													if(map.get(id)!=null){
														if(map.get(id)==2||map.get(id)==3||map.get(id)==4){
															jsonObj.put("open", true);
														}
													}
												}
												String fileId = file.getObjectId();
												String pId = file.getPid();
												String name = file.getName();
												String isDir = file.getIsDir();//1：目录， 2：文件
												String tempId = id;
												if(StringUtils.isNotEmpty(id)){
													id = addWord(count, id);
													if("".equals(pId)||"-1".equals(pId)){
														pId = resId;
													}else{
														pId = addWord(count, pId);
													}
													
													jsonObj.put("id", id);
													jsonObj.put("pId", pId);
													jsonObj.put("name", name);
													jsonObj.put("flag", isDir);
													jsonObj.put("fileType", fileType);
													jsonObj.put("path", path);
													jsonObj.put("objectId", fileId);
													jsonObj.put("caId", resId);
													if("2".equals(isDir)){
														if(StringUtils.isNotEmpty(tempId)){
															tempId = tempId.replaceAll("\\\\", "\\/");
															if(tempId.contains("/")){
																String fileTempPathName = tempId.substring(0, tempId.lastIndexOf("/"));
																Map<String, String> dirMap = getAliaPathName(caDetail);
																String filePathName = dirMap.get(fileTempPathName);
																if(StringUtils.isNotEmpty(filePathName)){
																	jsonObj.put("filePathName", filePathName);
																}else{
																	jsonObj.put("filePathName", "");
																}
															}
														}
													}else{
														jsonObj.put("filePathName", "");
													}
													array.add(jsonObj);
												}
											}
										}
									}else if(file.getPid().indexOf(fieldspath)==0){
										JSONObject jsonObj = new JSONObject();
										String id = file.getId();
										String fileType = file.getFileType();
										String path = file.getPath();
										if(map!=null){
											if(map.get(id)!=null){
												if(map.get(id)==2||map.get(id)==3||map.get(id)==4){
													jsonObj.put("open", true);
												}
											}
										}
										String fileId = file.getObjectId();
										String pId = file.getPid();
										String name = file.getName();
										String isDir = file.getIsDir();//1：目录， 2：文件
										String tempId = id;
										if(StringUtils.isNotEmpty(id)){
											id = addWord(count, id);
											if("".equals(pId)||"-1".equals(pId)){
												pId = resId;
											}else{
												pId = addWord(count, pId);
											}
											
											jsonObj.put("id", id);
											jsonObj.put("pId", pId);
											jsonObj.put("name", name);
											jsonObj.put("flag", isDir);
											jsonObj.put("fileType", fileType);
											jsonObj.put("path", path);
											jsonObj.put("objectId", fileId);
											jsonObj.put("caId", resId);
											if("2".equals(isDir)){
												if(StringUtils.isNotEmpty(tempId)){
													tempId = tempId.replaceAll("\\\\", "\\/");
													if(tempId.contains("/")){
														String fileTempPathName = tempId.substring(0, tempId.lastIndexOf("/"));
														Map<String, String> dirMap = getAliaPathName(caDetail);
														String filePathName = dirMap.get(fileTempPathName);
														if(StringUtils.isNotEmpty(filePathName)){
															jsonObj.put("filePathName", filePathName);
														}else{
															jsonObj.put("filePathName", "");
														}
													}
												}
											}else{
												jsonObj.put("filePathName", "");
											}
											array.add(jsonObj);
										}
										
									}
								}
							}
							JSONObject resObj = new JSONObject();
							resObj.put("id", resId);
							resObj.put("name", resName);
							resObj.put("open", true);
							resObj.put("flag", "1");
							resObj.put("pId", "-3");
							array.add(resObj);
							count++;
						}
					}
				}
				if(page==1&&"0".equals(param)){
					JSONObject jsonObjPar = new JSONObject();
					jsonObjPar.put("id", "-3");
					jsonObjPar.put("name", "资源文件");
					jsonObjPar.put("open", true);
					jsonObjPar.put("flag", "1");
					jsonObjPar.put("pId", "-4");
					jsonObjPar.put("count", allResult.size());
					jsonObjPar.put("page", 1);
					jsonObjPar.put("pageSize", 50);
					jsonObjPar.put("isParent", true);
					array.add(jsonObjPar);
				}
			}
		} catch(Exception e) {
			logger.error("资源查询出错...");
			return null;
		}
		return array.toString();
	}
	
	public String getFileTreeJsonByQueryCondition(int page, 
			int size, String param, Long orderId){
		HttpClientUtil http = new HttpClientUtil();
		JSONArray array = new JSONArray();
		ResOrder resOrder = (ResOrder) getBaseDao().getByPk(ResOrder.class, orderId);
		
		ProdParamsTemplate template = resOrder.getTemplate();
		String publishType = template.getType();
		String allUrl = PUBLISH_QUERY_URL + "?status=3&publishType=" + publishType  + "&page="+ page + "&size=1000000";
		String url = PUBLISH_QUERY_URL + "?status=3&publishType=" + publishType  + "&page="+ page + "&size=" + size;
		if(StringUtils.isNotEmpty(param)){
			try {
				allUrl += "&queryType=1";
				url += "&queryType=1";
				param = URLEncoder.encode(param, "UTF-8");
				allUrl += "&metadataMap=" + param;
				url += "&metadataMap=" + param;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}else{
			allUrl += "&queryType=0";
			url += "&queryType=0";
		}
		String userIds = LoginUserUtil.getLoginUser().getDeptUserIds();
		int isPrivate = LoginUserUtil.getLoginUser().getIsPrivate();
		if (isPrivate == 1) {
			if(StringUtils.isNotBlank(userIds)){
				if(userIds.endsWith(",")){
					userIds += LoginUserUtil.getLoginUser().getUserId();
				}else{
					userIds += "," + LoginUserUtil.getLoginUser().getUserId();
				}
			}else{
				userIds = LoginUserUtil.getLoginUser().getUserId()+"";
			}
		} 
		if (StringUtils.isNotBlank(userIds)) {
			if(userIds.endsWith(",")){
				userIds = userIds.substring(0,userIds.length()-1);
			}
			allUrl+="&creator="+userIds;
			url+="&creator="+userIds;
		}else{
			allUrl+="&creator=-2";
			url+="&creator=-2";
		}
		
		try{
			String allResources = http.executeGet(allUrl);
			JSONObject allJson = JSONObject.fromObject(allResources);
			if(allJson!=null){
				JSONArray allResult = (JSONArray) allJson.get("rows");
				String resources = http.executeGet(url);
				Gson gson = new Gson();
				JSONObject json = JSONObject.fromObject(resources);
				if(json!=null){
					JSONArray result = (JSONArray) json.get("rows");
					if(result!=null){
						int count = 0;
						for(int i=0;i<result.size();i++){
							Ca ca = gson.fromJson(result.get(i).toString(), Ca.class);
							Map<String, String> metadataMap = ca.getMetadataMap();
							String resName = metadataMap.get("title");
							//TODO
							//String title = metadataMap.get("title");
							String resId = ca.getObjectId();
							String detailUrl= PUBLISH_DETAIL_URL + "?id="+ resId;
							String detail = http.executeGet(detailUrl);
							Ca caDetail = gson.fromJson(detail, Ca.class);
							List<File> listFile = caDetail.getRealFiles();
							Map<String, Integer> map = getTreeLevel(listFile);
							if(listFile!=null){
								for(File file : listFile){
									JSONObject jsonObj = new JSONObject();
									String id = file.getId();
									String fileType = file.getFileType();
									String path = file.getPath();
									if(map!=null){
										if(map.get(id)!=null){
											if(map.get(id)==2||map.get(id)==3||map.get(id)==4){
												jsonObj.put("open", true);
											}
										}
									}
									String fileId = file.getObjectId();
									String pId = file.getPid();
									String name = file.getName();
									String isDir = file.getIsDir();//1：目录， 2：文件
									String tempId = id;
									if(StringUtils.isNotEmpty(id)){
										id = addWord(count, id);
										if("".equals(pId)||"-1".equals(pId)){
											pId = resId;
										}else{
											pId = addWord(count, pId);
										}
										jsonObj.put("id", id);
										jsonObj.put("pId", pId);
										jsonObj.put("name", name);
										jsonObj.put("flag", isDir);
										jsonObj.put("fileType", fileType);
										jsonObj.put("path", path);
										jsonObj.put("objectId", fileId);
										jsonObj.put("caId", resId);
										if("2".equals(isDir)){
											if(StringUtils.isNotEmpty(tempId)){
												tempId = tempId.replaceAll("\\\\", "\\/");
												if(tempId.contains("/")){
													String fileTempPathName = tempId.substring(0, tempId.lastIndexOf("/"));
													Map<String, String> dirMap = getAliaPathName(caDetail);
													String filePathName = dirMap.get(fileTempPathName);
													if(StringUtils.isNotEmpty(filePathName)){
														jsonObj.put("filePathName", filePathName);
													}else{
														jsonObj.put("filePathName", "");
													}
												}
											}
										}else{
											jsonObj.put("filePathName", "");
										}
										array.add(jsonObj);
									}
								}
							}
							JSONObject resObj = new JSONObject();
							resObj.put("id", resId);
							resObj.put("name", resName);
							resObj.put("open", true);
							resObj.put("flag", "1");
							resObj.put("pId", "-3");
							array.add(resObj);
							count++;
						}
					}
				}
				if(page==1&&"0".equals(param)){
					JSONObject jsonObjPar = new JSONObject();
					jsonObjPar.put("id", "-3");
					jsonObjPar.put("name", "资源文件");
					jsonObjPar.put("open", true);
					jsonObjPar.put("flag", "1");
					jsonObjPar.put("pId", "-4");
					jsonObjPar.put("count", allResult.size());
					jsonObjPar.put("page", 1);
					jsonObjPar.put("pageSize", 1);
					jsonObjPar.put("isParent", true);
					array.add(jsonObjPar);
				}
			}
		} catch(Exception e) {
			logger.error("资源查询出错...");
			return null;
		}
		return array.toString();
	}
	
	private Map<String, Integer> getTreeLevel(List<File> listFile){
		Map<String, Integer> map = new LinkedHashMap<String, Integer>();
		List<String> list2Lev = new ArrayList<String>();
		List<String> list3Lev = new ArrayList<String>();
		List<String> list4Lev = new ArrayList<String>();
		if(listFile!=null){
			for(File f: listFile){
				String pid = f.getPid();
				String id = f.getId();
				if("-1".equals(pid)||"".equals(pid)){
					list2Lev.add(id);
					map.put(id, 2);
				}
			}
			for(File f: listFile){
				String pid = f.getPid();
				String id = f.getId();
				if(list2Lev.contains(pid)){
					if(!map.containsKey(id)){
						list3Lev.add(id);
						map.put(id, 3);
					}
				}
			}
			for(File f: listFile){
				String pid = f.getPid();
				String id = f.getId();
				if(list3Lev.contains(pid)){
					if(!map.containsKey(id)){
						list4Lev.add(id);
						map.put(id, 4);
					}
				}
			}
		}
		return map;
	}
	
	public String queryResforTreePage(int page, int size){
		HttpClientUtil http = new HttpClientUtil();
		JSONArray array = new JSONArray();
		String url = PUBLISH_QUERY_URL + "?page="+ page + "&size=" + size;
		String resources = http.executeGet(url);
		Gson gson = new Gson();
		JSONObject json = JSONObject.fromObject(resources);
		JSONArray result = (JSONArray) json.get("rows");
		for(int i=0;i<result.size();i++){
			Ca ca = gson.fromJson(result.get(i).toString(), Ca.class);
			Map<String, String> metadataMap = ca.getMetadataMap();
			//TODO
			String title = metadataMap.get("title");
			String resId = ca.getObjectId();
			String detailUrl= PUBLISH_DETAIL_URL + "?id="+ resId;
			String detail = http.executeGet(detailUrl);
			Ca caDetail = gson.fromJson(detail, Ca.class);
			List<File> listFile = caDetail.getRealFiles();
			int count = 0;
			for(File file : listFile){
				String id = file.getId();
				String pId = file.getPid();
				String isDir = file.getIsDir();//1：目录， 2：文件
				id = addWord(count, id);
				pId = addWord(count, pId);
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("", "");
				
			}
			
		}
		return array.toString();
	}
	
	public String getDirTree(Long orderId,String restype){
		JSONArray array = new JSONArray();
		if(LoginUserUtil.getLoginUser() != null){
			//ResOrder resOrder = (ResOrder) getBaseDao().getByPk(ResOrder.class, orderId);
			/*ProdParamsTemplate template = resOrder.getTemplate();
			String resType = template.getType();*/
			int platformId = LoginUserUtil.getLoginUser().getPlatformId();
			UserInfo userInfo = LoginUserUtil.getLoginUser();
			User user = (User) getByPk(User.class, userInfo.getUserId());
			String resourceDataJson = user.getResourceDataJson();
			JSONArray jsonArray = JSONArray.fromObject(resourceDataJson);
			String fields = "";
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				if(jsonObject.getString("id").equals(restype)){
					fields = jsonObject.getString("field");
				}
			}
			JSONObject json = new JSONObject();
			json.put("id", 0);
			json.put("name", "资源目录");
			json.put("open", true);
			json.put("pId", -1);
			json.put("flag", "_dirs");
			array.add(json);
			String hql = " from SysDir where status='" + Status.STATUS1 + "' and resType='" + restype + "'";
			List<SysDir> dirList = getBaseDao().query(hql);
			int count = 1;
			for(SysDir dir: dirList) {
				if(StringUtils.isNotBlank(fields)){
					String fieldss[] = fields.split(",");
					for (String field : fieldss) {
						if(!field.trim().toLowerCase().equals("main.xml")){
						if(dir.getDirCnName().equals(field.trim())){
							int pId = 0;
							String types = dir.getFileTypes();
							String name = dir.getDirCnName();
							json.put("id", count);
							json.put("name", name);
							json.put("open", true);
							json.put("pId", 0);
							json.put("flag", "_dir");
							pId = count;
							count++;
							array.add(json);
							if(StringUtils.isNotEmpty(types)) {
								String[] typeArr = types.split(",");
								for(String type1 : typeArr) {
									json.put("id", count);
									json.put("name", type1);
									json.put("pId", pId);
									json.put("open", true);
									json.put("flag", "_file");
									count++;
									array.add(json);
								}
							}
						}
					}
					}
				}
				
			}
		}
		return array.toString();
	}
	
	private String addWord(int i, String str){
		if("-1".equals(str)){
			return str;
		}else{
			String[] arrs = str.split("/");
			str = "";
			for(String arr: arrs){
				str += i + arr + "/";
			}
		}
		if(str!=null&&str.length()>1){
			str = str.substring(0, str.length()-1);
		}
		return str;
	}
	
	public Map<String, List<String>> getListByOrderIdAndResId(Long orderId, String resId){
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		String hql = " from ResFileRelation where resId='" + resId + "' and orderId=" + orderId ;
		List<String> fileIdList = new ArrayList<String>();
		@SuppressWarnings("unchecked")
		List<ResFileRelation> list = getBaseDao().query(hql);
		if(list!=null){
			for(ResFileRelation file: list){
				String fileId = file.getFileId();
				fileIdList.add(fileId);
			}
			map.put(resId, fileIdList);
		}
		return map;
	}
	public Map<String, List<String>> getListByOrderIdAndResIds(Long orderId, String resId,String posttype,String restype){
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		String hql = " from ResFileRelation where resId='" + resId + "' and orderId=" + orderId +" and restype='"+restype+"' and posttype='"+posttype+"'";
		List<String> fileIdList = new ArrayList<String>();
		@SuppressWarnings("unchecked")
		List<ResFileRelation> list = getBaseDao().query(hql);
		if(list!=null){
			for(ResFileRelation file: list){
				String fileId = file.getFileId();
				fileIdList.add(fileId);
			}
			map.put(resId, fileIdList);
		}
		return map;
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public PageResult getPageResultByOrderId(String orderId, QueryConditionList conditionList) {
		// TODO Auto-generated method stub
		String hql = " from ResOrderDetail where orderId=" + Long.valueOf(orderId);
		int pageSize = conditionList.getPageSize();
		int page = conditionList.getStartIndex();
		//pageSize每页显示多少条      page当前所在也
		Session session = getSession();
		Query query = session.createQuery(hql);
		query.setFirstResult((page-1)*pageSize); 
		query.setMaxResults(pageSize); 
		List<ResOrderDetail> detailList = query.list();
		PageResult pageResult = new PageResult();
		List<Ca> list = new ArrayList<Ca>();
		if(detailList!=null){
			Long total = (long) detailList.size();
			pageResult.setTotal(total);
			String baseUrl = PUBLISH_DETAIL_URL;
			for(ResOrderDetail detail : detailList){
				String resId = detail.getResId();
				String url = baseUrl + "resId=" + resId;
				HttpClientUtil http = new HttpClientUtil();
				String resourceDetail =  http.executeGet(url);
				Gson gson = new Gson();
				Ca ca = gson.fromJson(resourceDetail, Ca.class);
				list.add(ca);
			}
		}else{
			pageResult.setTotal(0);
		}
		pageResult.setRows(list);
		return null;
	}
	
	
	/**
	 * 查询单条资源列表信息
	 */
	public String queryResourceList(String resId, String page, String size) {
		HttpClientUtil http = new HttpClientUtil();
		String url = PUBLISH_QUERY_URL;
		if(page==null) {
			page="1";
		}
		if(size==null) {
			size="0";
		}
		String formList = "";
		if(StringUtils.isNotBlank(resId)){
			url += "?queryType=0&pubObjectIds=" + resId + "&page=" + page + "&size=" + size;
		    formList = http.executeGet(url);
		}
		return formList;
	}
	
	/**
	 * 查询多条资源列表信息
	 */
	public String queryResourceLists(String resIds, String page, String size) {
		HttpClientUtil http = new HttpClientUtil();
		SearchParamCa searchParamCa = new SearchParamCa();
		searchParamCa.setQueryType(0);
		searchParamCa.setObjectIds(resIds);
		searchParamCa.setPage("1");
		searchParamCa.setSize(99);
		Gson gson = new Gson();
		String paraJson = gson.toJson(searchParamCa);
		return http.postJson(PUBLISH_QUERYBYPOST_URL, paraJson);

	}
	
	public static void main(String[] args) {
		/*String dirs = "[{'hgs':'{ghfdgh,q,d,7}'},{'12':'{pdf}'}]";
		JSONArray arr = JSONArray.fromObject(dirs);
		int len = arr.size();
		for(int i=0;i<len;i++){
			JSONObject st = (JSONObject) arr.get(i);
			Iterator<String> it = st.keys();
			while(it.hasNext()){
				String key = it.next();
				String value = (String) st.get(key);
				System.out.println(key + "  " + value);
			}
			
		}*/
		HttpClientUtil http = new HttpClientUtil();
		String url = "http://10.130.39.2:9000/semantic_index_server/publish/ontologyDetailQuery/ca?id="
				+ "urn:publish-4a5b8def-17a6-4d20-b984-2c6832f2982f";
		String resource = http.executeGet(url);
		Gson gson = new Gson();
		Ca ca = gson.fromJson(resource, Ca.class);
		Map<String, String> map = new HashMap<String, String>();
		List<File> fileList = ca.getRealFiles();
		for(File f : fileList){
			String fileId = f.getId();
			String name = f.getName();
			System.out.println(fileId+"   *******************   " + name);
			
		}
	}

	@Override
	public List<ResFileRelation> queryFileDetail(Long orderId, String resId,String posttype) {
		// TODO Auto-generated method stub
		String hql = " from ResFileRelation where orderId=" + orderId + " and resId='" + resId + "' and posttype="+posttype;
		List<ResFileRelation> list = baseDao.query(hql);
		return list;
	}
	
	private JSONArray getResourcesByCondition(String conditions, String resType){
		//TODO
		HttpClientUtil http = new HttpClientUtil();
		String url = PUBLISH_QUERY_URL + "?page=1&size=10000000&publishType=" + resType + "&status=3";
		
		if(conditions!=null&&!"''".equals(conditions)){
			try {
				url +="&queryType=1";
				conditions = URLEncoder.encode(conditions, "UTF-8");
				url += "&metadataMap=" + conditions;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}else {
			url +="&queryType=0";
		}
		String userIds = LoginUserUtil.getLoginUser().getDeptUserIds();
		if (StringUtils.isNotBlank(userIds)) {
			if(userIds.endsWith(",")){
				userIds = userIds.substring(0,userIds.length()-1);
			}
			url+="&creator="+userIds;
		}else{
			url+="&creator=-2";
		}
		String allResources = http.executeGet(url);
		logger.info("添加资源（添加全部页方法中） 带参数查询："+conditions+" 查询结果："+allResources);
		JSONObject allJson = JSONObject.fromObject(allResources);
		JSONArray allResult = (JSONArray) allJson.get("rows");
		return allResult;
	}
	
	//将目录文件组装成map集合
	private Map<String, List<String>> getDirsMap(String dirs){
		Map<String, List<String>> map = new HashMap<String, List<String>>();//key:文件目录 value:文件类型
		if(StringUtils.isNotEmpty(dirs)&&!"''".equals(dirs)){
			JSONArray arr = JSONArray.fromObject(dirs);
			int len = arr.size();
			for(int index=0;index<len;index++){
				JSONObject st = (JSONObject) arr.get(index);
				Iterator<String> it = st.keys();
				List<String> typeList = new ArrayList<String>();
				String key = "";
				while(it.hasNext()){
					key = it.next();
					String value = (String) st.get(key);
					System.out.println(key + "  " + value);
					if(value!=null&&!"".equals(value)){
						String[] types = value.split(",");
						for(String type: types){
							typeList.add(type);
						}
					}
				}
				map.put(key, typeList);
			}
		}
		return map;
	}
	
	//判断文件路径是否包含目录集合
	private boolean isContain(List<String> pathList, Set<String> dirSet){
		boolean flag = false;
		for(String path : pathList){
			if(dirSet.contains(path)){
				return true;
			}
		}
		return flag;
	}
	
	@Override
	public List<String> getResIdByOrderId(String orderId) {
		//List<String> list = new ArrayList<String>();
		String hql = "select resId from ResOrderDetail where orderId=" + Long.valueOf(orderId);
		List<String> list = getBaseDao().query(hql);
		if(list==null){
			list = new ArrayList<String>();
		}
		return list;
	}
	
	
	@Override
	public List<String> getResIdByOrderIds(String orderId, String restype,String posttype) {
		String hql = "select resId from ResOrderDetail where orderId=" + Long.valueOf(orderId)+" and resType='"+restype+"' and posttype='"+posttype+"'";
		List<String> list = getBaseDao().query(hql);
		if(list==null){
			list = new ArrayList<String>();
		}
		return list;
	}

	@Override
	public ResFileRelation queryFileByfileId(Long orderId,String fileId,String posttype) {
		// 
		String hql = " from ResFileRelation where orderId=" + orderId +" and fileId='" + fileId + "' and posttype='"+posttype+"'";
		Query query = getSession().createQuery(hql);
		//query.setFirstResult(0);  // 从第0条记录开始取
		//query.setMaxResults(30); // 取30条记录
		List<ResFileRelation> list = query.list();
		if(list != null && list.size() > 0){
			return list.get(0);
		}else{
			return null;
		}
	}
	
	@Override
	public List<ResFileRelation> queryFileByOrdeId(Long orderId) {
		// 
		String hql = " from ResFileRelation where orderId=" + orderId;
		Query query = getSession().createQuery(hql);
		//query.setFirstResult(0);  // 从第0条记录开始取
		//query.setMaxResults(30); // 取30条记录
		List<ResFileRelation> list = query.list();
		return list;
	}
	@Override
	public List<ResFileRelation> queryFileByOrdeIdAndposttype(Long orderId,String posttype) {
		// 
		String hql = " from ResFileRelation where orderId=" + orderId+" and posttype="+posttype;
		Query query = getSession().createQuery(hql);
		//query.setFirstResult(0);  // 从第0条记录开始取
		//query.setMaxResults(30); // 取30条记录
		List<ResFileRelation> list = query.list();
		return list;
	}
	
	
	@Override
	public List<ResFileRelation> queryFileByOrdeIdAndProcessStatus(Long orderId){
		String hql = " from ResFileRelation where orderId=" + orderId + " and processStatus=" + ResReleaseConstant.FileProcessStatus.FILE_PROCESS_SUCCESS;
		Query query = getSession().createQuery(hql);
		//query.setFirstResult(0);  // 从第0条记录开始取
		//query.setMaxResults(1000); // 取1000条记录
		List<ResFileRelation> list = query.list();
		return list;
	}
	@Override
	public ResFileRelation queryFileByOrdeIdAndFileId(Long orderId, String fileId){
		String hql = " from ResFileRelation where orderId=" + orderId + " and fileId='" + fileId + "'";
		List<ResFileRelation> list = getBaseDao().query(hql);
		ResFileRelation rf = null;
		if(list!=null){
			rf = list.get(0);
		}
		return rf;
	}

	@Override
	public ResFileRelation queryFileByOrdeIdAndFileIdAndResId(Long orderId,
			String fileId, String resId) {
		String hql = " from ResFileRelation where orderId=" + orderId + " and fileId='" + fileId + "'" + " and resId='" + resId + "'";
		List<ResFileRelation> list = getBaseDao().query(hql);
		ResFileRelation rf = null;
		if(list!=null){
			rf = list.get(0);
		}
		return rf;
	}
	
	@Override
	public List<ResFileRelation> queryFileByOrdeIdAndResId(Long orderId, String resId) {
		String hql = " from ResFileRelation where orderId=" + orderId + " and resId='" + resId + "'";
		List<ResFileRelation> list = getBaseDao().query(hql);
		return list;
	}
	
	private Map<String, String> getAliaPathName(Ca oldCa){

		Map<String, String> newDirMap = new TreeMap<String, String>();
		List<com.brainsoon.semantic.ontology.model.File> oldFiles = oldCa
				.getRealFiles();
		Map<String, String> dirMap = new TreeMap<String, String>();
		if(oldFiles!=null){
			for (com.brainsoon.semantic.ontology.model.File oldFile : oldFiles) {
				String isDir = oldFile.getIsDir();
				String id = oldFile.getId();
				if(StringUtils.isNotEmpty(id)){
					id = id.replaceAll("\\\\", "/");
					if ("1".equals(isDir)) {
						if(StringUtils.isNotEmpty(id)){
							if (id.contains("/")) {
								if(id.length()>1){
									id = id.substring(0, id.length() - 1);
									logger.info(id + "==========" + oldFile.getName());
									dirMap.put(id, oldFile.getName());
								}
							}else{
								dirMap.put(id, oldFile.getName());
							}
						}
					}
				}
			}
			Set<String> set = new HashSet<String>();
			if(dirMap!=null){
				for (String key : dirMap.keySet()) {
					String value = dirMap.get(key);
					if (key.indexOf("/") > 0) {
						String name = dirMap.get(key);
						String lastDir = key.substring(0, key.lastIndexOf("/"));
						String lastName = dirMap.get(lastDir);
						if (StringUtils.isNotBlank(lastName)) {
							dirMap.put(key, lastName + "/" + name);
							set.add(lastName + "/" + name);
						}
					} else {
						set.add(value);
					}
				}
			}
			for (com.brainsoon.semantic.ontology.model.File oldFile : oldFiles) {
				String isDir = oldFile.getIsDir();
				String id = oldFile.getId();
				if(StringUtils.isNotEmpty(id)){
					id = id.replaceAll("\\\\", "/");
					String tempStr = id;
					if ("2".equals(isDir)) {
						if(StringUtils.isNotEmpty(id)){
							if (id.contains("/")) {
								id = id.substring(0, id.lastIndexOf("/"));
								dirMap.put(tempStr,dirMap.get(id)+ oldFile.getName());
							}else{
								dirMap.put(tempStr, oldFile.getName());
							}
						}
					}

				}
			}
			for (Map.Entry<String, String> entry : dirMap.entrySet()) {
				logger.info("key= " + entry.getKey() + " and value= "
						+ entry.getValue());
			}
		}
		return dirMap;
	}

	@Override
	public List<ResOrderDetail> getResOrderDetailByOrderId(String orderId,String resid, String posttype) {
		String hql = " from ResOrderDetail where orderId=" + Long.decode(orderId) + " and posttype='" + posttype + "'" + " and resId='" + resid + "'";
		List<ResOrderDetail> list = getBaseDao().query(hql);
		return list;
	}

	@Override
	public String addResResource(String resId, String orderId, String posttype,String restype) {
		String resid[] = resId.split(",");
		HttpClientUtil http = new HttpClientUtil();
		String PUBLISH_DETAIL_URL =  WebappConfigUtil.getParameter("PUBLISH_DETAIL_URL");
		String resSize="";
		List<SysParameter> sr = sysParameterService.findParaValue("onLineType");
		if (sr != null && sr.size() > 0) {
			if (sr.get(0) != null && sr.get(0).getParaValue() != null) {
				resSize = sr.get(0).getParaValue();
			}
		}
		//循环每一个资源id
		for (int i = 0; i < resid.length; i++) {
			ResOrder resOrder =new ResOrder();
			SubjectStore store = new SubjectStore();
			if(posttype.equals("1")){
				resOrder= (ResOrder) getByPk(ResOrder.class, Long.decode(orderId));
			}
			if(posttype.equals("2")){
				store = (SubjectStore) getByPk(SubjectStore.class, Long.decode(orderId));
			}
			UserInfo userInfo = LoginUserUtil.getLoginUser();
			User u = new User();
			u.setId(userInfo.getUserId());
			int platformId = userInfo.getPlatformId();
			if(resOrder!=null || store!=null){
				List<String> listResList=  getResIdByOrderIds(orderId, restype, posttype);
				List<String> addResIdList = new ArrayList<String>();//考虑到事务 ，需要缓存需要添加的资源id
				Map<String, List<String>> addResIdAndFileIdListmap = new HashMap<String, List<String>>();//考虑到事务 ，需要缓存需要添加的资源fileIds
				Map<String, List<String>> dbResIdAndFileIdListmap = getListByOrderIdAndResIds(Long.valueOf(orderId), resid[i],posttype,restype);//获得所有的文件 key:resId,   value:fileIds
				//根据资源resId查询实体
				String resourceDetail = http.executeGet(PUBLISH_DETAIL_URL + "?id=" + resid[i]);
				if(resourceDetail!=null){
					Gson gson = new Gson();
					Ca oldCa = gson.fromJson(resourceDetail, Ca.class);
					List<File> fileList = oldCa.getRealFiles();
					if(fileList!=null){
						for (File file : fileList) {
							String isDir = file.getIsDir();
							String fileId = file.getObjectId();//文件id
							if("2".equals(isDir)){//文件
								String aa= DoFileUtils.replaceFliePathStr(file.getPath());
								java.io.File file2 = new java.io.File(aa);//new路径，转义斜线
								if(dbResIdAndFileIdListmap==null){
									List<String> fileIds = addResIdAndFileIdListmap.get(resid[i]);
									if(fileIds==null){
										if(file2.getPath().toLowerCase().contains("cover.jpg")){//根据封面路径查找封面文件
											if(resSize.contains("1")){
												addResOrderDetailToDBs(orderId, u,platformId, listResList,addResIdList, resid[i],restype,posttype);
												ResFileRelation resFileRel = new ResFileRelation();
												resFileRel.setFileId(fileId);
												resFileRel.setResId(resid[i]);
												resFileRel.setRestype(restype);
												resFileRel.setPosttype(posttype);
												resFileRel.setOrderId(Long.valueOf(orderId));
												getBaseDao().create(resFileRel);
												List<String> tempList = new ArrayList<String>();
												tempList.add(fileId);
												addResIdAndFileIdListmap.put(resid[i], tempList);
											}
										}
										String paths = file2.getPath();
										paths = paths.replaceAll("\\\\", "\\/");
										//查找后缀为pdf的文件
										if(paths.contains(".")){
										if(paths.substring(paths.lastIndexOf("."),paths.length()).toLowerCase().equals(".pdf")){
											//获取到pdf文件
											String pdfs =  paths.substring(paths.lastIndexOf("/"),paths.length()).toLowerCase();
											//判断包含h和印刷的为高精度文件
											if(pdfs.contains("h") || pdfs.contains("印刷")){
												if(resSize.contains("2")){
													addResOrderDetailToDBs(orderId, u,platformId, listResList,addResIdList, resid[i],restype,posttype);
													ResFileRelation resFileRel = new ResFileRelation();
													resFileRel.setFileId(fileId);
													resFileRel.setResId(resid[i]);
													resFileRel.setRestype(restype);
													resFileRel.setPosttype(posttype);
													resFileRel.setOrderId(Long.valueOf(orderId));
													getBaseDao().create(resFileRel);
													List<String> tempList = new ArrayList<String>();
													tempList.add(fileId);
													addResIdAndFileIdListmap.put(resid[i], tempList);
												}
											}
											//包含l和宣传的为低精度文件
											if(pdfs.contains("l") || pdfs.contains("宣传")){
												if(resSize.contains("3")){
													addResOrderDetailToDBs(orderId, u,platformId, listResList,addResIdList, resid[i],restype,posttype);
													ResFileRelation resFileRel = new ResFileRelation();
													resFileRel.setFileId(fileId);
													resFileRel.setResId(resid[i]);
													resFileRel.setRestype(restype);
													resFileRel.setPosttype(posttype);
													resFileRel.setOrderId(Long.valueOf(orderId));
													getBaseDao().create(resFileRel);
													List<String> tempList = new ArrayList<String>();
													tempList.add(fileId);
													addResIdAndFileIdListmap.put(resid[i], tempList);
												}
											}
										}
										if(paths.substring(paths.lastIndexOf("."),paths.length()).toLowerCase().equals(".epub")){
											//获取到epub文件
											addResOrderDetailToDBs(orderId, u,platformId, listResList,addResIdList, resid[i],restype,posttype);
											ResFileRelation resFileRel = new ResFileRelation();
											resFileRel.setFileId(fileId);
											resFileRel.setResId(resid[i]);
											resFileRel.setRestype(restype);
											resFileRel.setPosttype(posttype);
											resFileRel.setOrderId(Long.valueOf(orderId));
											getBaseDao().create(resFileRel);
											List<String> tempList = new ArrayList<String>();
											tempList.add(fileId);
											addResIdAndFileIdListmap.put(resid[i], tempList);
										}
										}
									}else{
										if(file2.getPath().toLowerCase().contains("cover.jpg")){//根据封面路径查找封面文件
											if(resSize.contains("1")){
												addResOrderDetailToDBs(orderId, u,platformId, listResList,addResIdList, resid[i],restype,posttype);
												ResFileRelation resFileRel = new ResFileRelation();
												resFileRel.setFileId(fileId);
												resFileRel.setResId(resid[i]);
												resFileRel.setRestype(restype);
												resFileRel.setPosttype(posttype);
												resFileRel.setOrderId(Long.valueOf(orderId));
												getBaseDao().create(resFileRel);
												fileIds.add(fileId);
												addResIdAndFileIdListmap.put(resid[i], fileIds);
											}
										}
										String paths = file2.getPath();
										paths = paths.replaceAll("\\\\", "\\/");
										//查找后缀为pdf的文件
										if(paths.contains(".")){
										if(paths.substring(paths.lastIndexOf("."),paths.length()).toLowerCase().equals(".pdf")){
											//获取到pdf文件
											String pdfs =  paths.substring(paths.lastIndexOf("/"),paths.length()).toLowerCase();
											//判断包含h和印刷的为高精度文件
											if(pdfs.contains("h") || pdfs.contains("印刷")){
												if(resSize.contains("2")){
													addResOrderDetailToDBs(orderId, u,platformId, listResList,addResIdList, resid[i],restype,posttype);
													ResFileRelation resFileRel = new ResFileRelation();
													resFileRel.setFileId(fileId);
													resFileRel.setResId(resid[i]);
													resFileRel.setRestype(restype);
													resFileRel.setPosttype(posttype);
													resFileRel.setOrderId(Long.valueOf(orderId));
													getBaseDao().create(resFileRel);
													fileIds.add(fileId);
													addResIdAndFileIdListmap.put(resid[i], fileIds);
												}
											}
											//包含l和宣传的为低精度文件
											if(pdfs.contains("l") || pdfs.contains("宣传")){
												if(resSize.contains("3")){
													addResOrderDetailToDBs(orderId, u,platformId, listResList,addResIdList, resid[i],restype,posttype);
													ResFileRelation resFileRel = new ResFileRelation();
													resFileRel.setFileId(fileId);
													resFileRel.setResId(resid[i]);
													resFileRel.setRestype(restype);
													resFileRel.setPosttype(posttype);
													resFileRel.setOrderId(Long.valueOf(orderId));
													getBaseDao().create(resFileRel);
													fileIds.add(fileId);
													addResIdAndFileIdListmap.put(resid[i], fileIds);
												}
											}
										}
										if(paths.substring(paths.lastIndexOf("."),paths.length()).toLowerCase().equals(".epub")){
											//获取到epub文件
											addResOrderDetailToDBs(orderId, u,platformId, listResList,addResIdList, resid[i],restype,posttype);
											ResFileRelation resFileRel = new ResFileRelation();
											resFileRel.setFileId(fileId);
											resFileRel.setResId(resid[i]);
											resFileRel.setRestype(restype);
											resFileRel.setPosttype(posttype);
											resFileRel.setOrderId(Long.valueOf(orderId));
											getBaseDao().create(resFileRel);
											fileIds.add(fileId);
											addResIdAndFileIdListmap.put(resid[i], fileIds);
										}
									}
									}
								}else{
									List<String> dbFileIds = dbResIdAndFileIdListmap.get(resid[i]);
									List<String> addFileIds = addResIdAndFileIdListmap.get(resid[i]);
									if(!dbFileIds.contains(fileId)){
										if(addFileIds==null){
											if(file2.getPath().toLowerCase().contains("cover.jpg")){//根据封面路径查找封面文件
												if(resSize.contains("1")){
													addResOrderDetailToDBs(orderId, u,platformId, listResList,addResIdList, resid[i],restype,posttype);
													ResFileRelation resFileRel = new ResFileRelation();
													resFileRel.setFileId(fileId);
													resFileRel.setResId(resid[i]);
													resFileRel.setRestype(restype);
													resFileRel.setPosttype(posttype);
													resFileRel.setOrderId(Long.valueOf(orderId));
													getBaseDao().create(resFileRel);
													List<String> tempList = new ArrayList<String>();
													tempList.add(fileId);
													addResIdAndFileIdListmap.put(resid[i], tempList);
												}
											}
											String paths = file2.getPath();
											paths = paths.replaceAll("\\\\", "\\/");
											//查找后缀为pdf的文件
											if(paths.contains(".")){
											if(paths.substring(paths.lastIndexOf("."),paths.length()).toLowerCase().equals(".pdf")){
												//获取到pdf文件
												String pdfs =  paths.substring(paths.lastIndexOf("/"),paths.length()).toLowerCase();
												//判断包含h和印刷的为高精度文件
												if(pdfs.contains("h") || pdfs.contains("印刷")){
													if(resSize.contains("2")){
														addResOrderDetailToDBs(orderId, u,platformId, listResList,addResIdList, resid[i],restype,posttype);
														ResFileRelation resFileRel = new ResFileRelation();
														resFileRel.setFileId(fileId);
														resFileRel.setResId(resid[i]);
														resFileRel.setRestype(restype);
														resFileRel.setPosttype(posttype);
														resFileRel.setOrderId(Long.valueOf(orderId));
														getBaseDao().create(resFileRel);
														List<String> tempList = new ArrayList<String>();
														tempList.add(fileId);
														addResIdAndFileIdListmap.put(resid[i], tempList);
													}
												}
												//包含l和宣传的为低精度文件
												if(pdfs.contains("l") || pdfs.contains("宣传")){
													if(resSize.contains("3")){
														addResOrderDetailToDBs(orderId, u,platformId, listResList,addResIdList, resid[i],restype,posttype);
														ResFileRelation resFileRel = new ResFileRelation();
														resFileRel.setFileId(fileId);
														resFileRel.setResId(resid[i]);
														resFileRel.setRestype(restype);
														resFileRel.setPosttype(posttype);
														resFileRel.setOrderId(Long.valueOf(orderId));
														getBaseDao().create(resFileRel);
														List<String> tempList = new ArrayList<String>();
														tempList.add(fileId);
														addResIdAndFileIdListmap.put(resid[i], tempList);
													}
												}
											}
											if(paths.substring(paths.lastIndexOf("."),paths.length()).toLowerCase().equals(".epub")){
												//获取到epub文件
												addResOrderDetailToDBs(orderId, u,platformId, listResList,addResIdList, resid[i],restype,posttype);
												ResFileRelation resFileRel = new ResFileRelation();
												resFileRel.setFileId(fileId);
												resFileRel.setResId(resid[i]);
												resFileRel.setRestype(restype);
												resFileRel.setPosttype(posttype);
												resFileRel.setOrderId(Long.valueOf(orderId));
												getBaseDao().create(resFileRel);
												List<String> tempList = new ArrayList<String>();
												tempList.add(fileId);
												addResIdAndFileIdListmap.put(resid[i], tempList);
											}
											}
										}else{
											if(!addFileIds.contains(fileId)){
												if(file2.getPath().toLowerCase().contains("cover.jpg")){//根据封面路径查找封面文件
													if(resSize.contains("1")){
														addResOrderDetailToDBs(orderId, u,platformId, listResList,addResIdList, resid[i],restype,posttype);
														ResFileRelation resFileRel = new ResFileRelation();
														resFileRel.setFileId(fileId);
														resFileRel.setResId(resid[i]);
														resFileRel.setRestype(restype);
														resFileRel.setPosttype(posttype);
														resFileRel.setOrderId(Long.valueOf(orderId));
														getBaseDao().create(resFileRel);
														addFileIds.add(fileId);
														addResIdAndFileIdListmap.put(resid[i], addFileIds);
													}
												}
												String paths = file2.getPath();
												paths = paths.replaceAll("\\\\", "\\/");
												//查找后缀为pdf的文件
												if(paths.contains(".")){
												if(paths.substring(paths.lastIndexOf("."),paths.length()).toLowerCase().equals(".pdf")){
													//获取到pdf文件
													String pdfs =  paths.substring(paths.lastIndexOf("/"),paths.length()).toLowerCase();
													//判断包含h和印刷的为高精度文件
													if(pdfs.contains("h") || pdfs.contains("印刷")){
														if(resSize.contains("2")){
															addResOrderDetailToDBs(orderId, u,platformId, listResList,addResIdList, resid[i],restype,posttype);
															ResFileRelation resFileRel = new ResFileRelation();
															resFileRel.setFileId(fileId);
															resFileRel.setResId(resid[i]);
															resFileRel.setRestype(restype);
															resFileRel.setPosttype(posttype);
															resFileRel.setOrderId(Long.valueOf(orderId));
															getBaseDao().create(resFileRel);
															addFileIds.add(fileId);
															addResIdAndFileIdListmap.put(resid[i], addFileIds);
														}
													}
													//包含l和宣传的为低精度文件
													if(pdfs.contains("l") || pdfs.contains("宣传")){
														if(resSize.contains("3")){
															addResOrderDetailToDBs(orderId, u,platformId, listResList,addResIdList, resid[i],restype,posttype);
															ResFileRelation resFileRel = new ResFileRelation();
															resFileRel.setFileId(fileId);
															resFileRel.setResId(resid[i]);
															resFileRel.setRestype(restype);
															resFileRel.setPosttype(posttype);
															resFileRel.setOrderId(Long.valueOf(orderId));
															getBaseDao().create(resFileRel);
															addFileIds.add(fileId);
															addResIdAndFileIdListmap.put(resid[i], addFileIds);
														}
													}
												}
												if(paths.substring(paths.lastIndexOf("."),paths.length()).toLowerCase().equals(".epub")){
													//获取到epub文件
													addResOrderDetailToDBs(orderId, u,platformId, listResList,addResIdList, resid[i],restype,posttype);
													ResFileRelation resFileRel = new ResFileRelation();
													resFileRel.setFileId(fileId);
													resFileRel.setResId(resid[i]);
													resFileRel.setRestype(restype);
													resFileRel.setPosttype(posttype);
													resFileRel.setOrderId(Long.valueOf(orderId));
													getBaseDao().create(resFileRel);
													addFileIds.add(fileId);
													addResIdAndFileIdListmap.put(resid[i], addFileIds);
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
				if(posttype.equals("1")){
					SysOperateLogUtils.addLog("resOrder_addResource", "添加资源", userInfo);
				}
				if(posttype.equals("2")){
					SysOperateLogUtils.addLog("subject_addResource", "添加资源", userInfo);
				}
			}
		}
		return "succ";
	}

	@Override
	public List<ResFileRelation> queryResFileRelationList(String orderId,
			String resId, String posttype) {
		String hql ="from ResFileRelation t where t.orderId="+Long.parseLong(orderId)+" and t.resId='"+resId+"' and t.posttype='"+posttype+"' and t.processStatus=-1";
		List<ResFileRelation> resFileRelations = getBaseDao().query(hql);
		return resFileRelations;
	}
}

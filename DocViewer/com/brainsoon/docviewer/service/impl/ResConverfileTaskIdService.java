package com.brainsoon.docviewer.service.impl;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.docviewer.model.ResConverfileTask;
import com.brainsoon.docviewer.model.ResConverfileTaskId;
import com.brainsoon.docviewer.service.IResConverfileTaskIdService;

/**
 * 
 * @ClassName: ResConverfileTaskIdService 
 * @Description: 文件转换Id相关功能service 
 * @author tanghui 
 * @date 2014-8-7 下午4:47:34 
 * @update功能：对ID分配表的查询
 *
 */
@Service("resConverfileTaskIdService")
public class ResConverfileTaskIdService extends BaseService implements IResConverfileTaskIdService{

	//总共的组数
	private static int totalGroupNumber = 50;
	//每组数量
	private static int singleGroupIdNumber = 50;
	
	/**
	 * 
	 * @Title: addFpIpAddrWithId 
	 * @Description: 分配ip段id
	 * @param   ipAddrs ip地址一个或者多个，以“,”分隔
	 * @param   singleGroupIdParmNumber 每组数量
	 * @param   groupIdParmNumber 总共分多少组
	 * @return String 
	 * @throws
	 */
	@Override
	public String addFpIpAddrWithId(String ipAddrs,String singleGroupIdParmNumber,String groupIdParmNumber){
		String msg = "";
		try {
			boolean bb = true;
			List<ResConverfileTaskId> list = queryFpIpAddrWithId();
			int ipNum = 0; //ip总数
			String dbMaxId = ""; //最大id
			
			//开始对各项值进行校验...
			if(StringUtils.isBlank(singleGroupIdParmNumber)){
				singleGroupIdParmNumber = singleGroupIdNumber + "";
			}
			
			if(StringUtils.isBlank(groupIdParmNumber)){
				groupIdParmNumber = totalGroupNumber + "";
			}
			
			if(bb){
				if(StringUtils.isBlank(ipAddrs)){
					bb = false;
					msg = "未传入IP地址，无法分配。";
				}
			}
			
			if(bb){
				if(list == null){
					bb = false;
					msg = "数据库无记录，无需分配。";
				}
			}
			
			if(bb){
				List<Object> listMin = getBaseDao().queryBySql("select * from res_converfile_task  ORDER BY id asc", ResConverfileTask.class); 
				if(listMin != null&&!listMin.equals("")){ 
					ResConverfileTask rcft = (ResConverfileTask) listMin.get(0);
					dbMaxId = rcft.getId()+""; //最小id
				}
			}
			
			//开始处理...
			if(bb){
				//清空所有
				deleteAllFpIpAddr();
				String[] ipAddrArr = ipAddrs.split(",");
				ipNum = ipAddrArr.length;
				Map<String,String> ipSqlParmMap = new HashMap<String,String>();
				if(ipNum == 1){ //如果只有一个ip，则直接默认就可以了
					ipSqlParmMap.put(ipAddrArr[0], " id>=0 ");
				}else{//否则需要重新分配到每个ip上
					for (int i = 0; i < ipAddrArr.length; i++) {
						StringBuffer groupParms = null; //变量
						String minId = ""; //最小id
						String maxId = ""; //最大id
						boolean bbb = true;
						int o = Integer.parseInt(singleGroupIdParmNumber);
						for (int j = 0; j < Integer.parseInt(groupIdParmNumber); j++) {
							BigInteger a = BigInteger.valueOf(j * ipNum * o);  //ip数*循环次数*每组数量
							BigInteger c = BigInteger.valueOf(i * Integer.parseInt(singleGroupIdParmNumber));  //ip循环次数*每组数量
							BigInteger e = a.add(c);
							List<Object> listObj = null;
							if(bbb){
								String sql = "select * from res_converfile_task ORDER BY id  LIMIT " + e + "," + o;
								listObj = getBaseDao().queryBySql(sql, ResConverfileTask.class); 
								if(listObj != null && listObj.size()>0){
									ResConverfileTask rcftMin = (ResConverfileTask) listObj.get(0);
									minId = new BigInteger(rcftMin.getId()+"").subtract(BigInteger.valueOf(1)) + ""; //最小id
									ResConverfileTask rcftMax = (ResConverfileTask) listObj.get(listObj.size()-1);
									maxId = rcftMax.getId() + ""; //最大id
									if(listObj.size() < o){
										bbb = false;
									}
								}else{
									bbb = false;
								}
							}
							
							
							if((listObj == null  || listObj.size() <= 0)  && !bbb){
								minId =  e.add(new BigInteger(dbMaxId)) + ""; 
								maxId = new BigInteger(minId).add(new BigInteger(singleGroupIdParmNumber)) + "";
							}
							
							if(groupParms == null){
								groupParms = new StringBuffer();
							}
							
							if(j == 0){
								groupParms.append("(id>" + minId + " and id<=" + maxId + ")");
							}else{
								if(i == ipAddrArr.length-1 && j == Integer.parseInt(groupIdParmNumber)-1){
									groupParms.append(" or (id>" + minId + ")");
								}else{
									groupParms.append(" or (id>" + minId + " and id<=" + maxId + ")");
								}
							}
							
							//循环结果
							if(j == Integer.parseInt(groupIdParmNumber)-1){
								ipSqlParmMap.put(ipAddrArr[i], groupParms.toString());
							}
						}
					}
				}
				

				Iterator<String> iter = ipSqlParmMap.keySet().iterator();
				while (iter.hasNext()) {
				    String ip = iter.next();
				    String sqlparam = ipSqlParmMap.get(ip);
				    ResConverfileTaskId rcfti = new ResConverfileTaskId();
					rcfti.setIpAddr(ip);
					rcfti.setStatus(1);
					rcfti.setSqlparam(sqlparam);
					rcfti.setSingleGroupIdParmNumber(singleGroupIdParmNumber);
					rcfti.setGroupIdParmNumber(groupIdParmNumber);
					rcfti.setUpdateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
					getBaseDao().create(rcfti);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return msg;
	}
	
	
	/**
	 * 
	 * @Title: updateFpIpAddrWithId 
	 * @Description: 更新分配ip段id
	 * @param   oldIpAddr 老的ip地址
	 * @param   newIpAddr 新的ip地址
	 * @return String 
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String updateFpIpAddr(String oldIpAddr,String newIpAddr){
		String desc = "";
		try {
			List<ResConverfileTaskId> list = getBaseDao().query("from ResConverfileTaskId t where t.ipAddr='" + oldIpAddr + "'");
			if(list != null && list.size() > 0){
				for (ResConverfileTaskId resConverfileTaskId : list) {
					resConverfileTaskId.setIpAddr(newIpAddr);
					getBaseDao().update(resConverfileTaskId);
				}
			}	
		} catch (Exception e) {
			desc = "更新失败";
			e.printStackTrace();
		}
		return desc;
	}

	
	/**
	 * 
	 * @Title: deleteNullIdFpIpAddr 
	 * @Description: 删除所有不存在的并且已分配的ip段id
	 * @param   
	 * @return String 
	 * @throws
	 */
	@Override
	public void deleteAllFpIpAddr(){
		try {
			List<ResConverfileTaskId> list = queryFpIpAddrWithId();
			if(list != null){
				for (ResConverfileTaskId resConverfileTaskId : list) {
					getBaseDao().delete(resConverfileTaskId);
				}
			}
			for (ResConverfileTaskId resConverfileTaskId : list) {
				getBaseDao().delete(resConverfileTaskId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 
	 * @Title: queryFpIpAddrWithId 
	 * @Description: 查询所有记录
	 * @param   
	 * @return String 
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	public List<ResConverfileTaskId> queryFpIpAddrWithId(){
		List<ResConverfileTaskId> list = null;
		try {
			list = getBaseDao().loadAll(ResConverfileTaskId.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public static void main(String[] args) {
		BigInteger a = new BigInteger("20");
		BigInteger b = BigInteger.valueOf(1 * 2);
		BigInteger c = BigInteger.valueOf(2*20);
		BigInteger d = BigInteger.valueOf(20);
		a = a.multiply(b);
		a = a.add(c);
		d = d.add(a);
		String ss = " (id>" + a + " and id<=" + d + ")";
		System.out.println(ss);
	}
	
}

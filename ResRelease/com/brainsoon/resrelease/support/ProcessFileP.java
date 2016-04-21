package com.brainsoon.resrelease.support;

import java.util.List;

import org.apache.log4j.Logger;

import com.brainsoon.common.service.IBaseService;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.common.support.GlobalAppCacheMap;
import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.resrelease.po.ResReleaseDetail;

/**
 * @ClassName: ProcessFileP
 * @Description: 生成线程
 * @author xiehewei
 * @date 2014年9月1日 下午3:31:48
 *
 */
public class ProcessFileP extends BaseService implements Runnable {

	private static final Logger logger = Logger.getLogger(ProcessFileP.class);
	
	ProcessFilePC processFilePC = new ProcessFilePC();

	ProcessFileP(ProcessFilePC processFilePC) {
		this.processFilePC = processFilePC;
	}
	
	private Long releaseId;

	/**
	 * 生产进程(查询数据库中的待转换文件列表)
	 */
	@SuppressWarnings("unchecked")
	public void run() {
		IBaseService baseQueryService;
		while(true){
			//等待一会
			try {
				baseQueryService = (IBaseService) BeanFactoryUtil.getBean("baseService");
				//查询状态为：待转换、转换失败（并且重试次数小于3的记录）
				// 第一步：查询 ，查询状态为待转换的列表，status 默认待转换 0
				// 或者查询如果有转换失败的，优先级默认为待转换的列表，
				// 如果没有待状态的，则查询失败的列表 ，失败的需要记录转换的次数，如果最高重试3次，如果都不能成功，则说明本视频无法转换....
				//String hql = "from ResConverfileTask s where (s.status != 1 and s.status != 2) and s.retryNum < 3 order by s.id asc";
				List<ResReleaseDetail> cf = null;
				String hql = " from ResReleaseDetail rrd where rrd.status=0 and rrd.releaseId="+releaseId+" order by rrd.detailId asc";
				cf = baseQueryService.query(hql);
				if(cf.size()==0){
					hql = " from ResReleaseDetail rrd where rrd.status=3 and rrd.processTimes<=3 and rrd.releaseId="+releaseId+" order by rrd.detailId asc";
					cf = baseQueryService.query(hql);
				}
				
				if(cf != null && cf.size() > 0){
					processFilePC.pushFile(cf);
					//移除生产者KEY
					GlobalAppCacheMap.removeKey(ProdConsRun.PKey);
				}else{
					//缓存key
					Object obj = GlobalAppCacheMap.getValue(ProdConsRun.PKey);
					if(obj == null){
						obj = 1;
					}else{
						int num = Integer.parseInt(obj+"");
						if(num == 1){
							GlobalAppCacheMap.removeKey(ProdConsRun.PKey);
							GlobalAppCacheMap.removeKey(ProdConsRun.PCKey);
//							logger.debug("######## 待转换的队列里还没有出现数据，我累了，不再等了，先休息下，等有数据的时候唤醒我！ bye bye！");
							break;
						}
						obj = num + 1;
					}
//					logger.debug("######## 待转换的队列里没有出现数据，我开始倒数了：【" + (100- Integer.parseInt(obj+"")) +"】--数到-->【0】我就退出了啊！");
					GlobalAppCacheMap.putKey(ProdConsRun.PKey, obj);
				}
//				logger.info("生产线程：【" + Thread.currentThread().getName() + "】被调用了");
				Thread.sleep((int) (Math.random() * 3000));
				if(cf.size()==0){
					notifyAll();
					break;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public Long getReleaseId() {
		return releaseId;
	}

	public void setReleaseId(Long releaseId) {
		this.releaseId = releaseId;
	}

}

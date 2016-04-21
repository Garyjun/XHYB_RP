package com.brainsoon.common.util.dofile.service.thread;

import java.util.List;

import org.apache.log4j.Logger;

import com.brainsoon.common.service.IBaseService;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.common.support.GlobalAppCacheMap;
import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.docviewer.model.ResConverfileTask;


/**
 * 
 * @ClassName: ResConverfileTaskProducer
 * @Description: 文件转换生产类
 * @author tanghui
 * @date 2014-5-23 上午11:36:06
 * 
 */
public class ConverFileP extends BaseService implements Runnable {
	
	private static IBaseService baseQueryService = null;
	
	private static final Logger logger = Logger.getLogger(ConverFileP.class);
	
	ConverFilePC converFilePC = new ConverFilePC();

	ConverFileP(ConverFilePC converFilePC) {
		this.converFilePC = converFilePC;
	}

	/**
	 * 生产进程(查询数据库中的待转换文件列表)
	 */
	@SuppressWarnings("unchecked")
	public void run() {
		while(true){
			//等待一会
			try {
				baseQueryService = (IBaseService) BeanFactoryUtil.getBean("baseService");
				//查询状态为：待转换、转换失败（并且重试次数小于3的记录） 0：待转换 1：转换中 2：转换成功 3：转换失败
				// 第一步：查询状态为待转换的列表:状态不为  =1 的记录
				String hql = "from ResConverfileTask s where s.status != 1 order by s.id asc";
				//判断是否需要对状态为：加工中 的列表进行处理（解决由于重启服务等造成的数据停止转换，
				//这样就会出现部分记录可能永远也无法被转换了，因为生产线程只处理：待转换和需要重试的记录）
				//判断缓存中是否有该KEY，如果没有则说明是刚启动应用，否则不是
				//ConverFilePStuas == 0  为需要对转换中的记录进行重新处理
				//查询所有的记录
				if(GlobalAppCacheMap.containsKey("ConverFilePStuas") 
						&& GlobalAppCacheMap.getValue("ConverFilePStuas").equals("0")){ //true or  false
					hql = "from ResConverfileTask s order by s.id asc";
					//移除KEY
					GlobalAppCacheMap.removeKey("ConverFilePStuas");
				}
				List<ResConverfileTask> cf = baseQueryService.query(hql);
				if(cf != null && cf.size() > 0){
					converFilePC.pushFile(cf);
					//移除生产者KEY
					GlobalAppCacheMap.removeKey(PCRun.ProducersKey);
				}else{
					//缓存key
					Object obj = GlobalAppCacheMap.getValue(PCRun.ProducersKey);
					if(obj == null){
						obj = 1;
					}else{
						int num = Integer.parseInt(obj+"");
						if(num == PCRun.PCycleNum){
							GlobalAppCacheMap.removeKey(PCRun.ProducersKey);
							GlobalAppCacheMap.removeKey(PCRun.PCRunKey);
							logger.debug("######## 待转换的队列里还没有出现数据，我累了，不再等了，先休息下，等有数据的时候唤醒我！ bye bye！");
							break;
						}
						obj = num + 1;
					}
					logger.debug("######## 待转换的队列里没有出现数据，我开始倒数了：【" + (100- Integer.parseInt(obj+"")) +"】--数到-->【0】我就退出了啊！");
					GlobalAppCacheMap.putKey(PCRun.ProducersKey, obj);
				}
//				logger.info("生产线程：【" + Thread.currentThread().getName() + "】被调用了");
				Thread.sleep((int) (Math.random() * 3000));
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}

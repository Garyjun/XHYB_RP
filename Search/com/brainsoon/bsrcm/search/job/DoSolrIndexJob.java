package com.brainsoon.bsrcm.search.job;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.bsrcm.search.po.SolrQueue;
import com.brainsoon.bsrcm.search.service.ISolrQueueFacede;
import com.brainsoon.bsrcm.search.solr.IndexCreateClient;
import com.brainsoon.bsrcm.search.solr.SearchException;
import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.common.util.dofile.util.WebAppUtils;
import com.brainsoon.semantic.ontology.model.Ca;
import com.google.gson.Gson;

public class DoSolrIndexJob {
	private static final Logger logger = Logger.getLogger(DoSolrIndexJob.class);
	private ISolrQueueFacede solrQueueFacede;	
	
	 /* 线程同步标识 */
	private static boolean isruning = false;
	
	/**
	 * 初始化数据
	 */
	public void initService() {
		 // 加载数据服务接口
		loadSolrQueueFacede();

	}

	/**
	 * 索引创建任务调度方法
	 */
	public void doIndexJob() {
		
		  /* 正在处理中则返回 */
		if (isruning) {
			return;
		}
		
		 //初始化服务接口
		initService();
		isruning = true;
		try {
			 //查询获取创建索引队列集合
			List<SolrQueue> solrQueues = solrQueueFacede.getSolrQueueByStatus("5");
			
			if(solrQueues!=null &&solrQueues.size()>0) {
				logger.debug("索引队列集合数量：" + solrQueues.size());
				for(int i = 0;i< solrQueues.size(); i++) {
					
					SolrQueue queue = solrQueues.get(i);
					 // 如果索引动作为0,则创建索引；如果为1，则删除索引
					int res = createIndex(queue);
					logger.debug("res==================================" + res);
					if(res == 0) {
						solrQueueFacede.updateSolrQueueStatus(queue.getId(), "1");
					}
					
				}
			}
			
		} catch(Exception ex) {
			logger.error(ex);
		} finally {
			isruning = false;
		}
	}
	
	/**
	 * 创建索引
	 * @param queue
	 */
	private int createIndex(SolrQueue queue) {
		try {		
			//待抽取txt文件的根目录-绝对路径
			String filesolr = StringUtils.replace(WebAppUtils.getWebRootBaseDir("filesolr"),"\\", "/");
			 //加载数据资源
			Ca ca = loadResource(queue);
			if(ca != null){
				if(ca.getObjectId() == null) {
					logger.debug("根据Id查找book不存在，bookId:" + queue.getResId());
					return 1;
				}
				String uid = ca.getObjectId().substring(4, ca.getObjectId().length());
				String combinePath = filesolr +"converted/"+uid+".txt";
				logger.debug("combinePath=================:" + combinePath);
				ca.setPublishType(ca.getPublishType());
				List<String> filePaths = new ArrayList<String>();
				filePaths.add(combinePath);
				ca.setXpaths(filePaths);
				ca.setObjectId(ca.getObjectId());
				
				/*HttpClientUtil http = new HttpClientUtil();
				Gson gson=new Gson();
				String url = WebappConfigUtil.getParameter("PUBLISH_FULL_SAVE");
				String paraJson = gson.toJson(ca);
				String result = http.postJson(url, paraJson);
				if(StringUtils.isNotBlank(result)){
					org.apache.commons.io.FileUtils.deleteQuietly(new File(combinePath));
				}*/
				
				
		    	try {
					IndexCreateClient.getInstance().createCAIndex(ca);
					org.apache.commons.io.FileUtils.deleteQuietly(new File(combinePath));
				} catch (SearchException e) {				
					logger.debug("资源索引覆盖失败，资源id:" + ca.getObjectId());
				}
			}else{
				logger.debug("全文索引创建异常：ca对象为空，资源id:" + queue.getResId());
			}
		} catch (Exception se) {
			se.printStackTrace();
			logger.debug("全文索引创建异常：" + se.getMessage());
			return 1;
		}
		return 0;
	}
	/**
	 * 封装资源参数
	 * @param queue
	 * @return
	 */
	private Ca loadResource(SolrQueue queue) {
//		String resDirId = "urn:publish-9689433c-e454-4782-8836-13ed54b64b45";
		String resDirId = queue.getResId();
		String postUrl = WebappConfigUtil.getParameter("PUBLISH_DETAIL_URL");
		HttpClientUtil http = new HttpClientUtil();
		String result = http.executeGet(postUrl + "?id=" + resDirId);
		Ca ca = null;
		if(StringUtils.isNotBlank(result)){
			Gson gson=new Gson();
		    ca=gson.fromJson(result, Ca.class);
		}
		return ca;
	}
	
	private void loadSolrQueueFacede() {
		try {
			this.solrQueueFacede = (ISolrQueueFacede) BeanFactoryUtil.getBean("solrQueueFacede");
		} catch (Exception e) {

		}
	}
	
	public static void main(String[] args) {
		String postUrl = "http://10.130.29.14:8090/semantic_index_server/publish/ontologyDetailQuery/ca";
		Ca bookCa = null;		

		HttpClientUtil http = new HttpClientUtil();
		String bookDetail= http.executeGet(postUrl + "?id=" + "urn:publish-9689433c-e454-4782-8836-13ed54b64b45");

        Gson gson=new Gson();
        bookCa=gson.fromJson(bookDetail, Ca.class);
        logger.debug("bookCa==============================" + bookCa.getObjectId());
//		DoSolrIndexJob job = new DoSolrIndexJob();
//		job.createIndex(null);
	}
}

package com.brainsoon.resrelease.support;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.resrelease.po.ResOrder;
import com.brainsoon.resrelease.po.ResRelease;
import com.brainsoon.resrelease.service.IResOrderService;
import com.brainsoon.resrelease.service.IResReleaseService;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * @ClassName: PublishTaskJob
 * @Description: 线上发布定时任务
 * @author xiehewei
 * @date 2015年4月17日 下午2:13:47
 *
 */
public class PublishTaskJob {
	
	private static Logger log = Logger.getLogger(PublishTaskJob.class);
	
	public void dealResult(){
		log.info("publishTaskJob  start....");
		try {
			IResReleaseService resReleaseService = (IResReleaseService) BeanFactoryUtil.getBean("resReleaseService");
			IResOrderService resOrderService = (IResOrderService) BeanFactoryUtil.getBean("resOrderService");
			String path = "";
			Map<String, Integer> map = countResult(path);
			Set<String> set = map.keySet();
			Iterator<String> it = set.iterator();
			while(it.hasNext()){
				String orderId = it.next();
				int result = map.get(orderId);
				ResOrder resOrder = (ResOrder) resOrderService.getByPk(ResOrder.class, Long.valueOf(orderId));
				ResRelease resRelease = resReleaseService.queryRelReleaseByOrderId(Long.valueOf(orderId));
				if(result==1){
					resRelease.setStatus("");
				}else{
					resRelease.setStatus("");
				}
				resOrder.setStatus("4");
				resOrderService.update(resOrder);
				resReleaseService.update(resRelease);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.info("publishTaskJob  end....");
	}
	
	private Map<String, Integer> countResult(String path){
		String source = "";
		String target = "";
		String targetHost = "";
		String targetPort = "";
		String userName = "";
		String password = "";
		int result = 0;
		Map<String, Integer> map = new HashMap<String, Integer>();
		try {
			SftpUtil util = new SftpUtil(targetHost, targetPort, userName, password);
			if(util.loadSFTPConnect()){
				if(util.downloadFile(source, target)){
					File file = new File(targetPort);
					InputStream fis = new FileInputStream(file);
					DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
					DocumentBuilder builder = factory.newDocumentBuilder();
					Document doc = builder.parse(fis);
					NodeList nodeList = doc.getElementsByTagName("status");
					String orderId = doc.getFirstChild().getNodeValue();
					int len = nodeList.getLength();
					for(int i=0;i<len;i++){
						Node node = nodeList.item(i);
						String value = node.getNodeValue();
						if(value.equals("-1")){
							result++;
							break;
						}
					}
					map.put(orderId, result);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	public static void main(String[] args) {
	}
}

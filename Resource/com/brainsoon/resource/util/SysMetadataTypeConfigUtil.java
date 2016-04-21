package com.brainsoon.resource.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

/**
 * 此配置文件专门处理SysMetadataType-mapping-config.xml，其他不适用
 * @author 唐辉
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked","static-access","unused" })
public class SysMetadataTypeConfigUtil {
	
	private static Log logger = LogFactory.getLog(SysMetadataTypeConfigUtil.class);
	
	 /* 配置文件对象 */
	private Document configDocument;
	
	 /* 配置文件路径 */
	private final static String CONFIG_FILE_NAME = "/SysMetadataType-mapping-config.xml";
	
	private static  SysMetadataTypeConfigUtil instance = new SysMetadataTypeConfigUtil();
	
	private SysMetadataTypeConfigUtil() {
		SAXReader reader = new SAXReader();
		try {
			configDocument = reader.read(SysMetadataTypeConfigUtil.class
					.getResourceAsStream(CONFIG_FILE_NAME));

		} catch (DocumentException exp) {
			logger.warn(CONFIG_FILE_NAME + "文件读取错误：" + exp.getMessage());
			configDocument = DocumentHelper.createDocument();
			configDocument.addElement("SysMetadataType-configs");
		}
	}
	
	/**
	 * 返回全部平台的所有配置参数
	 * @return
	 */
	public static Map<String,Map<String,String>> getAllConfig() {
		//List<PlatformConfig> platformConfigs = new ArrayList<PlatformConfig>();
		Map<String,Map<String,String>> maps = new HashMap<String,Map<String,String>>();
		
		
		Element root = instance.configDocument.getRootElement();
		List<Element> sysMetadataTypes = root.elements();
		
		for( Element sysMetadataType : sysMetadataTypes ) {
			
			List<Element> params = sysMetadataType.elements();
			Map<String,String> one = new HashMap<String,String>();
			for (Element ele : params) {
				/* 设置Bean配置对象 */
				one.put(ele.attributeValue("sourceName"), ele.attributeValue("targetName"));
			}
			maps.put(sysMetadataType.attributeValue("name"), one);
		}
		return  maps;
	}
	
	/**
	 * 返回某一平台的所有配置参数
	 * @param platform
	 * @return 配置对象
	 */
	public static Map<String,String> getConfig( String name ) {
		
		String xpath = "/SysMetadataType-configs/SysMetadataType-config[@name='" + name + "']";
		Node node = instance.configDocument.selectSingleNode(xpath);
		
		Map<String,String> one = new HashMap<String,String>();
		if (node == null) {
			instance.logger.warn("平台配置参数[" + name + "]不存在！");
		} else {
			
			try {
				Document doc = DocumentHelper.parseText(node.asXML());
				List<Element> params = doc.getRootElement().elements();
				
				for (Element ele : params) {
					/* 设置Bean配置对象 */
					one.put(ele.attributeValue("sourceName"), ele.attributeValue("targetName"));
				}
			}catch(DocumentException ex) {
				instance.logger.warn("解析[" + CONFIG_FILE_NAME + "]配置文件异常！");
			}
		}
		return  one;
	}
	
	public static void main(String[] args) {
		Map<String,Map<String,String>> allConfigs = SysMetadataTypeConfigUtil.getAllConfig();
		
		Set set =allConfigs.entrySet();       
		Iterator it=set.iterator();      
		while(it.hasNext()){          
			Map.Entry<String, Map<String,String>>  entry=(Entry<String, Map<String,String>>) it.next();           
			Map<String,String> values = entry.getValue();
			
			Iterator its=values.keySet().iterator();    
			while(its.hasNext()){    
			     String key;    
			     String value;    
			     key=its.next().toString();    
			     value=values.get(key);    
			}
		}
		
		Map<String,String> sms = getConfig( "smtocnmarc" ); 
		Iterator its=sms.keySet().iterator();    
		while(its.hasNext()){    
		     String key;    
		     String value;    
		     key=its.next().toString();    
		     value=sms.get(key);    
		}
	}
	
}

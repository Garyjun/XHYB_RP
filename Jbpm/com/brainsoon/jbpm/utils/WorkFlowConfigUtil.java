/**
 * FileName: UsbossConfigUtil.java
 */
package com.brainsoon.jbpm.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

public class WorkFlowConfigUtil  {
	protected final Log logger = LogFactory.getLog(getClass());
	public static final String DEPLOY_PROCESS_DEFINITION = "DEPLOY_PROCESS_DEFINITION";
	public static final String PROCESS_DEFINITION_DEPLOY_MODE = "PROCESS_DEFINITION_DEPLOY_MODE";
	public static final String PROCESS_DEFINITION_LIST = "PROCESS_DEFINITION_LIST";
	private final static String CONFIG_FILE_NAME = "/workFlow-config.xml";
	private Document configDocument;
	private static WorkFlowConfigUtil instance = new WorkFlowConfigUtil();

	public static Boolean getBoolean(String name) {
		String value = getParameter(name);
		return Boolean.valueOf(value);
	}

	public static Integer getInteger(String name, Integer defValue) {
		String value = getParameter(name);
		if (StringUtils.isEmpty(value)) {
			return defValue;
		}
		try {
			return Integer.valueOf(value);
		} catch (NumberFormatException exp) {
			instance.logger.error("配置参数[" + name + "]的值不是数字");
			return defValue;
		}
	}

	public static String getParameter(String name) {
		String xpath = "/configs/param[@name='" + name + "']/@value";
		Node node = instance.configDocument.selectSingleNode(xpath);
		if (node == null) {
			instance.logger.warn("配置参数[" + name + "]不存在");
			return "";
		}
		return node.getText();
	}

	/** 
	 * 
	 * @param name
	 * @return
	 * @author zhanglelei
	 * @date 2007-2-5 23:05:43 
	 */
	public static List<String> getParameterList(String name) {
		String xpath = "/configs/param[@name='" + name + "']/value";
		List nodes = instance.configDocument.selectNodes(xpath);
		List<String> values = new ArrayList<String>();
		for (Iterator iter = nodes.iterator(); iter.hasNext();) {
			Node node = (Node) iter.next();
			values.add(node.getText());
		}
		return values;
	}

	protected WorkFlowConfigUtil() {
		SAXReader reader = new SAXReader();
		try {
			configDocument = reader.read(WorkFlowConfigUtil.class
					.getResourceAsStream(CONFIG_FILE_NAME));

		} catch (DocumentException exp) {
			logger.warn(CONFIG_FILE_NAME + "文件读取错误：" + exp.getMessage());
			configDocument = DocumentHelper.createDocument();
			configDocument.addElement("configs");
		}
	}
	
	protected static Document getDocument() {
		return instance.configDocument;
	}
	
	protected static void outputWarnLog(String log) {
		instance.logger.warn(log);
	}
	
}

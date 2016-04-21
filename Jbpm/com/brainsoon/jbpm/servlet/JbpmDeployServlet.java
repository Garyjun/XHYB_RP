package com.brainsoon.jbpm.servlet;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.jbpm.service.IJbpmDefinitionProxyService;
import com.brainsoon.jbpm.utils.WorkFlowConfigUtil;

/**
 * <dl>
 * <dt>JbpmDeployServlet</dt>
 * <dd>Description:jbpm流程定义部署servlet</dd>
 */
public class JbpmDeployServlet extends HttpServlet {
	private final Log logger = LogFactory.getLog(getClass());

	/**
	 * Constructor of the object.
	 */
	public JbpmDeployServlet() {
		super();
	}

	/**
	 * Destruction of the servlet.
	 */
	public void destroy() {
		super.destroy();
	}

	/**
	 * Initialization of the servlet.
	 * 
	 * @throws ServletException if an error occure
	 */
	public void init() throws ServletException {
		if (!isNeedDeployProcessDefinition()) {
			return;
		}
		try {
			List<String> proDefList = WorkFlowConfigUtil.getParameterList(WorkFlowConfigUtil.PROCESS_DEFINITION_LIST);
			if (proDefList.isEmpty()) {
				return;
			}

			List<String> proDefFileList = getProDefFileList(proDefList);
			if (proDefFileList.isEmpty()) {
				return;
			}

			String deployMode = WorkFlowConfigUtil.getParameter(WorkFlowConfigUtil.PROCESS_DEFINITION_DEPLOY_MODE);

			IJbpmDefinitionProxyService defDepProxyService = (IJbpmDefinitionProxyService) BeanFactoryUtil.getBean("jbpmDefinitionProxyService");

			defDepProxyService.doDeployJpbmProcessDefinition(proDefFileList, deployMode);
		} catch (ServiceException e) {
			logger.error(e.getMessage());
		} catch (Exception e) {
			logger.error("JBPM流程定义部署失败：" + e.getMessage());
		}
	}

	private boolean isNeedDeployProcessDefinition() {
		return Boolean.parseBoolean(WorkFlowConfigUtil.getParameter(WorkFlowConfigUtil.DEPLOY_PROCESS_DEFINITION));
	}

	private List<String> getProDefFileList(List<String> proDefList) {
		StringBuffer proDefRootDirBuf = new StringBuffer(getServletContext().getRealPath("/"));
		if (!proDefRootDirBuf.toString().endsWith(SystemUtils.FILE_SEPARATOR)) {
			proDefRootDirBuf.append(SystemUtils.FILE_SEPARATOR);
		}
		proDefRootDirBuf.append("jbpmArchiveDeploy").append(SystemUtils.FILE_SEPARATOR);
		List<String> proDefFileList = new ArrayList<String>();
		for (String proDefName : proDefList) {
			StringBuffer proDefFilePathBuf = new StringBuffer();
			proDefFilePathBuf.append(proDefRootDirBuf).append(proDefName).append(".zip");
			if (!(new File(proDefFilePathBuf.toString()).exists())) {
				logger.error("部署流程定义" + proDefName + "失败：流程定义文件不存在！" + proDefFilePathBuf.toString());
				continue;
			}

			proDefFileList.add(proDefFilePathBuf.toString());
		}

		return proDefFileList;
	}
}
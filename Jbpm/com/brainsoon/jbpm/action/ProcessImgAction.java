package com.brainsoon.jbpm.action;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jbpm.api.ExecutionService;
import org.jbpm.api.ProcessDefinition;
import org.jbpm.api.ProcessInstance;
import org.jbpm.api.RepositoryService;
import org.jbpm.api.model.ActivityCoordinates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ProcessImgAction {
	@Autowired
	private RepositoryService repositoryService;
	@Autowired
	private ExecutionService executionService;
	/**
	 * 查看流程图
	 */
	@RequestMapping("/ProcessImgAction/viewImg.action") 
	public void viewImg(HttpServletRequest request,HttpServletResponse response) throws Exception{
	 String execuId=request.getParameter("execuId");
	 ProcessInstance processInstance = executionService.findProcessInstanceById(execuId);
	 //获取所有活跃的节点：  
	 Set<String> activitySet = processInstance.findActiveActivityNames(); 
	 String processDefinitionId = processInstance.getProcessDefinitionId();
	 ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(
			 processDefinitionId).uniqueResult();
	 String processName=processDefinition.getName();
	 String imgPath=request.getRealPath("/")+"jbpmPrcsDef"+File.separator+processName+File.separator+processName+".png";
	 imgPath = imgPath.replaceAll("\\\\", "/");
	 BufferedImage image = null;
	 for (String activityName : activitySet) {
	    ActivityCoordinates ac = repositoryService.getActivityCoordinates(
	 	processInstance.getProcessDefinitionId(), activityName);
	    image = ImageIO.read(new File(imgPath));
	    Graphics g = image.getGraphics();
	    g.setColor(new Color(255, 0, 0));
	    g.drawRect(ac.getX(), ac.getY(), ac.getWidth(), ac.getHeight());
	    g.dispose();
	 }
	 ByteArrayOutputStream output = new ByteArrayOutputStream();
	 ImageIO.write(image, "PNG", output);
	 response.setContentType("image/png");
     OutputStream stream = response.getOutputStream();
     stream.write(output.toByteArray());
     stream.flush();
     stream.close();
	}
	
}

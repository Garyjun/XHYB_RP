package com.brainsoon.jbpm.test;

import java.util.HashMap;
import java.util.Map;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.brainsoon.jbpm.constants.ProcessConstants;
import com.brainsoon.jbpm.service.IJbpmExcutionService;
import com.brainsoon.jbpm.service.impl.JbpmExcutionService;


public class StartProcess {
	public static void main(String[] args) {
        
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring-jbpm.xml");
		
		applicationContext.start();

		IJbpmExcutionService jbpmExcutionService = (IJbpmExcutionService) applicationContext
				.getBean("jbpmExcutionService");
		String processName="presOrderCheck";
		String wfId="101";
		Map<String, Object> vars=new HashMap<String, Object>();
		jbpmExcutionService.createProcessInstance(processName, wfId,"需求单-三国志", vars,"1");
		applicationContext.stop();

	
	}

}

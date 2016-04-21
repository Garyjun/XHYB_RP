package com.brainsoon.jbpm.test;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.brainsoon.jbpm.constants.ProcessConstants;
import com.brainsoon.jbpm.service.IJbpmExcutionService;

public class SetBusiDesc {
    public static void main(String[] args) {
        
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring-jbpm.xml");
		
		applicationContext.start();

		IJbpmExcutionService jbpmExcutionService = (IJbpmExcutionService) applicationContext
				.getBean("jbpmExcutionService");
		String processName="presOrderCheck";
		String busiId="101";
	    String busiDesc="三国志后传";
		jbpmExcutionService.doSetBusicDesc(processName, busiId, busiDesc);
		applicationContext.stop();

	
	
	}	

}

package com.brainsoon.jbpm.test;


import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.brainsoon.jbpm.service.IJbpmExcutionService;


public class EndTask {
	public static void main(String[] args) {
        
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring-jbpm.xml");
		
		applicationContext.start();

		IJbpmExcutionService jbpmExcutionService = (IJbpmExcutionService) applicationContext
				.getBean("jbpmExcutionService");
		String taskId="100003";
		String  transitionName="通过";
		String userId="1";
		jbpmExcutionService.endTask(taskId,transitionName, userId,"");
	
		applicationContext.stop();

	
	}



}

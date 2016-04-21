package com.brainsoon.jbpm.test;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.brainsoon.jbpm.service.IFreeJumpService;

public class TestFreeJump {
public static void main(String[] args) {
        
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring-jbpm.xml");
		applicationContext.start();

		IFreeJumpService freeJumpService = (IFreeJumpService) applicationContext.getBean("freeJumpService");
		String executionId="presOrderCheck.100";
		String nextTask="需求单修改";
		freeJumpService.doJumpToNextTask(executionId, nextTask);
		applicationContext.stop();

	
	}


}

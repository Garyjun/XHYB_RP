package com.brainsoon.jbpm.test;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.zip.ZipInputStream;

import org.jbpm.api.ProcessDefinition;
import org.jbpm.api.ProcessEngine;
import org.jbpm.api.RepositoryService;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class DelProcess {
	public static void main(String[] args) {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring-jbpm.xml");
		applicationContext.start();
		ProcessEngine processEngine = (ProcessEngine) applicationContext.getBean("processEngine");
		RepositoryService repositoryService = processEngine.getRepositoryService();

		List<ProcessDefinition> delList = repositoryService.createProcessDefinitionQuery().list();

		for (ProcessDefinition p : delList) {
			System.out.println("del process**** " + p.getName());
			repositoryService.deleteDeploymentCascade(p.getDeploymentId());
		}
		applicationContext.stop();
	}
}

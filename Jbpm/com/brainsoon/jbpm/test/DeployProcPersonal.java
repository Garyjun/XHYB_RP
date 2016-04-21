package com.brainsoon.jbpm.test;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.zip.ZipInputStream;

import org.jbpm.api.Configuration;
import org.jbpm.api.ProcessDefinition;
import org.jbpm.api.ProcessEngine;
import org.jbpm.api.RepositoryService;
import org.jbpm.pvm.internal.processengine.SpringHelper;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class DeployProcPersonal {
	public static void main(String[] args) throws Exception {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring-jbpm.xml");

		applicationContext.start();

		ProcessEngine processEngine = (ProcessEngine) applicationContext.getBean("processEngine");
		RepositoryService repositoryService = processEngine.getRepositoryService();

		File orderfile = new File("F:\\projectJEE\\bsrcm_cciph\\WebRoot\\jbpmPrcsDef\\subjectCheck\\subjectCheck.jpdl.xml");
		repositoryService.createDeployment().addResourceFromFile(orderfile).deploy();
		
//		List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().list();
//		for (ProcessDefinition p : list) {
//			System.out.println("process**** " + p.getId());
//
//		}

		applicationContext.stop();

	}
}

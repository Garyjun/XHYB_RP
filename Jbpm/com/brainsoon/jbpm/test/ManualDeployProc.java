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

import com.brainsoon.jbpm.service.IJbpmDefinitionService;

public class ManualDeployProc {
	public static void main(String[] args) {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring-jbpm.xml");
		applicationContext.start();
		IJbpmDefinitionService jbpmDefinitionService = (IJbpmDefinitionService) applicationContext.getBean("jbpmDefinitionService");
		applicationContext.stop();
	}
}

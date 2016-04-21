package com.brainsoon.rp.test;

import java.util.List;

import net.sf.json.JSONObject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.brainsoon.rp.model.annotation.Annotation;
import com.brainsoon.rp.service.IAnnotatorService;

/**
 * <dl>
 * <dt>ResearchPlateServiceTest.java</dt>
 * <dd>Function: 单元测试</dd>
 * <dd>Description:{详细描述该类的功能}</dd>
 * <dd>Copyright: Copyright (C) 2010</dd>
 * <dd>Company: 北京博云易迅技术有限公司</dd>
 * <dd>CreateUser: xujie</dd>
 * <dd>CreateDate: 2016年3月7日</dd>
 * </dl>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring-*.xml")
@Transactional
public class AnnotatorServiceTest {
	@Autowired
	private static IAnnotatorService annotatorService;

	@Test
	public void testSerarch() {
		JSONObject result = new JSONObject();
		List<Annotation> list = annotatorService.findAnnotations(100,"urn:art-19b14dd6-ee72-4d07-80f6-5345fae0ffe2");
		result.put("total", list.size());
		result.put("rows", list);
		System.err.println(result);
	}
}

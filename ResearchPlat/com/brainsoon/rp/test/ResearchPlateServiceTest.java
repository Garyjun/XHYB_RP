package com.brainsoon.rp.test;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.brainsoon.rp.service.IResearchPlatService;
import com.brainsoon.rp.service.impl.ResearchPlatService;
import com.brainsoon.rp.support.SearchParam;
import com.brainsoon.rp.support.TreeNode;
import com.googlecode.genericdao.search.Search;

/**
 * <dl>
 * <dt>ResearchPlateServiceTest.java</dt>
 * <dd>Function: {!!一句话描述功能}</dd>
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
public class ResearchPlateServiceTest {
	@Autowired
	private IResearchPlatService researchPlatService;

	@Test
	public void testCreate() {
		SearchParam param  = new SearchParam();
		param.setStart(1);
		param.setLength(10);
	}
}

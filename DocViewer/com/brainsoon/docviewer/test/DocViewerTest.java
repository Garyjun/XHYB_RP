package com.brainsoon.docviewer.test;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.brainsoon.docviewer.model.ResConverfileTask;
import com.brainsoon.docviewer.service.IResConverfileTaskService;

/**
 * 
 * @ClassName: UserServiceTest 
 * @Description:  该测试为集成测试，非单元测试
 * 功能代码按照Spring的标准书写，无论是通过XML配置还是通过annotation配置都可以。
 * 测试代码最重要的是告诉framework去哪里找bean的配置。
 * 该单元测试的特点：运用注释，使得编写测试更加简单，以及可以设置是否回滚。
	@RunWith(SpringJUnit4ClassRunner.class)
	表示该测试用例是运用junit4进行测试，也可以换成其他测试框架
	@Transactional 这个非常关键，如果不加入这个注解配置，事务控制就会完全失效！ 
	@TransactionConfiguration(transactionManager="txManager") 事物属性。为可选项，该项不会影响回滚的设置。
	@ContextConfiguration(locations={"classpath:spring-config.xml","../../../daoContext.xml"})
	该路径的设置时相当于该单元测试所在的路径，也可以用classpath进行设置，该设置还有一个inheritLocations的属性，默认为 true,表示子类可以继承该设置。
	@Autowired
	表示bean自动加载，而不用像之前的两个类要添加一个set的方法。
	@Test
	表示该方法是测试用例
	@Rollback(false)
	表示该测试用例不回滚
	=============本类说明==============：
	我在测试上使用了@Transactional注解，然后我将可以直接访问Session。
	如果测试没有使用@Transactional注解，事务将总是忽略掉@TransactionConfiguration提交。
	如果测试使用了@Transactional注解，@TransactionConfiguration将被视为默认配置。即使他被忽略了，在test执行最后的时候事务也将回滚，如果你想让他提交，就是用@TransactionConfiguration(defaultRollback=false).
	对于不同的生产环境而言，测试的@Configuration可能都不同，我这里使用的是嵌入式的H2数据库，对于真实的应用我将在测试数据库上使用相同的引擎。
	Spring事务配置的五种方式：
	http://www.blogjava.net/robbie/archive/2009/04/05/264003.html
 * @author tanghui 
 * @date 2013-8-10 下午9:24:04 
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-config.xml"})
@Transactional
/**
 * 
 * 组合单元测试-SuiteClasses的使用
 * public class AllTestClass {
	//此类的作用是整合测试也称 打包测试;可以把之前所有的写好的test class类进行集成;
	//如需测试多个类时，只需要把相关的测试类加入到"{}"即可;如果不是同一个包类的class记得加上package名称。
	//@Suite.SuiteClasses({JunitTest.class,TestClassDemo.class})
}
如果是需要多个单元测试类整合测试 使用一个Runner进行异步测试，
只需要把相关的class放入到SuiteClasses{}中即可,
如:JunitTest.class 和 TestClassDemo.class 都是写好的单元测试类.
 *
 */
//@Suite.SuiteClasses({JunitTest.class,TestClassDemo.class})
@TransactionConfiguration(transactionManager = "txManager", defaultRollback = true)
public class DocViewerTest {
    
    AtomicInteger counter = new AtomicInteger();
    
    @Autowired
    private IResConverfileTaskService resConverfileTaskService;
    
 
    @Test
    public void testCreate() {
    	ResConverfileTask rcft1 = new ResConverfileTask();
		rcft1.setResId("111");
		rcft1.setSrcPath("tb/ddd/1.doc");
		rcft1.setDoHasType("0,1,2"); //处理类型：0，文档或者视频转换（转换服务）  1，抽取封面文件，2抽取文本
		resConverfileTaskService.saveResConverfileTask(rcft1);
    }
    
    @Test
    public void testUpdate() {
        
//        User user = userService.save(genRandomUser());
//        String expectedPassword = "123234";
//        user.setPassword(expectedPassword);
//        userService.update(user);
//        
//        String actualPassword = userService.get(user.getId()).getPassword();
//        
//        assertEquals(expectedPassword, actualPassword);
        
    }
    
    @Test
    public void testDelete() {

//        resConverfileTaskService.delete(user.getId());
//        
//        int afterDbCount = userService.countAll();
//        
//        assertEquals(beforeDbCount, afterDbCount);
    }
    
    @Test
    public void testList() {

//        
//        assertThat(userList, hasItem(user));
    }
    
    
    public ResConverfileTask genRandomUser() {
        return null;
    }
    
    

}

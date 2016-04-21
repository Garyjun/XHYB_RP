/* 
 **TestMethod.java 
 */
package com.brainsoon.common.util.dofile.test;

import com.brainsoon.common.util.dofile.netfox.SiteFileFetch;
import com.brainsoon.common.util.dofile.netfox.SiteInfoBean;

/**
 * 
 * @ClassName: TestNetFox 
 * @Description:  断点续传 -测试类
 * @author tanghui 
 * @date 2013-8-14 下午5:23:06 
 *
 */
public class TestNetFox {
	public TestNetFox() {
		try {
			SiteInfoBean bean = new SiteInfoBean(
					"http://download.skycn.com/hao123-soft-online-bcs/soft/A/ADBEPHSPCS4.zip", "d:\\1",
					"test22.zip", 1);
			// SiteInfoBean bean = new
			// SiteInfoBean("http://localhost:8080/down.zip","L:\\temp","weblogic60b2_win.exe",5);
			SiteFileFetch fileFetch = new SiteFileFetch(bean);
			fileFetch.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new TestNetFox();
	}
}

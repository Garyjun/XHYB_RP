/* 
 **Utility.java 
 */
package com.brainsoon.common.util.dofile.netfox;

/**
 * 
 * @ClassName: Utility 
 * @Description:工具类  
 * @author tanghui 
 * @date 2013-8-14 下午5:22:58 
 *
 */
public class Utility {
	public Utility() {
	}

	public static void sleep(int nSecond) {
		try {
			Thread.sleep(nSecond);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void log(String sMsg) {
		System.err.println(sMsg);
	}

	public static void log(int sMsg) {
		System.err.println(sMsg);
	}
}

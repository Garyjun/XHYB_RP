package com.brainsoon.system.support;

import java.util.Random;

public class RandomString {
	public static String getRandomString(int length) { //length表示生成字符串的长度
	    String base = "CDEFIJLNOQRUWYZabcdefghijklmnopqrstuvwxyz0123456789";   
	    String first = "abcdefghijklmnopqrstuvwxyz";
	    Random random = new Random();   
	    StringBuffer sb = new StringBuffer();   
	    sb.append(first.charAt(random.nextInt(first.length())));
	    for (int i = 1; i < length; i++) {   
	        int number = random.nextInt(base.length());   
	        sb.append(base.charAt(number));   
	    }   
	    return sb.toString();   
	 }   
}

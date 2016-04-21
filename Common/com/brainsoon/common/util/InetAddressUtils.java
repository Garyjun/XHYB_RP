package com.brainsoon.common.util;

import java.net.*;

public class InetAddressUtils {
	
	// 取得LOCALHOST的IP地址
	public static InetAddress getMyIP() {
		InetAddress myIPaddress = null;
		try {
			myIPaddress = InetAddress.getLocalHost();
			} catch (UnknownHostException e) {
		}
		return (myIPaddress);
	}

	// 取得 域名的IP地址
	public static InetAddress getServerIP(String url) {
		InetAddress myServer = null;
		try {
			myServer = InetAddress.getByName(url);
		} catch (UnknownHostException e) {
		}
		return (myServer);
	}
	
	
	public static void main(String[] args) {
		String url;
		if (args.length > 0) {
			url = args[0];
		} else {
			url = "press.xnft.com.cn";
		}
		System.out.println("Your host IP is: " + getMyIP());
		System.out.println("The Server IP is :" + getServerIP(url));

	}
}
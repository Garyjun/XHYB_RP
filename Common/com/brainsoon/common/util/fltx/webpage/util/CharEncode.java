package com.brainsoon.common.util.fltx.webpage.util;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * 对字符进行各种编码转换的类。
 */
public class CharEncode {
	
	/**
	 * 是否为基本字符(ASCII)
	 */
	public static boolean isBaseChar(String text) {
		int strLen = text.length();
		int firstValue = 0;

		for (int i = 0; i < strLen; i++) {
			firstValue = (int) (text.charAt(i));

			if (firstValue >= 32 && firstValue < 128) {
				continue;
			} else {
				return false;
			}
		}
		return true;
	}

	/**
	 * 是否为UTF-8
	 */
	public static boolean isUTF8(String text) {
		int strLen = text.length();
		int firstValue = 0;
		int secValue = 0;
		int thirdValue = 0;
		boolean result = true;

		for (int i = 0; i < strLen; i++) {
			firstValue = (int) (text.charAt(i));

			//基本字符
			if (firstValue >= 32 && firstValue < 128) {
				continue;
			}

			if (firstValue >= 224 && firstValue < 240) {
				try {
					secValue = (int) (text.charAt(i + 1));
				} catch (Exception e) {
					return false;
				}
				if (secValue >= 128 && secValue < 192) {
					try {
						thirdValue = (int) (text.charAt(i + 2));
					} catch (Exception e) {
						return false;
					}
					if (thirdValue >= 128 && thirdValue < 192) {
						if (!(firstValue == 226 && secValue == 128 && thirdValue == 169)) {
							i += 2;
							continue;
						} else {
							return false;
						}
					}
				}
			} else {
				if (firstValue >= 192 && firstValue < 224) {
					try {
						secValue = (int) (text.charAt(i + 1));
					} catch (Exception e) {
						return false;
					}
					if (secValue >= 128 && secValue < 192) {
						i++;
						continue;
					} else {
						return false;
					}
				} else {
					return false;
				}
				//return false;
			}
		} //end for
		return result;
	}

    /**
	 * 是否为GBK
	 * GBK编码的范围是：高字节从0x81到0xFE，
	 *               低字节从0x40到0xFE，同时不包括0x7F。
	 */
	public static boolean isGBK(String text) {
		int strLen = text.length();
		int firstValue = 0;
		int secValue = 0;

		for (int i = 0; i < strLen; i++) {
			firstValue = (int) (text.charAt(i));

			if (firstValue >= 32 && firstValue < 128) {
				continue;
			}

			if (firstValue >= 129 && firstValue <= 254) {
				try {
					secValue = (int) (text.charAt(i + 1));
				} catch (Exception e) {
					return false;
				}
				if (secValue >= 64 && secValue <= 254 && secValue != 127) {
					i++;
					continue;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
		return true;
	}

	/**
	 * 是否为BIG5
	 * BIG5的范围是：高字节从0xA0到0xFE，低字节从0x40到0x7E，和0xA1到0xFE两部分。
	 */
	public static boolean isBIG5(String text) {
		int strLen = text.length();
		int firstValue = 0;
		int secValue = 0;

		for (int i = 0; i < strLen; i++) {
			firstValue = (int) (text.charAt(i));

			if (firstValue >= 32 && firstValue < 128) {
				continue;
			}

			if (firstValue >= 160 && firstValue <= 254) {
				try {
					secValue = (int) (text.charAt(i + 1));
				} catch (Exception e) {
					return false;
				}
				if ((secValue >= 64 && secValue <= 126)
						|| (secValue >= 161 && secValue <= 254)) {
					i++;
					continue;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
		return true;
	}

	/**
	 * 探测字符串是什么编码
	 * 
	 * @return 返回值说明：
	 *      	0: 未知编码
	 *      	1: ISO-8859-1
	 *      	2: GBK
	 *      	3: UTF-8
	 */
	public static int getEncodeType(String text) {
		if (isBaseChar(text)) {
			return 1;
		}

		if (isUTF8(text)) {
			return 3;
		}

		if (isGBK(text)) {
			return 2;
		}

		return 0;
	}

	/**
	 * 字符串由ISO-8859-1转换到UTF-8
	 */
	public static String Latin1ToUTF8(String text) {
		String result = null;
		try {
			result = new String(text.getBytes("8859_1"), "UTF-8");
		} catch (Exception e) {
		}
		return result;
	}
	
	/**
	 * 字符串由UTF-8转换到ISO-8859-1
	 */
	public static String UTF8ToLatin1(String text) {
		String result = null;
		try {
			result = new String(text.getBytes("UTF-8"), "8859_1");
		} catch (Exception e) {
		}
		return result;
	}
	
	/**
	 * 字符串由GBK转换到ISO-8859-1
	 */
	public static String GBToLatin1(String text) {
		String result = null;
		try {
			result = new String(text.getBytes("GBK"), "8859_1");
		} catch (Exception e) {
		}
		return result;
	}
	
	/**
	 * 字符串由ISO-8859-1转换到GBK
	 */
	public static String Latin1ToGB(String text) {
		String result = null;
		try {
			result = new String(text.getBytes("8859_1"), "GBK");
		} catch (Exception e) {
		}
		return result;
	}
	
	/**
	 * 字符串由ISO-8859-1转换到UTF-16
	 */
	public static String Latin1ToUTF16BE(String text) {
		String result = null;
		try {
			result = new String(text.getBytes("8859_1"), "UTF-16BE");
		} catch (Exception e) {
		}
		return result;
	}
	
	/**
	 * 字符串由UTF-8转换到GBK
	 */
	public static String UTF8ToGB(String text) {
		String result = null;
		try{
		        result = new String(text.getBytes("8859_1"),"UTF-8");
		        result = new String(result.getBytes("GB2312"), "8859_1");
		}
		catch(Exception e){}
		return result;
	}
	
	/**
	 * 字符串由GBK转换到UTF-8
	 */
	public static String GBToUTF8(String text) {
		String result = null;
		try {
			result = new String(text.getBytes("GBK"), "8859_1");
			result = new String(result.getBytes("8859_1"), "UTF-8");
		} catch (Exception e) {
		}
		return result;
	}
	
	/**
	 * 字符串由GBK转换到BIG5
	 */
	public static String GBToBig5(String text) {
		String result = null;
		try {
			result = new String(text.getBytes("8859_1"), "GBK");
			result = new String(result.getBytes("big5"), "8859_1");
		} catch (Exception e) {
		}
		return result;
	}
	
	/**
	 * 字符串由ISO-8859-1转换到BIG5
	 */
	public static String Latin1ToBig5(String text) {
		String result = null;
		try {
			result = new String(text.getBytes("8859_1"), "Big5");
		} catch (Exception e) {
		}
		return result;

	}
	
	/**
	 * 字符串由BIG5转换到ISO-8859-1
	 */
	public static String Big5ToLatin1(String text) {
		String result = null;
		try {
			result = new String(text.getBytes("Big5"), "8859_1");
		} catch (Exception e) {
		}
		return result;

	}
	
	/**
	 * 自动转换为GBK编码 
	 */
	public static String AutoGB(String text) {
		String result = null;
		switch (getEncodeType(text)) {
		case 0:
			result = Latin1ToGB(text);
			break;
		case 3:
			result = UTF8ToGB(text);
			break;
		default:
			result = new String(text);
		}
		return result;
	}
	
	/**
	 * 自动转换为UTF-8编码 
	 */
	public static String AutoUTF8(String text) {
		String result = null;
		switch (getEncodeType(text)) {
		case 0:
			result = Latin1ToUTF8(text);
			break;
		case 2:
			result = GBToUTF8(text);
			break;
		default:
			result = new String(text);
		}
		return result;
	}
	
	/**
	 * 自动转换为ISO-8859-1编码 
	 */
	public static String AutoLatin1(String text) {
		String result = null;
		switch (getEncodeType(text)) {
		case 2:
			result = GBToLatin1(text);
			break;
		case 3:
			result = UTF8ToLatin1(text);
			break;
		default:
			result = new String(text);
		}
		return result;
	}
	
	/**
	 * 转换为指定的编码格式后的字节数组
	 */
	public static byte[] testTrans(String text, String charset) {
		byte[] b = null;
		try {
			b = text.getBytes(charset);
		} catch (Exception e) {
		}
		return b;
	}
	
	/**
	 * 转换为Unicode字符集中顺序位置整数表示，每个字符的整数表示之间用“-”链接
	 */
	public static String getHexStr(String text) {
		int strLen = text.length();
		int firstValue = 0;
		String result = "";

		for (int i = 0; i < strLen; i++) {
			firstValue = (int) (text.charAt(i));
			if (!result.equals("")) {
				result += "-";
			}
			result += firstValue;
		}
		return result;
	}
	
	/**
	 * 转换为UNICODE的编码
	 */
	public static String stringToUnicode(String text) {
		String result = "";
		int input;
		InputStreamReader isr = new InputStreamReader(new ByteArrayInputStream(
				text.getBytes()));
		try {
			isr = new InputStreamReader(new ByteArrayInputStream(text
					.getBytes()), "GBK");
		} catch (UnsupportedEncodingException e) {
			return "-1";
		}
		try {
			while ((input = isr.read()) != -1) {
				result = result + "&#x" + Integer.toHexString(input) + ";";

			}
		} catch (java.io.IOException e) {

			return "-2";
		}
		try {
			isr.close();
		} catch (java.io.IOException e) {
			return "-3";
		}
		return result;
	}
	
	/**
	 * 指定字符串转换为十六进制表示，每个字符用“`”表示
	 */
	public static String stringToHexCode(String text) {
		if (text == null)
			return null;
		int strLen = text.length();
		int firstValue = 0;
		String out = "";
		for (int i = 0; i < strLen; i++) {
			firstValue = (int) (text.charAt(i));
			if (firstValue >= 32 && firstValue < 128) {
				out += (char) firstValue;
				if (firstValue == '`') {
					out += '`';
				}
			} else {
				out += "`" + Integer.toHexString(firstValue);
			}
		}
		return out;
	}
	
	/**
	 * 指定十六进制字符转换为字符串
	 */
	public static String hexCodeToString(String text) {
		if (text == null)
			return null;
		int strLen = text.length();
		int firstValue = 0;
		String out = "";
		for (int i = 0; i < strLen; i++) {
			firstValue = (int) (text.charAt(i));
			if (firstValue != '`') {
				out += (char) firstValue;
			} else {
				++i;
				if (text.charAt(i) == '`') {
					out += '`';
				} else {
					out += (char) Integer
							.parseInt(text.substring(i, i + 4), 16);
					i += 3;
				}
			}
		}
		return out;
	}

	public static void main(String[] args) {
		System.out.println((int)'中');
		System.out.println(getHexStr("中国"));
//		String a = CharEncode.stringToHexCode(c);
//		String a = CharEncode.getHexStr(c);
//		System.out.println(a);
//		String b = CharEncode.hexCodeToString(a);
//		System.out.println(b);
//		b += 0x59;
//		b += (char) (0x59 * 256 + 0xcb);
//		System.out.println(b);
		
//		System.out.println(CharEncode.GBToLatin1("华龙"));
	}
}
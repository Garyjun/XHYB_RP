package com.brainsoon.resource.util;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
/**
 * 
* @ClassName: CharacterUtils
* @Description: 字符转换工具类
* @author huangjun
* @date 2016-1-5 14:28:01
*
 */
public class CharacterUtils {
	
	/**
	 * 
	* @Title: stringFilter
	* @Description:  过滤特殊字符 
	* @param str
	* @return
	* @throws PatternSyntaxException    参数
	* @return String    返回类型
	* @throws
	 */
    public static String stringFilter(String  str) throws PatternSyntaxException {  
    	
        // 清除掉所有特殊字符   每个须过滤掉的字符串不能太长，所以放到数组里 多过滤几遍
    	String[] regEx={"[!';\\&`<>$^/@~∩_∩，~•×※§≈★… 、! ]" 
    			, "[? •ư 《》 ” “ °|{}『 』 【】^•〖〗「」]"
    			, "[〃±∑∏∪∩∈√⊥⊙∮≡≌∠∞∵∫℃￡‰ ..·……]"};   
		for (int i = 0; i < regEx.length; i++) {
			Pattern p = Pattern.compile(regEx[i]);      
			Matcher m = p.matcher(str);
			str = m.replaceAll("").trim();
		}
		return str;      
    }  
    
    /**
     * 
    * @Title: filterOffUtf8Mb4
    * @Description: 过滤掉非UTF-8字符方法
    * @param text
    * @return
    * @throws UnsupportedEncodingException    参数
    * @return String    返回类型
    * @throws
     */
    static public String filterOffUtf8Mb4(String text) throws UnsupportedEncodingException {  
        byte[] bytes = text.getBytes("UTF-8");  
        ByteBuffer buffer = ByteBuffer.allocate(bytes.length);  
        int i = 0;  
        while (i < bytes.length) {  
            short b = bytes[i];  
            if (b > 0) {  
                buffer.put(bytes[i++]);  
                continue;  
            }  
            b += 256;  
            if ((b ^ 0xC0) >> 4 == 0) {  
                buffer.put(bytes, i, 2);  
                i += 2;  
            }  
            else if ((b ^ 0xE0) >> 4 == 0) {  
                buffer.put(bytes, i, 3);  
                i += 3;  
            }  
            else if ((b ^ 0xF0) >> 4 == 0) {  
                i += 4;  
            }  
        }  
        buffer.flip();  
        return new String(buffer.array(), "utf-8");  
    }  
    
    /**
     * 
    * @Title: ToSBC
    * @Description: 半角转全角
    * @param input
    * @return    半角字符串
    * @return String    全角字符串
    * @throws
     */
    public static String ToSBC(String input) {
             char c[] = input.toCharArray();
             for (int i = 0; i < c.length; i++) {
               if (c[i] == ' ') {
                 c[i] = '\u3000';
               } else if (c[i] < '\177') {
                 c[i] = (char) (c[i] + 65248);

               }
             }
             return new String(c);
    }

    /**
     * 
    * @Title: ToDBC
    * @Description: 全角转半角
    * @param input
    * @return    全角字符串
    * @return String    半角字符串
    * @throws
     */
    public static String ToDBC(String input) {
        

             char c[] = input.toCharArray();
             for (int i = 0; i < c.length; i++) {
               if (c[i] == '\u3000') {
                 c[i] = ' ';
               } else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
                 c[i] = (char) (c[i] - 65248);

               }
             }
        String returnString = new String(c);
        
             return returnString;
    }

}

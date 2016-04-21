package com.brainsoon.resource.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;

import com.mysql.jdbc.log.Log;
import com.mysql.jdbc.log.LogFactory;

public class StringFileToolkit {

	private static final Logger logger = Logger.getLogger(StringFileToolkit.class); 

    /** 
     * 读取输入流为一个内存字符串,保持文件原有的换行格式 
     * 
     * @param in            输入流 
     * @param charset 文件字符集编码 
     * @return 文件内容的字符串 
     */ 
    public static String file2String(InputStream in, String charset) { 
            StringBuffer sb = new StringBuffer(); 
            try { 
                    LineNumberReader reader = new LineNumberReader(new BufferedReader(new InputStreamReader(in, charset))); 
                    String line; 
                    while ((line = reader.readLine()) != null) { 
                            sb.append(line).append(System.getProperty("line.separator")); 
                    } 
                    reader.close(); 
            } catch (UnsupportedEncodingException e) { 
                    logger.error("读取文件为一个内存字符串失败，失败原因是使用了不支持的字符编码" + charset, e); 
            } catch (IOException e) { 
            		logger.error("读取文件为一个内存字符串失败，失败原因是读取文件异常！", e); 
            } 
            return sb.toString(); 
    } 

    /** 
     * 读取文件为一个内存字符串,保持文件原有的换行格式 
     * 
     * @param file        文件对象 
     * @param charset 文件字符集编码 
     * @return 文件内容的字符串 
     */ 
    public static String file2String(File file, String charset) { 
            StringBuffer sb = new StringBuffer(); 
            try { 
                    LineNumberReader reader = new LineNumberReader(new BufferedReader(new InputStreamReader(new FileInputStream(file), charset))); 
                    String line; 
                    while ((line = reader.readLine()) != null) { 
                            sb.append(line).append(System.getProperty("line.separator")); 
                    } 
                    reader.close(); 
            } catch (UnsupportedEncodingException e) { 
            		logger.error("读取文件为一个内存字符串失败，失败原因是使用了不支持的字符编码" + charset, e); 
            } catch (FileNotFoundException e) { 
            		logger.error("读取文件为一个内存字符串失败，失败原因所给的文件" + file + "不存在！", e); 
            } catch (IOException e) { 
            		logger.error("读取文件为一个内存字符串失败，失败原因是读取文件异常！", e); 
            } 
            return sb.toString(); 
    } 

    /** 
     * 将字符串存储为一个文件，当文件不存在时候，自动创建该文件，当文件已存在时候，重写文件的内容，特定情况下，还与操作系统的权限有关。 
     * 
     * @param text         字符串 
     * @param distFile 存储的目标文件 
     * @return 当存储正确无误时返回true，否则返回false 
     */ 
    public static boolean string2File(String text, File distFile) { 
            if (!distFile.getParentFile().exists()) distFile.getParentFile().mkdirs(); 
            BufferedReader br = null; 
            BufferedWriter bw = null; 
            boolean flag = true; 
            try { 
                    br = new BufferedReader(new StringReader(text)); 
                    bw = new BufferedWriter(new FileWriter(distFile)); 
                    char buf[] = new char[1024 * 64];         //字符缓冲区 
                    int len; 
                    while ((len = br.read(buf)) != -1) { 
                            bw.write(buf, 0, len); 
                    } 
                    bw.flush(); 
                    br.close(); 
                    bw.close(); 
            } catch (IOException e) { 
                    flag = false; 
                    logger.error("将字符串写入文件发生异常！", e); 
            } 
            return flag; 
    } 

    public static void main(String[] args) { 
            String x = file2String(new File("C:\\a.txt"), "GBK"); 
            System.out.println(x); 

            boolean b = string2File(x, new File("C:\\b.txt")); 
            System.out.println(b); 
    } 
}

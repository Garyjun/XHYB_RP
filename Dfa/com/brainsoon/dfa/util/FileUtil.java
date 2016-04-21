/**
 * 
 */
package com.brainsoon.dfa.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;


/**
 * BSRCM应用com.channelsoft.mscp.common.utils.FileUtil.java 创建时间：2011-8-30 创建者：
 * liusy FileUtil TODO
 * 
 */
public class FileUtil {
    public static final Log logger = LogFactory.getLog(FileUtil.class);
    public static Properties properties;
    public static Calendar lastChangeTime;
    public static List<String> fileTypes;
    private static final DateFormat simpleDateFormat = new SimpleDateFormat(
            "yyyyMMddHHmmssSSS");
    
    public static String formatTestResult(String[] result) {
        StringBuilder testResult = new StringBuilder();
        Map<String, Integer> resultMap = new LinkedHashMap<String, Integer>();
        for (String string : result) {
            if (resultMap.keySet().contains(string)) {
                resultMap.put(string, resultMap.get(string) + 1);
            } else {
                resultMap.put(string, 1);
            }
        }
        for (String string : resultMap.keySet()) {
            testResult.append(string).append("(").append(resultMap.get(string))
                    .append("次)").append(",");
        }
        return testResult.subSequence(0, testResult.length() - 1).toString();
    }
}

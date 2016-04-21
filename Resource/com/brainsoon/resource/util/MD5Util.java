package com.brainsoon.resource.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.ServiceUI;
import javax.print.SimpleDoc;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.swing.JFileChooser;

import org.apache.log4j.Logger;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

  
/**
 * <dl> 生成文件对应的MD5码
 * <dt>MD5Util</dt>
 * <dd>Description:xxxxxxxxxxxxxxx </dd>
 * <dd>Copyright: Copyright (C) 2011</dd>
 * <dd>Company: 青牛（北京）技术有限公司</dd>
 * <dd>CreateDate: 2011-11-30下午02:35:52</dd>
 * </dl>
 * 
 * @author zhanglei
 */
public class MD5Util {  
	
	private static final Logger logger = Logger.getLogger(MD5Util.class);
    /** 
     * 默认的密码字符串组合，用来将字节转换成 16 进制表示的字符,apache校验下载的文件的正确性用的就是默认的这个组合 
     */  
    protected static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6','7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };  
    protected static MessageDigest messagedigest = null;  
    static {  
        try {  
            messagedigest = MessageDigest.getInstance("MD5");  
        } catch (NoSuchAlgorithmException e) {  
            logger.error(e);
        }  
    }  
  
    public static void main(String[] args) throws IOException {  
    	  MD5Util.writeWordFile();
//        File file = new File("e:/2.pdf" );  
//        String md5 = getFileMD5String(file);  
//        System.out.println("md5:" + md5);  
//        JFileChooser fileChooser = new JFileChooser(); //创建打印作业   
//        int state = fileChooser.showOpenDialog(null);   
//        if(state == fileChooser.APPROVE_OPTION){   
//            File file = new File("E:/积累/文档/构建系统.docx"); //获取选择的文件   
//            //构建打印请求属性集   
//            HashPrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();   
//            //设置打印格式，因为未确定类型，所以选择autosense   
//            DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;   
//            //查找所有的可用的打印服务   
//          //  PrintService printService[] = PrintServiceLookup.lookupPrintServices(flavor, pras); 
//            PrintService printService[] = new PrintService[1];
//            //定位默认的打印服务   
//            PrintService defaultService = PrintServiceLookup.lookupDefaultPrintService();   
//            printService[0] = defaultService;
//            //显示打印对话框   
//            PrintService service = ServiceUI.printDialog(null, 200, 200, printService,    
//                    defaultService, flavor, pras);   
//            if(service != null){   
//                try {   
//                    DocPrintJob job = service.createPrintJob(); //创建打印作业   
//                    FileInputStream fis = new FileInputStream(file); //构造待打印的文件流   
//                    DocAttributeSet das = new HashDocAttributeSet();   
//                    Doc doc = new SimpleDoc(fis, flavor, das);   
//                    job.print(doc, pras);   
//                } catch (Exception e) {   
//                    e.printStackTrace();   
//                }   
//            }   
  //      }   
    }  
  
    public static String getFileMD5String(File file) throws IOException {  
        InputStream fis;  
        fis = new FileInputStream(file);  
        byte[] buffer = new byte[1024];  
        int numRead = 0;  
        while ((numRead = fis.read(buffer)) > 0) {  
            messagedigest.update(buffer, 0, numRead);  
        }  
        fis.close();  
        return bufferToHex(messagedigest.digest());  
    }  
  
    private static String bufferToHex(byte bytes[]) {  
        return bufferToHex(bytes, 0, bytes.length);  
    }  
  
    private static String bufferToHex(byte bytes[], int m, int n) {  
        StringBuffer stringbuffer = new StringBuffer(2 * n);  
        int k = m + n;  
        for (int l = m; l < k; l++) {  
            appendHexPair(bytes[l], stringbuffer);  
        }  
        return stringbuffer.toString();  
    }  
  
    private static void appendHexPair(byte bt, StringBuffer stringbuffer) {  
        char c0 = hexDigits[(bt & 0xf0) >> 4];// 取字节中高 4 位的数字转换   
        // 为逻辑右移，将符号位一起右移,此处未发现两种符号有何不同   
        char c1 = hexDigits[bt & 0xf];// 取字节中低 4 位的数字转换   
        stringbuffer.append(c0);  
        stringbuffer.append(c1);  
    }  
    public static boolean writeWordFile() {  
        boolean w = false;  
        String path = "d:/";  
        try {  
            if (!"".equals(path)) {  
                // 检查目录是否存在  
                File fileDir = new File(path);  
                if (fileDir.exists()) {  
                    // 生成临时文件名称  
                    String fileName = "a.doc";  
                    String content = "<html><div style=\"text-align: center\"><span style=\"font-size: 28px\"><span style=\"font-family: 黑体\">" +  
                        "制度发布通知<br /> <br /> <img src=\"http://img0.bdstatic.com/img/image/shouye/sheying0311.jpg\"></img></span></span></div></html>";  
                    byte b[] = content.getBytes("GBK");  
                    ByteArrayInputStream bais = new ByteArrayInputStream(b);  
                    POIFSFileSystem poifs = new POIFSFileSystem();  
                    DirectoryEntry directory = poifs.getRoot();  
                    DocumentEntry documentEntry = directory.createDocument("WordDocument", bais);  
                    FileOutputStream ostream = new FileOutputStream(path+ fileName);  
                    poifs.writeFilesystem(ostream);  
                    bais.close();  
                    ostream.close();  
                }  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
      }  
      return w;  
    }  
}  

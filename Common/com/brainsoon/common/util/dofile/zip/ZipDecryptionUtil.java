package com.brainsoon.common.util.dofile.zip;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.zip.DataFormatException;

import org.apache.commons.io.FileUtils;

import de.idyl.winzipaes.AesZipFileDecrypter;
import de.idyl.winzipaes.AesZipFileEncrypter;
import de.idyl.winzipaes.impl.AESDecrypter;
import de.idyl.winzipaes.impl.AESDecrypterBC;
import de.idyl.winzipaes.impl.AESEncrypter;
import de.idyl.winzipaes.impl.AESEncrypterBC;
import de.idyl.winzipaes.impl.ExtZipEntry;

/**
 * 压缩指定文件或目录为ZIP格式压缩文件 : 支持密码(仅支持256bit的AES加密解密)
 * @author 唐辉
 *
 */
public class ZipDecryptionUtil {
	
	/**
	 * 使用指定密码将给定文件或文件夹压缩成指定的输出ZIP文件
	 * @param srcFile 需要压缩的文件或文件夹
	 * @param destZipPath 输出zip路径
	 * @param passwd 压缩文件使用的密码
	 * @param hasRootPath 是否包含根路径
	 * @param hasHiddenFile 是否包含隐藏文件
	 * 注意：此函数遇到大文件可能会出现堆内存溢出，解决思路有两种：
	 * 1） 增加JVM的内存参数  如：-Xms1024m -Xmx2048m
	 * 2） 如果是本身是压缩文件，则请先解压后再使用本方法
	 */
	public static void encryptZipFile(String srcFile,String destZipPath,String passwd,boolean hasRootPath,boolean hasHiddenFile) {
		AESEncrypter encrypter = new AESEncrypterBC();
		AesZipFileEncrypter zipFileEncrypter = null;
		try {
			zipFileEncrypter = new AesZipFileEncrypter(destZipPath, encrypter);
			/**
			 * 此方法是修改源码后添加,用以支持中文文件名
			 */
			zipFileEncrypter.setEncoding("utf8");
			File sFile = new File(srcFile);
			/**
			 * AesZipFileEncrypter提供了重载的添加Entry的方法,其中:
			 * add(File f, String passwd)  方法是将文件直接添加进压缩文件
			 * add(File f,  String pathForEntry, String passwd) 方法是按指定路径将文件添加进压缩文件
			 * pathForEntry - to be used for addition of the file (path within zip file)
			 */
			doZip(sFile, zipFileEncrypter, "", passwd,hasRootPath,hasHiddenFile);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				zipFileEncrypter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 具体压缩方法,将给定文件添加进压缩文件中,并处理压缩文件中的路径
	 * @param file 给定磁盘文件(是文件直接添加,是目录递归调用添加)
	 * @param encrypter AesZipFileEncrypter实例,用于输出加密ZIP文件
	 * @param pathForEntry ZIP文件中的路径
	 * @param passwd 压缩密码
	 * @param hasRootPath 是否包含根目录
	 * @param hasHiddenFile 是否包含隐藏文件
	 * @throws IOException
	 */
	private static void doZip(File file, AesZipFileEncrypter encrypter,
			String pathForEntry, String passwd,boolean hasRootPath,boolean hasHiddenFile) throws IOException {
		if (file.isFile()) {
			if(file.isHidden() && !hasHiddenFile){
				return;
			}else{
				pathForEntry += file.getName();
				encrypter.add(file, pathForEntry, passwd);
			}
			return;
		}
		if(hasRootPath){
			pathForEntry += file.getName() + File.separator;
		}
		for(File subFile : file.listFiles()) {
			doZip(subFile, encrypter, pathForEntry, passwd,true,hasHiddenFile);
		}
	}
	
	/**
	 * 使用给定密码解压指定压缩文件到指定目录
	 * @param zipFile 指定Zip文件
	 * @param outDir 解压到目录
	 * @param passwd 解压密码
	 */
	public static void unEncryptZipFile(String zipFile, String outDir, String passwd) {
		File outDirectory = new File(outDir);
		if (!outDirectory.exists()) {
			outDirectory.mkdir();
		}
		AESDecrypter decrypter = new AESDecrypterBC();
		AesZipFileDecrypter zipDecrypter = null;
		try {
			zipDecrypter = new AesZipFileDecrypter(new File(zipFile), decrypter);
			AesZipFileDecrypter.charset = "utf-8";
			/**
			 * 得到ZIP文件中所有Entry,但此处好像与JDK里不同,目录不视为Entry
			 * 需要创建文件夹,entry.isDirectory()方法同样不适用,不知道是不是自己使用错误
			 * 处理文件夹问题处理可能不太好
			 */
			List<ExtZipEntry> entryList = zipDecrypter.getEntryList();
			for(ExtZipEntry entry : entryList) {
				String eName = entry.getName();
				String dir = eName.substring(0, eName.lastIndexOf(File.separator) + 1);
				File extractDir = new File(outDir, dir);
				if (!extractDir.exists()) {
					FileUtils.forceMkdir(extractDir);
				}
				/**
				 * 抽出文件
				 */
				File extractFile = new File(outDir + File.separator + eName);
				zipDecrypter.extractEntry(entry, extractFile, passwd);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DataFormatException e) {
			e.printStackTrace();
		} finally {
			try {
				zipDecrypter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 测试
	 * @param args
	 */
	public static void main(String[] args) {
		/**
		 * 压缩测试
		 * 可以传文件或者目录
		 */
		encryptZipFile("D:/项目文档/中版集团2期/11111.zip", "D:/项目文档/中版集团2期/2222222.zip", "zyh",false,false);
		//unEncryptZipFile("D:/项目文档/中版集团2期/222222.zip","D:/项目文档/中版集团2期/222222", "zyh");
	}
}

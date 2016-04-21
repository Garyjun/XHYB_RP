package com.brainsoon.common.util.dofile.zip;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brainsoon.common.util.dofile.util.ConstantsDef;
import com.brainsoon.common.util.dofile.util.DoFileException;
import com.brainsoon.common.util.dofile.util.DoFileUtils;
import com.brainsoon.common.util.dofile.util.WebAppUtils;
import com.brainsoon.resource.util.ResUtils;
import com.brainsoon.semantic.ontology.model.Ca;

import de.idyl.winzipaes.AesZipFileEncrypter;
import de.idyl.winzipaes.impl.AESEncrypter;
import de.idyl.winzipaes.impl.AESEncrypterBC;

/**
 * 
 * @ClassName: ZipOrRarUtil 
 * @Description: 压缩解压文件工具类，支持zip or rar 文件，如果出现中文文件名这个类出错  
 * @author tanghui 
 * @date 2014-5-5 下午12:34:28 
 *
 */
public class ZipOrRarUtil {
	private static final String RAR = "rar";
	private static Log logger = LogFactory.getLog(ZipOrRarUtil.class);

	// 文件解压临时目录
	private static String fileUnZipTempDir;
	// 文件打压缩包临时目录
	private static String fileZipTempDir;
	static {
		fileUnZipTempDir = WebAppUtils.getWebRootBaseDir(ConstantsDef.fileTemp)  + "unzip/";
		fileZipTempDir = WebAppUtils.getWebRootBaseDir(ConstantsDef.fileTemp) + "zip/";
	}

	/**
	 * 转换字符编码格式
	 */
	private static String makeToGB(String str) {
		try {
			return new String(str.getBytes("8859_1"), "GB2312");
		} catch (UnsupportedEncodingException ioe) {
			return str;
		}
	}
	
	
	/**
	 * 
	 * @Title: unzip 
	 * @Description: 解压zip包，默认删除源zip文件，可以解压带中文字符的压缩文件
	 * @param zipFileName 源zip文件
	 * @param outputDirectory 解压目录
	 * @param fileType 文件类型
	 * @return void 
	 * @throws
	 */
	public static void unzip(String zipFileName, String outputDirectory,
			String fileType) throws Exception {
		unzip(zipFileName, outputDirectory, fileType,true);
	}
	/**
	 * 
	 * @Title: unzip 
	 * @Description: 解压zip包，默认删除源zip文件，可以解压带中文字符的压缩文件(直接生成CA的文件数组)
	 * @param zipFileName 源zip文件
	 * @param outputDirectory 解压目录
	 * @param fileType 文件类型
	 * @param importCoverType 导入封面类型
	 * @return void 
	 * @throws
	 */
	public static Ca unzipCa(String zipFileName, String outputDirectory,
			String fileType,Ca ca,String doi) throws Exception {
		return unzipCa(zipFileName, outputDirectory, fileType,true,ca,doi);
	}
	/**
	 * 
	 * @Title: unzip 
	 * @Description: 解压zip包，可以解压带中文字符的压缩文件
	 * @param zipFileName 源zip文件
	 * @param outputDirectory 解压目录
	 * @param fileType 文件类型
	 * @param isDelSourceFile 是否删除源zip文件   
	 * @return void 
	 * @throws
	 */
	public static void unzip(String zipFileName, String outputDirectory,
			String fileType,boolean isDelSourceFile) throws Exception {
		DoFileUtils.createdir(outputDirectory, "");
		// 判断文件类型 start...
		boolean b = false;
		if (StringUtils.isNotBlank(fileType)) {
			if (fileType.equals(RAR)) {
				b = true;
			}
		} else {
			if (DoFileUtils.isMyWantType(zipFileName, RAR)) {
				b = true;
			}
		}
		// 判断文件类型 end

		if (b) {
			// 如果传入的文件路径不是含RAR的，需要做转换，否则不用
			if (!DoFileUtils.isMyWantType(zipFileName, RAR)) {
				String filePath = fileUnZipTempDir + UUID.randomUUID() + ".rar";
				File file = new File(filePath);
				FileUtils.copyFile(new File(zipFileName), file);
				RarUtil.unRar(filePath, outputDirectory);
				if(isDelSourceFile){
					file.delete();
				}
			} else {
				RarUtil.unRar(zipFileName, outputDirectory);
			}
		} else {
			ZipUtil.unZipToFolder(zipFileName, outputDirectory);
		}
	}
	/**
	 * 
	 * @Title: unzip 
	 * @Description: 解压zip包，可以解压带中文字符的压缩文件(Ca)
	 * @param zipFileName 源zip文件
	 * @param outputDirectory 解压目录
	 * @param fileType 文件类型
	 * @param isDelSourceFile 是否删除源zip文件   
	 * @param importCoverType 导入封面类型
	 * @return void 
	 * @throws
	 */
	public static Ca unzipCa(String zipFileName, String outputDirectory,
			String fileType,boolean isDelSourceFile,Ca ca,String doi) throws Exception {
		DoFileUtils.createdir(outputDirectory, "");
		// 判断文件类型 start...
		boolean b = false;
		if (StringUtils.isNotBlank(fileType)) {
			if (fileType.equals(RAR)) {
				b = true;
			}
		} else {
			if (DoFileUtils.isMyWantType(zipFileName, RAR)) {
				b = true;
			}
		}
		// 判断文件类型 end

		if (b) {
			// 如果传入的文件路径不是含RAR的，需要做转换，否则不用
			if (!DoFileUtils.isMyWantType(zipFileName, RAR)) {
				String filePath = fileUnZipTempDir + UUID.randomUUID() + ".rar";
				File file = new File(filePath);
				FileUtils.copyFile(new File(zipFileName), file);
				ca = RarUtil.unRarCaNew(filePath, outputDirectory,ca,doi);
				if(isDelSourceFile){
					file.delete();
				}
			} else {
				ca = RarUtil.unRarCaNew(zipFileName, outputDirectory,ca,doi);
			}
			return ca;
		} else {
			ca = ZipUtil.unZipToCaNew(zipFileName, outputDirectory,ca,doi);
		}
		return ca;
	}

	/**
	 * 
	 * @Title: getFileType
	 * @Description: 取得文件格式
	 * @param
	 * @return String
	 * @throws
	 */
	public static String getFileType(String fileUrl) {
		String fileType = "";
		if (StringUtils.isNotBlank(fileUrl)) {
			fileType = fileUrl.substring(fileUrl.lastIndexOf(".") + 1,
					fileUrl.length()).toLowerCase();
		}
		return fileType;
	}

	/**
	 * 解压文件
	 * 
	 * @param inputFilePath
	 * @throws DoFileException
	 */
	public static void unZip(String inputFilePath) throws DoFileException {
		ZipInputStream zipInputStream = null;
		ZipEntry zipEntry = null;
		try {
			if (DoFileUtils.isMyWantType(inputFilePath, RAR)) {
				RarUtil.unRar(inputFilePath);
			} else {
				// 检查是否是ZIP文件
				checkFileIsExist(inputFilePath);
				// 建立与目标文件的输入连接
				zipInputStream = new ZipInputStream(new FileInputStream(
						inputFilePath));

				zipEntry = zipInputStream.getNextEntry();
				String dirname = createdir(inputFilePath);
				while (zipEntry != null) {
					String zipEntryName = zipEntry.getName();
					String changeZipEntryName = makeToGB(zipEntryName);
					String filePath = dirname + File.separator;
					int lastPoint = replaceLine(changeZipEntryName)
							.lastIndexOf('\\');
					if (lastPoint != -1) {
						unZipWriteDir(filePath
								+ replaceLine(changeZipEntryName).substring(0,
										lastPoint));
					}
					if (zipEntry.isDirectory()) {
						unZipWriteDir(zipEntryName);
					} else {
						String fileName = replaceLine(changeZipEntryName);
						if (StringUtils.isNotBlank(fileName))
							unZipWriteFile(zipInputStream, filePath
									+ zipEntryName);
					}
					zipEntry = zipInputStream.getNextEntry();
				}
			}
		} catch (IOException i) {
			throw new DoFileException("文件流处理异常");
		} catch (IllegalArgumentException e) {
			throw new DoFileException("压缩格式异常");
		} finally {
			try {
				if (zipInputStream != null)
					zipInputStream.close();
			} catch (IOException e) {
				throw new DoFileException("文件流关闭异常");
			}
		}
	}

	
	/**
	 * 
	 * @Title: checkFileIsExist 
	 * @Description: 校验文件是否存在
	 * @param   
	 * @return void 
	 * @throws
	 */
	private static void checkFileIsExist(String inputFilePath)
			throws DoFileException {
		org.apache.tools.zip.ZipFile zipFile = null;
		try {
			File inFile = new File(inputFilePath);
			if (!inFile.exists() || inFile.isDirectory()) {
				throw new DoFileException("压缩文件不存在");
			}
			zipFile = new org.apache.tools.zip.ZipFile(inFile);
		} catch (ZipException zipe) {
			throw new DoFileException("文件格式异常");
		} catch (IOException ioe) {
			throw new DoFileException("文件读取异常");
		} finally {
			try {
				if (zipFile != null)
					zipFile.close();
			} catch (IOException e) {
				throw new DoFileException("文件关闭异常");
			}
		}
	}

	private static String replaceLine(String str) {
		if (StringUtils.isBlank(str))
			return "";
		return str.replace('/', '\\');
	}

	private static void unZipWriteDir(String fileDir) {
		File dirs = new File(replaceLine(makeToGB(fileDir)));
		if (!dirs.mkdir()) {
			throw new DoFileException("创建文件目录失败！");
		}
	}

	private static void unZipWriteFile(ZipInputStream zipInputStream,
			String filePath) throws DoFileException {
		byte[] bytes = new byte[1024];
		int in;
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(filePath);
			while ((in = zipInputStream.read(bytes, 0, bytes.length)) != -1)
				fileOutputStream.write(bytes, 0, in);
		} catch (IOException ioe) {
			throw new DoFileException("文件读取异常");
		} finally {
			try {
				if (fileOutputStream != null) {
					fileOutputStream.close();
				}
			} catch (IOException e) {
				throw new DoFileException("文件流关闭异常");
			}
		}
	}

	private static String createdir(String inputFilePath) {
		int lastPoint = inputFilePath.lastIndexOf('.');
		if (lastPoint != -1)
			inputFilePath = inputFilePath.substring(0, lastPoint);
		File newdir = new File(inputFilePath);
		if (!newdir.mkdir()) {
			throw new DoFileException("创建文件目录失败！");
		}
		return inputFilePath;
	}

	/**
	 * 压缩文件和目录
	 * 
	 * @param inputFileName
	 *            ：被压缩的目录或文件的绝对路径
	 * @param outputPath
	 *            ：压缩后文件的绝对路径
	 * @throws DoFileException
	 */
	public static void zip(String inputFileName, String outputPath)
			throws DoFileException {
		ZipOutputStream out = null;
		try {
			out = new ZipOutputStream(new FileOutputStream(outputPath));
			File file = new File(inputFileName);
			if (file.isDirectory())
				zipDir(file, out);
			else
				zipFile(file, out);
		} catch (IOException e) {
			throw new DoFileException("压缩文件处理异常");
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (IOException e) {
				throw new DoFileException("关闭压缩文件异常");
			}
		}
	}

	private static void zipFile(File file, ZipOutputStream out)
			throws DoFileException {
		zipWriteFile(file, out, file.getName());
	}

	private static void zipDir(File file, ZipOutputStream out)
			throws DoFileException {
		zipDir(file, out, "");
	}

	private static void zipDir(File file, ZipOutputStream out, String base)
			throws DoFileException {
		if (file.isDirectory()) {
			zipWriteDir(file, out, base);
			return;
		}
		zipWriteFile(file, out, base);
	}

	/**
	 * 压缩文件操作写目录
	 */
	private static String zipWriteDir(File file, ZipOutputStream out,
			String base) throws DoFileException {

		try {
			File[] fl = file.listFiles();
			out.putNextEntry(new ZipEntry(base + "/"));
			String temp = (base.length() == 0) ? "" : base + "/";
			for (int i = 0; i < fl.length; i++) {
				zipDir(fl[i], out, temp + fl[i].getName());
			}

			return temp;
		} catch (IOException e) {
			throw new DoFileException("压缩文件处理异常");
		}
	}

	/**
	 * 压缩文件操作写文件
	 */
	private static void zipWriteFile(File file, ZipOutputStream out, String base)
			throws DoFileException {
		FileInputStream in = null;
		try {
			out.putNextEntry(new ZipEntry(base));
			in = new FileInputStream(file);
			int b;
			while ((b = in.read()) != -1) {
				out.write(b);
			}
		} catch (IOException e) {
			throw new DoFileException("压缩文件处理异常");
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				throw new DoFileException("流关闭异常");
			}
		}

	}

	/**
	 * 
	 * @param f
	 * @param out
	 * @param base
	 * @throws DoFileException
	 */
	private static void zipCN(File f, org.apache.tools.zip.ZipOutputStream out,
			String base) throws DoFileException {
		FileInputStream in = null;
		try {
			if (f.isDirectory()) {
				File[] fl = f.listFiles();
				out.putNextEntry(new org.apache.tools.zip.ZipEntry(base + "/"));
				base = base.length() == 0 ? "" : base + "/";
				for (int i = 0; i < fl.length; i++) {
					zipCN(fl[i], out, base + fl[i].getName());
				}
			} else {
				out.putNextEntry(new org.apache.tools.zip.ZipEntry(StringUtils
						.isBlank(base) ? f.getName() : base));
				in = new FileInputStream(f);

				int len;
				byte[] buf = new byte[1024];
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
			}
		} catch (IOException e) {
			throw new DoFileException("压缩文件出现异常：" + e.getMessage());
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException se) {
				throw new DoFileException("关闭文件出现异常：" + se.getMessage());
			}
		}
	}

	/**
	 * 压缩文件
	 * 
	 * @param inputFile
	 * @param zipFileName
	 * @throws DoFileException
	 *             注： 1、如果inputFile是目录，则将该目录下的文件以及目录统一压缩成一个文件。 例：new
	 *             File("D:\\kk") 2、如果inputFile是一个目录，则压缩后的zip文件的路径不能设置在这个目录中
	 */
	public static void zipCN(File inputFile, String zipFileName)
			throws DoFileException {

		org.apache.tools.zip.ZipOutputStream out = null;
		try {

			out = new org.apache.tools.zip.ZipOutputStream(
					new BufferedOutputStream(new FileOutputStream(zipFileName)));
			out.setEncoding("GBK");
		} catch (FileNotFoundException se) {
			throw new DoFileException("文件不存在：" + se.getMessage());
		}
		try {
			zipCN(inputFile, out, "");
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e1) {
				throw new DoFileException("关闭文件出现异常:" + e1.getMessage());
			}
		}
	}

	/**
	 * 删除文件夹下所有内容，包括此文件夹删除文件夹下所有内容，包括此文件夹
	 * 
	 * @param f
	 * @throws IOException
	 */
	public static void delAll(File f) throws IOException {
		if (!f.exists())// 文件夹不存在不存在
		{
			throw new IOException("指定目录不存在:" + f.getName());
		}

		boolean rslt = true;// 保存中间结果

		if (!(rslt = f.delete())) {// 先尝试直接删除

			// 若文件夹非空。枚举、递归删除里面内容
			File subs[] = f.listFiles();

			for (int i = 0; i <= subs.length - 1; i++) {

				if (subs[i].isDirectory()) {
					delAll(subs[i]);// 递归删除子文件夹内容
				}
				rslt = subs[i].delete();// 删除子文件夹本身
			}
			rslt = f.delete();// 删除此文件夹本身
		}
		if (!rslt)
			throw new IOException("无法删除:" + f.getName());
		return;
	}

	public static String joinIdForName(String[] outIds) {
		if (outIds == null || outIds.length <= 0) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		sb.append("【");
		for (int i = 0; i < outIds.length; i++) {
			if (i == 0) {
				sb.append(outIds[i]);
			} else {
				sb.append("-").append(outIds[i]);
			}
		}
		sb.append("】");
		return sb.toString();
	}

	/**
	 * 文件加密压缩 --如果是多个文件，则先压缩成ZIP文件，再调用该方法，生成加密的ZIP文件
	 * 
	 * @param File
	 *            要加密压缩的的文件
	 * @param zipFileName
	 *            生成的压缩文件路径和名称
	 * @param password
	 *            压缩密码
	 * @author zhanglei
	 * @throws IOException
	 */
	@SuppressWarnings("finally")
	public static File encryptZipFile(File file, String filePath,
			String password) {
		DateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		if (StringUtils.isBlank(filePath)) {
			// 为空时放入临时文件夹下面（先前传递的文件名将没意义）
			filePath = fileZipTempDir + simpleDateFormat.format(new Date());
		}
		File directory = new File(filePath);
		if (!directory.exists()) {
			directory.mkdirs();
		}
		String fileName = filePath + File.separatorChar + "加密压缩包.zip";
		AesZipFileEncrypter enc = null;
		AESEncrypter encrypter = new AESEncrypterBC();
		try {
			enc = new AesZipFileEncrypter(fileName, encrypter);
			enc.add(file, password);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				enc.close();
				File zipFile = new File(fileName);
				return zipFile;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	/**
	 * 对文件集合进行打包
	 * 
	 * @param outFiles
	 * @return
	 */
	public static File getZipFile(List<File> files) {

		DateFormat batchDate = new SimpleDateFormat("yyyyMMddHHmmssSSS");

		FileInputStream fis = null;
		org.apache.tools.zip.ZipOutputStream zipOutputStream = null;

		File zipFile = new File(fileZipTempDir + batchDate.format(new Date())
				+ File.separatorChar + "压缩.zip");
		if (!zipFile.getParentFile().exists()) {
			zipFile.getParentFile().mkdirs();
		}
		try {
			zipOutputStream = new org.apache.tools.zip.ZipOutputStream(
					new FileOutputStream(zipFile));
			zipOutputStream.setEncoding("GBK");
			byte[] buffer = new byte[4096];
			for (File file : files) {
				String name = file.getName();
				fis = new FileInputStream(file);
				zipOutputStream.putNextEntry(new org.apache.tools.zip.ZipEntry(
						name));
				int len;
				while ((len = fis.read(buffer)) > 0) {
					zipOutputStream.write(buffer, 0, len);
				}
				zipOutputStream.closeEntry();
				fis.close();
			}
		} catch (IOException e) {
			logger.error(e);
			logger.error("压缩文件时出错！");
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					logger.error(e);
				}
			}
			if (zipOutputStream != null) {
				try {
					zipOutputStream.close();
				} catch (IOException e) {
					logger.error(e);
				}
			}
		}
		return zipFile;
	}

	/**
	 * @param outFiles
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static InputStream zipStream(List<File> files)
			throws UnsupportedEncodingException {

		DateFormat batchDate = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		org.apache.tools.zip.ZipOutputStream zipOutputStream = null;
		File zipFile = new File(fileZipTempDir + batchDate.format(new Date())
				+ File.separatorChar + "压缩.zip");
		if (!zipFile.getParentFile().exists()) {
			zipFile.getParentFile().mkdirs();
		}
		try {
			zipOutputStream = new org.apache.tools.zip.ZipOutputStream(
					new FileOutputStream(zipFile));
			zipOutputStream.setEncoding("GBK");
			byte[] buffer = new byte[4096];
			for (File file : files) {
				FileInputStream fis = null;
				try {
					fis = new FileInputStream(file);
					String name = file.getName();
					zipOutputStream
							.putNextEntry(new org.apache.tools.zip.ZipEntry(
									name));
					int len;
					while ((len = fis.read(buffer)) > 0) {
						zipOutputStream.write(buffer, 0, len);
					}
					zipOutputStream.closeEntry();
				} catch (IOException ex) {
					logger.error(ex);
				} finally {
					if (fis != null) {
						try {
							fis.close();
						} catch (IOException e) {
							logger.error(e);
						}
					}
				}
			}
		} catch (IOException e) {
			logger.error(e);
			logger.error("压缩文件时出错");
		} finally {
			if (zipOutputStream != null) {
				try {
					zipOutputStream.close();
				} catch (IOException e) {
					logger.error(e);
				}
			}
		}
		return formalStream(zipFile);
	}

	/**
	 * @param outFile
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static InputStream formalStream(File outFile) {
		InputStream out = null;
		try {
			out = new FileInputStream(outFile);// 将file转换成输入流
		} catch (Exception e) {
			throw new DoFileException(e.getMessage());
		}
		return out;
	}

	/**
	 * 解压zip包，可以解压中文字符的压缩文件
	 * 
	 * @param zipFileName
	 * @param outputDirectory
	 * @throws Exception
	 */
	public static void unzip(String zipFileName, String outputDirectory) {
		org.apache.tools.zip.ZipFile zipFile = null;
		try {
			zipFile = new org.apache.tools.zip.ZipFile(zipFileName);
			java.util.Enumeration e = zipFile.getEntries();
			org.apache.tools.zip.ZipEntry zipEntry = null;
			DoFileUtils.createdir(outputDirectory, "");
			while (e.hasMoreElements()) {
				zipEntry = (org.apache.tools.zip.ZipEntry) e.nextElement();
				logger.debug("正在解压: " + zipEntry.getName());
				String name = null;
				if (zipEntry.isDirectory()) {
					name = zipEntry.getName();
					name = name.substring(0, name.length() - 1);
					File f = new File(outputDirectory + File.separator + name);
					if (!f.exists()) {
						// 2012年10月22日 zuohl 创建文件夹
						DoFileUtils.createdir(outputDirectory, name);
					}
					if (!f.exists() && !f.mkdir()) {
						throw new DoFileException("创建文件目录失败！");
					}
				} else {
					String fileName = zipEntry.getName();
					fileName = fileName.replace('\\', '/');

					if (fileName.indexOf("/") != -1) {
						DoFileUtils.createdir(outputDirectory, fileName.substring(0,
								fileName.lastIndexOf("/")));
						fileName = fileName.substring(
								fileName.lastIndexOf("/") + 1,
								fileName.length());
					}

					File f = new File(outputDirectory + File.separator
							+ zipEntry.getName().replace('\\', '/'));

					if (!f.createNewFile()) {
						throw new DoFileException("创建文件失败！");
					}
					InputStream in = zipFile.getInputStream(zipEntry);
					FileOutputStream out = new FileOutputStream(f);

					byte[] by = new byte[1024];
					int c;
					while ((c = in.read(by)) != -1) {
						out.write(by, 0, c);
					}
					out.close();
					in.close();
				}
			}
		} catch (ZipException e) {
			logger.error("读取zip文件时异常", e);
			throw new DoFileException("读取zip文件时异常", e);
		} catch (FileNotFoundException e) {
			logger.error("zip文件没有找到", e);
			throw new DoFileException("zip文件没有找到", e);
		} catch (IOException e) {
			logger.error("读取zip文件时IO异常", e);
			throw new DoFileException("读取zip文件时IO异常", e);
		} finally {
			if (null != zipFile) {
				try {
					zipFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args) {
		// DoFileUtils.createdir("D:\\channelSpace","D:\\channelSpace\\test\\aaaa\\bbb");
		// unzip("D:/4/n.rar","d:/4/","rar");
	}
}

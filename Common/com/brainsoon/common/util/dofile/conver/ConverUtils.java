package com.brainsoon.common.util.dofile.conver;

import java.awt.Color;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.brainsoon.common.util.dofile.image.ImageUtils;
import com.brainsoon.common.util.dofile.image.ImgCoverUtil;
import com.brainsoon.common.util.dofile.metadata.FileMetadataFactory;
import com.brainsoon.common.util.dofile.metadata.fo.Multimedia;
import com.brainsoon.common.util.dofile.util.ConstantsDef;
import com.brainsoon.common.util.dofile.util.DateTools;
import com.brainsoon.common.util.dofile.util.DoFileException;
import com.brainsoon.common.util.dofile.util.DoFileUtils;
import com.brainsoon.common.util.dofile.util.PropertiesReader;
import com.brainsoon.common.util.dofile.util.WebAppUtils;

/**
 * 
 * @ClassName: ConverUtils
 * @Description: 视频处理工具类 主要完成视频的格式转换和视频截图，打水印 命令行的调用方式实现： 
 * 主要调用工具是 Ffmpeg + Mencoder
 * @author tanghui
 * @date 2014-4-22 下午4:36:39
 * 
 */
public class ConverUtils {

	protected static final Logger logger = Logger.getLogger(ConverUtils.class);
	public static String ffmpegPath;// ffmpeg的目录（win or linux）
	public static String ffmpeg2theoraPath;
	public static String mencoderPath;
	public static String mp4BoxPath;
	public static boolean convertSuccess;

	/** 完成进度属性 */
	private static int COMPLETE = 0;

	// 配置图片及视频相关参数常量
	public static final String STARTTIME = "00.003"; // 默认视频开始时间
	public static final String ENDTIME = "00.001"; // 默认视频结束时间
	public static final String AVSPATH = "fileTemp/watermark/avs/"; // AVS文件默认路径
	public static final String TXTIMGPATH = "watermark/txtImg/"; // 文字生成的图片临时路径

	static {
		ffmpegPath = PropertiesReader.getInstance().getProperty(
				ConstantsDef.ffmpegPath);
		ffmpeg2theoraPath = PropertiesReader.getInstance().getProperty(
				ConstantsDef.ffmpegPath);
		mencoderPath = PropertiesReader.getInstance().getProperty(
				ConstantsDef.mencoderPath);
		mp4BoxPath = PropertiesReader.getInstance().getProperty(
				ConstantsDef.mp4BoxPath);
		// processFfmpegCodeFormat(); //默认打印出系统支持的视频编码
		logger.debug("=====ffmpegPath===" + ffmpegPath);
		logger.debug("=====mp4BoxPath===" + mp4BoxPath);
	}

	

	/**
	 * 在线程池中执行,每个执行线程是单线程，整个以队列的形式呈现
	 * 
	 * @param srcVideoPath
	 *            要转换的视频路径 如：D:/byyxproject/test/video/1.3gp
	 * @param tarVideoPath
	 *            转换后的视频路径 如：D:/byyxproject/test/video-new/1.mp4
	 * 
	 */
	public static void threadPoolToFlv(String srcVideoPath, String tarVideoPath) throws DoFileException{
		if (!DoFileUtils.exitFile(srcVideoPath)) {
			throw new DoFileException("源文件路径不存在，请检查。【 " + srcVideoPath +"】");
		}
		if (StringUtils.isNotBlank(srcVideoPath)
				&& StringUtils.isNotBlank(tarVideoPath)) {
			ConverThreadPool.getThreadPoolInstance().execute(
					new ConverThread(srcVideoPath, tarVideoPath)); // 启动多线程
		}
	}

	/**
	 * @throws InterruptedException 
	 * @throws IOException 
	 * 
	 * @Title: processFLV
	 * @Description: 转FLV格式 ffmpeg能解析的格式：（asx，asf，mpg，wmv，3gp，mp4，mov，avi，flv等）
	 * @param
	 * @return boolean
	 * @throws
	 */
	public static boolean processFfmpegToFLV(String srcVideoPath, String tarVideoPath) throws DoFileException{
		boolean b = true;
		try {
			//如果文件存在，且大于0kb，则不再转换
			if (DoFileUtils.exitFile(tarVideoPath)) {
				if(new File(tarVideoPath).length() > 0){
					logger.info("文件已经存在,不需要转换!");
					return true;
				}
			}
			StringBuffer cmd = new StringBuffer();
			cmd.append(ffmpegPath);
			cmd.append(" -y");
			cmd.append(" -i");
			cmd.append(" \"" + srcVideoPath + "\"");
			cmd.append(" -ab");
			cmd.append(" 64");
			// cmd.append("-sameq");
//			cmd.append(" -acodec");
//			cmd.append(" mp3");
			// cmd.append("-vcodec");
			// cmd.append("xvid");
			cmd.append(" -ac");
			cmd.append(" 2");
			cmd.append(" -ar");
			cmd.append(" 22050");
			cmd.append(" -qscale");
			cmd.append(" 6"); // 压缩大小
			// cmd.append("-b");
			// cmd.append("1500");
			cmd.append(" -r");
			cmd.append(" 24");
			cmd.append(" \"" + tarVideoPath + "\"");
			//执行转换
			DoFileUtils.exeShell(cmd.toString());
			if (!DoFileUtils.exitFile(tarVideoPath)) {
				b = false;
				throw new DoFileException("源文件存在，转换失败｛可能版本较低、或者ffmpeg不存在、转换器无法支持该编码、视频本身有问题｝");
			}
		} catch (Exception e) {
			e.printStackTrace();
			b= false;
		}
		return b;
	}
	
	/**
	 * @Title: processFfmpegToMp3
	 * @Description: ffmpeg将其他格式音频文件转换成mp3格式文件 ffmpeg能解析的格式：（mp3,wav,ape,ac3,ogg,wma等）
	 * @param srcAudioPath 音频文件(原)
	 * @param tarAudioPath 音频文件(新)
	 * @return
	 */
	public static boolean processFfmpegToMp3(String srcAudioPath,String tarAudioPath) throws DoFileException{
		if (!DoFileUtils.exitFile(srcAudioPath)) {
			throw new DoFileException("源文件路径不存在，请检查。【 " + srcAudioPath +"】");
		}
		String cmd = ffmpegPath + " -y -i \"" + srcAudioPath +"\" -ab 64 -ar 11025 \"" + tarAudioPath + "\"";
		//执行转换
		DoFileUtils.exeShell(cmd.toString());
		if (!DoFileUtils.exitFile(tarAudioPath)) {
			throw new DoFileException("源文件存在，转换失败｛可能版本较低、或者ffmpeg不存在、转换器无法支持该编码、视频本身有问题｝");
		}else{
			return true;
		}
	}
	
	/**
	 * 根据水印文字---添加视频水印
	 * 
	 * @param srcPath
	 *            原视频路径
	 * @param tarVideoPath
	 *            生成后的视频路径
	 * @param wmPosition
	 *            水印位子
	 * @param alpha
	 *            透明度
	 * @param txt
	 *            String 文字
	 * @param fontSize
	 *            每个字的宽度和高度是一样的
	 * @param fontColor
	 *            Color 字体颜色
	 * @param isBold
	 *            字体是否加粗
	 * @param fontType
	 *            字体样式
	 * @param fontPath
	 *            字体文件
	 * @return
	 * @throws URISyntaxException 
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	public  static boolean processFfmpegWatermkByFont(
			String srcPath, String tarVideoPath, int wmPosition, String alpha,
			String txt, int fontSize, Color fontColor, boolean isBold,
			String fontType, String fontPath) throws DoFileException, URISyntaxException {
		if (!DoFileUtils.exitFile(srcPath)) {
			throw new DoFileException("源文件路径不存在，请检查。【 " + srcPath +"】");
		}
		// 通过文字生成的图片临时路径
		String waterMarkPath = WebAppUtils.getWebRootBaseDir("fileTemp") + TXTIMGPATH
				+ System.nanoTime() + ".png";
		// 通过文字生成透明图片
		ImageUtils.createJpgByFont(txt, fontSize, fontColor, isBold, fontType,
				fontPath, waterMarkPath);
		// vfilters ---通过水印图片添加水印视频
		boolean boo = processFfmpegWatermarkByImg(srcPath, tarVideoPath,waterMarkPath, wmPosition, alpha);
		if (boo) {
			logger.info("【" + srcPath + "】 视频添加文字水印图片成功! ");
			return true;
		} else {
			logger.error("【" + srcPath + "】 processFfmpegWatermkByFont  视频添加文字水印图片失败! ");
			return false;
		}

	}

	/**
	 * 根据水印图片---添加视频水印
	 * 
	 * @param srcPath
	 *            原视频路径
	 * @param tarVideoPath
	 *            生成后的视频路径
	 * @param waterMarkPath
	 *            水印图片路径
	 * @param wmPosition
	 *            水印位子
	 * @param alpha
	 *            透明度
	 * @return
	 * @throws URISyntaxException 
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public  static boolean processFfmpegWatermarkByImg(
			String srcPath, String tarVideoPath, String waterMarkPath,
			int wmPosition, String alpha) throws DoFileException, URISyntaxException{
		boolean b = true;
		if (!DoFileUtils.exitFile(srcPath)) {
			b = false;
			throw new DoFileException("源文件路径不存在，请检查。【 " + srcPath +"】");
		}
		if (!DoFileUtils.exitFile(waterMarkPath)) {
			b = false;
			throw new DoFileException("水印图片不存在，请检查。【 " + waterMarkPath +"】");
		}
		// 如果父目录不存在就创建一个
		tarVideoPath = DoFileUtils.replaceFliePathStr(tarVideoPath);
		DoFileUtils.mkdir(tarVideoPath);
		// 缩放图片
		//zooImage(srcPath, waterMarkPath);
		String extendTarName = DoFileUtils.getFileNameNoEx(tarVideoPath);
		String os = System.getProperty("os.name");
		
		//水印图片路径处理
		String picPathStr = waterMarkPath.replaceAll("\\\\", "\\/");
		String picPath = picPathStr.substring(0,picPathStr.lastIndexOf("/"));
		waterMarkPath = picPathStr.substring(picPathStr.lastIndexOf("/") + 1,picPathStr.length());
		
		//shell路径
		String shellPath = "";
		URL url = null;
		if (os != null && os.toLowerCase().startsWith("windows")) {
			// 要执行的shell脚本路径
			url = ConverUtils.class.getResource("../tool/coverVideo.bat"); 
		} else {
			// 要执行的shell脚本路径
			url = ConverUtils.class.getResource("../tool/coverVideo.sh"); 
			
		}
		
		File file = new File(url.toURI());
        shellPath = file.getAbsolutePath();
		if (!DoFileUtils.exitFile(shellPath)) {
			logger.error("【" + shellPath + "】 shell脚本路径 不存在 !");
		}
		//CMD命令
		String cmd = shellPath + " " + picPath + " " + ffmpegPath + " " + srcPath + " "
				+ waterMarkPath + " " + getVideoPosition(wmPosition) + " "
				+ tarVideoPath + " ";
		
		if(StringUtils.isNotBlank(cmd)){
			//执行转换
			DoFileUtils.exeShellWithParams(cmd);
			// 转换mate信息
			if ("MP4".equals(extendTarName.toUpperCase())) {
				return execMp4Box(tarVideoPath);
			}
			if (!DoFileUtils.exitFile(tarVideoPath)) {
				throw new DoFileException("视频添加水印不成功 !");
			}
		}
		return b;
	}
	
	
	/**
	 * ffmpeg 截图，并指定图片的大小
	 * 
	 * @param srcVideoPath
	 * @param tarImagePath
	 *            截取后图片路径
	 * @param width
	 *            截图的宽
	 * @param hight
	 *            截图的高
	 * @param offsetValue
	 *            表示相对于文件开始处的时间偏移值 可以是分秒
	 * @param vframes
	 *            表示截图的桢数
	 * 
	 * @return
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static boolean processFfmpegImage(String srcVideoPath,
			String tarImagePath, int width, int hight, float offsetValue,
			float vframes) throws DoFileException {
		if (!DoFileUtils.exitFile(srcVideoPath)) {
			throw new DoFileException("源文件路径不存在，请检查。【 " + srcVideoPath +"】");
		}
		
		StringBuffer commend = new StringBuffer();

		commend.append(ffmpegPath);

		commend.append(" -i");

		commend.append(" \"" + srcVideoPath + "\"");

		commend.append(" -y");

		commend.append(" -f");

		commend.append(" image2");

		commend.append(" -ss");
		
		commend.append(" " + offsetValue); // 在视频的某个插入时间截图，例子为5秒后

		commend.append(" -vframes");

		commend.append(" " + vframes + ""); // 截图的桢数

		commend.append(" -s");

		commend.append(" " + width + "x" + hight); // 截图的的大小

		commend.append(" \"" + tarImagePath + "\"");
		
		//执行转换
		DoFileUtils.exeShell(commend.toString());
		if (!DoFileUtils.exitFile(tarImagePath)) {
			throw new DoFileException("截图失败.");
		}
		return true;
	}

	
	/**
	 * ffmpeg按照时间段进行截取
	 * 
	 * @param srcVideoPath
	 *            视频文件(原)
	 * @param tarImgPath
	 *            图片文件Path
	 * @return
	 */
	public static boolean processFfmpegImageBySureTime(String srcVideoPath,
			String tarImgPath){
		DoFileUtils.mkdir(tarImgPath);
		boolean b = true;
		try {
			
			StringBuffer commend = new StringBuffer();
			
			commend.append(ffmpegPath);
			
			commend.append(" -y"); // 覆盖

			commend.append(" -i");

			commend.append(" \"" + srcVideoPath + "\"");
			
			commend.append(" -f");

			commend.append(" image2");
			
//			commend.append("  -vcodec mjpeg");

			commend.append(" -ss");

			commend.append(" " + STARTTIME);

			commend.append(" -t");

			commend.append(" " + ENDTIME);
			
//			commend.append(" -qscale");
//			
//			commend.append(" 0.01"); // 压缩大小
			
//			commend.append(" -vframes 1");

//			commend.append("-s"); //指定图片大小

//			commend.append("595x824");

			commend.append(" \"" + tarImgPath + "\"");
			//执行转换
			DoFileUtils.exeShell(commend.toString());
			if (!DoFileUtils.exitFile(tarImgPath)) {
				b = false;
			}
			Thread.sleep(1000);
		} catch (Exception e) {
			b = false;
			e.printStackTrace();
		}
		return b;
	}
	
	
	

	/**
	 * Mp4Box 转换MP4
	 * 
	 * @param srcPath
	 *            源MP4路径
	 * @return
	 */
	public static boolean execMp4Box(String srcPath) {
		if (!DoFileUtils.exitFile(srcPath)) {
			throw new DoFileException("源文件路径不存在，请检查。【 " + srcPath +"】");
		}
		String cmd = mp4BoxPath + " -isma \"" + srcPath + "\"";
		//执行转换
		DoFileUtils.exeShell(cmd);
		if (!DoFileUtils.exitFile(srcPath)) {
			logger.error("【" + srcPath + "】  转换MP4 metadata 不成功 !");
			return false;
		}
		return true;
	}


	// 等待线程处理完成
	public static void doWaitFor(Process p,String srcVideoPath) throws IOException, InterruptedException {
		String errorMsg = readInputStream(p.getErrorStream(), "error",srcVideoPath);
		String outputMsg = readInputStream(p.getInputStream(), "out",srcVideoPath);
		int c = p.waitFor();
		if (c != 0) {// 如果处理进程在等待
			System.out.println("处理失败：" + errorMsg);
		} else {
			System.out.println(COMPLETE + outputMsg);
		}
	}

	

	/**
	 * ffmpeg按照时间段进行截取 ffmpeg能解析的格式：（asx，asf，mpg，wmv，3gp，mp4，mov，avi，flv等）
	 * 
	 * @param srcVideoPath
	 *            视频文件(原)
	 * @param tarVideoPath
	 *            视频文件(新)
	 * @param startTime
	 *            开始时间 形如：00:12:20
	 * @param endTime
	 *            结束时间 形如：01:20:10
	 * @return
	 */
	public static boolean processFfmpegVideoByTime(String srcVideoPath,
			String tarVideoPath, String startTime, String endTime) {
		if (!DoFileUtils.exitFile(srcVideoPath)) {
			throw new DoFileException("源文件路径不存在，请检查。【 " + srcVideoPath +"】");
		}

		List<String> commend = new java.util.ArrayList<String>();

		commend.add(ffmpegPath);

		commend.add("-ss");

		if (startTime != null && !startTime.equals("")) {
			commend.add(startTime);
		} else {
			commend.add(STARTTIME);
		}

		commend.add("-t");

		if (endTime != null && !endTime.equals("")) {
			if (!DateTools.calTime(DateTools.getSplitStr(startTime),
					DateTools.getSplitStr(endTime)).equals("")) {
				commend.add(DateTools.calTime(
						DateTools.getSplitStr(startTime),
						DateTools.getSplitStr(endTime)));
			} else {
				return false;
			}
		} else {
			commend.add(ENDTIME);
		}

		commend.add("-y"); // 覆盖

		commend.add("-i");

		commend.add(srcVideoPath);

		commend.add(tarVideoPath);

		try {
			ProcessBuilder builder = new ProcessBuilder();
			builder.command(commend);
			Process process = builder.start();
			doWaitFor(process,srcVideoPath);
			process.destroy();
			if (!DoFileUtils.exitFile(tarVideoPath)) {
				logger.error("【" + tarVideoPath
						+ "】processFfmpegByTime  截图不成功 !");
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.error("【" + srcVideoPath + "】 processFfmpegByTime  截图不成功 !");
			return false;
		}
	}

	/**
	 * ffmpeg转换成swf文件
	 * 
	 * @param srcVideoPath
	 * @param tarVideoPath
	 * @return
	 */
	public static boolean processFfmpegSwf(String srcVideoPath,
			String tarVideoPath) {
		if (!DoFileUtils.exitFile(srcVideoPath)) {
			throw new DoFileException("源文件路径不存在，请检查。【 " + srcVideoPath +"】");
		}
		List<String> commend = new java.util.ArrayList<String>();

		commend.add(ffmpegPath);

		commend.add("-y");

		commend.add("-i");

		commend.add(srcVideoPath);

		commend.add("-b");

		commend.add("360");

		commend.add("-r");

		commend.add("25");

		commend.add("-s");

		commend.add("640x480");

		commend.add("-ab");

		commend.add("56");

		commend.add("-ar");

		commend.add("22050");

		commend.add("-ac");

		commend.add("1");

		commend.add(tarVideoPath);

		try {
			ProcessBuilder builder = new ProcessBuilder();
			builder.command(commend);
			Process process = builder.start();
			doWaitFor(process,srcVideoPath);
			process.destroy();
			if (!DoFileUtils.exitFile(tarVideoPath)) {
				logger.error("【" + tarVideoPath + "】 processFfmpegSwf  转换不成功 !");
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.error("【" + srcVideoPath + "】 processFfmpegSwf  转换不成功 !");
			return false;
		}
	}

	


	
	/**
	 * ffmpeg将其他格式转换成WEBM格式文件 ffmpeg能解析的格式：（asx，asf，mpg，wmv，3gp，mp4，avi，flv等）
	 * 
	 * @param srcVideoPath
	 *            视频文件(原)
	 * @param tarVideoPath
	 *            视频文件(新)
	 * @return
	 */
	public static boolean processFfmpegToWebm(String srcVideoPath,
			String tarVideoPath) {
		if (!DoFileUtils.exitFile(srcVideoPath)) {
			throw new DoFileException("源文件路径不存在，请检查。【 " + srcVideoPath +"】");
		}

		List<String> commend = new java.util.ArrayList<String>();

		commend.add(ffmpegPath);

		commend.add("-y");

		commend.add("-i");

		commend.add(srcVideoPath);
		commend.add("-f");
		commend.add("webm");
		commend.add("-vcodec");
		commend.add("libvpx");
		commend.add("-acodec");
		commend.add("libvorbis");
		commend.add("-vb");
		commend.add("1600000");
		commend.add(tarVideoPath);

		try {
			ProcessBuilder builder = new ProcessBuilder();
			builder.command(commend);
			Process process = builder.start();
			doWaitFor(process,srcVideoPath);
			process.destroy();
			if (!DoFileUtils.exitFile(tarVideoPath)) {
				logger.info(tarVideoPath
						+ " is not exit! processFfmpegToWebm 转换不成功 !");
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.error("【" + srcVideoPath + "】processFfmpegToWebm 转换不成功 !");
			return false;
		}
	}

	/**
	 * ffmpeg将其他格式转换成WEBM格式文件 ffmpeg能解析的格式：（asx，asf，mpg，wmv，3gp，mp4，avi，flv等）
	 * 
	 * @param srcVideoPath
	 *            视频文件(原)
	 * @param tarVideoPath
	 *            视频文件(新)
	 * @return
	 */
	public static boolean processFfmpegToOgv(String srcVideoPath,
			String tarVideoPath) {
		if (!DoFileUtils.exitFile(srcVideoPath)) {
			throw new DoFileException("源文件路径不存在，请检查。【 " + srcVideoPath +"】");
		}

		List<String> commend = new java.util.ArrayList<String>();

		commend.add(ffmpeg2theoraPath);

		commend.add("-V");

		commend.add("4000");
		commend.add("-A");
		commend.add("128");
		commend.add(srcVideoPath);
		commend.add("-o");
		commend.add(tarVideoPath);

		try {
			ProcessBuilder builder = new ProcessBuilder();
			builder.command(commend);
			Process process = builder.start();
			doWaitFor(process,srcVideoPath);
			process.destroy();
			if (!DoFileUtils.exitFile(tarVideoPath)) {
				logger.info(tarVideoPath
						+ " is not exit! processFfmpegToOggOrOgv 转换不成功 !");
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.error("【" + srcVideoPath
					+ "】processFfmpegToOggOrOgv 转换不成功 !");
			return false;
		}
	}

	/**
	 * ffmpeg将其他音频格式转换成ogg格式文件 ffmpeg能解析的格式：（aac;ac3;au;wav;wma等）
	 * 
	 * @param srcVideoPath
	 *            音频文件(原)
	 * @param tarVideoPath
	 *            音频文件(新)
	 * @return
	 */
	public static boolean processFfmpegToOgg(String srcVideoPath,
			String tarVideoPath) {
		if (!DoFileUtils.exitFile(srcVideoPath)) {
			throw new DoFileException("源文件路径不存在，请检查。【 " + srcVideoPath +"】");
		}

		List<String> commend = new java.util.ArrayList<String>();

		commend.add(ffmpegPath);

		commend.add("-i");

		commend.add(srcVideoPath);
		commend.add("-acodec");
		commend.add("libvorbis");
		commend.add("-ab");
		commend.add("64k");
		commend.add(tarVideoPath);

		try {
			ProcessBuilder builder = new ProcessBuilder();
			builder.command(commend);
			Process process = builder.start();
			doWaitFor(process,srcVideoPath);
			process.destroy();
			if (!DoFileUtils.exitFile(tarVideoPath)) {
				logger.info(tarVideoPath
						+ " is not exit! processFfmpegToOggOrOgv 转换不成功 !");
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.error("【" + srcVideoPath
					+ "】processFfmpegToOggOrOgv 转换不成功 !");
			return false;
		}
	}
	
	
	/**
	 * 根据方位数字获取值
	 * 0:左上角 1:右上角 2:左下角 3:右下角 4:底部居中
	 * @param wmPosition
	 *            Top left corner ffmpeg –i inputvideo.avi -vf
	 *            "movie=watermarklogo.png [watermark]; [in][watermark] overlay=10:10 [out]"
	 *            outputvideo.flv Top right corner ffmpeg –i inputvideo.avi -vf
	 *            "movie=watermarklogo.png [watermark]; [in][watermark] overlay=main_w-overlay_w-10:10 [out]"
	 *            outputvideo.flv Bottom left corner ffmpeg –i inputvideo.avi
	 *            -vf
	 *            "movie=watermarklogo.png [watermark]; [in][watermark] overlay=10:main_h-overlay_h-10 [out]"
	 *            outputvideo.flv Bottom right corner ffmpeg –i inputvideo.avi
	 *            -vf
	 *            "movie=watermarklogo.png [watermark]; [in][watermark] overlay=main_w-overlay_w-10:main_h-overlay_h-10 [out]"
	 *            outputvideo.flv
	 * @return
	 */
	public static String getVideoPosition(int wmPosition) {
		String positionStr = "main_w-overlay_w-10:main_h-overlay_h-10"; // 默认右上角
		switch (wmPosition) {
		case 0:
			positionStr = "10:10"; // 左上角
			break;
		case 1:
			positionStr = "main_w-overlay_w-10:10"; // 右上角
			break;
		case 2:
			positionStr = "10:main_h-overlay_h-10"; // 左下角
			break;
		case 3:
			positionStr = "main_w-overlay_w-10:main_h-overlay_h-10"; // 右下角
			break;
		case 4:
			positionStr = "(main_w-overlay_w)/2:main_h-overlay_h:1"; // 底部居中
			break;
		default:
			break;
		}
		return positionStr;
	}

	/**
	 * 缩放图片
	 * 
	 * @param srcPath
	 *            视频路径
	 * @param wmImgPath
	 *            水印图片路径
	 * @return
	 */
	public static void zooImage(String srcPath, String wmImgPath) {
		if (!DoFileUtils.exitFile(srcPath)) {
			throw new DoFileException("源文件路径不存在，请检查。【 " + srcPath +"】");
		}
		// 获取视频信息
		Multimedia videoInfo = (Multimedia) FileMetadataFactory
				.getMetadata(srcPath);
		int width = 0; // 视频宽
		int height = 0; // 视频高
		if (videoInfo != null) {
			String widthStr = videoInfo.getWidth(); // 宽
			if (StringUtils.isNotEmpty(widthStr)) {
				width = Integer.parseInt(widthStr);
			}
			String heightStr = videoInfo.getHeight(); // 高
			if (StringUtils.isNotEmpty(heightStr)) {
				height = Integer.parseInt(heightStr);
			}
		}

		if (width != 0 || height != 0) { // 如果获取到视频宽高
			// 进行图片缩放
			ImgCoverUtil.resizeImage(wmImgPath, wmImgPath, width, height);
		}
	}

	/**
	 * ffmpeg通过Avs文件添加视频水印
	 * 
	 * @param avsPath
	 *            avs文件路径
	 * @param tarVideoPath
	 *            视频文件(新)
	 * @return
	 */
	public static boolean processFfmpegWmByAvs(String srcAvsPath,
			String tarVideoPath) {
		if (!DoFileUtils.exitFile(srcAvsPath)) {
			throw new DoFileException("源文件路径不存在，请检查。【 " + srcAvsPath +"】");
		}

		// 如果父目录不存在就创建一个
		tarVideoPath = DoFileUtils.replaceFliePathStr(tarVideoPath);
		DoFileUtils.mkdir(tarVideoPath.substring(0,
				tarVideoPath.lastIndexOf("/")));

		List<String> commend = new java.util.ArrayList<String>();

		commend.add(ffmpegPath);

		commend.add("-y");

		commend.add("-i");

		commend.add(srcAvsPath);

		commend.add(tarVideoPath);

		try {
			ProcessBuilder builder = new ProcessBuilder();
			builder.command(commend);
			Process process = builder.start();
			doWaitFor(process,tarVideoPath);
			process.destroy();
			if (!DoFileUtils.exitFile(tarVideoPath)) {
				logger.error("【" + srcAvsPath
						+ "】 processFfmpegWmByAvs 视频添加水印不成功 !");
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.error("【" + srcAvsPath
					+ "】processFfmpegWmByAvs  视频添加水印不成功 !");
			return false;
		}
	}

	/**
	 * 生成Avs文件
	 * 
	 * @param srcPath
	 *            视频路径
	 * @param tarAvsPath
	 * @param waterMarkPath
	 *            水印图片路径
	 * @param x
	 * @param y
	 * @param alpha
	 * @return
	 */
	public static boolean createAvsFile(String srcPath, String tarAvsPath,
			String waterMarkPath, int x, int y, float alpha) {
		if (!DoFileUtils.exitFile(srcPath)) {
			throw new DoFileException("源文件路径不存在，请检查。【 " + srcPath +"】");
		}

		if (!DoFileUtils.exitFile(waterMarkPath)) {
			throw new DoFileException("【" + waterMarkPath + "】  水印图片不存在 !");
		}

		PrintWriter out = null;
		try {
			// 这里设置avs文件的存放路径以及文件名
			try {
				StringBuffer n = new StringBuffer();
				n.append("video").append("=").append(" DirectShowSource ")
						.append("(\"").append(srcPath).append("\")")
						.append("\n");
				n.append("logo").append("=").append(" ImageSource ")
						.append("(\"").append(waterMarkPath).append("\")")
						.append("\n");
				n.append("logomask").append("=").append(" ImageSource ")
						.append("(\"").append(waterMarkPath).append("\")")
						.append("\n");
				// overlay参数
				n.append("overlay");
				n.append("(");
				n.append("video").append(","); // video
				n.append("logo").append(","); // logo
				n.append("mask=logomask").append(","); // mask
				n.append("y=").append(y).append(","); // 水印的纵坐标 y
				n.append("x=").append(x).append(","); // 水印的水平坐标 x
				n.append("mode=\"blend\"").append(","); // 水印模式：opacity
				n.append("opacity=").append(alpha); // 水印透明度
				n.append(")");
				n.append("\n");
				out = new PrintWriter(new File(tarAvsPath));
				out.print(n.toString());
				return true;
			} catch (FileNotFoundException e) {
				logger.error("创建【" + srcPath + "】createAvsFile  AVS文件不成功 !");
				return false;
			}
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}



	/**
	 * 获取x，y坐标值，返回String数组
	 * 
	 * @param wmPosition
	 *            0:左上角 1:右上角 2:左下角 3:右下角 4:居中
	 * @param srcPath
	 *            视频路径
	 * @param wmImgPath
	 *            水印图片路径
	 * @return
	 */
	public static int[] getXy(int wmPosition, String srcPath, String wmImgPath) {

		int x = 10, y = 10; // 定义默认为左上角

		int[] xy = new int[2];

		// 获取视频信息
		Multimedia videoInfo = (Multimedia) FileMetadataFactory
				.getMetadata(srcPath);
		int width = 0; // 视频宽
		int height = 0; // 视频高
		if (videoInfo != null) {
			String widthStr = videoInfo.getWidth(); // 宽
			if (StringUtils.isNotEmpty(widthStr))
				width = Integer.parseInt(widthStr);
			String heightStr = videoInfo.getHeight(); // 高
			if (StringUtils.isNotEmpty(heightStr))
				height = Integer.parseInt(heightStr);
		}

		if (width != 0 || height != 0) { // 如果获取到视频宽高

			// 取得坐标方位编码
			int position = DoFileUtils.getWmDegree(wmPosition);

			int wideth_biao = 0; // 水印图片的宽
			int height_biao = 0; // 水印图片的高
			try {
				// 首先进行图片缩放
				ImageUtils.zoomPerImg(wmImgPath, wmImgPath, width, height);
				// 水印文件
				Image src_biao = ImageIO.read(new File(wmImgPath));
				wideth_biao = src_biao.getWidth(null);
				height_biao = src_biao.getHeight(null);
			} catch (IOException e) {
				logger.error("【" + wmImgPath + "】getXy  读取水印图片失败 !");
			}

			// 左上角距离边距10像素
			int leftUpWideth = 10;
			int leftUpHeight = 10;

			// 右上角距离边距10像素
			int rightUpWideth = (width - wideth_biao) - 10;
			int rightUpHeight = 10;

			// 左下角距离边距10像素
			int leftDownWideth = 10;
			int leftDownHeight = (height - height_biao) - 10;

			// 右下角距离边距10像素
			int rightDownWideth = (width - wideth_biao) - 10;
			int rightDownHeight = (height - height_biao) - 10;

			// 居中
			int centerWideth = (width - wideth_biao) / 2;
			int centerHeight = (height - height_biao) / 2;

			switch (position) {
			case 0:
				x = leftUpWideth;
				y = leftUpHeight;
				break;
			case 1:
				x = rightUpWideth;
				y = rightUpHeight;
				break;
			case 2:
				x = leftDownWideth;
				y = leftDownHeight;
				break;
			case 3:
				x = rightDownWideth;
				y = rightDownHeight;
				break;
			case 4:
				x = centerWideth;
				y = centerHeight;
				break;
			default:
				break;
			}
		}

		// 设值
		xy[0] = x;
		xy[1] = y;

		return xy;
	}

	
	/**
	 * 
	 * @Title: readInputStream
	 * @Description: 完成进度百分比
	 * @param
	 * @return String
	 * @throws
	 */
	private static String readInputStream(InputStream is, String f,String srcPath)
			throws IOException {
		// 将进程的输出流封装成缓冲读者对象
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		StringBuffer lines = new StringBuffer();// 构造一个可变字符串
		long totalTime = 0;

		// 对缓冲读者对象进行每行循环
		for (String line = br.readLine(); line != null; line = br.readLine()) {
			lines.append(line);// 将每行信息字符串添加到可变字符串中
			int positionDuration = line.indexOf("Duration:");// 在当前行中找到第一个"Duration:"的位置
			int positionTime = line.indexOf("time=");
			if (positionDuration > 0) {// 如果当前行中有"Duration:"
				String dur = line.replace("Duration:", "");// 将当前行中"Duration:"替换为""
				dur = dur.trim().substring(0, 8);// 将替换后的字符串去掉首尾空格后截取前8个字符
				int h = Integer.parseInt(dur.substring(0, 2));// 封装成小时
				int m = Integer.parseInt(dur.substring(3, 5));// 封装成分钟
				int s = Integer.parseInt(dur.substring(6, 8));// 封装成秒
				totalTime = h * 3600 + m * 60 + s;// 得到总共的时间秒数
			}
			if (positionTime > 0) {// 如果所用时间字符串存在
				// 截取包含time=的当前所用时间字符串
				String time = line.substring(positionTime,
						line.indexOf("bitrate") - 1);
				time = time.substring(time.indexOf("=") + 1, time.indexOf("."));// 截取当前所用时间字符串
				int h = Integer.parseInt(time.substring(0, 2));// 封装成小时
				int m = Integer.parseInt(time.substring(3, 5));// 封装成分钟
				int s = Integer.parseInt(time.substring(6, 8));// 封装成秒
				long hasTime = h * 3600 + m * 60 + s;// 得到总共的时间秒数
				float t = (float) hasTime / (float) totalTime;// 计算所用时间与总共需要时间的比例
				COMPLETE = (int) Math.ceil(t * 100);// 计算完成进度百分比
			}
			if(StringUtils.isNotBlank(srcPath)){
				System.out.println("【" + srcPath + "】 完成：" + COMPLETE + "%");
			}else{
				System.out.println(" 完成：" + COMPLETE + "%");
			}
			
		}
		br.close();// 关闭进程的输出流
		return lines.toString();
	}

	
	private static void dealWith(final Process pro){      
        // 下面是处理堵塞的情况       
        try {      
            new Thread(){      
                public void run(){      
                    BufferedReader br1 = new BufferedReader(new InputStreamReader(pro.getInputStream()));      
                    String text;      
                    try {      
                        while ( (text = br1.readLine()) != null) {      
                            System.out.println(text);      
                        }      
                    } catch (IOException e) {      
                        e.printStackTrace();      
                    }      
                }      
            }.start();      
        } catch (Exception e) {      
            e.printStackTrace();      
        }      
              
        try {      
            new Thread(){      
                public void run(){      
                    BufferedReader br2 = new BufferedReader(new InputStreamReader(pro.getErrorStream()));//这定不要忘记处理出理时产生的信息，不然会堵塞不前的       
                    String text;      
                    try {      
                        while( (text = br2.readLine()) != null){      
                            System.err.println(text);      
                        }      
                    } catch (IOException e) {      
                        e.printStackTrace();      
                    }      
                }      
            }.start();      
        } catch (Exception e) {      
            e.printStackTrace();      
        }      
    } 
	
	
	 /**
     * 等待进程处理
     * @param p
     * @return
     */
	@SuppressWarnings("unused")
	public static int doWaitFor(Process p) {  
        InputStream in = null;  
        InputStream err = null;  
        int exitValue = -1; // returned to caller when p is finished  
        try {  
            in = p.getInputStream();  
            err = p.getErrorStream();  
            boolean finished = false; // Set to true when p is finished  
            while (!finished) {  
                try {  
                    while (in.available() > 0) {  
                        Character c = new Character((char) in.read());  
                    }  
                    while (err.available() > 0) {  
                        Character c = new Character((char) err.read());  
                    }  
                    exitValue = p.exitValue();  
                    finished = true;  
                } catch (IllegalThreadStateException e) {  
                    Thread.currentThread();
					Thread.sleep(500);  
                }  
            }  

        } catch (Exception e) {  
        	logger.error("doWaitFor();: unexpected exception - " 
                    + e.getMessage());
        } finally {  
            try {  
                if (in != null) {  
                    in.close();  
                }  
            } catch (IOException e) {  
                logger.error("等待进程处理错误");
            }  
            if (err != null) {  
                try {  
                    err.close();  
                } catch (IOException e) {  
                	logger.error("等待进程处理错误");  
                }  
            }  
        }  
        return exitValue;  
    }  
	
	

	// test
	public static void main(String[] args) {
		// 测试开始
		long ss = DateTools.getStartTime();
		// VideoConverHelps videoConverHelps = new VideoConverHelps();
		// videoConverHelps.processFfmpegToWebm("D:/video/video/1.rm",
		// "E:/13.webm");
		// videoConverHelps.processFfmpegToOggOrOgv("D:/video/video/1.rm",
		// "E:/14.ogv");
		// VideoConverHelps.processFfmpegToOgg("D:/video/mp3BY2.mp3",
		// "E:/15.ogg");
		// System.out.println(VideoInfoHelps.getVideoInfo(
		// "E:/Workspaces/BSRCM_20120802/WebRoot/oresfile/0/30.mp4").getCodeFormat());
		// System.out.println(VideoInfoHelps.getVideoInfo( "D:/video/5.mp4"));
		// System.out.println(VideoInfoHelps.getVideoInfo( "D:/video/3.mp4"));
		// processFfmpegToFLV("D:/Project素材/video/1.avi", "D:/Project素材/video/1.flv");
		// 使用线程池转换
		// for (int i = 1; i <= 4; i++) {
//		threadPoolToFlv("D:/Project素材/video/1.avi", "D:/Project素材/video/1.flv");
//		threadPoolToFlv("D:/Project素材/video/1.rmvb", "D:/Project素材/video/2.flv");
//		threadPoolToFlv("D:/Project素材/video/4.mp4", "D:/Project素材/video/3.flv");
		// }
//		 processFfmpegOther("D:/Project素材/video/闪电十一人_07.rmvb",
//		 "D:/Project素材/video/1.flv");
//		 try {
//			processFfmpegWatermarkByImg(
//			 "D:/Project素材/video/1.mov",
//			 "D:/Project素材/video/1.flv",
//			 "D:/Project素材/video/watermark.png",
//			 03, //水印位置
//			 Float.parseFloat(DoFileUtils.getAlpha(01))//透明度
//			 );
//		} catch (NumberFormatException e) {
//			e.printStackTrace();
//		} catch (URISyntaxException e) {
//			e.printStackTrace();
//		}
		// processFfmpegBySureTime("D:/conver/9.mp4","D:/conver/555.jpg");

		 
		
//		processFfmpegImageBySureTime("D:/《静夜思》朗读.asf", "D:/1-1-22.png");
		//processFfmpegSyntheticImage("D:/Project素材/video/1.mp4", "D:/Project素材/video/image%d.jpg", "D:/Project素材/video/11.mp4");
		try {
			processFfmpegWatermkByFont(
					 "F:/Project素材/video/1.asf",
					 "F:/Project素材/video/2.asf",
					 01, //水印位置
					 DoFileUtils.getAlpha(01),//透明度
					 "大佳网com",
					 20,
					 Color.RED,
					 true,
					 "宋体",
					 ""
					 );
//			//boolean b = processFfmpegToFLV("D:/Project素材/video/成都七中育才学校微课——化学方程式配平技巧_（聂荣华）.f4v", "D:D:/Project素材/video/成都七中育才学校微课——化学方程式配平技巧_（聂荣华）.flv");
//			//System.out.println("==========" + b);
////			processFfmpegToMp3("D:/Project素材/audio/ogg格式铃声/Big_Easy.ogg", "D:/Project素材/audio/1-1-1-2.mp3");
		} catch (Exception e) {
			e.printStackTrace();
		}
//		catch (URISyntaxException e) {
//			// tanghui Auto-generated catch block
//			e.printStackTrace();
//		}
		//删除文件
//		try {
//			//DoFileUtils.delDirs("D:\\2222");
//		} catch (IOException e) {
//			// tanghui Auto-generated catch block
//			e.printStackTrace();
//		}
//		try {
////			DoFileUtils.exeCmd("");
//            final Process p = Runtime.getRuntime().exec("cmd /c start /b d:\\1.bat D:\\11\\lib\\");
//            InputStream in = p.getInputStream();
//            BufferedReader br = new BufferedReader(new InputStreamReader(in));
//            String tmp = null;
//            while((tmp=br.readLine())!=null){
//            	System.out.println(tmp);
//            }
//		} catch (IOException e1) {
//	            // TODO 自动生成 catch 块
//	            e1.printStackTrace();
//	    }
		// 测试结束
		DateTools.getTotaltime(ss);
	}

}

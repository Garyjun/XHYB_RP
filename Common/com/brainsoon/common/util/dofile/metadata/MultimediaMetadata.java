package com.brainsoon.common.util.dofile.metadata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.brainsoon.common.util.dofile.metadata.fo.FileObject;
import com.brainsoon.common.util.dofile.metadata.fo.Multimedia;
import com.brainsoon.common.util.dofile.util.ConstantsDef;
import com.brainsoon.common.util.dofile.util.PropertiesReader;

/**
 * 
 * @ClassName: VideoMetadata 
 * @Description:  视频元数据元数据分类
 * @author tanghui 
 * @date 2014-4-17 下午12:37:10 
 *
 */
public class MultimediaMetadata implements IFileMetadata {

	protected static final Logger logger = Logger.getLogger(MultimediaMetadata.class);
	private String fileUrl; //文件绝对路径
	private static final String _BITRATE = "bitrate: ";
	private static final String _START = "start: ";
	private static final String _AUDIO = "Audio: ";
	private static final String _VIDEO = "Video: ";
	private static final String _DURATION = "Duration: ";
	public static final String ffmpegPath; // ffmpeg.exe的目录

	static {
		ffmpegPath = PropertiesReader.getInstance().getProperty(
				ConstantsDef.ffmpegPath);
	}
	
	public MultimediaMetadata(String fileUrl) {
		super();
		this.fileUrl = fileUrl;
		// tanghui Auto-generated constructor stub
	}

	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	@Override
	public FileObject getFileMetadata(){
		return getMetadata(fileUrl);
	}
	
	
	/**
	 * 
	 * @Title: getMetadata
	 * @Description: 获取视频 or 音频 信息
	 * @param
	 * @return Multimedia
	 * @throws
	 */
	public static synchronized Multimedia getMetadata(String fileUrl) {
		Multimedia multimedia = new Multimedia();
		// 获取视频 or 音频 信息
		String result = processVideo(fileUrl);
		if (StringUtils.isNotEmpty(result)) {
			String[] arr = result.split("\n");
			if (arr != null && arr.length > 0) {
				for (String str0 : arr) {
					if (str0.contains(_DURATION) || str0.contains(_START)
							|| str0.contains(_BITRATE)) {
						String[] arr1 = str0.split(",");
						for (String str1 : arr1) {
							String[] arr2 = null;
							String arr2Str1 = "";
							if (str1.contains(_DURATION)) {
								arr2 = str1.split(_DURATION);
								arr2Str1 = (arr2[1] == null) ? "" : arr2[1]
										.trim();
								multimedia.setDuration(arr2Str1); // 播放时间
							} else if (str1.contains(_START)) {
								arr2 = str1.split(_START);
								arr2Str1 = (arr2[1] == null) ? "" : arr2[1]
										.trim();
								multimedia.setPlayingStartTime(arr2Str1); // 开始播放时间
							} else if (str1.contains(_BITRATE)) {
								arr2 = str1.split(_BITRATE);
								arr2Str1 = (arr2[1] == null) ? "" : arr2[1]
										.trim();
								multimedia.setBitrateSize(arr2Str1); // bitrate
																		// 码率 单位
																		// kb
							}
						}
					} else if (str0.contains(_VIDEO)) {
						str0 = str0.substring(_VIDEO.length(), str0.length());
						String[] arr1 = str0.split(",");
						for (int i = 0; i < arr1.length; i++) {
							if (arr1.length >= 3) {
								multimedia.setVideoCodeFormat(arr1[0].trim()); // 视频编码格式
								multimedia.setVideoFormat(arr1[1].trim()); // 视频格式
								String videoSpecification = arr1[2].trim();
								if (StringUtils.isNotBlank(videoSpecification)) {
									multimedia
											.setVideoSpecification(videoSpecification); // 分
																						// 辨
																						// 率
									if (videoSpecification.contains("[")) {
										videoSpecification = videoSpecification
												.substring(
														0,
														videoSpecification
																.lastIndexOf("["));
									}
									String[] wOrh = videoSpecification
											.split("x");
									if(wOrh!=null && wOrh.length==2){
										multimedia.setWidth(wOrh[0].trim()); // 视频宽
										multimedia.setHeight(wOrh[1].trim()); // 视频高
									}
								}

							}
							if (arr1[i].contains("kb/s")) {
								multimedia.setVideoKbps(arr1[i].trim()); // 视频码率
							}
							if (arr1[i].contains("fps")) {
								multimedia.setVideoFps(arr1[i].trim()); // 视频帧率
							}
						}
					} else if (str0.contains(_AUDIO)) {
						str0 = str0.substring(_VIDEO.length(), str0.length());
						String[] arr1 = str0.split(",");
						for (int i = 0; i < arr1.length; i++) {
							if (arr1.length >= 2) {
								multimedia.setAudioCoding(arr1[0].trim()); // 音频编码
								multimedia.setAudioSampling(arr1[1].trim()); // 采样频率(采
																				// 样
																				// 数)
							}
							if (arr1[i].contains("kb/s")) {
								multimedia.setAudioKbps(arr1[i].trim()); // 音频码率
							}
							// 暂时无法获取
							if (arr1[i].contains("mono")
									|| arr1[i].contains("stereo")) {
								multimedia.setAudioChannel(arr1[i].trim()); // 采样声道(数)
							}
						}
					}
				}
			}
			logger.info("获取【" + fileUrl + "】视频信息成功!");
		} else {
			logger.info("执行成功！但未获取到【" + fileUrl + "】视频信息!");
		}
		return multimedia;
	}

	/**
	 * ffmpeg能解析的格式：（asx，asf，mpg，wmv，3gp，mp4，mov，avi，flv等）
	 * 
	 * @param inputPath
	 * @return
	 */
	private synchronized static String processVideo(String filePath) {
		List<String> commend = new java.util.ArrayList<String>();
		commend.add(ffmpegPath);// 可以设置环境变量从而省去这行
		commend.add("-i");
		commend.add(filePath);
		BufferedReader buf = null; // 保存ffmpeg的输出结果流
		try {
			ProcessBuilder builder = new ProcessBuilder();
			builder.command(commend);
			builder.redirectErrorStream(true);
			Process p = builder.start();
			String line = null;
			buf = new BufferedReader(new InputStreamReader(p.getInputStream()));
			StringBuffer sb = new StringBuffer();
			while ((line = buf.readLine()) != null) {
				String lineStr = line.trim();
				if (lineStr.contains(_DURATION) || lineStr.contains(_VIDEO)
						|| lineStr.contains(_AUDIO)) {
					if (lineStr.contains("Stream #0:0: ")) {
						lineStr = lineStr.substring("Stream #0:0: ".length(),
								lineStr.length());
					} else if (lineStr.contains("Stream #0:1:")) {
						lineStr = lineStr.substring("Stream #0:1: ".length(),
								lineStr.length());
					}
					sb.append(lineStr + "\n");
				}
				continue;
			}
			p.waitFor();// 这里线程阻塞，将等待外部转换进程运行成功运行结束后，才往下执行
			return sb.toString();
		} catch (Exception e) {
			logger.error("ffmpeg解析视频文件【" + filePath + "】失败!");
			return null;
		}finally {
			if(buf != null){
				try {
					buf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}// 关闭
			}
		}
	}

}

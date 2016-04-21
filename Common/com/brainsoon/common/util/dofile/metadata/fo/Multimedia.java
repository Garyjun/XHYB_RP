package com.brainsoon.common.util.dofile.metadata.fo;

/**
 * 视频信息fo类
 * @author 唐辉
 *
 */
public class Multimedia extends FileObject{

	private String duration;         	//播放时间---以秒为单位，播放时间依据平台技术要求定
	private String playingStartTime; 	//开始时间
	private String bitrateSize;      	//bitrate 码率 单位 kb
	private String videoSpecification;  //分 辨 率 - 规格---长度与宽度，以像素计，格式如640*480
	private String videoCodeFormat;	 	//视频编码格式
	private String videoFormat;			//视频格式
	private String videoFps;		 	//视频帧率(动画中的帧（frame）数目)
	private String videoKbps;		 	//视频码率
	private String audioCoding;	     	//音频编码
	private String audioChannel;     	//采样声道数
	private String audioKbps;        	//音频码率
	private String audioSampling;    	//采样频率---数字化时的采样频率，以MHZ为单位（依据平台技术要求定）
	private String width; 				//视频宽
	private String height; 				//视频高

	
	public Multimedia() {
		super();
	}


	public String getDuration() {
		return duration;
	}


	public void setDuration(String duration) {
		this.duration = duration;
	}


	public String getPlayingStartTime() {
		return playingStartTime;
	}


	public void setPlayingStartTime(String playingStartTime) {
		this.playingStartTime = playingStartTime;
	}


	public String getBitrateSize() {
		return bitrateSize;
	}


	public void setBitrateSize(String bitrateSize) {
		this.bitrateSize = bitrateSize;
	}


	public String getVideoSpecification() {
		return videoSpecification;
	}


	public void setVideoSpecification(String videoSpecification) {
		this.videoSpecification = videoSpecification;
	}


	public String getVideoCodeFormat() {
		return videoCodeFormat;
	}


	public void setVideoCodeFormat(String videoCodeFormat) {
		this.videoCodeFormat = videoCodeFormat;
	}


	public String getVideoFormat() {
		return videoFormat;
	}


	public void setVideoFormat(String videoFormat) {
		this.videoFormat = videoFormat;
	}


	public String getVideoFps() {
		return videoFps;
	}


	public void setVideoFps(String videoFps) {
		this.videoFps = videoFps;
	}


	public String getVideoKbps() {
		return videoKbps;
	}


	public void setVideoKbps(String videoKbps) {
		this.videoKbps = videoKbps;
	}



	public String getAudioCoding() {
		return audioCoding;
	}


	public void setAudioCoding(String audioCoding) {
		this.audioCoding = audioCoding;
	}


	public String getAudioChannel() {
		return audioChannel;
	}


	public void setAudioChannel(String audioChannel) {
		this.audioChannel = audioChannel;
	}


	public String getAudioKbps() {
		return audioKbps;
	}


	public void setAudioKbps(String audioKbps) {
		this.audioKbps = audioKbps;
	}


	public String getAudioSampling() {
		return audioSampling;
	}


	public void setAudioSampling(String audioSampling) {
		this.audioSampling = audioSampling;
	}

	

	public String getWidth() {
		return width;
	}


	public void setWidth(String width) {
		this.width = width;
	}


	public String getHeight() {
		return height;
	}


	public void setHeight(String height) {
		this.height = height;
	}


	@Override
	public String toString() {
		return "Multimedia [duration=" + duration + ", playingStartTime="
				+ playingStartTime + ", bitrateSize=" + bitrateSize
				+ ", videoSpecification=" + videoSpecification
				+ ", videoCodeFormat=" + videoCodeFormat + ", videoFormat="
				+ videoFormat + ", videoFps=" + videoFps + ", videoKbps="
				+ videoKbps + ", audioCoding=" + audioCoding
				+ ", audioChannel=" + audioChannel + ", audioKbps=" + audioKbps
				+ ", audioSampling=" + audioSampling + ", width=" + width
				+ ", height=" + height + ", getFileName()=" + getFileName()
				+ ", isExists()=" + isExists() + ", getPath()=" + getPath()
				+ ", getAbsolutePath()=" + getAbsolutePath() + ", isCanRead()="
				+ isCanRead() + ", isCanWrite()=" + isCanWrite()
				+ ", getParentPath()=" + getParentPath() + ", getLength()="
				+ getLength() + ", getLastModified()=" + getLastModified()
				+ ", isFile()=" + isFile() + ", isDirectory()=" + isDirectory()
				+ ", getFormat()=" + getFormat() + ", getClass()=" + getClass()
				+ ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}


	

	
	
} 

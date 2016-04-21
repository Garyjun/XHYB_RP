package com.brainsoon.common.util.dofile.conver;

import java.io.IOException;

import com.brainsoon.common.util.dofile.util.DateTools;

public class ConverThread implements Runnable {

	private String srcVideoPath;
	private String tarVideoPath;
	
	//启动线程要加载的参数
    public ConverThread(String srcVideoPath,String tarVideoPath) {
		super();
		this.srcVideoPath = srcVideoPath;
		this.tarVideoPath = tarVideoPath;
	}
    
	@Override
    public void run() {
		//转换服务
		long ss = DateTools.getStartTime();
		ConverUtils.processFfmpegToFLV(srcVideoPath, tarVideoPath);
        DateTools.getTotaltime(ss);
    }
}

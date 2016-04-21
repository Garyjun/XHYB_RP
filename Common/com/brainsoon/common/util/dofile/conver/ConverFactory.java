//package com.brainsoon.common.util.dofile.conver;
//
//import com.brainsoon.common.service.impl.BaseService;
//import com.brainsoon.common.util.dofile.metadata.DateTools;
//
///**
// * 
// * @ClassName: ConverFactory 
// * @Description:  视频转换转换工厂类
// * @author tanghui 
// * @date 2014-4-29 下午3:09:45 
// *
// */
//public class ConverFactory  extends BaseService{
//
//	private String srcVideoPath;
//	private String tarVideoPath;
//	
//	//启动线程要加载的参数
//    public ConverFactory(String srcVideoPath,String tarVideoPath) {
//		super();
//		this.srcVideoPath = srcVideoPath;
//		this.tarVideoPath = tarVideoPath;
//	}
//    
//	@Override
//    public synchronized void run() {
//		//转换服务
//		long ss = DateTools.getStartTime();
//		ConverUtils.processFfmpegToFLV(srcVideoPath, tarVideoPath);
//		//getBaseDao().executeUpdate("from tb where tb.id=" + ss, parameters);
//        DateTools.getTotaltime(ss);
//    }
//}

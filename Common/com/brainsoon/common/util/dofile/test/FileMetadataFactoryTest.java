package com.brainsoon.common.util.dofile.test;

import com.brainsoon.common.util.dofile.metadata.FileMetadataFactory;
import com.brainsoon.common.util.dofile.metadata.fo.Multimedia;
import com.brainsoon.common.util.dofile.metadata.fo.Picture;
import com.brainsoon.common.util.dofile.util.DateTools;

/**
 * 
 * @ClassName: FileMetadataFactoryTest 
 * @Description:  test 文件元数据抽取测试类
 * @author tanghui 
 * @date 2014-4-29 下午2:46:58 
 *
 */
public class FileMetadataFactoryTest {

	public static void main(String[] args){
		//测试开始
		long ss = DateTools.getStartTime();
		//图片
		Picture picture = (Picture) FileMetadataFactory.getMetadata("D:/Project素材/image/10.JPG");
		//视频
		Multimedia video = (Multimedia) FileMetadataFactory.getMetadata("D:/Project素材/video/6.flv");
		//音频
		Multimedia audio = (Multimedia) FileMetadataFactory.getMetadata("D:/Project素材/video/1.wav");
		//动画
		Multimedia animation = (Multimedia) FileMetadataFactory.getMetadata("D:/Project素材/video/tzdxsdn.swf");
        
		System.out.println(picture);
        
        System.out.println(video);
        
        System.out.println(audio);
        
        System.out.println(animation);
        
        //测试结束
      	DateTools.getTotaltime(ss);

     }
}

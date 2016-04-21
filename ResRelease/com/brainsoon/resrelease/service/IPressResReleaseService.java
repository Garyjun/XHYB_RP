package com.brainsoon.resrelease.service;

import java.io.IOException;
import java.net.URISyntaxException;

import magick.MagickException;

import com.brainsoon.common.service.IBaseService;
import com.brainsoon.resrelease.po.ParamsTempEntity;
import com.brainsoon.resrelease.po.ResOrder;
import com.brainsoon.resrelease.po.ResReleaseDetail;

/**
 * @ClassName: IPressResReleaseService
 * @Description: 出版库发布
 * @author xiehewei
 * @date 2014年9月17日 下午4:55:28
 *
 */
public interface IPressResReleaseService extends IBaseService {
	public void doProcess(String processTaskId);

	public void processResource(ResOrder resOrder, Long releaseId, String fileRoot,
			ResReleaseDetail resRelDetail, String header,
			ParamsTempEntity paramsTempEntity) throws NumberFormatException,
			URISyntaxException, IOException, InterruptedException;

}

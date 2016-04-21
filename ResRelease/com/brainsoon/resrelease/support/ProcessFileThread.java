package com.brainsoon.resrelease.support;

import org.apache.log4j.Logger;

import com.brainsoon.resrelease.po.ParamsTempEntity;
import com.brainsoon.resrelease.po.ResOrder;
import com.brainsoon.resrelease.po.ResReleaseDetail;
import com.brainsoon.resrelease.service.impl.ResReleaseService;

public class ProcessFileThread implements Runnable {

	private static Logger logger = Logger.getLogger(ProcessFileThread.class);
	private ResOrder resOrder;
	private Long releaseId;
	private String fileRoot;
	private ResReleaseDetail resRelDetail;
	private String header;
	private ParamsTempEntity paramsTempEntity;
	@Override
	public void run() {
		while (true) {
			try {
				// 获取任务队列
				ProcessTaskQueue queue = ProcessTaskQueue.getInst();
				// 启动任务
				ResReleaseDetail task = queue.getMessage();
				if (null != task) {
					//处理
					new ResReleaseService().processResource(resOrder, releaseId, fileRoot, header, paramsTempEntity);
				}
			}catch (Exception e) {
				logger.error(e);
			}
		}
	}
	public ResOrder getResOrder() {
		return resOrder;
	}
	public void setResOrder(ResOrder resOrder) {
		this.resOrder = resOrder;
	}
	public Long getReleaseId() {
		return releaseId;
	}
	public void setReleaseId(Long releaseId) {
		this.releaseId = releaseId;
	}
	public String getFileRoot() {
		return fileRoot;
	}
	public void setFileRoot(String fileRoot) {
		this.fileRoot = fileRoot;
	}
	public ResReleaseDetail getResRelDetail() {
		return resRelDetail;
	}
	public void setResRelDetail(ResReleaseDetail resRelDetail) {
		this.resRelDetail = resRelDetail;
	}
	public String getHeader() {
		return header;
	}
	public void setHeader(String header) {
		this.header = header;
	}
	public ParamsTempEntity getParamsTempEntity() {
		return paramsTempEntity;
	}
	public void setParamsTempEntity(ParamsTempEntity paramsTempEntity) {
		this.paramsTempEntity = paramsTempEntity;
	}

}

package com.brainsoon.search.service;


import java.util.List;

import com.brainsoon.common.service.IBaseService;
import com.brainsoon.semantic.ontology.model.Ca;

public interface IAcrossStoreSearchService  extends IBaseService{
	public String queryFormList(String metadataMap,String page,String size);
	
	/**
	 * 根据选中的资源的id 通过ftp下载对应资源下的文件
	 * fengda 2015年11月9日
	 * @param ids  选中的资源id
	 * @param encryptPwd	加密密匙
	 * @param ftpFlag		下载方式1.http下载 2.ftp下载但此处只支持ftp下载
	 * @param encryptZip	下载后文件的相对路径
	 * @param isComplete	是否压缩  1.是 2.不是
	 * @return
	 */
	public String createFtpDownload(String ids, String encryptPwd,
			String ftpFlag, String encryptZip, String isComplete);
	
	
	
	/**
	 * fengda 2015年11月9日
	 * 根据分页（输入起始页结束页）下载对应资源下的文件
	 * @param cas    根据页数查出的资源列表
	 * @param flag	   下载方式1.http下载 2.ftp下载但此处只支持ftp下载
	 * @param encryptZip   下载后文件的相对路径
	 * @param encryptPwd    加密密匙
	 * @param isComplete   是否压缩  1.是 2.不是
	 * @return
	 */
	public String createByPageFtpDownload(List<Ca> cas, String flag,
				String encryptZip, String encryptPwd, String isComplete);
}

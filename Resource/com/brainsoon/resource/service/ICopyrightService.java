package com.brainsoon.resource.service;

import java.io.File;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.pagination.PageInfo;
import com.brainsoon.common.service.IBaseService;
import com.brainsoon.resource.po.CopyrightImportResult;
import com.brainsoon.resource.po.CopyrightRepeat;
import com.brainsoon.resource.po.CopyrightWarning;
import com.brainsoon.resource.support.SemanticResponse;
import com.brainsoon.semantic.ontology.model.FileList;
import com.brainsoon.system.model.ResTarget;

@SuppressWarnings("rawtypes")
public interface ICopyrightService extends IBaseService{
	public FileList getFilesByDoi(String doi);
	/*
	 * 更新版权预警信息
	 */
	public void updateCopyrightWarning();
	/*
	 * 公共资源更新版权预警信息
	 */
	public void updatePubliCopyrightWarning();
	/**
	 * 分页查询版权预警
	 * @param pageInfo
	 * @param role
	 * @return
	 */
	public PageResult queryCopyrightWarnings(PageInfo pageInfo, CopyrightWarning copyrightWarning,HttpServletRequest request) throws ServiceException;
	/**
	 * 批量导入版权元数据
	 * @param excelFile
	 */
	public void doImportCopyright(File excelFile);
	/**
	 * 分页查询重复资源
	 * @param pageInfo
	 * @param role
	 * @return
	 */
	public PageResult queryCopyrightRepeats(PageInfo pageInfo, CopyrightRepeat copyrightRepeat) throws ServiceException;
	/**
	 * 分页查询批量导入日志
	 * @param pageInfo
	 * @param role
	 * @return
	 */
	public PageResult queryCopyrightImportResults(PageInfo pageInfo, CopyrightImportResult copyrightImportResult) throws ServiceException;
	
	/**
	 * 关联版权
	 * @param id
	 * @param objectId
	 * @return
	 */
	public String doRelateCopyright(int id,String objectId);
	/**
	 * 煤炭版权查询
	 */
	public void searchCopyrightWarning();
	/**
	 * 更新本地sql期刊，大事迹
	 */
	public void updateMysqlEntry();
}

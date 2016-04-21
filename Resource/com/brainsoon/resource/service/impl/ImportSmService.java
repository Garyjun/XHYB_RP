package com.brainsoon.resource.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.aspectj.util.FileUtil;
import org.springframework.stereotype.Service;

import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.common.util.dofile.cnmarc.CNMarc;
import com.brainsoon.common.util.dofile.cnmarc.CNMarcUtils;
import com.brainsoon.common.util.dofile.cnmarc.ICNMarcService;
import com.brainsoon.common.util.dofile.content.PdfUtil;
import com.brainsoon.resource.service.IImportSmService;
import com.brainsoon.resource.service.IResourceService;
import com.brainsoon.resource.util.ResUtils;
import com.brainsoon.resource.util.SysMetadataTypeConfigUtil;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.semantic.ontology.model.SearchParamCa;

/**
 * 书目导入service
 * @author 唐辉
 *
 */
@Service
public class ImportSmService extends BaseService implements IImportSmService {
	/**
	 * 自动导入书目资源
	 * @param SearchParamCa
	 * @throws ServiceException
	 */
	public Ca addSm(SearchParamCa searchParamCa) throws ServiceException {
		Ca ca = new Ca();
		try {
			//第一步获取书目类型id
			/* 书目资源元数据模板定义 */
			if (searchParamCa != null) {
				CNMarc cnmarc = ICNMarcService.createCNMarcByColumnsStr(searchParamCa.getMarc());
				//获取书目元数据的值
				ca = loadDataMapFormCNMARC(cnmarc);
				//设置资源类型id
				ca.setPublishType(searchParamCa.getPublishType());
				ca.getMetadataMap().put("sm_Oidentifier", searchParamCa.getPublishType()); //资源类型标识
				ca.getMetadataMap().put("sm_versionnum", "00"); //版本号
				ca.setCreateTime(searchParamCa.getUpdateTime());
				ca.setCreator(searchParamCa.getUpdateUser().getId()+"");
				ca.setUpdateTime(searchParamCa.getUpdateTime());
				ca.setUpdater(searchParamCa.getUpdateUser().getId()+"");
				//默认审核通过
				ca.setStatus("3");
				//获取Ca中的realFiles对象
				ca = getCaOfRealFiles(ca,searchParamCa,cnmarc);
			}
		} catch (Exception ex) {
			logger.error("书目入库异常！", ex);

			throw new ServiceException(ex.getMessage());
		}
		return ca;
	}

	/**
	 * 获取文件对象
	 * @param ca
	 * @return
	 */
	public Ca getCaOfRealFiles(Ca ca,SearchParamCa searchParamCa,CNMarc cnmarc){
		/* 书目样本所在服务器目录 */
		String parameValue = searchParamCa.getSmFilePath();
		//设置解压路径
		try {
			String doi = ca.getMetadataMap().get("identifier");
			IResourceService resourceService = (IResourceService) BeanFactoryUtil.getBean("resourceService");
			//拷贝后的文件路径
			String parentPath = resourceService.createPublishParentPath(searchParamCa.getPublishType(), doi) + "/11/";
			//String scrFilePath = parameValue + File.separatorChar + java.util.UUID.randomUUID().toString();
			int bookId = Integer.parseInt(cnmarc.getColumns().get("001").getContent());
			/* 创建目录并把文件复制到基础资源对应的目录下 */
//			com.brainsoon.common.util.BresUtil.createDir(unzipFilePath);
			File file = new File(parentPath);
			if(!file.exists()){
				file.mkdirs();
			}
			ICNMarcService.createCNMarcFile(parentPath + File.separatorChar + bookId + ".iso", cnmarc, com.brainsoon.common.util.dofile.cnmarc.CNMarcConstants.CNMarcStandard.CN);
			/* 书目样本所在服务器目录 */
			String smPdfPath = parameValue + File.separatorChar + String.valueOf(bookId / 1000) + File.separatorChar + String.valueOf(bookId) + ".pdf";
			if (new File(smPdfPath).exists()) {
				try {
					//PDF文件
					String pdfPath = parentPath + File.separatorChar + bookId + ".pdf";
					FileUtil.copyFile(new File(smPdfPath), new File(pdfPath));
					//生成封面文件
					PdfUtil.pdfToImage(pdfPath, parentPath + File.separatorChar + "cover.jpg", 1);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			int i = 1;
			int j = 1;
			String pid = "-1";
			int num = 0;
			try {
				//ca = ResUtils.getFileLists(parentPath, new File(parentPath), pid, ca, i, j,num,doi,true);
				ca = ResUtils.getFileLists(parentPath, new File(parentPath), pid, ca, doi, true);//修改 不用改名 huangjun 2015-11-9 18:33:33
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return ca;
	}

	/**
	 * 通过CNMARC生成u资源元数据对象
	 *
	 * @param importType
	 * @return
	 * @throws ServiceException
	 */
	private Ca loadDataMapFormCNMARC(CNMarc cnmarc) throws ServiceException {
		Ca ca = new Ca();
		Map<String, String> metadataMap = new LinkedHashMap<String, String>();
		try {
			//获取书目元数据属性的方法
			Map<String, String> config = SysMetadataTypeConfigUtil.getConfig("smtocnmarc");
			Iterator<String> its = config.keySet().iterator();
			String index = ""; //流水号
			String isbn = ""; //isbn
			while (its.hasNext()) {
				String keyConfig = its.next().toString();
				String valueConfig = config.get(keyConfig);
				String cnmarcValue = "";
				if (StringUtils.equals("bookId", keyConfig)) {
					int identifier = Integer.parseInt(cnmarc.getColumns().get("001").getContent());
					cnmarcValue = String.valueOf(identifier);
					index = cnmarcValue;
				} else if (StringUtils.equals("subject", keyConfig)) {
					cnmarcValue = ICNMarcService.getSubject(cnmarc);
				} else if (StringUtils.equals("dimensions", keyConfig)) {
					cnmarcValue = cnmarc.getFieldDesc(valueConfig);
					cnmarcValue = CNMarcUtils.formatSize(cnmarcValue);
				} else if (StringUtils.equals("creator", keyConfig)) {
					cnmarcValue = ICNMarcService.getCreator(cnmarc);
				} else if (StringUtils.equals("type", keyConfig)) {
					cnmarcValue = ICNMarcService.getType(cnmarc);
				} else if (StringUtils.equals("contributortrl", keyConfig)) {
					cnmarcValue = ICNMarcService.getContributortrl(cnmarc);
				} else if (StringUtils.equals("parTitle", keyConfig)) {
					// 并列题名
					cnmarcValue = cnmarc.getFieldDesc(valueConfig);
					cnmarcValue = cnmarcValue.replace("=", "");
				}else if(StringUtils.equals("cbclass", keyConfig)){/* 特殊处理中图分类 */
					//=============未完成
					cnmarcValue = cnmarc.getFieldDesc(valueConfig);
					cnmarcValue = cnmarcValue.replace("=", "");
//					
				}else if(StringUtils.equals("isbn", keyConfig)){
					cnmarcValue = cnmarc.getFieldDesc(valueConfig); //isbn对于的值
					isbn = cnmarcValue;
				}else {
					cnmarcValue = cnmarc.getFieldDesc(valueConfig);
				}
				
				metadataMap.put(keyConfig, cnmarcValue);
			}
			
			//设置identifier
			if(StringUtils.isNotBlank(index) && StringUtils.isNotBlank(isbn)){
				int len = 8 - index.length();
				String str = "";
				if(len > 0){
					for (int i = 0; i < len; i++) {
						str += "0";
					}
				}
				String doi = "CGP.07/07.isbn."+ isbn +".00."+ str + index;
				metadataMap.put("identifier", doi); //doi
			}
			
			ca.setMetadataMap(metadataMap);
			//其他属性
			
		} catch (Exception ex) {
			logger.error("生成资源元数据异常", ex);
			throw new ServiceException("生成资源元数据异常");
		}
		return ca;
	}

 }

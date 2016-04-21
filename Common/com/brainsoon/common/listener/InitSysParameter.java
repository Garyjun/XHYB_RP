package com.brainsoon.common.listener;
import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.support.Constants;
import com.brainsoon.common.support.GlobalAppCacheMap;
import com.brainsoon.common.support.GlobalDataCacheMap;
import com.brainsoon.common.support.MetadataDefineCacheMap;
import com.brainsoon.common.support.MetadataDefinitionGroupCacheMap;
import com.brainsoon.common.support.OperDbUtils;
import com.brainsoon.common.support.SysResTypeCacheMap;
import com.brainsoon.semantic.schema.ontology.CustomMetaData;
import com.brainsoon.semantic.schema.store.jena.service.MetaService;
import com.brainsoon.system.model.MetaDataModelGroup;
import com.brainsoon.system.model.MetadataDefinitionGroup;

/**
 * 
 * @ClassName: InitSysParameter 
 * @Description:  系统启动时，初始化系统相关配置参数
 * @author tanghui 
 * @date 2014-9-3 下午12:51:13 
 *
 */
public class InitSysParameter implements ServletContextListener {
	private static final Logger logger = Logger.getLogger(InitSysParameter.class);

	public void contextDestroyed(ServletContextEvent arg0) {
	}

	public void contextInitialized(ServletContextEvent event) {
		logger.info("初始化InitParameter....");
		//ServletContext servletContext = event.getServletContext();
		String platformIds = WebappConfigUtil.getParameter(Constants.PLATFORM_ID);
		//servletContext.setAttribute("platformId", platformIds);
		GlobalAppCacheMap.putKey(Constants.PLATFORM_ID, platformIds);
		List<MetaDataModelGroup> sysResTypes = OperDbUtils.querySysResTypeList();
		if(sysResTypes!=null && sysResTypes.size()>0){
			for(MetaDataModelGroup metaDataModelGroup:sysResTypes){
				SysResTypeCacheMap.putKey(metaDataModelGroup.getId()+"",metaDataModelGroup.getTypeName());
			}
		}
		CustomMetaData cmBaseMetaData = MetaService.getBaseMetaSchemas("BaseMetaData");
		CustomMetaData cmCommonMetaData = MetaService.getBaseMetaSchemas("CommonMetaData");
		CustomMetaData fileMetaData = MetaService.getBaseMetaSchemas("FileMetaData");
		if(cmCommonMetaData!=null){
			MetadataDefineCacheMap.putKey("CommonMetaData", cmCommonMetaData);
		}
		if(cmBaseMetaData!=null){
			MetadataDefineCacheMap.putKey("BaseMetaData", cmBaseMetaData);
		}
		if(fileMetaData!=null){
			MetadataDefineCacheMap.putKey("FileMetaData", fileMetaData);
		}
		//判断是否需要对状态为：加工中 的列表进行处理（解决由于重启服务等造成的数据停止转换，
		//这样就会出现部分记录可能永远也无法被转换了，因为生产线程只处理：待转换和需要重试的记录）
		//判断缓存中是否有该KEY，如果没有则说明是刚启动应用，否则不是
		//ConverFilePStuas == 0  为需要对转换中的记录进行重新处理
		//GlobalAppCacheMap.putKey("ConverFilePStuas", "0");
		//启动自动转换服务
		//PCRun.getInstance().run();
		logger.info("初始化InitParameter....OK.");
		
		//加载数据字典缓存
		GlobalDataCacheMap.refreshAll();
		//加载人员单位缓存
		GlobalDataCacheMap.refreshPeopleUnit();
		List<MetadataDefinitionGroup> metadataDefinitionGroup = OperDbUtils.queryMetaDataDefinitionGroupList();
		if(metadataDefinitionGroup!=null && metadataDefinitionGroup.size()>0){
			for(MetadataDefinitionGroup metaDataDefinitionGroup:metadataDefinitionGroup){
				MetadataDefinitionGroupCacheMap.putKey(metaDataDefinitionGroup.getId()+"",metaDataDefinitionGroup.getFieldZhName());
			}
		}
		
	}

}

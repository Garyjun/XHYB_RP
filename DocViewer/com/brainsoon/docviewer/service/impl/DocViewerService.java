package com.brainsoon.docviewer.service.impl;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.common.util.dofile.util.ConstantsDef;
import com.brainsoon.common.util.dofile.util.DoFileUtils;
import com.brainsoon.common.util.dofile.util.WebAppUtils;
import com.brainsoon.docviewer.model.ResConverfileTask;
import com.brainsoon.docviewer.model.ResConverfileTaskHistory;
import com.brainsoon.docviewer.service.IDocViewerService;
import com.brainsoon.resrelease.support.FileUtil;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.semantic.ontology.model.DoFileHistory;
import com.brainsoon.semantic.ontology.model.File;
import com.google.gson.Gson;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

/**
 * 
 * @ClassName: DocViewerService 
 * @Description: 文件转换相关功能service 
 * @author tanghui 
 * @date 2014-8-7 下午4:47:34 
 * @update功能：对历史表的查询
 *
 */
@Service("docViewerService")
public class DocViewerService extends BaseService implements IDocViewerService{

	@Override
	/**
	 * 检查文件是否转换成功
	 * 第一步检查历史表是否有转换成功或失败的记录
	 * 第二步检查待转换表的记录
	 * 
	 */
	@SuppressWarnings("unchecked")
	public ResConverfileTask checkingConverSucessFull(String srcPath) {
		ResConverfileTask rft = null;
		List<ResConverfileTaskHistory> historyList = getBaseDao().query("from ResConverfileTaskHistory rft where rft.srcPath='" + srcPath + "'");
		if(historyList != null && !historyList.isEmpty()){
			ResConverfileTaskHistory historyrft = historyList.get(0);
			try {
				rft = new ResConverfileTask();
				BeanUtils.copyProperties(rft, historyrft);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}else{
			List<ResConverfileTask> list = getBaseDao().query("from ResConverfileTask rft where rft.srcPath='" + srcPath + "'");
			if(list != null && !list.isEmpty()){
			    rft = list.get(0);
			}
		}
		return rft;
	}
	
	/**
	 * 
	* @Title: getConverPathByObjectId
	* @Description: 根据objectId获取转换后路径
	* @param objectId
	* @return    参数
	* @return String    返回类型
	* @throws
	 */
	public String getConverPathByObjectId(String objectId){
		String converPath = "";
		List<DoFileHistory> doFileHistories = getBaseDao().query("from DoFileHistory d where d.objectId='" + objectId + "'");
		if(doFileHistories != null && !doFileHistories.isEmpty()){
			DoFileHistory doFileHistory = doFileHistories.get(0);
			converPath = doFileHistory.getResultConveredfilePath();
		}
		return converPath;
	}
	
}

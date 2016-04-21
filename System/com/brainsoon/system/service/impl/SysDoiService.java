package com.brainsoon.system.service.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.brainsoon.appframe.query.QueryConditionItem;
import com.brainsoon.appframe.query.QueryConditionList;
import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.pagination.PageInfo;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.resource.service.IBaseSemanticSerivce;
import com.brainsoon.semantic.ontology.model.Asset;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.system.model.MetaDataFileModelGroup;
import com.brainsoon.system.model.Privilege;
import com.brainsoon.system.model.ResTarget;
import com.brainsoon.system.model.ResTargetData;
import com.brainsoon.system.model.SysDoi;
import com.brainsoon.system.model.User;
import com.brainsoon.system.service.IResTargetService;
import com.brainsoon.system.service.ISysDoiService;
import com.brainsoon.system.support.SysOperateLogUtils;
import com.google.gson.Gson;
@Service
public class SysDoiService extends BaseService implements ISysDoiService {
	@Override
	public boolean createDoi(HttpServletRequest request,
			HttpServletResponse response, SysDoi sysDoi)
			throws ServiceException {
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		String hql ="";
		boolean tag = false;
		hql = "update SysDoi set ";
		try {
			if (sysDoi.getPublishType() != null && sysDoi.getId()!=null && !"".equals(sysDoi.getId())) {
				if(StringUtils.isNotBlank(sysDoi.getFirstPartOne())){
					hql = hql+"firstPartOne='"+sysDoi.getFirstPartOne()+"'";	
					}
//				if(StringUtils.isNotBlank(sysDoi.getFirstPartTwo())){
					hql = hql+",firstPartTwo='"+sysDoi.getFirstPartTwo()+"'";	
//					}
					hql = hql+",firstPartThree='"+sysDoi.getFirstPartThree()+"'";
					hql = hql+",secondOptionalOne='"+sysDoi.getSecondOptionalOne()+"'";	
					hql = hql+",secondOptionalTwo='"+sysDoi.getSecondOptionalTwo()+"'";	
					hql = hql+",secondOptionalThree='"+sysDoi.getSecondOptionalThree()+"'";	
				if(StringUtils.isNotBlank(sysDoi.getSeparator())){
					hql = hql+",separator='"+sysDoi.getSeparator()+"'";
					}
					hql = hql+",thirdExtendOne='"+sysDoi.getThirdExtendOne()+"'";	
					hql = hql+",thirdExtendTwo='"+sysDoi.getThirdExtendTwo()+"'";	
					hql = hql+",thirdExtendThree='"+sysDoi.getThirdExtendThree()+"'";	
					hql = hql+",thirdExtendFour='"+sysDoi.getThirdExtendFour()+"'";	
					hql = hql+",thirdExtendFive='"+sysDoi.getThirdExtendThree()+"'";
					hql=hql+" where publishType="+sysDoi.getPublishType();
					tag = baseDao.updateWithHql(hql);
//				SysOperateLogUtils.addLog("doi_update", "修改Doi为:"+ sysDoi.getFirstPartOne(), LoginUserUtil.getLoginUser());	
		// 添加
			} else {
				baseDao.create(sysDoi);
				tag = true;
//				SysOperateLogUtils.addLog("doi_resSave", "添加Doi为:"+ sysDoi.getFirstPartOne(), LoginUserUtil.getLoginUser());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tag;
	}

	@Override
	public List queryDoiSub() throws ServiceException {
		List<SysDoi> doiSubName = query("from SysDoi");
		return doiSubName;
	}

}

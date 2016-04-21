package com.brainsoon.system.service.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.springframework.stereotype.Service;

import com.brainsoon.appframe.query.QueryConditionList;
import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.common.exception.DaoException;
import com.brainsoon.common.pagination.PageInfo;
import com.brainsoon.common.po.BaseHibernateObject;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.system.model.Company;
import com.brainsoon.system.service.ICompanyService;
@Service
public class CompanyService extends BaseService implements ICompanyService {

	
	/**
	 * 处理出版社，根据出版社的名称和地址查询数据库中是否存在与之相同的出版社
	 * 若有返回已经保存的出版社的id,并且更新实体
	 * 若没有创建一个出版社保存后返回出版社的id
	 * @param request
	 * @return
	 */
	@Override
	public String doSaveOrUpdate(Company company) {
		List<Company> companyList = null;
		boolean record = false;
		String companyId = null;
		String hql = "from Company where name = '" + company.getName() + "'";
		try{
			companyList = query(hql);
			if(companyList.size()>0){
				for (Company companys : companyList) {
					if(StringUtils.isNotBlank(companys.getAddress()) && StringUtils.isNotBlank(company.getAddress())){
						if(companys.getAddress().equals(company.getAddress())){
							record = true;
							companyId = companys.getId().toString();
							company.setId(companys.getId());
							break;
						}
					}else{
						record = true;
						companyId = companys.getId().toString();
						company.setId(companys.getId());
						break;
					}
					
				}
			}
			
			if(!record){
				create(company);
				companyId = company.getId().toString();
			}else{
				saveOrUpdate(company);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return companyId;
	}

	@Override
	public String searchName(String ids) {
		String nameArr[] = null;
		String names = "";
		if(StringUtils.isNotBlank(ids)){
			nameArr = ids.split(",");
			for(String id:nameArr){
				String hql = "select name from Company where id="+id;	
				List<String> nameList = query(hql);
				if(nameList!=null && !nameList.isEmpty()){
					names = names+nameList.get(0)+",";
				}
			}
			if(names.endsWith(",")){
				names = names.substring(0,names.length()-1);
			}
		}
		return names;
	}
}

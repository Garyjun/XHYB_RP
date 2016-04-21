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
import com.brainsoon.system.model.Staff;
import com.brainsoon.system.service.IStaffService;
@Service
public class StaffService extends BaseService implements IStaffService {

	 /**
     * 根据姓名、团体或组织单位名称和地址查询员工表中，该人是否存在
     * 姓名和组织单位必有一项不为空，或都不为空
     * 存在返回此人的id，不存在创建，返回创建后此人的id
     * @param request
     * @return
     */
	@Override
	public String doSaveOrUpdate(Staff staff) {
		
		List<Staff> staffList = null;  //记录查询出来的员工实体类列表
    	String staffId = null;         //记录返回值员工的id
    	boolean record = false;        //标识数据库中是否存在该人员的信息
    	String hql = "from Staff where";
    	try{
    		if(StringUtils.isNotBlank(staff.getName())){
    			 hql += " name = '" + staff.getName() + "'" ;
    		}
    		
    		staffList = query(hql);
    		
    		//如果根据姓名或者组织单位名称查询有此人，
    		//在比较数据库中查询出的人的地址和传入参数的地址是否相同
    		if(staffList.size()>0){
    			for (Staff staffs : staffList) {
    				String staffAdress = staff.getAddress();
					String staffAdtess1 = staffs.getAddress();
    				if(StringUtils.isNotBlank(staffAdress) && StringUtils.isNotBlank(staffAdtess1)){
    					if(staffAdress.equals(staffAdtess1)){
    						staffId = staffs.getId().toString();
    						staff.setId(Long.parseLong(staffId));
    						record = true ;
    						break;
    					}
    				}else{
    					staffId = staffs.getId().toString();
    					staff.setId(Long.parseLong(staffId));
						record = true ;
						break;
    				}
					
				}
    		}
    		
    		if(!record){
    			create(staff);
    			staffId = staff.getId().toString();
    		}else{
    			saveOrUpdate(staff);
    		}
    		
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return staffId;
	}

	@Override
	public String searchName(String ids) {
		String nameArr[] = null;
		String names = "";
		if(StringUtils.isNotBlank(ids)){
			nameArr = ids.split(",");
			for(String id:nameArr){
				String hql = "select name from Staff where id="+id;	
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

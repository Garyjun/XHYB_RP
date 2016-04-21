package com.brainsoon.system.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.pagination.PageInfo;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.system.model.SysDir;
import com.brainsoon.system.service.ISysDirService;
@Service
public class SysDirService extends BaseService implements ISysDirService {

	private JdbcTemplate jdbcTemplate ;
	@Autowired
	public void init(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	@Override
	public List validateDirName(String sql) {
		return jdbcTemplate.queryForList(sql);
	}
	/**
	 * 分页查询
	 * @param pageInfo
	 * @param sysDir
	 * @return
	 */
	public PageResult querySysDir(PageInfo pageInfo, SysDir sysDir) throws ServiceException{
		int platformId = sysDir.getPlatformId();
		String hql=" from SysDir where platformId = "+platformId+"";
    	Map<String, Object> params=new HashMap<String, Object>();
    	try {
			baseDao.query4Page(hql, pageInfo, params);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ServiceException(e.getMessage());
		}
    
    	return pageInfo.getPageResult();
	}

	@Override
	public void save(SysDir sysDir) {
		this.getBaseDao().create(sysDir);
	}

	@Override
	public void doUpdateSysDir(SysDir sysDir) {
		this.getBaseDao().update(sysDir);
	}
	
	@Override
	public List<String> getDirByResType(String resType) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		int platformId = LoginUserUtil.getLoginUser().getPlatformId();
		parameters.put("resType", resType);
		parameters.put("platformId", platformId);
		String hql = "select dirCnName from SysDir sd where sd.resType=:resType and sd.platformId=:platformId";
		return this.getBaseDao().query(hql, parameters);
	}
	//根据资源类型查询该资源下的资源目录
	@Override
	public String findResourceByResType(String resType) {
		String sql = "select dirEnName from sys_dir where resType = "+resType;
		List<String> dirNameList = null;
		try{
			dirNameList = jdbcTemplate.queryForList(sql, String.class);
		}catch(Exception e){
			e.printStackTrace();
		}
		return StringUtils.substringBetween(dirNameList.toString(), "[", "]");
	}
	
	
	/**
	 * 查询历史记录中的敏感词(数据字典中)
	 */
	@Override
	public String findWords() {
		String sql ="select v.name from dict_name n,dict_value v where v.pid = n.Id and n.indexTag = 'word_name'";
		List<String> wordList = null;
		try{
			wordList = jdbcTemplate.queryForList(sql, String.class);
		}catch(Exception e){
			e.printStackTrace();
		}
		return StringUtils.substringBetween(wordList.toString(), "[", "]");
	}
}

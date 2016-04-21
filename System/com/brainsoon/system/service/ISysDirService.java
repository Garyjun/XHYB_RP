package com.brainsoon.system.service;

import java.util.List;

import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.pagination.PageInfo;
import com.brainsoon.common.service.IBaseService;
import com.brainsoon.system.model.SysDir;

public interface ISysDirService extends IBaseService {

	public void save(SysDir sysDir);
	public void doUpdateSysDir(SysDir sysDir);
	public PageResult querySysDir(PageInfo pageInfo, SysDir sysDir) throws ServiceException;
	public List<String> getDirByResType(String resType);
	public List validateDirName(String sql);
	
	//根据资源类型查询该资源下的资源目录
	public String findResourceByResType(String resType);
	
	//查询历史记录保存的敏感词
	public String findWords();
}

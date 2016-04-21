package com.brainsoon.system.service;



import java.util.List;

import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.common.pagination.PageInfo;
import com.brainsoon.common.service.IBaseService;
import com.brainsoon.system.model.SysParameter;

public interface ISysParameterService  extends IBaseService{
	public List<SysParameter> getSysList();
	public void save(SysParameter sysParameter);
	public List validateParamKey(String sql);
	public List findParaValue(String paraKey);
	public PageResult querySysParameter(PageInfo pageInfo, SysParameter sysParameter);
}

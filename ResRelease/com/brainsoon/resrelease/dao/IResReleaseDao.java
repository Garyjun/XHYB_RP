package com.brainsoon.resrelease.dao;


import java.util.List;

import com.brainsoon.common.dao.IBaseDao;
import com.brainsoon.resrelease.po.ResFileRelation;
import com.brainsoon.resrelease.po.ResReleaseDetail;

public interface IResReleaseDao extends IBaseDao {
	
	public List<ResReleaseDetail> getResReleaseDetailByRelIdAndStatus(Long relId,
			String status);
	
	public ResReleaseDetail getResReleaseDetailByRelIdAndResId(Long relId,
			String resId);
	
	public List<ResFileRelation> getFileListByOrderId(Long orderId);
}

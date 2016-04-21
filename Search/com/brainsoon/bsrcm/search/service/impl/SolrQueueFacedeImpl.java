package com.brainsoon.bsrcm.search.service.impl;

import java.util.Date;
import java.util.List;

import com.brainsoon.bsrcm.search.po.SolrQueue;
import com.brainsoon.bsrcm.search.service.ISolrQueueFacede;
import com.brainsoon.common.service.impl.BaseService;

public class SolrQueueFacedeImpl extends BaseService implements
		ISolrQueueFacede {

	/**
	 * 通过状态返回创建索引信息的集合
	 * 
	 * @param status
	 * @return
	 */
	@Override
	public List<SolrQueue> getSolrQueueByStatus(String status) {
		List<SolrQueue> result = null;
		try {
			String hql = "from SolrQueue where status = " + status + " order by createTime asc";
			result = query(hql);

   		} catch (Exception ex) {
            logger.error("获取索引集合信息异常！", ex);
        }
        return result;
	}


    /**
     * 更新状态接口
     * 
     * @param status
     * @return
     */
    @Override
    public void updateSolrQueueStatus(Long id, String status) {        
        try {
        	SolrQueue queue = (SolrQueue)getByPk(SolrQueue.class, id);
        	queue.setStatus(status);
			update(queue);
        } catch (Exception ex) {
            logger.error("更新索引队列状态异常！", ex);
        }
    }

	@Override
	public String addSolrQueue(String resId,String url) {
		String msg = "";
		List<SolrQueue> result = null;
		try {
			String hql = "from SolrQueue where resId='" + resId + "'";
			result = query(hql);

		} catch (Exception ex) {
			msg = "建索引信息的集合异常";
			logger.error("建索引信息的集合异常！", ex);
		}
		if (result != null && result.size() > 0) {
			msg = "重复：该资源在SolrQueue中已存在！";
		} else {
			SolrQueue queue = new SolrQueue();
			queue.setResId(resId);
			queue.setActions(url);
			queue.setCreateTime(new Date());
			queue.setStatus("0");
			this.create(queue);
			msg = "添加成功！";
		}
		return msg;
	}


}

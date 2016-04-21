package com.brainsoon.common.dao.hibernate;

import java.sql.SQLException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.brainsoon.common.dao.IBaseJdbcDao;
import com.brainsoon.common.exception.DaoException;
import com.brainsoon.resrelease.po.ResReleaseDetail;

/**
 * @ClassName: BaseJdbcDao
 * @Description: 
 * @author xiehewei
 * @date 2014年11月25日 下午2:23:25
 *
 */
public class BaseJdbcDao extends JdbcDaoSupport implements IBaseJdbcDao {

	private final Logger logger = Logger.getLogger(BaseJdbcDao.class);
	
	@Override
	public int update(ResReleaseDetail detail,int count,int total) throws DaoException {
		if (logger.isDebugEnabled()) {
			logger.debug("执行更新SQL语句：");
		}
		try {
			String sql = "insert into `res_release_detail` (`release_id`,`res_id`,`template_id`,`status`,`file_type`,"
					+ "`create_user_id`,`create_time`,`moduleName`,`resType`,`channel_name`,`res_title`,`platformId`,`pubResType`"
					+ ",`version`,`publish_status`,`publish_type`) "
					+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			int result =  this.getJdbcTemplate().update(sql, new Object[]{detail.getReleaseId(), detail.getResId(), 
					detail.getTemplate().getId(), detail.getStatus(),detail.getFileType(), 
					detail.getCreateUser().getId(),new Date(),detail.getModuleName(),detail.getResType(),
					detail.getChannelName(),detail.getResTitle(),detail.getPlatformId(),detail.getPubResType(),
					1,1,0
			});
			/*String sql = "insert into `res_release_detail`(`release_id`,`res_id`,`template_id`,`status`,`file_type`) "
					+ "values(?,?,?,?,?)";
			return this.getJdbcTemplate().update(sql, new Object[]{detail.getReleaseId(), detail.getResId(), 
					detail.getTemplate().getId(), detail.getStatus(),detail.getFileType()
			});*/
			
			//String sql = "INSERT INTO `order_publish_task`(`orderId`,`time`,`publishId`,`publishUser`) VALUES(331,'2014-11-25 14:45:56',100,1)";
			//String sql = "INSERT INTO `res_release_detail`(`release_id`,`res_id`,`template_id`,`status`,`file_type`) VALUES(33,'urn:asset-257a4109-b7c6-47cb-acbd-adcc50ea587a',17,'1','doc')";
			//int result =  this.getJdbcTemplate().update(sql);
			/*if(count%5==0){
				logger.debug("time***************");
				try {
					this.getConnection().commit();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}else if(count==total){
				try {
					this.getConnection().commit();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}*/
			/*try {
				this.getConnection().commit();
			} catch (SQLException e) {
				e.printStackTrace();
			}*/
			return result;
			
			//return getJdbcTemplate().update(sql);
		} catch (DataAccessException e) {
			logger.warn(e.getMessage());
			throw new DaoException(e.getMessage(), e);
		}
	}

}

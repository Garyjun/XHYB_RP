package com.brainsoon.taskprocess.dao.impl;

import org.springframework.stereotype.Repository;
import com.brainsoon.common.dao.hibernate.BaseHibernateDao;
import com.brainsoon.taskprocess.dao.ITaskProcessDao;

@Repository("taskProcessDao")
public class TaskProcessDao extends BaseHibernateDao implements ITaskProcessDao {

}

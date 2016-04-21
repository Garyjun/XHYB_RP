package com.brainsoon.system.dao.impl;
import org.springframework.stereotype.Repository;

import com.brainsoon.common.dao.hibernate.BaseHibernateDao;
import com.brainsoon.system.dao.IRoleDao;
import com.brainsoon.system.dao.IUserDao;
import com.brainsoon.system.model.Role;
import com.brainsoon.system.model.User;
@Repository
public class UserDao extends BaseHibernateDao implements IUserDao {

}

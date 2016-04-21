package com.brainsoon.common.dao.util;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @ClassName: ConditionQuery 
 * @Description:  查询参数封装类
 * @author tanghui 
 * @date 2013-8-10 下午8:48:21 
 *
 */
public class ConditionQuery {

    
    private List<Criterion> criterions = new ArrayList<Criterion>();
    
    public void add(Criterion criterion) {
        criterions.add(criterion);
    }
    
    public void build(Criteria criteria) {
        for(Criterion criterion : criterions) {
            criteria.add(criterion);
        }
    }
        
}

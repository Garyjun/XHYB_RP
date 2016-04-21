package com.brainsoon.common.dao.util;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @ClassName: OrderBy 
 * @Description: 查询参数排序类 
 * @author tanghui 
 * @date 2013-8-10 下午8:48:47 
 *
 */
public class OrderBy {
    
    private List<Order> orders = new ArrayList<Order>();
    
    public void add(Order order) {
        orders.add(order);
    }
    
    public void build(Criteria criteria) {
        for(Order order : orders) {
            criteria.addOrder(order);
        }
    }
}

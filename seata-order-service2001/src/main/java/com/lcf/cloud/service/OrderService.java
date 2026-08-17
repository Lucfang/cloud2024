package com.lcf.cloud.service;

import com.lcf.cloud.entities.Order;

public interface OrderService {

    /**
     * 创建订单
     * @param order
     */
    void create(Order order);
}

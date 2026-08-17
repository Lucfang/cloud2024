package com.lcf.cloud.service.impl;

import com.lcf.cloud.apis.AccountFeignApi;
import com.lcf.cloud.apis.StorageFeignApi;
import com.lcf.cloud.entities.Order;
import com.lcf.cloud.mapper.OrderMapper;
import com.lcf.cloud.service.OrderService;
import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.binding.MapperProxy;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private StorageFeignApi storageFeignApi;

    @Resource
    private AccountFeignApi  accountFeignApi;

    @Override
    @GlobalTransactional(name="lcf-create-order",rollbackFor =  Exception.class)//AT
    public void create(Order order) {
        //xid全局事务的检查
        String xid = RootContext.getXID();
        //新建订单
        log.info("===========开始新建订单：" + "\t" + "xid:" + xid);
        //订单创建时默认初始订单状态是零
        order.setStatus(0);
        int result = orderMapper.insertSelective(order);
        //插入订单成功后获得插入mysql的实体对象
        Order orderFromDb = null;

        if (result > 0) {
            //从mysql里面查出刚插入的记录
            orderFromDb = orderMapper.selectOne(order);
            log.info("------> 新建订单成功，orderFromDb info:" + orderFromDb);
            // 扣减库存
            log.info("===========订单微服务开始调用storage库存，做扣减count");
            storageFeignApi.decrease(orderFromDb.getProductId(),orderFromDb.getCount());
            log.info("===========订单微服务结束调用storage库存，做扣减完成");
            // 扣减账户余额
            log.info("===========订单微服务开始调用account账号，做扣减money");
            accountFeignApi.decrease(orderFromDb.getUserId(),orderFromDb.getMoney());
            log.info("===========订单微服务结束调用account账号，做扣减完成");
            //修改订单状态，将订单状态从0修改为1，表示已完结
            log.info("----->修改订单状态");
            Example whereCondition = new Example(Order.class);
            Example.Criteria criteria = whereCondition.createCriteria();
            criteria.andEqualTo("userId", orderFromDb.getUserId());
            criteria.andEqualTo("status", 1);
            int updateResult = orderMapper.updateByExampleSelective(orderFromDb, whereCondition);
            log.info("----->修改订单状态完成" + "\t" + "updateResult:" + updateResult);
            log.info("----->orderFromDb info:" + orderFromDb);
        }

        log.info("==============结束新建订单：" + "\t" + "xid:" + xid);
    }
}

package com.henry.mallorder.order.mapper;

import com.henry.mallorder.order.entity.OrderInfo;
import com.henry.mallorder.order.entity.OrderItem;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OrderMapper {

    int insertOrderInfo(OrderInfo orderInfo);

    int insertOrderItem(OrderItem orderItem);

    OrderInfo selectOrderInfoByOrderNo(@Param("orderNo") String orderNo);
}

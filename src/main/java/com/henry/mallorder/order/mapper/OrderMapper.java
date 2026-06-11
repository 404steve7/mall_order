package com.henry.mallorder.order.mapper;

import com.henry.mallorder.order.entity.OrderInfo;
import com.henry.mallorder.order.entity.OrderItem;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper {

    int insertOrderInfo(OrderInfo orderInfo);

    int insertOrderItem(OrderItem orderItem);

    int updateOrderStatus(@Param("orderNo") String orderNo, @Param("status") Integer status);

    OrderInfo selectOrderInfoByOrderNo(@Param("orderNo") String orderNo);

    List<OrderItem> selectOrderItemsByOrderNo(@Param("orderNo") String orderNo);
}

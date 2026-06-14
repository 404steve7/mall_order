package com.henry.mallorder.order.service;

import com.henry.mallorder.order.dto.CreateOrderRequest;
import com.henry.mallorder.order.entity.OrderInfo;
import com.henry.mallorder.order.entity.OrderItem;
import com.henry.mallorder.order.mapper.OrderMapper;
import com.henry.mallorder.order.vo.OrderDetailVO;
import com.henry.mallorder.product.entity.Product;
import com.henry.mallorder.product.mapper.ProductMapper;
import com.henry.mallorder.common.exception.BusinessException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Service
public class OrderService {

    private final OrderMapper orderMapper;

    private final ProductMapper productMapper;

    public OrderService(OrderMapper orderMapper, ProductMapper productMapper) {
        this.orderMapper = orderMapper;
        this.productMapper = productMapper;
    }

    @Transactional
    public String createOrder(CreateOrderRequest request) {
        Product product = productMapper.selectById(request.getProductId());
        if (product == null) {
            throw new BusinessException(4001,"商品不存在");
        }

        BigDecimal totalAmount = product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));

        int reduceResult = productMapper.reduceStock(request.getProductId(), request.getQuantity());
        if (reduceResult == 0) {
            throw new BusinessException(4002,"库存不足或商品已下架");
        }

        String orderNo = generateOrderNo();

        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderNo(orderNo);
        orderInfo.setUserId(request.getUserId());
        orderInfo.setTotalAmount(totalAmount);
        orderInfo.setStatus(1);
        orderMapper.insertOrderInfo(orderInfo);

        OrderItem orderItem = new OrderItem();
        orderItem.setOrderNo(orderNo);
        orderItem.setProductId(product.getId());
        orderItem.setProductName(product.getProductName());
        orderItem.setProductPrice(product.getPrice());
        orderItem.setQuantity(request.getQuantity());
        orderItem.setTotalAmount(totalAmount);
        orderMapper.insertOrderItem(orderItem);

        return orderNo;
    }

    public OrderDetailVO getOrderDetailByOrderNo(String orderNo) {
        OrderInfo orderInfo = orderMapper.selectOrderInfoByOrderNo(orderNo);
        if (orderInfo == null) {
            throw new BusinessException(4003,"订单不存在");
        }

        List<OrderItem> items = orderMapper.selectOrderItemsByOrderNo(orderNo);

        OrderDetailVO detailVO = new OrderDetailVO();
        detailVO.setOrderNo(orderInfo.getOrderNo());
        detailVO.setUserId(orderInfo.getUserId());
        detailVO.setTotalAmount(orderInfo.getTotalAmount());
        detailVO.setStatus(orderInfo.getStatus());
        detailVO.setCreateTime(orderInfo.getCreateTime());
        detailVO.setUpdateTime(orderInfo.getUpdateTime());
        detailVO.setItems(items);

        return detailVO;
    }

    @Transactional
    public boolean cancelOrder(String orderNo) {
        OrderInfo orderInfo = orderMapper.selectOrderInfoByOrderNo(orderNo);
        if (orderInfo == null) {
            throw new BusinessException(4003,"订单不存在");
        }

        if (Integer.valueOf(2).equals(orderInfo.getStatus())) {
            throw new BusinessException(4004,"订单已取消");
        }

        List<OrderItem> items = orderMapper.selectOrderItemsByOrderNo(orderNo);

        int updateResult = orderMapper.updateOrderStatus(orderNo, 2);
        if (updateResult == 0) {
            throw new BusinessException(4005,"订单取消失败");
        }

        for (OrderItem item : items) {
            int increaseResult = productMapper.increaseStock(item.getProductId(), item.getQuantity());
            if (increaseResult == 0) {
                throw new BusinessException(4006,"恢复库存失败");
            }
        }

        return true;
    }

    private String generateOrderNo() {
        String timePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        return "OD" + timePart;
    }
}

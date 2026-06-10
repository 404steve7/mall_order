package com.henry.mallorder.order.service;

import com.henry.mallorder.order.dto.CreateOrderRequest;
import com.henry.mallorder.order.entity.OrderInfo;
import com.henry.mallorder.order.entity.OrderItem;
import com.henry.mallorder.order.mapper.OrderMapper;
import com.henry.mallorder.product.entity.Product;
import com.henry.mallorder.product.mapper.ProductMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


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
            throw new RuntimeException("商品不存在");
        }

        BigDecimal totalAmount = product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));

        int reduceResult = productMapper.reduceStock(request.getProductId(), request.getQuantity());
        if (reduceResult == 0) {
            throw new RuntimeException("库存不足或商品已下架");
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

    public OrderInfo getOrderByOrderNo(String orderNo) {
        return orderMapper.selectOrderInfoByOrderNo(orderNo);
    }

    private String generateOrderNo() {
        String timePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        return "OD" + timePart;
    }
}

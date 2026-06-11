package com.henry.mallorder.order.controller;

import com.henry.mallorder.order.dto.CreateOrderRequest;
import com.henry.mallorder.order.service.OrderService;
import com.henry.mallorder.order.vo.OrderDetailVO;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/create")
    public String createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return orderService.createOrder(request);
    }

    @GetMapping("/{orderNo}")
    public OrderDetailVO getOrderInfo(@PathVariable("orderNo") String orderNo) {
        return orderService.getOrderDetailByOrderNo(orderNo);
    }

    @PostMapping("/cancel/{orderNo}")
    public Boolean cancelOrder(@PathVariable("orderNo") String orderNo) {
        return orderService.cancelOrder(orderNo);
    }
}

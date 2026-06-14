package com.henry.mallorder.order.controller;

import com.henry.mallorder.order.dto.CreateOrderRequest;
import com.henry.mallorder.order.service.OrderService;
import com.henry.mallorder.order.vo.OrderDetailVO;
import com.henry.mallorder.common.Result;

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
    public Result<String> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return Result.success(orderService.createOrder(request));
    }

    @GetMapping("/{orderNo}")
    public Result<OrderDetailVO> getOrderInfo(@PathVariable("orderNo") String orderNo) {
        return Result.success(orderService.getOrderDetailByOrderNo(orderNo));
    }

    @PostMapping("/cancel/{orderNo}")
    public Result<Boolean> cancelOrder(@PathVariable("orderNo") String orderNo) {
        return Result.success(orderService.cancelOrder(orderNo));
    }
}

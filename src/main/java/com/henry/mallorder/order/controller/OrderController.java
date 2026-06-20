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
    public Result<String> createOrder(
            @RequestAttribute("currentUserId") Long userId,
            @Valid @RequestBody CreateOrderRequest request) {
        return Result.success(orderService.createOrder(userId,request));
    }

    @GetMapping("/{orderNo}")
    public Result<OrderDetailVO> getOrderInfo(
            @RequestAttribute("currentUserId") Long userId,
            @PathVariable("orderNo") String orderNo) {
        return Result.success(orderService.getOrderDetailByOrderNo(userId,orderNo));
    }

    @PostMapping("/cancel/{orderNo}")
    public Result<Boolean> cancelOrder(
            @RequestAttribute("currentUserId") Long userId,
            @PathVariable("orderNo") String orderNo) {
        return Result.success(orderService.cancelOrder(userId,orderNo));
    }
}

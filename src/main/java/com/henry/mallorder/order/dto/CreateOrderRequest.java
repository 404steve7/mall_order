package com.henry.mallorder.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CreateOrderRequest {

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotNull(message = "商品ID不能为空")
    private Long productId;

    @NotNull(message = "购买数量不能为空")
    @Min(value = 1,message = "购买数量必须大于0")
    private Integer quantity;

    public Long getUserId() {
        return userId;
    }
    public void setUseId(Long userId) {
        this.userId = this.userId;
    }

    public Long getProductId() {
        return productId;
    }
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

}

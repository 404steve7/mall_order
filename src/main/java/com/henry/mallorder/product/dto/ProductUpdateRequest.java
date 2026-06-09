package com.henry.mallorder.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class ProductUpdateRequest {

    @NotBlank(message = "商品名称不能为空")
    private String productName;

    @NotNull(message = "商品价格不能为空")
    @DecimalMin(value = "0.01",message = "商品价格必须大于0")
    private BigDecimal price;

    @NotNull(message = "库存不能为空")
    @Min(value = 0,message = "库存不能小于0")
    private Integer stock;

    @NotNull(message = "商品状态不能为空")
    @Min(value = 0,message = "商品状态只能是0或1")
    @Max(value = 1,message = "商品状态只能是0或1")
    private Integer status;

    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getPrice() {
        return price;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }
    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getStatus() {
        return status;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }
}

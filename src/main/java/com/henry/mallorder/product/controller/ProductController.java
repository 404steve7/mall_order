package com.henry.mallorder.product.controller;

import com.henry.mallorder.product.dto.ProductCreateRequest;
import com.henry.mallorder.product.dto.ProductUpdateRequest;
import com.henry.mallorder.product.entity.Product;
import com.henry.mallorder.product.service.ProductService;
import com.henry.mallorder.common.Result;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    @PostMapping("/add")
    public Long addProduct(@Valid @RequestBody ProductCreateRequest request){
        return productService.createProduct(request);
    }

    @GetMapping("/list")
    public Result<List<Product>> listProducts(){
        return Result.success(productService.listProducts());
    }

    @GetMapping("/{id}")
    public Product getProduct(@PathVariable("id") Long id){
        return productService.getProductById(id);
    }

    @PutMapping("/{id}")
    public Boolean updateProduct(@PathVariable("id") Long id, @Valid @RequestBody ProductUpdateRequest request){
        return productService.updateProduct(id, request);
    }
}

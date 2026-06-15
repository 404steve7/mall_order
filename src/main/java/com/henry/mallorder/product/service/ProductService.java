package com.henry.mallorder.product.service;

import com.henry.mallorder.product.dto.ProductCreateRequest;
import com.henry.mallorder.product.dto.ProductUpdateRequest;
import com.henry.mallorder.product.entity.Product;
import com.henry.mallorder.product.mapper.ProductMapper;
import com.henry.mallorder.common.exception.BusinessException;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductMapper productMapper;

    public ProductService(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    public Long createProduct(ProductCreateRequest request){
        Product product = new Product();
        product.setProductName(request.getProductName());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setStatus(1);

        productMapper.insert(product);

        return product.getId();
    }

    public List<Product> listProducts(){
        return productMapper.selectList();
    }

    public Product getProductById(Long id){
        Product product = productMapper.selectById(id);
        if(product == null){
            throw new BusinessException(4001,"商品不存在");
        }
        return product;
    }

    public boolean updateProduct(Long id, ProductUpdateRequest request){
        Product product = new Product();
        product.setId(id);
        product.setProductName(request.getProductName());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setStatus(request.getStatus());

        return productMapper.updateById(product) > 0;
    }
}

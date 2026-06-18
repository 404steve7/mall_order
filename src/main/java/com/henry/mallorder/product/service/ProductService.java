package com.henry.mallorder.product.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.henry.mallorder.product.dto.ProductCreateRequest;
import com.henry.mallorder.product.dto.ProductUpdateRequest;
import com.henry.mallorder.product.entity.Product;
import com.henry.mallorder.product.mapper.ProductMapper;
import com.henry.mallorder.common.exception.BusinessException;

import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.time.Duration;

@Service
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);
    private static final String PRODUCT_CACHE_KEY_PREFIX = "product:detail:";
    private static final Duration PRODUCT_CACHE_TTL = Duration.ofMinutes(10);

    private final ProductMapper productMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    public ProductService(ProductMapper productMapper,
                          StringRedisTemplate stringRedisTemplate,
                          ObjectMapper objectMapper) {
        this.productMapper = productMapper;
        this.stringRedisTemplate = stringRedisTemplate;
        this.objectMapper = objectMapper;
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

        String cacheKey = PRODUCT_CACHE_KEY_PREFIX + id;
        String productJson = stringRedisTemplate.opsForValue().get(cacheKey);

        if(productJson != null){
            Product cacheProduct = readProductFromCache(cacheKey,productJson);
            if(cacheProduct != null){
                log.info("product cache hit, id={}",id);
                return cacheProduct;
            }
        }

        log.info("product cache miss, id={}",id);

        Product product = productMapper.selectById(id);
        if(product == null){
            throw new BusinessException(4001,"商品不存在");
        }

        writeProductToCache(cacheKey,product);
        return product;
    }

    public boolean updateProduct(Long id, ProductUpdateRequest request){
        Product product = new Product();
        product.setId(id);
        product.setProductName(request.getProductName());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setStatus(request.getStatus());

        boolean updated = productMapper.updateById(product) > 0;
        if(updated){
            stringRedisTemplate.delete(PRODUCT_CACHE_KEY_PREFIX + id);
        }
        return updated;
    }

    private Product readProductFromCache(String cacheKey,String productJson){
        try{
            return objectMapper.readValue(productJson, Product.class);
        }catch (JsonProcessingException e){
            log.warn("product cache parse faild, key={}",cacheKey,e);
            stringRedisTemplate.delete(cacheKey);
            return null;
        }
    }

    private void writeProductToCache(String cacheKey,Product product){
        try {
            String productJson = objectMapper.writeValueAsString(product);
            stringRedisTemplate.opsForValue().set(cacheKey,productJson,PRODUCT_CACHE_TTL);
        }catch (JsonProcessingException e){
            log.warn("product cache write faild, key={}",cacheKey,e);
        }
    }
}

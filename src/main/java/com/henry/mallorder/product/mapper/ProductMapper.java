package com.henry.mallorder.product.mapper;

import com.henry.mallorder.product.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductMapper {

    int insert(Product product);

    List<Product> selectList();

    Product selectById(@Param("id") Long id);

    int updateById(Product product);

}

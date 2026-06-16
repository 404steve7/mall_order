package com.henry.mallorder.user.mapper;

import com.henry.mallorder.user.entity.User;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    int insert(User user);

    User selectByUsername(@Param("username") String username);

    User selectById(@Param("id") Long id);
}

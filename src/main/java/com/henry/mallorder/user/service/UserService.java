package com.henry.mallorder.user.service;

import com.henry.mallorder.common.exception.BusinessException;
import com.henry.mallorder.user.dto.UserLoginRequest;
import com.henry.mallorder.user.dto.UserRegisterRequest;
import com.henry.mallorder.user.entity.User;
import com.henry.mallorder.user.mapper.UserMapper;

import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService {

    private  final UserMapper userMapper;

    private  final ConcurrentHashMap<String,Long> tokenStore = new ConcurrentHashMap<>();

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public Long register(UserRegisterRequest request){
        User existUser = userMapper.selectByUsername(request.getUsername());
        if(existUser != null){
            throw new BusinessException(4012,"用户名已存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setNickname(request.getNickname());
        user.setStatus(1);

        userMapper.insert(user);

        return user.getId();
    }

    public String login(UserLoginRequest request){
        User user = userMapper.selectByUsername(request.getUsername());
        if(user == null|| !user.getPassword().equals(request.getPassword())){
            throw new BusinessException(4011,"用户名或密码错误");
        }

        String token = UUID.randomUUID().toString();
        tokenStore.put(token, user.getId());

        return token;
    }

    public User getCurrentUser(String token){
        Long userId = tokenStore.get(token);
        if (userId == null) {
            throw  new BusinessException(4010,"未登录");
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(4010,"未登录");
        }

        user.setPassword(null);

        return user;
    }

}

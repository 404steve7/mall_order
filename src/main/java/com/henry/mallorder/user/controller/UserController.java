package com.henry.mallorder.user.controller;

import com.henry.mallorder.common.Result;
import com.henry.mallorder.user.dto.UserLoginRequest;
import com.henry.mallorder.user.dto.UserRegisterRequest;
import com.henry.mallorder.user.entity.User;
import com.henry.mallorder.user.service.UserService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public Result<Long> register(@Valid @RequestBody UserRegisterRequest request){
        return Result.success(userService.register(request));
    }

    @PostMapping("/login")
    public Result<String> login(@Valid @RequestBody UserLoginRequest request){
        return Result.success(userService.login(request));
    }

    @GetMapping("/me")
    public Result<User> me(@RequestHeader(value = "X-Token",required = false)String token){
        return Result.success(userService.getCurrentUser(token));
    }
}

package com.henry.mallorder.user.dto;

import jakarta.validation.constraints.NotBlank;

public class UserLoginRequest {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    public void setUsername(String username) {
        this.username = username;
    }
    public String getUsername() {
       return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String getPassword() {
       return password;
    }
}

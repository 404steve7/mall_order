package com.henry.mallorder.common.exception;

import com.henry.mallorder.common.Result;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value=BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e){
        return Result.fail(e.getCode(),e.getMessage());
    }

    @ExceptionHandler(value=Exception.class)
    public Result<Void> handleException(Exception e){
        return Result.fail(5000,"系统异常");
    }
}

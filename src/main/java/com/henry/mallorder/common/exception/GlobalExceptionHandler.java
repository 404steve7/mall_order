package com.henry.mallorder.common.exception;

import com.henry.mallorder.common.Result;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value=BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e){
        return Result.fail(e.getCode(),e.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Result<Void> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e){
        return Result.fail(4000,"参数错误");
    }

    @ExceptionHandler(value=Exception.class)
    public Result<Void> handleException(Exception e){
        return Result.fail(5000,"系统异常");
    }
}

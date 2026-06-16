package com.henry.mallorder.common.exception;

import com.henry.mallorder.common.Result;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        FieldError fieldError = e.getBindingResult().getFieldError();

        if(fieldError==null){
            return Result.fail(4000,"参数错误");
        }
        return Result.fail(4000,fieldError.getDefaultMessage());
    }

    @ExceptionHandler(value=Exception.class)
    public Result<Void> handleException(Exception e){
        return Result.fail(5000,"系统异常");
    }
}

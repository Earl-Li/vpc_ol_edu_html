package com.atlisheng.servicebase.handler;

import com.atlisheng.commonutils.response.ResponseData;
import com.atlisheng.servicebase.exceptions.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)//指定出现哪种异常的情况下执行该方法
    @ResponseBody
    public ResponseData handleException(Exception e){
        e.printStackTrace();
        log.error(e.getMessage());
        return ResponseData.responseErrorCall().message("服务器异常,请联系管理员");
    }

    @ExceptionHandler(ArithmeticException.class)//特定异常执行该方法
    @ResponseBody
    public ResponseData handleException(ArithmeticException e){
        e.printStackTrace();
        log.error(e.getMessage());
        return ResponseData.responseErrorCall().message("数学运算异常,请联系管理员");
    }

    @ExceptionHandler(CustomException.class)//自定义异常执行方法
    @ResponseBody
    public ResponseData handleException(CustomException e){
        e.printStackTrace();
        log.error(e.getMessage());
        return ResponseData.responseErrorCall().code(e.getCode()).message(e.getMessage());
    }
}

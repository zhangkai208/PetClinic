package com.zk.petclinic.exception;

import com.zk.petclinic.util.ResultUtil;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public <T>ResultUtil<T> handleException(Exception e){
        e.printStackTrace();
        return ResultUtil.fail(StringUtils.hasLength(e.getMessage()) ? e.getMessage() : "操作失败");
    }
}

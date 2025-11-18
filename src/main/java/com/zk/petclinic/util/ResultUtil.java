package com.zk.petclinic.util;

import lombok.Data;

@Data
public class ResultUtil<T> {
    private Integer code;
    private String message;
    private T data;

    public static <T> ResultUtil<T> success(T data) {
        ResultUtil<T> resultUtil = new ResultUtil<>();
        resultUtil.setCode(200);
        resultUtil.setMessage("操作成功");
        resultUtil.setData(data);
        return resultUtil;
    }
    public static <T> ResultUtil<T> success() {
        ResultUtil<T> resultUtil = new ResultUtil<>();
        resultUtil.setCode(200);
        resultUtil.setMessage("操作成功");
        return resultUtil;
    }
    public static <T> ResultUtil<T> success(String message){
        ResultUtil<T> resultUtil = new ResultUtil<>();
        resultUtil.setCode(200);
        resultUtil.setMessage(message);
        return resultUtil;
    }
    public static <T> ResultUtil<T> fail(String message) {
        ResultUtil<T> resultUtil = new ResultUtil<>();
        resultUtil.setCode(500);
        resultUtil.setMessage(message);
        return resultUtil;
    }
    public static <T> ResultUtil<T> fail(Integer code, String message) {
        ResultUtil<T> resultUtil = new ResultUtil<>();
        resultUtil.setCode(code);
        resultUtil.setMessage(message);
        return resultUtil;
    }
}

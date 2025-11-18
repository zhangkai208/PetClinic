package com.zk.petclinic.util;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;

import java.util.Map;

public class JWTUtil {
    private static final String KEY = "zk";

    //生成token
    private static String genToken(Map<String, Object> claims) {
        // 使用 HS256 算法签名
        JWTSigner signer = JWTSignerUtil.hs256(KEY.getBytes());
        return JWT.create()
                .addPayloads(claims) // 添加自定义声明
                .setSigner(signer)    // 设置签名器
                .sign();              // 生成 Token
    }

    // 验证 JWT Token
    public static boolean verifyToken(String token) {
        try {
            JWTSigner signer = JWTSignerUtil.hs256(KEY.getBytes());
            JWT jwt = JWT.of(token).setSigner(signer);
            return jwt.verify(); // 验证签名
        } catch (Exception e) {
            return false;
        }
    }

    // 解析 JWT Token
    public static Map<String, Object> parseToken(String token) {
        JWT jwt = JWT.of(token);
        return jwt.getPayloads(); // 返回声明部分
    }
}
package com.zk.petclinic.util;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;

import java.util.Map;

public class JWTUtil {
    private static final String KEY = "zk";

    //生成token
    public static String genToken(Map<String, Object> claims) {
        // 使用 HS256 算法签名
        JWTSigner signer = JWTSignerUtil.hs256(KEY.getBytes());
        return JWT.create()
                .addPayloads(claims) // 添加自定义声明
                .setSigner(signer)    // 设置签名器
                .sign();              // 生成 Token
    }
    
    /**
     * 生成登录token的便捷方法
     * @param username 用户名
     * @param userId 用户ID
     * @param roleType 角色类型
     * @return token字符串
     */
    public static String generateLoginToken(String username, Long userId, Integer roleType) {
        Map<String, Object> claims = new java.util.HashMap<>();
        claims.put("username", username);
        claims.put("userId", userId);
        claims.put("roleType", roleType);
        claims.put("exp", System.currentTimeMillis() / 1000 + 86400); // 24小时过期
        return genToken(claims);
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
    
    /**
     * 从token中获取用户名
     */
    public static String getUsernameFromToken(String token) {
        try {
            Map<String, Object> payloads = parseToken(token);
            if (payloads == null) {
                return null;
            }
            return (String) payloads.get("username");
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 从token中获取用户ID
     */
    public static Long getUserIdFromToken(String token) {
        try {
            Map<String, Object> payloads = parseToken(token);
            if (payloads == null) {
                return null;
            }
            Object userId = payloads.get("Id");
            if (userId == null) {
                return null;
            }
            if (userId instanceof Integer) {
                return ((Integer) userId).longValue();
            }
            return (Long) userId;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 从token中获取角色类型
     */
    public static Integer getRoleTypeFromToken(String token) {
        try {
            Map<String, Object> payloads = parseToken(token);
            if (payloads == null) {
                return null;
            }
            return (Integer) payloads.get("roleType");
        } catch (Exception e) {
            return null;
        }
    }
}
package com.zk.petclinic.domain.dto;

import lombok.Data;

/**
 * 登录结果DTO
 * @author 张恺
 */
@Data
public class LoginResult {
    /**
     * 是否登录成功
     */
    private boolean success;
    
    /**
     * 错误信息（登录失败时使用）
     */
    private String message;
    
    /**
     * JWT令牌
     */
    private String token;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 昵称
     */
    private String nickname;
    
    /**
     * 角色类型
     */
    private Integer roleType;
    
    /**
     * 头像
     */
    private String avatar;

    /**
     * 创建失败结果
     */
    public static LoginResult fail(String message) {
        LoginResult result = new LoginResult();
        result.setSuccess(false);
        result.setMessage(message);
        return result;
    }

    /**
     * 创建成功结果
     */
    public static LoginResult success(String token, Long userId, String username, 
                                       String nickname, Integer roleType, String avatar) {
        LoginResult result = new LoginResult();
        result.setSuccess(true);
        result.setToken(token);
        result.setUserId(userId);
        result.setUsername(username);
        result.setNickname(nickname);
        result.setRoleType(roleType);
        result.setAvatar(avatar);
        return result;
    }
}

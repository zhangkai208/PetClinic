package com.zk.petclinic.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zk.petclinic.util.JWTUtil;
import com.zk.petclinic.util.ResultUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT认证过滤器
 * 从请求头中获取token并验证，设置到Spring Security上下文中
 * @author 张恺
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        // 从请求头中获取token
        String token = getTokenFromRequest(request);

        // 如果请求需要认证但没有token，让Spring Security处理（会返回401）
        if (!StringUtils.hasText(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 验证token
            if (!JWTUtil.verifyToken(token)) {
                // Token无效或已过期，返回统一错误响应
                writeErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token无效或已过期，请重新登录");
                return;
            }

            // 从token中获取用户信息
            String username = JWTUtil.getUsernameFromToken(token);
            Integer roleType = JWTUtil.getRoleTypeFromToken(token);
            
            // 如果username为空，返回错误
            if (username == null) {
                writeErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token解析失败，请重新登录");
                return;
            }

            // 构建角色权限（Spring Security需要ROLE_前缀）
            // 这里根据roleType判断角色类型：1-宠物主人，2-服务商，3-管理员
            String role = "ROLE_USER";
            if (roleType != null) {
                switch (roleType) {
                    case 1:
                        role = "ROLE_OWNER";  // 宠物主人
                        break;
                    case 2:
                        role = "ROLE_PROVIDER";  // 服务商
                        break;
                    case 3:
                        role = "ROLE_ADMIN";  // 管理员
                        break;
                }
            }

            // 创建认证对象
            UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(
                    username, 
                    null, 
                    Collections.singletonList(new SimpleGrantedAuthority(role))
                );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // 设置到Security上下文
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // 认证成功，继续执行过滤器链
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            
            writeErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "认证过程发生异常");
        }
    }

    /**
     * 写入错误响应（使用ResultUtil统一格式）
     */
    private void writeErrorResponse(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json;charset=UTF-8");
        ResultUtil<Object> result = ResultUtil.fail(statusCode, message);
        objectMapper.writeValue(response.getWriter(), result);
    }

    /**
     * 从请求头中获取JWT Token
     * 格式：Authorization: Bearer <token>
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}


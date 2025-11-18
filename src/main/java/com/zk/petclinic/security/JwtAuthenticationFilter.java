package com.zk.petclinic.security;

import com.zk.petclinic.util.JWTUtil;
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

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // 从请求头中获取token
            String token = getTokenFromRequest(request);

            // 验证token
            if (StringUtils.hasText(token) && JWTUtil.verifyToken(token)) {
                // 从token中获取用户信息
                String username = JWTUtil.getUsernameFromToken(token);
                Integer roleType = JWTUtil.getRoleTypeFromToken(token);
                
                // 如果username为空，跳过认证
                if (username == null) {
                    filterChain.doFilter(request, response);
                    return;
                }

                // 构建角色权限（Spring Security需要ROLE_前缀）
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
            }
        } catch (Exception e) {
            logger.error("JWT认证失败", e);
        }

        filterChain.doFilter(request, response);
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


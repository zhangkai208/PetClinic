package com.zk.petclinic.config;

import com.zk.petclinic.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Spring Security配置类
 * @author 张恺
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Security过滤器链配置
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 禁用CSRF（因为使用JWT，不需要CSRF保护）
            .csrf(csrf -> csrf.disable())
            // 配置CORS（跨域）
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // 无状态会话（使用JWT，不需要Session）
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 配置请求授权（注意：规则按顺序匹配，先匹配到的规则会生效）
            .authorizeHttpRequests(auth -> auth
                // 1. 公开接口（不需要认证）- 必须放在最前面
                .requestMatchers(
                    "/sysUser/login",      // 登录接口
                    "/sysUser/register",   // 注册接口
                    "/error"               // 错误页面
                ).permitAll()
                // 2. 用户常用接口（已登录即可访问）
                .requestMatchers(
                    "/sysUser/logout",     // 退出登录
                    "/sysUser/page",       // 分页查询用户
                    "/sysUser/{id}"        // 根据ID查询用户
                ).authenticated()
                // 3. 用户管理接口（需要ADMIN角色）
                .requestMatchers(
                    "/sysUser/create",     // 创建用户
                    "/sysUser/{id}",       // 更新/删除用户（PUT/DELETE）
                    "/sysUser"             // 批量删除
                ).hasRole("ADMIN")
                // 4. 管理员接口（需要ADMIN角色）
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // 5. 服务商接口（需要PROVIDER或ADMIN角色）
                .requestMatchers("/provider/**").hasAnyRole("PROVIDER", "ADMIN")
                // 6. 其他接口需要认证（登录后即可访问，不限制角色）
                .anyRequest().authenticated()
            )
            // 添加JWT过滤器（在UsernamePasswordAuthenticationFilter之前）
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * CORS跨域配置
 * 该方法配置跨域资源共享(CORS)规则，允许前端应用访问后端API
 * @return CorsConfigurationSource 跨域配置源对象
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
    // 创建CORS配置对象
        CorsConfiguration configuration = new CorsConfiguration();
        // 允许所有来源（生产环境应配置具体域名）
    // 使用setAllowedOriginPatterns而不是setAllowedOrigins，以支持通配符
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        // 允许的HTTP方法，包括GET、POST、PUT、DELETE和OPTIONS
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // 允许的请求头
        configuration.setAllowedHeaders(Arrays.asList("*"));
        // 允许携带凭证（注意：使用setAllowedOriginPatterns时才能设置allowCredentials为true）
        configuration.setAllowCredentials(true);
        // 预检请求的缓存时间
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}


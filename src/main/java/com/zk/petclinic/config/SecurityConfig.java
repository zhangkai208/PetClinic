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
            // 配置请求授权
            .authorizeHttpRequests(auth -> auth
                // 公开接口（不需要认证）
                .requestMatchers(
                    "/sysUser/login",      // 登录接口
                    "/sysUser/register",   // 注册接口（如果后续添加）
                    "/error"               // 错误页面
                ).permitAll()
                // 管理员接口（需要ADMIN角色）
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // 服务商接口（需要PROVIDER或ADMIN角色）
                .requestMatchers("/provider/**").hasAnyRole("PROVIDER", "ADMIN")
                // 其他接口需要认证（登录后即可访问）
                .anyRequest().authenticated()
            )
            // 添加JWT过滤器（在UsernamePasswordAuthenticationFilter之前）
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * CORS跨域配置
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 允许所有来源（生产环境应配置具体域名）
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        // 允许的HTTP方法
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


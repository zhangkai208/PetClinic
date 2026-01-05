package com.zk.petclinic.config;

import com.zk.petclinic.security.JwtAuthenticationFilter;
import jakarta.servlet.DispatcherType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
                // 0. 允许异步分发请求（SSE流式响应完成后的async dispatch）
                .dispatcherTypeMatchers(DispatcherType.ASYNC).permitAll()
                
                // ========== 1. 公开接口（无需认证） ==========
                .requestMatchers(
                    "/sysUser/login",      // 登录接口
                    "/sysUser/register",   // 注册接口
                    "/error"               // 错误页面
                ).permitAll()
                
                // ========== 2. 用户模块 ==========
                // 用户退出、上传头像（已登录即可）
                .requestMatchers(HttpMethod.POST, "/sysUser/logout").authenticated()
                .requestMatchers(HttpMethod.POST, "/sysUser/upload").authenticated()
                // 查看用户信息（已登录即可）
                .requestMatchers(HttpMethod.GET, "/sysUser/{id}").authenticated()
                //修改用户信息（已登录即可）
                .requestMatchers(HttpMethod.PUT, "/sysUser/{id}").authenticated()
                // 用户管理（仅ADMIN）
                .requestMatchers(HttpMethod.GET, "/sysUser/page").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/sysUser/create").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/sysUser/{id}").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/sysUser/{id}").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/sysUser").hasRole("ADMIN")
                
                // ========== 3. 宠物模块 ==========
                // 查看全部宠物列表（服务商和管理员可查看）
                .requestMatchers(HttpMethod.GET, "/pet/list").hasAnyRole("PROVIDER", "ADMIN")
                // 其他宠物管理接口（宠物主人OWNER和管理员ADMIN可访问）
                .requestMatchers("/pet/**").hasAnyRole("OWNER", "ADMIN")
                
                // ========== 4. AI聊天模块 ==========
                // AI对话（所有已登录用户可用）
                .requestMatchers("/ChatConversation/**").authenticated()
                .requestMatchers("/chatMessage/**").authenticated()
                
                // ========== 5. 预约模块 ==========
                // 预约功能（都可用）
                .requestMatchers("/appointment/**").hasAnyRole("OWNER", "PROVIDER", "ADMIN")
                
                // ========== 6. 健康记录模块 ==========
                // 健康记录查看（主人、服务商、管理员都可读）
                .requestMatchers(HttpMethod.GET, "/healthRecord/**").hasAnyRole("OWNER", "PROVIDER", "ADMIN")
                // 健康记录增删改（仅服务商和管理员）
                .requestMatchers("/healthRecord/**").hasAnyRole("PROVIDER", "ADMIN")
                
                // ========== 7. 服务商模块 ==========
                // 服务商列表查看（主人、服务商、管理员都可读，用于预约选择）
                .requestMatchers(HttpMethod.GET, "/serviceProviders/page").hasAnyRole("OWNER", "PROVIDER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/serviceProviders/list").hasAnyRole("OWNER", "PROVIDER", "ADMIN")
                // 服务商增删改（仅服务商和管理员）
                .requestMatchers("/serviceProviders/**").hasAnyRole("PROVIDER", "ADMIN")
                
                // ========== 8. 通用管理接口 ==========
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/provider/**").hasAnyRole("PROVIDER", "ADMIN")
                
                // ========== 9. 默认规则 ==========
                // 其他接口需要认证（登录后即可访问，不限制角色）
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


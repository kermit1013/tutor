package com.gtalent.tutor.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//向spring boot標示該類別為設定檔，Spring將會在啟動時讀取
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;

    @Autowired
    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                //stateless jwt 用不上csrf
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authz -> authz
                                .requestMatchers("/jwt/**").permitAll()
                                .requestMatchers("/session/**").permitAll()
                                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**","/swagger-ui.html").permitAll()
                                .requestMatchers(HttpMethod.GET,"/products/**").permitAll()
                                .requestMatchers(HttpMethod.GET,"/v2/users/**").permitAll()
                                .requestMatchers(HttpMethod.DELETE, "/v2/users/**").hasRole("ADMIN")
                                //Spring Security 將會自動加上 ROLE_ -> ROLE_ADMIN
                                .requestMatchers(HttpMethod.GET,"/suppliers/**").permitAll()
                                .requestMatchers("/suppliers/**").hasRole("SUPPLIER")
                                .anyRequest().authenticated()
                )
                // restful 核心： 伺服器無法從session中獲得使用者資訊
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 確保 spring security 進行UsernamePassword的驗證以前，我們的jwtAuthFilter會先被執行
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

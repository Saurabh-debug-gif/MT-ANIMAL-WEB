package com.poultry.shop.config;

import com.poultry.shop.security.CustomLoginSuccessHandler;
import com.poultry.shop.service.CustomOAuthUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Autowired
    private CustomOAuthUserService customOAuthUserService;

    @Autowired
    private CustomLoginSuccessHandler customLoginSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/products",
                                "/products/**",
                                "/checkout/**",
                                "/uploads/**",
                                "/images/**",
                                "/css/**",
                                "/js/**",
                                "/pdf/**",
                                "/ai-chat",
                                "/error",
                                "/oauth2/**",
                                "/login/**",
                                "/login/oauth2/**"
                        ).permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/cart/**", "/checkout/**").authenticated()
                        .anyRequest().permitAll()   // 🔥 TEMPORARY: allow everything else
                )
                .oauth2Login(oauth -> oauth
                        .loginPage("/login")
                        .userInfoEndpoint(userInfo ->
                                userInfo.userService(customOAuthUserService)
                        )
                        .successHandler(customLoginSuccessHandler)
                )
                .logout(logout -> logout.logoutSuccessUrl("/").permitAll());

        return http.build();
    }
}
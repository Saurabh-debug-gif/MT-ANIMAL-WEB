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
                                "/login/oauth2/**",
                                "/sitemap.xml",
                                "/google491b0d2ab3dfd7d4.html"
                        ).permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/cart/**", "/checkout/**", "/my-orders").authenticated()
                        .anyRequest().permitAll()
                )
                .oauth2Login(oauth -> oauth
                        .loginPage("/login")
                        .userInfoEndpoint(userInfo ->
                                userInfo.userService(customOAuthUserService)
                        )
                        .successHandler(customLoginSuccessHandler)
                )
                .logout(logout -> logout
                        .logoutUrl("/admin/logout")           // ✅ FIXED
                        .logoutSuccessUrl("/login?logout")    // ✅ redirect after logout
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }
}
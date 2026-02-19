package com.poultry.shop.config;

import com.poultry.shop.security.CustomLoginSuccessHandler;
import com.poultry.shop.service.CustomOAuthUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final CustomOAuthUserService customOAuthUserService;
    private final CustomLoginSuccessHandler customLoginSuccessHandler;

    public SecurityConfig(CustomOAuthUserService customOAuthUserService,
                          CustomLoginSuccessHandler customLoginSuccessHandler) {
        this.customOAuthUserService = customOAuthUserService;
        this.customLoginSuccessHandler = customLoginSuccessHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                        // 🌍 Public pages (Homepage MUST be public)
                        .requestMatchers(
                                "/",
                                "/products",
                                "/products/**",
                                "/uploads/**",
                                "/images/**",
                                "/css/**",
                                "/js/**",
                                "/pdf/**",
                                "/ai-chat",
                                "/error",
                                "/sitemap.xml",
                                "/google491b0d2ab3dfd7d4.html",
                                "/oauth2/**",
                                "/login",
                                "/login/**"
                        ).permitAll()

                        // 🔐 Admin only
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // 🔐 Login required only for these
                        .requestMatchers("/cart/**", "/checkout/**", "/my-orders").authenticated()

                        // 🌍 Everything else public
                        .anyRequest().permitAll()
                )

                // 🔑 OAuth Login
                .oauth2Login(oauth -> oauth
                        .loginPage("/login")
                        .userInfoEndpoint(userInfo ->
                                userInfo.userService(customOAuthUserService)
                        )
                        .successHandler(customLoginSuccessHandler)
                )

                // 🚪 Logout
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )

                // ❌ Important: Disable default login redirect
                .formLogin(form -> form.disable());

        return http.build();
    }
}

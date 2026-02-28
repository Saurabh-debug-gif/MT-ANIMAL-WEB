package com.poultry.shop.config;

import com.poultry.shop.security.CustomLoginSuccessHandler;
import com.poultry.shop.service.CustomOAuthUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

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

                // ── Allow cross-device access (same network, deployed URL) ──
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // ── Session must be created and shared properly ──
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )

                .authorizeHttpRequests(auth -> auth

                        // 🌍 Public pages
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
                                "/enquiry",
                                "/error",
                                "/sitemap.xml",
                                "/google491b0d2ab3dfd7d4.html",
                                "/oauth2/**",
                                "/login",
                                "/login/**"
                        ).permitAll()

                        // 🔐 Admin only
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // 🔐 Login required for these
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

                // ❌ Disable default form login
                .formLogin(form -> form.disable());

        return http.build();
    }

    // ── CORS: allow access from any device on the network ──
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*")); // allows localhost, IPs, deployed URL
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true); // required for session cookies cross-device
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
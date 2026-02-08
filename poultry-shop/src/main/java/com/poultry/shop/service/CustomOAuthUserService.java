package com.poultry.shop.service;

import com.poultry.shop.model.User;
import com.poultry.shop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class CustomOAuthUserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) {

        // 1️⃣ Google se user data lao
        OAuth2User oauthUser = super.loadUser(request);

        // 2️⃣ Google attributes
        String email      = oauthUser.getAttribute("email");
        String name       = oauthUser.getAttribute("name");
        String providerId = oauthUser.getAttribute("sub");

        // 3️⃣ DB me user find ya create
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setName(name);
                    newUser.setProvider("GOOGLE");
                    newUser.setProviderId(providerId);
                    newUser.setRole("USER"); // default role
                    return userRepository.save(newUser);
                });

        // ⚠️ 4️⃣ MOST IMPORTANT: ROLE_ prefix
        Set<SimpleGrantedAuthority> authorities =
                Set.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()));

        // 5️⃣ Spring Security ko user + role return karo
        return new DefaultOAuth2User(
                authorities,
                oauthUser.getAttributes(),
                "email"
        );
    }
}


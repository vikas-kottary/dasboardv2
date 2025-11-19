package com.people10.dashboard.service;

import com.people10.dashboard.model.User;
import com.people10.dashboard.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    
    private final UserRepository userRepository;
    
    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        log.info("CustomOAuth2UserService CREATED with userRepository: {}", userRepository);
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("===============================================");
        log.info("CustomOAuth2UserService.loadUser() CALLED!");
        log.info("===============================================");
        
        // Load the OAuth2User from the provider (Google, etc.)
        OAuth2User oauth2User = super.loadUser(userRequest);
        
        log.info("Loading OAuth2 user: {}", oauth2User.getAttributes());
        
        // Extract email from OAuth2 user
        String email = oauth2User.getAttribute("email");
        if (email == null) {
            log.error("Email not found in OAuth2 user attributes");
            throw new OAuth2AuthenticationException("Email not found in user attributes");
        }
        
        log.info("Looking up user with email: {}", email);
        
        // Find user in database
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            log.warn("User not found in database with email: {}", email);
            // You can either throw an exception or create a user with default role
            throw new OAuth2AuthenticationException("User not found in database");
        }
        
        User user = userOpt.get();
        log.info("Found user: {} with role: {}", user.getName(), 
                user.getRole() != null ? user.getRole().getName() : "NO_ROLE");
        
        // Create authorities from user roles
        Set<GrantedAuthority> authorities = new HashSet<>();
        
        if (user.getRole() != null && user.getRole().getName() != null) {
            // Add role with ROLE_ prefix (Spring Security convention)
            String roleName = user.getRole().getName().toUpperCase();
            if (!roleName.startsWith("ROLE_")) {
                roleName = "ROLE_" + roleName;
            }
            authorities.add(new SimpleGrantedAuthority(roleName));
            log.info("Added authority: {}", roleName);
        }
        
        // Create a new OAuth2User with the authorities
        return new DefaultOAuth2User(
            authorities,
            oauth2User.getAttributes(),
            "email" // The key used to identify the user (usually "email" or "sub")
        );
    }
}

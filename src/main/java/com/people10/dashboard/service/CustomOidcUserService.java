package com.people10.dashboard.service;

import com.people10.dashboard.model.User;
import com.people10.dashboard.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class CustomOidcUserService extends OidcUserService {
    
    private final UserRepository userRepository;
    
    public CustomOidcUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        log.info("===============================================");
        log.info("CustomOidcUserService CREATED with userRepository: {}", userRepository);
        log.info("===============================================");
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("===============================================");
        log.info("CustomOidcUserService.loadUser() CALLED!");
        log.info("===============================================");
        
        // Load the OIDC user from the provider (Google, etc.)
        OidcUser oidcUser = super.loadUser(userRequest);
        
        log.info("Loading OIDC user: {}", oidcUser.getAttributes());
        
        // Extract email from OIDC user
        String email = oidcUser.getAttribute("email");
        if (email == null) {
            log.error("Email not found in OIDC user attributes");
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
        

        if (!user.isActive()) {
            log.warn("User is inactive: {}", email);
            throw new OAuth2AuthenticationException("User is inactive");
        }
        

        // Create authorities from user roles
        Set<GrantedAuthority> authorities = new HashSet<>(oidcUser.getAuthorities());
        
        if (user.getRole() != null && user.getRole().getName() != null) {
            // Add role with ROLE_ prefix (Spring Security convention)
            String roleName = user.getRole().getName().toUpperCase();
            if (!roleName.startsWith("ROLE_")) {
                roleName = "ROLE_" + roleName;
            }
            authorities.add(new SimpleGrantedAuthority(roleName));
            log.info("Added authority: {}", roleName);
        }
        
        log.info("Final authorities: {}", authorities);
        
        // Create a new OIDC user with the authorities
        return new DefaultOidcUser(
            authorities,
            oidcUser.getIdToken(),
            oidcUser.getUserInfo(),
            "email" // The key used to identify the user
        );
    }
}

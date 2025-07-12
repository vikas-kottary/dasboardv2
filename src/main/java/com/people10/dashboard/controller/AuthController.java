package com.people10.dashboard.controller;

import com.people10.dashboard.dto.UserInfoResponse;
import com.people10.dashboard.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository userRepository;

    @GetMapping("/post-login")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal OAuth2User principal) {
        
        log.info("Received request to /api/auth/post-login");
        log.info("AuthenticationPrincipal: {}", principal);
        log.info("Authorities: {}", principal.getAuthorities());
        
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }

        String email = principal.getAttribute("email");
        log.info("Email from principal: {}", email);
        if (email == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Email not found in principal"));
        }
        var userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(403).body(Map.of("error", "Not authorized"));
        }
        var user = userOpt.get();
        
        return ResponseEntity.ok(new UserInfoResponse(
            user.getId(),
            user.getEmail(),
            user.getName(),
            user.getRole() != null ? user.getRole().getName() : null
        ));
    }

    @GetMapping("/debug/authorities")
    public ResponseEntity<?> debugAuthorities(@AuthenticationPrincipal OAuth2User principal) {
        log.info("=== DEBUG AUTHORITIES ENDPOINT ===");
        if (principal == null) {
            log.warn("Principal is null");
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }
        
        log.info("Principal class: {}", principal.getClass().getName());
        log.info("Authorities: {}", principal.getAuthorities());
        log.info("Attributes: {}", principal.getAttributes());
        
        return ResponseEntity.ok(Map.of(
            "principalClass", principal.getClass().getName(),
            "authorities", principal.getAuthorities(),
            "attributes", principal.getAttributes(),
            "hasManagerRole", principal.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_MANAGER")),
            "allAuthorities", principal.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .toList()
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        log.info("Logout endpoint called - Spring Security will handle the actual logout");
        
        // Spring Security's logout filter will handle:
        // - Session invalidation
        // - Security context clearing
        // - Cookie deletion
        // - Redirect to success URL
        
        return ResponseEntity.ok(Map.of("message", "Logout initiated"));
    }
}

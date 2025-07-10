package com.people10.dashboard.controller;

import com.people10.dashboard.dto.UserInfoResponse;
import com.people10.dashboard.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
        
        log.info("Received request to /api/v1/auth/post-login");
        log.info("AuthenticationPrincipal: {}", principal);
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

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // Actual logout is handled by Spring Security, but you can instruct the frontend to clear tokens/cookies
        return ResponseEntity.ok(Map.of("message", "Logged out"));
    }
}

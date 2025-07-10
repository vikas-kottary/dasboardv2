package com.people10.dashboard.service;

import com.people10.dashboard.model.User;
import com.people10.dashboard.model.Role;
import com.people10.dashboard.repository.UserRepository;
import com.people10.dashboard.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public Optional<User> setUserRole(Long userId, String roleName) {
        var userOpt = userRepository.findById(userId);
        var roleOpt = roleRepository.findByName(roleName);
        if (userOpt.isEmpty() || roleOpt.isEmpty()) {
            return Optional.empty();
        }
        var user = userOpt.get();
        user.setRole(roleOpt.get());
        userRepository.save(user);
        return Optional.of(user);
    }

    public Optional<String> getUserRole(Long userId) {
        var userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty() || userOpt.get().getRole() == null) {
            return Optional.empty();
        }
        return Optional.of(userOpt.get().getRole().getName());
    }

    public Optional<User> createUser(User user) {
        if (user.getId() != null) {
            return Optional.empty();
        }
        return Optional.of(userRepository.save(user));
    }

    public Optional<User> updateUser(Long userId, User user) {
        return userRepository.findById(userId)
                .map(existingUser -> {
                    existingUser.setName(user.getName());
                    existingUser.setEmail(user.getEmail());
                    existingUser.setRole(user.getRole());
                    return userRepository.save(existingUser);
                });
    }

    public Optional<User> patchUser(Long userId, User user) {
        return userRepository.findById(userId)
                .map(existingUser -> {
                    if (user.getName() != null) existingUser.setName(user.getName());
                    if (user.getEmail() != null) existingUser.setEmail(user.getEmail());
                    if (user.getRole() != null) existingUser.setRole(user.getRole());
                    return userRepository.save(existingUser);
                });
    }

    public boolean deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            return false;
        }
        userRepository.deleteById(userId);
        return true;
    }
}

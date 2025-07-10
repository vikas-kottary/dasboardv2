package com.people10.dashboard.controller;

import com.people10.dashboard.model.Opco;
import com.people10.dashboard.model.Manager;
import com.people10.dashboard.model.Team;
import com.people10.dashboard.model.User;
import com.people10.dashboard.repository.UserRepository;
import com.people10.dashboard.repository.RoleRepository;
import com.people10.dashboard.dto.OpcoRequestDto;
import com.people10.dashboard.dto.ManagerRequestDto;
import com.people10.dashboard.dto.TeamRequestDto;
import com.people10.dashboard.service.OpcoService;
import com.people10.dashboard.service.ManagerService;
import com.people10.dashboard.service.TeamService;
import com.people10.dashboard.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {
    private final OpcoService opcoService;
    private final ManagerService managerService;
    private final TeamService teamService;
    private final UserRepository userRepository;
    private final AdminService adminService;

    // --- Opco CRUD ---
    @GetMapping("/opcos")
    public ResponseEntity<List<Opco>> getAllOpcos() {
        return ResponseEntity.ok(opcoService.getAllOpcos());
    }

    @GetMapping("/opcos/{id}")
    public ResponseEntity<Opco> getOpco(@PathVariable Long id) {
        var opco = opcoService.getOpco(id);
        return opco != null ? ResponseEntity.ok(opco) : ResponseEntity.notFound().build();
    }

    @PostMapping("/opcos")
    public ResponseEntity<Opco> createOpco(@Valid @RequestBody OpcoRequestDto opcoDto) {
        return ResponseEntity.ok(opcoService.createOpco(opcoDto));
    }

    @PutMapping("/opcos/{id}")
    public ResponseEntity<Opco> updateOpco(@PathVariable Long id, @Valid @RequestBody OpcoRequestDto opcoDto) {
        var opco = opcoService.updateOpco(id, opcoDto);
        return opco != null ? ResponseEntity.ok(opco) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/opcos/{id}")
    public ResponseEntity<Void> deleteOpco(@PathVariable Long id) {
        opcoService.deleteOpco(id);
        return ResponseEntity.noContent().build();
    }

    // --- Manager CRUD ---
    @GetMapping("/managers")
    public ResponseEntity<List<Manager>> getAllManagers() {
        return ResponseEntity.ok(managerService.getAllManagers());
    }

    @GetMapping("/managers/{id}")
    public ResponseEntity<Manager> getManager(@PathVariable Long id) {
        var manager = managerService.getManager(id);
        return manager != null ? ResponseEntity.ok(manager) : ResponseEntity.notFound().build();
    }

    @PostMapping("/managers")
    public ResponseEntity<Manager> createManager(@Valid @RequestBody ManagerRequestDto managerDto) {
        return ResponseEntity.ok(managerService.createManager(managerDto));
    }

    @PutMapping("/managers/{id}")
    public ResponseEntity<Manager> updateManager(@PathVariable Long id, @Valid @RequestBody ManagerRequestDto managerDto) {
        var manager = managerService.updateManager(id, managerDto);
        return manager != null ? ResponseEntity.ok(manager) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/managers/{id}")
    public ResponseEntity<Void> deleteManager(@PathVariable Long id) {
        managerService.deleteManager(id);
        return ResponseEntity.noContent().build();
    }

    // --- Team CRUD ---
    @GetMapping("/teams")
    public ResponseEntity<List<Team>> getAllTeams() {
        return ResponseEntity.ok(teamService.getAllTeams());
    }

    @GetMapping("/teams/{id}")
    public ResponseEntity<Team> getTeam(@PathVariable Long id) {
        var team = teamService.getTeam(id);
        return team != null ? ResponseEntity.ok(team) : ResponseEntity.notFound().build();
    }

    @PostMapping("/teams")
    public ResponseEntity<Team> createTeam(@Valid @RequestBody TeamRequestDto teamDto) {
        return ResponseEntity.ok(teamService.createTeam(teamDto));
    }

    @PutMapping("/teams/{id}")
    public ResponseEntity<Team> updateTeam(@PathVariable Long id, @Valid @RequestBody TeamRequestDto teamDto) {
        var team = teamService.updateTeam(id, teamDto);
        return team != null ? ResponseEntity.ok(team) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/teams/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
        return ResponseEntity.noContent().build();
    }

    // --- User CRUD ---
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGEMENT')")
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGEMENT')")
    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return adminService.createUser(user)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGEMENT')")
    @PutMapping("/users/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable Long userId, @RequestBody User user) {
        return adminService.updateUser(userId, user)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGEMENT')")
    @PatchMapping("/users/{userId}")
    public ResponseEntity<User> patchUser(@PathVariable Long userId, @RequestBody User user) {
        return adminService.patchUser(userId, user)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGEMENT')")
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        if (!adminService.deleteUser(userId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    // --- User Role Management ---
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGEMENT')")
    @PostMapping("/users/{userId}/role")
    public ResponseEntity<User> setUserRole(@PathVariable Long userId, @RequestParam String roleName) {
        return adminService.setUserRole(userId, roleName)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGEMENT')")
    @GetMapping("/users/{userId}/role")
    public ResponseEntity<String> getUserRole(@PathVariable Long userId) {
        return adminService.getUserRole(userId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}

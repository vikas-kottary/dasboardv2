package com.people10.dashboard.controller;

// import com.people10.dashboard.model.Opco;
// import com.people10.dashboard.model.Manager;
// import com.people10.dashboard.model.Team;
import com.people10.dashboard.model.User;
import com.people10.dashboard.model.TeamMapping;
import com.people10.dashboard.repository.UserRepository;
import com.people10.dashboard.repository.TeamMappingRepository;
import com.people10.dashboard.dto.AdminReportResponseMeta;
import com.people10.dashboard.dto.AdminTeamMappingResponse;
import com.people10.dashboard.dto.ReportResponseDto;
import com.people10.dashboard.dto.TeamMappingResponse;
import com.people10.dashboard.dto.TeamRequestDto;
import com.people10.dashboard.dto.UserRequestDto;
import com.people10.dashboard.dto.UserResponseDto;
import com.people10.dashboard.service.AdminService;
import com.people10.dashboard.service.ReportService;
import com.people10.dashboard.service.TeamMappingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final TeamMappingRepository teamMappingRepository;
    private final AdminService adminService;
    private final TeamMappingService teamMappingService;
    private final ReportService reportService;

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll().stream()
                .map(adminService::convertToUserResponse)
                .toList());
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/users")
    public ResponseEntity<UserResponseDto> createUser(@RequestBody UserRequestDto user) {
        return adminService.createUser(user)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/users/{userId}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable Long userId, @RequestBody UserRequestDto user) {
        return adminService.updateUser(userId, user)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

        
    @PutMapping("/users/{userId}/role")
    public ResponseEntity<UserResponseDto> setUserRole(@PathVariable Long userId, @RequestParam String roleName) {
        return adminService.setUserRole(userId, roleName)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    //api to inactivate / activate the user
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/users/{userId}/{isActive}")
    public ResponseEntity<UserResponseDto> updateUserStatus(@PathVariable Long userId, @PathVariable boolean isActive) {
        return adminService.updateUserStatus(userId, isActive)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // TeamMapping CRUD endpoints
    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/team-mappings")
    public ResponseEntity<List<AdminTeamMappingResponse>> getAllTeamMappings() {
        return ResponseEntity.ok(teamMappingRepository.findAll().stream()
                .map(teamMappingService::convertToResponse)
                .toList());
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/team-mappings")
    public ResponseEntity<AdminTeamMappingResponse> createTeamMapping(@RequestBody @Valid TeamRequestDto teamRequestDto) {
        return teamMappingService.createTeamMapping(teamRequestDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/team-mappings/{teamMappingId}")
    public ResponseEntity<AdminTeamMappingResponse> updateTeamMapping(@PathVariable Long teamMappingId, @RequestBody @Valid TeamRequestDto teamRequestDto) {
        return teamMappingService.updateTeamMapping(teamMappingId, teamRequestDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    //api to inactivate / activate the team mapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/team-mappings/{teamMappingId}/{isActive}")
    public ResponseEntity<AdminTeamMappingResponse> updateTeamMappingStatus(@PathVariable Long teamMappingId, @PathVariable boolean isActive) {
        return teamMappingService.updateTeamMappingStatus(teamMappingId, isActive)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
   
    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/reports/meta")
    public ResponseEntity<AdminReportResponseMeta> getReportsMeta() {
        AdminReportResponseMeta responseMeta = adminService.getReportsMeta();
        return ResponseEntity.ok(responseMeta);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/reports/{id}")
    public ResponseEntity<ReportResponseDto> getReport(@PathVariable Long id) {
        ReportResponseDto report = reportService.getReport(id);
        return ResponseEntity.ok(report);
    }

}

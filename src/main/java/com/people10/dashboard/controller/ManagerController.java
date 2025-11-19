package com.people10.dashboard.controller;

import com.people10.dashboard.dto.ReportDto;
import com.people10.dashboard.dto.ReportResponseDto;
import com.people10.dashboard.dto.SummaryResponseDto;
import com.people10.dashboard.dto.TeamDashboardRequest;
import com.people10.dashboard.dto.TeamMappingResponse;
import com.people10.dashboard.dto.TeamMappingResponse.OpcoInfo;
import com.people10.dashboard.model.TeamMapping;
import com.people10.dashboard.repository.TeamMappingRepository;
import com.people10.dashboard.service.ReportService;
import com.people10.dashboard.service.SummarizeService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/manager")
@RequiredArgsConstructor
public class ManagerController {
    private final ReportService reportService;
    private final SummarizeService summarizeService;
    private final TeamMappingRepository teamMappingRepository;
    // @PreAuthorize("hasAnyRole('MANAGER')")
    // @GetMapping
    // public ResponseEntity<List<ReportResponseDto>> getAllReports() {
    //     return ResponseEntity.ok(reportService.getAllReports());
    // }

    @PreAuthorize("hasAnyRole('MANAGER')")
    @GetMapping("/{id}")
    public ResponseEntity<List<ReportResponseDto>> getReportByManager(
            @PathVariable Long id,
            @RequestParam(value = "reportStatus", required = false) String reportStatus) {
        var reports = reportService.getReportsByManager(id, reportStatus);
        return reports != null ? ResponseEntity.ok(reports) : ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/submit")
    public ResponseEntity<ReportResponseDto> createReport(@Valid @RequestBody ReportDto reportDto) {
        try {
            var report = reportService.createReport(reportDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(report);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/summarize")
    public SummaryResponseDto summarizeReport(@RequestBody TeamDashboardRequest reportText) {
        return summarizeService.summarizeReport(reportText);
    }    
    
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/team-mappings")
    public ResponseEntity<List<TeamMappingResponse>> getAllTeamMappings() {
        List<TeamMapping> teamMappings = teamMappingRepository.findAll();
        List<TeamMappingResponse> response = teamMappings.stream()
                .filter(tm -> tm.isActive())    
                .map(this::convertToResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("{id}/team-mappings")
    public ResponseEntity<List<TeamMappingResponse>> getTeamForManager(@PathVariable Long id) {
        List<TeamMapping> teamMappings = teamMappingRepository.findByManagerId(id);
        List<TeamMappingResponse> response = teamMappings.stream()
                .filter(tm -> tm.isActive())    
                .map(this::convertToResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    private TeamMappingResponse convertToResponse(TeamMapping teamMapping) {
        TeamMappingResponse response = new TeamMappingResponse();
        OpcoInfo opcoInfo = new OpcoInfo();

        opcoInfo.setId(teamMapping.getOpco().getId());
        opcoInfo.setName(teamMapping.getOpco().getName());

        response.setId(teamMapping.getId());
        response.setName(teamMapping.getName());
        response.setClient(teamMapping.getClient());
        response.setManagerId(teamMapping.getManager() != null ? teamMapping.getManager().getId() : null);
        response.setOpco(opcoInfo);
        return response;
    }

    // @PreAuthorize("hasAnyRole('OPCO', 'ADMIN', 'MANAGEMENT')")
    // @PutMapping("/{id}")
    // public ResponseEntity<ReportResponseDto> updateReport(@PathVariable Long id, @Valid @RequestBody ReportDto reportDto) {
    //     try {
    //         var report = reportService.updateReport(id, reportDto);
    //         return report != null ? ResponseEntity.ok(report) : ResponseEntity.notFound().build();
    //     } catch (Exception e) {
    //         return ResponseEntity.badRequest().build();
    //     }
    // }

    // @PreAuthorize("hasAnyRole('OPCO', 'ADMIN', 'MANAGEMENT')")
    // @DeleteMapping("/{id}")
    // public ResponseEntity<Void> deleteReport(@PathVariable Long id) {
    //     try {
    //         reportService.deleteReport(id);
    //         return ResponseEntity.noContent().build();
    //     } catch (Exception e) {
    //         return ResponseEntity.badRequest().build();
    //     }
    // }

    // @PreAuthorize("hasRole('OPCO') or hasRole('ADMIN') or hasRole('MANAGEMENT')")
    // @GetMapping("/opco/{id}")
    // public ResponseEntity<List<ReportResponseDto>> getReportsByOpco(@PathVariable Long id) {
    //     var reports = reportService.getReportsByOpco(id);
    //     return reports != null ? ResponseEntity.ok(reports) : ResponseEntity.notFound().build();
    // }

    @GetMapping("/test-auth")
    public ResponseEntity<?> testAuth(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }
        
        return ResponseEntity.ok(Map.of(
            "message", "Authentication works!",
            "email", principal.getAttribute("email"),
            "authorities", principal.getAuthorities(),
            "hasManagerRole", principal.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_MANAGER"))
        ));
    }
}

package com.people10.dashboard.controller;

import com.people10.dashboard.dto.ReportDto;
import com.people10.dashboard.dto.ReportResponseDto;
import com.people10.dashboard.dto.TeamDashboardRequest;
import com.people10.dashboard.model.TeamMapping;
import com.people10.dashboard.repository.TeamMappingRepository;
import com.people10.dashboard.service.ReportService;
import com.people10.dashboard.service.SummarizeService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

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

    //@PreAuthorize("hasAnyRole('MANAGER')")
    @GetMapping("/{id}")
    public ResponseEntity<List<ReportResponseDto>> getReportByManager(
            @PathVariable Long id,
            @RequestParam(value = "reportStatus", required = false) String reportStatus) {
        var reports = reportService.getReportsByManager(id, reportStatus);
        return reports != null ? ResponseEntity.ok(reports) : ResponseEntity.notFound().build();
    }

    //@PreAuthorize("hasRole('MANAGER')")
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
    public String summarizeReport(@RequestBody TeamDashboardRequest reportText) {
        return summarizeService.summarizeReport(reportText);
    }

    @GetMapping("/team-mappings")
    public ResponseEntity<List<TeamMapping>> getAllTeamMappings() {
        List<TeamMapping> teamMappings = teamMappingRepository.findAll();
        return ResponseEntity.ok(teamMappings);
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
}

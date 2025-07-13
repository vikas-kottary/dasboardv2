package com.people10.dashboard.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.people10.dashboard.dto.ReportResponseDto;
import com.people10.dashboard.dto.ReportStatusUpdateDto;
import com.people10.dashboard.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/opco")
@RequiredArgsConstructor
public class OpcoController {
    
    /*
     * 1 - Reports that are pending approval
     * 2 - endpoint to update the status of a report
     * 3 - endpoint to get all reports for an opco
     */

    private final ReportService reportService;

    @PreAuthorize("hasAnyRole('OPCO')")
    @GetMapping("reports/{id}")
    public ResponseEntity<List<ReportResponseDto>> getReportsByOpco(
            @PathVariable Long id,
            @RequestParam(value = "reportStatus", required = false) String reportStatus) {
        var reports = reportService.getReportsByOpco(id, reportStatus);
        return reports != null ? ResponseEntity.ok(reports) : ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasAnyRole('OPCO')")
    @PutMapping("report/status")
    public ResponseEntity<List<ReportResponseDto>> updateReportStatus(
            @RequestBody ReportStatusUpdateDto reportStatusUpdateDto) {
        reportService.updateReportStatus(reportStatusUpdateDto);
        return ResponseEntity.ok().build();
    }
}
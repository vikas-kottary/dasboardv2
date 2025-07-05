package com.people10.dashboard.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.people10.dashboard.dto.TeamDashboardRequest;
import com.people10.dashboard.service.SummarizeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/summarize")
@RequiredArgsConstructor
public class SummarizeController {
    
    private final SummarizeService summarizeService;

    @PostMapping()
    public String summarizeReport(@RequestBody TeamDashboardRequest reportText) {
        return summarizeService.summarizeReport(reportText);
    }

}

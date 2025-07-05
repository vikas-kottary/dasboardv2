package com.people10.dashboard.dto;

import java.util.List;
import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReportDto {
    @NotNull(message = "Team ID is required")  
    private Long teamId;
    @NotNull(message = "Manager ID is required")
    private Long managerId;
    @NotNull(message = "Client Name is required")
    private String clientName;
    @NotNull(message = "Opco ID is required")
    private Long opcoId;
    @NotNull(message = "Start Date is required")
    private LocalDate startDate;
    @NotNull(message = "End Date is required")
    private LocalDate endDate;
    
    // Nested objects
    private List<MilestoneDto> milestones;
    private WorkloadVisibilityDto workloadVisibility;
    private AdequateQualityDto adequateQuality;
    private EscalationsDto escalations;
    private TrainingsDto trainings;
    private BillabilityDto billability;
    private List<ImprovementDto> improvements;
    private NonAdherenceDto nonAdherence;
    private TimesheetsDto timesheets;
    private InnovationDto innovation;
    private RiskDto risk;
    private List<ShowcaseDto> showcases;
    private String summary; // Assuming summary is a simple string, adjust as needed
}

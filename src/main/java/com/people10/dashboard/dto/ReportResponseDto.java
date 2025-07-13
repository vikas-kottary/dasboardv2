package com.people10.dashboard.dto;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

import lombok.Data;

@Data
public class ReportResponseDto {
   
    private String teamName;
   
    private String managerName;
   
    private String clientName;
   
    private String opcoName;
   
    private LocalDate startDate;
   
    private LocalDate endDate;
    
    // Nested objects
    private List<MilestoneDto> milestones = new ArrayList<>();
    private WorkloadVisibilityDto workloadVisibility;
    private AdequateQualityDto adequateQuality;
    private EscalationsDto escalations;
    private TrainingsDto trainings;
    private BillabilityDto billability;
    private List<ImprovementDto> improvements = new ArrayList<>();
    private NonAdherenceDto nonAdherence;
    private TimesheetsDto timesheets;
    private InnovationDto innovation;
    private RiskDto risk;
    private List<ShowcaseDto> showcases= new ArrayList<>();
    private String summary; 

    private String status;
    private Long reportId;
    private List<CommentResponseDto> comments = new ArrayList<>();
}

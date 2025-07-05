package com.people10.dashboard.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class MilestoneDto {
    private Integer sequenceNo;
    private String projectName;
    private String detail;
    private LocalDate milestoneDate;
    private Long ragStatusId;
}

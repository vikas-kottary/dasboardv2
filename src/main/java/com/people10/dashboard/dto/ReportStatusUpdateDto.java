package com.people10.dashboard.dto;

import lombok.Data;

@Data
public class ReportStatusUpdateDto {
    private String newStatus;
    private Long reportId;
    private Long userId;
    private String comment;
}

package com.people10.dashboard.dto;

import lombok.Data;

@Data
public class EscalationsDto {
    private boolean hasEscalation;
    private String details;
    private Long ragStatusId;
}

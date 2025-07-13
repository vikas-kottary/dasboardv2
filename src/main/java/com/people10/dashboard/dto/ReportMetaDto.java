package com.people10.dashboard.dto;

import lombok.Data;

@Data
public class ReportMetaDto {
    private Long reportId;
    private String teamName;
    private String clientName;
    private String managerName;
    private String opcoName;
    private String processStatus;
    private String summary;
}

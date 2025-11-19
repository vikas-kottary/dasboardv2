package com.people10.dashboard.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminTeamMappingResponse {
    private Long id;
    private String name;
    private String client;
    private ManagerInfo manager;
    private OpcoInfo opco;
    private boolean isActive;
    private boolean skipOpcoApproval;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OpcoInfo {
        private Long id;
        private String name;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ManagerInfo {
        private Long id;
        private String name;
    }
}

package com.people10.dashboard.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamMappingResponse {
    private Long id;
    private String name;
    private String client;
    private Long managerId;
    private OpcoInfo opco;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OpcoInfo {
        private Long id;
        private String name;
    }
}

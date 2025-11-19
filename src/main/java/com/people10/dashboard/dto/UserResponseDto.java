package com.people10.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {
    private Long id;
    private String email;
    private String name;
    private RoleInfo role;
    private boolean isActive;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RoleInfo {
        private Long id;
        private String name;
    }
}

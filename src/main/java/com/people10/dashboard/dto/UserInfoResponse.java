package com.people10.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserInfoResponse {
    private Long id;
    private String email;
    private String name;
    private String role;
}

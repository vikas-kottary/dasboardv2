package com.people10.dashboard.dto;

import lombok.Data;

@Data
public class UserRequestDto {
    private String email;
    private String name;
    private Long roleId;
}
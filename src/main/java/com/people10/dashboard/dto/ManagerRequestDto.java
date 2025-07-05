package com.people10.dashboard.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class ManagerRequestDto {
    @NotBlank(message = "Name is required")
    private String name;
}

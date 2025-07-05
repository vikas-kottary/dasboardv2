package com.people10.dashboard.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class TeamRequestDto {
    @NotBlank(message = "Name is required")
    private String name;
    @NotBlank(message = "Client is required")
    private String client;
    @NotNull(message = "Manager ID is required")
    private Long managerId;
    @NotNull(message = "Opco ID is required")
    private Long opcoId;

}

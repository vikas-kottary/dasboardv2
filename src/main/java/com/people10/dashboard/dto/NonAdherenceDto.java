package com.people10.dashboard.dto;

import lombok.Data;

@Data
public class NonAdherenceDto {
    private String nonAdherenceValue;
    private Integer count;
    private String impact;
    private String timeToResolve;
    private Long ragStatusId;
}

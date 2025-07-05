package com.people10.dashboard.dto;

import lombok.Data;

@Data
public class BillabilityDto {
    private float billedResources;
    private float unbilledResources;
    private float leavesBilled;
    private float leavesUnbilled;
    private int holidays;
    private double overallBillabilityPercent;
    private Long ragStatusId;
}

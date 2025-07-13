package com.people10.dashboard.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class AdminReportResponseMeta {
    private Map<String, List<ReportMetaDto>> reports = new HashMap<>();
}

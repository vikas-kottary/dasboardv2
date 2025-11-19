package com.people10.dashboard.model.report;

import lombok.Data;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

import com.people10.dashboard.dto.ReportStatus;

@Data
@Entity
@Table(name = "report_history")
public class ReportHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "report_id", nullable = false)
    private Long reportId;

    @Column(name = "changed_by", nullable = false)
    private String changedBy;

    @Column(name = "previous_status")
    private String previousStatus;

    @Column(name = "new_status", nullable = false)
    private String newStatus;

    @Column(name = "comment", length = 1000)
    private String comment;

    @Column(name = "changed_at")
    private LocalDateTime changedAt;
}
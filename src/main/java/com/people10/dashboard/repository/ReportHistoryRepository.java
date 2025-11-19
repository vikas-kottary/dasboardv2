package com.people10.dashboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.people10.dashboard.model.report.ReportHistory;

public interface ReportHistoryRepository extends JpaRepository<ReportHistory, Long> {
}
package com.people10.dashboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.people10.dashboard.model.report.Summary;

public interface SummaryRepository extends JpaRepository<Summary, Long> {
}

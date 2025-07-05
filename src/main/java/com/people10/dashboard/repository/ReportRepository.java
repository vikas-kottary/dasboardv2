package com.people10.dashboard.repository;

import com.people10.dashboard.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
}

package com.people10.dashboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.people10.dashboard.model.report.Risk;

public interface RiskRepository extends JpaRepository<Risk, Long> {
}

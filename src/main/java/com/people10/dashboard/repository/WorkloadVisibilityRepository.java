package com.people10.dashboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.people10.dashboard.model.report.WorkloadVisibility;

public interface WorkloadVisibilityRepository extends JpaRepository<WorkloadVisibility, Long> {
}

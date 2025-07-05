package com.people10.dashboard.repository;

import com.people10.dashboard.model.report.Milestone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MilestoneRepository extends JpaRepository<Milestone, Long> {
}
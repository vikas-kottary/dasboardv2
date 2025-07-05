package com.people10.dashboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.people10.dashboard.model.report.NonAdherence;

public interface NonAdherenceRepository extends JpaRepository<NonAdherence, Long> {
}

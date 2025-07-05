package com.people10.dashboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.people10.dashboard.model.report.Innovation;

public interface InnovationRepository extends JpaRepository<Innovation, Long> {
}

package com.people10.dashboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.people10.dashboard.model.report.AdequateQuality;

public interface AdequateQualityRepository extends JpaRepository<AdequateQuality, Long> {
}

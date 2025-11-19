package com.people10.dashboard.repository;

import com.people10.dashboard.model.TeamMapping;
import com.people10.dashboard.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamMappingRepository extends JpaRepository<TeamMapping, Long> {
    
    // Find team mappings by manager
    List<TeamMapping> findByManager(User manager);
    
    // Find team mappings by manager ID
    List<TeamMapping> findByManagerId(Long managerId);
    
    // Find team mappings by opco
    List<TeamMapping> findByOpco(User opco);
    
    // Find team mappings by opco ID
    List<TeamMapping> findByOpcoId(Long opcoId);
    
    // Find team mappings by name (case insensitive)
    List<TeamMapping> findByNameContainingIgnoreCase(String name);
    
    // Custom query to find team mappings by manager and opco
    @Query("SELECT tm FROM TeamMapping tm WHERE tm.manager.id = :managerId AND tm.opco.id = :opcoId")
    List<TeamMapping> findByManagerIdAndOpcoId(@Param("managerId") Long managerId, @Param("opcoId") Long opcoId);
}

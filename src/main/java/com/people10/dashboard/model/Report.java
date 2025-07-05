package com.people10.dashboard.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

import com.people10.dashboard.model.report.Summary;
import com.people10.dashboard.model.report.Improvement;
import com.people10.dashboard.model.report.Showcase;
import com.people10.dashboard.model.report.WorkloadVisibility;
import com.people10.dashboard.model.report.AdequateQuality;
import com.people10.dashboard.model.report.Escalation;
import com.people10.dashboard.model.report.Training;
import com.people10.dashboard.model.report.Billability;
import com.people10.dashboard.model.report.NonAdherence;
import com.people10.dashboard.model.report.Timesheet;
import com.people10.dashboard.model.report.Innovation;
import com.people10.dashboard.model.report.Milestone;
import com.people10.dashboard.model.report.Risk;

@Entity
@Data
@Table(name = "report")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private Manager manager;

    private String clientName;

    @ManyToOne
    @JoinColumn(name = "opco_id")
    private Opco opco;

    private LocalDate startDate;

    private LocalDate endDate;

    private LocalDate createdAt;
    
    // One-to-many relationships
    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Milestone> milestones;
    
    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Improvement> improvements;
    
    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Showcase> showcases;
    
    // One-to-one relationships
    @OneToOne(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private WorkloadVisibility workloadVisibility;
    
    @OneToOne(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private AdequateQuality adequateQuality;
    
    @OneToOne(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private Escalation escalation;
    
    @OneToOne(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private Training training;
    
    @OneToOne(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private Billability billability;
    
    @OneToOne(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private NonAdherence nonAdherence;
    
    @OneToOne(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private Timesheet timesheet;
    
    @OneToOne(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private Innovation innovation;
    
    @OneToOne(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private Risk risk;

    @OneToOne(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private Summary summary;
}

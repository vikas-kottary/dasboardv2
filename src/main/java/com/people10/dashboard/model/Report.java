package com.people10.dashboard.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
@Table(name = "reports")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_name", length = 100)
    private String clientName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_mapping_id")
    private TeamMapping teamMapping;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private User manager;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opco_id")
    private User opco;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "team_name_snapshot", length = 100)
    private String teamNameSnapshot;

    @Column(name = "manager_name_snapshot", length = 100)
    private String managerNameSnapshot;

    @Column(name = "opco_name_snapshot", length = 100)
    private String opcoNameSnapshot;

    @Column(name = "process_status", length = 55)
    private String processStatus;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
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

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();
}

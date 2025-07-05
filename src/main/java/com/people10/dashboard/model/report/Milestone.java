package com.people10.dashboard.model.report;

import com.people10.dashboard.model.Report;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "milestone")
public class Milestone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "report_id")
    private Report report;
    
    private Integer sequenceNo;
    private String projectName;
    
    @Column(columnDefinition = "TEXT")
    private String detail;
    
    private LocalDate milestoneDate;
    
    @Column(name = "rag_status_id")
    private Long ragStatusId;
}

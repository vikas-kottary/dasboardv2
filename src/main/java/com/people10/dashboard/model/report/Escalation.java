package com.people10.dashboard.model.report;

import com.people10.dashboard.model.Report;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "escalation")
public class Escalation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "report_id")
    private Report report;
    
    private boolean hasEscalation;
    
    @Column(columnDefinition = "TEXT")
    private String details;
    
    @Column(name = "rag_status_id")
    private Long ragStatusId;
}

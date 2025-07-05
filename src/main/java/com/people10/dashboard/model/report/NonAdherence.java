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
@Table(name = "non_adherence")
public class NonAdherence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "report_id")
    private Report report;
    
    @Column(name = "non_adherence_value")
    private String nonAdherenceValue;
    
    private Integer count;
    
    @Column(columnDefinition = "TEXT")
    private String impact;
    
    @Column(name = "time_to_resolve")
    private String timeToResolve;
    
    @Column(name = "rag_status_id")
    private Long ragStatusId;
}

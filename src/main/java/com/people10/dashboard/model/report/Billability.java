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
@Table(name = "billability")
public class Billability {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "report_id")
    private Report report;
    
    @Column(name = "billed_resources")
    private float billedResources;
    
    @Column(name = "unbilled_resources")
    private float unbilledResources;
    
    @Column(name = "leaves_billed")
    private float leavesBilled;
    
    @Column(name = "leaves_unbilled")
    private float leavesUnbilled;
    
    private int holidays;
    
    @Column(name = "overall_billability_percent")
    private double overallBillabilityPercent;
    
    @Column(name = "rag_status_id")
    private Long ragStatusId;
}

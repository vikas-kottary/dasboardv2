package com.people10.dashboard.model.report;

import com.people10.dashboard.model.Report;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "showcase")
public class Showcase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "report_id")
    private Report report;
    
    @Column(name = "sequence_no")
    private Integer sequenceNo;
    
    @Column(columnDefinition = "TEXT")
    private String detail;
}

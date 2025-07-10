package com.people10.dashboard.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // e.g. MANAGER, OPCO, ADMIN, MANAGEMENT

    @OneToMany(mappedBy = "role")
    private java.util.Set<User> users;
}

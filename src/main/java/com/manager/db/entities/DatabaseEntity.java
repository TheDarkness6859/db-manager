package com.manager.db.entities;

import com.manager.db.enums.DatabaseEngine;
import com.manager.db.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "user_database")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DatabaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DatabaseEngine databaseEngine;

    @Column(nullable = false)
    private String userDatabase;

    @Column(nullable = false)
    private String passwordDatabase;

    @Column(nullable = false)
    private Integer port;

    @Enumerated(EnumType.STRING)
    private Status databaseStatus;

    private LocalDate databaseCreation;

    private LocalDate databaseUpdate;

    @Column(nullable = true)
    private String containerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @PrePersist
    protected void onCreate() {
        this.databaseCreation = LocalDate.now();
        this.databaseUpdate = LocalDate.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.databaseUpdate = LocalDate.now();
    }

}

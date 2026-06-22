package com.manager.db.entities;

import com.manager.db.enums.Roles;
import com.manager.db.enums.UserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "user")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Roles rol;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    private LocalDate registerDate;

    private LocalDate lastUpdate;

    @PrePersist
    protected void onCreate() {
        this.registerDate = LocalDate.now();
        this.lastUpdate = LocalDate.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastUpdate = LocalDate.now();
    }

}

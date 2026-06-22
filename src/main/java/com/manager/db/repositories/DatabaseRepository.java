package com.manager.db.repositories;

import com.manager.db.entities.DatabaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DatabaseRepository extends JpaRepository<DatabaseEntity, UUID> {
}

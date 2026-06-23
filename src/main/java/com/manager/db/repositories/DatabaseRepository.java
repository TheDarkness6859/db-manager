package com.manager.db.repositories;

import com.manager.db.entities.DatabaseEntity;
import com.manager.db.enums.DatabaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DatabaseRepository extends JpaRepository<DatabaseEntity, UUID> {

    List<DatabaseEntity> findByUserId (UUID useId);

    DatabaseEntity findByContainerId (String id);

    boolean existsByPortAndDatabaseStatus (Integer port, DatabaseStatus status);
}

package com.manager.db.repositories;

import com.manager.db.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findByEmail (String email);

    List<UserEntity> findByNameOrEmail (String email, String name);

}

package com.manager.db.mappers;

import com.manager.db.entities.DatabaseEntity;
import com.manager.db.models.Database;

public class DatabaseMapper {

    private final UserMapper mapper;

    DatabaseMapper (UserMapper mapper){
        this.mapper = mapper;
    }

    public Database toDomain (DatabaseEntity entity){

        Database domain = new Database();

        domain.setId(entity.getId());
        domain.setName(entity.getName());
        domain.setDescription(entity.getDescription());
        domain.setDatabase(entity.getDatabaseEngine());
        domain.setUserDatabase(entity.getUserDatabase());
        domain.setPasswordDatabase(entity.getPasswordDatabase());
        domain.setPort(entity.getPort());
        domain.setContainerId(entity.getContainerId());
        domain.setDatabaseStatus(entity.getDatabaseStatus());
        domain.setDatabaseCreation(entity.getDatabaseCreation());
        domain.setDatabaseUpdate(entity.getDatabaseUpdate());
        domain.setUser(mapper.toDomain(entity.getUser()));

        return domain;

    }

    public DatabaseEntity toEntity (Database domain) {

        DatabaseEntity entity = new DatabaseEntity();

        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setDatabaseEngine(domain.getDatabase());
        entity.setUserDatabase(domain.getUserDatabase());
        entity.setPasswordDatabase(domain.getPasswordDatabase());
        entity.setPort(domain.getPort());
        entity.setDatabaseStatus(domain.getDatabaseStatus());
        entity.setDatabaseCreation(domain.getDatabaseCreation());
        entity.setDatabaseUpdate(domain.getDatabaseUpdate());
        entity.setContainerId(domain.getContainerId());
        entity.setUser(mapper.toEntity(domain.getUser()));

        return entity;

    }

}

package com.manager.db.mappers;

import com.manager.db.entities.UserEntity;
import com.manager.db.models.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toDomain (UserEntity entity) {

        User domain = new User();

        domain.setId(entity.getId());
        domain.setName(entity.getName());
        domain.setLastName(entity.getLastName());
        domain.setEmail(entity.getEmail());
        domain.setPassword(entity.getPassword());
        domain.setRol(entity.getRol());
        domain.setStatus(entity.getStatus());
        domain.setRegisterDate(entity.getRegisterDate());
        domain.setLastUpdate(entity.getLastUpdate());

        return domain;

    }

    public UserEntity toEntity (User domain) {

        UserEntity entity = new UserEntity();

        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setLastName(domain.getLastName());
        entity.setEmail(domain.getEmail());
        entity.setPassword(domain.getPassword());
        entity.setRol(domain.getRol());
        entity.setStatus(domain.getStatus());
        entity.setRegisterDate(domain.getRegisterDate());
        entity.setLastUpdate(domain.getLastUpdate());

        return entity;

    }

}

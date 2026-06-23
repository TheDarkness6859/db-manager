package com.manager.db.services;

import com.manager.db.entities.UserEntity;
import com.manager.db.enums.Roles;
import com.manager.db.enums.UserStatus;
import com.manager.db.mappers.UserMapper;
import com.manager.db.models.User;
import com.manager.db.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository repository;
    private final EmailService email;
    private final UserMapper mapper;
    private final PasswordEncoder encoder;

    UserService (UserRepository repository, EmailService email, UserMapper mapper, PasswordEncoder encoder){
        this.repository = repository;
        this.email = email;
        this.mapper = mapper;
        this.encoder = encoder;
    }

    public List<User> getAll () {

        return repository.findAll()
                .stream()
                .map(mapper::toDomain)
                .toList();

    }

    public List<User> getByNameOrEmail (String keyword){

        if (keyword == null || keyword.isBlank()){

           return repository.findAll()
                   .stream()
                   .map(mapper::toDomain)
                   .toList();

        }

        return repository.findByNameOrEmail(keyword, keyword)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    public User save (User user) {

        if (user == null){
            return null;
        }

        Optional<UserEntity> existingUser = repository.findByEmail(user.getEmail());

        if (existingUser.isPresent()) {
            throw new RuntimeException("The email is already registered");
        }

        UserEntity entity = mapper.toEntity(user);

        entity.setPassword(encoder.encode(user.getPassword()));
        entity.setStatus(UserStatus.ACTIVATE);
        entity.setRol(Roles.USER);

        String subject = "¡Welcome to Crudzaso DB Manager!";
        String body = "Hello, " + entity.getName() + ",\n\n" +
                "your account has been created correctly in our platform";

        email.sendEmail(entity.getEmail(), subject, body);

        return mapper.toDomain(repository.save(entity));

    }

    public void blockUser (UUID id){

        UserEntity entity = repository.findById(id).orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        entity.setStatus(UserStatus.BLOCKED);
        repository.save(entity);

        String subject = "Your account has been blocked";
        String body = "Hello, " + entity.getName() + ",\n\n" +
                "We inform you that your account was blocked by an Admin";

        email.sendEmail(entity.getEmail(), subject, body);

    }

    public void unblockUser (UUID id){

        UserEntity entity = repository.findById(id).orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        entity.setStatus(UserStatus.ACTIVATE);
        repository.save(entity);

        String subject = "Your account has been unblocked";
        String body = "Hello, " + entity.getName() + ",\n\n" +
                "We inform you that your account was Activated by an Admin";

        email.sendEmail(entity.getEmail(), subject, body);

    }

    public User edit (UUID id, User user) {

        if (id == null){
            return null;
        }

        UserEntity entity = repository.findById(id).orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        if (!entity.getEmail().equalsIgnoreCase(user.getEmail())) {

            Optional<UserEntity> existingUser = repository.findByEmail(user.getEmail());

            if (existingUser.isPresent()) {
                throw new RuntimeException("The email is already in use by another account");
            }

            entity.setEmail(user.getEmail());

        }

        entity.setName(user.getName());
        entity.setLastName(user.getLastName());

        return mapper.toDomain(repository.save(entity));

    }

    public void deleteById (UUID id){

        if (id == null){
            throw new RuntimeException("The id can't be empty");
        }

        repository.deleteById(id);

    }
}

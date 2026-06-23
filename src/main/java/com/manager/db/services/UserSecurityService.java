package com.manager.db.services;

import com.manager.db.entities.UserEntity;
import com.manager.db.enums.UserStatus;
import com.manager.db.repositories.UserRepository;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserSecurityService implements UserDetailsService {

    private final UserRepository repository;

    UserSecurityService(UserRepository repository){
        this.repository = repository;
    }

    @Override
    @NullMarked
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        UserEntity entity = repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        boolean isBlocked = entity.getStatus() == UserStatus.BLOCKED;

        return User.builder()
                .username(entity.getEmail())
                .password(entity.getPassword())
                .roles(entity.getRol().name())
                .accountLocked(isBlocked)
                .build();

    }

}

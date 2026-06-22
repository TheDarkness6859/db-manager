package com.manager.db.models;

import com.manager.db.enums.Roles;
import com.manager.db.enums.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private UUID id;

    @NotBlank(message = "The name can't be empty")
    private String name;

    @NotBlank(message = "The last name can't be empty")
    private String lastName;

    @NotBlank(message = "The email can't be empty")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "The password can't be empty")
    private String password;

    private Roles rol;

    private UserStatus status;

    private LocalDate registerDate;

    private LocalDate lastUpdate;

}

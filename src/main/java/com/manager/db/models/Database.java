package com.manager.db.models;

import com.manager.db.models.enums.DatabaseEngine;
import com.manager.db.models.enums.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Database {

    @NotBlank(message = "The database name can't be empty")
    private String name;

    private String description;

    @NotNull(message = "The database need have a engine")
    private DatabaseEngine database;

    @NotBlank(message = "The database need have a user connection")
    private String userDatabase;

    @NotBlank(message = "The database need have password")
    private String passwordDatabase;

    @NotBlank(message = "The port can't be empty")
    private String port;

    private Status databaseStatus;

    private LocalDate databaseCreation;

    private LocalDate databaseUpdate;

    private User user;

}

package com.manager.db.services;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.manager.db.entities.DatabaseEntity;
import com.manager.db.entities.UserEntity;
import com.manager.db.enums.DatabaseStatus;
import com.manager.db.mappers.DatabaseMapper;
import com.manager.db.models.Database;
import com.manager.db.repositories.DatabaseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DatabaseService {

    private final DatabaseRepository repository;
    private final DockerClient dockerClient;
    private final EmailService sender;
    private final DatabaseMapper mapper;

    DatabaseService (DatabaseRepository repository,
                     DockerClient dockerClient,
                     EmailService sender,
                     DatabaseMapper mapper){

        this.repository = repository;
        this.dockerClient = dockerClient;
        this.sender = sender;
        this.mapper = mapper;

    }

    public List<Database> getDatabase (UUID id){

        if (id == null){
            throw new RuntimeException("The user ID cannot be null");
        }

        return repository.findByUserId(id)
                .stream()
                .map(mapper::toDomain)
                .toList();

    }

    public List<Database> getAllDatabase () {

        return repository.findAll()
                .stream()
                .map(mapper::toDomain)
                .toList();

    }

    public Database save (UUID userId, Database database){

        if (userId == null || database == null){

            throw new RuntimeException("User ID or database data cannot be null");

        }

        List<DatabaseEntity> activeDbs = repository.findByUserId(userId)
                .stream()
                .filter(db -> db.getDatabaseStatus() != DatabaseStatus.ARCHIVED)
                .toList();

        if (activeDbs.size() >= 3){
            throw new RuntimeException("Maximum limit of 3 active databases reached for this user");
        }

        boolean portExists = repository.existsByPortAndDatabaseStatus(database.getPort(), DatabaseStatus.RUNNING);

        if (portExists) {
            throw new RuntimeException("The selected port " + database.getPort() + " is already in use by another active container");
        }

        UserEntity user = repository.findById(userId)
                .map(DatabaseEntity::getUser)
                .orElseThrow(() -> new RuntimeException("User owner not found"));

        DatabaseEntity databaseEntity = mapper.toEntity(database);
        databaseEntity.setUser(user);
        databaseEntity.setDatabaseStatus(DatabaseStatus.RUNNING);

        String containerId = this.createDockerContainer(databaseEntity);
        databaseEntity.setContainerId(containerId);

        String subject = "Your new database instance is ready!";
        String body = "Hello " + user.getName() + ",\n\n" +
                "Your " + databaseEntity.getDatabaseEngine() + " database instance '" + databaseEntity.getName() + "' is running on port " + databaseEntity.getPort();

        sender.sendEmail(user.getEmail(), subject, body);

        return mapper.toDomain(repository.save(databaseEntity));

    }

    private String createDockerContainer(DatabaseEntity databaseEntity){

        String image = "";
        String password = "";
        String user = "";
        int internalPort = 5432;

        switch (databaseEntity.getDatabaseEngine()){

            case PostgreSQL -> {
                image = "postgres:16-alpine";
                password = "POSTGRES_PASSWORD=" + databaseEntity.getPasswordDatabase();
                user = "POSTGRES_USER=" + databaseEntity.getUserDatabase();
            }
            case MySQL -> {
                image = "mysql:8.0";
                password = "MYSQL_ROOT_PASSWORD=" + databaseEntity.getPasswordDatabase();
                user = "MYSQL_USER=" + databaseEntity.getUserDatabase(); // Opcional para root
            }
            case MongoDB -> {
                image = "mongo:7.0";
                password = "MONGO_INITDB_ROOT_PASSWORD=" + databaseEntity.getPasswordDatabase();
                user = "MONGO_INITDB_ROOT_USERNAME=" + databaseEntity.getUserDatabase();
            }

        }

        try {

            dockerClient.pullImageCmd(image).start().awaitCompletion();

            PortBinding portBinding = PortBinding.parse(databaseEntity.getPort() + ":" + internalPort);

            CreateContainerResponse container = dockerClient.createContainerCmd(image)
                    .withName("crudzaso-" + databaseEntity.getName() + "-" + UUID.randomUUID().toString().substring(0, 5))
                    .withEnv(user, password)
                    .withHostConfig(HostConfig.newHostConfig().withPortBindings(portBinding))
                    .exec();

            dockerClient.startContainerCmd(container.getId()).exec();

            return container.getId();

        }catch (Exception e){

            throw new RuntimeException("Docker error: Failed to create infrastructure. " + e.getMessage());

        }

    }

    public Database edit (UUID id, Database database){

        if (id == null || database == null){

            throw new RuntimeException("Database ID or data cannot be null");

        }

        DatabaseEntity entity = repository.findById(id).orElseThrow(() -> new RuntimeException("Database instance not found with id: " + id));

        if (!entity.getPort().equals(database.getPort())) {

            boolean portExists = repository.existsByPortAndDatabaseStatus(database.getPort(), DatabaseStatus.RUNNING);

            if (portExists) {

                throw new RuntimeException("The port " + database.getPort() + " is already in use by another active database");

            }

            entity.setPort(database.getPort());

        }

        entity.setName(database.getName());

        return mapper.toDomain(repository.save(entity));

    }

    public void archiveDatabase (UUID id){

        if (id == null){

            throw new RuntimeException("The id cannot be empty");

        }

        DatabaseEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("The database with id:" + id + "cannot be found"));

        try {

            dockerClient.stopContainerCmd(entity.getContainerId()).exec();

        }catch (Exception e){
            System.err.println("Could not stop container from Docker container " + e.getMessage());
        }

        try{

            dockerClient.removeContainerCmd(entity.getContainerId()).withRemoveVolumes(true).exec();

        }catch (Exception e){
            System.err.println("Could not remove container from Docker host: " + e.getMessage());
        }

        entity.setDatabaseStatus(DatabaseStatus.ARCHIVED);
        repository.save(entity);

        String subject = "Database Instance Archived";
        String body = "Your database " + entity.getName() + " has been successfully archived";

        sender.sendEmail(entity.getUser().getEmail(), subject, body);

    }

}

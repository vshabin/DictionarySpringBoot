package com.example.demo.infrastructure.repositories.user;

import io.ebean.annotation.WhenCreated;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "users")
public class UserEntity {
    public static final String ID = "id";
    public static final String LOGIN = "login";
    public static final String PASSWORD = "password";
    public static final String ROLE = "role";
    public static final String FULLNAME = "full_name";
    public static final String CREATED_AT = "created_at";
    public static final String ARCHIVEDATE = "archive_date";

    @Id
    @Column(name = ID)
    private UUID id;

    @Column(name = LOGIN)
    @NotBlank
    private String login;

    @Column(name = PASSWORD)
    @NotBlank
    private String password;

    @Column(name = ROLE)
    @NotBlank
    private String role;

    @Column(name = FULLNAME)
    @NotBlank
    private String fullName;

    @Column(name = CREATED_AT)
    @WhenCreated
    private LocalDateTime createdAt;

    @Column(name = ARCHIVEDATE)
    private LocalDateTime archived;
}

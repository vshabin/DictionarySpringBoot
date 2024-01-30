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
    public static final String FULL_NAME = "full_name";
    public static final String CREATED_AT = "created_at";
    public static final String ARCHIVE_DATE = "archive_date";
    public static final String EMAIL = "email";
    public static final String PHONE_NUMBER = "phoneNumber";

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

    @Column(name = FULL_NAME)
    @NotBlank
    private String fullName;

    @Column(name = EMAIL)
    private String email;

    @Column(name = PHONE_NUMBER)
    private String phoneNumber;

    @Column(name = CREATED_AT)
    @WhenCreated
    private LocalDateTime createdAt;

    @Column(name = ARCHIVE_DATE)
    private LocalDateTime archived;
}

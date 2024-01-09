package com.example.demo.infrastructure.repositories.language;

import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhoCreated;
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
@Table(name = "languages")
public class LanguageEntity {
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String CREATED_AT = "created_at";
    public static final String CREATED_BY_USER_ID = "created_by_user_id";
    public static final String REG_EX = "reg_ex";

    @Id
    @Column(name = ID)
    private UUID id;

    @Column(name = NAME)
    @NotBlank
    private String name;

    @Column(name = CREATED_AT)
    @WhenCreated
    private LocalDateTime createdAt;

    @Column(name = CREATED_BY_USER_ID)
    @WhoCreated
    private UUID createdByUserId;

    @Column(name = REG_EX)
    private String regEx;
}


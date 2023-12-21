package com.example.demo.infrastructure.repositories.language;

import io.ebean.annotation.DbDefault;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhoCreated;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Entity
@Data
@Table(name = "languages")
public class LanguageEntity {
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String CREATED_AT = "created_at";
    public static final String CREATED_BY_USER_ID = "created_by_user_id";

    @Id
    @Column(name = ID)
    private UUID id;

    @Column(name = NAME)
    @NotBlank
    private String name;

    @Column(name = CREATED_AT)
    @WhenCreated
    private LocalDateTime createdAt;

    @Column(name=CREATED_BY_USER_ID)
    @WhoCreated
    private UUID createdByUserId;
}


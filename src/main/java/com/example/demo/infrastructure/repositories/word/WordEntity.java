package com.example.demo.infrastructure.repositories.word;

import com.example.demo.infrastructure.repositories.language.LanguageEntity;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhoCreated;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Getter
@Entity
@Setter
@Table(name = "words")
public class WordEntity {
    public static final String ID = "id";
    public static final String WORD = "word";
    public static final String LANGUAGE = "language";
    public static final String CREATED_AT = "created_at";
    public static final String CREATED_BY_USER_ID = "created_by_user_id";

    @Id
    @Column(name = ID)
    private UUID id;

    @Column(name = WORD)
    @NotBlank
    private String word;

    @Column(name = LANGUAGE)
    private UUID languageId;

    @Column(name = CREATED_AT)
    @WhenCreated
    private LocalDateTime createdAt;

    @Column(name=CREATED_BY_USER_ID)
    @WhoCreated
    private UUID createdByUserId;
}

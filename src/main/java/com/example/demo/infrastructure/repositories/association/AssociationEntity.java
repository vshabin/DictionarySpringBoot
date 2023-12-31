package com.example.demo.infrastructure.repositories.association;

import com.example.demo.infrastructure.repositories.word.WordEntity;
import io.ebean.annotation.NotNull;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhoCreated;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "association")
public class AssociationEntity {
    public static final String ID = "id";
    public static final String WORD = "word";
    public static final String TRANSLATION = "translation";
    public static final String CREATED_AT = "created_at";
    public static final String CREATED_BY_USER_ID = "created_by_user_id";

    @Id
    @Column(name = ID)
    private UUID id;

    @Column(name = WORD)
    @NotNull
    private UUID word;

    @Column(name = TRANSLATION)
    @NotNull
    private UUID translation;

    @Column(name = CREATED_AT)
    @WhenCreated
    private LocalDateTime createdAt;

    @Column(name=CREATED_BY_USER_ID)
    @WhoCreated
    private UUID createdByUserId;
}

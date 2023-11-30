package com.example.demo.infrastructure.repositories.word;

import com.example.demo.infrastructure.repositories.language.LanguageEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Getter
@Entity
@Setter
@Table(name="words")
public class WordEntity {
    @Id
    UUID id;

    @Column(name="word")
    String word;

    @Column(name="language")
    UUID languageId;

}

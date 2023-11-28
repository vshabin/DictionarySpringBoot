package com.example.demo.infrastructure.repositories.word;

import com.example.demo.infrastructure.repositories.language.LanguageEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name="languages")
public class WordEntity {
    @Id
    @Getter
    @Setter
    UUID uuid;

    @Column(name="name")
    @Getter
    @Setter
    String Name;

    //@Column(name="language")
    @Setter
    @Getter
    @ManyToOne
    @JoinColumn(name="language",referencedColumnName = "uuid")
    LanguageEntity language;

}

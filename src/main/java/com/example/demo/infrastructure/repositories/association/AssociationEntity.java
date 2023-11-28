package com.example.demo.infrastructure.repositories.association;

import com.example.demo.infrastructure.repositories.word.WordEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name="association")
public class AssociationEntity {
    @Id
    UUID uuid;
    @ManyToOne
    @JoinColumn(name="first_word",referencedColumnName = "uuid")
    WordEntity firstWord;
    @ManyToOne
    @JoinColumn(name="second_word",referencedColumnName = "uuid")
    WordEntity secondWord;

}

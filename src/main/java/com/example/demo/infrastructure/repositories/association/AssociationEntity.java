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
    UUID id;
    @Column(name = "firstWord")
    UUID firstWordId;
    @Column(name = "secondWord")
    UUID secondWordId;

}

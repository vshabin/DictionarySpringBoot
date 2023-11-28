package com.example.demo.infrastructure.repositories.language;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name="languages")
@Data
public class LanguageEntity {
    @Id
    private UUID uuid;

    @Column(name="name")
    private String Name;

}

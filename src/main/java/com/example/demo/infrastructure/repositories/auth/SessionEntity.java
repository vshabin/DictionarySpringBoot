package com.example.demo.infrastructure.repositories.auth;

import io.ebean.annotation.WhenCreated;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Getter
@Entity
@Setter
@AllArgsConstructor
@Table(name = "sessions")
public class SessionEntity {
    public static final String ID = "id";
    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String REFRESH_EXPIRES_AT = "expires_at";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String ACCESS_EXPIRES_AT = "access_token_expires";
    public static final String USER_ID = "user_id";

    @Id
    @Column(name = ID)
    private UUID id;

    @Column(name = REFRESH_TOKEN)
    @NotBlank
    private String refreshToken;

    @Column(name = REFRESH_EXPIRES_AT)
    private Date refreshExpiresAt;

    @Column(name = ACCESS_TOKEN)
    @NotBlank
    private String accessToken;

    @Column(name = ACCESS_EXPIRES_AT)
    private Date accessExpiresAt;

    @Column(name = USER_ID)
    private UUID userId;
}

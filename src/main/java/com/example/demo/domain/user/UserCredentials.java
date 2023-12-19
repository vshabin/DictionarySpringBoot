package com.example.demo.domain.user;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

@Data
@NoArgsConstructor
public class UserCredentials {
    UUID userId;
    UUID sessionId;
}

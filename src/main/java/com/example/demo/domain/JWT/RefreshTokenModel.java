package com.example.demo.domain.JWT;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenModel {
    @NotBlank(message="Refresh token should be not empty")
    String refreshToken;
}

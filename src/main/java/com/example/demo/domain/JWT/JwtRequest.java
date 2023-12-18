package com.example.demo.domain.JWT;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JwtRequest {
    @Schema(name = "User login", example = "vshabin", required = true)
    @NotBlank(message = "Login should be not empty")
    private String login;
    @Schema(name = "User password", example = "qwerty123", required = true)
    @NotBlank(message = "Password should be not empty")
    private String password;
}

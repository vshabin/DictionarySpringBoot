package com.example.demo.domain.user;

import com.example.demo.domain.common.GeneralResultModel;
import com.example.demo.infrastructure.RoleTypeValidate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserModelPost extends GeneralResultModel {
    @Schema(name = "User login", example = "vshabin", required = true)
    @NotBlank(message = "Login should be not empty")
    private String login;
    @Schema(name = "User password", example = "qwerty123", required = true)
    @NotBlank(message = "Password should be not empty")
    private String password;
    @Schema(name = "Full name", example = "Шабин Виталий Сергеевич", required = true)
    @NotBlank(message = "Full name should be not empty")
    private String fullName;
    @Schema(name = "User role", example = "ADMIN", required = true)
    @RoleTypeValidate(anyOf = {Role.ADMIN, Role.USER})
    private Role role;
}

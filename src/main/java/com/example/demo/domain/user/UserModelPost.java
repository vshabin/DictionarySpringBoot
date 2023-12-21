package com.example.demo.domain.user;

import com.example.demo.domain.common.GeneralResultModel;
import com.example.demo.infrastructure.RoleTypeValidate;
import com.example.demo.infrastructure.security.SecurityConst;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import com.example.demo.infrastructure.security.SecurityConst.Role;

@Data
public class UserModelPost extends GeneralResultModel {
    @Schema(name = "User login", example = "vshabin", requiredMode= Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Login should be not empty")
    private String login;
    @Schema(name = "User password", example = "qwerty123", requiredMode= Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Password should be not empty")
    private String password;
    @Schema(name = "Full name", example = "Шабин Виталий Сергеевич", requiredMode= Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Full name should be not empty")
    private String fullName;
    @Schema(name = "User role", example = "ADMIN", requiredMode= Schema.RequiredMode.REQUIRED)
    @RoleTypeValidate(anyOf = {Role.ADMIN, Role.USER})
    private Role role;
}

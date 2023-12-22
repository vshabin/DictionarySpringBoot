package com.example.demo.domain.user;

import com.example.demo.domain.common.GeneralResultModel;
import com.example.demo.infrastructure.RoleTypeValidate;
import com.example.demo.security.SecurityConst.Role;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserModelReturn extends GeneralResultModel {
    @Schema(name = "User UUID", example = "1e723432-ed5e-420e-9cf8-3a51ff669735", requiredMode= Schema.RequiredMode.REQUIRED)
    private UUID id;
    @Schema(name = "User login", example = "vshabin", requiredMode= Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Login should be not empty")
    private String login;
    @Schema(name = "Full name", example = "Шабин Виталий Сергеевич",  requiredMode= Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Full name should be not empty")
    private String fullName;
    @Schema(name = "User role", example = "ADMIN", requiredMode= Schema.RequiredMode.REQUIRED)
    @RoleTypeValidate(anyOf = {Role.ADMIN, Role.USER})
    private Role role;
    @JsonFormat(pattern = "yyyy.MM.dd HH:mm:ss.SSS")
    @Schema(name = "Date of creation", example = "2023-12-03 18:59:00.655995 +00:00", requiredMode= Schema.RequiredMode.NOT_REQUIRED, hidden = true)
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy.MM.dd HH:mm:ss.SSS")
    @Schema(name = "Date of archive", example = "2023-12-03 18:59:00.655995 +00:00", requiredMode= Schema.RequiredMode.NOT_REQUIRED, hidden = true)
    private LocalDateTime archiveDate;

}

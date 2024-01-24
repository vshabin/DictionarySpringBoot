package com.example.demo.api;

import com.example.demo.domain.common.GuidResultModel;
import com.example.demo.domain.user.UserModelPost;
import com.example.demo.domain.user.UserModelReturn;
import com.example.demo.domainservices.UserService;
import com.example.demo.infrastructure.CommonUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Collection;
import java.util.UUID;

@RestController
@RequestMapping("/user")
@Tag(name = "User", description = "User management APIs")
@Validated
public class UserController {
    @Inject
    private UserService service;

    @GetMapping(value = "/searchById/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Get a user by id", description = "Returns a user as per the id or null")
    public UserModelReturn getById(@PathVariable @Parameter(name = "User UUID",
            description = "User id",
            example = "1e723432-ed5e-420e-9cf8-3a51ff669735") UUID id) {
         return service.getById(id);
    }

    @GetMapping(value = "/searchByLogin/{login}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Get a user by login", description = "Returns a user as per the login or null")
    public UserModelReturn getByLogin(@PathVariable @Parameter(name = "User login", description = "User login", example = "vshabin") String login) {
        return service.getByLogin(login);
    }
    @PutMapping(value = "/add")
    //@PreAuthorize("hasAuthority('ADMIN')")
    @PermitAll
    @Operation(summary = "Add new user", description = "Returns the created object or an error")
    public GuidResultModel add(@Valid @RequestBody UserModelPost model) {
        return service.save(model);
    }
}

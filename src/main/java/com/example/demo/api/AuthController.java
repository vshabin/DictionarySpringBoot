package com.example.demo.api;

import com.example.demo.domain.JWT.JwtRequest;
import com.example.demo.domain.JWT.JwtResponse;
import com.example.demo.domain.JWT.RefreshTokenModel;
import com.example.demo.domain.common.GeneralResultModel;
import com.example.demo.domainservices.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth", description = "Auth management APIs")
@Validated
public class AuthController {
    @Autowired
    private AuthService service;

    @PostMapping("/login")
    @PermitAll
    public JwtResponse login(@Valid @RequestBody JwtRequest request) {
        return service.login(request);
    }

    @PostMapping("/refresh")
    @PreAuthorize("isAuthenticated()")
    public JwtResponse refresh(@RequestBody @Valid RefreshTokenModel refreshToken) {
        return service.refresh(refreshToken);
    }

    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public GeneralResultModel logout(@RequestBody @Valid RefreshTokenModel refreshToken) {
        return service.logout(refreshToken);
    }
}

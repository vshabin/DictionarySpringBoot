package com.example.demo.api;

import com.example.demo.domain.JWT.JwtRequest;
import com.example.demo.domain.JWT.JwtResponse;
import com.example.demo.domain.common.GeneralResultModel;
import com.example.demo.domainservices.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth", description = "Auth management APIs")
@Validated
public class AuthController {
    @Inject
    private AuthService service;

    @PostMapping("/login")
    public JwtResponse login(@Valid @RequestBody JwtRequest request) {
        return service.login(request);
    }

    @PostMapping("/refresh")
    public JwtResponse refresh(@RequestBody String refreshToken) {
        return service.refresh(refreshToken);
    }

    @PostMapping("/logout")
    public GeneralResultModel logout(@RequestBody String refreshToken) {
        return service.logout(refreshToken);
    }
}

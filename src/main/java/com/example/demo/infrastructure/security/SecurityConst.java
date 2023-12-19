package com.example.demo.infrastructure.security;

import lombok.RequiredArgsConstructor;

public class SecurityConst {
    public static final String ROLE_CLAIM = "role";
    @RequiredArgsConstructor
    public enum Role {
        ADMIN("Админ"),
        USER("Пользователь"),
        REFRESH_TOKEN("REFRESH_TOKEN"),


        ;
        private final String description;
    }

}

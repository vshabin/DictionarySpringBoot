package com.example.demo.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@RequiredArgsConstructor
public enum Role implements GrantedAuthority {
 ADMIN("ADMIN"), USER("USER"), REFRESH_TOKEN("REFRESH_TOKEN");
    private final String value;

    @Override
    public String getAuthority() {
        return value;
    }
}

package com.example.demo.infrastructure;

import com.example.demo.domain.user.UserCredentials;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.UUID;

@Component
public class CommonUtils {
    public static UUID getUserId() {
        UserCredentials userCredentials = (UserCredentials) SecurityContextHolder.getContext().getAuthentication().getCredentials();
        return userCredentials.getUserId();
    }

    public static UUID getSessionId() {
        UserCredentials userCredentials = (UserCredentials) SecurityContextHolder.getContext().getAuthentication().getCredentials();
        return userCredentials.getSessionId();
    }
    public static Collection<? extends GrantedAuthority> test() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities();
    }
}

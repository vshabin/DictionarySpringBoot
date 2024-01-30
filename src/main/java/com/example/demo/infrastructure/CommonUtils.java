package com.example.demo.infrastructure;

import com.example.demo.domain.user.UserCredentials;
import com.example.demo.security.SecurityConst;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
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

    public static Authentication getSchedulerAuth() {
        var role = SecurityConst.Role.SCHEDULER.name();
        var user = new UserCredentials();
        user.setUserId(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        var authorities = List.of(new SimpleGrantedAuthority(role));
        return new UsernamePasswordAuthenticationToken(user.getUserId(), user, authorities);
    }

    public static String escape(String string, char esc) {
        return esc + string + esc;
    }
}

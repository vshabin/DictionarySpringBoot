package com.example.demo.infrastructure.security;

import com.example.demo.domain.user.UserCredentials;
import com.example.demo.domainservices.JwtProvider;

import com.example.demo.domainservices.UserService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {
    private static final String AUTHORIZATION = "Authorization";

    @Autowired
    private JwtProvider jwtProvider;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String authHeader = request.getHeader(AUTHORIZATION);
            if(authHeader==null){
                filterChain.doFilter(request, response);
                return;
            }
            var token = authHeader.substring(7);
            var claims = jwtProvider.getAccessClaims(token);

            var userCredentials = new UserCredentials();
            //TODO чек на null
            userCredentials.setUserId(UUID.fromString(claims.get("userId", String.class)));
            userCredentials.setSessionId(UUID.fromString(claims.get("sessionId", String.class)));
            var role = claims.get("role", String.class);

            var authorities = List.of(new SimpleGrantedAuthority(role));

            var authToken = new UsernamePasswordAuthenticationToken(claims.getSubject(), userCredentials, authorities);
            //authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            response.setStatus(403);
        }
    }
}

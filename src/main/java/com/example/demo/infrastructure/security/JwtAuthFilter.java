package com.example.demo.infrastructure.security;

import com.example.demo.domain.common.GeneralResultModel;
import com.example.demo.domain.user.UserCredentials;
import com.example.demo.domainservices.JwtProvider;
import com.example.demo.infrastructure.JsonUtils;
import jakarta.servlet.FilterChain;
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
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends GenericFilterBean {
    private static final String AUTHORIZATION = "Authorization";
    private static final String JWT_FILTER_ERROR_CODE = "JWT_FILTER_ERROR_CODE";
    private static final String JWT_FILTER_ERROR_MESSAGE = "An error occurred in the JWT filter with message: ";

    @Autowired
    private JwtProvider jwtProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException {
        try {
            String path = ((HttpServletRequest) request).getRequestURI();
            if ("/auth/login".equals(path)) {
                chain.doFilter(request, response);
                return;
            }

            String authHeader = ((HttpServletRequest) request).getHeader(AUTHORIZATION);
            if (authHeader == null) {
                throw new Exception("Missing access token");
            }
            var token = authHeader.substring(authHeader.lastIndexOf(' ') + 1);
            var claims = jwtProvider.getAccessClaims(token);

            var userCredentials = new UserCredentials();

            if (claims.get(SecurityConst.USER_ID_CLAIM, String.class) != null) {
                userCredentials.setUserId(UUID.fromString(claims.get(SecurityConst.USER_ID_CLAIM, String.class)));
            }
            if (claims.get(SecurityConst.SESSION_ID_CLAIM, String.class) != null) {
                userCredentials.setSessionId(UUID.fromString(claims.get(SecurityConst.SESSION_ID_CLAIM, String.class)));
            }

            var role = claims.get(SecurityConst.ROLE_CLAIM, String.class);
            var authorities = List.of(new SimpleGrantedAuthority(role));

            var authToken = new UsernamePasswordAuthenticationToken(claims.getSubject(), userCredentials, authorities);
            SecurityContextHolder.getContext().setAuthentication(authToken);

            chain.doFilter(request, response);
        } catch (Exception e) {
            ((HttpServletResponse) response).setStatus(403);
            response.setContentType("application/json");
            response.getWriter().write(JsonUtils.toJson(new GeneralResultModel(JWT_FILTER_ERROR_CODE, JWT_FILTER_ERROR_MESSAGE + e.getMessage())));
            response.getWriter().flush();
        }
    }
}

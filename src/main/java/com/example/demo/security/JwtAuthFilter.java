package com.example.demo.security;

import com.example.demo.domain.JWT.ParsingResult;
import com.example.demo.domain.common.GeneralResultModel;
import com.example.demo.domain.user.UserCredentials;
import com.example.demo.domainservices.JwtProvider;
import com.example.demo.infrastructure.JsonUtils;
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
    private static final String JWT_FILTER_ERROR_MESSAGE = "An error occurred in the JWT filter";

    @Autowired
    private JwtProvider jwtProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        var parsingResult = getAuthentication((HttpServletRequest) request);
        if (parsingResult.getParsingResultType() == ParsingResult.ParsingResultType.ERROR) {
            ((HttpServletResponse) response).setStatus(403);
            response.setContentType("application/json");
            response.getWriter().write(JsonUtils.toString(new GeneralResultModel(JWT_FILTER_ERROR_CODE, JWT_FILTER_ERROR_MESSAGE)));
            response.getWriter().flush();
            return;
        }
        SecurityContextHolder.getContext().setAuthentication(parsingResult.getAuthentication());
        chain.doFilter(request,response);
    }

    private ParsingResult getAuthentication(HttpServletRequest request){
        try {
            String authHeader = ((HttpServletRequest) request).getHeader(AUTHORIZATION);
            if (authHeader == null) {
                return new ParsingResult(ParsingResult.ParsingResultType.UNAUTHORIZED, null);
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

            var auth = new UsernamePasswordAuthenticationToken(claims.getSubject(), userCredentials, authorities);
            return new ParsingResult(ParsingResult.ParsingResultType.OK, auth);
        } catch (Exception e) {
            return new ParsingResult(ParsingResult.ParsingResultType.ERROR, null);
        }
    }
}

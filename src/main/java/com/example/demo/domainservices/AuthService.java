package com.example.demo.domainservices;

import com.example.demo.domain.JWT.JwtRequest;
import com.example.demo.domain.JWT.JwtResponse;
import com.example.demo.domain.JWT.RefreshTokenModel;
import com.example.demo.domain.common.GeneralResultModel;
import com.example.demo.domain.user.UserModelReturn;
import com.example.demo.infrastructure.repositories.auth.SessionRepository;
import com.example.demo.infrastructure.security.SecurityConst;
import org.springframework.stereotype.Service;
import com.example.demo.infrastructure.security.SecurityConst.Role;

import javax.inject.Inject;
import java.util.UUID;

@Service
public class AuthService {
    @Inject
    private SessionRepository repository;
    @Inject
    private JwtProvider tokenProvider;
    @Inject
    private UserService userService;

    private static final String USER_NOT_FOUND_CODE = "USER_NOT_FOUND_CODE";
    private static final String USER_NOT_FOUND_MESSAGE = "User not found";
    private static final String INCORRECT_PASSWORD_CODE = "INCORRECT_PASSWORD_CODE";
    private static final String INCORRECT_PASSWORD_MESSAGE = "Incorrect password";
    private static final String INCORRECT_REFRESH_TOKEN_CODE = "INCORRECT_REFRESH_TOKEN_CODE";
    private static final String INCORRECT_REFRESH_TOKEN_MESSAGE = "Incorrect refresh token";


    public JwtResponse login(JwtRequest request) {
        if(!userService.exists(request.getLogin())) {
            return new JwtResponse(USER_NOT_FOUND_CODE, USER_NOT_FOUND_MESSAGE);
        }
        if(!userService.isRightPassword(request.getLogin(), request.getPassword())){
            return  new JwtResponse(INCORRECT_PASSWORD_CODE, INCORRECT_PASSWORD_MESSAGE);
        }
        UserModelReturn user = userService.getByLogin(request.getLogin());
        UUID sessionId = UUID.randomUUID();
        var accessToken = tokenProvider.generateAccessToken(user.getLogin(), user.getId(), sessionId, user.getRole());
        var refreshToken = tokenProvider.generateRefreshToken(user.getLogin(), user.getId(), sessionId);

        repository.save(sessionId, refreshToken, tokenProvider.getRefreshClaims(refreshToken).getExpiration(), accessToken, tokenProvider.getAccessClaims(accessToken).getExpiration(), user.getId());
        return new JwtResponse(accessToken,refreshToken,"", "");
    }

    public JwtResponse refresh(RefreshTokenModel model) {
        if(!repository.exists(model.getRefreshToken())){
            return new JwtResponse(INCORRECT_REFRESH_TOKEN_CODE, INCORRECT_REFRESH_TOKEN_MESSAGE);
        }
        var claims = tokenProvider.getRefreshClaims(model.getRefreshToken());
        String login = claims.getSubject();
        UUID userId= UUID.fromString(claims.get(SecurityConst.USER_ID_CLAIM,String.class));
        UUID sessionId = UUID.fromString(claims.get(SecurityConst.SESSION_ID_CLAIM,String.class));
        Role role = Role.valueOf(claims.get(SecurityConst.ROLE_CLAIM, String.class));
        String accessToken = tokenProvider.generateAccessToken(login, userId, sessionId, role);
        String newRefreshToken = tokenProvider.generateRefreshToken(login, userId, sessionId);
        repository.update(sessionId,newRefreshToken,tokenProvider.getRefreshClaims(newRefreshToken).getExpiration(),accessToken, tokenProvider.getAccessClaims(accessToken).getExpiration());
        return new JwtResponse(accessToken, newRefreshToken,"","");
    }

    public GeneralResultModel logout(RefreshTokenModel model) {
        if(!repository.exists(model.getRefreshToken())){
            return new GeneralResultModel(INCORRECT_REFRESH_TOKEN_CODE, INCORRECT_REFRESH_TOKEN_MESSAGE);
        }
        return repository.delete(model.getRefreshToken());
    }
}

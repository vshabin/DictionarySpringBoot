package com.example.demo.domainservices;

import com.example.demo.domain.common.GuidResultModel;
import com.example.demo.domain.user.UserModelPost;
import com.example.demo.domain.user.UserModelReturn;
import com.example.demo.infrastructure.repositories.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.UUID;

@Service
public class UserService {
    private static final String USER_ALREADY_EXIST_ERROR_CODE = "USER_ALREADY_EXIST";
    private static final String USER_ALREADY_EXIST_ERROR_MESSAGE = "Такой пользователь уже существует: ";
    @Inject
    private UserRepository repository;
    @Inject
    private PasswordEncoder encoder;

    public UserModelReturn getById(UUID id) {
        return repository.getById(id);
    }

    public UserModelReturn getByLogin(String login) {
        return repository.getByLogin(login);
    }

    public GuidResultModel save(UserModelPost model) {
        if (repository.exists(model.getLogin())) {
            return new GuidResultModel(USER_ALREADY_EXIST_ERROR_CODE, USER_ALREADY_EXIST_ERROR_MESSAGE + model.getLogin());
        }
        model.setPassword(encoder.encode(model.getPassword()));
        return repository.save(model);
    }

    public boolean isRightPassword(String login, String password) {
        return encoder.matches(password, repository.getEncodedPassword(login));
    }

    public boolean exists(String login) {
        return repository.exists(login);
    }


}

package com.example.demo.security;

import com.example.demo.domain.common.GeneralResultModel;
import com.example.demo.domainservices.TelegramBot;
import com.example.demo.infrastructure.JsonUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.io.IOException;
import java.util.stream.Collectors;

@ControllerAdvice
@Log4j2
public class ApplicationExceptionHandler {
    static final String VALIDATION_ERROR = "VALIDATION_ERROR";
    static final String ACCESS_ERROR = "ACCESS_ERROR";
    static final String NO_ACCESS_TOKEN_ERROR_CODE = "NO_ACCESS_TOKEN_ERROR";
    static final String NO_ACCESS_TOKEN_ERROR_MESSAGE = "Access token wasn't found";
    static final String UNKNOWN_ERROR = "UNKNOWN_ERROR";
    @Autowired
    @Lazy
    private TelegramBot telegramBot;

    @ExceptionHandler(value = Exception.class)
    public void processUncaughtApplicationException(HttpServletRequest req, HttpServletResponse response, Exception e) throws IOException {
        log.error(e.getMessage(), e);
        telegramBot.sendMessage(e.getMessage());

        var errorMessage = e.getMessage();
        var errorCode = UNKNOWN_ERROR;
        if (e instanceof MethodArgumentTypeMismatchException) {
            errorCode = VALIDATION_ERROR;
            errorMessage = String.format("The parameter '%s' of value '%s' could not be converted to type '%s'", ((MethodArgumentTypeMismatchException) e).getName(), ((MethodArgumentTypeMismatchException) e).getValue(), ((MethodArgumentTypeMismatchException) e).getRequiredType().getSimpleName());
            response.setStatus(400);
        }
        if (e instanceof MethodArgumentNotValidException) {
            errorCode = VALIDATION_ERROR;
            if (((MethodArgumentNotValidException) e).getBindingResult().getFieldErrorCount() > 0) {
                errorMessage = ((MethodArgumentNotValidException) e).getBindingResult().getFieldError().getDefaultMessage();
            }
            response.setStatus(400);
        }
        if (e instanceof HttpMessageNotReadableException) {
            errorCode = VALIDATION_ERROR;
            response.setStatus(400);
        }
        if (e instanceof ConstraintViolationException) {
            errorCode = VALIDATION_ERROR;
            errorMessage = ((ConstraintViolationException) e).getConstraintViolations().stream()
                    .map(jakarta.validation.ConstraintViolation::getMessage)
                    .collect(Collectors.joining(" "));
            response.setStatus(400);

        }
        if (e instanceof AccessDeniedException) {
            errorCode = ACCESS_ERROR;
            response.setStatus(403);
        }
        if (e instanceof AuthenticationCredentialsNotFoundException) {
            errorCode = NO_ACCESS_TOKEN_ERROR_CODE;
            errorMessage = NO_ACCESS_TOKEN_ERROR_MESSAGE;
            response.setStatus(403);
        }

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().write(JsonUtils.toString(new GeneralResultModel(errorCode, errorMessage)));
        response.getWriter().flush();
    }
}

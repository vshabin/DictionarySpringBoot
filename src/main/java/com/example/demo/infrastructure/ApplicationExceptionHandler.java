package com.example.demo.infrastructure;

import com.example.demo.domain.common.GeneralResultModel;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.io.IOException;

@ControllerAdvice
@Qualifier("exceptionHandler")
public class ApplicationExceptionHandler {
    static final String VALIDATION_ERROR = "VALIDATION_ERROR";
    static final String UNKNOWN_ERROR = "UNKNOWN_ERROR";

    @ExceptionHandler(value = Exception.class)
    public void processUncaughtApplicationException(HttpServletRequest req, HttpServletResponse response, Exception e) throws IOException {
        System.out.println(e.getClass());
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
            ((ConstraintViolationException) e).getConstraintViolations().stream().map(jakarta.validation.ConstraintViolation::getMessage).forEach(errorMessage::concat);
            response.setStatus(400);

        }

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().write(JsonUtils.toJson(new GeneralResultModel(errorCode, errorMessage)));
        response.getWriter().flush();
    }
}

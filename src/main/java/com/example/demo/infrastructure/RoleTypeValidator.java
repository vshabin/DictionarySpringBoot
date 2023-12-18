package com.example.demo.infrastructure;

import com.example.demo.domain.user.Role;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class RoleTypeValidator implements ConstraintValidator<RoleTypeValidate, Role> {
    private Role[] subset;
    @Override
    public void initialize(RoleTypeValidate constraintAnnotation) {
        this.subset = constraintAnnotation.anyOf();
    }

    @Override
    public boolean isValid(Role value, ConstraintValidatorContext context) {
        return value == null || Arrays.asList(subset).contains(value);
    }
}

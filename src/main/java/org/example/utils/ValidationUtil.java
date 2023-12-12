package org.example.utils;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.example.dtos.InputErrorDto;
import org.example.exceptions.InputNotValidException;

import java.util.ArrayList;
import java.util.Set;

public class ValidationUtil {
    public static <T> void runValidation(T object) {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<T>> violations = validator.validate(object);
        ArrayList<InputErrorDto> errors = new ArrayList<>();
        if (!violations.isEmpty()) {
            for (ConstraintViolation<T> violation : violations) {
                InputErrorDto error = new InputErrorDto(violation.getPropertyPath().toString(), violation.getMessage());
                errors.add(error);
            }
            throw new InputNotValidException("Validation error occurred", errors);
        }
    }
}

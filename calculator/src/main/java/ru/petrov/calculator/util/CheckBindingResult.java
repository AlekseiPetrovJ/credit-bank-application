package ru.petrov.calculator.util;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import ru.petrov.calculator.util.exception.NotValidDto;

import java.util.List;

public class CheckBindingResult {
    public void check(BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMsg.append(error.getField())
                        .append(" - ")
                        .append(error.getDefaultMessage() == null ? error.getCode() : error.getDefaultMessage())
                        .append(";");
            }
            throw new NotValidDto(errorMsg.toString());
        }
    }

}

package ru.petrov.calculator.util.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.petrov.calculator.dto.LoanStatementRequestDto;

import java.time.LocalDate;
import java.time.Period;

@Component
public class LoanStatementRequestDtoValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return LoanStatementRequestDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        LoanStatementRequestDto requestDto = (LoanStatementRequestDto) target;
        //todo добавить проверку возраста
        if (Period.between(requestDto.getBirthdate(), LocalDate.now()).getYears()<18) {
            errors.rejectValue("birthdate", "", "should be no later than 18 years from the current date");
        }
    }
}
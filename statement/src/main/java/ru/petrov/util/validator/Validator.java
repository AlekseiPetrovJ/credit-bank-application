package ru.petrov.util.validator;

import org.springframework.stereotype.Component;
import ru.petrov.dto.LoanStatementRequestDto;
import ru.petrov.util.exception.NotValidDto;

import java.time.LocalDate;
import java.time.Period;

@Component
public class Validator {

    public static  void validateAgeOlder18(LoanStatementRequestDto request) {
        ageOlder18(request.getBirthdate());
    }

    private static void ageOlder18(LocalDate birthdate){
        if (Period.between(birthdate, LocalDate.now()).getYears()<18) {
            throw new NotValidDto("age should be no later than 18 years from the current date");
        }
    }
}
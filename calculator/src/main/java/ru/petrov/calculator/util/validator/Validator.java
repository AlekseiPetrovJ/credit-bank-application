package ru.petrov.calculator.util.validator;

import org.springframework.stereotype.Component;
import ru.petrov.calculator.dto.LoanStatementRequestDto;
import ru.petrov.calculator.dto.ScoringDataDto;
import ru.petrov.calculator.util.exception.NotValidDto;

import java.time.LocalDate;
import java.time.Period;

@Component
public class Validator {

    public static  void validateAgeOlder18(LoanStatementRequestDto request) {
        ageOlder18(request.getBirthdate());
    }

    public static void validateAgeOlder18(ScoringDataDto scoringDataDto) {
        ageOlder18(scoringDataDto.getBirthdate());
    }

    private static void ageOlder18(LocalDate birthdate){
        if (Period.between(birthdate, LocalDate.now()).getYears()<18) {
            throw new NotValidDto("age should be no later than 18 years from the current date");
        }
    }
}
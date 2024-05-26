package ru.petrov.calculator.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class LoanStatementRequestDto {
    private final BigDecimal amount;
    /**
     * Срок кредита в месяцах
     */
    private final Integer term;
    private final String firstName;
    private final String lastName;
    private final String middleName;
    private final String email;
    private final LocalDate birthdate;
    private final String passportSeries;
    private final String passportNumber;
}

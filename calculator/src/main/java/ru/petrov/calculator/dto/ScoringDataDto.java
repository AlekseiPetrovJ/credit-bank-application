package ru.petrov.calculator.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ScoringDataDto {
    private final BigDecimal amount;
    private final Integer term;
    private final String firstName;
    private final String lastName;
    private final String middleName;
    private final Gender gender;
    private final LocalDate birthdate;
    private final String passportSeries;
    private final String passportNumber;
    private final LocalDate passportIssueDate;
    private final String passportIssueBranch;
    private final MaritalStatus maritalStatus;
    private final Integer dependentAmount;
    private final EmploymentDto employment;
    private final String accountNumber;
    private final Boolean isInsuranceEnabled;
    private final Boolean isSalaryClient;

}

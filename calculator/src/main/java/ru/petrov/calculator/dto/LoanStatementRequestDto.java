package ru.petrov.calculator.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class LoanStatementRequestDto {
    @DecimalMin(value = "30000")
    @NotNull
    //todo Уточнить как проверять, что ввели число.
    private final BigDecimal amount;

    /**
     * Срок кредита в месяцах
     */
    @Min(6)
    @NotNull
    private final Integer term;

    @Pattern(regexp = "^[a-zA-Z]{2,30}$", message = "First name might  contain 2-30 latin char")
    @NotEmpty(message = "First name not might be empty")
    private final String firstName;

    @Pattern(regexp = "^[a-zA-Z]{2,30}$", message = "Last name might  contain 2-30 latin char")
    @NotEmpty(message = "Last name not might be empty")
    private final String lastName;

    @Pattern(regexp = "^[a-zA-Z]{2,30}$", message = "Middle name might  contain 2-30 latin char")
    private final String middleName;

    @Pattern(regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$", message = "email should be like login@domain")
    private final String email;

    @NotNull(message = "Birth date is required")
    @Past(message = "Birth date must be in the past")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private final LocalDate birthdate;


    @Pattern(regexp = "^[0-9]{4}$", message = "Passport series should be contain 4 digit")
    private final String passportSeries;

    @Pattern(regexp = "^[0-9]{6}$", message = "Passport number should be contain 6 digit")
    private final String passportNumber;
}

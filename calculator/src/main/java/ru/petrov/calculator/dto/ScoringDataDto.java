package ru.petrov.calculator.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import ru.petrov.calculator.dto.enums.Gender;
import ru.petrov.calculator.dto.enums.MaritalStatus;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ScoringDataDto {
    //todo уточнить Может создать базовый класс LoanStatementRequestDto
    @DecimalMin(value = "30000")
    @NotNull
    @Schema(example = "35000")
    private final BigDecimal amount;
    /**
     * Срок кредита в месяцах
     */
    @Min(6)
    @NotNull
    @Schema(example = "6")
    private final Integer term;

    @Pattern(regexp = "^[a-zA-Z]{2,30}$", message = "First name might  contain 2-30 latin char")
    @NotBlank(message = "First name not might be blank")
    @Schema(example = "Ivan")
    private final String firstName;

    @Pattern(regexp = "^[a-zA-Z]{2,30}$", message = "Last name might  contain 2-30 latin char")
    @NotBlank(message = "Last name not might be blank")
    @Schema(example = "Petrov")
    private final String lastName;

    @Pattern(regexp = "^[a-zA-Z]{2,30}$", message = "Middle name might  contain 2-30 latin char")
    @Schema(example = "Ivanovich")
    private final String middleName;

    @NotNull
    private final Gender gender;

    @NotNull(message = "Birth date is required")
    @Past(message = "Birth date must be in the past")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(example = "2000-02-15")
    private final LocalDate birthdate;

    @Pattern(regexp = "^[0-9]{4}$", message = "Passport series should be contain 4 digit")
    @Schema(example = "7007")
    private final String passportSeries;

    @Pattern(regexp = "^[0-9]{6}$", message = "Passport number should be contain 6 digit")
    @Schema(example = "777888")
    private final String passportNumber;

    @NotNull
    @Past(message = "Birth date must be in the past")
    private final LocalDate passportIssueDate;

    @NotBlank
    private final String passportIssueBranch;

    @NotNull
    private final MaritalStatus maritalStatus;

    @NotNull
    @Min(0)
    private final Integer dependentAmount;

    @NotNull
    @Valid
    private final EmploymentDto employment;

    @NotBlank
    private final String accountNumber;

    @NotNull
    private final Boolean isInsuranceEnabled;

    @NotNull
    private final Boolean isSalaryClient;
}

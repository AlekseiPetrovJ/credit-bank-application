package ru.petrov.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ru.petrov.models.enums.Gender;
import ru.petrov.models.enums.MaritalStatus;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScoringDataDto {
    @DecimalMin(value = "30000")
    @NotNull
    @Schema(example = "35000")
    private BigDecimal amount;
    /**
     * Срок кредита в месяцах
     */
    @Min(6)
    @NotNull
    @Schema(example = "6")
    private Integer term;

    @Pattern(regexp = "^[a-zA-Z]{2,30}$", message = "First name might  contain 2-30 latin char")
    @NotBlank(message = "First name not might be blank")
    @Schema(example = "Ivan")
    private String firstName;

    @Pattern(regexp = "^[a-zA-Z]{2,30}$", message = "Last name might  contain 2-30 latin char")
    @NotBlank(message = "Last name not might be blank")
    @Schema(example = "Petrov")
    private String lastName;

    @Pattern(regexp = "^[a-zA-Z]{2,30}$", message = "Middle name might  contain 2-30 latin char")
    @Schema(example = "Ivanovich")
    private String middleName;

    @NotNull
    private Gender gender;

    @NotNull(message = "Birth date is required")
    @Past(message = "Birth date must be in the past")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(example = "2000-02-15")
    private LocalDate birthdate;

    @Pattern(regexp = "^[0-9]{4}$", message = "Passport series should be contain 4 digit")
    @Schema(example = "7007")
    private String passportSeries;

    @Pattern(regexp = "^[0-9]{6}$", message = "Passport number should be contain 6 digit")
    @Schema(example = "777888")
    private String passportNumber;

    @NotNull
    @Past(message = "Birth date must be in the past")
    private LocalDate passportIssueDate;

    @NotBlank
    private String passportIssueBranch;

    @NotNull
    private MaritalStatus maritalStatus;

    @NotNull
    @Min(0)
    private Integer dependentAmount;

    @NotNull
    @Valid
    private EmploymentDto employment;

    @NotBlank
    private String accountNumber;

    @NotNull
    private Boolean isInsuranceEnabled;

    @NotNull
    private Boolean isSalaryClient;
}

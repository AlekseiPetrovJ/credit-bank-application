package ru.petrov.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class LoanStatementRequestDto {
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
    @NotEmpty(message = "First name not might be empty")
    @Schema(example = "Ivan")
    private final String firstName;

    @Pattern(regexp = "^[a-zA-Z]{2,30}$", message = "Last name might  contain 2-30 latin char")
    @NotEmpty(message = "Last name not might be empty")
    @Schema(example = "Petrov")
    private final String lastName;

    @Pattern(regexp = "^[a-zA-Z]{2,30}$", message = "Middle name might  contain 2-30 latin char")
    @Schema(example = "Ivanovich")
    private final String middleName;

    @Pattern(regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$", message = "email should be like login@domain")
    @Schema(example = "ivan@gmail.com")
    private final String email;

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
}

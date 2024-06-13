package ru.petrov.dto;

import lombok.Data;
import ru.petrov.models.enums.Gender;
import ru.petrov.models.enums.MaritalStatus;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Data
public class FinishRegistrationRequestDto {

    @NotNull
    private final Gender gender;

    @NotNull
    private final MaritalStatus maritalStatus;

    @NotNull
    @Min(0)
    private final Integer dependentAmount;

    @NotNull
    @Past(message = "Birth date must be in the past")
    private final LocalDate passportIssueDate;

    @NotBlank
    private final String passportIssueBranch;

    @NotNull
    @Valid
    private final EmploymentDto employment;

    @NotBlank
    private final String accountNumber;
}
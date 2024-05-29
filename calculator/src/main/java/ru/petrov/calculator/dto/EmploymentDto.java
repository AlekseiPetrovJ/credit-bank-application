package ru.petrov.calculator.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import ru.petrov.calculator.dto.enums.EmploymentStatus;
import ru.petrov.calculator.dto.enums.Position;

import java.math.BigDecimal;

@Data
public class EmploymentDto {
    @Schema(example = "SELF_EMPLOYED")
    private final EmploymentStatus employmentStatus;
    private final String employerINN;
    @Schema(example = "50000")
    private final BigDecimal salary;
    private final Position position;
    @Schema(example = "36")
    private final Integer workExperienceTotal;
    @Schema(example = "36")
    private final Integer workExperienceCurrent;
}
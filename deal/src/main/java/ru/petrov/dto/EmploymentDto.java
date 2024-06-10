package ru.petrov.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import ru.petrov.dto.enums.EmploymentStatus;
import ru.petrov.dto.enums.Position;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class EmploymentDto {
    @NotNull
    @Schema(example = "SELF_EMPLOYED")
    private final EmploymentStatus employmentStatus;
    @NotBlank
    private final String employerINN;
    @NotNull
    @DecimalMin(value = "0.0")
    @Schema(example = "50000")
    private final BigDecimal salary;
    @NotNull
    private final Position position;
    @NotNull
    @Min(0)
    @Schema(example = "36")
    private final Integer workExperienceTotal;
    @NotNull
    @Min(0)
    @Schema(example = "36")
    private final Integer workExperienceCurrent;
}
package ru.petrov.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.petrov.models.enums.EmploymentStatus;
import ru.petrov.models.enums.Position;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmploymentDto {
    @NotNull
    @Schema(example = "SELF_EMPLOYED")
    private EmploymentStatus employmentStatus;
    @NotBlank
    private String employerINN;
    @NotNull
    @DecimalMin(value = "0.0")
    @Schema(example = "50000")
    private BigDecimal salary;
    @NotNull
    private Position position;
    @NotNull
    @Min(0)
    @Schema(example = "36")
    private Integer workExperienceTotal;
    @NotNull
    @Min(0)
    @Schema(example = "36")
    private Integer workExperienceCurrent;
}
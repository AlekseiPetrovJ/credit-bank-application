package ru.petrov.calculator.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class EmploymentDto {
    private final EmploymentStatus employmentStatus;
    private final String employerINN;
    private final BigDecimal salary;
    private final Position position;
    private final Integer workExperienceTotal;
    private final Integer workExperienceCurrent;
}
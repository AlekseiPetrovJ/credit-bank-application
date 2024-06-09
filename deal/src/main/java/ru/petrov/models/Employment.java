package ru.petrov.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.petrov.models.enums.EmploymentStatus;
import ru.petrov.models.enums.Position;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class Employment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID employmentId;
    private EmploymentStatus status;
    private String employerInn;
    private BigDecimal salary;
    private Position position;
    private Integer workExperienceTotal;
    private Integer workExperienceCurrent;
}
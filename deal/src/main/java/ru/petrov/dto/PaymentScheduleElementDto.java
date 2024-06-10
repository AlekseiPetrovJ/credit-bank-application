package ru.petrov.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PaymentScheduleElementDto {
    private final Integer number;
    private final LocalDate date;
    private final BigDecimal totalPayment;
    private final BigDecimal interestPayment;
    private final BigDecimal debtPayment;
    private final BigDecimal remainingDebt;
}

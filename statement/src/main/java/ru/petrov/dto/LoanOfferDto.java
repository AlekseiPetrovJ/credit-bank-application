package ru.petrov.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class LoanOfferDto {
    private final UUID statementId;
    private final BigDecimal requestedAmount;
    private final BigDecimal totalAmount;
    /**
     * Срок кредита в месяцах
     */
    private final Integer term;
    private final BigDecimal monthlyPayment;
    /**
     * Ставка по кредиту — годовой процент за использование заёмных денег
     */
    private final BigDecimal rate;
    private final Boolean isInsuranceEnabled;
    private final Boolean isSalaryClient;
}
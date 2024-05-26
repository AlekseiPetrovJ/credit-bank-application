package ru.petrov.calculator.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@ConfigurationProperties(prefix="calculator.rates")
@Data
public class ScoringProps {
    /**
     * Ставка по кредиту — это годовой процент
     */
    private BigDecimal basicYearRate;
    /**
     * Процент для расчета цены страховки
     */
    private BigDecimal insuranceRate;
    /**
     * Процент на который уменьшается размер базовой ставки при условии страхования
     */
    private BigDecimal insuranceFactor;
    /**
     * Процент на который уменьшается размер базовой ставки для зарплатных клиентов
     */
    private BigDecimal salaryClientFactor;
}

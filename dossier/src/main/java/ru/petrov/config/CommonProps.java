package ru.petrov.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
@ConfigurationProperties(prefix="dossier.properties")
@Data
public class CommonProps {
    private String dealUrl = "http://127.0.0.1:8081";
    /**
     * Ставка по кредиту — это годовой процент
     */
    private BigDecimal basicYearRate =  BigDecimal.valueOf(10);
    /**
     * Процент для расчета цены страховки
     */
    private BigDecimal insuranceRate =  BigDecimal.valueOf(2);
    /**
     * Процент на который уменьшается размер базовой ставки при условии страхования
     */
    private BigDecimal insuranceFactor = BigDecimal.valueOf(3);
    /**
     * Процент на который уменьшается размер базовой ставки для зарплатных клиентов
     */
    private BigDecimal salaryClientFactor = BigDecimal.ONE;

    private Integer minAgeForPrescoring = 18;
    private Integer minAgeForLoan = 20;
    private Integer maxAgeForLoan = 65;

    private Integer minWorkExperienceTotal = 18;
    private Integer minWorkExperienceCurrent = 3;

    private BigDecimal factorBetweenLoanAndSalaryAllowLoan = BigDecimal.valueOf(25);

    private BigDecimal selfEmployedPoints = BigDecimal.ONE;
    private BigDecimal businessOwnerPoints = BigDecimal.valueOf(2);

    private BigDecimal middleManagerPoints = BigDecimal.valueOf(2);
    private BigDecimal topManagerPoints = BigDecimal.valueOf(3);

    private BigDecimal marriedPoints = BigDecimal.valueOf(3);
    private BigDecimal divorcedPoints = BigDecimal.ONE;

    private Integer minAgeMaleForPoints = 30;
    private Integer maxAgeMaleForPoints = 55;
    private BigDecimal ageMalePoints = BigDecimal.valueOf(3);

    private Integer minAgeFemaleForPoints = 32;
    private Integer maxAgeFemaleForPoints = 60;
    private BigDecimal ageFemalePoints = BigDecimal.valueOf(3);
}

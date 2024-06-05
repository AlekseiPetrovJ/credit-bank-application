package ru.petrov.calculator.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.petrov.calculator.config.RoundingProps;
import ru.petrov.calculator.config.ScoringProps;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class InsuranceTest {
    @Autowired
    private ScoringProps scoringProps;
    @Autowired
    private RoundingProps roundingProps;
    @Autowired
    private Insurance insurance;

    @ParameterizedTest
    @ValueSource(ints = {1, 0, 10000, 15555})
    @DisplayName("Получение суммы кредита увеличенной на размер страховки")
    void getAmountWithInsurance(int amount) {
        BigDecimal insuranceRate = scoringProps.getInsuranceRate();
        Integer scale = roundingProps.getScale();
        RoundingMode roundingMode = roundingProps.getRoundingMode();
        BigDecimal insExpected = BigDecimal.valueOf(amount).multiply(insuranceRate
                .divide(BigDecimal.valueOf(100), scale, roundingMode).add(BigDecimal.ONE));
        assertEquals(insExpected, insurance.getAmountWithInsurance(BigDecimal.valueOf(amount)));
    }
}
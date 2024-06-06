package ru.petrov.calculator.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
class InsuranceTest {
    @Autowired
    private Insurance insurance;

    @Test
    @DisplayName("Получение суммы кредита увеличенной на размер страховки")
    void getAmountWithInsurance() {
        assertEquals(0, insurance.getAmountWithInsurance(BigDecimal.valueOf(1000)).compareTo(BigDecimal.valueOf(2000)));
    }
}
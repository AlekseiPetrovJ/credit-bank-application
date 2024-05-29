package ru.petrov.calculator.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.petrov.calculator.config.RoundingProps;
import ru.petrov.calculator.config.ScoringProps;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class InsuranceTest {
    @Mock
    private ScoringProps scoringProps;
    @Mock
    private RoundingProps roundingProps;
    @InjectMocks
    private Insurance insurance;

    @Test
    @DisplayName("Получение суммы кредита увеличенной на размер страховки")
    void getAmountWithInsurance() {

        int  insuranceRate = 5;
        BigDecimal amount = BigDecimal.valueOf(10000);

        Mockito.when(scoringProps.getInsuranceRate()).thenReturn(BigDecimal.valueOf(insuranceRate));
        Mockito.when(roundingProps.getScale()).thenReturn(5);
        Mockito.when(roundingProps.getRoundingMode()).thenReturn(RoundingMode.HALF_EVEN);
        BigDecimal insExpected = amount.multiply(BigDecimal.valueOf(insuranceRate)
                        .divide(BigDecimal.valueOf(100), roundingProps.getScale(),
                                roundingProps.getRoundingMode()).add(BigDecimal.ONE));
        assertEquals(insExpected, insurance.getAmountWithInsurance(amount));
    }
}
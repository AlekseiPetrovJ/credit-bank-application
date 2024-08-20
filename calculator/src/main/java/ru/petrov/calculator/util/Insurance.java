package ru.petrov.calculator.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.petrov.calculator.config.RoundingProps;
import ru.petrov.calculator.config.ScoringProps;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class Insurance {
    private final ScoringProps scoringProps;
    private final RoundingProps roundingProps;

    public BigDecimal getAmountWithInsurance(BigDecimal amount) {
       return amount.multiply(scoringProps.getInsuranceRate()
                .divide(BigDecimal.valueOf(100), roundingProps.getScale(),
                        roundingProps.getRoundingMode()).add(BigDecimal.ONE));
    }
}
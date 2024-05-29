package ru.petrov.calculator.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.RoundingMode;

@Configuration
@ConfigurationProperties(prefix="calculator.rounding")
@Data
public class RoundingProps {
    /**
     * Режим округления для BigDecimal
     */
    RoundingMode roundingMode = RoundingMode.HALF_EVEN;
    /**
     * Количество десятичных знаков при округлении.
     */
    Integer scale = 10;
}

package ru.petrov.calculator.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class AnnuityPayments {
    /**
     * Расчет ежемесячного аннуитетного платежа
     *
     * @return Размер ежемесячного платежа
     */
    public static BigDecimal getAnnuityMonthlyPayment(BigDecimal amount, BigDecimal yearRate, Integer monthsTerm, Integer scale, RoundingMode roundingMode) {
        BigDecimal monthsRate = yearRate.divide(BigDecimal.valueOf(12 * 100), scale, roundingMode);

//         Вспомогательный расчет (1 + М) ^ S)
        BigDecimal auxiliaryFactor = BigDecimal.ONE.add(monthsRate).pow(monthsTerm);

        /*Коэффициент аннуитета считаем по формуле
                К = (М * (1 + М) ^ S) / ((1 + М) ^ S — 1)
        где М — месячная процентная ставка по кредиту,
                S — срок кредита в месяцах.*/
        
        BigDecimal annuityFactor = (monthsRate.multiply(auxiliaryFactor))
                .divide(auxiliaryFactor.subtract(BigDecimal.ONE), scale, roundingMode);
        return (amount.multiply(annuityFactor));
    }

    public static BigDecimal getAnnuityTotalPayment(BigDecimal amount, BigDecimal yearRate, Integer monthsTerm, Integer scale, RoundingMode roundingMode) {
        return getAnnuityMonthlyPayment(amount, yearRate, monthsTerm, scale, roundingMode)
                .multiply(BigDecimal.valueOf(monthsTerm));
    }
}

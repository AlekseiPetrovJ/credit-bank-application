package ru.petrov.calculator.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.petrov.calculator.config.RoundingProps;
import ru.petrov.calculator.dto.PaymentScheduleElementDto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AnnuityPayments {
    private final RoundingProps roundingProps;

    /**
     * Расчет ежемесячного аннуитетного платежа
     *
     * @return Размер ежемесячного платежа
     */
    public BigDecimal getAnnuityMonthlyPayment(BigDecimal amount, BigDecimal yearRate, Integer monthsTerm) {
        Integer scale = roundingProps.getScale();
        RoundingMode roundingMode = roundingProps.getRoundingMode();
        BigDecimal monthsRate = yearRate.divide(BigDecimal.valueOf(12 * 100), scale, roundingMode);

        // Вспомогательный расчет (1 + М) ^ S) для коэф. аннуитета
        BigDecimal auxiliaryFactor = BigDecimal.ONE.add(monthsRate).pow(monthsTerm);

        /*Коэффициент аннуитета считаем по формуле
                К = (М * (1 + М) ^ S) / ((1 + М) ^ S — 1)
        где М — месячная процентная ставка по кредиту,
                S — срок кредита в месяцах.*/

        BigDecimal annuityFactor = (monthsRate.multiply(auxiliaryFactor))
                .divide(auxiliaryFactor.subtract(BigDecimal.ONE), scale, roundingMode);
        return (amount.multiply(annuityFactor));
    }

    /**
     * Расчет примерной общей суммы кредита, как суммы ежемесячных платежей. Расчет по графику является предпочтительным
     *
     * @param amount запрашиваемая сумма
     * @param yearRate годовая ставка
     * @param monthsTerm срок кредита в месяцах
     */
    public BigDecimal getTotalAmount(BigDecimal amount, BigDecimal yearRate, Integer monthsTerm) {
        return getAnnuityMonthlyPayment(amount, yearRate, monthsTerm)
                .multiply(BigDecimal.valueOf(monthsTerm));
    }

    /**
     * Расчет полной стоимости кредита ПСК по упрощенной формуле:
     * ПСК = (СП/СЗ – 1)/С * 100,
     * где СП – сумма всех платежей клиента;
     * СЗ – сумма выданного потребительского кредита;
     * С – срок кредитования в годах.
     *
     * @param amount      сумма выданного потребительского кредита
     * @param totalAmount сумма всех платежей клиента;
     * @param monthsTerm  срок в месяцах, в методе пересчитывается в срок в годах
     * @return Полная стоимость кредита в процентах
     */
    public BigDecimal getPsk(BigDecimal amount, BigDecimal totalAmount, Integer monthsTerm) {
        Integer scale = roundingProps.getScale();
        RoundingMode roundingMode = roundingProps.getRoundingMode();
        BigDecimal yearTerm = BigDecimal.valueOf(monthsTerm).divide(BigDecimal.valueOf(12), scale, roundingMode);
        return (totalAmount.divide(amount, scale, roundingMode).subtract(BigDecimal.ONE))
                .divide(yearTerm, scale, roundingMode).multiply(BigDecimal.valueOf(100));
    }

    /**
     * Расчет графика платежей
     *
     * @param amount         сумма долга
     * @param rate           годовая процентная ставка
     * @param monthlyPayment ежемесячный платеж
     * @return График платежей
     */
    public List<PaymentScheduleElementDto> getPaymentSchedule(BigDecimal amount, BigDecimal rate, BigDecimal monthlyPayment) {
        BigDecimal remainingDebt = amount;
        List<PaymentScheduleElementDto> paymentSchedule = new ArrayList<>();
        LocalDate datePayment = LocalDate.now();

        //Распределяем платежи по месяцам пока есть нераспределенный долг
        while (remainingDebt.compareTo(BigDecimal.ZERO) > 0) {
            //Если остаток долга меньше либо равен ежемесячному платежу, то это последний платеж
            if (remainingDebt.compareTo(monthlyPayment) <= 0) {
                //todo избавится от дублирования
                BigDecimal interestPayment = getMonthlyInterestPayment(remainingDebt, rate);
                paymentSchedule.add(new PaymentScheduleElementDto(paymentSchedule.size() + 1,
                        datePayment = datePayment.plusMonths(1),
                        remainingDebt.add(interestPayment),
                        interestPayment,
                        remainingDebt,
                        remainingDebt = BigDecimal.ZERO));


            } else {
                BigDecimal interestPayment = getMonthlyInterestPayment(remainingDebt, rate);
                BigDecimal debtPayment = monthlyPayment.subtract(interestPayment);
                paymentSchedule.add(new PaymentScheduleElementDto(paymentSchedule.size() + 1,
                        datePayment = datePayment.plusMonths(1),
                        monthlyPayment,
                        interestPayment,
                        debtPayment,
                        remainingDebt = remainingDebt.subtract(debtPayment)));
            }
        }
        return paymentSchedule;
    }

    /**
     * Считаем сумму за месяц причитающуюся банку по кредиту
     *
     * @param amount сумма
     * @param rate   годовая процентная ставка, переводится в месячную ставку
     * @return проценты за кредит
     */
    public BigDecimal getMonthlyInterestPayment(BigDecimal amount, BigDecimal rate) {
        return amount.multiply(rate.divide(BigDecimal.valueOf(12 * 100), roundingProps.getScale(), roundingProps.getRoundingMode()));
    }

    /**
     * Расчет суммы общих выплат по графику платежей
     *
     * @param paymentSchedule график платежей
     * @return полная сумма выплат
     */
    public BigDecimal getTotalPayment(List<PaymentScheduleElementDto> paymentSchedule) {
        return paymentSchedule.stream()
                .map(PaymentScheduleElementDto::getTotalPayment)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}

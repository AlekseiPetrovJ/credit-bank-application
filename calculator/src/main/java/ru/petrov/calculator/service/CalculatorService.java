package ru.petrov.calculator.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.petrov.calculator.config.ScoringProps;
import ru.petrov.calculator.dto.*;
import ru.petrov.calculator.util.AnnuityPayments;
import ru.petrov.calculator.util.Insurance;
import ru.petrov.calculator.util.Rate;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CalculatorService {
    private final ScoringProps scoringProps;
    private final Insurance insurance;
    private final AnnuityPayments payments;
    private final Rate rate;


    /**
     * в зависимости от страховых услуг увеличивается/уменьшается процентная ставка и сумма кредита,
     * базовая ставка хардкодится в коде через property файл. Например цена страховки 100к (или прогрессивная,
     * в зависимости от запрошенной суммы кредита), ее стоимость добавляется в тело кредита, но она уменьшает ставку на 3.
     * Цена зарплатного клиента 0, уменьшает ставку на 1.
     *
     * @param requestDto запрос кредита
     * @return список из 4х LoanOfferDto от "худшего" к "лучшему" (чем меньше итоговая ставка, тем лучше)
     */
    public List<LoanOfferDto> preScoring(LoanStatementRequestDto requestDto) {
        BigDecimal requestedAmount = requestDto.getAmount();
        Integer monthsTerm = requestDto.getTerm();
        List<LoanOfferDto> loanOffers = new ArrayList<>();
        //todo подумать как упростить возможно избавится от отдельной переменной amountWithInsurance
        BigDecimal amountWithInsurance = insurance.getAmountWithInsurance(requestedAmount);
        BigDecimal basicYearRate;
        //todo выяснить нужно ли округлять для DTO. В ТЗ ничего не сказано про округление и валюту.

        //todo вынести однотипный код в отдельный метод

        //isInsuranceEnabled - false; isSalaryClient - false;
        basicYearRate = scoringProps.getBasicYearRate();


        //todo ко всем BigDecimal применить .stripTrailingZeros() для удаления нулей справа
        loanOffers.add(new LoanOfferDto(UUID.randomUUID(),
                requestedAmount,
                payments.getTotalAmount(requestedAmount, basicYearRate, monthsTerm),
                monthsTerm,
                payments.getAnnuityMonthlyPayment(requestedAmount, basicYearRate, monthsTerm),
                basicYearRate,
                false,
                false));

        //isInsuranceEnabled - true; isSalaryClient - true;
        basicYearRate = scoringProps.getBasicYearRate()
                .subtract(scoringProps.getInsuranceFactor())
                .subtract(scoringProps.getSalaryClientFactor());

        loanOffers.add(new LoanOfferDto(UUID.randomUUID(),
                requestedAmount,
                payments.getTotalAmount(amountWithInsurance, basicYearRate, monthsTerm),
                monthsTerm,
                payments.getAnnuityMonthlyPayment(amountWithInsurance, basicYearRate, monthsTerm),
                basicYearRate,
                true,
                true));

        //isInsuranceEnabled - true; isSalaryClient - false;
        basicYearRate = scoringProps.getBasicYearRate()
                .subtract(scoringProps.getInsuranceFactor());

        loanOffers.add(new LoanOfferDto(UUID.randomUUID(),
                requestedAmount,
                payments.getTotalAmount(amountWithInsurance, basicYearRate, monthsTerm),
                monthsTerm,
                payments.getAnnuityMonthlyPayment(amountWithInsurance, basicYearRate, monthsTerm),
                basicYearRate,
                true,
                false));

        //isInsuranceEnabled - false; isSalaryClient -true;
        basicYearRate = scoringProps.getBasicYearRate()
                .subtract(scoringProps.getSalaryClientFactor());

        loanOffers.add(new LoanOfferDto(UUID.randomUUID(),
                requestedAmount,
                payments.getTotalAmount(requestedAmount, basicYearRate, monthsTerm),
                monthsTerm,
                payments.getAnnuityMonthlyPayment(requestedAmount, basicYearRate, monthsTerm),
                basicYearRate,
                false,
                true));

        loanOffers.sort(Comparator.comparing(LoanOfferDto::getRate));
        return loanOffers;
    }

    public CreditDto scoring(ScoringDataDto scoringDataDto) {
        //todo вначале валидируем scoringDataDto
        //Получаем ставку проверяем ставку и сразу возвращаем в случае отказа
        Optional<BigDecimal> rateOpt = rate.getRate(scoringDataDto);
        if (rateOpt.isEmpty()) {
            return null;
        }
        BigDecimal yearRate = rateOpt.get();
        Integer term = scoringDataDto.getTerm();

        //получаем сумму кредита с учетом страховки
        BigDecimal amount = scoringDataDto.getIsInsuranceEnabled() ?
                insurance.getAmountWithInsurance(scoringDataDto.getAmount()) : scoringDataDto.getAmount();

        // получаем месячные платежи
        BigDecimal monthlyPayment = payments.getAnnuityMonthlyPayment(amount,
                yearRate, term);

        //Получаем график платежей
        List<PaymentScheduleElementDto> paymentSchedule = payments.getPaymentSchedule(amount, yearRate, monthlyPayment);

        //получаем общую сумму платежей по кредиту
        BigDecimal totalAmount = payments.getTotalPayment(paymentSchedule);

        //Получаем PSK
        BigDecimal psk = payments.getPsk(amount, totalAmount, term);

        return new CreditDto(scoringDataDto.getAmount(), term, monthlyPayment, yearRate, psk,
                scoringDataDto.getIsInsuranceEnabled(), scoringDataDto.getIsSalaryClient(), paymentSchedule);
    }
}

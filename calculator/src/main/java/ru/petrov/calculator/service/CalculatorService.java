package ru.petrov.calculator.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.petrov.calculator.config.RoundingProps;
import ru.petrov.calculator.config.ScoringProps;
import ru.petrov.calculator.dto.LoanOfferDto;
import ru.petrov.calculator.dto.LoanStatementRequestDto;

import java.math.BigDecimal;
import java.util.*;

import static ru.petrov.calculator.util.AnnuityPayments.getAnnuityMonthlyPayment;
import static ru.petrov.calculator.util.AnnuityPayments.getAnnuityTotalPayment;

@Service
public class CalculatorService {
    private final ScoringProps scoringProps;
    private final RoundingProps roundingProps;

    @Autowired
    public CalculatorService(ScoringProps scoringProps, RoundingProps roundingProps) {
        this.scoringProps = scoringProps;
        this.roundingProps = roundingProps;
    }

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
        BigDecimal amountWithInsurance = requestedAmount.add(getInsuranceAmount(requestedAmount));
        BigDecimal basicYearRate;
        //todo выяснить нужно ли округлять для DTO. В ТЗ ничего не сказано про округление и валюту.

        //isInsuranceEnabled - false; isSalaryClient - false;
        basicYearRate = scoringProps.getBasicYearRate();

        loanOffers.add(new LoanOfferDto(UUID.randomUUID(),
                requestedAmount,
                getAnnuityTotalPayment(requestedAmount, basicYearRate, monthsTerm,
                        roundingProps.getScale(), roundingProps.getRoundingMode()),
                monthsTerm,
                getAnnuityMonthlyPayment(requestedAmount, basicYearRate, monthsTerm,
                        roundingProps.getScale(), roundingProps.getRoundingMode()),
                basicYearRate,
                false,
                false));

        //isInsuranceEnabled - true; isSalaryClient - true;
        basicYearRate = scoringProps.getBasicYearRate()
                .subtract(scoringProps.getInsuranceFactor())
                .subtract(scoringProps.getSalaryClientFactor());

        loanOffers.add(new LoanOfferDto(UUID.randomUUID(),
                requestedAmount,
                getAnnuityTotalPayment(amountWithInsurance, basicYearRate, monthsTerm,
                        roundingProps.getScale(), roundingProps.getRoundingMode()),
                monthsTerm,
                getAnnuityMonthlyPayment(amountWithInsurance, basicYearRate, monthsTerm,
                        roundingProps.getScale(), roundingProps.getRoundingMode()),
                basicYearRate,
                true,
                true));

        //isInsuranceEnabled - true; isSalaryClient - false;
        basicYearRate = scoringProps.getBasicYearRate()
                .subtract(scoringProps.getInsuranceFactor());

        loanOffers.add(new LoanOfferDto(UUID.randomUUID(),
                requestedAmount,
                getAnnuityTotalPayment(amountWithInsurance, basicYearRate, monthsTerm,
                        roundingProps.getScale(), roundingProps.getRoundingMode()),
                monthsTerm,
                getAnnuityMonthlyPayment(amountWithInsurance, basicYearRate, monthsTerm,
                        roundingProps.getScale(), roundingProps.getRoundingMode()),
                basicYearRate,
                true,
                false));

        //isInsuranceEnabled - false; isSalaryClient -true;
        basicYearRate = scoringProps.getBasicYearRate()
                .subtract(scoringProps.getSalaryClientFactor());

        loanOffers.add(new LoanOfferDto(UUID.randomUUID(),
                requestedAmount,
                getAnnuityTotalPayment(requestedAmount, basicYearRate, monthsTerm,
                        roundingProps.getScale(), roundingProps.getRoundingMode()),
                monthsTerm,
                getAnnuityMonthlyPayment(requestedAmount, basicYearRate, monthsTerm,
                        roundingProps.getScale(), roundingProps.getRoundingMode()),
                basicYearRate,
                false,
                true));

        loanOffers.sort(Comparator.comparing(LoanOfferDto::getRate));
        return loanOffers;
    }

    private BigDecimal getInsuranceAmount(BigDecimal requestedAmount) {
        return requestedAmount.multiply(scoringProps.getInsuranceRate())
                .divide(BigDecimal.valueOf(100), roundingProps.getScale(),
                        roundingProps.getRoundingMode());
    }

}

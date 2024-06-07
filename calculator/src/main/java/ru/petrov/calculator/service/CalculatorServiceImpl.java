package ru.petrov.calculator.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.petrov.calculator.config.ScoringProps;
import ru.petrov.calculator.dto.*;
import ru.petrov.calculator.dto.enums.Gender;
import ru.petrov.calculator.dto.enums.MaritalStatus;
import ru.petrov.calculator.dto.enums.Position;
import ru.petrov.calculator.util.AnnuityPayments;
import ru.petrov.calculator.util.Insurance;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;

import static ru.petrov.calculator.dto.enums.EmploymentStatus.*;

@Service
@RequiredArgsConstructor
public class CalculatorServiceImpl implements CalculatorService {
    private final ScoringProps scoringProps;
    private final Insurance insurance;
    private final AnnuityPayments payments;

    /**
     * в зависимости от страховых услуг увеличивается/уменьшается процентная ставка и сумма кредита,
     * базовая ставка хардкодится в коде через property файл. Например цена страховки 100к (или прогрессивная,
     * в зависимости от запрошенной суммы кредита), ее стоимость добавляется в тело кредита, но она уменьшает ставку на 3.
     * Цена зарплатного клиента 0, уменьшает ставку на 1.
     * @param requestDto запрос кредита
     * @return список из 4х LoanOfferDto от "худшего" к "лучшему" (чем меньше итоговая ставка, тем лучше)
     */
    public List<LoanOfferDto> preScoring(LoanStatementRequestDto requestDto) {

        List<LoanOfferDto> loanOffers = new ArrayList<>();

        loanOffers.add(addOffer(requestDto, false, false));
        loanOffers.add(addOffer(requestDto, true, true));
        loanOffers.add(addOffer(requestDto, true, false));
        loanOffers.add(addOffer(requestDto, false, true));

        loanOffers.sort(Comparator.comparing(LoanOfferDto::getRate));
        return loanOffers;
    }

    /**
     * Расчет условий кредита
     * @param scoringDataDto
     * @return в случае отказа возвращается null
     */
    public CreditDto scoring(ScoringDataDto scoringDataDto) {
        Optional<BigDecimal> rateOpt = getRate(scoringDataDto);
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


    /**
     * Рассчитывает процентную ставку в зависимости от входных параметров:
     * Цена страховки уменьшает ставку на 3.
     * Для зарплатного клиента ставка меньше на 1.
     * Рабочий статус: Безработный → отказ; Самозанятый → ставка увеличивается на 1; Владелец бизнеса → ставка увеличивается на 2
     * Позиция на работе: Менеджер среднего звена → ставка уменьшается на 2; Топ-менеджер → ставка уменьшается на 3
     * Сумма займа больше, чем 25 зарплат → отказ
     * Семейное положение: Замужем/женат → ставка уменьшается на 3; Разведен → ставка увеличивается на 1
     * Возраст менее 20 или более 65 лет → отказ
     * Пол: Женщина, возраст от 32 до 60 лет → ставка уменьшается на 3; Мужчина, возраст от 30 до 55 лет → ставка уменьшается на 3
     * Стаж работы: Общий стаж менее 18 месяцев → отказ; Текущий стаж менее 3 месяцев → отказ
     *
     * @return Optional.empty() в случае отказа в предоставлении кредита
     */
    private Optional<BigDecimal> getRate(ScoringDataDto scoringDataDto) {
        if (scoringDataDto == null) {
            return Optional.empty();
        }
        int age = Period.between(scoringDataDto.getBirthdate(), LocalDate.now()).getYears();
        boolean loanSizeIsTooLarge = scoringDataDto.getAmount()
                .compareTo(scoringDataDto.getEmployment()
                        .getSalary().multiply(scoringProps.getFactorBetweenLoanAndSalaryAllowLoan())) > 0;

        BigDecimal resultRate;
        if (scoringDataDto.getEmployment().getEmploymentStatus()==(UNEMPLOYED)
                || loanSizeIsTooLarge || age < scoringProps.getMinAgeForPrescoring()
                || age > scoringProps.getMaxAgeForLoan()
                || scoringDataDto.getEmployment().getWorkExperienceTotal() < scoringProps.getMinWorkExperienceTotal()
                || scoringDataDto.getEmployment().getWorkExperienceCurrent() < scoringProps.getMinWorkExperienceCurrent()) {
            return Optional.empty();
        } else {
            resultRate = scoringProps.getBasicYearRate();

            if (scoringDataDto.getEmployment().getEmploymentStatus()==(SELF_EMPLOYED))
                resultRate = resultRate.add(scoringProps.getSelfEmployedPoints());
            if (scoringDataDto.getEmployment().getEmploymentStatus()==(BUSINESS_OWNER))
                resultRate = resultRate.add(scoringProps.getBusinessOwnerPoints());

            if (scoringDataDto.getEmployment().getPosition()==(Position.MIDDLE_MANAGER))
                resultRate = resultRate.subtract(scoringProps.getMiddleManagerPoints());
            if (scoringDataDto.getEmployment().getPosition()==(Position.TOP_MANAGER))
                resultRate = resultRate.subtract(scoringProps.getTopManagerPoints());

            if (scoringDataDto.getMaritalStatus()==(MaritalStatus.MARRIED))
                resultRate = resultRate.subtract(scoringProps.getMarriedPoints());
            if (scoringDataDto.getMaritalStatus()==(MaritalStatus.DIVORCED))
                resultRate = resultRate.add(scoringProps.getDivorcedPoints());

            if (scoringDataDto.getGender()==(Gender.FAMELE) &&
                    age >= scoringProps.getMinAgeFemaleForPoints() &&
                    age <= scoringProps.getMaxAgeFemaleForPoints())
                resultRate = resultRate.subtract(scoringProps.getAgeFemalePoints());
            if (scoringDataDto.getGender()==(Gender.MALE) &&
                    age >= scoringProps.getMinAgeMaleForPoints() &&
                    age <= scoringProps.getMaxAgeMaleForPoints())
                resultRate = resultRate.subtract(scoringProps.getAgeMalePoints());

            if (scoringDataDto.getIsInsuranceEnabled()) resultRate = resultRate.
                    subtract(scoringProps.getInsuranceFactor());
            if (scoringDataDto.getIsSalaryClient()) resultRate = resultRate.
                    subtract(scoringProps.getSalaryClientFactor());

        }
        return Optional.of(resultRate);
    }

    private BigDecimal getPreScoreRate(Boolean isInsuranceEnabled, Boolean isSalaryClient) {
        BigDecimal basicYearRate = scoringProps.getBasicYearRate();
        if (isInsuranceEnabled) basicYearRate = basicYearRate.subtract(scoringProps.getInsuranceFactor());
        if (isSalaryClient) basicYearRate = basicYearRate.subtract(scoringProps.getSalaryClientFactor());
        return basicYearRate;
    }

    private LoanOfferDto addOffer(LoanStatementRequestDto requestDto,
                                  Boolean isInsuranceEnabled, Boolean isSalaryClient) {
        BigDecimal requestedAmount = requestDto.getAmount();
        Integer monthsTerm = requestDto.getTerm();
        BigDecimal amountIncludingInsurance;
        if (isInsuranceEnabled) {
            amountIncludingInsurance = insurance.getAmountWithInsurance(requestedAmount);
        } else {
            amountIncludingInsurance = requestedAmount;
        }
        BigDecimal basicYearRate = getPreScoreRate(isInsuranceEnabled, isSalaryClient);

        return (new LoanOfferDto(UUID.randomUUID(),
                requestedAmount, payments.getTotalAmount(amountIncludingInsurance, basicYearRate, monthsTerm),
                monthsTerm, payments.getAnnuityMonthlyPayment(amountIncludingInsurance, basicYearRate, monthsTerm),
                basicYearRate, isInsuranceEnabled, isSalaryClient));
    }
}
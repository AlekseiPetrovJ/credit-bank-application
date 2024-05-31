package ru.petrov.calculator.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.petrov.calculator.config.ScoringProps;
import ru.petrov.calculator.dto.*;
import ru.petrov.calculator.dto.enums.EmploymentStatus;
import ru.petrov.calculator.dto.enums.Gender;
import ru.petrov.calculator.dto.enums.MaritalStatus;
import ru.petrov.calculator.dto.enums.Position;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

import static ru.petrov.calculator.dto.enums.EmploymentStatus.*;

@Component
@RequiredArgsConstructor
public class Rate {
    private final ScoringProps scoringProps;

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
    public Optional<BigDecimal> getRate(BigDecimal amount,
                                        Boolean isInsuranceEnabled,
                                        Boolean isSalaryClient,
                                        EmploymentStatus employmentStatus,
                                        Position position,
                                        BigDecimal salary,
                                        MaritalStatus maritalStatus,
                                        LocalDate birthdate,
                                        Gender gender,
                                        Integer workExperienceTotal,
                                        Integer workExperienceCurrent) {
        int age = Period.between(birthdate, LocalDate.now()).getYears();
        boolean isAmountMore25Salary = amount.compareTo(salary.multiply(BigDecimal.valueOf(25))) > 0;

        BigDecimal resultRate;
        if (employmentStatus.equals(UNEMPLOYED) || isAmountMore25Salary || age < 20 || age > 65 || workExperienceTotal < 18 || workExperienceCurrent < 3) {
            return Optional.empty();
        } else {
            resultRate = scoringProps.getBasicYearRate();

            if (employmentStatus.equals(SELF_EMPLOYED)) resultRate = resultRate.add(BigDecimal.ONE);
            if (employmentStatus.equals(BUSINESS_OWNER)) resultRate = resultRate.add(BigDecimal.valueOf(2));

            if (position.equals(Position.MIDDLE_MANAGER)) resultRate = resultRate.subtract(BigDecimal.valueOf(2));
            if (position.equals(Position.TOP_MANAGER)) resultRate = resultRate.subtract(BigDecimal.valueOf(3));

            if (maritalStatus.equals(MaritalStatus.MARRIED)) resultRate = resultRate.subtract(BigDecimal.valueOf(3));
            if (maritalStatus.equals(MaritalStatus.DIVORCED)) resultRate = resultRate.add(BigDecimal.ONE);

            if (gender.equals(Gender.FAMELE) && age >= 32 && age <= 60)
                resultRate = resultRate.subtract(BigDecimal.valueOf(3));
            if (gender.equals(Gender.MALE) && age >= 30 && age <= 55)
                resultRate = resultRate.subtract(BigDecimal.valueOf(3));

            if (isInsuranceEnabled) resultRate = resultRate.subtract(BigDecimal.valueOf(3));
            if (isSalaryClient) resultRate = resultRate.subtract(BigDecimal.valueOf(1));

        }
        return Optional.of(resultRate);
    }

    /**
     * Рассчитывает процентную ставку в зависимости от входных параметров
     *
     * @return Optional пустой в случае отказа в предоставлении кредита
     */
    public Optional<BigDecimal> getRate(ScoringDataDto scoringDataDto) {
        if (scoringDataDto == null){
            return Optional.empty();
        }
        return getRate(scoringDataDto.getAmount(),
                scoringDataDto.getIsInsuranceEnabled(),
                scoringDataDto.getIsSalaryClient(),
                scoringDataDto.getEmployment().getEmploymentStatus(),
                scoringDataDto.getEmployment().getPosition(),
                scoringDataDto.getEmployment().getSalary(),
                scoringDataDto.getMaritalStatus(),
                scoringDataDto.getBirthdate(),
                scoringDataDto.getGender(),
                scoringDataDto.getEmployment().getWorkExperienceTotal(),
                scoringDataDto.getEmployment().getWorkExperienceCurrent());
    }
}

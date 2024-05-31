package ru.petrov.calculator.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.petrov.calculator.config.ScoringProps;
import ru.petrov.calculator.dto.EmploymentDto;
import ru.petrov.calculator.dto.LoanStatementRequestDto;
import ru.petrov.calculator.dto.ScoringDataDto;
import ru.petrov.calculator.dto.enums.EmploymentStatus;
import ru.petrov.calculator.dto.enums.Gender;
import ru.petrov.calculator.dto.enums.MaritalStatus;
import ru.petrov.calculator.dto.enums.Position;
import ru.petrov.calculator.util.AnnuityPayments;
import ru.petrov.calculator.util.Insurance;
import ru.petrov.calculator.util.Rate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalculatorServiceTest {
    @Mock
    private ScoringProps scoringProps;
    @Mock
    private Insurance insurance;
    @Mock
    private AnnuityPayments payments;
    @Mock
    private Rate rate;

    @InjectMocks
    private CalculatorService calculatorService;

    @Test
    @DisplayName("Проверка количества возвращаемых предложений")
    void testPreScoringCountOffers() {
        LoanStatementRequestDto request = new LoanStatementRequestDto(BigDecimal.valueOf(40000),
                12, "Ivanov", "Ivan", "Ivanovich", "sfd@sdf.ru",
                LocalDate.of(2000, 5, 30), "5555", "878989");
        when(scoringProps.getBasicYearRate()).thenReturn(BigDecimal.TEN);
        when(scoringProps.getInsuranceFactor()).thenReturn(BigDecimal.valueOf(2));
        when(scoringProps.getSalaryClientFactor()).thenReturn(BigDecimal.ONE);
        when(payments.getAnnuityMonthlyPayment(any(), any(), any())).thenReturn(BigDecimal.valueOf(1000));
        assertEquals(4, calculatorService.preScoring(request).size());
    }

    @Test
    @DisplayName("Проверка соответствия выходных данных входным")
    void testPreScoringEquals() {
        BigDecimal amount = BigDecimal.valueOf(40000);
        int term = 12;
        LoanStatementRequestDto request = new LoanStatementRequestDto(amount,
                term, "Ivanov", "Ivan", "Ivanovich", "sfd@sdf.ru",
                LocalDate.of(2000, 5, 30), "5555", "878989");
        when(scoringProps.getBasicYearRate()).thenReturn(BigDecimal.TEN);
        when(scoringProps.getInsuranceFactor()).thenReturn(BigDecimal.valueOf(2));
        when(scoringProps.getSalaryClientFactor()).thenReturn(BigDecimal.ONE);
        when(payments.getAnnuityMonthlyPayment(any(), any(), any())).thenReturn(BigDecimal.valueOf(1000));

        assertEquals(amount, calculatorService.preScoring(request).get(0).getRequestedAmount());
        assertEquals(term, calculatorService.preScoring(request).get(0).getTerm());
    }

    @Test
    @DisplayName("Проверка получения отказа (null)")
    void testScoringReturnNull() {
        EmploymentDto employmentDto = new EmploymentDto(EmploymentStatus.UNEMPLOYED, "22222", BigDecimal.valueOf(500000), Position.MIDDLE_MANAGER, 20, 10);

        BigDecimal amount = BigDecimal.valueOf(40000);
        int term = 12;
        ScoringDataDto scoringDataDto = new ScoringDataDto(BigDecimal.valueOf(35000),
                12, "Ivanov", "Ivan", "Ivanovich",
                Gender.MALE, LocalDate.of(2000, 5, 29), "7007",
                "111222", LocalDate.of(2018, 5, 30), "TTT",
                MaritalStatus.DIVORCED, 0, employmentDto, "555", false, false);
        when(rate.getRate(scoringDataDto)).thenReturn(Optional.empty());
        assertNull(calculatorService.scoring(scoringDataDto));
    }

    @Test
    @DisplayName("Проверка успешного получения кредитного предложения")
    void testScoringReturnNotNull() {
        EmploymentDto employmentDto = new EmploymentDto(EmploymentStatus.BUSINESS_OWNER, "22222", BigDecimal.valueOf(500000), Position.MIDDLE_MANAGER, 20, 10);

        BigDecimal amount = BigDecimal.valueOf(40000);
        int term = 12;
        ScoringDataDto scoringDataDto = new ScoringDataDto(BigDecimal.valueOf(35000),
                12, "Ivanov", "Ivan", "Ivanovich",
                Gender.MALE, LocalDate.of(2000, 5, 29), "7007",
                "111222", LocalDate.of(2018, 5, 30), "TTT",
                MaritalStatus.DIVORCED, 0, employmentDto, "555", false, false);
        when(rate.getRate(scoringDataDto)).thenReturn(Optional.of(BigDecimal.TEN));
        assertNotNull(calculatorService.scoring(scoringDataDto));
    }
}
package ru.petrov.calculator.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.petrov.calculator.config.ScoringProps;
import ru.petrov.calculator.dto.EmploymentDto;
import ru.petrov.calculator.dto.ScoringDataDto;
import ru.petrov.calculator.dto.enums.EmploymentStatus;
import ru.petrov.calculator.dto.enums.Gender;
import ru.petrov.calculator.dto.enums.MaritalStatus;
import ru.petrov.calculator.dto.enums.Position;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;
@ExtendWith(MockitoExtension.class)
class RateTest {
    @Mock
    private ScoringProps scoringProps;

    @InjectMocks
    private Rate rate;

    @Test
    @DisplayName("Получение отказа для безработного ")
    void testGetRateGetReject() {
        EmploymentDto unemployedDto = new EmploymentDto(EmploymentStatus.UNEMPLOYED, "22222", BigDecimal.valueOf(500000), Position.MIDDLE_MANAGER, 20, 10);
        ScoringDataDto scoringUnemployedDto = new ScoringDataDto(BigDecimal.valueOf(35000),
                12, "Ivanov", "Ivan", "Ivanovich",
                Gender.MALE, LocalDate.of(2000, 5, 29), "7007",
                "111222", LocalDate.of(2018, 5, 30), "TTT",
                MaritalStatus.DIVORCED, 0, unemployedDto, "555", false, false);
        assertTrue(rate.getRate(scoringUnemployedDto).isEmpty());
    }

    @Test
    @DisplayName("Получение отказа на входной null ")
    void testRateGetRejectIfInputNull() {
        assertTrue(rate.getRate(null).isEmpty());
    }

    @Test
    @DisplayName("Получение ставки")
    void testGetRate() {
        EmploymentDto unemployedDto = new EmploymentDto(EmploymentStatus.BUSINESS_OWNER, "22222", BigDecimal.valueOf(500000), Position.MIDDLE_MANAGER, 20, 10);
        ScoringDataDto scoringDataDto = new ScoringDataDto(BigDecimal.valueOf(35000),
                12, "Ivanov", "Ivan", "Ivanovich",
                Gender.MALE, LocalDate.of(2000, 5, 29), "7007",
                "111222", LocalDate.of(2018, 5, 30), "TTT",
                MaritalStatus.DIVORCED, 0, unemployedDto, "555", false, false);
        Mockito.when(scoringProps.getBasicYearRate()).thenReturn(BigDecimal.valueOf(10));

        assertTrue(rate.getRate(scoringDataDto).isPresent());
    }

    @Test
    @DisplayName("Ставка для мужчины старше 30 ставка меньше чем для мужчины младше 30")
    void testGetRateReduction() {
        EmploymentDto employmentDto = new EmploymentDto(EmploymentStatus.BUSINESS_OWNER, "22222", BigDecimal.valueOf(500000), Position.MIDDLE_MANAGER, 20, 10);
        ScoringDataDto scoringYoungDto = new ScoringDataDto(BigDecimal.valueOf(35000),
                12, "Ivanov", "Ivan", "Ivanovich",
                Gender.MALE, LocalDate.of(2000, 5, 29), "7007",
                "111222", LocalDate.of(2018, 5, 30), "TTT",
                MaritalStatus.DIVORCED, 0, employmentDto, "555", false, false);
        ScoringDataDto scoringOldDto = new ScoringDataDto(BigDecimal.valueOf(35000),
                12, "Ivanov", "Ivan", "Ivanovich",
                Gender.MALE, LocalDate.of(1980, 5, 29), "7007",
                "111222", LocalDate.of(2018, 5, 30), "TTT",
                MaritalStatus.DIVORCED, 0, employmentDto, "555", false, false);
        Mockito.when(scoringProps.getBasicYearRate()).thenReturn(BigDecimal.valueOf(10));

        BigDecimal rateYang = rate.getRate(scoringYoungDto).get();
        BigDecimal rateOld = rate.getRate(scoringOldDto).get();
        assertTrue(rateYang.compareTo(rateOld)>0);
    }
}
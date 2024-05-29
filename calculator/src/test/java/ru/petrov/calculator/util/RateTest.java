package ru.petrov.calculator.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.petrov.calculator.dto.EmploymentDto;
import ru.petrov.calculator.dto.ScoringDataDto;
import ru.petrov.calculator.dto.enums.EmploymentStatus;
import ru.petrov.calculator.dto.enums.Gender;
import ru.petrov.calculator.dto.enums.MaritalStatus;
import ru.petrov.calculator.dto.enums.Position;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RateTest {

    @Test
    @DisplayName("Получение отказа для безработного ")
    void getRateGetReject() {
        EmploymentDto unemployedDto = new EmploymentDto(EmploymentStatus.UNEMPLOYED, "22222", BigDecimal.valueOf(500000), Position.MIDDLE_MANAGER, 20, 10);
        ScoringDataDto scoringUnemployedDto = new ScoringDataDto(BigDecimal.valueOf(35000),
                12, "Ivanov", "Ivan", "Ivanovich",
                Gender.MALE, LocalDate.of(2000, 05, 29), "7007",
                "111222", LocalDate.of(2018, 05, 30), "TTT",
                MaritalStatus.DIVORCED, 0, unemployedDto, "555", false, false);
        assertTrue(Rate.getRate(scoringUnemployedDto, BigDecimal.TEN).isEmpty());
    }

    @Test
    @DisplayName("Получение ставки")
    void getRate() {
        EmploymentDto unemployedDto = new EmploymentDto(EmploymentStatus.BUSINESS_OWNER, "22222", BigDecimal.valueOf(500000), Position.MIDDLE_MANAGER, 20, 10);
        ScoringDataDto scoringDataDto = new ScoringDataDto(BigDecimal.valueOf(35000),
                12, "Ivanov", "Ivan", "Ivanovich",
                Gender.MALE, LocalDate.of(2000, 05, 29), "7007",
                "111222", LocalDate.of(2018, 05, 30), "TTT",
                MaritalStatus.DIVORCED, 0, unemployedDto, "555", false, false);
        assertTrue(Rate.getRate(scoringDataDto, BigDecimal.TEN).isPresent());
    }

    @Test
    @DisplayName("Ставка для мужчины старше 30 ставка меньше чем для мужчины младше 30")
    void getRateReduction() {
        EmploymentDto unemployedDto = new EmploymentDto(EmploymentStatus.BUSINESS_OWNER, "22222", BigDecimal.valueOf(500000), Position.MIDDLE_MANAGER, 20, 10);
        ScoringDataDto scoringYoungDto = new ScoringDataDto(BigDecimal.valueOf(35000),
                12, "Ivanov", "Ivan", "Ivanovich",
                Gender.MALE, LocalDate.of(2000, 05, 29), "7007",
                "111222", LocalDate.of(2018, 05, 30), "TTT",
                MaritalStatus.DIVORCED, 0, unemployedDto, "555", false, false);
        ScoringDataDto scoringOldDto = new ScoringDataDto(BigDecimal.valueOf(35000),
                12, "Ivanov", "Ivan", "Ivanovich",
                Gender.MALE, LocalDate.of(1980, 05, 29), "7007",
                "111222", LocalDate.of(2018, 05, 30), "TTT",
                MaritalStatus.DIVORCED, 0, unemployedDto, "555", false, false);
        BigDecimal rateYang = Rate.getRate(scoringYoungDto, BigDecimal.TEN).get();
        BigDecimal rateOld = Rate.getRate(scoringOldDto, BigDecimal.TEN).get();
        assertTrue(rateYang.compareTo(rateOld)>0);
    }
}
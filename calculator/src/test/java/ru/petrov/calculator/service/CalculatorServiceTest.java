package ru.petrov.calculator.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.petrov.calculator.dto.EmploymentDto;
import ru.petrov.calculator.dto.LoanStatementRequestDto;
import ru.petrov.calculator.dto.ScoringDataDto;
import ru.petrov.calculator.dto.enums.EmploymentStatus;
import ru.petrov.calculator.dto.enums.Gender;
import ru.petrov.calculator.dto.enums.MaritalStatus;
import ru.petrov.calculator.dto.enums.Position;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CalculatorServiceTest {

    @Autowired
    private CalculatorService calculatorService;

    //todo assertEquals(4, calculatorService.preScoring(request).size()); - перевести на json
    // подготовить тестовый ответ в виде json. Результат через маппер перевести в json и сравнить с эталонным.
    //есть параметры маппера по виду сравнения compareMode. JSONAssert.assertEquals(string, string, compareMode)

    @Test
    @DisplayName("Проверка количества возвращаемых предложений")
    void testPreScoringCountOffers() {
        LoanStatementRequestDto request = new LoanStatementRequestDto(BigDecimal.valueOf(40000),
                12, "Ivanov", "Ivan", "Ivanovich", "sfd@sdf.ru",
                LocalDate.of(2000, 5, 30), "5555", "878989");
        assertEquals(4, calculatorService.preScoring(request).size());

    }

    @Test
    @DisplayName("Проверка соответствия параметров ответов параметрам запросов")
    void testPreScoringEquals() {
        BigDecimal amount = BigDecimal.valueOf(40000);
        int term = 12;
        LoanStatementRequestDto request = new LoanStatementRequestDto(amount,
                term, "Ivanov", "Ivan", "Ivanovich", "sfd@sdf.ru",
                LocalDate.of(2000, 5, 30), "5555", "878989");
        assertAll(
                "Соответствие параметров ответов параметрам запросов",
                () -> assertEquals(amount, calculatorService.preScoring(request).get(0).getRequestedAmount()),
                () -> assertEquals(term, calculatorService.preScoring(request).get(0).getTerm())
        );
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
        assertNotNull(calculatorService.scoring(scoringDataDto));
    }
}
package ru.petrov.calculator.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.JSONException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.petrov.calculator.CalculatorApplicationTests;
import ru.petrov.calculator.dto.LoanOfferDto;
import ru.petrov.calculator.dto.LoanStatementRequestDto;
import ru.petrov.calculator.dto.ScoringDataDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CalculatorServiceTest extends CalculatorApplicationTests {

    @Autowired
    private CalculatorService calculatorService;
    @Test
    @DisplayName("Проверка количества возвращаемых предложений")
    void testPreScoringCountOffers() throws JsonProcessingException {
        assertEquals(4, calculatorService
                .preScoring(getObjectMapper().readValue(getStringFromFile("good_LoanStatementRequestDto_1.json"),
                        LoanStatementRequestDto.class)).size());
    }

    @Test
    @DisplayName("Сверка результата с эталоном")
    void testPreScoringEquals() throws JSONException, JsonProcessingException {
        List<LoanOfferDto> loanOfferDtos = calculatorService
                .preScoring(getObjectMapper().readValue(getStringFromFile("good_LoanStatementRequestDto_1.json"),
                        LoanStatementRequestDto.class));
        String loanOffersActual = getObjectMapper().writeValueAsString(loanOfferDtos);
        String loanOffersExpected = getStringFromFile("good_list_LoanOfferDto_1.json");
        JSONAssert.assertEquals(loanOffersExpected, loanOffersActual, JSONCompareMode.LENIENT);
    }

    @Test
    @DisplayName("Проверка получения отказа (null)")
    void testScoringReturnNull() throws JsonProcessingException {
        ScoringDataDto scoringDataDto1 = getObjectMapper().readValue(getStringFromFile("bad_ScoringDataDto_1.json"),
                ScoringDataDto.class);
        ScoringDataDto scoringDataDto2 = getObjectMapper().readValue(getStringFromFile("bad_ScoringDataDto_2.json"),
                ScoringDataDto.class);
        ScoringDataDto scoringDataDto3 = getObjectMapper().readValue(getStringFromFile("bad_ScoringDataDto_3.json"),
                ScoringDataDto.class);
        assertAll("Отказы по условиям в запросе",
                () -> assertNull(calculatorService.scoring(scoringDataDto1)),
                () -> assertNull(calculatorService.scoring(scoringDataDto2)),
                () -> assertNull(calculatorService.scoring(scoringDataDto3))
        );
    }

    @Test
    @DisplayName("Проверка успешного получения кредитного предложения")
    void testScoringReturnNotNull() throws JsonProcessingException {
        ScoringDataDto scoringDataDto = getObjectMapper().readValue(getStringFromFile("good_ScoringDataDto_1.json"),
                ScoringDataDto.class);
        assertNotNull(calculatorService.scoring(scoringDataDto));
    }
}
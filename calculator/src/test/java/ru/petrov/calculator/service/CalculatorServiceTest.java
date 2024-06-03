package ru.petrov.calculator.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import ru.petrov.calculator.dto.LoanOfferDto;
import ru.petrov.calculator.dto.LoanStatementRequestDto;
import ru.petrov.calculator.dto.ScoringDataDto;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CalculatorServiceTest {

    @Autowired
    private CalculatorService calculatorService;
    @Test
    @DisplayName("Проверка количества возвращаемых предложений")
    void testPreScoringCountOffers() {
        assertEquals(4, calculatorService
                .preScoring((LoanStatementRequestDto) getObjectFromFile("good_LoanStatementRequestDto_1.json",
                        LoanStatementRequestDto.class)).size());
    }

    @Test
    @DisplayName("Сверка результата с эталоном")
    void testPreScoringEquals() throws JSONException {
        List<LoanOfferDto> loanOfferDtos = calculatorService
                .preScoring((LoanStatementRequestDto) getObjectFromFile("good_LoanStatementRequestDto_1.json",
                        LoanStatementRequestDto.class));
        String loanOffersActual = getStringFromObject(loanOfferDtos);
        String loanOffersActualCleared = removeFieldFromJsonArray(loanOffersActual, "statementId");
        String loanOffersExpected = getStringFromFile("good_list_LoanOfferDto_1.json");
        String loanOffersExpectedCleared = removeFieldFromJsonArray(loanOffersExpected, "statementId");
        JSONAssert.assertEquals(loanOffersExpectedCleared, loanOffersActualCleared, JSONCompareMode.LENIENT);
    }

    @Test
    @DisplayName("Проверка получения отказа (null)")
    void testScoringReturnNull() {
        ScoringDataDto scoringDataDto1 = (ScoringDataDto) getObjectFromFile("bad_ScoringDataDto_1.json", ScoringDataDto.class);
        ScoringDataDto scoringDataDto2 = (ScoringDataDto) getObjectFromFile("bad_ScoringDataDto_2.json", ScoringDataDto.class);
        ScoringDataDto scoringDataDto3 = (ScoringDataDto) getObjectFromFile("bad_ScoringDataDto_3.json", ScoringDataDto.class);
        assertAll("Отказы по условиям в запросе",
                () -> assertNull(calculatorService.scoring(scoringDataDto1)),
                () -> assertNull(calculatorService.scoring(scoringDataDto2)),
                () -> assertNull(calculatorService.scoring(scoringDataDto3))
        );
    }

    @Test
    @DisplayName("Проверка успешного получения кредитного предложения")
    void testScoringReturnNotNull() {
        ScoringDataDto scoringDataDto = (ScoringDataDto) getObjectFromFile("good_ScoringDataDto_1.json", ScoringDataDto.class);
        assertNotNull(calculatorService.scoring(scoringDataDto));
    }

    private ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new ParameterNamesModule());
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    private Object getObjectFromFile(String path, Class clazz) {
        try {
            File file = new ClassPathResource(path).getFile();
            return getObjectMapper().readValue(file, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getStringFromObject(Object obj) {
        try {
            return getObjectMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String getStringFromFile(String path) {
        try {
            File file = new ClassPathResource(path).getFile();
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String removeFieldFromJsonArray(String jsonArrayString, String fieldToRemove) {
        try {
            JSONArray jsonArray = new JSONArray(jsonArrayString);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                jsonObject.remove(fieldToRemove);
            }
            return jsonArray.toString();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
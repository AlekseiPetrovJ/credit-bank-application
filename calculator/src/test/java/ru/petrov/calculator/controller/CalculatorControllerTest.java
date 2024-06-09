package ru.petrov.calculator.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.petrov.calculator.CalculatorApplicationTests;
import ru.petrov.calculator.dto.CreditDto;
import ru.petrov.calculator.dto.LoanOfferDto;
import ru.petrov.calculator.dto.LoanStatementRequestDto;
import ru.petrov.calculator.dto.ScoringDataDto;
import ru.petrov.calculator.service.CalculatorService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CalculatorController.class)
class CalculatorControllerTest extends CalculatorApplicationTests {
    @MockBean
    CalculatorService calculatorService;

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("Получение расчета с валидным запросом")
    void testWithMVC() throws Exception {
        LoanStatementRequestDto request = getObjectMapper().readValue(getStringFromFile("good_LoanStatementRequestDto_1.json"),
                LoanStatementRequestDto.class);
        String stringLoanOffersDto = getStringFromFile("good_list_LoanOfferDto_1.json");
        List<LoanOfferDto> listLoanOffersDto = getObjectMapper().readValue(stringLoanOffersDto,
                getObjectMapper().getTypeFactory().constructCollectionType(List.class, LoanOfferDto.class));
        when(calculatorService.preScoring(request)).thenReturn(listLoanOffersDto);
        mvc.perform(post("/calculator/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(stringLoanOffersDto));
    }

    @Test
    @DisplayName("Получение offers с невалидным запросом")
    void testOfferWithBindingResultErrors() throws Exception {
        String request = getStringFromFile("bad_LoanStatementRequestDto_1.json");
        mvc.perform(post("/calculator/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Проверка валидного вызова calc")
    void testCalcControllerWithoutErr() throws Exception {
        ScoringDataDto request = getObjectMapper().readValue(getStringFromFile("good_ScoringDataDto_1.json"),
                ScoringDataDto.class);
        String stringCreditDto = getStringFromFile("good_CreditDto.json");
        CreditDto creditDto = getObjectMapper().readValue(stringCreditDto,
                CreditDto.class);
        when(calculatorService.scoring(request)).thenReturn(creditDto);

        mvc.perform(post("/calculator/calc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk()).
                andExpect(MockMvcResultMatchers.content().json(stringCreditDto));
    }

    @Test
    @DisplayName("Проверка невалидного вызова calc")
    void testCalcControllerWithErr() throws Exception {
        String stringScoringDataDto = getStringFromFile("bad_ScoringDataDto_1.json");
        ScoringDataDto request = getObjectMapper().readValue(stringScoringDataDto, ScoringDataDto.class);

        mvc.perform(post("/calculator/calc").contentType(MediaType.APPLICATION_JSON)
                        .content(stringScoringDataDto))
                .andExpect(status().isBadRequest());
    }
}
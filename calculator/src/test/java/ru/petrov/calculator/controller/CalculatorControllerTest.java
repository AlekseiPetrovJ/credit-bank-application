package ru.petrov.calculator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.petrov.calculator.dto.*;
import ru.petrov.calculator.dto.enums.EmploymentStatus;
import ru.petrov.calculator.dto.enums.Gender;
import ru.petrov.calculator.dto.enums.MaritalStatus;
import ru.petrov.calculator.dto.enums.Position;
import ru.petrov.calculator.service.CalculatorService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CalculatorController.class)
class CalculatorControllerTest {
    @MockBean
    CalculatorService calculatorService;

    @Autowired
    private MockMvc mvc;


    @Test
    @DisplayName("Получение расчета с валидным запросом")
    void testWithMVC() throws Exception {
        LoanStatementRequestDto request = new LoanStatementRequestDto(BigDecimal.valueOf(40000),
                12, "Ivanov", "Ivan", "Ivanovich", "sfd@sdf.ru",
                LocalDate.now().minusYears(20), "5555", "600004");
        List<LoanOfferDto> loanOfferDtos = Collections.singletonList(new LoanOfferDto(UUID.randomUUID(),
                BigDecimal.valueOf(40000), BigDecimal.TEN, 12, BigDecimal.TEN, BigDecimal.TEN, false, false));

        when(calculatorService.preScoring(request)).thenReturn(loanOfferDtos);
        mvc.perform(post("/calculator/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].requestedAmount").value("40000"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].term").value("12"));
    }

    @Test
    @DisplayName("Получение offers с невалидным запросом")
    void testOfferWithBindingResultErrors() throws Exception {
        LoanStatementRequestDto request = new LoanStatementRequestDto(BigDecimal.valueOf(40000),
                12, "eeeeee", "rrrrrrr", "eeeee", "wefwef@sdfs.ru",
                LocalDate.now().minusYears(15), "5555", "600004");
        mvc.perform(post("/calculator/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Проверка валидного вызова calc")
    void testCalcControllerWithoutErr() throws Exception {
        EmploymentDto unemployedDto = new EmploymentDto(EmploymentStatus.UNEMPLOYED, "22222", BigDecimal.valueOf(500000), Position.MIDDLE_MANAGER, 20, 10);
        ScoringDataDto request = new ScoringDataDto(BigDecimal.valueOf(35000),
                12, "Ivanov", "Ivan", "Ivanovich",
                Gender.MALE, LocalDate.now().minusYears(20), "7007",
                "111222", LocalDate.now().minusYears(4), "TTT",
                MaritalStatus.DIVORCED, 0, unemployedDto, "555", false, false);
        CreditDto creditDto = new CreditDto(BigDecimal.valueOf(35000),
                12, BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN, true, true, new ArrayList<>());
        when(calculatorService.scoring(request)).thenReturn(creditDto);

        mvc.perform(post("/calculator/calc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.amount").value("35000"));
    }

    @Test
    @DisplayName("Проверка невалидного вызова calc")
    void testCalcControllerWithErr() throws Exception {
        EmploymentDto unemployedDto = new EmploymentDto(EmploymentStatus.UNEMPLOYED, "22222", BigDecimal.valueOf(500000), Position.MIDDLE_MANAGER, 20, 10);
        ScoringDataDto request = new ScoringDataDto(BigDecimal.valueOf(35000),
                12, "Ivanov", "Ivan", "Ivanovich",
                Gender.MALE, LocalDate.now().minusYears(15), "7007",
                "111222", LocalDate.now().minusYears(1), "TTT",
                MaritalStatus.DIVORCED, 0, unemployedDto, "555", false, false);

        mvc.perform(post("/calculator/calc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isBadRequest());
    }

    private String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
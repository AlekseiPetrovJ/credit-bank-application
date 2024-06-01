package ru.petrov.calculator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.validation.BindingResult;
import ru.petrov.calculator.dto.*;
import ru.petrov.calculator.dto.enums.EmploymentStatus;
import ru.petrov.calculator.dto.enums.Gender;
import ru.petrov.calculator.dto.enums.MaritalStatus;
import ru.petrov.calculator.dto.enums.Position;
import ru.petrov.calculator.service.CalculatorService;
import ru.petrov.calculator.util.validator.LoanStatementRequestDtoValidator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(CalculatorController.class)
class CalculatorControllerTest {
    @Mock
    private BindingResult bindingResult;
    @MockBean
    CalculatorService calculatorService;
    @MockBean
    LoanStatementRequestDtoValidator loanStatementRequestDtoValidator;
    @Mock
    private ScoringDataDto scoringDataDto;
    @Mock
    private LoanStatementRequestDto loanStatementRequestDto;

    @Autowired
    private MockMvc mvc;
    @Autowired
    private CalculatorController calculatorController;

    @Test
    @DisplayName("Получение расчета с валидным запросом")
    void testWithMVC() throws Exception {
        LoanStatementRequestDto request = new LoanStatementRequestDto(BigDecimal.valueOf(40000),
                12, "Ivanov", "Ivan", "Ivanovich", "sfd@sdf.ru",
                LocalDate.of(2000, 5, 30), "5555", "600004");
        List<LoanOfferDto> loanOfferDtos = Collections.singletonList(new LoanOfferDto(UUID.randomUUID(),
                BigDecimal.valueOf(40000), BigDecimal.TEN, 12, BigDecimal.TEN, BigDecimal.TEN, false, false));

        when(calculatorService.preScoring(request)).thenReturn(loanOfferDtos);
        mvc.perform(post("/calculator/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].requestedAmount").value("40000"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].term").value("12"));
    }

    @Test
    @DisplayName("Получение расчета с невалидным запросом")
    void testOfferWithBindingResultErrors() throws Exception {
        LoanStatementRequestDto request = new LoanStatementRequestDto(BigDecimal.valueOf(40000),
                12, "", "", "", "",
                LocalDate.of(2000, 5, 30), "", "");
        mvc.perform(post("/calculator/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("Проверка вызова валидации")
    void testOffersValidation() {
        when(calculatorService.preScoring(loanStatementRequestDto)).thenReturn(null);
        calculatorController.offers(loanStatementRequestDto, bindingResult);
        verify(loanStatementRequestDtoValidator).validate(any(), any());
    }

    @Test
    @DisplayName("Проверка валидного вызова")
    void testCalcControllerWithoutErr() throws Exception {
        EmploymentDto unemployedDto = new EmploymentDto(EmploymentStatus.UNEMPLOYED, "22222", BigDecimal.valueOf(500000), Position.MIDDLE_MANAGER, 20, 10);
        ScoringDataDto request = new ScoringDataDto(BigDecimal.valueOf(35000),
                12, "Ivanov", "Ivan", "Ivanovich",
                Gender.MALE, LocalDate.of(2000, 5, 29), "7007",
                "111222", LocalDate.of(2018, 5, 30), "TTT",
                MaritalStatus.DIVORCED, 0, unemployedDto, "555", false, false);
        CreditDto creditDto = new CreditDto(BigDecimal.valueOf(35000),
                12, BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN, true, true, new ArrayList<>());
        when(calculatorService.scoring(request)).thenReturn(creditDto);
        mvc.perform(post("/calculator/calc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.amount").value("35000"));
    }
}
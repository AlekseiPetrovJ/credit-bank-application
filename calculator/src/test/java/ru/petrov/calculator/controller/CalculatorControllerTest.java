package ru.petrov.calculator.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import ru.petrov.calculator.dto.LoanStatementRequestDto;
import ru.petrov.calculator.dto.ScoringDataDto;
import ru.petrov.calculator.service.CalculatorService;
import ru.petrov.calculator.util.exception.NotValidDto;
import ru.petrov.calculator.util.validator.LoanStatementRequestDtoValidator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalculatorControllerTest {
    @Mock
    LoanStatementRequestDtoValidator loanStatementRequestDtoValidator;
    @Mock
    private BindingResult bindingResult;
    @Mock
    private CalculatorService calculatorService;
    @Mock
    private ScoringDataDto scoringDataDto;
    @Mock
    private LoanStatementRequestDto request;

    @InjectMocks
    private CalculatorController calculatorController;

    @Test
    @DisplayName("Проверка вызова валидации")
    void testOffersValidation() {
        when(calculatorService.preScoring(request)).thenReturn(null);
        calculatorController.offers(request, bindingResult);
        verify(loanStatementRequestDtoValidator).validate(any(), any());
    }

    @Test
    @DisplayName("Получение расчета с невалидным запросом")
    void testOfferWithBindingResultErrors() {
        when(bindingResult.hasErrors()).thenReturn(true);
        assertThrows(NotValidDto.class,() -> calculatorController.offers(request, bindingResult));
    }

    @Test
    @DisplayName("Получение расчета с валидным запросом")
    void testOfferWithoutBindingResultErrors() {
        when(bindingResult.hasErrors()).thenReturn(false);
        assertEquals( HttpStatus.OK, calculatorController.offers(request, bindingResult).getStatusCode());
    }

    @Test
    @DisplayName("Проверка вызова CheckBindingResult")
    void testCalcWithBindingResultErrors() {
        when(bindingResult.hasErrors()).thenReturn(true);
        assertThrows(NotValidDto.class,() -> calculatorController.calc(scoringDataDto, bindingResult));
    }

    @Test
    @DisplayName("Проверка валидного вызова")
    void testCalcWithoutBindingResultErrors() {
        when(bindingResult.hasErrors()).thenReturn(false);
        assertEquals( HttpStatus.OK, calculatorController.calc(scoringDataDto, bindingResult).getStatusCode());
    }
}
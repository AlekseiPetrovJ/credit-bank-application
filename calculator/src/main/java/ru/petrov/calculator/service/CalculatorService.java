package ru.petrov.calculator.service;

import ru.petrov.calculator.dto.CreditDto;
import ru.petrov.calculator.dto.LoanOfferDto;
import ru.petrov.calculator.dto.LoanStatementRequestDto;
import ru.petrov.calculator.dto.ScoringDataDto;

import java.util.List;

public interface CalculatorService {
    List<LoanOfferDto> preScoring(LoanStatementRequestDto requestDto);
    CreditDto scoring(ScoringDataDto scoringDataDto);
}

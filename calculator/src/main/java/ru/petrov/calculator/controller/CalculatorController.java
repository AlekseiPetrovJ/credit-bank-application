package ru.petrov.calculator.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.petrov.calculator.dto.CreditDto;
import ru.petrov.calculator.dto.LoanOfferDto;
import ru.petrov.calculator.dto.LoanStatementRequestDto;
import ru.petrov.calculator.dto.ScoringDataDto;
import ru.petrov.calculator.service.CalculatorService;
import ru.petrov.calculator.util.CheckBindingResult;
import ru.petrov.calculator.util.validator.LoanStatementRequestDtoValidator;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/calculator", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class CalculatorController {
    private final CalculatorService calculatorService;
    private final LoanStatementRequestDtoValidator loanStatementRequestDtoValidator;

    @PostMapping("/offers")
    public ResponseEntity<List<LoanOfferDto>> offers(@RequestBody @Valid LoanStatementRequestDto requestDto,
                                                     BindingResult result){

        loanStatementRequestDtoValidator.validate(requestDto, result);
        CheckBindingResult.check(result);

        return ResponseEntity.ok(calculatorService.preScoring(requestDto));
    }

    @PostMapping("/calc")
    public ResponseEntity<CreditDto> calc(@RequestBody @Valid ScoringDataDto scoringDataDto, BindingResult result){
        CheckBindingResult.check(result);
        return ResponseEntity.ok(calculatorService.scoring(scoringDataDto));
    }
}

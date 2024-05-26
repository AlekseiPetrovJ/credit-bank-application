package ru.petrov.calculator.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.petrov.calculator.dto.LoanStatementRequestDto;
import ru.petrov.calculator.service.CalculatorService;
import ru.petrov.calculator.util.CheckBindingResult;
import ru.petrov.calculator.util.validator.LoanStatementRequestDtoValidator;

@RestController
@RequestMapping(path = "/calculator")
public class Calculator {
    private final CalculatorService calculatorService;
    private final LoanStatementRequestDtoValidator loanStatementRequestDtoValidator;

    @Autowired
    public Calculator(CalculatorService calculatorService, LoanStatementRequestDtoValidator loanStatementRequestDtoValidator) {
        this.calculatorService = calculatorService;
        this.loanStatementRequestDtoValidator = loanStatementRequestDtoValidator;
    }

    @PostMapping("/offers")
    public ResponseEntity<?> offers(@RequestBody @Valid LoanStatementRequestDto requestDto, BindingResult result){

        loanStatementRequestDtoValidator.validate(requestDto, result);
        new CheckBindingResult().check(result);

        return ResponseEntity.ok(calculatorService.preScoring(requestDto));
    }
}

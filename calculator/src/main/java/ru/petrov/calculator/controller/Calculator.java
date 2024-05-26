package ru.petrov.calculator.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.petrov.calculator.dto.LoanStatementRequestDto;
import ru.petrov.calculator.service.CalculatorService;

@RestController
@RequestMapping(path = "/calculator")
public class Calculator {
    private final CalculatorService calculatorService;

    @Autowired
    public Calculator(CalculatorService calculatorService) {
        this.calculatorService = calculatorService;
    }

    //todo Посоветоваться. Некрасиво возвращается список ошибок валидации. Как улучшить?
    @PostMapping("/offers")
    public ResponseEntity<?> offers(@RequestBody @Valid LoanStatementRequestDto requestDto, BindingResult result){
        if (result.hasErrors()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Validation errors occurred: " + result.getAllErrors());
        }
        return ResponseEntity.ok(calculatorService.preScoring(requestDto));
    }
}

package ru.petrov.calculator.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.petrov.calculator.dto.CreditDto;
import ru.petrov.calculator.dto.LoanOfferDto;
import ru.petrov.calculator.dto.LoanStatementRequestDto;
import ru.petrov.calculator.dto.ScoringDataDto;
import ru.petrov.calculator.service.CalculatorService;
import ru.petrov.calculator.util.exception.NotValidDto;
import ru.petrov.calculator.util.validator.Validator;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/calculator", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class CalculatorController {
    private final CalculatorService calculatorService;

    @PostMapping("/offers")
    public ResponseEntity<List<LoanOfferDto>> offers(@RequestBody @Valid LoanStatementRequestDto requestDto) {
        try {
            Validator.validateAgeOlder18(requestDto);
            return new ResponseEntity<>(calculatorService.preScoring(requestDto), HttpStatus.OK);
        } catch (NotValidDto e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/calc")
    public ResponseEntity<CreditDto> calc(@RequestBody @Valid ScoringDataDto scoringDataDto) {
        try {
            Validator.validateAgeOlder18(scoringDataDto);
            return new ResponseEntity<>(calculatorService.scoring(scoringDataDto), HttpStatus.OK);
        } catch (NotValidDto e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

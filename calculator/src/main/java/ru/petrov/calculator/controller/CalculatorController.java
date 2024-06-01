package ru.petrov.calculator.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RestController
@RequestMapping(path = "/calculator", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class CalculatorController {
    private final CalculatorService calculatorService;

    @PostMapping("/offers")
    public ResponseEntity<List<LoanOfferDto>> offers(@RequestBody @Valid LoanStatementRequestDto requestDto) {
        log.info("POST request {} path {}", requestDto, "/calculator/offers");
        try {
            Validator.validateAgeOlder18(requestDto);
            log.info("successfully passed validation {}", requestDto);
            log.info("POST response {}", new ResponseEntity<>(calculatorService.preScoring(requestDto), HttpStatus.OK));
            return new ResponseEntity<>(calculatorService.preScoring(requestDto), HttpStatus.OK);
        } catch (NotValidDto e) {
            log.error("NotValidDto error {} ", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error(" Exception error {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/calc")
    public ResponseEntity<CreditDto> calc(@RequestBody @Valid ScoringDataDto scoringDataDto) {
        log.info("POST request {} path {}", scoringDataDto, "/calculator/calc");
        try {
            Validator.validateAgeOlder18(scoringDataDto);
            log.info("successfully passed validation {}", scoringDataDto);
            log.info("POST response {}", new ResponseEntity<>(calculatorService.scoring(scoringDataDto), HttpStatus.OK));
            return new ResponseEntity<>(calculatorService.scoring(scoringDataDto), HttpStatus.OK);
        } catch (NotValidDto e) {
            log.error("NotValidDto error {} ", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error(" Exception error {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

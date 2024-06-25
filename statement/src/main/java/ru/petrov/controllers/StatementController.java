package ru.petrov.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.petrov.dto.LoanOfferDto;
import ru.petrov.dto.LoanStatementRequestDto;
import ru.petrov.services.StatementService;
import ru.petrov.util.exception.NotValidDto;
import ru.petrov.util.validator.Validator;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/statement", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class StatementController {
    private final StatementService service;


    @PostMapping()
    public ResponseEntity<List<LoanOfferDto>> offers(@RequestBody @Valid LoanStatementRequestDto requestDto) {
        log.info("POST request {} path {}", requestDto, "/statement");
        try {
            Validator.validateAgeOlder18(requestDto);
            log.info("successfully passed validation {}", requestDto);
            return service.exchangeLoanStatementToOffers(requestDto);

        } catch (NotValidDto e) {
            log.error("NotValidDto error {} ", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Exception error {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/offer")
    @ResponseStatus(code = HttpStatus.OK)
    public ResponseEntity selectOffer(@RequestBody LoanOfferDto loanOfferDto) {
        log.info("POST request {} path /offer", loanOfferDto);
        try {
            return service.selectOffer(loanOfferDto);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
package ru.petrov.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.petrov.dto.*;
import ru.petrov.services.DealService;
import ru.petrov.util.exceptions.StatementNotFoundException;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(path = "/deal", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class DealController {
    private static final String CALCULATOR_URL = "http://127.0.0.1:8080";
    private final DealService dealService;
    private final RestTemplate rest;

    @PostMapping("/statement")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ResponseEntity<List<LoanOfferDto>> createStatement(@RequestBody @Valid LoanStatementRequestDto requestDto) {
        log.info("POST request {} path {}", requestDto, "/deal/statement");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<LoanStatementRequestDto> entity = new HttpEntity<>(requestDto, headers);
        ParameterizedTypeReference<List<LoanOfferDto>> responseTypeRef = new ParameterizedTypeReference<List<LoanOfferDto>>() {};
        ResponseEntity<List<LoanOfferDto>> response =
                rest.exchange(CALCULATOR_URL + "/calculator/offers", HttpMethod.POST, entity, responseTypeRef);
        if (response.getStatusCode()==HttpStatus.OK){
            List<LoanOfferDto> loanOffersDto = response.getBody();
            loanOffersDto.forEach(loanOfferDto -> loanOfferDto.
                    setStatementId(dealService.saveStatement(requestDto).getStatementId()));
            log.info("POST response to path {} was CREATED", "/deal/statement");

            return new ResponseEntity<>(loanOffersDto, HttpStatus.CREATED);
        }
        log.info("POST response to path {} was UNPROCESSABLE_ENTITY", "/deal/statement");
        return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @PostMapping("/offer/select")
    public ResponseEntity<Object> selectOffer(@RequestBody LoanOfferDto loanOfferDto) {
        log.info("POST request {} path /offer/select", loanOfferDto);
        try {
            dealService.selectOffer(loanOfferDto);
            log.info("POST response to path {} was Ok", "/offer/select");
            return new ResponseEntity<>(null, HttpStatus.OK);
        } catch (StatementNotFoundException e) {
            log.info("POST response to path {} was NOT_FOUND", "/offer/select");
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/calculate/{statementId}")
    public ResponseEntity<Object> setScoring(@PathVariable("statementId") UUID uuid,
                                              @RequestBody @Valid FinishRegistrationRequestDto finishRequest) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            ScoringDataDto scoringDataDto = dealService.finishCalculationLoan(uuid, finishRequest);
            HttpEntity<ScoringDataDto> entity = new HttpEntity<>(scoringDataDto, headers);
            ParameterizedTypeReference<CreditDto> responseTypeRef = new ParameterizedTypeReference<CreditDto>() {
            };
            ResponseEntity<CreditDto> response =
                    rest.exchange(CALCULATOR_URL + "/calculator/calc", HttpMethod.POST, entity, responseTypeRef);
            if (response.getStatusCode() == HttpStatus.OK) {
                dealService.saveCredit(uuid, response.getBody());
                return new ResponseEntity<>(null, HttpStatus.OK);
            } else {
                log.error("Get some error from other MC calculator/calc");
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (StatementNotFoundException e) {
            log.info("POST response to path /calculate/{} was NOT_FOUND", uuid);
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
}

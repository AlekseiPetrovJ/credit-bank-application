package ru.petrov.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.petrov.dto.*;
import ru.petrov.models.Statement;
import ru.petrov.services.DealService;
import ru.petrov.util.RestUtil;
import ru.petrov.util.exceptions.StatementNotFoundException;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(path = "/deal", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class DealController {
    private static final String CALCULATOR_URL = "http://127.0.0.1:8084";
    private final DealService dealService;
    private final RestUtil restUtil;

    @PostMapping("/statement")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ResponseEntity<List<LoanOfferDto>> createStatement(@RequestBody @Valid LoanStatementRequestDto requestDto) {
        log.info("POST request {} path {}", requestDto, "/deal/statement");
        Statement statement = dealService.saveStatement(requestDto);
        ResponseEntity<List<LoanOfferDto>> response = restUtil.exchangeDtoToEntity(CALCULATOR_URL + "/calculator/offers",
                requestDto,
                new ParameterizedTypeReference<>() {
                });
        if (response.getStatusCode()==HttpStatus.OK){
            List<LoanOfferDto> loanOffersDto = response.getBody();
            loanOffersDto.forEach(loanOfferDto -> loanOfferDto.
                    setStatementId(statement.getStatementId()));
            log.info("POST response to path {} was CREATED", "/deal/statement");

            return new ResponseEntity<>(loanOffersDto, HttpStatus.CREATED);
        }
        log.info("POST response to path {} was UNPROCESSABLE_ENTITY", "/deal/statement");
        return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @PostMapping("/offer/select")
    @ResponseStatus(code = HttpStatus.OK)
    public ResponseEntity selectOffer(@RequestBody LoanOfferDto loanOfferDto) {
        log.info("POST request {} path /offer/select", loanOfferDto);
        try {
            dealService.selectOffer(loanOfferDto);
            log.info("POST response to path {} was Ok", "/offer/select");
            return new ResponseEntity(HttpStatus.OK);
        } catch (StatementNotFoundException e) {
            log.info("POST response to path {} was NOT_FOUND", "/offer/select");
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/calculate/{statementId}")
    @ResponseStatus(code = HttpStatus.OK)
    public ResponseEntity setScoring(@PathVariable("statementId") UUID uuid,
                                              @RequestBody @Valid FinishRegistrationRequestDto finishRequest) {
        log.info("POST request {} to path /calculate/{}", finishRequest, uuid);

        try {
            ScoringDataDto scoringDataDto = dealService.finishCalculationLoan(uuid, finishRequest);

            ResponseEntity<CreditDto> response = restUtil.exchangeDtoToEntity(CALCULATOR_URL + "/calculator/calc",
                    scoringDataDto,
                    new ParameterizedTypeReference<>() {
                    });
            if (response.getStatusCode() == HttpStatus.OK) {
                dealService.saveCredit(uuid, response.getBody());
                log.info("POST request {} to path /calculate/{} returned OK", finishRequest, uuid);

                return new ResponseEntity(HttpStatus.OK);
            } else {
                log.error("Get some error from other MC calculator/calc");
                return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (StatementNotFoundException e) {
            log.info("POST response to path /calculate/{} was NOT_FOUND", uuid);
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/document/{statementId}/send")
    @ResponseStatus(code = HttpStatus.OK)
    public ResponseEntity sendDocument(@PathVariable("statementId") UUID uuid) {
        log.info("POST response to path /document/{}/send", uuid);

        try {
            dealService.sendDocument(uuid);
            log.info("POST response to path /document/{}/send returned OK", uuid);
            return new ResponseEntity(HttpStatus.OK);
        } catch (StatementNotFoundException e) {
            log.error("POST response to path /document/{}/send was NOT_FOUND", uuid);
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/document/{statementId}/sign")
    @ResponseStatus(code = HttpStatus.OK)
    public ResponseEntity signDocument(@PathVariable("statementId") UUID uuid) {
        try {
            dealService.signDocument(uuid);
            return new ResponseEntity(HttpStatus.OK);
        } catch (StatementNotFoundException e) {
            log.error("POST response to path /document/{}/sign was NOT_FOUND", uuid);
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/document/{statementId}/code")
    @ResponseStatus(code = HttpStatus.OK)
    public ResponseEntity codeDocument(@PathVariable("statementId") UUID uuid) {
        try {
            dealService.codeDocument(uuid);
            return new ResponseEntity(HttpStatus.OK);
        } catch (StatementNotFoundException e) {
            log.error("POST response to path /document/{}/code was NOT_FOUND", uuid);
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }
}

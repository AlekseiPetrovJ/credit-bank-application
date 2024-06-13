package ru.petrov.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.petrov.dto.*;
import ru.petrov.models.Client;
import ru.petrov.models.Credit;
import ru.petrov.models.LoanOffer;
import ru.petrov.models.Statement;
import ru.petrov.services.DealService;
import ru.petrov.util.exceptions.StatementNotFoundException;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@Slf4j
@Controller
@RequestMapping(path = "/deal", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class DealController {


    private final DealService dealService;
    private final RestTemplate rest;
    private final ModelMapper mapper;

    @PostMapping("/statement")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ResponseEntity<List<LoanOfferDto>> createStatement(@RequestBody LoanStatementRequestDto requestDto) {
        log.info("POST request {} path {}", requestDto, "/deal/statement");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Client client = mapper.map(requestDto, Client.class);
        Client savedClient = dealService.saveClient(client);
        Statement statement = dealService.createStatement(savedClient);

        HttpEntity<LoanStatementRequestDto> entity = new HttpEntity<>(requestDto, headers);
        ParameterizedTypeReference<List<LoanOfferDto>> responseTypeRef = new ParameterizedTypeReference<List<LoanOfferDto>>() {};
        ResponseEntity<List<LoanOfferDto>> response =
                rest.exchange("http://127.0.0.1:8080/calculator/offers", HttpMethod.POST, entity, responseTypeRef);
        if (response.getStatusCode()==HttpStatus.OK){
            UUID statementId = statement.getStatementId();
            List<LoanOfferDto> loanOffersDto = response.getBody();
            loanOffersDto.forEach(loanOfferDto -> loanOfferDto.setStatementId(statementId));
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
            dealService.selectOffer(mapper.map(loanOfferDto, LoanOffer.class));
            log.info("POST response to path {} was Ok", "/offer/select");
            return new ResponseEntity<>(null, HttpStatus.OK);
        } catch (StatementNotFoundException e) {
            log.info("POST response to path {} was NOT_FOUND", "/offer/select");
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/calculate/{statementId}")
    public ResponseEntity<Object> setScorring(@PathVariable("statementId") UUID uuid,
                                              @RequestBody @Valid FinishRegistrationRequestDto finishRequest) {
        try {
            Client client = dealService.fillClientInStatementAdditionalData(uuid, mapper.map(finishRequest, Client.class));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            ScoringDataDto scoringDataDto = mapper.map(client, ScoringDataDto.class);
            LoanOffer appliedOffer = dealService.getOfferByStatementId(uuid);
            //todo Облагородить. Возможно через mapper
            scoringDataDto.setAmount(appliedOffer.getRequestedAmount());
            scoringDataDto.setTerm(appliedOffer.getTerm());
            scoringDataDto.setIsInsuranceEnabled(appliedOffer.getIsInsuranceEnabled());
            scoringDataDto.setIsSalaryClient(appliedOffer.getIsSalaryClient());

            HttpEntity<ScoringDataDto> entity = new HttpEntity<>(scoringDataDto, headers);
            ParameterizedTypeReference<CreditDto> responseTypeRef = new ParameterizedTypeReference<CreditDto>() {
            };
            ResponseEntity<CreditDto> response =
                    rest.exchange("http://127.0.0.1:8080/calculator/calc", HttpMethod.POST, entity, responseTypeRef);
            if (response.getStatusCode() == HttpStatus.OK) {
                Credit credit = mapper.map(response.getBody(), Credit.class);
                dealService.saveCredit( uuid, credit);
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

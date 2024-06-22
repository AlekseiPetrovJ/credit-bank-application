package ru.petrov.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.petrov.config.CommonProps;
import ru.petrov.dto.LoanOfferDto;
import ru.petrov.dto.LoanStatementRequestDto;
import ru.petrov.util.RestUtil;
import ru.petrov.util.exception.NotValidDto;
import ru.petrov.util.validator.Validator;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/statement", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class StatementController {
    private final CommonProps commonProps;
    private final RestUtil restUtil;


    @PostMapping("/offers")
    public ResponseEntity<List<LoanOfferDto>> offers(@RequestBody @Valid LoanStatementRequestDto requestDto) {
        log.info("POST request {} path {}", requestDto, "/statement/offers");
        try {
            Validator.validateAgeOlder18(requestDto);
            log.info("successfully passed validation {}", requestDto);
            String fullDealUrl = commonProps.getDealUrl() + "/deal/statement";
            log.info("Contacting the MS Deal. URL fullDealUrl: {}; requestDto: {}", fullDealUrl, requestDto);

            ResponseEntity<List<LoanOfferDto>> response = restUtil.exchangeDtoToEntity(fullDealUrl,
                    requestDto,
                    new ParameterizedTypeReference<List<LoanOfferDto>>() {
                    });
            if (response.getStatusCode().is2xxSuccessful()) {
                List<LoanOfferDto> loanOfferDtos = response.getBody();
                log.info("Get {} from {}", loanOfferDtos, fullDealUrl);
                return response;
            } else {
                log.info("Get status code {} from {}", response.getStatusCode(), fullDealUrl);
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (NotValidDto e) {
            log.error("NotValidDto error {} ", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error(" Exception error {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
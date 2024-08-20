package ru.petrov.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.petrov.config.CommonProps;
import ru.petrov.dto.LoanOfferDto;
import ru.petrov.dto.LoanStatementRequestDto;
import ru.petrov.util.RestUtil;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class StatementServiceImpl implements StatementService {
    private final CommonProps commonProps;
    private final RestUtil restUtil;
    private final RestTemplate rest;

    @Override
    public ResponseEntity<List<LoanOfferDto>> exchangeLoanStatementToOffers(LoanStatementRequestDto requestDto)
            throws RestClientException {
        String fullDealUrl = commonProps.getDealUrl() + "/deal/statement";
        log.info("Contacting the MS Deal. URL fullDealUrl: {}; requestDto: {}", fullDealUrl, requestDto);

        ResponseEntity<List<LoanOfferDto>> response = restUtil.exchangeDtoToEntity(fullDealUrl,
                requestDto,
                new ParameterizedTypeReference<>() {
                });
        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Get {} from {}", response, fullDealUrl);
            return response;
        } else {
            log.info("Get status code {} from {}", response.getStatusCode(), fullDealUrl);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity selectOffer(LoanOfferDto loanOfferDto) throws RestClientException {
        String fullDealUrl = commonProps.getDealUrl() + "/deal/offer/select";
        ResponseEntity<Void> response = rest.postForEntity(fullDealUrl, loanOfferDto, Void.class);
        log.info("Get status code {} from {}", response.getStatusCode(), fullDealUrl);

        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

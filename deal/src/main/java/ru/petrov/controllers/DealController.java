package ru.petrov.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import ru.petrov.dto.LoanOfferDto;
import ru.petrov.dto.LoanStatementRequestDto;
import ru.petrov.models.Statement;
import ru.petrov.repositories.ClientRepository;

import java.util.List;

@Slf4j
@Controller
@RequestMapping(path = "/deal", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class DealController {

    private final ClientRepository clientRepository;
    private final RestTemplate rest;
    private final ModelMapper mapper;

    @PostMapping("/statement")
    public ResponseEntity<List<LoanOfferDto>> getStatement(@RequestBody LoanStatementRequestDto requestDto){
        log.info("POST request {} path {}", requestDto, "/deal/statement");
        // Set the headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Statement statement = mapper.map(requestDto, Statement.class);
        System.out.println("print requestDto" + requestDto);
        System.out.println("\n\n\n");
        System.out.println("print requestatementstDto" + statement);

        // Wrap the request object and headers into an HttpEntity
        HttpEntity<LoanStatementRequestDto> entity = new HttpEntity<>(requestDto, headers);
        ParameterizedTypeReference<List<LoanOfferDto>> responseTypeRef = new ParameterizedTypeReference<List<LoanOfferDto>>() {};
        ResponseEntity<List<LoanOfferDto>> response =
                rest.exchange("http://127.0.0.1:8080/calculator/offers", HttpMethod.POST, entity, responseTypeRef);
        if (response.getStatusCode()==HttpStatus.OK){
            List<LoanOfferDto> loanOffersDto = response.getBody();
            return new ResponseEntity<>(loanOffersDto, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
    }
}

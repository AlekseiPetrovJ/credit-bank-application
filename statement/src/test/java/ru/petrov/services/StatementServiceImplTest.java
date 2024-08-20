package ru.petrov.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.petrov.StatementApplicationTest;
import ru.petrov.config.CommonProps;
import ru.petrov.dto.LoanOfferDto;
import ru.petrov.dto.LoanStatementRequestDto;
import ru.petrov.util.RestUtil;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
class StatementServiceImplTest extends StatementApplicationTest {
    @MockBean
    RestUtil restUtil;
    @MockBean
    RestTemplate rest;
    @MockBean
    CommonProps props;

    @Autowired
    StatementService service;

    @Test
    @DisplayName("Получение расчета с валидным запросом")
    void testCreateStatement() throws Exception {
        LoanStatementRequestDto request = getObjectMapper().readValue(getStringFromFile("good_LoanStatementRequestDto_1.json"),
                LoanStatementRequestDto.class);
        String stringLoanOffersDto = getStringFromFile("good_list_LoanOfferDto_1.json");
        List<LoanOfferDto> listLoanOffersDto = getObjectMapper().readValue(stringLoanOffersDto,
                getObjectMapper().getTypeFactory().constructCollectionType(List.class, LoanOfferDto.class));
        ResponseEntity<List<LoanOfferDto>> responseEntity = new ResponseEntity<>(listLoanOffersDto, HttpStatus.OK);
        when(props.getDealUrl()).thenReturn("anyString");
        when(restUtil.exchangeDtoToEntity(
                anyString(),
                any(Object.class),
                any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);
        assertEquals(responseEntity, service.exchangeLoanStatementToOffers(request));
    }

    @Test
    @DisplayName("Получение расчета с валидным запросом")
    void testCreateStatementInternalError() throws Exception {
        LoanStatementRequestDto request = getObjectMapper().readValue(getStringFromFile("good_LoanStatementRequestDto_1.json"),
                LoanStatementRequestDto.class);
        String stringLoanOffersDto = getStringFromFile("good_list_LoanOfferDto_1.json");
        List<LoanOfferDto> listLoanOffersDto = getObjectMapper().readValue(stringLoanOffersDto,
                getObjectMapper().getTypeFactory().constructCollectionType(List.class, LoanOfferDto.class));
        ResponseEntity<List<LoanOfferDto>> responseEntity = new ResponseEntity<>(listLoanOffersDto, HttpStatus.NOT_FOUND);
        when(props.getDealUrl()).thenReturn("anyString");
        when(restUtil.exchangeDtoToEntity(
                anyString(),
                any(Object.class),
                any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, service.exchangeLoanStatementToOffers(request).getStatusCode());
    }

    @Test
    @DisplayName("Выбор корректного offer")
    void testSelectOffer() throws Exception {
        LoanOfferDto request = getObjectMapper().readValue(getStringFromFile("good_LoanOfferDto_1.json"),
                LoanOfferDto.class);
        ResponseEntity responseEntity = new ResponseEntity<>(HttpStatus.OK);

        when(props.getDealUrl()).thenReturn("");

        when(rest.postForEntity(
                "/deal/offer/select",
                request,
                Void.class)).thenReturn(responseEntity);
        assertEquals(responseEntity, service.selectOffer(request));

    }

    @Test
    @DisplayName("Получение ошибки INTERNAL_SERVER_ERROR")
    void testSelectOfferInternalError() throws Exception {
        LoanOfferDto request = getObjectMapper().readValue(getStringFromFile("good_LoanOfferDto_1.json"),
                LoanOfferDto.class);
        ResponseEntity responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);

        when(props.getDealUrl()).thenReturn("");

        when(rest.postForEntity(
                "/deal/offer/select",
                request,
                Void.class)).thenReturn(responseEntity);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, service.selectOffer(request).getStatusCode());
    }

}
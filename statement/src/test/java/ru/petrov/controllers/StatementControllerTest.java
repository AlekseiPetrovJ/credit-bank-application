package ru.petrov.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.petrov.StatementApplicationTest;
import ru.petrov.config.CommonProps;
import ru.petrov.dto.LoanOfferDto;
import ru.petrov.dto.LoanStatementRequestDto;
import ru.petrov.util.RestUtil;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StatementController.class)
class StatementControllerTest extends StatementApplicationTest {
    @MockBean
    RestUtil restUtil;
    @MockBean
    RestTemplate rest;
    @MockBean
    CommonProps props;

    @Autowired
    private MockMvc mvc;

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

        mvc.perform(post("/statement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(stringLoanOffersDto));
    }

    @Test
    @DisplayName("Получение расчета с невалидным запросом")
    void testWithNotValidRequest() throws Exception {
        LoanStatementRequestDto request = getObjectMapper().readValue(getStringFromFile("bad_LoanStatementRequestDto_1.json"),
                LoanStatementRequestDto.class);
        ResponseEntity<List<LoanOfferDto>> responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        when(props.getDealUrl()).thenReturn("anyString");
        when(restUtil.exchangeDtoToEntity(
                anyString(),
                any(Object.class),
                any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);

        mvc.perform(post("/statement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Ответ при возникновении исключения")
    void testWithException() throws Exception {
        LoanStatementRequestDto request = getObjectMapper().readValue(getStringFromFile("good_LoanStatementRequestDto_1.json"),
                LoanStatementRequestDto.class);
        when(props.getDealUrl()).thenReturn("some");
        when(restUtil.exchangeDtoToEntity(
                anyString(),
                any(Object.class),
                any(ParameterizedTypeReference.class)
        )).thenThrow(RestClientException.class);

        mvc.perform(post("/statement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Получение ответа при ошибке на сервере")
    void testInternalError() throws Exception {
        LoanStatementRequestDto request = getObjectMapper().readValue(getStringFromFile("good_LoanStatementRequestDto_1.json"),
                LoanStatementRequestDto.class);
        String stringLoanOffersDto = getStringFromFile("good_list_LoanOfferDto_1.json");
        List<LoanOfferDto> listLoanOffersDto = getObjectMapper().readValue(stringLoanOffersDto,
                getObjectMapper().getTypeFactory().constructCollectionType(List.class, LoanOfferDto.class));
        ResponseEntity<List<LoanOfferDto>> responseEntity = new ResponseEntity<>(listLoanOffersDto, HttpStatus.BAD_REQUEST);
        when(props.getDealUrl()).thenReturn("anyString");
        when(restUtil.exchangeDtoToEntity(
                anyString(),
                any(Object.class),
                any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);

        mvc.perform(post("/statement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isInternalServerError());
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

        mvc.perform(post("/statement/offer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk());
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

        mvc.perform(post("/statement/offer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Возникновение исключения")
    void testSelectOfferException() throws Exception {
        LoanOfferDto request = getObjectMapper().readValue(getStringFromFile("good_LoanOfferDto_1.json"),
                LoanOfferDto.class);
        ResponseEntity responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);

        when(props.getDealUrl()).thenReturn("");

        when(rest.postForEntity(
                "/deal/offer/select",
                request,
                Void.class))
                .thenThrow(RestClientException.class);

        mvc.perform(post("/statement/offer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isInternalServerError());
    }
}
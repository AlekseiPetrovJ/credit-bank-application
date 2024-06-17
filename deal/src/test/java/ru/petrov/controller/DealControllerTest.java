package ru.petrov.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;
import ru.petrov.DealApplicationTest;
import ru.petrov.dto.LoanOfferDto;
import ru.petrov.dto.LoanStatementRequestDto;
import ru.petrov.models.Statement;
import ru.petrov.services.DealService;
import ru.petrov.util.exceptions.StatementNotFoundException;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DealController.class)
class DealControllerTest extends DealApplicationTest {
    @MockBean
    RestTemplate rest;
    @MockBean
    DealService dealService;

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
        when(rest.exchange(
                anyString(),
                any(HttpMethod.class),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);
        Statement statement = new Statement().builder()
                .statementId(UUID.randomUUID())
                .build();
        when(dealService.saveStatement(request))
                .thenReturn(statement);
        mvc.perform(post("/deal/statement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().json(stringLoanOffersDto));
    }

    @Test
    @DisplayName("Выбор корректного offer")
    void testSelectOffer() throws Exception {
        LoanOfferDto request = getObjectMapper().readValue(getStringFromFile("good_LoanOfferDto_1.json"),
                LoanOfferDto.class);
        when(dealService.selectOffer(any(LoanOfferDto.class)))
                .thenReturn(any(LoanOfferDto.class));
        mvc.perform(post("/deal/offer/select")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Выбор некорректного offer")
    void testSelectIsNotCorrectOffer() throws Exception {
        LoanOfferDto request = getObjectMapper().readValue(getStringFromFile("good_LoanOfferDto_1.json"),
                LoanOfferDto.class);
        when(dealService.selectOffer(any(LoanOfferDto.class)))
                .thenThrow(StatementNotFoundException.class);
        mvc.perform(post("/deal/offer/select")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isNotFound());
    }
}
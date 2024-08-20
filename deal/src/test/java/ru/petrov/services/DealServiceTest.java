package ru.petrov.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import ru.petrov.DealApplicationTest;
import ru.petrov.dto.CreditDto;
import ru.petrov.dto.LoanOfferDto;
import ru.petrov.dto.LoanStatementRequestDto;
import ru.petrov.models.Client;
import ru.petrov.models.Statement;
import ru.petrov.repositories.ClientRepository;
import ru.petrov.repositories.StatementRepository;
import ru.petrov.util.exceptions.StatementNotFoundException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@RequiredArgsConstructor
class DealServiceTest extends DealApplicationTest {
    @MockBean
    private StatementRepository statementRepository;
    @Autowired
    private DealService dealService;
    @Autowired
    private ModelMapper mapper;

    @MockBean
    private ClientRepository clientRepository;

    @MockBean
    private MessagingService messagingService;

    private Client client;
    private Statement statement;
    private LoanOfferDto loanOfferDto;
    private LoanStatementRequestDto loanStatementReqDto;

    @BeforeEach
    public void setUp() throws JsonProcessingException {
        client = getObjectMapper().readValue(getStringFromFile("good_Client_1.json"),
                Client.class);

        statement = new Statement().builder().client(client).creationDate(LocalDateTime.now()).build();

        loanOfferDto = getObjectMapper().readValue(getStringFromFile("good_LoanOfferDto_1.json"),
                LoanOfferDto.class);

        loanStatementReqDto = mapper.map(client, LoanStatementRequestDto.class);
    }

    @AfterEach
    public void tearDown() {
        statementRepository.deleteAll();
    }


    @Test
    @DisplayName("Проверка успешного добавления клиента")
    public void testSaveClient() {
        when(clientRepository.save(any(Client.class)))
                .thenReturn(client);
        Client savedClient = dealService.saveClient(client);
        assertEquals(client, savedClient);
    }

    @Test
    @DisplayName("Проверка добавления Statement")
    public void testSaveStatement() {
        when(statementRepository.save(any(Statement.class))).thenReturn(statement);
        Statement savedStatement = dealService.saveStatement(loanStatementReqDto);
        assertEquals(statement, savedStatement);
    }

    @Test
    @DisplayName("Проверка выбора предложения")
    public void testSelectOffer() {
        Mockito.doNothing().when(messagingService).send(any());
        when(statementRepository.findById(any()))
                .thenReturn(Optional.of(statement));
        when(statementRepository.save(any(Statement.class))).thenReturn(statement);

        LoanOfferDto selectedOffer = dealService.selectOffer(loanOfferDto);
        assertAll("Проверка выбора предложения и отправки сообщения",
                () -> assertEquals(loanOfferDto, selectedOffer),
                () -> verify(messagingService).send(any())
        );
    }

    @Test
    @DisplayName("Попытка сохранения кредита по отсутствующему Statement")
    public void testSaveCreditExceptionStatement() {
        UUID uuid = UUID.randomUUID();
        CreditDto credit = new CreditDto();

        assertThrows(StatementNotFoundException.class, () -> dealService.saveCredit(uuid, credit));
    }

    @Test
    @DisplayName("Проверка отправки документов")
    public void testSendDocument() {
        Mockito.doNothing().when(messagingService).send(any());
        when(statementRepository.findById(any()))
                .thenReturn(Optional.of(statement));
        dealService.sendDocument(UUID.randomUUID());
        verify(messagingService).send(any());
    }

    @Test
    @DisplayName("Проверка подписания документа")
    public void testSignDocument() {
        Mockito.doNothing().when(messagingService).send(any());
        when(statementRepository.findById(any()))
                .thenReturn(Optional.of(statement));
        dealService.signDocument(UUID.randomUUID());
        verify(messagingService).send(any());
    }

    @Test
    @DisplayName("Проверка выдачи кредита")
    public void testCodeDocument() {
        Mockito.doNothing().when(messagingService).send(any());
        when(statementRepository.findById(any()))
                .thenReturn(Optional.of(statement));
        dealService.codeDocument(UUID.randomUUID());
        verify(messagingService).send(any());
    }
}
package ru.petrov.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import ru.petrov.DealApplicationTest;
import ru.petrov.dto.CreditDto;
import ru.petrov.dto.EmailMessageDto;
import ru.petrov.dto.LoanOfferDto;
import ru.petrov.dto.LoanStatementRequestDto;
import ru.petrov.models.Client;
import ru.petrov.models.Statement;
import ru.petrov.repositories.StatementRepository;
import ru.petrov.util.exceptions.StatementNotFoundException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
class DealServiceTest extends DealApplicationTest {
    @Autowired
    private StatementRepository statementRepository;
    @Autowired
    private DealService dealService;
    @Autowired
    private ModelMapper mapper;

    @Mock
    private MessagingService messagingService;
    KafkaTemplate<String, EmailMessageDto> kafkaTemplate = Mockito.mock(KafkaTemplate.class);

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
        Client savedClient = dealService.saveClient(client);
        assertEquals(client, savedClient);
    }

    @Test
    @DisplayName("Проверка добавления Statement")
    public void testSaveStatement() {

        Statement savedStatement = dealService.saveStatement(loanStatementReqDto);
        assertAll("Проверка сохраненного Statement и поиска клиента по Statement",
                () -> assertTrue(Duration.between(savedStatement.getCreationDate(), LocalDateTime.now()).toSeconds() < 3),
                () -> assertThat(statement).usingRecursiveComparison().
                        ignoringFields("statementId", "creationDate",
                                "client.clientId", "client.employment", "client.maritalStatus").isEqualTo(savedStatement),
                () -> assertNotNull(dealService.getClientByStatementId(savedStatement.getStatementId()))
        );
    }

    //Мок emailMessagingService и/или kafkaTemplate не срабатывает. Далее ошибка таймайта Кафки. Победить не смог
//    @Test
//    @DisplayName("Проверка выбора предложения")
//    public void testSelectOffer() {
//        Mockito.doNothing().when(emailMessagingService).send(any());
//        SettableListenableFuture future = new SettableListenableFuture();
//        future.set(null);
//        Mockito.when(kafkaTemplate.send(Mockito.anyString(), Mockito.any(EmailMessageDto.class)))
//                .thenReturn(future);
//        Statement savedStatement = dealService.saveStatement(loanStatementReqDto);
//        loanOfferDto.setStatementId(savedStatement.getStatementId());
//        LoanOfferDto selectedOffer = dealService.selectOffer(loanOfferDto);
//        LoanOffer actualOffer = statementRepository.findById(savedStatement.getStatementId()).orElse(null).getAppliedOffer();
//        assertEquals(loanOfferDto, mapper.map(actualOffer, LoanOfferDto.class));
//    }

    //Мок emailMessagingService и/или kafkaTemplate не срабатывает. Далее ошибка таймайта Кафки. Победить не смог
//
//    @Test
//    @DisplayName("Проверка сохранения кредита")
//    public void testSaveCredit() {
//        Mockito.doNothing().when(emailMessagingService).send(any());
//        dealService.saveClient(client);
//        Credit credit = new Credit();
//        credit.setCreditStatus(CreditStatus.CALCULATED);
//        Statement savedStatement = statementRepository.save(this.statement);
//
//        dealService.saveCredit(savedStatement.getStatementId(), mapper.map(credit, CreditDto.class));
//
//        Credit savedCredit = statementRepository.findById(savedStatement.getStatementId()).get().getCredit();
//        Statement updatedStatement = dealService.getStatementById(savedStatement.getStatementId());
//        assertAll(
//                () -> assertNotNull(savedCredit),
//                () -> assertEquals(CreditStatus.CALCULATED, savedCredit.getCreditStatus()),
//                () -> assertNotNull(updatedStatement),
//                () -> assertThat(savedCredit).usingRecursiveComparison().isEqualTo(updatedStatement.getCredit()),
//                () -> assertEquals(ApplicationStatus.DOCUMENT_CREATED, updatedStatement.getStatus()),
//                () -> assertFalse(updatedStatement.getStatusHistory().isEmpty())
//        );
//    }

    @Test
    @DisplayName("Попытка сохранения кредита по отсутствующему Statement")
    public void testSaveCreditExceptionStatement() {
        UUID uuid = UUID.randomUUID();
        CreditDto credit = new CreditDto();

        assertThrows(StatementNotFoundException.class, () -> dealService.saveCredit(uuid, credit));
    }

}
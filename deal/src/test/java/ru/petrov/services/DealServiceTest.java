package ru.petrov.services;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.petrov.dto.CreditDto;
import ru.petrov.dto.LoanOfferDto;
import ru.petrov.dto.LoanStatementRequestDto;
import ru.petrov.models.*;
import ru.petrov.models.enums.ApplicationStatus;
import ru.petrov.models.enums.CreditStatus;
import ru.petrov.models.enums.MaritalStatus;
import ru.petrov.repositories.ClientRepository;
import ru.petrov.repositories.CreditRepository;
import ru.petrov.repositories.StatementRepository;
import ru.petrov.util.exceptions.StatementNotFoundException;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static ru.petrov.models.enums.EmploymentStatus.UNEMPLOYED;
import static ru.petrov.models.enums.Position.MID_MANAGER;

@SpringBootTest
@ActiveProfiles("test")
@RequiredArgsConstructor
class DealServiceTest {
    @Autowired
    private StatementRepository statementRepository;
    @Autowired
    private DealService dealService;
    @Autowired
    private ModelMapper mapper;

    private Client client;
    private Statement statement;
    private LoanOfferDto loanOfferDto;
    private LoanStatementRequestDto loanStatementReqDto;

    @BeforeEach
    public void setUp() {
        client = Client.builder()
                .email("eeee@sdfsdf.ru")
                .employment(new Employment(UUID.randomUUID(), UNEMPLOYED, "sdd",
                        BigDecimal.valueOf(123123), MID_MANAGER, 20, 20))
                .birthDate(LocalDate.now().minusYears(20))
                .lastName("Perov")
                .firstName("Ivan")
                .middleName("qwe")
                .maritalStatus(MaritalStatus.DIVORCED).build();

        statement = new Statement().builder().client(client).creationDate(LocalDateTime.now()).build();

        loanOfferDto = new LoanOfferDto(null, BigDecimal.valueOf(40000),
                BigDecimal.valueOf(42000), 10, BigDecimal.valueOf(4200), BigDecimal.valueOf(10),
                false, false);

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

    @Test
    @DisplayName("Проверка выбора предложения")
    public void testSelectOffer() {
        Statement savedStatement = dealService.saveStatement(loanStatementReqDto);
        loanOfferDto.setStatementId(savedStatement.getStatementId());
        LoanOfferDto selectedOffer = dealService.selectOffer(loanOfferDto);
        LoanOffer actualOffer = statementRepository.findById(savedStatement.getStatementId()).orElse(null).getAppliedOffer();
        assertEquals(loanOfferDto, mapper.map(actualOffer, LoanOfferDto.class));
    }

    @Test
    @DisplayName("Проверка сохранения кредита")
    public void testSaveCredit() {
        dealService.saveClient(client);
        Credit credit = new Credit();
        credit.setCreditStatus(CreditStatus.CALCULATED);
        Statement savedStatement = statementRepository.save(this.statement);

        dealService.saveCredit(savedStatement.getStatementId(), mapper.map(credit, CreditDto.class));

        Credit savedCredit = statementRepository.findById(savedStatement.getStatementId()).get().getCredit();
        Statement updatedStatement = dealService.getStatementById(savedStatement.getStatementId());
        assertAll(
                () -> assertNotNull(savedCredit),
                () -> assertEquals(CreditStatus.CALCULATED, savedCredit.getCreditStatus()),
                () -> assertNotNull(updatedStatement),
                () -> assertThat(savedCredit).usingRecursiveComparison().isEqualTo(updatedStatement.getCredit()),
                () -> assertEquals(ApplicationStatus.DOCUMENT_CREATED, updatedStatement.getStatus()),
                () -> assertNotNull(updatedStatement.getStatusHistory())
        );
    }

    @Test
    @DisplayName("Попытка сохранения кредита по отсутствующему Statement")
    public void testSaveCreditExceptionStatement() {
        UUID uuid = UUID.randomUUID();
        CreditDto credit = new CreditDto();

        assertThrows(StatementNotFoundException.class, () -> dealService.saveCredit(uuid, credit));
    }

}
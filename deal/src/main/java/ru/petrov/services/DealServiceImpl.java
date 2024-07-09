package ru.petrov.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.petrov.dto.*;
import ru.petrov.models.*;
import ru.petrov.models.enums.ApplicationStatus;
import ru.petrov.models.enums.ChangeType;
import ru.petrov.models.enums.CreditStatus;
import ru.petrov.repositories.ClientRepository;
import ru.petrov.repositories.CreditRepository;
import ru.petrov.repositories.StatementRepository;
import ru.petrov.util.exceptions.ClientNotFoundException;
import ru.petrov.util.exceptions.OfferNotFoundException;
import ru.petrov.util.exceptions.StatementNotFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

import static ru.petrov.models.enums.ApplicationStatus.*;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class DealServiceImpl implements DealService {
    private final ClientRepository clientRepository;
    private final StatementRepository statementRepository;
    private final CreditRepository creditRepository;
    private final ModelMapper mapper;
    private final MessagingService messagingService;


    @Transactional
    public Client saveClient(Client client) {
        Client saved = clientRepository.save(client);
        log.info("Client {} was saved}", saved);
        return saved;
    }

    @Transactional
    public Statement saveStatement(LoanStatementRequestDto requestDto) {
        Statement statement = statementRepository.save(new Statement().builder()
                .client(saveClient(mapper.map(requestDto, Client.class)))
                .creationDate(LocalDateTime.now())
                .build());
        log.info("Statement {} was saved}", statement);
        return statement;
    }

    @Transactional
    public LoanOfferDto selectOffer(LoanOfferDto loanOfferDto){

        Statement statement = getStatementById(loanOfferDto.getStatementId());
        log.info("LoanOffer {} was select for Statement {}}", loanOfferDto, statement);

        statement.setAppliedOffer(mapper.map(loanOfferDto, LoanOffer.class));
        statement.setStatus(APPROVED);
        statement.setStatusHistory(new ArrayList<>());
        statement.getStatusHistory().add(new StatusHistory("LoanOffer " + loanOfferDto + "was select",
                LocalDateTime.now(), ChangeType.AUTOMATIC));
        statementRepository.save(statement);
        log.info("Statement {} was saved}", statement);

        sendEmail(statement, Theme.FINISH_REGISTRATION);

        return loanOfferDto;
    }

    public Statement getStatementById(UUID uuid) {
        return statementRepository.findById(uuid).orElseThrow(StatementNotFoundException::new);
    }

    public Client getClientByStatementId(UUID uuid) {
        Client client = getStatementById(uuid).getClient();
        if (client == null) {
            throw new ClientNotFoundException();
        }
        return client;
    }

    @Transactional
    public Client fillClientInStatementAdditionalData(UUID statementUuid, FinishRegistrationRequestDto finishRequest) {
        Client additionalDataClient = mapper.map(finishRequest, Client.class);
        Client client = getClientByStatementId(statementUuid);
        mapper.map(additionalDataClient, client);
        return clientRepository.save(client);
    }

    public LoanOffer getOfferByStatementId(UUID uuid) {
        LoanOffer loanOffer = getStatementById(uuid).getAppliedOffer();
        if (loanOffer == null) {
            throw new OfferNotFoundException();
        }
        return loanOffer;
    }

    @Transactional
    public void saveCredit(UUID statementUuid, CreditDto creditDto) {
        Credit credit = mapper.map(creditDto, Credit.class);
        credit.setCreditStatus(CreditStatus.CALCULATED);
        Credit saveCredit = creditRepository.save(credit);
        log.info("Credit {} was saved}", saveCredit);

        Statement statement = getStatementById(statementUuid);
        statement.setCredit(saveCredit);
        updateStatementStatus(statementUuid, CC_APPROVED);
        statementRepository.save(statement);
        log.info("Statement {} was saved}", statement);
        sendEmail(statement, Theme.CREATE_DOCUMENTS);
    }

    public ScoringDataDto finishCalculationLoan(UUID uuid, FinishRegistrationRequestDto finishRequest) {
        Client client = fillClientInStatementAdditionalData(uuid, finishRequest);
        ScoringDataDto scoringDataDto = mapper.map(client, ScoringDataDto.class);
        LoanOffer appliedOffer = getOfferByStatementId(uuid);
        mapper.map(appliedOffer,scoringDataDto);
        return scoringDataDto;
    }

    @Transactional
    public void sendDocument(UUID statementUuid) {
        Statement statementById = getStatementById(statementUuid);
        updateStatementStatus(statementUuid, PREPARE_DOCUMENTS);
        sendEmail(statementById, Theme.SEND_DOCUMENTS);
    }

    @Transactional
    public void signDocument(UUID statementUuid) {
        Statement statement = getStatementById(statementUuid);
        String sesCode = "some ses";
        statement.setSesCode(sesCode);
        statementRepository.save(statement);
        log.info("Statement {} set sesCode {} ", statement, sesCode);
        sendEmail(statement, Theme.SEND_SES);
    }

    @Transactional
    public void codeDocument(UUID statementUuid) {
        Statement statement = getStatementById(statementUuid);
        updateStatementStatus(statementUuid, DOCUMENT_SIGNED);
        updateStatementStatus(statementUuid, CREDIT_ISSUED);
        sendEmail(statement, Theme.CREDIT_ISSUED);
    }

    @Transactional
    @Override
    public Statement updateStatementStatus(UUID statementUuid, ApplicationStatus newStatus) {
        //todo перед обновлением было бы не плохо проверять текущий статус
        // (Например нельзя устанавливать DOCUMENT_CREATED если текущий статус не PREPARE_DOCUMENTS)
        Statement statement = getStatementById(statementUuid);
        statement.setStatus(newStatus);
        if (statement.getStatusHistory()==null){
            statement.setStatusHistory(new ArrayList<>());
        }
        statement.getStatusHistory().add(new StatusHistory("new status: " + newStatus,
                LocalDateTime.now(), ChangeType.AUTOMATIC));
        statementRepository.save(statement);
        log.info("Statement {} status set {}", statement, newStatus);
        return statement;
    }

    public void sendEmail(Statement statement, Theme status) {
        EmailMessageDto messageDto = new EmailMessageDto(statement.getClient().getEmail(),
                status,
                statement.getStatementId());
        messagingService.send(messageDto);
        log.info("EmailMessage {} sent", messageDto);
    }
}
package ru.petrov.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
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

import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class DealServiceImpl implements DealService {
    private final ClientRepository clientRepository;
    private final StatementRepository statementRepository;
    private final CreditRepository creditRepository;
    private final ModelMapper mapper;


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
        statement.setStatus(ApplicationStatus.APPROVED);
        statement.getStatusHistory().add(new StatusHistory("LoanOffer " + loanOfferDto + "was select",
                LocalDateTime.now(), ChangeType.AUTOMATIC));
        statementRepository.save(statement);
        log.info("Statement {} was saved}", statement);
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
        statement.setStatus(ApplicationStatus.DOCUMENT_CREATED);
        statement.getStatusHistory().add(new StatusHistory("Credit " + saveCredit + "was calculated",
                LocalDateTime.now(), ChangeType.AUTOMATIC));
        statementRepository.save(statement);
        log.info("Statement {} was saved}", statement);
    }

    public ScoringDataDto finishCalculationLoan(UUID uuid, FinishRegistrationRequestDto finishRequest) {
        Client client = fillClientInStatementAdditionalData(uuid, finishRequest);
        ScoringDataDto scoringDataDto = mapper.map(client, ScoringDataDto.class);
        LoanOffer appliedOffer = getOfferByStatementId(uuid);
        mapper.map(appliedOffer,scoringDataDto);
        return scoringDataDto;
    }
}
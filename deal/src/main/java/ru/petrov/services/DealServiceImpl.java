package ru.petrov.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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

    @Transactional
    public Client saveClient(Client client) {
        Client saved = clientRepository.save(client);
        log.info("Client {} was saved}", saved);
        return saved;
    }
    @Transactional
    public Statement saveStatement(Client client) {
        Statement statement = statementRepository.save(new Statement().builder()
                .client(client)
                .creationDate(LocalDateTime.now())
                .build());
        log.info("Statement {} was saved}", statement);
        return statement;
    }

    @Transactional
    public LoanOffer selectOffer(LoanOffer loanOffer){
        Statement statement = getStatementById(loanOffer.getStatementId());
        log.info("LoanOffer {} was select for Statement {}}", loanOffer, statement);

        StatusHistory statusHistory = new StatusHistory("LoanOffer " + loanOffer + "was select",
                LocalDateTime.now(), ChangeType.AUTOMATIC);
        statement.setAppliedOffer(loanOffer);
        statement.setStatus(ApplicationStatus.APPROVED);
        statement.setStatusHistory(statusHistory);
        statementRepository.save(statement);
        log.info("Statement {} was saved}", statement);
        return loanOffer;
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
    public Client fillClientInStatementAdditionalData(UUID statementUuid, Client additionalDataClient) {
        Client client = getClientByStatementId(statementUuid);
        fillClientAdditionalData(additionalDataClient, client);
        return clientRepository.save(client);
    }

    private void fillClientAdditionalData(Client sourceClient, Client destinationClient) {
        destinationClient.setGender(sourceClient.getGender());
        destinationClient.setMaritalStatus(sourceClient.getMaritalStatus());
        destinationClient.setDependentAmount(sourceClient.getDependentAmount());
        destinationClient.getPassport().setIssueDate(sourceClient.getPassport().getIssueDate());
        destinationClient.getPassport().setIssueBranch(sourceClient.getPassport().getIssueBranch());
        destinationClient.setEmployment(sourceClient.getEmployment());
        destinationClient.setAccountNumber(sourceClient.getAccountNumber());
    }

    public LoanOffer getOfferByStatementId(UUID uuid) {
        LoanOffer loanOffer = getStatementById(uuid).getAppliedOffer();
        if (loanOffer == null) {
            throw new OfferNotFoundException();
        }
        return loanOffer;
    }

    @Transactional
    public void saveCredit(UUID statementUuid, Credit credit) {
        credit.setCreditStatus(CreditStatus.CALCULATED);
        Credit saveCredit = creditRepository.save(credit);
        log.info("Credit {} was saved}", saveCredit);

        Statement statement = getStatementById(statementUuid);
        statement.setCredit(saveCredit);
        statement.setStatus(ApplicationStatus.DOCUMENT_CREATED);
        statement.setStatusHistory(new StatusHistory("Credit " + saveCredit + "was calculated",
                LocalDateTime.now(), ChangeType.AUTOMATIC));
        statementRepository.save(statement);
        log.info("Statement {} was saved}", statement);
    }
}
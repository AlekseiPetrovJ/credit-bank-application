package ru.petrov.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.petrov.models.Client;
import ru.petrov.models.LoanOffer;
import ru.petrov.models.Statement;
import ru.petrov.models.StatusHistory;
import ru.petrov.models.enums.ApplicationStatus;
import ru.petrov.models.enums.ChangeType;
import ru.petrov.repositories.ClientRepository;
import ru.petrov.repositories.StatementRepository;
import ru.petrov.util.exceptions.StatementNotFoundException;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class DealService {
    private final ClientRepository clientRepository;
    private final StatementRepository statementRepository;

    public Client saveClient(Client client) {
        Client saved = clientRepository.save(client);
        log.info("Client {} was saved}", saved);
        return saved;
    }

    public Statement createStatement(Client client) {
        Statement statement = statementRepository.save(new Statement().builder()
                .client(client)
                .creationDate(LocalDateTime.now())
                .build());
        log.info("Statement {} was saved}", statement);
        return statement;
    }

    public LoanOffer selectOffer(LoanOffer loanOffer){
        Optional<Statement> foundStatement = statementRepository.findById(loanOffer.getStatementId());
        log.info("LoanOffer {} was select for Statement {}}", loanOffer, foundStatement);
        Statement statement = foundStatement.orElseThrow(StatementNotFoundException::new);

        StatusHistory statusHistory = new StatusHistory("LoanOffer " + loanOffer + "was select",
                LocalDateTime.now(), ChangeType.AUTOMATIC);
        statement.setAppliedOffer(loanOffer);
        statement.setStatus(ApplicationStatus.CC_APPROVED);
        statement.setStatusHistory(statusHistory);
        statementRepository.save(statement);
        return loanOffer;
    }
}
package ru.petrov.services;

import ru.petrov.models.Client;
import ru.petrov.models.Credit;
import ru.petrov.models.LoanOffer;
import ru.petrov.models.Statement;

import java.util.UUID;

public interface DealService {
    Client saveClient(Client client);
    Statement saveStatement(Client client);
    LoanOffer selectOffer(LoanOffer loanOffer);
    Statement getStatementById(UUID uuid);
    void saveCredit(UUID statementUuid, Credit credit);
    Client getClientByStatementId(UUID uuid);
}

package ru.petrov.services;

import ru.petrov.dto.*;
import ru.petrov.models.Client;
import ru.petrov.models.Statement;
import ru.petrov.models.enums.ApplicationStatus;

import java.util.UUID;

public interface DealService {
    Client saveClient(Client client);
    Statement saveStatement(LoanStatementRequestDto requestDto);
    LoanOfferDto selectOffer(LoanOfferDto loanOffer);
    Statement getStatementById(UUID uuid);
    void saveCredit(UUID statementUuid, CreditDto credit);
    Client getClientByStatementId(UUID uuid);
    ScoringDataDto finishCalculationLoan(UUID uuid, FinishRegistrationRequestDto finishRequest);
    void sendDocument(UUID statementUuid);
    void signDocument(UUID statementUuid);
    void codeDocument(UUID statementUuid);
    Statement updateStatementStatus(UUID statementUuid, ApplicationStatus newStatus);
    void sendEmail(Statement statement, Theme status);
}

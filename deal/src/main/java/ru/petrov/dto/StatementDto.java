package ru.petrov.dto;

import lombok.Data;
import ru.petrov.models.Client;
import ru.petrov.models.Credit;
import ru.petrov.models.LoanOffer;
import ru.petrov.models.StatusHistory;
import ru.petrov.models.enums.ApplicationStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class StatementDto {
    private UUID statementId;
    private Client client;
    private Credit credit;
    private ApplicationStatus status;
    private LocalDateTime creationDate;
    private LoanOffer appliedOffer;
    private LocalDateTime signDate;
    private String sesCode;
    private List<StatusHistory> statusHistory = new ArrayList<>();
}
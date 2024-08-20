package ru.petrov.services;

import org.springframework.http.ResponseEntity;
import ru.petrov.dto.LoanOfferDto;
import ru.petrov.dto.LoanStatementRequestDto;

import java.util.List;

public interface StatementService {
    ResponseEntity<List<LoanOfferDto>> exchangeLoanStatementToOffers(LoanStatementRequestDto requestDto);
    ResponseEntity selectOffer(LoanOfferDto loanOfferDto);
}
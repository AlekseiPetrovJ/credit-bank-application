package ru.petrov.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.petrov.models.Client;
import ru.petrov.models.Statement;
import ru.petrov.repositories.ClientRepository;
import ru.petrov.repositories.StatementRepository;

import java.time.LocalDateTime;

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
}
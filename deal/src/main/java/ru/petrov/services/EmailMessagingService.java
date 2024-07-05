package ru.petrov.services;

import ru.petrov.dto.EmailMessageDto;

public interface EmailMessagingService {
    void send (EmailMessageDto messageDto);
}
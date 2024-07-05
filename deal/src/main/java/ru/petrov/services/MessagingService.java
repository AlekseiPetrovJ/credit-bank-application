package ru.petrov.services;

import ru.petrov.dto.EmailMessageDto;

public interface MessagingService {
    void send (EmailMessageDto messageDto);
}
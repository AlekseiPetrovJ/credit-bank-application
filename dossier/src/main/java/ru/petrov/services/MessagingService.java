package ru.petrov.services;

import ru.petrov.dto.EmailMessageDto;

public interface MessagingService {
    void handle(EmailMessageDto emailMessageDto);
    void handleWithPutNotification(EmailMessageDto emailMessageDto);
}
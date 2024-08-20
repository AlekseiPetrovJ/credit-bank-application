package ru.petrov.services;

import ru.petrov.dto.EmailMessageDto;

public interface EmailService {
    void sendSimpleEmail(EmailMessageDto emailMessageDto);
}
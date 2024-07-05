package ru.petrov.services;

import ru.petrov.dto.EmailMessageDto;

public interface EmailService {
    public void sendSimpleEmail(EmailMessageDto emailMessageDto);
}
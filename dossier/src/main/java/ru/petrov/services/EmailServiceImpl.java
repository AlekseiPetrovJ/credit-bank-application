package ru.petrov.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.petrov.dto.EmailMessageDto;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    public JavaMailSender emailSender;

    @Override
    public void sendSimpleEmail(EmailMessageDto emailMessageDto) {

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(emailMessageDto.getAddress());
        simpleMailMessage.setSubject(emailMessageDto.getTheme().getTitle());
        simpleMailMessage.setText("По заявлению " + emailMessageDto.getStatementId() +
                " принято решение "  + emailMessageDto.getTheme().getTitle());
        emailSender.send(simpleMailMessage);
    }
}
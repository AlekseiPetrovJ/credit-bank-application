package ru.petrov.services;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.petrov.dto.EmailMessageDto;

@Service
@RequiredArgsConstructor
public class KafkaEmailMessagingServiceImpl implements EmailMessagingService{
    private final KafkaTemplate<String, EmailMessageDto> kafkaTemplate;

    @Override
    public void send(EmailMessageDto messageDto) {
        kafkaTemplate.send(messageDto.getTheme().name(), messageDto);
    }
}


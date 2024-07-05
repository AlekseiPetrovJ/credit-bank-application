package ru.petrov.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.petrov.config.CommonProps;
import ru.petrov.dto.EmailMessageDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaMessagingServiceImpl implements MessagingService {
    private final EmailService emailService;
    private final RestTemplate restTemplate;
    private final CommonProps commonProps;

    @KafkaListener(
            topics = {"FINISH_REGISTRATION",
                    "CREATE_DOCUMENTS",
                    "SEND_SES",
                    "CREDIT_ISSUED",
                    "STATEMENT_DENIED"})
    public void handle(EmailMessageDto message) {
        log.info("get message {} from kafka", message);
        emailService.sendSimpleEmail(message);
    }

    @KafkaListener(
            topics = "SEND_DOCUMENTS")
    public void handleWithPutNotification(EmailMessageDto message) {
        String fullDealUrl = commonProps.getDealUrl() + "/deal/admin/statement/"
                + message.getStatementId() + "/status";

        log.info("get message {} from kafka", message);
        restTemplate.put(fullDealUrl, "");
        emailService.sendSimpleEmail(message);
    }
}

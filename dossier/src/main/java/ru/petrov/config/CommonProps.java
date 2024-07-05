package ru.petrov.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
@ConfigurationProperties(prefix="dossier.properties")
@Data
public class CommonProps {
    private String dealUrl = "http://127.0.0.1:8081";
}

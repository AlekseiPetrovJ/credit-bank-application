package ru.petrov.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix="gateway.properties")
@Data
public class CommonProps {
    private String dealUrl = "http://127.0.0.1:8081";
    private String statementUrl = "http://127.0.0.1:8082";
}
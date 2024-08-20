package ru.petrov.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix="gateway")
@Data
public class CommonProps {
    private String dealUrl = "http://deal:8080";
    private String statementUrl = "http://statement:8080";
}
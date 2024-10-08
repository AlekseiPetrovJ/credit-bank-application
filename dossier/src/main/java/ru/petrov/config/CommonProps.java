package ru.petrov.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix="dossier")
@Data
public class CommonProps {
    private String dealUrl = "http://deal:8080";
}

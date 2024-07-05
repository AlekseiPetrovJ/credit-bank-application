package ru.petrov.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class EmailMessageDto {
    private String address;
    private Theme theme;
    private UUID statementId;
}
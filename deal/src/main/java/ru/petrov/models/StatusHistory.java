package ru.petrov.models;

import lombok.*;
import ru.petrov.models.enums.ChangeType;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class StatusHistory {
    private String status;
    private LocalDateTime time;
    private ChangeType changeType;
}
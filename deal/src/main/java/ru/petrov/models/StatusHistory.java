package ru.petrov.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.petrov.models.enums.ChangeType;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class StatusHistory {
    private String status;
    private LocalDateTime time;
    private ChangeType changeType;
}
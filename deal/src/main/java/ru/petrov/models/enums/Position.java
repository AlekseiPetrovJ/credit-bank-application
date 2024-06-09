package ru.petrov.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Position {
    WORKER, MID_MANAGER, TOP_MANAGER, OWNER
}
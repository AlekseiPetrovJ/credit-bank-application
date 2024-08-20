package ru.petrov.calculator.dto.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Getter
public enum Gender {
    MALE("мужчина", 1),
    FAMELE("женщина", 2),
    NON_BINARY("не бинарный", 3);
    private final String title;
    private final int code;

}

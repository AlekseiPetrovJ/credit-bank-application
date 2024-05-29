package ru.petrov.calculator.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Getter
public enum Gender {
    MALE("мужчина", 1),
    FAMELE("женщина", 2);
    private final String title;
    private final int code;

}

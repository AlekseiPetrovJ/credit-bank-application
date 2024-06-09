package ru.petrov.calculator.util.exception;

public class NotValidDto extends RuntimeException{
    public NotValidDto(String message) {
        super(message);
    }
}
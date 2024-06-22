package ru.petrov.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.petrov.util.exception.ErrorResponse;
import ru.petrov.util.exception.NotValidDto;

@RestControllerAdvice
public class ExceptionApiHandler {

//    @ExceptionHandler
    @ResponseStatus(code = HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseEntity<ErrorResponse> handleException(NotValidDto e) {
        ErrorResponse response = new ErrorResponse(
                e.getMessage(),
                System.currentTimeMillis()
        );
        //https://stackoverflow.com/questions/16133923/400-vs-422-response-to-post-of-data
        return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
package ru.petrov.calculator.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.petrov.calculator.util.exception.ErrorResponse;
import ru.petrov.calculator.util.exception.NotValidDto;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class ExceptionApiHandlerTest {
    @InjectMocks
    private ExceptionApiHandler exceptionHandler;

    @Test
    @DisplayName("Получение статуса UNPROCESSABLE_ENTITY при исключении")
    void testHandlerExeption(){
        NotValidDto notValid = new NotValidDto("not valid");
        ResponseEntity<ErrorResponse> responseEntity = exceptionHandler.handleException(notValid);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, responseEntity.getStatusCode());
        assertEquals("not valid", Objects.requireNonNull(responseEntity.getBody()).getMessage());
        assertTrue(System.currentTimeMillis() - responseEntity.getBody().getTimestamp()< 5000);
    }
}
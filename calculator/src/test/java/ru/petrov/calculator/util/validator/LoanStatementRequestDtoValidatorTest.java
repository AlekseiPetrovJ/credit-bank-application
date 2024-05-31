package ru.petrov.calculator.util.validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import ru.petrov.calculator.dto.LoanStatementRequestDto;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class LoanStatementRequestDtoValidatorTest {

    private final LoanStatementRequestDtoValidator validator = new LoanStatementRequestDtoValidator();

    @Test
    @DisplayName("Валидные входные данные")
    public void testValidateWithValidRequestDto() {
        LoanStatementRequestDto requestDto = new LoanStatementRequestDto(BigDecimal.valueOf(40000),
                12, "Ivanov", "Ivan", "Ivanovich", "sfd@sdf.ru",
                LocalDate.now().minusYears(20), "5555", "878989");

        Errors errors = new BeanPropertyBindingResult(requestDto, "requestDto");
        validator.validate(requestDto, errors);

        assertEquals(0, errors.getErrorCount());
    }

    @Test
    @DisplayName("Не валидные входные данные")
    public void testValidateWithUnderageRequestDto() {
        LoanStatementRequestDto requestDto = new LoanStatementRequestDto(BigDecimal.valueOf(40000),
                12, "Ivanov", "Ivan", "Ivanovich", "sfd@sdf.ru",
                LocalDate.now().minusYears(17), "5555", "878989");

        Errors errors = new BeanPropertyBindingResult(requestDto, "requestDto");
        validator.validate(requestDto, errors);

        assertEquals(1, errors.getErrorCount());
        assertEquals("should be no later than 18 years from the current date", errors.getFieldError("birthdate").getDefaultMessage());
    }
}
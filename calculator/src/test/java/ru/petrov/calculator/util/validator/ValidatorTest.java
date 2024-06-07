package ru.petrov.calculator.util.validator;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.petrov.calculator.CalculatorApplicationTests;
import ru.petrov.calculator.dto.LoanStatementRequestDto;
import ru.petrov.calculator.dto.ScoringDataDto;
import ru.petrov.calculator.util.exception.NotValidDto;

import static org.junit.jupiter.api.Assertions.*;

public class ValidatorTest extends CalculatorApplicationTests {

    @Test
    @DisplayName("Валидные входные данные")
    public void testValidateWithValidRequestDto() throws JsonProcessingException {
        LoanStatementRequestDto requestDto = getObjectMapper()
                .readValue(getStringFromFile("good_LoanStatementRequestDto_1.json"), LoanStatementRequestDto.class);
        assertDoesNotThrow(() -> Validator.validateAgeOlder18(requestDto));
    }

    @Test
    @DisplayName("Невалидные входные данные")
    public void testValidateWithUnderageRequestDto() throws JsonProcessingException {
        LoanStatementRequestDto requestDto = getObjectMapper()
                .readValue(getStringFromFile("bad_LoanStatementRequestDto_1.json"), LoanStatementRequestDto.class);
        Exception exception = assertThrows(NotValidDto.class, () -> Validator.validateAgeOlder18(requestDto));
        String expectedMessage = "age should be no later than 18 years from the current date";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    @DisplayName("Валидные входные данные")
    public void testValidateWithValidScoringDataDto() throws JsonProcessingException {
        ScoringDataDto scoringDataDto = getObjectMapper().readValue(getStringFromFile("bad_ScoringDataDto_1.json"),
                ScoringDataDto.class);
        assertDoesNotThrow(() -> Validator.validateAgeOlder18(scoringDataDto));
    }
}
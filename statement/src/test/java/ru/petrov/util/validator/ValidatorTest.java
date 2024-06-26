package ru.petrov.util.validator;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.petrov.StatementApplicationTest;
import ru.petrov.dto.LoanStatementRequestDto;
import ru.petrov.util.exception.NotValidDto;

import static org.junit.jupiter.api.Assertions.*;

class ValidatorTest extends StatementApplicationTest {

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
}
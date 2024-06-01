package ru.petrov.calculator.util.validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.petrov.calculator.dto.EmploymentDto;
import ru.petrov.calculator.dto.LoanStatementRequestDto;
import ru.petrov.calculator.dto.ScoringDataDto;
import ru.petrov.calculator.dto.enums.EmploymentStatus;
import ru.petrov.calculator.dto.enums.Gender;
import ru.petrov.calculator.dto.enums.MaritalStatus;
import ru.petrov.calculator.dto.enums.Position;
import ru.petrov.calculator.util.exception.NotValidDto;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ValidatorTest {

    @Test
    @DisplayName("Валидные входные данные")
    public void testValidateWithValidRequestDto() {
        //todo через mapper подтягивать из json
        LoanStatementRequestDto requestDto = new LoanStatementRequestDto(BigDecimal.valueOf(40000),
                12, "Ivanov", "Ivan", "Ivanovich", "sfd@sdf.ru",
                LocalDate.now().minusYears(20), "5555", "878989");
        assertDoesNotThrow(() -> Validator.validateAgeOlder18(requestDto));
    }

    @Test
    @DisplayName("Невалидные входные данные")
    public void testValidateWithUnderageRequestDto() {
        LoanStatementRequestDto requestDto = new LoanStatementRequestDto(BigDecimal.valueOf(40000),
                12, "Ivanov", "Ivan", "Ivanovich", "sfd@sdf.ru",
                LocalDate.now().minusYears(17), "5555", "878989");
        Exception exception = assertThrows(NotValidDto.class, () -> Validator.validateAgeOlder18(requestDto));
        String expectedMessage = "age should be no later than 18 years from the current date";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    @DisplayName("Валидные входные данные")
    public void testValidateWithValidScoringDataDto() {
        EmploymentDto employmentDto = new EmploymentDto(EmploymentStatus.BUSINESS_OWNER, "22222", BigDecimal.valueOf(500000), Position.MIDDLE_MANAGER, 20, 10);

        ScoringDataDto scoringDataDto = new ScoringDataDto(BigDecimal.valueOf(35000),
                12, "Ivanov", "Ivan", "Ivanovich",
                Gender.MALE, LocalDate.now().minusYears(20), "7007",
                "111222", LocalDate.of(2018, 5, 30), "TTT",
                MaritalStatus.DIVORCED, 0, employmentDto, "555", false, false);
        assertDoesNotThrow(() -> Validator.validateAgeOlder18(scoringDataDto));
    }
}
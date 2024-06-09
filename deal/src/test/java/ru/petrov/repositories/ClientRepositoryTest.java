package ru.petrov.repositories;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.petrov.models.Client;
import ru.petrov.models.Employment;
import ru.petrov.models.Passport;
import ru.petrov.models.enums.EmploymentStatus;
import ru.petrov.models.enums.Gender;
import ru.petrov.models.enums.MaritalStatus;
import ru.petrov.models.enums.Position;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

//@Profile("development")
//@DataJpaTest
@SpringBootTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ClientRepositoryTest {
    private final ClientRepository clientRepository;

    @Autowired
    public ClientRepositoryTest(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }


    @Test
    public void test() {
        Passport passport = new Passport().builder().series("AAAA").issueBranch("QQQQQQ")
                .issueDate(LocalDate.now().minusYears(10)).build();
        Employment employment = new Employment(UUID.randomUUID(), EmploymentStatus.EMPLOYED, "sdf", BigDecimal.TEN,
                Position.WORKER, 123, 123);

        Client client = new Client().builder()
                .lastName("qw")
                .firstName("ss")
                .middleName("sdf")
                .birthDate(LocalDate.now().minusYears(20))
                .email("sdfsf@sdfdf.ru")
                .gender(Gender.MALE)
                .maritalStatus(MaritalStatus.SINGLE)
                .dependentAmount(0)
                .passport(passport)
                .accountNumber("sfsfwef")
                .employment(employment)
                .accountNumber("sdfsfddd").build();
        Client save = clientRepository.save(client);
        System.out.println(save);

        try {
            System.out.println("enter ");
            Thread.sleep(50000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertEquals(2, 2);

    }


}

package ru.petrov.models;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import ru.petrov.models.enums.Gender;
import ru.petrov.models.enums.MaritalStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "client")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "client_id", nullable = false)
    private UUID clientId;

    @NotNull
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotNull
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotNull
    @Column(name = "middle_name", nullable = false)
    private String middleName;

    @NotNull
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "marital_status", nullable = false)
    private MaritalStatus maritalStatus;

    @Column(name = "dependent_amount", nullable = false)
    private Integer dependentAmount;

    @Type(type = "jsonb")
    @Column(name = "passport", columnDefinition = "jsonb")
    private Passport passport;

    @Type(type = "jsonb")
    @Column(name = "employment", columnDefinition = "jsonb")
    private Employment employment;

    @Column(name = "account_number", nullable = false)
    private String accountNumber;
}
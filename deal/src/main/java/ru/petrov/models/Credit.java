package ru.petrov.models;

import lombok.*;
import org.hibernate.annotations.Type;
import ru.petrov.models.enums.CreditStatus;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Credit {
    @Id
    private UUID clientId;
    private Integer term;
    private BigDecimal monthlyPayment;
    private BigDecimal rate;
    private BigDecimal psk;

    @Type(type = "jsonb")
    @Column(name = "payment_schedule", columnDefinition = "jsonb")
    private String paymentSchedule;
    private Boolean insuranceEnabled;
    private Boolean salaryClient;
    private CreditStatus creditStatus;

}
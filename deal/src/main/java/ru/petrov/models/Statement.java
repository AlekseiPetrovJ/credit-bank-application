package ru.petrov.models;

import lombok.*;
import org.hibernate.annotations.Type;
import ru.petrov.models.enums.ApplicationStatus;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Statement {
    @Id
    private UUID statementId;
    private Client client;
    private Credit credit;
    private ApplicationStatus status;
    private LocalDateTime creationDate;

    @Type(type = "jsonb")
    @Column(name = "applied_offer", columnDefinition = "jsonb")
    private String appliedOffer;

    private LocalDateTime signDate;

    @Type(type = "jsonb")
    @Column(name = "ses_code", columnDefinition = "jsonb")
    private String sesCode;

    @Type(type = "jsonb")
    @Column(name = "status_history", columnDefinition = "jsonb")
    private StatusHistory statusHistory;
}
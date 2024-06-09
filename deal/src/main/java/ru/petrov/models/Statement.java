package ru.petrov.models;

import lombok.*;
import org.hibernate.annotations.Type;
import ru.petrov.models.enums.ApplicationStatus;

import javax.persistence.*;
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
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "statement_id", nullable = false)
    private UUID statementId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id")
    private Client client;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "credit_id")
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
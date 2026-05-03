package com.jee.app.model;
import com.jee.app.enums.ParticipationStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;



@Entity
@Table(name="participations",
         uniqueConstraints = {
            @UniqueConstraint(columnNames = {"user_id","action_id"})
         })

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Participation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private ParticipationStatus status = ParticipationStatus.REGISTERED;
    private LocalDateTime registrationDate;
    private String cancellationReason;
    private boolean attended = false;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne
    @JoinColumn(name = "action_id")
    private CharityAction action;

    @PrePersist
    public void prePersist(){
        registrationDate = LocalDateTime.now();
    }
}

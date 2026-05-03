package com.jee.app.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "action_views",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"action_id", "user_id"})
       })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ActionView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "action_id")
    private CharityAction action;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    private LocalDateTime viewedAt;

    @PrePersist
    public void prePersist() {
        viewedAt = LocalDateTime.now();
    }
}
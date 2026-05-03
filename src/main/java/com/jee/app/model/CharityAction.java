package com.jee.app.model;

import java.time.LocalDateTime;
import java.util.List;

import com.jee.app.enums.ActionStatus;
import com.jee.app.enums.Category;

import jakarta.annotation.Generated;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="charity_actions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CharityAction {
    @Id
@GeneratedValue(strategy = GenerationType.SEQUENCE,
                generator = "charity_action_seq")
@SequenceGenerator(
    name = "charity_action_seq",
    sequenceName = "charity_actions_id_seq",
    allocationSize = 1
)   
 private Long id;
    @Column(nullable=false)
    private String title;
    @Column(length=2000)
    private String description;

    private String shortDescription;
    @Enumerated (EnumType.STRING)
    private Category category;

    private String location;
    
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Double targetAmount;

    @Column(columnDefinition = "double precision default 0.0")
    private Double currentAmount = 0.0;
    private String currency = "MAD";
    @Enumerated(EnumType.STRING)
    private ActionStatus status = ActionStatus.DRAFT;
    private int viewCount = 0;
    private int donationCount = 0;
    private int participantCount = 0;
    private String featuredImage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;
    @ManyToOne
    @JoinColumn(name = "organisation_id")
    private Organisation organisation;
    @OneToMany(mappedBy = "action", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ActionView> actionViews;
    @OneToMany(mappedBy = "action", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Donation> donations;
    @OneToMany(mappedBy = "action", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Participation> participations;
    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public double getProgressPercentage() {
        if (targetAmount == null || targetAmount == 0) return 0;
        if(currentAmount == null) return 0;
        return Math.min((currentAmount / targetAmount) * 100, 100);
    }
    


    
}

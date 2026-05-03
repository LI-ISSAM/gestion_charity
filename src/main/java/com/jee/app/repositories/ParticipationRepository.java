package com.jee.app.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jee.app.model.CharityAction;
import com.jee.app.model.Participation;
import com.jee.app.model.Users;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    boolean existsByUserAndAction(Users user, CharityAction action);
    Optional<Participation> findByUserAndAction(Users user, CharityAction action);
    List<Participation> findByUser(Users user);
    List<Participation> findByAction(CharityAction action);
    long countByAction(CharityAction action);
    
}

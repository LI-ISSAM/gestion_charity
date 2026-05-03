package com.jee.app.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jee.app.enums.ParticipationStatus;
import com.jee.app.model.CharityAction;
import com.jee.app.model.Participation;
import com.jee.app.model.Users;
import com.jee.app.repositories.CharityActionRepository;
import com.jee.app.repositories.ParticipationRepository;
import com.jee.app.services.EmailService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ParticipationService {
    private final ParticipationRepository participationRepository;  
    private final CharityActionRepository charityActionRepository;
    private final EmailService emailService;

    public void participate(Users user , CharityAction action){
        if(participationRepository.existsByUserAndAction(user, action)){
            throw new RuntimeException("Vous participez déjà à cette action");
        }

        Participation participation = Participation.builder()
                .user(user)
                .action(action)
                .status(ParticipationStatus.REGISTERED)
                .build();
        participationRepository.save(participation);

        action.setParticipantCount(action.getParticipantCount()+1);
        charityActionRepository.save(action);
        emailService.sendParticipationConfirmation(user, action);
    }

    public void cancel(Users user, CharityAction action) {
        Participation participation = participationRepository
                .findByUserAndAction(user, action)
                .orElseThrow(() -> new RuntimeException(
                        "Participation introuvable"));

        participation.setStatus(ParticipationStatus.CANCELLED);
        participationRepository.save(participation);

        action.setParticipantCount(
                Math.max(0, action.getParticipantCount() - 1));
        charityActionRepository.save(action);
    }

    public boolean isParticipating(Users user, CharityAction action) {
        return participationRepository
                .existsByUserAndAction(user, action);
    }

    public ParticipationStatus getStatus(Users user,
                                          CharityAction action) {
        return participationRepository
                .findByUserAndAction(user, action)
                .map(Participation::getStatus)
                .orElse(null);
    }

    public List<Participation> getByUser(Users user){
        return participationRepository.findByUser(user);
    }

    public List<Participation> getByAction(CharityAction action) {
        return participationRepository.findByAction(action);
    }
}

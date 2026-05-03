package com.jee.app.services;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import com.jee.app.dto.CharityActionDTO;
import com.jee.app.enums.ActionStatus;
import com.jee.app.enums.Category;
import com.jee.app.model.ActionView;
import com.jee.app.model.CharityAction;
import com.jee.app.model.Organisation;
import com.jee.app.model.Users;
import com.jee.app.repositories.ActionViewRepository;
import com.jee.app.repositories.CharityActionRepository;
import com.jee.app.repositories.UserRepository;

import jakarta.validation.constraints.Email;

import org.springframework.data.domain.Page;       
import org.springframework.data.domain.PageRequest; 
import org.springframework.data.domain.Pageable;    
import org.springframework.data.domain.Sort;        
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CharityActionService {
    private final CharityActionRepository charityActionRepository;
    private final FileUploadService fileUploadService;
    private final ActionViewRepository actionViewRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;
    public void create(CharityActionDTO dto, Organisation organisation) {
        String imageFileName = null;
        if (dto.getImageFile() != null && !dto.getImageFile().isEmpty()) {
            imageFileName = fileUploadService.saveFile(dto.getImageFile());
        }

        CharityAction action = CharityAction.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .shortDescription(dto.getShortDescription())
                .category(dto.getCategory())
                .location(dto.getLocation())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .targetAmount(dto.getTargetAmount())
                .currency(dto.getCurrency())
                .featuredImage(imageFileName)
                .status(ActionStatus.DRAFT)
                .organisation(organisation)
                .build();

        charityActionRepository.save(action);
    }

    public void update(Long id, CharityActionDTO dto) {
        CharityAction action = charityActionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Action introuvable"));

        if (dto.getImageFile() != null && !dto.getImageFile().isEmpty()) {
            fileUploadService.deleteFile(action.getFeaturedImage());
            action.setFeaturedImage(
                fileUploadService.saveFile(dto.getImageFile()));
        }

        action.setTitle(dto.getTitle());
        action.setDescription(dto.getDescription());
        action.setShortDescription(dto.getShortDescription());
        action.setCategory(dto.getCategory());
        action.setLocation(dto.getLocation());
        action.setStartDate(dto.getStartDate());
        action.setEndDate(dto.getEndDate());
        action.setTargetAmount(dto.getTargetAmount());
        action.setCurrency(dto.getCurrency());
        charityActionRepository.save(action);
    }
    
    public void publish(Long id){
        CharityAction action = charityActionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Action introuvable"));
        action.setStatus(ActionStatus.ACTIVE);
        action.setPublishedAt(LocalDateTime.now());
        charityActionRepository.save(action);
        List<Users> allUsers = userRepository.findAll();
        emailService.sendNewActionNotification(allUsers, action);
    }



   public List<CharityAction> getArchivedByOrganisation(Organisation org) {
    return charityActionRepository
            .findByOrganisationAndStatusOrderByCreatedAtDesc(
                    org, ActionStatus.ARCHIVED);
}
public void unarchive(Long id) {
    CharityAction action = charityActionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Action introuvable"));
    action.setStatus(ActionStatus.DRAFT);
    charityActionRepository.save(action);
}
      // ── Archiver une action ────────────────────────────
    public void archive(Long id) {
        CharityAction action = charityActionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Action introuvable"));
        action.setStatus(ActionStatus.ARCHIVED);
        charityActionRepository.save(action);
    }
    
    // ── Annuler une action ─────────────────────────────
    public void cancel(Long id) {
        CharityAction action = charityActionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Action introuvable"));
        action.setStatus(ActionStatus.CANCELLED);
        charityActionRepository.save(action);
    }

    // ── Supprimer une action ───────────────────────────
    public void delete(Long id) {
        CharityAction action = charityActionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Action introuvable"));
        fileUploadService.deleteFile(action.getFeaturedImage());
        charityActionRepository.delete(action);
    }

     // ── Récupérer par organisation ─────────────────────
    public List<CharityAction> getByOrganisation(Organisation org) {
        return charityActionRepository.findByOrganisation(org);
    }

    // ── Récupérer une action par ID ────────────────────
    public CharityAction getById(Long id) {
        return charityActionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Action introuvable"));
    }

    // ── Toutes les actions actives ─────────────────────
    public List<CharityAction> getActiveActions() {
        return charityActionRepository
                .findByStatusOrderByCreatedAtDesc(ActionStatus.ACTIVE);
    }
    
    // ── Actions actives d'une organisation ────────────
    public List<CharityAction> getActiveByOrganisation(Organisation org) {
    return charityActionRepository
            .findByOrganisationAndStatusOrderByCreatedAtDesc(
                    org, ActionStatus.ACTIVE);
}

// ── Incrémenter les vues ───────────────────────────
    public void incrementView(Long actionId , Users user) {
    CharityAction action = charityActionRepository.findById(actionId)
            .orElseThrow(() -> new RuntimeException("Action introuvable"));
    
    if (user == null){
        return;
    }

    boolean alreadyViewed = actionViewRepository.existsByActionAndUser(action, user);

    if(!alreadyViewed){
        ActionView view = ActionView.builder()
                .action(action)
                .user(user)
                .build();
        actionViewRepository.save(view);
        action.setViewCount(action.getViewCount() + 1);
        charityActionRepository.save(action);
    }

}

    public Long countActive(){
        return (long) charityActionRepository.findByStatusOrderByCreatedAtDesc(ActionStatus.ACTIVE).size();
    }

    public double getTotalDonations(){
        return charityActionRepository.findAll()
               .stream()
               .mapToDouble(a->a.getCurrentAmount()!=null
                    ?a.getCurrentAmount():0.0)
                .sum();
    }

    public Page<CharityAction> search(String keyword, Category category,
                                    String location, int page, int size) {

        String kw  = (keyword  != null && !keyword.trim().isEmpty())
                    ? keyword.trim() : null;
        String loc = (location != null && !location.trim().isEmpty())
                    ? location.trim() : null;

        return charityActionRepository.search(
                ActionStatus.ACTIVE,
                kw,
                category,
                loc,
                PageRequest.of(page, size, Sort.by("createdAt").descending())
        );
    }

}

package com.jee.app.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


import java.util.List;
import com.jee.app.dto.OrganisationDTO;
import com.jee.app.enums.OrganisationsStatus;
import com.jee.app.enums.Role;
import com.jee.app.model.Organisation;
import com.jee.app.model.Users;
import com.jee.app.repositories.OrganisationRepository;
import com.jee.app.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service; 

@Service
@RequiredArgsConstructor
public class OrganisationService {

    private final FileUploadService fileUploadService;
    private final OrganisationRepository organisationRepository;
    private final UserRepository userRepository;
  
    public void create(OrganisationDTO dto , Users manager){
        if(organisationRepository.existsByManagerId(manager.getId())){
            throw new RuntimeException("Vous avez deja une organisation enregistrée");
        }

        String logoFileName = null;
        if (dto.getLogoFile() != null && !dto.getLogoFile().isEmpty()) {
            logoFileName = fileUploadService.saveFile(dto.getLogoFile());
        }
        Organisation org = Organisation.builder()
                .name(dto.getName())
                .legalAddress(dto.getLegalAddress())
                .taxId(dto.getTaxId())
                .description(dto.getDescription())
                .mission(dto.getMission())
                .website(dto.getWebsite())
                .phone(dto.getPhone())
                .logo(logoFileName)
                .status(OrganisationsStatus.PENDING)
                .manager(manager)
                .build();

                organisationRepository.save(org);
    }

    public Organisation getByManager(Users manager){
        return organisationRepository.findByManagerId(manager.getId())
        .orElse(null);
    }

    public List<Organisation> getAll(){
        return organisationRepository.findAll();
    }    

    public List<Organisation> getApproved() {

    return organisationRepository
            .findByStatus(OrganisationsStatus.APPROVED);
}
   
    public List<Organisation> getPending(){
        return organisationRepository.findByStatus(OrganisationsStatus.PENDING);
    }

   public Page<Organisation> getApprovedPaged(int page, int size) {
    Pageable pageable = PageRequest.of(page, size,
            Sort.by("createdAt").descending());
    return organisationRepository
            .findByStatus(OrganisationsStatus.APPROVED, pageable);
}

    public void approve(Long id){
        Organisation org = organisationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organisation non trouvée"));
        org.setStatus(OrganisationsStatus.APPROVED);
        org.setValidatedAt(java.time.LocalDateTime.now());
        organisationRepository.save(org);

        Users manager = org.getManager();
        manager.setRole(Role.ORGANIZATION);
        userRepository.save(manager);
    }

    public void reject(Long id){
        Organisation org = organisationRepository.findById(id)
                   .orElseThrow(()->new RuntimeException("Organisation introuvable"));
        org.setStatus(OrganisationsStatus.REJECTED);
        organisationRepository.save(org);
    }

    public void update (Long id , OrganisationDTO dto){

        Organisation org = organisationRepository.findById(id)
                   .orElseThrow(()->new RuntimeException("Organisation introuvable"));
            if (dto.getLogoFile() != null && !dto.getLogoFile().isEmpty()) {
                // Supprime l'ancien logo si existe
                fileUploadService.deleteFile(org.getLogo());
                // Sauvegarde le nouveau logo
                String logoFileName = fileUploadService.saveFile(dto.getLogoFile());
                org.setLogo(logoFileName);
            }
        org.setName(dto.getName());
        org.setLegalAddress(dto.getLegalAddress());
        org.setTaxId(dto.getTaxId());
        org.setDescription(dto.getDescription());
        org.setMission(dto.getMission());
        org.setWebsite(dto.getWebsite());
        org.setPhone(dto.getPhone());
        org.setStatus(OrganisationsStatus.PENDING);
        organisationRepository.save(org);
    }

    public void delete(Long id) {
        Organisation org = organisationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Organisation introuvable"));

        Users manager = org.getManager();
        manager.setRole(Role.USER);
        userRepository.save(manager);

        fileUploadService.deleteFile(org.getLogo());

        organisationRepository.delete(org);
}
   
    public Organisation getById(Long id) {
    return organisationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Organisation introuvable"));
}

    public Long countApproved(){
        return (long) organisationRepository
            .findByStatus(OrganisationsStatus.APPROVED).size();    
    }
}
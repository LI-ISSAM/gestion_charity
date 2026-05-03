package com.jee.app.services;

import com.jee.app.model.Users;
import com.jee.app.repositories.UserRepository;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.jee.app.dto.LoginDTO;
import com.jee.app.dto.RegisterDTO;
import com.jee.app.enums.Role;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final EmailService emailService;
    private final FileUploadService fileUploadService;
    
    public UserService(UserRepository userRepository, EmailService emailService, FileUploadService fileUploadService)  {

		this.userRepository = userRepository;
        this.emailService = emailService;
        this.fileUploadService = fileUploadService; 
	}

    public void register(RegisterDTO dto){
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email déjà utilisé");
        }

        Role role = dto.getRole();
        if (role == null|| role == Role.ADMIN) {
            role = Role.USER; 
            }
          String pictureName = null;
    if (dto.getProfilePicture() != null &&
        !dto.getProfilePicture().isEmpty()) {
        pictureName = fileUploadService
                .saveFile(dto.getProfilePicture());
    }
        Users user = Users.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .motDePasse(passwordEncoder.encode(dto.getPassword()))
                .role(dto.getRole())
                .profilePicture(pictureName)
                .build();
        userRepository.save(user);
        emailService.sendWelcomeEmail(user);
    }
    
    public Users login(LoginDTO dto){
        Users user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(()-> new RuntimeException("Email introuvable"));

        if(!passwordEncoder.matches(dto.getPassword(), user.getMotDePasse())){
            throw new RuntimeException("Mot de passe incorrect");
        }
        return user;

    }
    public void updateProfilePicture(Long userId,
        MultipartFile file) {
    Users user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException(
                    "User introuvable"));

    // Supprime l'ancienne photo
    if (user.getProfilePicture() != null) {
        fileUploadService.deleteFile(
                user.getProfilePicture());
    }

    // Sauvegarde la nouvelle
    String fileName = fileUploadService.saveFile(file);
    user.setProfilePicture(fileName);
    userRepository.save(user);

    // ✅ Met à jour la session aussi
}
public Users getById(Long id) {
    return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException(
                    "User introuvable"));
}

public void removeProfilePicture(Long userId) {
    Users user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException(
                    "User introuvable"));
    if (user.getProfilePicture() != null) {
        fileUploadService.deleteFile(
                user.getProfilePicture());
        user.setProfilePicture(null);
        userRepository.save(user);
    }
}
// ✅ Ajoute cette méthode dans UserService.java
public void updateProfile(Long userId,
                           String firstName,
                           String lastName) {
    Users user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException(
                    "User introuvable"));
    user.setFirstName(firstName);
    user.setLastName(lastName);
    userRepository.save(user);
}

    public Long countAll(){
        return userRepository.count();
    }

}
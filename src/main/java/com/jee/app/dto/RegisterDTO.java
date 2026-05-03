package com.jee.app.dto;
import org.springframework.web.multipart.MultipartFile;

import com.jee.app.enums.Role;

import jakarta.validation.constraints.*;
import lombok.Data;


@Data
public class RegisterDTO {

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(min = 2, max = 50, message = "Le prénom doit avoir entre 2 et 50 caractères")
    private String firstName;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 50, message = "Le nom doit avoir entre 2 et 50 caractères")
    private String lastName;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format email invalide (exemple: nom@email.com)")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, message = "Le mot de passe doit avoir au moins 6 caractères")
    private String password;

    @NotNull(message = "Veuillez choisir un rôle")
    private Role role;
    private MultipartFile profilePicture;



   



  
}
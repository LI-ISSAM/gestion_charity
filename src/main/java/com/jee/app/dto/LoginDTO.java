package com.jee.app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

   @Data 
public class LoginDTO {
    @Email(message = "Emai invalide")
    @NotBlank(message="Email est obligatoire")
    private String email;

    @NotBlank(message="Mot de passe est obligatoire")
    private String password;

    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }

    

    
}

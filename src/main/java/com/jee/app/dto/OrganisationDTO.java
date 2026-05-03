package com.jee.app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class OrganisationDTO {

    @NotBlank(message = "Nom de l'organisation obligatoire")
    private String name;

    @NotBlank(message = "Adresse légale obligatoire")
    private String legalAddress;

    @NotBlank(message = "Numéro fiscal obligatoire")
    private String taxId;

    @NotBlank(message = "Description obligatoire")
    private String description;

    private String mission;
    private String website;
    private String phone;

    private MultipartFile logoFile; 
}